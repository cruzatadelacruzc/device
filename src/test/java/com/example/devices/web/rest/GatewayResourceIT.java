package com.example.devices.web.rest;

import com.example.devices.DeviceStoreApplication;
import com.example.devices.domain.Gateway;
import com.example.devices.repository.GatewayRepository;
import com.example.devices.service.GatewayService;
import com.example.devices.service.dto.GatewayDto;
import com.example.devices.service.mapper.GatewayMapper;
import com.example.devices.web.rest.errors.ExceptionTranslator;
import com.example.devices.web.rest.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for {@link GatewayResource}
 */
@SpringBootTest(classes = DeviceStoreApplication.class)
public class GatewayResourceIT {

    private static final String DEFAULT_SERIAL = "12WE31SD43RT";
    private static final String BAD_SERIAL = null;

    private static final String DEFAULT_NAME = "Cisco";
    private static final String BAD_NAME = "#$@:;";

    private static final String DEFAULT_IPV4_ADDRESS = "172.168.1.1";
    private static final String OUT_RANGE_IPV4_ADDRESS = "172.8.7.280";
    private static final String FOUR_DIGIT_IPV4_ADDRESS = "1724.8.7.28";

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private GatewayRepository repository;

    @Autowired
    private GatewayMapper gatewayMapper;

    @Autowired
    private GatewayService service;

    @Autowired
    private Validator validator;

    private Gateway gateway;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        final GatewayResource gatewayResource = new GatewayResource(service);
        this.mvc = MockMvcBuilders.standaloneSetup(gatewayResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter)
                .setControllerAdvice(exceptionTranslator)
                .setValidator(validator)
                .build();
    }

    @BeforeEach
    void initTest() {
        gateway = new Gateway();
        gateway.setSerial(DEFAULT_SERIAL);
        gateway.setIpAddress(DEFAULT_IPV4_ADDRESS);
        gateway.setName(DEFAULT_NAME);
    }

    @Test
    void testCreateGateway() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        GatewayDto gatewayDto = gatewayMapper.toDto(gateway);

        mvc.perform(post("/api/gateways")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(gatewayDto)))
                .andExpect(status().isCreated());

        List<Gateway> gateways = repository.findAll();
        assertThat(gateways).hasSize(initDatabaseSize + 1);
        Gateway testGateway = gateways.get(gateways.size() - 1);
        assertThat(testGateway.getSerial()).isEqualTo(DEFAULT_SERIAL);
        assertThat(testGateway.getIpAddress()).isEqualTo(DEFAULT_IPV4_ADDRESS);
        assertThat(testGateway.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void testCreateGatewayWithNullSerial() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        GatewayDto gatewayDto = gatewayMapper.toDto(gateway);
        gatewayDto.setSerial(BAD_SERIAL);

        mvc.perform(post("/api/gateways")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(gatewayDto)))
                .andExpect(status().isBadRequest());

        List<Gateway> gateways = repository.findAll();
        assertThat(gateways).hasSize(initDatabaseSize);
    }

    @Test
    void testCreateGatewayWithHumanUnableRead() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        GatewayDto gatewayDto = gatewayMapper.toDto(gateway);
        gatewayDto.setName(BAD_NAME);

        mvc.perform(post("/api/gateways")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(gatewayDto)))
                .andExpect(status().isBadRequest());

        List<Gateway> gateways = repository.findAll();
        assertThat(gateways).hasSize(initDatabaseSize);
    }

    @Test
    void testCreateGatewayNotValidIPv4AddressWithOutRange() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        GatewayDto gatewayDto = gatewayMapper.toDto(gateway);
        gatewayDto.setIpAddress(OUT_RANGE_IPV4_ADDRESS);

        mvc.perform(post("/api/gateways")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(gatewayDto)))
                .andExpect(status().isBadRequest());

        List<Gateway> gateways = repository.findAll();
        assertThat(gateways).hasSize(initDatabaseSize);
    }

    @Test
    void testCreateGatewayNotValidIPv4AddressWithFourRange() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        GatewayDto gatewayDto = gatewayMapper.toDto(gateway);
        gatewayDto.setIpAddress(FOUR_DIGIT_IPV4_ADDRESS);

        mvc.perform(post("/api/gateways")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(gatewayDto)))
                .andExpect(status().isBadRequest());

        List<Gateway> gateways = repository.findAll();
        assertThat(gateways).hasSize(initDatabaseSize);
    }

    @Test
    void testGetAllGateways() throws Exception {
        repository.saveAndFlush(gateway);

        mvc.perform(get("/api/gateways?sort=serial,desc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
                .andExpect(jsonPath("$.[*].ipAddress").value(hasItem(DEFAULT_IPV4_ADDRESS)))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    void testGetGatewayBySerial() throws Exception {
        repository.saveAndFlush(gateway);

        mvc.perform(get("/api/gateways/" + gateway.getSerial())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL))
                .andExpect(jsonPath("$.ipAddress").value(DEFAULT_IPV4_ADDRESS))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    void testGetNoExistingGateway() throws Exception {
        repository.saveAndFlush(gateway);

        mvc.perform(get("/api/gateways/unknown")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
