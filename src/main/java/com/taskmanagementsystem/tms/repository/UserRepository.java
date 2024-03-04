package com.taskmanagementsystem.tms.repository;

import com.taskmanagementsystem.tms.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository  extends JpaRepository<UserInfo, Long> {
    UserInfo findByUsername(String username);

}