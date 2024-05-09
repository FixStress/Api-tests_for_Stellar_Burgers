package site.nomoreparties.stellarburgers.order;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderGenerator {
    static Random random = new Random();

    public static List<String> incorrectHash() {
        String incorrectHash = RandomStringUtils.random(24, true, true); // Латиница и цифры
        List<String> incorrectHashList = new ArrayList<>();
        incorrectHashList.add(incorrectHash);
        return incorrectHashList;
    }

    /*Так как явных требований нет, опирался на свою логику и логику работы сайта
    Оформил создание рандомных ТД для создания заказа, с двумя одинаковыми булками
    и с рандомным количеством и выбором других ингредиентов*/
    public static Order randomOrder(ValidatableResponse getIngredientsResponse) {

        JsonPath jsonPath = getIngredientsResponse.extract().jsonPath();
        List<String> dataIds = jsonPath.getList("data._id"); // Список всех хэшей ингредиентов
        List<String> dataTypes = jsonPath.getList("data.type"); // Список всех типов ингредиентов
        List<String> bunHash = new ArrayList<>(); // Список хэшей булок
        List<String> otherIngredientsHash = new ArrayList<>(); // Список хэшей других ингредиентов
        List<String> randomHashesToCreateOnOrder = new ArrayList<>(); // Список для создания заказа с рандомными хешами из БД
        // Перебираем все хэши типов ингредиентов и забираем хэши только булок
        for (int i = 0; i < dataTypes.size(); i++) {
            if ("bun".equals(dataTypes.get(i))) {
                bunHash.add(dataIds.get(i));
            }
        }
        // Перебираем все хэши типов ингредиентов и забираем хэши , которые не являются булкой
        for (int i = 0; i < dataTypes.size(); i++) {
            if (!("bun".equals(dataTypes.get(i)))) {
                otherIngredientsHash.add(dataIds.get(i));
            }
        }
        int randomBunIndex = random.nextInt(bunHash.size()); // Переменная для добавления в заказ рандомной булки
        randomHashesToCreateOnOrder.add(bunHash.get(randomBunIndex)); // Так как явных требований нет, опирался на логику работы оформления заказа на сайте, где в заказе обязательны две булки
        randomHashesToCreateOnOrder.add(bunHash.get(randomBunIndex)); // Добавили одну и ту же булку в двух экземплярах
        int randomCountIngredients = random.nextInt(otherIngredientsHash.size()) + 1; // Переменная для добавления рандомного количества ингредиентов помимо булки( исключили 0-ое количество)
        for (int i = 0; i < randomCountIngredients; i++) {
            int randomIndex = random.nextInt(otherIngredientsHash.size()); // Рандомный индекс из списка других ингредиентов
            randomHashesToCreateOnOrder.add(otherIngredientsHash.get(randomIndex)); // Добавляем хэш в заказ
        }


        return new Order(randomHashesToCreateOnOrder); // Получили рандомные тестовые данные для создания заказа
    }
}

