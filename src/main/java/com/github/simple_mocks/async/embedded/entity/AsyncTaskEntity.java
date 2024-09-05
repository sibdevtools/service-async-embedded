package com.github.simple_mocks.async.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "async_service", name = "async_task")
public class AsyncTaskEntity {
    @Id
    @Column(name = "task_uid", nullable = false, length = 128)
    private String uid;
    @Column(name = "task_type", nullable = false, length = 32)
    private String type;
    @Column(name = "task_version", nullable = false, length = 16)
    private String version;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AsyncTaskStatus status;
    @Column(name = "status_description", nullable = false, length = 512)
    private String statusDescription;
    @Column(name = "retry", nullable = false)
    private int retry;
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    @Column(name = "last_retry_at", nullable = false)
    private ZonedDateTime lastRetryAt;
    @Column(name = "next_retry_at", nullable = false)
    private ZonedDateTime nextRetryAt;
}
