/*
 * Copyright (c) 2026 Jenforcer contributors
 */
package org.jenforcer.contracts;

/**
 * Base interface for tests interfaces that will test adherence to a contract
 * behavior defined by an interface.
 *
 * @param <T> the type of the value created for testing
 *
 */
public interface Testable<T>
{
    /**
     * Create an instance of the class under test.
     *
     * @return the value to be used for testing
     * @throws Exception allows for easy handling of checked exceptions instead
     * of having to catch and handle/re-throw
     */
    T createValue() throws Exception;
}
