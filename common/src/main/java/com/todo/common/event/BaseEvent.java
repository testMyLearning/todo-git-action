package com.todo.common.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class BaseEvent {
    protected UUID eventId= UUID.randomUUID();
    protected Long aggregateId;
    protected String eventType;
    protected Instant createdAt = Instant.now();

}
