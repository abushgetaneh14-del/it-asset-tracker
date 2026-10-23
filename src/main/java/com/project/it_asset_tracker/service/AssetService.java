package com.project.it_asset_tracker.service;

import com.project.it_asset_tracker.dto.AssetDTO;
import com.project.it_asset_tracker.dto.AssetRequestDTO;
import com.project.it_asset_tracker.entity.Asset;
import com.project.it_asset_tracker.entity.AssetStatus;
import com.project.it_asset_tracker.entity.Category;
import com.project.it_asset_tracker.entity.User;
import com.project.it_asset_tracker.exception.BadRequestException;
import com.project.it_asset_tracker.exception.ResourceNotFoundException;
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
    public List<AssetDTO> getAllAssets(AssetStatus status) {
        if (status == null) {
            return getAllAssets();
        }
        return getAssetsByStatus(status);
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
                    "Serial number already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (request.getAssignedToUserId() != null) {
            throw new BadRequestException(
                    "Create the asset as available, then assign it using the assign endpoint");
        }

        if (request.getStatus() != null
                && request.getStatus() != AssetStatus.AVAILABLE) {
            throw new BadRequestException(
                    "New assets must have AVAILABLE status");
        }

        Asset asset = new Asset();
        asset.setName(request.getName().trim());
        asset.setSerialNumber(serialNumber);
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);

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
                    "Serial number already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));

        if (request.getAssignedToUserId() != null
                && !request.getAssignedToUserId().equals(
                        asset.getAssignedTo() == null
                                ? null : asset.getAssignedTo().getId())) {
            throw new BadRequestException(
                    "Use the assign endpoint to change asset assignment");
        }

        if (request.getStatus() != null
                && request.getStatus() != asset.getStatus()) {
            throw new BadRequestException(
                    "Use the assign or return endpoint to change asset status");
        }

        asset.setName(request.getName().trim());
        asset.setSerialNumber(serialNumber);
        asset.setCategory(category);

        return toDTO(assetRepository.save(asset));
    }

    @Transactional
    public AssetDTO assignAsset(Long assetId, Long userId) {
        Asset asset = findAsset(assetId);

        if (asset.getStatus() != AssetStatus.AVAILABLE
                || asset.getAssignedTo() != null) {
            throw new BadRequestException(
                    "Only available, unassigned assets can be assigned");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        asset.setAssignedTo(user);
        asset.setStatus(AssetStatus.ASSIGNED);

        return toDTO(assetRepository.save(asset));
    }

    @Transactional
    public AssetDTO returnAsset(Long assetId) {
        Asset asset = findAsset(assetId);

        if (asset.getStatus() != AssetStatus.ASSIGNED
                || asset.getAssignedTo() == null) {
            throw new BadRequestException(
                    "Only assigned assets can be returned");
        }

        asset.setAssignedTo(null);
        asset.setStatus(AssetStatus.AVAILABLE);

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
                        new ResourceNotFoundException("Asset not found"));
    }

    private AssetDTO toDTO(Asset asset) {
        Long categoryId = asset.getCategory() != null
                ? asset.getCategory().getId() : null;

        String categoryName = asset.getCategory() != null
                ? asset.getCategory().getName() : null;

        Long assignedToUserId = asset.getAssignedTo() != null
                ? asset.getAssignedTo().getId() : null;

        String assignedToUsername = asset.getAssignedTo() != null
                ? asset.getAssignedTo().getUsername() : null;

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