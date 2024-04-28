package site.nomoreparties.stellarburgers.user;

import com.google.gson.Gson;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.rest.RestClient;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {

    private static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    private static final String GET_AND_REFRESH_AND_DELETE_USER_DATA_ENDPOINT = "/api/auth/user";
    private static final String AUTHORIZATION_USER_ENDPOINT = "/api/auth/login";

    @Step("Создать пользователя")
    public ValidatableResponse createUser(User user) {
        String requestBody = new Gson().toJson(user);
        Allure.addAttachment("Request body", "application/json", requestBody);
        return given()
                .spec(specContentType())
                .body(user)
                .when()
                .post(CREATE_USER_ENDPOINT)
                .then();
    }

    @Step("Получить accessToken")
    public String getAccessToken(ValidatableResponse createResponse) {
        return createResponse.extract().jsonPath().getString("accessToken");
    }

    @Step("Авторизоваться пользователем")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        String requestBody = new Gson().toJson(userCredentials);
        Allure.addAttachment("Request body", "application/json", requestBody);
        return given()
                .spec(specContentType())
                .body(userCredentials)
                .post(AUTHORIZATION_USER_ENDPOINT)
                .then();
    }

    @Step("Изменить данные пользователя")
    public ValidatableResponse changeUserData(String accessToken, User user) {
        String requestBody = new Gson().toJson(user);
        Allure.addAttachment("Request body", "application/json", requestBody);
        return given()
                .spec(specContentType())
                .header("Authorization", accessToken)
                .body(user)
                .patch(GET_AND_REFRESH_AND_DELETE_USER_DATA_ENDPOINT)
                .then();
    }

    @Step("Изменить данные, без передачи accessToken")
    public ValidatableResponse changeUserDataWithoutAccessToken(User user) {
        String requestBody = new Gson().toJson(user);
        Allure.addAttachment("Request body", "application/json", requestBody);
        return given()
                .spec(specContentType())
                .body(user)
                .patch(GET_AND_REFRESH_AND_DELETE_USER_DATA_ENDPOINT)
                .then();
    }

    @Step("Получить данные пользователя")
    public ValidatableResponse getUserData(String accessToken) {
        return given()
                .spec(specWithoutContentType())
                .header("Authorization", accessToken)
                .get(GET_AND_REFRESH_AND_DELETE_USER_DATA_ENDPOINT)
                .then();
    }


    @Step("Удалить пользователя")
    public void deleteUser(String accessToken) {
        given()
                .spec(specWithoutContentType())
                .header("Authorization", accessToken)
                .delete(GET_AND_REFRESH_AND_DELETE_USER_DATA_ENDPOINT)
                .then();

    }


}