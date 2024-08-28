package com.microservices.inventory.service;

import com.microservices.inventory.InventoryApp;
import com.microservices.inventory.dto.InventoryRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@DisplayName("Integration Test")
@SpringBootTest(classes = InventoryApp.class,
                properties = {"spring.datasource.url=jdbc:h2:mem:testdb"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setup() {
        InventoryRequestDto inventory = new InventoryRequestDto("product1", 11);
        restTemplate.postForEntity("/api/v1/inventory", inventory, String.class);
    }

    @Test
    void shouldReadInventory() {

        ResponseEntity<String> getResponse1 = restTemplate
            .getForEntity("/api/v1/inventory?sku=product1&stock=1", String.class);
        //assertThat(getBodyDetail).isEqualTo(expected);
        assertThat(getResponse1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse1.getBody()).isEqualTo("true");

        log.info("GET RESPONSE -> " + getResponse1.getBody());

        ResponseEntity<String> getResponse2 = restTemplate
            .getForEntity("/api/v1/inventory?sku=product1&stock=9999", String.class);
        //assertThat(getBodyDetail).isEqualTo(expected);
        assertThat(getResponse2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse2.getBody()).isEqualTo("false");

    }

    /*
    @Test
    void testCreateInventory() {

    }

    @Test
    void testDeleteInventory() {

    }

    @Test
    void testIsInStock() {

    }

    @Test
    void testReadAll() {

    }

    @Test
    void testReadInventory() {

    }

    @Test
    void testUpdateInventory() {

    }
    */
}
