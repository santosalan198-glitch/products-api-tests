package com.alan.qa.tests;

import com.alan.qa.base.BaseTest;
import com.alan.qa.utils.ApiConstants;
import com.alan.qa.utils.TokenManager;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ValidationTests extends BaseTest {

    // ==================== VALIDACIONES DE CAMPOS REQUERIDOS ====================

    @Test(description = "Crear producto sin titulo (debe fallar)")
    public void testCreateProductWithoutTitle() {
        String requestBody = "{\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Producto sin título rechazado (400)");
    }

    @Test(description = "Crear producto sin price (debe fallar)")
    public void testCreateProductWithoutPrice() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Producto sin price rechazado (400)");
    }

    @Test(description = "Crear producto sin category (debe fallar)")
    public void testCreateProductWithoutCategory() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 50\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Producto sin category rechazado (400)");
    }

    // ==================== VALIDACIONES DE VALORES INVÁLIDOS ====================

    @Test(description = "Precio negativo rechazado")
    public void testCreateProductWithNegativePrice() {
        String requestBody = "{\n" +
                "    \"title\": \"Invalid Product\",\n" +
                "    \"price\": -100,\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Precio negativo rechazado (400)");
    }

    @Test(description = "Precio cero rechazado")
    public void testCreateProductWithZeroPrice() {
        String requestBody = "{\n" +
                "    \"title\": \"Zero Price Product\",\n" +
                "    \"price\": 0,\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Precio cero rechazado (400)");
    }

    @Test(description = "Stock negativo rechazado")
    public void testCreateProductWithNegativeStock() {
        String requestBody = "{\n" +
                "    \"title\": \"Negative Stock Product\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": -10,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Stock negativo rechazado (400)");
    }

    // ==================== VALIDACIONES DE CAMPOS VACÍOS ====================

    @Test(description = "Título vacío rechazado")
    public void testCreateProductWithEmptyTitle() {
        String requestBody = "{\n" +
                "    \"title\": \"\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Título vacío rechazado (400)");
    }

    @Test(description = "Category vacío rechazado")
    public void testCreateProductWithEmptyCategory() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"\"\n" +
                "}";

        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Category vacío rechazado (400)");
    }

    // ==================== VALIDACIONES DE ACTUALIZACIÓN ====================

    @Test(description = "Actualizar precio a negativo (debe fallar)",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testUpdateProductToNegativePrice() {
        String requestBody = "{\n" +
                "    \"price\": -500\n" +
                "}";

        String token = TokenManager.getToken();

        int productId = getFirstProductId();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Actualizar a precio negativo rechazado (400)");
    }

    @Test(description = "Actualizar stock a negativo (debe fallar)",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testUpdateProductToNegativeStock() {
        String requestBody = "{\n" +
                "    \"stock\": -50\n" +
                "}";

        String token = TokenManager.getToken();

        int productId = getFirstProductId();

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Validación: Stock negativo en actualización rechazado (400)");
    }

    // ==================== VALIDACIONES DE TIPOS DE DATOS ====================

    private void validateInvalidField(String requestBody, String fieldName) {

        Response response = given()
                .header("Authorization", "Bearer " + TokenManager.getToken())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT);

        int statusCode = response.getStatusCode();

        Assert.assertNotEquals(statusCode, 500,
                "La API explotó al recibir " + fieldName + " inválido");

        if (statusCode == 400) {
            System.out.println("[PASS] Campo '" + fieldName +
                    "' inválido rechazado correctamente");
        } else if (statusCode == 201) {
            Object savedValue = response.path(fieldName);
            System.out.println("[WARN] API aceptó " + fieldName +
                    " inválido. Valor guardado: " + savedValue);
        } else {
            Assert.fail("Status inesperado: " + statusCode);
        }
    }

    @Test(description = "Precio como string debe fallar o manejarse")
    public void testCreateProductWithPriceAsString() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"price\": \"not-a-number\",\n" +
                "    \"stock\": 50,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        validateInvalidField(requestBody, "price");
    }

    @Test(description = "Stock como string debe fallar o manejarse")
    public void testCreateProductWithStockAsString() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": \"fifty\",\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        validateInvalidField(requestBody, "stock");
    }
}
