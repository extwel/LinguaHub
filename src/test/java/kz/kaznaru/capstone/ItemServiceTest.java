package kz.kaznaru.capstone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import kz.kaznaru.capstone.infra.InMemoryItemRepository;
import kz.kaznaru.capstone.service.ItemService;
import org.junit.jupiter.api.Test;

class ItemServiceTest {
    private final ItemService service = new ItemService(new InMemoryItemRepository());

    @Test
    void createdItemIsListed() {
        service.create("first");
        assertEquals(1, service.all().size());
    }

    @Test
    void blankTitleIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> service.create(" "));
    }
}
