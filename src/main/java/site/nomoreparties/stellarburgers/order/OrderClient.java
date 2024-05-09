package site.nomoreparties.stellarburgers.order;

import com.google.gson.Gson;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import site.nomoreparties.stellarburgers.rest.RestClient;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String CREATE_AND_GET_ORDER_ENDPOINT = "/api/orders";
    private static final String GET_INGREDIENTS_LIST_ENDPOINT = "/api/ingredients";
    private static final String GET_ALL_ORDERS_ENDPOINT = "/api/orders/all";

    @Step("Получить список всех ингредиентов")
    public ValidatableResponse getAllIngredients() {
        return given()
                .spec(specWithoutContentType())
                .get(GET_INGREDIENTS_LIST_ENDPOINT)
                .then();
    }

    @Step("Получить список всех заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(specWithoutContentType())
                .get(GET_ALL_ORDERS_ENDPOINT)
                .then();

    }

    @Step("Оформить заказ авторизованным пользователем")
    public ValidatableResponse createOrderWithAccessToken(Order order, String accessToken) {
        Allure.addAttachment("Request body", "application/json", new Gson().toJson(order));
        return given()
                .spec(specContentType())
                .header("Authorization", accessToken)
                .body(order)
                .post(CREATE_AND_GET_ORDER_ENDPOINT)
                .then();
    }

    @Step("Создать заказ неавторизованным пользователем")
    public ValidatableResponse createOrderWithoutAccessToken(Order order) {
        Allure.addAttachment("Request body", "application/json", new Gson().toJson(order));
        return given()
                .spec(specContentType())
                .body(order)
                .post(CREATE_AND_GET_ORDER_ENDPOINT)
                .then();
    }

    @Step("Получить хэш код заказа")
    public String getOrderHashCOde(ValidatableResponse createOrderResponse) {
        return createOrderResponse.extract().jsonPath().getString("order._id");
    }

    @Step("Получить заказы конкретного пользователя, с авторизацией")
    public ValidatableResponse getOrdersListWithAccessToken(String accessToken) {
        return given()
                .spec(specWithoutContentType())
                .header("Authorization", accessToken)
                .get(CREATE_AND_GET_ORDER_ENDPOINT)
                .then();
    }

    @Step("Получить заказы конкретного пользователя, без авторизации")
    public ValidatableResponse getOrdersListWithoutAccessToken() {
        return given()
                .spec(specWithoutContentType())
                .get(CREATE_AND_GET_ORDER_ENDPOINT)
                .then();
    }
}
