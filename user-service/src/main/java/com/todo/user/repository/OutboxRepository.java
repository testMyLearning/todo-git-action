package com.todo.user.repository;

import com.todo.user.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent,Long> {
    @Query("select o from OutboxEvent o where o.publishedAt is null order by o.id ASC")
    List<OutboxEvent> findUnpublishedEvents();
}
