package com.money.me.motivate.repository;

import com.money.me.motivate.domain.user.AppUser;
import com.money.me.motivate.domain.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findByUserAndCompleted(AppUser user, boolean completed);
}
