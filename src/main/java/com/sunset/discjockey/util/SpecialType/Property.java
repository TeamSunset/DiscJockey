package com.sunset.discjockey.util.SpecialType;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * C# style property
 *
 * @param <T> type of the value
 *            getter: oldValue -> {}
 *            setter: (oldValue, newValue) -> {}
 */
public class Property<T> {
    T value;

    public Function<T, T> getter;

    public BiConsumer<T, T> setter;

    public Property(Function<T, T> getter, BiConsumer<T, T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public Property(T value, Function<T, T> getter, BiConsumer<T, T> setter) {
        this.value = value;
        this.getter = getter;
        this.setter = setter;
    }

    public T get() {
        return getter.apply(this.value);
    }

    public void set(T value) {
        setter.accept(this.value, value);
    }
}
