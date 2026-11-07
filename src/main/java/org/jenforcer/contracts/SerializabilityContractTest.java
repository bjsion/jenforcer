/*
 * Copyright (c) 2026 Jenforcer contributors
 */

package org.jenforcer.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Tests the adherence to the contract for natural ordering of classes that implement the
 * {@link Serializable} interface.
 *
 * <p>An object that implements the {@link Serializable} interface must be able to pass all the
 * tests applied based on the contract definition.
 *
 * <p>This class is based heavily off the SerializabilityTestCase from junit addons.
 *
 * @see <a href=http://junit-addons.sourceforge.net/>junit-addons</a>
 * @param <T> the serializable type under test
 */
@TestInstance(Lifecycle.PER_CLASS)
@Tag("jenforcer-code-contracts")
public abstract class SerializabilityContractTest<T extends Serializable>
{
    /**
     * Creates a serializability contract test.
     */
    protected SerializabilityContractTest()
    {
    }

    /**
     * Evaluate if the class declares a field {@code serialVersionUID} that is static final long.
     *
     * @throws Exception if an instance cannot be created
     */
    @Test
    protected void containsSerialVersionUID() throws Exception
    {
        final T originalObject = createInstance();
        final boolean hasSerialVersionUID = Arrays.stream(
                originalObject.getClass().getDeclaredFields())
                    .anyMatch(f -> f.getName().equals("serialVersionUID"));
        assertTrue(hasSerialVersionUID, "Contract Violation: it is highly recommended "
                + "Serializable objects declare a static final long serialVersionUID field");
    }

    /**
     * Evaluate the serializability of the class under test by performing a serialisation
     * and deserialization and evaluating the deserialized object is equal to the original.
     *
     * @throws Exception if instance creation or serialization fails
     */
    @Test
    protected void serializationTest() throws Exception
    {
        final T originalObject = createInstance();
        final Class<? extends Serializable> type = originalObject.getClass();
        final byte[] input = serialize(originalObject);
        final T actual = deserialize(input);
        assertNotSame(originalObject, actual);
        assertEquals(type, actual.getClass());
        assertEquals(originalObject, actual);
    }

    /**
     * Implementers of this test need to implement and return the object under serialization testing.
     *
     * @return the object to serialize
     * @throws Exception if an instance cannot be created
     */
    protected abstract T createInstance() throws Exception;

    private byte[] serialize(final T obj) throws IOException
    {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos))
        {
            oos.writeObject(obj);
            oos.flush();

            return baos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private T deserialize(final byte[] bytes) throws ClassNotFoundException, IOException
    {
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bais))
        {
            return (T) ois.readObject();
        }
    }
}
