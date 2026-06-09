package com.alan.qa.base;

import com.alan.qa.utils.ApiConstants;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeTest;

public class BaseTest {
    @BeforeTest
    public void setup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
    }
}
