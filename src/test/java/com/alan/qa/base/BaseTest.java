package com.alan.qa.base;

import com.alan.qa.utils.ApiConstants;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeTest;

public class BaseTest {

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000; // 5 segundos entre intentos

    @BeforeTest
    public void setup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
        warmupWithRetries();
    }

    private void warmupWithRetries() {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                Response r = RestAssured.get(ApiConstants.PRODUCTS_ENDPOINT);
                if (r.getStatusCode() == 200) {
                    System.out.println("Warmup successful on attempt " + attempt + ". Status: " + r.getStatusCode());
                    return;
                }
                System.out.println("Warmup attempt " + attempt + " returned status: " + r.getStatusCode());
            } catch (Exception e) {
                System.out.println("Warmup attempt " + attempt + " failed: " + e.getMessage());
            }

            if (attempt < MAX_RETRIES) {
                sleep(RETRY_DELAY_MS);
            }
        }

        throw new IllegalStateException(
                "API not available after " + MAX_RETRIES + " warmup attempts. Aborting test suite."
        );
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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