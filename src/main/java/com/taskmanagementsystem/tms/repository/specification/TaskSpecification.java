package com.taskmanagementsystem.tms.repository.specification;

import com.taskmanagementsystem.tms.models.Task;
import com.taskmanagementsystem.tms.models.TaskSearchCriteria;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TaskSpecification {

  public static Specification<Task> withCriteria(TaskSearchCriteria criteria) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (criteria.getTitle() != null) {
        predicates.add(cb.like(root.get("title"), "%" + criteria.getTitle() + "%"));
      }
      if (criteria.getDescription() != null) {
        predicates.add(cb.like(root.get("description"), "%" + criteria.getDescription() + "%"));
      }
      if (criteria.getStatus() != null) {
        predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
      }
      if (criteria.getDueDate() != null) {
        predicates.add(cb.equal(root.get("dueDate"), criteria.getDueDate()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
