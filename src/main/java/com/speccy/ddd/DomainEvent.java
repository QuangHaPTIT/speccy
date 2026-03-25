package com.speccy.ddd;

import java.time.Instant;

public interface DomainEvent {
    Instant happenedAt();

    String getEventName();
}