package com.microservices.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;

import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.beans.factory.annotation.Autowired;

import com.microservices.order.dto.OrderRequestDto;
import com.microservices.order.dto.UserDetailsDto;
import com.microservices.order.stubs.InventoryClientStub;
import com.jayway.jsonpath.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@DisplayName("Integration Test")
@SpringBootTest(classes = OrderApp.class,
                properties = {"spring.datasource.url=jdbc:h2:mem:testdb"},
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWireMock(port = 0)
public class OrderAppTest {

        @Autowired
        private TestRestTemplate restTemplate;

        static UserDetailsDto userDetails = new UserDetailsDto("user@email", "Firstname", "Lastname");

        @Test
        @Order(1)
        void shouldReturnNoStaticResourceForWrongURI() {
                //InventoryClientStub.stubInventoryCall();
                String expected = "No static resource api/v1/order.";
                OrderRequestDto request = new OrderRequestDto("P1", "sku1", 12, userDetails);

                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/order", String.class);
                log.info("GET RESPONSE -> " + getResponse.getBody());
                
                String getBodyDetail = JsonPath.parse(getResponse.getBody()).read("$.detail");
                assertThat(getBodyDetail).isEqualTo(expected);
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

                ResponseEntity<String> postResponse = restTemplate
                        .postForEntity("/api/v1/order", request, String.class);
                log.info("POST RESPONSE -> " + postResponse.getBody());
                
                String postBodyDetail = JsonPath.parse(postResponse.getBody()).read("$.detail");
                assertThat(postBodyDetail).isEqualTo(expected);
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

                HttpEntity<OrderRequestDto> httpEntity = new HttpEntity<>(request);
                ResponseEntity<String> putResponse = restTemplate
                        .exchange("/api/v1/order", HttpMethod.PUT, httpEntity, String.class);
                log.info("PUT RESPONSE -> " + putResponse.getBody());
                
                String putBodyDetail = JsonPath.parse(putResponse.getBody()).read("$.detail");
                assertThat(putBodyDetail).isEqualTo(expected);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(2)
        void shouldNotGetAPageOfOrdersIfDbEmpty() {
                String expected = "No results for: Orders";
                ResponseEntity<String> response = restTemplate
                        .getForEntity("/api/v1/orders?page=0&size=1", String.class);
                log.info("GET RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(11)
        void shouldNotPostOrderWithIncompleteRequest() {
                String expected = "Sku must not be empty.";
                OrderRequestDto postRequest = new OrderRequestDto("P1", "", 12, userDetails);
                InventoryClientStub.stubInventoryCall("", 12);
                ResponseEntity<String> postResponse = restTemplate
                        .postForEntity("/api/v1/orders", postRequest, String.class);
                log.info("POST RESPONSE -> " + postResponse.getBody());
                String bodyDetail = JsonPath.parse(postResponse.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(12)
        void shouldNotPostOrderAtWrongUrl() {
                String expected = "Method 'POST' is not supported.";
                OrderRequestDto request = new OrderRequestDto("P1", "sku1", 12, userDetails);
                ResponseEntity<String> response = restTemplate
                        .postForEntity("/api/v1/orders/550e8400-e29b-41d4-a716-446655440000", request, String.class);
                log.info("POST RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Test
        @Order(13)
        void shouldPostOrderCorrectly() {
                OrderRequestDto postRequest = new OrderRequestDto("P1", "sku1", 12, userDetails);
                InventoryClientStub.stubInventoryCall("sku1", 12);
                ResponseEntity<String> postResponse = restTemplate
                        .postForEntity("/api/v1/orders", postRequest, String.class);
                log.info("POST RESPONSE -> " + postResponse);
                assertThat(postResponse.getBody()).isEqualTo(null);
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                
                String id = postResponse.getHeaders().get("Location").toString().split("orders/")[1].replace("]", "");
                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/orders/" + id, String.class);
                log.info("GET RESPONSE -> " + getResponse.getBody());
                String sku = JsonPath.parse(getResponse.getBody()).read("$.sku");
                assertThat(sku).isEqualTo("sku1");
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @Order(21)
        void shouldNotPutOrderWithInvalidId() {
                String expected = "No results for: 123";
                OrderRequestDto request = new OrderRequestDto("P1", "sku1", 12, userDetails);
                HttpEntity<OrderRequestDto> httpEntity = new HttpEntity<>(request);
                ResponseEntity<String> response = restTemplate
                        .exchange("/api/v1/orders/123", HttpMethod.PUT, httpEntity, String.class);
                log.info("PUT RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(22)
        void shouldNotPutOrderWithAnUnknownId() {
                String expected = "No results for: 550e8400-e29b-41d4-a716-446655440000";
                OrderRequestDto request = new OrderRequestDto("P1", "sku1", 12, userDetails);
                HttpEntity<OrderRequestDto> httpEntity = new HttpEntity<>(request);
                ResponseEntity<String> response = restTemplate
                        .exchange("/api/v1/orders/550e8400-e29b-41d4-a716-446655440000", HttpMethod.PUT, httpEntity, String.class);
                log.info("PUT RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(23)
        void shouldPutUpdateExistingOrder() {
                OrderRequestDto postRequest = new OrderRequestDto("P1", "sku1", 12, userDetails);
                InventoryClientStub.stubInventoryCall("sku1", 12);
                ResponseEntity<String> postResponse = restTemplate
                        .postForEntity("/api/v1/orders", postRequest, String.class);

                String id = postResponse.getHeaders().get("Location").toString().split("orders/")[1].replace("]", "");

                OrderRequestDto putRequest = new OrderRequestDto("P1", "sku1-update", 111, userDetails);
                HttpEntity<OrderRequestDto> httpEntity = new HttpEntity<>(putRequest);
                ResponseEntity<String> putResponse = restTemplate
                        .exchange("/api/v1/orders/" + id, HttpMethod.PUT, httpEntity, String.class);
                log.info("PUT RESPONSE -> " + putResponse.getBody());

                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/orders/" + id, String.class);
                DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
                String sku = documentContext.read("$.sku");
                Integer quantity = documentContext.read("$.quantity");
                assertThat(sku).isEqualTo("sku1-update");
                assertThat(quantity).isEqualTo(111);
                assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @Order(31)
        void shouldNotGetOrderWithInvalidId() {
                String expected = "No results for: 123";
                OrderRequestDto request = new OrderRequestDto("P1", "sku1", 11, userDetails);
                HttpEntity<OrderRequestDto> httpEntity = new HttpEntity<>(request);
                ResponseEntity<String> response = restTemplate
                        .exchange("/api/v1/orders/123", HttpMethod.PUT, httpEntity, String.class);
                log.info("GET RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(32)
        void shouldNotGetOrderWithAnUnknownId() {
                String expected = "No results for: 550e8400-e29b-41d4-a716-446655440000";
                ResponseEntity<String> response = restTemplate
                        .getForEntity("/api/v1/orders/550e8400-e29b-41d4-a716-446655440000", String.class);
                log.info("GET RESPONSE -> " + response.getBody());
                
                String bodyDetail = JsonPath.parse(response.getBody()).read("$.detail");
                assertThat(bodyDetail).isEqualTo(expected);
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @Order(34)
        void shouldGetOrderCorrectly() {
                OrderRequestDto postRequest = new OrderRequestDto("P2", "sku2", 12, userDetails);
                InventoryClientStub.stubInventoryCall("sku2", 12);
                ResponseEntity<String> postResponse = restTemplate
                        .postForEntity("/api/v1/orders", postRequest, String.class);
                log.info("POST RESPONSE -> " + postResponse);
                assertThat(postResponse.getBody()).isEqualTo(null);
                assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                
                String id = postResponse.getHeaders().get("Location").toString().split("orders/")[1].replace("]", "");
                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/orders/" + id, String.class);
                log.info("GET RESPONSE -> " + getResponse.getBody());
                String sku = JsonPath.parse(getResponse.getBody()).read("$.product_id");
                assertThat(sku).isEqualTo("P2");
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @Order(35)
        void shouldReturnASortedPageOfOrdersWithNoParametersAndUseDefaultValues() {
                OrderRequestDto postRequest1 = new OrderRequestDto("P1", "sku1", 11, userDetails);
                OrderRequestDto postRequest2 = new OrderRequestDto("P2", "sku2", 12, userDetails);
                OrderRequestDto postRequest3 = new OrderRequestDto("P3", "sku3", 13, userDetails);   
                InventoryClientStub.stubInventoryCall("sku1", 11);
                InventoryClientStub.stubInventoryCall("sku2", 12);
                InventoryClientStub.stubInventoryCall("sku3", 13);

                ResponseEntity<String> postResponse1 = restTemplate
                .postForEntity("/api/v1/orders", postRequest1, String.class);
                ResponseEntity<String> postResponse2 = restTemplate
                .postForEntity("/api/v1/orders", postRequest2, String.class);
                ResponseEntity<String> postResponse3 = restTemplate
                .postForEntity("/api/v1/orders", postRequest3, String.class);

                assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(postResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(postResponse3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                
                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/orders?page=0&size=3", String.class);
                DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
                log.info("GET RESPONSE BODY: " + getResponse.getBody());
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                String product1Id = documentContext.read("content[0].product_id");
                assertThat(product1Id.equals("P1"));
                Integer pageNumber = documentContext.read("pageable.pageNumber");
                assertThat(pageNumber.equals(0));
                Integer pageSize = documentContext.read("pageable.pageSize");
                assertThat(pageSize.equals(3));
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @Order(36)
        void shouldReturnASortedPageOfOrders() {
                OrderRequestDto postRequest1 = new OrderRequestDto("P1", "sku1", 11, userDetails);
                OrderRequestDto postRequest2 = new OrderRequestDto("P2", "sku2", 12, userDetails);
                OrderRequestDto postRequest3 = new OrderRequestDto("P3", "sku3", 13, userDetails);
                InventoryClientStub.stubInventoryCall("sku1", 11);
                InventoryClientStub.stubInventoryCall("sku2", 12);
                InventoryClientStub.stubInventoryCall("sku3", 13);

                ResponseEntity<String> postResponse1 = restTemplate
                .postForEntity("/api/v1/orders", postRequest1, String.class);
                ResponseEntity<String> postResponse2 = restTemplate
                .postForEntity("/api/v1/orders", postRequest2, String.class);
                ResponseEntity<String> postResponse3 = restTemplate
                .postForEntity("/api/v1/orders", postRequest3, String.class);

                assertThat(postResponse1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(postResponse2.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                assertThat(postResponse3.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                
                ResponseEntity<String> getResponse = restTemplate
                        .getForEntity("/api/v1/orders?page=0&size=3&sort=quantity,desc", String.class);
                DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
                log.info("GET RESPONSE BODY: " + getResponse.getBody());
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                Integer quantity = documentContext.read("content[0].quantity");
                assertThat(quantity.equals(13));
                assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
}