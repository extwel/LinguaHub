
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

                // US-05: Применение процентной скидки
                "4", "1", "10",
                "2",

                // Проверка недопустимой фиксированной скидки
                "4", "2", "50000",

                // Применение фиксированной скидки
                "4", "2", "5000",
                "2",

                // US-05: Отмена скидки
                "5",
                "2",

                // US-04: Удаление курса
                "3", "1",

                // Удаление отсутствующего курса
                "3", "99",

                // Попытка применить скидку к пустой корзине
                "4",

                // Отмена скидки
                "5",

                // US-07: Попытка покупки с пустой корзиной
                "7",

                // Добавляем курс снова
                "1", "1",

                // US-07: Подтверждение покупки
                "7", "1",

                // US-07: Отмена покупки
                "7", "0",

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
        assertTrue(result.contains("======= Подтверждение покупки ======="));
        assertTrue(result.contains("ИТОГО К ОПЛАТЕ"));
        assertTrue(result.contains("Покупка успешно подтверждена"));
        assertTrue(result.contains("Покупка отменена"));

        // Завершение программы
        assertTrue(result.contains("Спасибо за использование LinguaHub"));
    }
}
