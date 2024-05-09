package site.nomoreparties.stellarburgers.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestClient {
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    protected RequestSpecification specContentType() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification specWithoutContentType() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .build();
    }
}
