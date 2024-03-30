package com.axllblc.worlddays.data;

import java.util.function.Supplier;

/**
 * Container object that wraps a value (of type T) or an exception. If a value is present,
 * {@link #isSuccess()} returns {@code true} and {@link #get()} returns that value. If no value is
 * present, {@link #get()} throws an exception.
 * @param <T> Type of value
 * @see Success
 * @see Error
 */
public interface Result<T> {
    boolean isSuccess();
    T get() throws Throwable;
    T orNull();
    T orElse(T other);
    T orElseGet(Supplier<? extends T> supplier);

    /**
     * Create a new {@link Success} object, containing {@code value}.
     * @param value An object
     * @return A new {@link Success} object
     * @param <T> Type of value
     */
    static <T> Success<T> success(T value) {
        return Success.of(value);
    }

    /**
     * Create a new {@link Error} object, with {@code exception}.
     * @param exception A {@link Throwable} object
     * @return A new {@link Error} object
     * @param <T> Type of expected value
     */
    static <T> Error<T> error(Throwable exception) {
        return Error.of(exception);
    }

    class Success<T> implements Result<T> {
        /**
         * Create a new {@link Success} object, containing {@code value}.
         * @param value An object
         * @return A new {@link Success} object
         * @param <T> Type of value
         */
        public static <T> Success<T> of(T value) {
            return new Success<>(value);
        }

        private final T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
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
        /**
         * Create a new {@link Error} object, with {@code exception}.
         * @param exception A {@link Throwable} object
         * @return A new {@link Error} object
         * @param <T> Type of expected value
         */
        public static <T> Error<T> of(Throwable exception) {
            return new Error<>(exception);
        }

        private final Throwable exception;

        private Error(Throwable exception) {
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() throws Throwable {
            throw exception;
        }

        @Override
        public T orNull() {
            return null;
        }

        @Override
        public T orElse(T other) {
            return other;
        }

        @Override
        public T orElseGet(Supplier<? extends T> supplier) {
            return supplier.get();
        }
    }
}
