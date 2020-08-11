package com.example.devices.service.dto;

import com.example.devices.config.Constants;
import com.example.devices.domain.Gateway;
import com.example.devices.service.UniqueKey;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * Data transform Object for {@link com.example.devices.domain.Gateway}
 */
@Data
public class GatewayDto implements Serializable {

    @NotBlank
    @UniqueKey(entityClass = Gateway.class, columnName = "serial")
    private String serial;

    @Pattern(regexp = Constants.HUMAN_READABLE_REGEX)
    private String name;

    @Pattern(regexp = Constants.IPv4_ADDRESS_REGEX)
    private String ipAddress;

    @Size(max = 10)
    private Set<PeripheralDeviceDto> peripheralDevices;
}
