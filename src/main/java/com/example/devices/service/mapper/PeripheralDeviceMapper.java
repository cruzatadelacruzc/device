package com.example.devices.service.mapper;

import com.example.devices.domain.PeripheralDevice;
import com.example.devices.service.dto.PeripheralDeviceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {GatewayMapper.class})
public interface PeripheralDeviceMapper extends EntityMapper<PeripheralDeviceDto, PeripheralDevice> {

    @Mapping(source = "gateway.serial", target = "gatewaySerial")
    PeripheralDeviceDto toDto(PeripheralDevice peripheralDevice);

    @Mapping(source = "gatewaySerial", target = "gateway.serial")
    PeripheralDevice toEntity(PeripheralDeviceDto peripheralDeviceDto);

    default PeripheralDevice fromId(UUID uid) {
        if (uid == null) {
            return null;
        }
        PeripheralDevice peripheralDevice = new PeripheralDevice();
        peripheralDevice.setUid(uid);
        return peripheralDevice;
    }
}
