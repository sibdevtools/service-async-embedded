package com.github.sibdevtools.async.embedded.exception;

import com.github.sibdevtools.error.exception.ServiceException;
import jakarta.annotation.Nonnull;

import static com.github.sibdevtools.async.embedded.constant.Constant.ERROR_SOURCE;

/**
 * @author sibmaks
 * @since 0.0.2
 */
public class UnexpectedServiceError extends ServiceException {

    public UnexpectedServiceError(@Nonnull String systemMessage) {
        super(ERROR_SOURCE, "UNEXPECTED_ERROR", systemMessage);
    }
}
