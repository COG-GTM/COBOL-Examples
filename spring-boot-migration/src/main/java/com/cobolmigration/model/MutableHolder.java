package com.cobolmigration.model;

/**
 * Generic mutable wrapper to simulate COBOL's CALL BY REFERENCE semantics.
 *
 * In COBOL (sub_program/main_app.cbl):
 *   CALL "sub-app" USING ws-item-1 ws-item-2          -> BY REFERENCE (default)
 *   CALL "sub-app" USING BY CONTENT ws-item-1 ws-item-2 -> BY CONTENT (copy)
 *
 * BY REFERENCE: The callee can modify the caller's variable directly.
 *   In Java, primitives and immutable objects (String) are passed by value,
 *   so we use MutableHolder to wrap them and allow modification.
 *
 * BY CONTENT: A copy is passed; the callee cannot affect the caller's data.
 *   In Java, this is the default behavior for primitives and String.
 *
 * @param <T> the type of the held value
 */
public class MutableHolder<T> {

    private T value;

    public MutableHolder() {
    }

    public MutableHolder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }
}
