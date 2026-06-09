package com.alan.qa.tests;

import com.alan.qa.base.BaseTest;
import com.alan.qa.utils.ApiConstants;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class ProductTests extends BaseTest {

    @Feature("Products")
    @Story("Get Products")
    @Description("Obtener lista completa de todos los productos disponibles")
    @Test(description = "Obtener todos los productos")
    public void testGetAllProducts() {
        int totalProducts = given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .path("total");

        Assert.assertTrue(totalProducts > 0, "Debería haber productos");
        System.out.println("✅ Total productos: " + totalProducts);
    }

    @Feature("Products")
    @Story("Get Products")
    @Description("Obtener un producto específico por su ID")
    @Test(description = "Obtener producto por ID")
    public void testGetProductById() {
        int productId = 1;

        String title = given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT + "/" + productId)
                .then()
                .statusCode(200)
                .extract()
                .path("title");

        Assert.assertNotNull(title, "Producto debería tener título");
        System.out.println("✅ Producto encontrado: " + title);
    }

    @Feature("Products")
    @Story("Get Products")
    @Description("Validar que producto inexistente devuelve 404")
    @Test(description = "Producto no existe (404)")
    public void testGetNonExistentProduct() {
        given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT + "/99999")
                .then()
                .statusCode(404);

        System.out.println("✅ Producto no encontrado (404)");
    }

    @Feature("Products")
    @Story("Get Products")
    @Description("Validar estructura completa y tipos de datos del producto")
    @Test(description = "Validar estructura de producto")
    public void testProductStructure() {
        Number price = given()
                .when()
                .get(ApiConstants.PRODUCTS_ENDPOINT + "/1")
                .then()
                .statusCode(200)
                .extract()
                .path("price");

        Assert.assertTrue(price.doubleValue() > 0, "Price debe ser mayor a 0");
        System.out.println("✅ Estructura de producto validada");
    }
}