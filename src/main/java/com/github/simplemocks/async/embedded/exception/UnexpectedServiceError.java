package com.github.simplemocks.async.embedded.exception;

import com.github.simplemocks.error_service.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.simplemocks.async.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.2
 */
public class UnexpectedServiceError extends ServiceException {

    public UnexpectedServiceError(@Nonnull String systemMessage) {
        super(ERROR_SOURCE, "UNEXPECTED_ERROR", systemMessage);
    }
}
