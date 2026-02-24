package com.todo.analytics.repository;

import com.todo.analytics.entity.DeadlineAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeadlineAlertRepository extends JpaRepository<DeadlineAlert,Long> {
@Query("select d from DeadlineAlert d where d.deadline between :start and :end " +
        "and d.status= 'ACTIVE' order by d.deadline asc")
    List<DeadlineAlert> findTasksWithDeadlineBetween(@Param("start") LocalDate today,
                                                     @Param("end") LocalDate tomorrow);
}
