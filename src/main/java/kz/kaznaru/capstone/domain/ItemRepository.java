package kz.kaznaru.capstone.domain;

import java.util.List;
import java.util.Optional;

/** Port owned by the domain; implemented in infra (Repository pattern, Lecture 14). */
public interface ItemRepository {
    Item save(String title);
    Optional<Item> findById(long id);
    List<Item> findAll();
}
