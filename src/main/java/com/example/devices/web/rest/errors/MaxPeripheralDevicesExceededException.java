package com.example.devices.web.rest.errors;

public class MaxPeripheralDevicesExceededException extends RuntimeException {

    public MaxPeripheralDevicesExceededException() {
        super("Amount of Peripheral Devices exceeded");
    }
}
