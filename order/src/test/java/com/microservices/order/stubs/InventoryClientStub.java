package com.microservices.order.stubs;

import lombok.experimental.UtilityClass;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@UtilityClass
public class InventoryClientStub {
    
    public void stubInventoryCall(String sku, Integer quantity) {
        stubFor(get(urlEqualTo("/api/v1/inventory?sku=" + sku + "&quantity=" + quantity))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("true")));
    }
}
