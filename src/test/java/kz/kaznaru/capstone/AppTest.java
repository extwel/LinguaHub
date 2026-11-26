package kz.kaznaru.capstone;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void shouldHandleCartDiscountAndPurchaseOperations() throws Exception {

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String input = String.join("\n",
                "English",
                "Beginner",

                // US-03: Добавление курса
                "1", "1",

                // Повторное добавление
                "1", "1",

                // Некорректный ID
                "1", "abc",

                // Несуществующий курс
                "1", "99",

                // Добавление недоступного курса
                "1", "4",

                // Просмотр корзины
                "2",

                // US-05: Процентная скидка
                "4", "1", "10",
                "2",

                // Недопустимая фиксированная скидка
                "4", "2", "50000",

                // Фиксированная скидка
                "4", "2", "5000",
                "2",

                // Отмена скидки
                "5",
                "2",

                // US-04: Удаление курса
                "3", "1",

                // Удаление отсутствующего курса
                "3", "99",

                // Скидка на пустую корзину
                "4",

                // Отмена скидки
                "5",

                // Покупка с пустой корзиной
                "7",

                // US-08: Пустой список купленных курсов
                "8",

                // Добавляем курс снова
                "1", "1",

                // US-07: Сначала отменяем покупку
                "7", "0",

                // Проверяем сохранение корзины
                "2",

                // US-08: Подтверждаем покупку
                "7", "1",

                // Проверяем купленные курсы
                "8",

                // Проверяем очистку корзины
                "2",

                // Попытка повторной покупки
                "1", "1",

                // Завершение программы
                "0"
        ) + "\n";

        try {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)
            ));

            System.setOut(new PrintStream(output, true, "UTF-8"));

            App.main(new String[0]);

        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }

        String result = output.toString("UTF-8");

        // US-03
        assertTrue(result.contains("Курс успешно добавлен"));
        assertTrue(result.contains("уже находится в корзине"));

        // US-04
        assertTrue(result.contains("Курс успешно удалён"));

        // US-05
        assertTrue(result.contains("Скидка успешно применена"));
        assertTrue(result.contains("Скидка отменена"));

        // US-07
        assertTrue(result.contains("Корзина пуста"));
        assertTrue(result.contains(
                "======= Подтверждение покупки ======="));
        assertTrue(result.contains("ИТОГО К ОПЛАТЕ"));
        assertTrue(result.contains("Покупка отменена"));
        assertTrue(result.contains("Покупка успешно подтверждена"));

        // US-08
        assertTrue(result.contains(
                "У вас пока нет купленных курсов"));

        assertTrue(result.contains(
                "Вы успешно зарегистрированы на курсах"));

        assertTrue(result.contains("Корзина очищена"));

        assertTrue(result.contains("Мои курсы"));

        assertTrue(result.contains("Название: English A1"));

        assertTrue(result.contains("Статус: Вы зарегистрированы"));

        assertTrue(result.contains("Корзина пока пуста"));

        assertTrue(result.contains("Вы уже купили этот курс"));

        // Завершение программы
        assertTrue(result.contains(
                "Спасибо за использование LinguaHub"));
    }
}
