/*
 * Copyright (c) 2026 Jenforcer contributors
 */
package org.jenforcer.contracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Tests the adherence to the {@link Object#equals(Object)} and {@link Object#hashCode()}
 * contract.
 *
 * <p>An object that implements the equals method must be able to pass all the
 * tests applied based on the contract definition.
 *
 * <p>This class also ensures that successive invocations of x.Equals(y) return
 * the same value as long as the objects referenced by x and y are not modified.
 *
 * <p>This class is based heavily off the EqualsHashCodeTestCase from junit addons.
 *
 * @see <a href=http://junit-addons.sourceforge.net/>junit-addons</a>
 *
 * @param <T> the type under test
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
@Tag("jenforcer-code-contracts")
public abstract class EqualsContractTest<T> implements Testable<T>
{
    private T value1;
    private T value2;
    private T value3;
    private T[] notEqualValues;

    /**
     * Creates an equals and hash code contract test.
     */
    protected EqualsContractTest()
    {
    }

    /**
     * Creates and returns a list of unequal instances of the class under test.
     * Each instance should vary by one condition (usually field value) of the equals
     * method to ensure full branch test coverage. If this is not important then just
     * add variants that are important for your testing.
     *
     * @return a list of instances of the object under test that should not be
     * equivalent to the object under test.
     * @throws Exception allows for easy handling of checked exceptions instead
     * of having to catch and handle/re-throw
     */
    protected abstract T[] createNotEqualValues() throws Exception;

    /**
     * Creates the test values before running the contract tests.
     *
     * @throws Exception if a test value cannot be created
     */
    @SuppressWarnings("ReferenceEquality") // Identity checks ensure the factories return distinct instances.
    @BeforeAll
    protected void setup() throws Exception
    {
        this.notEqualValues = this.createNotEqualValues();
        assertNotNull(this.notEqualValues, "must have values to compare against!");
        assertTrue(this.notEqualValues.length > 0, "must have values to compare against!");
        this.value1 = createValue();
        this.value2 = createValue();
        this.value3 = createValue();

        this.validateValue(this.value1);
        this.validateValue(this.value2);
        this.validateValue(this.value3);
        for (final T neValue: this.notEqualValues)
        {
            this.validateValue(neValue);
        }

        assertNotSame(value1, value2);
        assertNotSame(value1, value3);
        assertNotSame(value2, value3);
//        if (this.value1 == this.value2 ||
//            this.value1 == this.value3 ||
//            this.value2 == this.value3)
//        {
//            throw new IllegalArgumentException("Objects cannot reference the same object.");
//        }
        for (final T neValue: this.notEqualValues)
        {
            if (this.value1 == neValue ||
                this.value2 == neValue ||
                this.value3 == neValue)
            {
                throw new IllegalArgumentException("Objects cannot reference the same object.");
            }
        }
    }

    private void validateValue(final T value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException("instance1 cannot be null");
        }
    }

    /**
     * Test Equals method against <code>null</code>
     */
    @RepeatedTest(3)
    protected void TestEqualsMethodWithNull()
    {
        assertFalse(this.value1.equals(null), "Contract violation that x.Equals(null) returns false.");
        assertFalse(this.value2.equals(null), "Contract violation that x.Equals(null) returns false.");
        assertFalse(this.value3.equals(null), "Contract violation that x.Equals(null) returns false.");
        for (final T neValue: this.notEqualValues)
        {
            assertFalse(neValue.equals(null), "Contract violation that x.Equals(null) returns false.");
        }
    }

    /**
     * Test Equals against a new Object.
     */
    @RepeatedTest(3)
    protected void TestEqualsMethodWithNewObject()
    {
        final IndependentObject obj = new IndependentObject();

        assertFalse(this.value1.equals(obj), "Contract violation that object is not the same type");
        assertFalse(this.value2.equals(obj), "Contract violation that object is not the same type");
        assertFalse(this.value3.equals(obj), "Contract violation that object is not the same type");
        for (final T neValue: this.notEqualValues)
        {
            assertFalse(neValue.equals(obj), "Contract violation that x.Equals(null) returns false.");
        }
    }

    /**
     * Test that the object is Reflexive.
     */
    @RepeatedTest(3)
    protected void TestEqualsMethodIsReflexive()
    {
        assertEquals(this.value1, this.value1, "Contract violation that x.Equals(x) returns true.");
        assertEquals(this.value2, this.value2, "Contract violation that x.Equals(x) returns true.");
        assertEquals(this.value3, this.value3, "Contract violation that x.Equals(x) returns true.");
        for (final T neValue: this.notEqualValues)
        {
            assertEquals(neValue, neValue, "Contract violation that x.Equals(x) returns true.");
        }
    }

    /**
     * Test that the object is symmetric and transitive.
     */
    @RepeatedTest(3)
    protected void testEqualsMethodIsSymmetricAndTransitive()
    {
        assertEquals(this.value1, this.value2, "Contract violation that x, y and z are symmetric and transitive.");
        assertEquals(this.value2, this.value1, "Contract violation that x, y and z are symmetric and transitive.");

        assertEquals(this.value2, this.value3, "Contract violation that x, y and z are symmetric and transitive.");
        assertEquals(this.value3, this.value2, "Contract violation that x, y and z are symmetric and transitive.");

        assertEquals(this.value1, this.value3, "Contract violation that x and z are transitive.");
        assertEquals(this.value3, this.value2, "Contract violation that x and z are transitive.");
    }

    /**
     * Verifies that instances differing by the supplied variations are unequal.
     */
    @RepeatedTest(3)
    protected void testEqualsMethodAgainstUnequalObject()
    {
        for (final T neValue: this.notEqualValues)
        {
            assertNotEquals(this.value1, neValue);
            assertNotEquals(this.value2, neValue);
            assertNotEquals(this.value3, neValue);

            assertNotEquals(neValue, this.value1);
            assertNotEquals(neValue, this.value2);
            assertNotEquals(neValue, this.value3);
        }
    }

    /**
     * Test contract of {@link Object#hashCode()} method.
     */
    @Test
    protected void testHashCode()
    {
        assertEquals(this.value1.hashCode(), this.value2.hashCode());
        assertEquals(this.value3.hashCode(), this.value2.hashCode());
        assertEquals(this.value1.hashCode(), this.value3.hashCode());
    }

    /**
     * Test successive invocations of x.hashCode return the same value as long
     * as the objects referenced by x are not modified.
     */
    @Test
    protected void testGetHashCodeConsistentAcrossInvocations()
    {
        final int hashCode1 = this.value1.hashCode();
        final int hashCode2 = this.value2.hashCode();
        final int hashCode3 = this.value3.hashCode();
        final int hashCode4 = this.notEqualValues[0].hashCode();

        for (int i=0; i<3; i++)
        {
            assertEquals(hashCode1, this.value1.hashCode(), "Hash code values are inconsistant across invocations");
            assertEquals(hashCode2, this.value2.hashCode(), "Hash code values are inconsistant across invocations");
            assertEquals(hashCode3, this.value3.hashCode(), "Hash code values are inconsistant across invocations");
            assertEquals(hashCode4, this.notEqualValues[0].hashCode(), "Hash code values are inconsistant across invocations");
        }
        for (final T neValue: this.notEqualValues)
        {
            final int hashCode = neValue.hashCode();
            assertEquals(hashCode, neValue.hashCode(), "Hash code values are inconsistant across invocations");
        }
    }

    private static class IndependentObject
    {

    }
}
