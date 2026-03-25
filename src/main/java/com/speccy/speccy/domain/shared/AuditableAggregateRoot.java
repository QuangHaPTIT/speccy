package com.speccy.speccy.domain.shared;

import com.speccy.ddd.AggregateRoot;
import com.speccy.speccy.application.utils.Util;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class AuditableAggregateRoot<R extends AggregateRoot<R>> extends AggregateRoot<R> {

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Util.getNowUTC();
        this.updatedAt = Util.getNowUTC();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Util.getNowUTC();
    }
}