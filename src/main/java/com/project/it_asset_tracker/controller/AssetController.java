package com.project.it_asset_tracker.controller;

import com.project.it_asset_tracker.dto.AssetDTO;
import com.project.it_asset_tracker.dto.AssetRequestDTO;
import com.project.it_asset_tracker.entity.AssetStatus;
import com.project.it_asset_tracker.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@Validated
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AssetDTO> getAllAssets(
            @RequestParam(required = false) AssetStatus status) {
        return assetService.getAllAssets(status);
    }

    @GetMapping("/my-assets")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AssetDTO> getMyAssets(Authentication authentication) {
        return assetService.getAssetsByAssignedUsername(
                authentication.getName());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public AssetDTO getAssetById(@PathVariable Long id) {
        return assetService.getAssetById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AssetDTO createAsset(
            @Valid @RequestBody AssetRequestDTO request) {
        return assetService.createAsset(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetDTO updateAsset(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequestDTO request) {
        return assetService.updateAsset(id, request);
    }

    @PatchMapping("/{id}/assign/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetDTO assignAsset(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return assetService.assignAsset(id, userId);
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasRole('ADMIN')")
    public AssetDTO returnAsset(@PathVariable Long id) {
        return assetService.returnAsset(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AssetDTO> getAssetsByStatus(
            @PathVariable AssetStatus status) {
        return assetService.getAssetsByStatus(status);
    }

    @GetMapping("/assigned/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<AssetDTO> getAssetsByAssignedUsername(
            @PathVariable String username) {
        return assetService.getAssetsByAssignedUsername(username);
    }
}