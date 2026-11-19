package kz.kaznaru.capstone.infra;

import kz.kaznaru.capstone.domain.Item;
import kz.kaznaru.capstone.domain.ItemRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> store = new LinkedHashMap<>();
    private long next = 1;

    @Override
    public Item save(String title) {
        Item item = new Item(next++, title);
        store.put(item.id(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }
}
