package com.todo.user.event;

import com.todo.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserCreatedEvent extends BaseEvent {
    private Long userId;
    private String email;
    private String name;
    private String role;

    public UserCreatedEvent(Long userId, String email, String name, String role) {
        this.eventType = "USER_CREATED";
        this.aggregateId = userId;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
