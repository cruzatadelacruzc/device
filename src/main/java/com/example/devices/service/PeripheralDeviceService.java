package com.example.devices.service;

import com.example.devices.domain.PeripheralDevice;
import com.example.devices.repository.PeripheralDeviceRepository;
import com.example.devices.service.dto.PeripheralDeviceDto;
import com.example.devices.service.mapper.PeripheralDeviceMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class PeripheralDeviceService {


    private final PeripheralDeviceRepository repository;

    private final PeripheralDeviceMapper mapper;

    public PeripheralDeviceDto createPeripheralDevice(PeripheralDeviceDto dto) {
        log.debug("Request to create a PeripheralDevice: {}", dto);
        PeripheralDevice peripheralDevice = mapper.toEntity(dto);
        repository.save(peripheralDevice);
        log.info("Created information PeripheralDevice: {}", peripheralDevice);
        return mapper.toDto(peripheralDevice);
    }

    public void deletePeripheralDevice(UUID uuid) {
        log.debug("Request to delete a PeripheralDevice with ID: {}", uuid);
        repository.deleteById(uuid);
    }
}
