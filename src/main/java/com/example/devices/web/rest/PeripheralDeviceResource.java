package com.example.devices.web.rest;

import com.example.devices.service.PeripheralDeviceService;
import com.example.devices.service.dto.PeripheralDeviceDto;
import com.example.devices.web.rest.util.HeaderResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PeripheralDeviceResource {

    private static final String ENTITY_NAME = "PeripheralDevice";

    private final PeripheralDeviceService service;

    /**
     * {@code POST /devices }: create a PeripheralDevice
     * @param dto contain information about {@link com.example.devices.domain.PeripheralDevice}
     * @return a {@link ResponseEntity} with status {@code 201 (Created)} and with PeripheralDevice body
     * @throws URISyntaxException if Location URI syntax is incorrect
     */
    @PostMapping("/devices")
    public ResponseEntity<PeripheralDeviceDto> addPeripheralDevicesToGateway(@Valid @RequestBody PeripheralDeviceDto dto) throws URISyntaxException {
        log.debug("REST request to create a PeripheralDevice: {}", dto);
        PeripheralDeviceDto peripheralDeviceStored = service.createPeripheralDevice(dto);
        return ResponseEntity.created(new URI("/api/devices/" + peripheralDeviceStored.getUid()))
                .headers(new HeaderResponse().createAddAlert(ENTITY_NAME, peripheralDeviceStored.getUid().toString()))
                .body(peripheralDeviceStored);
    }

    @DeleteMapping("/devices/{id}")
    public ResponseEntity<Void> deletePeripheralDevice(@PathVariable("id") UUID uuid) {
        log.debug("REST to request to delete a PeripheralDevice By ID: {}", uuid);
        service.deletePeripheralDevice(uuid);
        return ResponseEntity.noContent()
                .headers(new HeaderResponse().createDeleteAlert(ENTITY_NAME, uuid.toString()))
                .build();
    }
}
