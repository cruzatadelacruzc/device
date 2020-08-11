package com.example.devices.service.mapper;

import com.example.devices.domain.Gateway;
import com.example.devices.service.dto.GatewayDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GatewayMapper extends EntityMapper<GatewayDto, Gateway> {

    @Mapping(source = "peripheralDevices" , target = "peripheralDevices")
    GatewayDto toDto(Gateway gateway);

    default Gateway fromId(String serial) {
        if (serial == null) {
            return null;
        }
        Gateway gateway = new Gateway();
        gateway.setSerial(serial);
        return gateway;
    }
}
