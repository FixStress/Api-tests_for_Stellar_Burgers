package site.nomoreparties.stellarburgers.usertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.user.User;
import site.nomoreparties.stellarburgers.user.UserClient;
import site.nomoreparties.stellarburgers.user.UserCredentials;
import site.nomoreparties.stellarburgers.user.UserGenerator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("Логин пользователя. Ручка: POST /api/auth/login")
public class LoginUserTests {

    private User user;
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void createTestDataAndStartLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = UserGenerator.randomUser();
        accessToken = userClient.getAccessToken(userClient.createUser(user));
    }

    @After
    public void deleteUserAndStopLogging() {
        userClient.deleteUser(accessToken);
        RestAssured.reset();
    }


    @Test
    @DisplayName("Существующий пользователь может авторизоваться")
    @Description("Чтобы авторизоваться пользователем, нужно передать в теле обязательные поля: email, password. Запрос возвращает корректное тело ответа и статус код 200")
    public void userCanLoginTest() {
        userClient.loginUser(UserCredentials.from(user))
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true))
                .and()
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .and()
                .body("accessToken", containsString("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Нельзя авторизоваться с некорректным полем email")
    @Description("Запрос возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: email or password are incorrect")
    public void userCannotLoginIncorrectEmailFieldTest() {
        user.setEmail(UserGenerator.changeEmail());
        userClient.loginUser(UserCredentials.from(user)) // Попытка авторизации, с некорректной почтой
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Нельзя авторизоваться с некорректным полем password")
    @Description("Запрос возвращает статус код 401 Unauthorized и корректное тело ответа - success: false, message: email or password are incorrect")
    public void userCannotLoginIncorrectPasswordFieldTest() {
        user.setPassword(UserGenerator.changePassword());
        userClient.loginUser(UserCredentials.from(user)) // Попытка авторизации, с некорректным паролем
                .assertThat()
                .statusCode(401)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }


}
