
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
    void shouldHandleCartAndDiscountOperations() throws Exception {

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String input = String.join("\n",
                "Beginner",
        "1", "1",
        "1", "1",
        "1", "abc",
        "1", "99",
        "1", "4",
        "2",
        "4", "1", "10",
        "2",
        "4", "2", "50000",
        "4", "2", "5000",
        "2",
        "5",
        "2",
        "3", "1",
        "3", "99",
        "4",
        "5",
        "9",
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

        assertTrue(result.contains("Курс успешно добавлен"));
        assertTrue(result.contains("уже находится в корзине"));
        assertTrue(result.contains("Скидка успешно применена"));
        assertTrue(result.contains("Скидка отменена"));
        assertTrue(result.contains("Спасибо за использование LinguaHub"));
    }
}
