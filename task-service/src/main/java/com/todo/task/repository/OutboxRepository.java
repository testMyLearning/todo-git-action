package com.todo.task.repository;

import com.todo.task.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;


public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    @Query("select o from OutboxEvent o where o.publishedAt is null and o.retryCount<10 order by o.id ASC")
    List<OutboxEvent> findUnpublishedEvents(int limit);
    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.publishedAt < :before")
    void deletePublishedBefore(Instant before);
}
