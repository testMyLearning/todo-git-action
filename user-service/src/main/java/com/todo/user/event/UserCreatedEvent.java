package com.todo.user.event;

import com.todo.common.event.BaseEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class UserCreatedEvent extends BaseEvent {
    private Long userId;
    private String email;
    private String name;
    private String role;

    public UserCreatedEvent(Long userId, String email, String name, String role) {
        super(String.valueOf(userId),"USER_CREATED", Instant.now(),"user-service");
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }
}
