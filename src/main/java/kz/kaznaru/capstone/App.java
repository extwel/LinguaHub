package kz.kaznaru.capstone;

import kz.kaznaru.capstone.infra.InMemoryItemRepository;
import kz.kaznaru.capstone.service.ItemService;

/** Composition root: the ONLY place allowed to wire concrete classes together. */
public final class App {
    private App() { }

    public static void main(String[] args) {
        ItemService service = new ItemService(new InMemoryItemRepository());
        service.create("Hello, patterns");
        service.all().forEach(i -> System.out.println(i.id() + ": " + i.title()));
    }
}
