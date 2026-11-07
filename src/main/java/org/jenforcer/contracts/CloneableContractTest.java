/*
 * Copyright (c) 2026 Jenforcer contributors
 */

package org.jenforcer.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests the adherence to the contract for shallow cloning of classes that implement the
 * {@link Cloneable} interface.
 *
 * An object that implements the  {@link Cloneable} interface must be able to pass all the
 * tests applied based on the contract definition.
 *
 * @param <T> the cloneable type under test
 */
@Tag("jenforcer-code-contracts")
public abstract class CloneableContractTest<T extends Cloneable> implements Testable<T>
{
    private T value;
    private T clone1;
    private T clone2;

    /**
     * Creates a clone contract test.
     */
    protected CloneableContractTest()
    {
    }

    /**
     * Creates the source value and its clones before each contract test.
     *
     * @throws Exception if a value cannot be created or cloned
     */
    @BeforeEach
    protected void setUp() throws Exception
    {
        this.value = createValue();
        this.clone1 = cloneObject(value);
        this.clone2 = cloneObject(value);
    }

    /**
     * Verify class override Object.clone (which is protected) with a public method.
     */
    @Test
    protected void testPublicCloneMethod()
    {
        final Method method = Assertions.assertDoesNotThrow(
                () -> value.getClass().getMethod("clone"),
                "Contract violation clone() method should be public");
        if (!Modifier.isPublic(method.getModifiers()))
        {
            fail("Contract violation clone() method should be public");
        }
    }

    /**
     * Test field-for-field copy of instances of class.
     *
     * @throws Exception if field cannot be read
     */
    @Test
    protected void testClone() throws Exception
    {
        assertNotSame(value, clone1);
        assertNotSame(value, clone2);
        assertNotSame(clone1, clone2);
//        if (value == clone1 || value == clone2 || clone1 == clone2)
//        {
//            fail("Contract violation clonable object cannot be refer to the same object being cloned");
//        }

        assertEquals(value.getClass(), clone1.getClass());
        assertEquals(value.getClass(), clone2.getClass());

        final Set<String> ignoredFields = getExcludedFieldNames();
        final List<Field> fields = getAllFields(value.getClass());
        Object expectedValue;
        Object actualValue;
        for (final Field field : fields)
        {
            if (!ignoredFields.contains(field.getName()))
            {
                expectedValue = readField(field, value);
                actualValue = readField(field, clone1);

                assertEquals(expectedValue, actualValue);
                assertEquals(expectedValue, readField(field, clone2));

            }
        }
    }

    /**
     * The clone method of Object is protected. Cloneable test classes will need to implement this
     * method to invoke the clone method on the public clone method so the object can be cloned in
     * test methods.
     *
     * @param value the object to be cloned
     * @return the cloned object
     * @throws CloneNotSupportedException if the object cannot or does not support cloning
     */
    protected abstract T cloneObject(T value) throws CloneNotSupportedException;

    /**
     * Override this method if you want to exclude certain fields when comparing clones.
     *
     * @return the collection of field names to ignore
     */
    protected Set<String> getExcludedFieldNames()
    {
        return new HashSet<>();
    }

    private List<Field> getAllFields(final Class<?> type)
    {
        final List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass())
        {
            fields.addAll(List.of(current.getDeclaredFields()));
        }
        return fields;
    }

    private Object readField(final Field field, final Object instance) throws IllegalAccessException
    {
        if (!field.trySetAccessible())
        {
            throw new IllegalAccessException("Cannot access field " + field.getName());
        }
        return field.get(instance);
    }
}
