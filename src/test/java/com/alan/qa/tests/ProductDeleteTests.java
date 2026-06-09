package com.alan.qa.tests;

import com.alan.qa.base.BaseTest;
import com.alan.qa.utils.ApiConstants;
import com.alan.qa.utils.TokenManager;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ProductDeleteTests extends BaseTest {

    @Feature("Products")
    @Story("Delete Products")
    @Description("Eliminar un producto existente de la base de datos")
    @Test(description = "Eliminar producto",
            dependsOnMethods = "com.alan.qa.tests.AuthTests.testLoginSuccessfully")
    public void testDeleteProduct() {
        // Primero obtenemos un producto que sabemos que existe
        int productId = given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .path("products[0].id");

        String token = TokenManager.getToken();

        // Ahora eliminamos ese producto
        String message = given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .then()
                .statusCode(200)
                .extract()
                .path("message");

        Assert.assertTrue(message.contains("eliminado"), "Mensaje de eliminación incorrecto");

        System.out.println("✅ Producto ID " + productId + " eliminado correctamente");
    }

    @Feature("Products")
    @Story("Delete Products")
    @Description("Validar que eliminar sin token es rechazado")
    @Test(description = "Eliminar producto sin token (debe fallar)")
    public void testDeleteProductWithoutToken() {
        given()
                .when()
                .delete(ApiConstants.PRODUCTS_ENDPOINT + "/1")
                .then()
                .statusCode(401);

        System.out.println("✅ Eliminación denegada sin token");
    }

    @Feature("Products")
    @Story("Delete Products")
    @Description("Validar que eliminar producto inexistente devuelve 404")
    @Test(description = "Eliminar producto inexistente (404)")
    public void testDeleteNonExistentProduct() {
        String token = TokenManager.getToken();

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(ApiConstants.PRODUCTS_ENDPOINT + "/99999")
                .then()
                .statusCode(404);

        System.out.println("✅ Producto inexistente no eliminado (404)");
    }
}