package product.com.microservices;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import com.microservices.product.ProductApp;
import com.microservices.product.dto.ProductRequestDto;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;


@Slf4j
//@RunWith(SpringRunner.class)
@DisplayName("Integration Test")
@Testcontainers
@SpringBootTest(classes = ProductApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ProductAppTest {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"));

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void Setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
    }

    @Test
    void shouldPostAndGetProduct() {
        ProductRequestDto request = new ProductRequestDto("product_A", 111f);
        Response postResponse = RestAssured.given()
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/v1/products").andReturn();
        log.info("POST TEST -> " + postResponse.getHeader("Location"));
        postResponse.then().statusCode(201);
        postResponse.then().header("Location", Matchers.notNullValue());

        String id = postResponse.getHeader("Location").split("/")[4];

        Response getResponse = RestAssured.given()
            .contentType("application/json")
            .when()
            .get("/api/v1/products/" + id).andReturn();
        log.info("GET TEST -> " + getResponse.asString());
        getResponse.then().statusCode(200);
        getResponse.then().body("price", Matchers.equalTo(request.price()));
    }

    @Test
    void shouldPostAndUpdateAndGetProduct() {
        ProductRequestDto request = new ProductRequestDto("product_B", 222f);
        Response postResponse = RestAssured.given()
            .contentType("application/json")
            .body(request)
            .when()
            .post("/api/v1/products").andReturn();
        log.info("POST TEST -> " + postResponse.getHeader("Location"));
        postResponse.then().statusCode(201);
        postResponse.then().header("Location", Matchers.notNullValue());
        
        String id = postResponse.getHeader("Location").split("/")[4];
        ProductRequestDto updateRequest = new ProductRequestDto("product_B", 333f);
        Response putResponse = RestAssured.given()
            .contentType("application/json")
            .body(updateRequest)
            .when()
            .put("/api/v1/products/" + id).andReturn();
        log.info("PUT TEST -> " + putResponse.asString());
        putResponse.then().statusCode(204);

        Response getResponse = RestAssured.given()
            .contentType("application/json")
            .when()
            .get("/api/v1/products/" + id).andReturn();
        log.info("GET TEST -> " + getResponse.asString());
        getResponse.then().body("price", Matchers.equalTo(updateRequest.price()));

    }
}
