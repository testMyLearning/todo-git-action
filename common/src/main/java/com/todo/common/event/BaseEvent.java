package com.todo.common.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public abstract class BaseEvent {
    public BaseEvent(String aggregateId, String eventType, Instant createdAt, String service) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.createdAt = createdAt;
        this.service = service;
    }

    protected UUID eventId= UUID.randomUUID();
    protected String aggregateId;
    protected String eventType;
    protected Instant createdAt;
    protected String service;



}
