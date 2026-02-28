package com.todo.task.repository;

import com.todo.task.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;


public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    @Query(value = "SELECT * FROM outbox WHERE published_at IS NULL AND retry_count < 10 ORDER BY id ASC LIMIT ?1",
            nativeQuery = true)
    List<OutboxEvent> findUnpublishedEvents(int limit);
    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.publishedAt < :before")
    void deletePublishedBefore(Instant before);
}
