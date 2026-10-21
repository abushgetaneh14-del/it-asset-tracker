package com.project.it_asset_tracker.repository;

import com.project.it_asset_tracker.entity.Asset;
import com.project.it_asset_tracker.entity.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    boolean existsBySerialNumber(String serialNumber);

    List<Asset> findByStatus(AssetStatus status);

    List<Asset> findByAssignedTo_Username(String username);
}