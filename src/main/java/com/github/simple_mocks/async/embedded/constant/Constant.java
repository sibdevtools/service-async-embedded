package com.github.simple_mocks.async.embedded.constant;

import com.github.simple_mocks.error_service.api.ErrorSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author sibmaks
 * @since 0.0.2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constant {

    public static final ErrorSource ERROR_SOURCE = new ErrorSource("ASYNC_SERVICE");

}
