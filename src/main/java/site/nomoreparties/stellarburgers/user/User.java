package site.nomoreparties.stellarburgers.user;

import io.qameta.allure.Step;

public class User {
    private String email;
    private String password;
    private String name;

    public String getEmail() {
        return email;
    }

    @Step("Изменить email")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    @Step("Изменить password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }
    @Step("Изменить name")
    public void setName(String name) {
        this.name = name;
    }


    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User() {
    }


}
