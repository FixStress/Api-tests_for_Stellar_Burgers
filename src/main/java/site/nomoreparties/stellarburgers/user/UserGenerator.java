package site.nomoreparties.stellarburgers.user;

import com.github.javafaker.Faker;


public class UserGenerator {

    private static final Faker faker = new Faker();

    public static User randomUser() {
        final String email = faker.internet().emailAddress();
        final String password = faker.internet().password();
        final String name = faker.name().firstName();
        return new User(email, password, name);
    }

    public static String changeEmail() {
        return faker.internet().emailAddress();
    }

    public static String changePassword() {
        return faker.internet().password();
    }

    public static String changeName() {
        return faker.name().firstName();
    }
}
