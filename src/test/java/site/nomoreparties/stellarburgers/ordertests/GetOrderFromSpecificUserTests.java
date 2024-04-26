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

import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("Получение заказов конкретного пользователя. Ручка: GET /api/orders")
public class GetOrderFromSpecificUserTests {

    UserClient userClient = new UserClient();
    OrderClient orderClient = new OrderClient();


    @Before
    public void startLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @After
    public void stopLogging() {
        RestAssured.reset();
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя, с авторизацией")
    @Description("Чтобы получить список заказов пользователя, нужно передать в headers accessToken. Запрос возвращает корректное тело ответа и статус код 200")
    public void getOrdersWithAccessTokenTest() {
        User user = UserGenerator.randomUser();
        Order order = OrderGenerator.randomOrder(orderClient.getAllIngredients());
        ValidatableResponse createUserResponse = userClient.createUser(user);
        String accessToken = userClient.getAccessToken(createUserResponse);
        ValidatableResponse createOrderResponse = orderClient.createOrderWithAccessToken(order, accessToken);
        String orderHashCode = orderClient.getOrderHashCOde(createOrderResponse);
        orderClient.getOrdersListWithAccessToken(accessToken)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("orders[0]._id", equalTo(orderHashCode)) // Проверили наличие созданного заказа, в списке заказов конкретного пользователя
                .and()
                .body("orders[0].ingredients", equalTo(order.getIngredients())); // Проверили, что заказ создан с корректными ингредиентами
        userClient.deleteUser(accessToken);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя, без авторизации")
    @Description("Запрос возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: You should be authorised")
    public void getOrdersWithoutAccessTokenTest() {
        orderClient.getOrdersListWithoutAccessToken()
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

