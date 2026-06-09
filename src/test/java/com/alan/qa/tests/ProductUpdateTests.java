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

public class ProductUpdateTests extends BaseTest {

    @Feature("Products")
    @Story("Update Products")
    @Description("Actualizar precio de un producto existente")
    @Test(description = "Actualizar precio de producto",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testUpdateProductPrice() {
        int productId = 1;
        String requestBody = "{\n" +
                "    \"price\": 1299.99\n" +
                "}";

        String token = TokenManager.getToken();

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Number updatedPrice = response.path("price");
        Assert.assertEquals(updatedPrice.doubleValue(), 1299.99, 0.01, "Precio no actualizado");

        System.out.println("✅ Precio actualizado a: " + updatedPrice);
    }

    @Feature("Products")
    @Story("Update Products")
    @Description("Validar que actualizar sin token es rechazado")
    @Test(description = "Actualizar producto sin token (debe fallar)")
    public void testUpdateProductWithoutToken() {
        String requestBody = "{\n" +
                "    \"price\": 500\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put(ApiConstants.PRODUCTS_ENDPOINT + "/1")
                .then()
                .statusCode(401);

        System.out.println("✅ Actualización denegada sin token");
    }
}