package site.nomoreparties.stellarburgers.ordertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.usertests.User;
import site.nomoreparties.stellarburgers.usertests.UserClient;
import site.nomoreparties.stellarburgers.usertests.UserGenerator;

import java.util.ArrayList;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("Создание заказа. Ручка: POST /api/orders")
public class CreateOrderTests {
    private User user;
    UserClient userClient = new UserClient();
    private Order order;
    OrderClient orderClient = new OrderClient();
    private String accessToken;
    boolean skipAfter = false;


    @Before
    public void createTestDataAndStartLogging() {
        user = UserGenerator.randomUser();
        order = OrderGenerator.randomOrder(orderClient.getAllIngredients());
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @After
    public void deleteUserAndStopLogging() {
        if (!skipAfter) {
            userClient.deleteUser(accessToken);
        }
        RestAssured.reset();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    @Description("Чтобы создать заказ, нужно передать в headers accessToken и в теле список ингредиентов. Запрос возвращает корректное тело ответа и статус код 200")
    public void createOrderWithAccessTokenAndWithIngredients() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createUserResponse);
        ValidatableResponse createOrderResponse = orderClient.createOrderWithAccessToken(order, accessToken)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order._id", notNullValue(String.class))
                .and()
                .body("order.ingredients", not(empty()))
                .and()
                .body("order.owner.email", equalTo(user.getEmail()));
        String orderHashCode = orderClient.getOrderHashCOde(createOrderResponse);
        orderClient.getAllOrders()
                .assertThat()
                .body("orders[0]._id", equalTo(orderHashCode)); // Убедились, что в списке всех заказов, есть заказ с таким хэш кодом

    }

    @Test
    @DisplayName("Создание заказа без авторизации и с ингредиентами")
    @Description("Чтобы создать заказ, нужно передать в теле список ингредиентов. Запрос возвращает корректное тело ответа и статус код 200")
    public void createOrderWithoutAccessTokenWithIngredients() {
        orderClient.createOrderWithoutAccessToken(order)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("order.number", notNullValue(int.class));
        skipAfter = true;
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с пустым списком ингредиентов")
    @Description("Запрос возвращает статус код 400 Bad Request и корректное тело ответа - success: false, message: Ingredient ids must be provided")
    public void createOrderWithAuthorizationAndWithoutIngredients() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createUserResponse);
        order.setIngredients(new ArrayList<>());
        orderClient.createOrderWithAccessToken(order, accessToken)
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
        orderClient.getOrdersListWithAccessToken(accessToken)
                .assertThat()
                .body("orders", empty()); // Убедились, что в списке заказов пользователя не создан заказ с пустым списком ингредиентов
    }

    @Test
    @DisplayName("Создание заказа без авторизации и с пустым списком ингредиентов")
    @Description("Запрос возвращает статус код 400 Bad Request и корректное тело ответа - success: false, message: Ingredient ids must be provided")
    public void createOrderWithoutAuthorizationAndWithoutIngredients() {
        order.setIngredients(new ArrayList<>());
        orderClient.createOrderWithoutAccessToken(order)
                .assertThat()
                .statusCode(400)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
        skipAfter = true;
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и невалидным хэш кодом ингредиента")
    @Description("Запрос возвращает статус код 500 Internal Server Error")
    public void createOrderWithAuthorizationAndWIthIncorrectHash() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createUserResponse);
        order.setIngredients(OrderGenerator.incorrectHash());
        orderClient.createOrderWithAccessToken(order, accessToken)
                .assertThat()
                .statusCode(500);
        orderClient.getOrdersListWithAccessToken(accessToken)
                .assertThat()
                .body("orders", empty()); // Убедились, что в списке заказов пользователя не создан заказ с невалидным хэшом ингредиента
    }

    @Test
    @DisplayName("Создание заказа без авторизаци и невалидным хэш кодом ингредиента")
    @Description("Запрос возвращает статус код 500 Internal Server Error")
    public void createOrderWithoutAuthorizationAndWIthIncorrectHash() {
        order.setIngredients(OrderGenerator.incorrectHash());
        orderClient.createOrderWithoutAccessToken(order)
                .assertThat()
                .statusCode(500);
        skipAfter = true;
    }
}
