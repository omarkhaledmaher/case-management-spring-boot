package com.example.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import com.example.model.Case;
import jakarta.persistence.criteria.Predicate;

public class CaseSpecification {
    public static Specification<Case> hasUser(String username) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("assignedUsers").get("username"),
                username);
    }

    public static Specification<Case> hasSearchTerm(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isBlank()) {
                return null;
            }
            String comparisonString = "%" + searchTerm.toLowerCase() + "%";

            Predicate nameLike = cb.like(cb.lower(root.get("name")), comparisonString);
            Predicate customerNameLike = cb.like(cb.lower(root.get("details").get("customerName")), comparisonString);
            Predicate applicantNameLike = cb.like(cb.lower(root.get("details").get("applicantName")), comparisonString);
            Predicate referenceNameLike = cb.like(cb.lower(root.get("details").get("referenceName")), comparisonString);

            return cb.or(nameLike, customerNameLike, applicantNameLike, referenceNameLike);
        };
    }
}
