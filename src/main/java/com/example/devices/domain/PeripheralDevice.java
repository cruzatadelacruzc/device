package com.example.devices.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
public class PeripheralDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "uid", updatable = false, nullable = false)
    private UUID uid;

    private String vendor;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private Instant createdDate = Instant.now();

    private String status;

    @ManyToOne
    @JsonIgnoreProperties("peripheralDevices") // see this link:https://stackoverflow.com/a/18288939/13586810
    private Gateway gateway;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PeripheralDevice)) {
            return false;
        }
        return uid != null && uid.equals(((PeripheralDevice) o).uid);
    }

    @Override
    public int hashCode() {
        return 32;
    }

    @Override
    public String toString() {
        return "PeripheralDevice{" +
                "uid=" + uid +
                ", vendor='" + vendor + '\'' +
                ", createdDate=" + createdDate +
                ", status='" + status + '\'' +
                ", gateway=" + gateway +
                '}';
    }
}
