package com.example.devices.service;

import com.example.devices.domain.Gateway;
import com.example.devices.domain.PeripheralDevice;
import com.example.devices.repository.GatewayRepository;
import com.example.devices.service.dto.GatewayDto;
import com.example.devices.service.mapper.GatewayMapper;
import com.example.devices.service.mapper.PeripheralDeviceMapper;
import com.example.devices.web.rest.errors.MaxPeripheralDevicesExceededException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;


@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class GatewayService {

    private final GatewayMapper mapper;

    private final PeripheralDeviceMapper peripheralDeviceMapper;

    private final GatewayRepository repository;


    public GatewayDto createGateway(GatewayDto gatewayDto) {
        log.debug("Request to create a gateway: {}", gatewayDto);
        if (gatewayDto.getPeripheralDevices() != null && gatewayDto.getPeripheralDevices().size() > 10) {
            throw new MaxPeripheralDevicesExceededException();
        }
        Gateway gateway = mapper.toEntity(gatewayDto);
        if (gatewayDto.getPeripheralDevices() != null) {
            Set<PeripheralDevice> peripheralDevices = peripheralDeviceMapper.toEntities(gatewayDto.getPeripheralDevices());
            gateway.setPeripheralDevices(peripheralDevices);
        }
        repository.save(gateway);
        log.info("Created information for gateway: {}", gateway);
        return mapper.toDto(gateway);
    }

    @Transactional(readOnly = true)
    public Page<Gateway> getAllGatewayWithPeripheralDevices(Pageable pageable) {
        log.debug("Request to get all gateways with peripheral devices");
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Gateway> getGatewayWithPeripheralDevices(String serial) {
        log.debug("Request to get a gateway by serial: {}", serial);
        return repository.findById(serial);
    }
}
