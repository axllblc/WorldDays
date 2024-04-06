package com.axllblc.worlddays.data;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.NonNull;

/**
 * Container object that wraps a value (of type T) and/or an exception.
 * @param <T> Type of value
 * @see Success
 * @see Error
 */
public interface Result<T> {
    /**
     * Returns {@code true} in case of success, {@code false} in case of error.
     * See {@link #hasValue()} to check if a value is present
     * (in case of error, there can be a fallback value).
     *
     * @return {@code true} in case of success, {@code false} otherwise
     */
    boolean isSuccess();

    /**
     * Returns {@code true} if a value is present, {@code false} otherwise.
     * (In case of error, there can be a fallback value.)
     *
     * @return {@code true} if a value is present, {@code false} otherwise.
     * @see #isSuccess()
     */
    boolean hasValue();

    /**
     * If a value is present, returns it, otherwise throws a {@link NoSuchElementException}.
     * @return The value of this {@code Result}
     * @throws NoSuchElementException If no value is present
     */
    T get() throws NoSuchElementException;

    /**
     * If an exception is present, throws it.
     * @throws Throwable The exception
     */
    void throwException() throws Throwable;

    /**
     * If a value is present, returns it, otherwise throws the exception contained in this
     * {@code Result}.
     * @return The value of this {@code Result}
     * @throws Throwable If an exception is present
     */
    T orThrow() throws Throwable;

    /**
     * If a value is present, returns it, otherwise returns {@code null}.
     * @return The value of this {@code Result}, or {@code null}
     */
    T orNull();

    /**
     * If a value is present, returns it, otherwise returns {@code other}.
     * @return The value of this {@code Result}, or {@code other}
     */
    T orElse(T other);

    /**
     * If a value is present, returns it, otherwise returns the value provided by {@code supplier}.
     * @return The value of this {@code Result}, or the value provided by {@code supplier}
     */
    T orElseGet(Supplier<? extends T> supplier);

    /**
     * Create a new {@link Success} object, containing {@code value}.
     * @param value An object
     * @return A new {@link Success} object
     * @param <T> Type of value
     */
    static <T> Success<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Create a new {@link Error} object, with {@code exception}.
     * @param exception A {@link Throwable} object
     * @return A new {@link Error} object
     * @param <T> Type of expected value
     */
    static <T> Error<T> error(Throwable exception) {
        return new Error<>(exception);
    }

    /**
     * Create a new {@link Error} object, with {@code exception} and a fallback value.
     * @param exception A {@link Throwable} object
     * @param fallbackValue Fallback value
     * @return A new {@link Error} object
     * @param <T> Type of expected value
     */
    static <T> Error<T> error(Throwable exception, T fallbackValue) {
        return new Error<>(exception, fallbackValue);
    }

    class Success<T> implements Result<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean hasValue() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public void throwException() {
            // No exception to throw
        }

        @Override
        public T orThrow() {
            // No exception to throw
            return value;
        }

        @Override
        public T orNull() {
            return value;
        }

        @Override
        public T orElse(T other) {
            return value;
        }

        @Override
        public T orElseGet(Supplier<? extends T> supplier) {
            return value;
        }
    }

    class Error<T> implements Result<T> {
        private final Throwable exception;
        private final T fallbackValue;

        public Error(Throwable exception) {
            this.exception = exception;
            this.fallbackValue = null;
        }

        public Error(Throwable exception, @NonNull T fallbackValue) {
            this.exception = exception;
            this.fallbackValue = fallbackValue;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean hasValue() {
            return fallbackValue != null;
        }

        @Override
        public T get() throws NoSuchElementException {
            if (fallbackValue != null) {
                return fallbackValue;
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void throwException() throws Throwable {
            throw exception;
        }

        @Override
        public T orThrow() throws Throwable {
            if (fallbackValue != null) {
                return fallbackValue;
            } else {
                throw exception;
            }
        }

        @Override
        public T orNull() {
            return fallbackValue;
        }

        @Override
        public T orElse(T other) {
            if (fallbackValue != null) {
                return fallbackValue;
            } else {
                return other;
            }
        }

        @Override
        public T orElseGet(Supplier<? extends T> supplier) {
            if (fallbackValue != null) {
                return fallbackValue;
            } else {
                return supplier.get();
            }
        }
    }
}
