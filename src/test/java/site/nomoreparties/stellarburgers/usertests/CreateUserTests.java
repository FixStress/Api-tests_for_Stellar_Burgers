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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("Создание пользователя. Ручка: POST /api/auth/register")
public class CreateUserTests {
    private User user;
    private final UserClient userClient = new UserClient();
    private ValidatableResponse createResponse;
    private boolean skipAfter = false;


    @Before
    public void createTestDataAndStartLogging() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        user = UserGenerator.randomUser();
    }

    @After
    public void deleteUserAndStopLogging() {
        if (!skipAfter) {
            String accessToken = userClient.getAccessToken(createResponse);
            userClient.deleteUser(accessToken);
        }
        RestAssured.reset();
    }

    @Test
    @DisplayName("Уникального пользователя можно создать")
    @Description("Чтобы создать пользователя, нужно передать в ручку все обязательные поля: email, password, name. Запрос возвращает корректное тело ответа и статус код 200")
    public void userCanBeCreatedTest() {
        createResponse = userClient.createUser(user)
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
        userClient.loginUser(UserCredentials.from(user)) // Отправили запрос на авторизацию, чтобы убедиться, что пользователь создан
                .assertThat()
                .statusCode(200)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать пользователя который уже зарегистрирован")
    @Description("Запрос возвращает статус код 403 Forbidden и корректное тело ответа - success: false, message: User already exists")
    public void userWhoIsAlreadyRegisteredCannotBeCreatedTest() {
        createResponse = userClient.createUser(user);
        userClient.createUser(user) // Попытка создания пользователя, с такими же данными
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Нельзя создать пользователя без передачи обязательного поля email")
    @Description("Запрос возвращает статус код 403 Forbidden и корректное тело ответа - success: false, message: Email, password and name are required fields")
    public void userCannotBeCreatedWithoutEmailFieldTest() {
        user.setEmail(null);
        userClient.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        skipAfter = true;
    }

    @Test
    @DisplayName("Нельзя создать пользователя без передачи обязательного поля password")
    @Description("Запрос возвращает статус код 403 Forbidden и корректное тело ответа - success: false, message: Email, password and name are required fields")
    public void userCannotBeCreatedWithoutPasswordFieldTest() {
        user.setPassword(null);
        userClient.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        skipAfter = true;
    }

    @Test
    @DisplayName("Нельзя создать пользователя без передачи обязательного поля email")
    @Description("Запрос возвращает статус код 403 Forbidden и корректное тело ответа - success: false, message: Email, password and name are required fields")
    public void userCannotBeCreatedWithoutNameFieldTest() {
        user.setName(null);
        userClient.createUser(user)
                .assertThat()
                .statusCode(403)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
        skipAfter = true;
    }
}
