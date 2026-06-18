package com.alan.qa.tests;

import com.alan.qa.base.BaseTest;
import com.alan.qa.utils.ApiConstants;
import com.alan.qa.utils.TokenManager;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ProductCreateTests extends BaseTest {
    @Feature("Products")
    @Story("Create Products")
    @Description("Crear un producto nuevo con todos los campos válidos")
    @Test(description = "Crear producto con datos válidos",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testCreateProductSuccessfully() {
        String requestBody = "{\n" +
                "    \"title\": \"Monitor LG 4K\",\n" +
                "    \"price\": 599.99,\n" +
                "    \"stock\": 25,\n" +
                "    \"category\": \"electronics\",\n" +
                "    \"description\": \"Monitor Ultra HD 4K\"\n" +
                "}";

        String token = TokenManager.getToken();

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(201)
                .extract()
                .response();

        Integer newProductId = response.path("id");
        String title = response.path("title");
        Number price = response.path("price");

        Assert.assertNotNull(newProductId, "ID no debería ser null");
        Assert.assertEquals(title, "Monitor LG 4K", "Título incorrecto");
        Assert.assertEquals(price.doubleValue(), 599.99, 0.01, "Precio incorrecto");

        System.out.println("[PASS] Producto creado. ID: " + newProductId);
    }

    @Feature("Products")
    @Story("Create Products")
    @Description("Validar que crear producto sin token es rechazado (seguridad)")
    @Test(description = "Crear producto sin token (debe fallar)")
    public void testCreateProductWithoutToken() {
        String requestBody = "{\n" +
                "    \"title\": \"Test Product\",\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 10,\n" +
                "    \"category\": \"electronics\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(401);

        System.out.println("[PASS] Acceso denegado sin token");
    }

    @Feature("Products")
    @Story("Create Products")
    @Description("Validar que campo title es requerido en creación")
    @Test(description = "Crear producto sin title (validación)")
    public void testCreateProductWithoutTitle() {
        String requestBody = "{\n" +
                "    \"price\": 100,\n" +
                "    \"stock\": 10,\n" +
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

        System.out.println("[PASS] Validación: Producto sin title rechazado");
    }
}
