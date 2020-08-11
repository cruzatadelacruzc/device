package com.example.devices.web.rest;

import com.example.devices.DeviceStoreApplication;
import com.example.devices.domain.Gateway;
import com.example.devices.domain.PeripheralDevice;
import com.example.devices.repository.GatewayRepository;
import com.example.devices.repository.PeripheralDeviceRepository;
import com.example.devices.service.PeripheralDeviceService;
import com.example.devices.service.dto.PeripheralDeviceDto;
import com.example.devices.service.mapper.PeripheralDeviceMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for {@link PeripheralDeviceResource}
 */
@SpringBootTest(classes = DeviceStoreApplication.class)
public class PeripheralDevicesResourceIT {

    private static final String DEFAULT_VENDOR = "Illumination";
    private static final String DEFAULT_STATUS = "online";


    @Autowired
    private PageableHandlerMethodArgumentResolver handlerMethodArgumentResolver;

    @Autowired
    private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private PeripheralDeviceRepository repository;

    @Autowired
    private GatewayRepository gatewayRepository;

    @Autowired
    private PeripheralDeviceService service;

    @Autowired
    private PeripheralDeviceMapper mapper;

    @Autowired
    private Validator validator;

    private PeripheralDevice peripheralDevice;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        final PeripheralDeviceResource resource = new PeripheralDeviceResource(service);
        mockMvc = MockMvcBuilders.standaloneSetup(resource)
                .setCustomArgumentResolvers(handlerMethodArgumentResolver)
                .setMessageConverters(jackson2HttpMessageConverter)
                .setControllerAdvice(exceptionTranslator)
                .setValidator(validator)
                .build();
    }

    @BeforeEach
    void initTest() {
        peripheralDevice = new PeripheralDevice();
        peripheralDevice.setStatus(DEFAULT_STATUS);
        peripheralDevice.setVendor(DEFAULT_VENDOR);
        Gateway gateway = new Gateway();
        gateway.setSerial("asd123dfg456");
        gatewayRepository.saveAndFlush(gateway);
        peripheralDevice.setGateway(gateway);
    }

    @Test
    @Transactional
    void testCreatePeripheralDevice() throws Exception {
        int initDatabaseSize = repository.findAll().size();
        PeripheralDeviceDto deviceDto = mapper.toDto(peripheralDevice);
        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(deviceDto)))
                .andExpect(status().isCreated());
        List<PeripheralDevice> peripheralDevices = repository.findAll();
        assertThat(peripheralDevices).hasSize(initDatabaseSize + 1);
        PeripheralDevice testPeripheralDevice = peripheralDevices.get(peripheralDevices.size() - 1);
        assertThat(testPeripheralDevice.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testPeripheralDevice.getVendor()).isEqualTo(DEFAULT_VENDOR);
        assertThat(testPeripheralDevice.getGateway().getSerial()).isEqualTo(peripheralDevice.getGateway().getSerial());
    }

    @Test
    void testCreatePeripheralDeviceWithStatusFormatWrong() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        // Create the PeripheralDevice with an Status format wrong
        peripheralDevice.setStatus("ONLIne");
        PeripheralDeviceDto deviceDto = mapper.toDto(peripheralDevice);

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(deviceDto)))
                .andExpect(status().isBadRequest());

        List<PeripheralDevice> peripheralDevices = repository.findAll();
        assertThat(peripheralDevices).hasSize(initDatabaseSize);
    }

    @Test
    void assertThatVendorCanNotBlank() throws Exception {
        int initDatabaseSize = repository.findAll().size();

        // Create the PeripheralDevice with an Status format wrong
        peripheralDevice.setVendor("");
        PeripheralDeviceDto deviceDto = mapper.toDto(peripheralDevice);

        mockMvc.perform(post("/api/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(deviceDto)))
                .andExpect(status().isBadRequest());

        List<PeripheralDevice> peripheralDevices = repository.findAll();
        assertThat(peripheralDevices).hasSize(initDatabaseSize);
    }

    @Test
    void testDeletePeripheralDevice() throws Exception {
        repository.saveAndFlush(peripheralDevice);
        int initDatabaseSize = repository.findAll().size();


        mockMvc.perform(delete("/api/devices/" + peripheralDevice.getUid())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        List<PeripheralDevice> peripheralDevices = repository.findAll();
        assertThat(peripheralDevices).hasSize(initDatabaseSize - 1);
    }
}
