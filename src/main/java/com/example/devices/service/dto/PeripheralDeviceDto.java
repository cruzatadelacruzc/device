package com.example.devices.service.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.UUID;

/**
 * Data transform Object for {@link com.example.devices.domain.PeripheralDevice}
 */
@Data
public class PeripheralDeviceDto implements Serializable {

    private UUID uid;

    @NotBlank
    private String vendor;

    private String createdDate;

    @Pattern(regexp = "online|offline", message = "must.match")
    private String status;

    @NotNull
    private String gatewaySerial;
}
