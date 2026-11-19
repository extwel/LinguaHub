package kz.kaznaru.capstone.service;

import kz.kaznaru.capstone.domain.Item;
import kz.kaznaru.capstone.domain.ItemRepository;
import java.util.List;

/** Application service: constructor injection, depends on the interface only. */
public class ItemService {
    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public Item create(String title) {
        return repository.save(title);
    }

    public List<Item> all() {
        return repository.findAll();
    }
}
