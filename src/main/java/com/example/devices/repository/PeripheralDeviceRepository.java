package com.example.devices.repository;

import com.example.devices.domain.PeripheralDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PeripheralDeviceRepository extends JpaRepository<PeripheralDevice, UUID> {

}
