package com.todo.analytics.repository;

import com.todo.analytics.entity.DeadlineAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeadlineAlertRepository extends JpaRepository<DeadlineAlert,Long> {
@Query("select d from DeadlineAlert d where d.deadline between :start and :end " +
        "and d.status= 'ACTIVE' and d.alertSent = false order by d.deadline asc")
    List<DeadlineAlert> findTasksWithDeadlineBetween(@Param("start") LocalDate today,
                                                     @Param("end") LocalDate tomorrow);

    @Modifying
    @Query("update DeadlineAlert d set d.alertSent = true, d.updatedAt = :now " +
            "where d.taskId = :taskId")
    void markAlertAsSent(@Param("taskId") UUID taskId, @Param("now") LocalDateTime now);

    Optional<DeadlineAlert> findByTaskId(UUID taskId);
}
