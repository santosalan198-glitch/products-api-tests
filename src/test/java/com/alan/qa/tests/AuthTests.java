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

public class AuthTests extends BaseTest {

    @Feature("Authentication")
    @Story("User Login")
    @Description("Login exitoso y guardar token para usar en otros tests")
    @Test(description = "Login exitoso y guardar token", priority = 1)
    public void testLoginSuccessfully() {
        String requestBody = "{\n" +
                "    \"username\": \"emilys\",\n" +
                "    \"password\": \"emilyspass\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.LOGIN_ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = response.path("accessToken");
        Assert.assertNotNull(token, "Token no debería ser null");
        Assert.assertFalse(token.isEmpty(), "Token no debería estar vacío");

        TokenManager.setToken(token);

        String username = response.path("username");
        Assert.assertEquals(username, "emilys", "Username incorrecto");

        System.out.println("[PASS] Login exitoso. Token: " + token.substring(0, 20) + "...");
    }

    @Feature("Authentication")
    @Story("User Login")
    @Description("Validar que credenciales incorrectas son rechazadas")
    @Test(description = "Login con credenciales incorrectas", priority = 2)
    public void testLoginWithWrongCredentials() {
        String requestBody = "{\n" +
                "    \"username\": \"wronguser\",\n" +
                "    \"password\": \"wrongpass\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.LOGIN_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Login rechazado con credenciales incorrectas");
    }

    @Feature("Authentication")
    @Story("User Login")
    @Description("Validar que login sin password es rechazado")
    @Test(description = "Login sin password")
    public void testLoginWithoutPassword() {
        String requestBody = "{\n" +
                "    \"username\": \"emilys\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(ApiConstants.LOGIN_ENDPOINT)
                .then()
                .statusCode(400);

        System.out.println("[PASS] Login rechazado sin password");
    }
}

