package com.project.it_asset_tracker;

import com.project.it_asset_tracker.dto.AssetDTO;
import com.project.it_asset_tracker.entity.Asset;
import com.project.it_asset_tracker.entity.AssetStatus;
import com.project.it_asset_tracker.entity.Category;
import com.project.it_asset_tracker.entity.Role;
import com.project.it_asset_tracker.entity.User;
import com.project.it_asset_tracker.exception.BadRequestException;
import com.project.it_asset_tracker.exception.ResourceNotFoundException;
import com.project.it_asset_tracker.repository.AssetRepository;
import com.project.it_asset_tracker.repository.CategoryRepository;
import com.project.it_asset_tracker.repository.UserRepository;
import com.project.it_asset_tracker.service.AssetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTests {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset asset;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        category = new Category("Laptop");
        user = new User("abush", "encoded-password", Role.ROLE_STAFF);

        asset = new Asset();
        asset.setName("Dell Latitude 5440");
        asset.setSerialNumber("DELL-IT-0001");
        asset.setCategory(category);
        asset.setStatus(AssetStatus.AVAILABLE);
    }

    @Test
    void assignAvailableAssetShouldAssignUser() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(assetRepository.save(asset)).thenReturn(asset);

        AssetDTO result = assetService.assignAsset(1L, 3L);

        assertEquals(AssetStatus.ASSIGNED, result.getStatus());
        assertEquals("abush", result.getAssignedToUsername());
        assertSame(user, asset.getAssignedTo());
        verify(assetRepository).save(asset);
    }

    @Test
    void assigningAlreadyAssignedAssetShouldThrowBadRequest() {
        asset.setAssignedTo(user);
        asset.setStatus(AssetStatus.ASSIGNED);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assertThrows(BadRequestException.class,
                () -> assetService.assignAsset(1L, 3L));

        verify(assetRepository, never()).save(any(Asset.class));
        verifyNoInteractions(userRepository);
    }

    @Test
    void assigningMissingAssetShouldThrowNotFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetService.assignAsset(99L, 3L));

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void assigningToMissingUserShouldThrowNotFound() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetService.assignAsset(1L, 99L));

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void returningAssignedAssetShouldClearUserAndSetAvailable() {
        asset.setAssignedTo(user);
        asset.setStatus(AssetStatus.ASSIGNED);
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetRepository.save(asset)).thenReturn(asset);

        AssetDTO result = assetService.returnAsset(1L);

        assertEquals(AssetStatus.AVAILABLE, result.getStatus());
        assertNull(result.getAssignedToUserId());
        assertNull(result.getAssignedToUsername());
        assertNull(asset.getAssignedTo());
        verify(assetRepository).save(asset);
    }

    @Test
    void returningAvailableAssetShouldThrowBadRequest() {
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));

        assertThrows(BadRequestException.class,
                () -> assetService.returnAsset(1L));

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void gettingMissingAssetShouldThrowNotFound() {
        when(assetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> assetService.getAssetById(99L));
    }
}