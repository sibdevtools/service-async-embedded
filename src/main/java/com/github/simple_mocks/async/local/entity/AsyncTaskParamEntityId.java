package com.github.simple_mocks.async.local.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AsyncTaskParamEntityId implements Serializable {
    @Column(name = "task_uid", nullable = false, length = 128)
    private String uid;
    @Column(name = "param_name", nullable = false, length = 256)
    private String name;
}
