package com.github.simple_mocks.async.embedded.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@Getter
@AllArgsConstructor
public enum AsyncTaskStatus {
    CREATED(false),
    RETRYING(false),
    COMPLETED(true),
    FAILED(true);

    private final boolean finalStatus;
}
