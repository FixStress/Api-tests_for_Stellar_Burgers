package site.nomoreparties.stellarburgers.usertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.user.User;
import site.nomoreparties.stellarburgers.user.UserClient;
import site.nomoreparties.stellarburgers.user.UserCredentials;
import site.nomoreparties.stellarburgers.user.UserGenerator;

import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("Изменение данных пользователя. Ручка: PATCH /api/auth/user")
public class DataChangeUserTests {

    private User user;
    private final UserClient userClient = new UserClient();
    private String accessToken;


    @Before
    public void createTestDataAndStartLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = UserGenerator.randomUser();
        ValidatableResponse createResponse = userClient.createUser(user);
        accessToken = userClient.getAccessToken(createResponse);
    }

    @After
    public void deleteUserAndStopLogging() {
        userClient.deleteUser(accessToken);
        RestAssured.reset();
    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить свою почту")
    @Description("Для изменения данных необходимо передать в headers accessToken и поля с отредактированной информацией. Запрос возвращает корректное тело ответа и статус код 200")
    public void userChangeEmailWithAccessTokenTest() {
        user.setEmail(UserGenerator.changeEmail());
        userClient.changeUserData(accessToken, user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(user.getEmail()));
        userClient.getUserData(accessToken) // Получили данные пользователя, чтобы убедиться, что почта обновилась
                .assertThat()
                .body("user.email", equalTo(user.getEmail()));

    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить свой пароль")
    @Description("Для изменения данных необходимо передать в headers accessToken и поля с отредактированной информацией. Запрос возвращает корректное тело ответа и статус код 200")
    public void userChangePasswordWithAccessTokenTest() {
        user.setPassword(UserGenerator.changePassword());
        userClient.changeUserData(accessToken, user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
        userClient.loginUser(UserCredentials.from(user)) // Авторизовались пользователем с новым паролем, чтобы убедиться, что пароль обновился
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(user.getEmail()));

    }

    @Test
    @DisplayName("Авторизованный пользователь может изменить своё имя ")
    @Description("Для изменения данных необходимо передать в headers accessToken и поля с отредактированной информацией. Запрос возвращает корректное тело ответа и статус код 200")
    public void userChangeNameWithAccessTokenTest() {
        user.setName(UserGenerator.changeName());
        userClient.changeUserData(accessToken, user)
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.name", equalTo(user.getName()));
        userClient.getUserData(accessToken) // Получили данные пользователя, чтобы убедиться, что имя обновилось
                .assertThat()
                .body("user.name", equalTo(user.getName()));

    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить свою почту")
    @Description("Запрос выполняется без передачи accessToken. Возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: You should be authorised")
    public void userCannotChangeEmailWithoutAccessTokenTest() {
        String actualEmail = user.getEmail(); // Сохранили актуальную почту пользователя, для дальнейшей проверки
        user.setEmail(UserGenerator.changeEmail());
        userClient.changeUserDataWithoutAccessToken(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
        userClient.getUserData(accessToken) // Получили данные пользователя, чтобы убедиться, что почта не обновилась
                .assertThat()
                .body("user.email", equalTo(actualEmail));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить свой пароль")
    @Description("Запрос выполняется без передачи accessToken. Возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: You should be authorised")
    public void userCannotChangePasswordWithoutAccessTokenTest() {
        user.setPassword(UserGenerator.changePassword());
        userClient.changeUserDataWithoutAccessToken(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
        userClient.loginUser(UserCredentials.from(user)) // Убедились, что не можем авторизоваться с паролем, на который пытались изменить актуальный пароль пользователя
                .assertThat()
                .statusCode(401)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить своё имя")
    @Description("Запрос выполняется без передачи accessToken. Возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: You should be authorised")
    public void userCannotChangeNameWithoutAccessTokenTest() {
        String actualName = user.getName(); // Сохранили актуальное имя пользователя, для дальнейшей проверки
        user.setName(UserGenerator.changeName());
        userClient.changeUserDataWithoutAccessToken(user)
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("You should be authorised"));
        userClient.getUserData(accessToken) // Получили данные пользователя, чтобы убедиться, что имя не обновилось
                .assertThat()
                .body("user.name", equalTo(actualName));
    }
}
