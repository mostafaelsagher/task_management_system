package com.taskmanagementsystem.tms.service.impl;

import com.taskmanagementsystem.tms.exception.BusinessError;
import com.taskmanagementsystem.tms.exception.BusinessException;
import com.taskmanagementsystem.tms.models.Task;
import com.taskmanagementsystem.tms.models.TaskSearchCriteria;
import com.taskmanagementsystem.tms.repository.TaskRepository;
import com.taskmanagementsystem.tms.repository.specification.TaskSpecification;
import com.taskmanagementsystem.tms.service.EmailService;
import com.taskmanagementsystem.tms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final EmailService emailService;

  @Override
  public Task save(Task task) {
    emailService.sendEmail(
        getCurrentUsername(),
        "New Task Created",
        "A new task has been created: " + task.getTitle());
    return taskRepository.save(task);
  }

  @Override
  public Task update(Long id, Task task) {
    Task existingTask = findById(id);
    existingTask.setTitle(task.getTitle());
    existingTask.setDescription(task.getDescription());
    existingTask.setStatus(task.getStatus());
    existingTask.setPriority(task.getPriority());
    existingTask.setDueDate(task.getDueDate());
    return taskRepository.save(existingTask);
  }

  @Override
  public void delete(Long id) {
    taskRepository.deleteById(id);
  }

  @Override
  public Task findById(Long id) {
    return taskRepository
        .findById(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "Task with id " + id + " not found", BusinessError.RESOURCE_NOT_FOUND));
  }

  @Override
  public Page<Task> findAll(Pageable pageable) {
    return taskRepository.findAll(pageable);
  }

  @Override
  public Page<Task> findByCriteria(TaskSearchCriteria criteria, Pageable pageable) {
    return taskRepository.findAll(TaskSpecification.withCriteria(criteria), pageable);
  }

  @Override
  public String getCurrentUsername() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    } else {
      return principal.toString();
    }
  }
}
