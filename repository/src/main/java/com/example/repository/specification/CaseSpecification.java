package com.example.repository.specification;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.example.common.enums.CaseStatus;
import com.example.common.enums.CaseType;
import com.example.model.Case;
import jakarta.persistence.criteria.Predicate;

public class CaseSpecification {
    public static Specification<Case> hasUser(String username) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("assignedUsers").get("username"),
                username);
    }

    public static Specification<Case> hasSearchTerm(String searchTerm) {
        return (root, query, cb) -> {
            String comparisonString = "%" + searchTerm.toLowerCase() + "%";

            Predicate nameLike = cb.like(cb.lower(root.get("name")), comparisonString);
            Predicate customerNameLike = cb.like(cb.lower(root.get("details").get("customerName")), comparisonString);
            Predicate applicantNameLike = cb.like(cb.lower(root.get("details").get("applicantName")), comparisonString);
            Predicate referenceNameLike = cb.like(cb.lower(root.get("details").get("referenceName")), comparisonString);

            return cb.or(nameLike, customerNameLike, applicantNameLike, referenceNameLike);
        };
    }

    public static Specification<Case> hasType(CaseType type) {
        return (root, query, cb) -> {
            return cb.equal(root.get("type"), type);
        };
    }

    public static Specification<Case> hasAnyType(List<CaseType> types) {
        return (root, query, cb) -> {
            if (types.isEmpty()) {
                return cb.conjunction();
            }
            Predicate predicate = cb.equal(root.get("type"), types.getFirst());
            for (int i = 1; i < types.size(); i++) {
                Predicate condition = cb.equal(root.get("type"), types.get(i));
                predicate = cb.or(predicate, condition);
            }
            return predicate;
        };
    }

    public static Specification<Case> hasStatus(CaseStatus status) {
        return (root, query, cb) -> {
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Case> hasAnyStatus(List<CaseStatus> statuses) {
        return (root, query, cb) -> {
            if (statuses.isEmpty()) {
                return cb.conjunction();
            }
            Predicate predicate = cb.equal(root.get("status"), statuses.getFirst());
            for (int i = 1; i < statuses.size(); i++) {
                Predicate condition = cb.equal(root.get("status"), statuses.get(i));
                predicate = cb.or(predicate, condition);
            }
            return predicate;
        };
    }
}
