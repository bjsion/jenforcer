/*
 * Copyright (c) 2026 Jenforcer contributors
 */

package org.jenforcer.contracts;

import java.util.Comparator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Tests the ordering contract of a {@link Comparator}.
 *
 * <p>Implementations provide a comparator and representative values for less-than,
 * equivalent, and greater-than comparisons. Comparator equivalence does not imply
 * {@link Object#equals(Object)} equality. This contract does not prescribe null handling
 * or consistency with {@code equals}.
 *
 * @param <T> the type compared by the comparator
 */
@TestInstance(Lifecycle.PER_CLASS)
@Tag("jenforcer-code-contracts")
public abstract class ComparatorContractTest<T>
{
    private Comparator<? super T> comparator;
    private T less;
    private T equal1;
    private T equal2;
    private T greater;

    /**
     * Creates a comparator contract test.
     */
    protected ComparatorContractTest()
    {
    }

    /**
     * Creates the comparator under test.
     *
     * @return the comparator to test
     */
    protected abstract Comparator<? super T> createComparator();

    /**
     * Creates a value expected to compare less than the equal values.
     *
     * @return the less value
     */
    protected abstract T createLessValue();

    /**
     * Creates the first value in the comparator-equivalence class.
     *
     * @return the first equivalent value
     */
    protected abstract T createEqualValue1();

    /**
     * Creates a distinct value in the comparator-equivalence class.
     *
     * @return the second equivalent value
     */
    protected abstract T createEqualValue2();

    /**
     * Creates a value expected to compare greater than the equal values.
     *
     * @return the greater value
     */
    protected abstract T createGreaterValue();

    /**
     * Creates the comparator and sample values before each contract test.
     */
    @BeforeEach
    protected void setUp()
    {
        comparator = createComparator();
        less = createLessValue();
        equal1 = createEqualValue1();
        equal2 = createEqualValue2();
        greater = createGreaterValue();
        Assertions.assertNotNull(comparator, "createComparator() returned null");
        Assertions.assertNotNull(less, "createLessValue() returned null");
        Assertions.assertNotNull(equal1, "createEqualValue1() returned null");
        Assertions.assertNotNull(equal2, "createEqualValue2() returned null");
        Assertions.assertNotNull(greater, "createGreaterValue() returned null");
        Assertions.assertNotSame(equal1, equal2, "equal comparison values must be distinct instances");
    }

    /**
     * Verifies that comparison signs are reversed when the operands are swapped.
     */
    @Test
    protected void testForReverseSigns()
    {
        assertReverseSigns(less, equal1);
        assertReverseSigns(less, equal2);
        assertReverseSigns(less, greater);
        assertReverseSigns(equal1, equal2);
        assertReverseSigns(equal1, greater);
        assertReverseSigns(equal2, greater);

        assertSelfComparison(less);
        assertSelfComparison(equal1);
        assertSelfComparison(equal2);
        assertSelfComparison(greater);
    }

    /**
     * Verifies that strict comparisons are transitive.
     */
    @Test
    protected void testForTransitivity()
    {
        assertTransitive(less, equal1, greater, -1);
        assertTransitive(less, equal2, greater, -1);
        assertTransitive(greater, equal1, less, 1);
        assertTransitive(greater, equal2, less, 1);
    }

    /**
     * Verifies that comparator-equivalent values have the same ordering against every sample.
     */
    @Test
    protected void testEquivalentValuesHaveConsistentSigns()
    {
        assertEquivalentSigns(equal1, equal2, less);
        assertEquivalentSigns(equal1, equal2, equal1);
        assertEquivalentSigns(equal1, equal2, equal2);
        assertEquivalentSigns(equal1, equal2, greater);
    }

    /**
     * Verifies the expected ordering and equivalence of the supplied sample values.
     */
    @Test
    protected void testSampleValueOrdering()
    {
        assertLess(less, equal1);
        assertLess(less, equal2);
        Assertions.assertEquals(0, comparator.compare(equal1, equal2), "equal values must compare as equivalent");
        assertLess(equal1, greater);
        assertLess(equal2, greater);
        assertLess(less, greater);
    }

    private void assertLess(final T left, final T right)
    {
        Assertions.assertTrue(comparator.compare(left, right) < 0,
                "expected " + left + " to compare less than " + right);
    }

    private void assertReverseSigns(final T left, final T right)
    {
        Assertions.assertEquals(sign(comparator.compare(left, right)),
                -sign(comparator.compare(right, left)), "reverse signs for " + left + " and " + right);
    }

    private void assertSelfComparison(final T value)
    {
        Assertions.assertEquals(0, comparator.compare(value, value), "value must compare equal to itself");
    }

    private void assertTransitive(final T first, final T second, final T third, final int expectedSign)
    {
        Assertions.assertEquals(expectedSign, sign(comparator.compare(first, second)),
                "first comparison has the expected sign");
        Assertions.assertEquals(expectedSign, sign(comparator.compare(second, third)),
                "second comparison has the expected sign");
        Assertions.assertEquals(expectedSign, sign(comparator.compare(first, third)),
                "strict comparisons must be transitive");
    }

    private void assertEquivalentSigns(final T first, final T second, final T other)
    {
        Assertions.assertEquals(0, comparator.compare(first, second), "values must compare as equivalent");
        Assertions.assertEquals(sign(comparator.compare(first, other)),
                sign(comparator.compare(second, other)),
                "equivalent values must compare consistently against " + other);
    }

    private int sign(final int value)
    {
        return Integer.compare(value, 0);
    }
}
