package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feed {
    Long timestamp;
    Integer userId;
    Event eventType;
    Operation operation;
    Integer eventId;
    Integer entityId;

}
