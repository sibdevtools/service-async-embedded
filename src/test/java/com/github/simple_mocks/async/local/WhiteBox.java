package com.github.simple_mocks.async.local;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

/**
 * @author sibmaks
 * @since 0.0.1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WhiteBox {

    public static void set(Object object, String fieldName, Object value) {
        var folderField = Objects.requireNonNull(ReflectionUtils.findField(object.getClass(), fieldName));
        ReflectionUtils.makeAccessible(folderField);
        ReflectionUtils.setField(folderField, object, value);
    }

}
