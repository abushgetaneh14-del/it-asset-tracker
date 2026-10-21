package com.project.it_asset_tracker.service;

import com.project.it_asset_tracker.dto.AssetDTO;
import com.project.it_asset_tracker.dto.AssetRequestDTO;
import com.project.it_asset_tracker.entity.Asset;
import com.project.it_asset_tracker.entity.AssetStatus;
import com.project.it_asset_tracker.entity.Category;
import com.project.it_asset_tracker.entity.User;
import com.project.it_asset_tracker.repository.AssetRepository;
import com.project.it_asset_tracker.repository.CategoryRepository;
import com.project.it_asset_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AssetService(
            AssetRepository assetRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        this.assetRepository = assetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AssetDTO> getAllAssets() {
        return assetRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssetDTO getAssetById(Long id) {
        return toDTO(findAsset(id));
    }

    @Transactional
    public AssetDTO createAsset(AssetRequestDTO request) {
        String serialNumber = request.getSerialNumber().trim();

        if (assetRepository.existsBySerialNumber(serialNumber)) {
            throw new IllegalArgumentException(
                    "Serial number already exists"
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found"));

        Asset asset = new Asset();
        asset.setName(request.getName().trim());
        asset.setSerialNumber(serialNumber);
        asset.setCategory(category);

        if (request.getAssignedToUserId() != null) {
            User user = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Assigned user not found"));

            asset.setAssignedTo(user);
        }

        AssetStatus status = request.getStatus();

        if (status == null) {
            status = request.getAssignedToUserId() == null
                    ? AssetStatus.AVAILABLE
                    : AssetStatus.ASSIGNED;
        }

        validateStatusAndAssignment(status, request.getAssignedToUserId());

        asset.setStatus(status);

        return toDTO(assetRepository.save(asset));
    }

    @Transactional
    public AssetDTO updateAsset(Long id, AssetRequestDTO request) {
        Asset asset = findAsset(id);
        String serialNumber = request.getSerialNumber().trim();

        boolean serialUsedByAnotherAsset =
                assetRepository.existsBySerialNumber(serialNumber)
                        && !asset.getSerialNumber().equalsIgnoreCase(serialNumber);

        if (serialUsedByAnotherAsset) {
            throw new IllegalArgumentException(
                    "Serial number already exists"
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found"));

        asset.setName(request.getName().trim());
        asset.setSerialNumber(serialNumber);
        asset.setCategory(category);

        User assignedUser = null;

        if (request.getAssignedToUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedToUserId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Assigned user not found"));
        }

        asset.setAssignedTo(assignedUser);

        AssetStatus status = request.getStatus();

        if (status == null) {
            status = assignedUser == null
                    ? AssetStatus.AVAILABLE
                    : AssetStatus.ASSIGNED;
        }

        validateStatusAndAssignment(
                status,
                request.getAssignedToUserId()
        );

        asset.setStatus(status);

        return toDTO(assetRepository.save(asset));
    }

    @Transactional
    public void deleteAsset(Long id) {
        Asset asset = findAsset(id);
        assetRepository.delete(asset);
    }

    @Transactional(readOnly = true)
    public List<AssetDTO> getAssetsByStatus(AssetStatus status) {
        return assetRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AssetDTO> getAssetsByAssignedUsername(String username) {
        return assetRepository.findByAssignedTo_Username(username)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private Asset findAsset(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Asset not found"));
    }

    private void validateStatusAndAssignment(
            AssetStatus status,
            Long assignedToUserId) {

        if (status == AssetStatus.ASSIGNED && assignedToUserId == null) {
            throw new IllegalArgumentException(
                    "An assigned asset must have an assigned user"
            );
        }

        if (status == AssetStatus.AVAILABLE && assignedToUserId != null) {
            throw new IllegalArgumentException(
                    "An available asset cannot have an assigned user"
            );
        }
    }

    private AssetDTO toDTO(Asset asset) {
        Long categoryId = asset.getCategory() != null
                ? asset.getCategory().getId()
                : null;

        String categoryName = asset.getCategory() != null
                ? asset.getCategory().getName()
                : null;

        Long assignedToUserId = asset.getAssignedTo() != null
                ? asset.getAssignedTo().getId()
                : null;

        String assignedToUsername = asset.getAssignedTo() != null
                ? asset.getAssignedTo().getUsername()
                : null;

        return new AssetDTO(
                asset.getId(),
                asset.getName(),
                asset.getSerialNumber(),
                asset.getStatus(),
                categoryId,
                categoryName,
                assignedToUserId,
                assignedToUsername,
                asset.getCreatedAt()
        );
    }
}