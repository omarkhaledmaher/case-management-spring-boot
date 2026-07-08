package com.example.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import com.example.model.Event;
import jakarta.persistence.criteria.Predicate;

public class EventSpecification {
    public static Specification<Event> hasSearchTerm(String searchTerm) {
        return (root, query, cb) -> {
            String comparisonString = "%" + searchTerm.toLowerCase() + "%";
            Predicate entityNameLike = cb.like(cb.lower(root.get("code").get("entityName")), comparisonString);
            Predicate usernameLike = cb.like(cb.lower(root.get("username")), comparisonString);

            return cb.or(entityNameLike, usernameLike);
        };
    }
}
