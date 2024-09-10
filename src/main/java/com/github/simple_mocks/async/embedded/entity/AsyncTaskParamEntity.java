package com.github.simple_mocks.async.embedded.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Entity(name = "async_task_async_task_param")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "async_service", name = "async_task_param")
public class AsyncTaskParamEntity {
    @EmbeddedId
    private AsyncTaskParamEntityId entityId;
    @Column(name = "param_value", nullable = false, length = 1024)
    private String value;
}
