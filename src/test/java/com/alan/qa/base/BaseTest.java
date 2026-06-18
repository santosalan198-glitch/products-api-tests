package com.alan.qa.base;

import com.alan.qa.utils.ApiConstants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;

public class BaseTest {
    @BeforeTest
    public void setup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;

        try {
            Response r = RestAssured.get(ApiConstants.PRODUCTS_ENDPOINT);
            System.out.println("Warmup request status: " + r.getStatusCode());
        } catch (Exception e) {
            System.out.println("Warning: warmup request failed: " + e.getMessage());
        }
    }

    protected int getFirstProductId() {
        try {
            return RestAssured.get(ApiConstants.PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(200)
                    .extract()
                    .path("products[0].id");
        } catch (Exception e) {
            throw new IllegalStateException("No hay productos disponibles: " + e.getMessage());
        }
    }

}
