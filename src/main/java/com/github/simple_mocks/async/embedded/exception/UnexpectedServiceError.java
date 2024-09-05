package com.github.simple_mocks.async.embedded.exception;

import com.github.simple_mocks.error_service.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.simple_mocks.async.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.2
 */
public class UnexpectedServiceError extends ServiceException {

    public UnexpectedServiceError(@Nonnull String systemMessage) {
        super(ERROR_SOURCE, "UNEXPECTED_ERROR", systemMessage);
    }
}
