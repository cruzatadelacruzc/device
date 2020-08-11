package com.example.devices.web.rest;

import com.example.devices.domain.Gateway;
import com.example.devices.service.GatewayService;
import com.example.devices.service.dto.GatewayDto;
import com.example.devices.web.rest.util.HeaderResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class GatewayResource {

    private final GatewayService service;

    private static final String ENTITY_NAME = "Gateway";

    /**
     * {@code POST /gateways } : create a Gateway
     *
     * @param gatewayDto contain information about {@link com.example.devices.domain.Gateway}
     * @return a {@link ResponseEntity} with status {@code 201 (Created)} and with Gateway body
     * @throws URISyntaxException if Location URI syntax is incorrect
     */
    @PostMapping("/gateways")
    public ResponseEntity<GatewayDto> createGateway(@Valid @RequestBody GatewayDto gatewayDto) throws URISyntaxException {
        log.debug("REST request to create a gateway: {}", gatewayDto);
        GatewayDto gatewayDtoStored = service.createGateway(gatewayDto);
        return ResponseEntity.created(new URI("/api/gateways/" + gatewayDtoStored.getSerial()))
                .headers(new HeaderResponse().createAddAlert(ENTITY_NAME, gatewayDtoStored.getSerial()))
                .body(gatewayDtoStored);
    }

    /**
     * {@code GET /gateways }: get all the gateways
     *
     * @param pageable the pagination information.
     * @return a {@link ResponseEntity} with status {@code 200  (ok)} and the list of gateway in body
     */
    @GetMapping("/gateways")
    public ResponseEntity<List<Gateway>> getAllGatewayWithPeripheralDevices(Pageable pageable) {
        log.debug("REST request to get all gateways");
        Page<Gateway> gatewaysStored = service.getAllGatewayWithPeripheralDevices(pageable);
        HttpHeaders pagination = HeaderResponse.generatePagination(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                gatewaysStored);
        return ResponseEntity.ok().headers(pagination).body(gatewaysStored.getContent());
    }

    /**
     * {@code GET /gateways/:serial }: get a gateway with all peripheral devices
     *
     * @param serial identifier of the gateway
     * @return a {@link ResponseEntity} with status {@code 200  (ok)} and a gateway in body
     */
    @GetMapping("/gateways/{serial}")
    public ResponseEntity<Gateway> getGatewayBySerial(@PathVariable String serial) {
        log.debug("REST request to get a gateway by serial: {}", serial);
        return service.getGatewayWithPeripheralDevices(serial)
                .map(gateway -> ResponseEntity.ok().body(gateway))
                .orElse(new ResponseEntity(HttpStatus.NOT_FOUND));
    }
}
