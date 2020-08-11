package com.example.devices.repository;

import com.example.devices.domain.Gateway;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, String> {

    @EntityGraph(attributePaths = "peripheralDevices")
    Page<Gateway> findAll(Pageable pageable);
}
