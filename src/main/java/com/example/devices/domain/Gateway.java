package com.example.devices.domain;


import com.example.devices.config.Constants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Gateway implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String serial;

    @Pattern(regexp = Constants.HUMAN_READABLE_REGEX)
    private String name;

    @Pattern(regexp = Constants.IPv4_ADDRESS_REGEX)
    private String ipAddress;

    @Size(max = 10)
    @OneToMany(mappedBy = "gateway", fetch = FetchType.EAGER)
    private Set<PeripheralDevice> peripheralDevices = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Gateway)) {
            return false;
        }
        return serial != null && serial.equals(((Gateway) o).serial);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Gateway{" +
                "serial='" + serial + '\'' +
                ", name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", peripheralDevices=" + peripheralDevices +
                '}';
    }
}
