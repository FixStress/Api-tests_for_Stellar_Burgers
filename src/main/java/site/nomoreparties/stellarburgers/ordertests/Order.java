package site.nomoreparties.stellarburgers.ordertests;

import io.qameta.allure.Step;

import java.util.List;

public class Order {
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public Order() {

    }

    public List<String> getIngredients() {
        return ingredients;
    }

    @Step("Изменить список ингредиентов")
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

}
