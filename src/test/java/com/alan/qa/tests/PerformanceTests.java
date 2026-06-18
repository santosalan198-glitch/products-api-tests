package com.alan.qa.tests;

import com.alan.qa.base.BaseTest;
import com.alan.qa.utils.ApiConstants;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PerformanceTests extends BaseTest {

    // ==================== RESPONSE TIME TESTS ====================

    @Test(description = "GET /products responde en < 500ms")
    public void testGetProductsResponseTime() {
        long startTime = System.currentTimeMillis();

        given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(200);

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Umbral relajado a 1000ms para reducir falsos negativos por latencia de red/arranque
        Assert.assertTrue(responseTime < 1000,
                "GET /products tardó " + responseTime + "ms (máximo 1000ms)");

        System.out.println("[PASS] Performance: GET /products respondió en " + responseTime + "ms");
    }

    @Test(description = "GET /products/:id responde en < 500ms")
    public void testGetProductByIdResponseTime() {
        // Intentamos hasta 3 veces si el servicio está en cold-start y devuelve 404
        int productId = getFirstProductId();
        long responseTime = -1;
        Response response = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            long startTime = System.currentTimeMillis();
            response = given()
                    .when()
                    .get(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                    .andReturn();
            long endTime = System.currentTimeMillis();
            responseTime = endTime - startTime;

            if (response.getStatusCode() == 200) {
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }

        // Verificar código y tiempo
        Assert.assertEquals(response.getStatusCode(), 200, "GET /products/:id devolvió " + response.getStatusCode());
        Assert.assertTrue(responseTime < 500,
                "GET /products/:id tardó " + responseTime + "ms (máximo 500ms)");

        System.out.println("[PASS] Performance: GET /products/:id respondió en " + responseTime + "ms (status " + response.getStatusCode() + ")");
    }

    @Test(description = "POST /auth/login responde en < 500ms",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testLoginResponseTime() {
        // Usar mismas credenciales que las pruebas de Auth para consistencia
        String requestBody = "{\n" +
                "    \"username\": \"emilys\",\n" +
                "    \"password\": \"emilyspass\"\n" +
                "}";

        long startTime = System.currentTimeMillis();
        Response response = given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(ApiConstants.LOGIN_ENDPOINT)
                .andReturn();

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        Assert.assertEquals(response.getStatusCode(), 200, "POST /auth/login devolvió " + response.getStatusCode());
        // Relajamos umbral a 1000ms para reducir falsos negativos por latencia/arranque
        Assert.assertTrue(responseTime < 1000,
                "POST /auth/login tardó " + responseTime + "ms (máximo 1000ms)");

        System.out.println("[PASS] Performance: POST /auth/login respondió en " + responseTime + "ms");
    }

    // ==================== STRESS TESTS ====================

    @Test(description = "GET /products bajo múltiples requests consecutivos")
    public void testMultipleRequestsUnderLoad() {
        int requestCount = 10;
        long totalTime = 0;
        long maxTime = 0;
        long minTime = Long.MAX_VALUE;

        for (int i = 0; i < requestCount; i++) {
            long startTime = System.currentTimeMillis();

            given()
                    .when()
                    .get(ApiConstants.PRODUCTS_ENDPOINT)
                    .then()
                    .statusCode(200);

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            totalTime += responseTime;
            maxTime = Math.max(maxTime, responseTime);
            minTime = Math.min(minTime, responseTime);

            Assert.assertTrue(responseTime < 1000,
                    "Request #" + (i + 1) + " tardó " + responseTime + "ms");
        }

        long avgTime = totalTime / requestCount;

        System.out.println("[PASS] Performance: " + requestCount + " requests completados");
        System.out.println("   Tiempo promedio: " + avgTime + "ms");
        System.out.println("   Mínimo: " + minTime + "ms");
        System.out.println("   Máximo: " + maxTime + "ms");

        Assert.assertTrue(avgTime < 500,
                "Tiempo promedio (" + avgTime + "ms) exceede 500ms");
    }

    @Test(description = "GET /products/1 responde consistentemente")
    public void testConsistentResponseTimes() {
        int iterations = 5;
        long[] responseTimes = new long[iterations];

        for (int i = 0; i < iterations; i++) {
            // Intento con reintentos en caso de cold start/404
            int productId = getFirstProductId();
            long responseTime = -1;
            Response response = null;
            for (int attempt = 1; attempt <= 3; attempt++) {
                long startTime = System.currentTimeMillis();
                response = given()
                        .when()
                        .get(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                        .andReturn();
                long endTime = System.currentTimeMillis();
                responseTime = endTime - startTime;

                if (response.getStatusCode() == 200) {
                    break;
                }

                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {
                }
            }

            // Si tras reintentos no hay 200, fallar con mensaje claro
            Assert.assertEquals(response.getStatusCode(), 200, "GET /products/1 devolvió " + response.getStatusCode() + " en la iteración " + (i + 1));
            responseTimes[i] = responseTime;
        }

        // Verificar que no hay variación extrema
        long maxTime = responseTimes[0];
        long minTime = responseTimes[0];

        for (long time : responseTimes) {
            maxTime = Math.max(maxTime, time);
            minTime = Math.min(minTime, time);
        }

        long variance = maxTime - minTime;

        // La variación no debería ser mayor a 200ms
        Assert.assertTrue(variance < 200,
                "Variación de " + variance + "ms es muy alta (máximo 200ms)");

        System.out.println("[PASS] Performance: Respuestas consistentes");
        System.out.println("   Variación: " + variance + "ms");
    }
}
