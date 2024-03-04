package com.taskmanagementsystem.tms.service;

import com.taskmanagementsystem.tms.models.Task;
import com.taskmanagementsystem.tms.models.TaskSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface TaskService {

    Task save(Task task);

    Task update(Long id, Task task);

    void delete(Long id);

    Task findById(Long id);

    Page<Task> findAll(Pageable pageable);

    Page<Task> findByCriteria(TaskSearchCriteria criteria, Pageable pageable);

     String getCurrentUsername();
}

