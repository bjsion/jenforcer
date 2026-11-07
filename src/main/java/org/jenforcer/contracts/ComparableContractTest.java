/*
 * Copyright (c) 2026 Jenforcer contributors
 */

package org.jenforcer.contracts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

/**
 * Tests the adherence to the contract for natural ordering of classes that implement the
 * {@link Comparable} interface.
 *
 * <p>An object that implements the {@link Comparable} interface must be able to pass all the
 * tests applied based on the contract definition.
 *
 * <p>Verifies the {@link Comparable#compareTo(Object)} contract, including rejecting
 * {@code null} arguments.
 *
 * <p>This class is based heavily off the ComparibilityTestCase from junit addons.
 *
 * @see <a href=http://junit-addons.sourceforge.net/>junit-addons</a>
 * @param <T> the comparable type under test
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
@Tag("jenforcer-code-contracts")
public abstract class ComparableContractTest<T extends Comparable<T>>
{

    private T less;
    private T equal1;
    private T equal2;
    private T greater;

    /**
     * Creates a comparable contract test.
     */
    protected ComparableContractTest()
    {
    }

    /**
     * Creates an instance of the class under test expected to be less than the
     * initial value as defined in {@link ComparableContractTest#createEqualInstance()}
     *
     * @return an instance of {@link Comparable} that is less
     * @see #createEqualInstance()
     * @see #createGreaterInstance()
     * @throws Exception if an instance cannot be created
     */
    protected abstract T createLessInstance() throws Exception;

    /**
     * Creates an instance of the class under test that is the basis for comparison.
     *
     * @return an instance of {@link Comparable}
     * @see #createLessInstance()
     * @see #createGreaterInstance()
     * @throws Exception if an instance cannot be created
     */
    protected abstract T createEqualInstance() throws Exception;

    /**
     * Creates an instance of the class under test expected to be greater than the
     * initial value as defined in {@link ComparableContractTest#createEqualInstance()}
     *
     * @return an instance of {@link Comparable} that is greater
     * @see #createEqualInstance()
     * @see #createLessInstance()
     * @throws Exception if an instance cannot be created
     */
    protected abstract T createGreaterInstance() throws Exception;

    /**
     * Creates test instances before each contract test.
     *
     * @throws Exception if a test instance cannot be created
     */
    @BeforeEach
    protected void setUp() throws Exception
    {
        less = this.createLessInstance();
        equal1 = this.createEqualInstance();
        equal2 = this.createEqualInstance();
        greater = this.createGreaterInstance();

        Assertions.assertNotNull(less, "createLessInstance() returned null");
        Assertions.assertNotNull(equal1, "createEqualInstance() returned null");
        Assertions.assertNotNull(equal2, "2nd createEqualInstance() returned null");
        Assertions.assertNotNull(greater, "createGreaterInstance() returned null");

        Assertions.assertEquals(less.getClass(), equal1.getClass(), "less and equal1 of different classes");
        Assertions.assertEquals(less.getClass(), equal2.getClass(), "less and equal2 of different classes");
        Assertions.assertEquals(less.getClass(), greater.getClass(), "less and greater of different classes");

        checkForEquality(equal1, equal2);
    }

    /**
     * Override as a no-op if you do not require that createEqualInstance() return
     * distinct but equivalent objects.
     *
     * @param c1 the first equal instance
     * @param c2 the second equal instance
     */
    protected void checkForEquality(final T c1, final T c2)
    {
        Assertions.assertNotSame(c1, c2, "1st equal instance same as 2nd");
        Assertions.assertEquals(c1, c2, "1st equal not equal to 2nd");
    }

    /**
     * Tests whether {@code sgn(x.compareTo(y)) == -sgn(y.compareTo(x))} for all x and y
     * given to this test.
     */
    @Test
    protected void testForReverseSigns()
    {
        Assertions.assertEquals(Sgn(less.compareTo(equal1)), -Sgn(equal1.compareTo(less)), "less vs. equal1");
        Assertions.assertEquals(Sgn(less.compareTo(equal2)), -Sgn(equal2.compareTo(less)), "less vs. equal2");
        Assertions.assertEquals(Sgn(less.compareTo(greater)), -Sgn(greater.compareTo(less)), "less vs. greater");
        Assertions.assertEquals(Sgn(equal1.compareTo(equal2)), -Sgn(equal2.compareTo(equal1)), "equal1 vs. equal2");
        Assertions.assertEquals(Sgn(equal1.compareTo(greater)), -Sgn(greater.compareTo(equal1)), "equal1 vs. greater");
        Assertions.assertEquals(Sgn(equal2.compareTo(greater)), -Sgn(greater.compareTo(equal2)), "equal2 vs. greater");
    }

    /**
     * Tests whether sgn(x.compareTo(z)) == sgn(y.compareTo(z)) for all z when
     * x.compareTo(y) == 0.
     */
    @Test
    protected void testForSameSigns()
    {
        Assertions.assertEquals(Sgn(equal1.compareTo(less)), Sgn(equal2.compareTo(less)), "equal vs. less");
        Assertions.assertEquals(Sgn(equal1.compareTo(greater)), Sgn(equal2.compareTo(greater)), "equal vs. greater");
    }

    /**
     * Tests for sensible return values from the class under test's compareTo
     * method. Doing so effectively tests the transitivity of compareTo also --
     * (x.compareTo(y)>0 {@literal &&} y.compareTo(z)>0) implies x.compareTo(z)>0.
     */
    @Test
    protected void testReturnValues()
    {
        assertCompareToLesser(equal1, less);
        assertCompareToLesser(equal2, less);
        assertCompareToGreater(less, greater);
        assertCompareToEquals(equal1, equal2);
        assertCompareToGreater(equal1, greater);
        assertCompareToGreater(equal2, greater);
    }

    /**
     * Verifies that {@code compareTo(null)} throws {@link NullPointerException}.
     */
    @Test
    protected void testNull()
    {
        Assertions.assertThrows(NullPointerException.class, () -> less.compareTo(null));
        Assertions.assertThrows(NullPointerException.class, () -> equal1.compareTo(null));
        Assertions.assertThrows(NullPointerException.class, () -> equal2.compareTo(null));
        Assertions.assertThrows(NullPointerException.class, () -> greater.compareTo(null));
    }

    private int Sgn(final int x)
    {
        return (x == 0) ? 0 : (x / Math.abs(x));
    }

    private void assertCompareToLesser(final T expected, final T actual)
    {
        Assertions.assertNotNull(expected);
        Assertions.assertNotNull(actual);
        if (expected.compareTo(actual) <= 0)
        {
            Assertions.fail("Contract violation expected lesser than:<"+expected+"> but was:<"+actual+">");
        }
    }

    private void assertCompareToGreater(final T expected, final T actual)
    {
        Assertions.assertNotNull(expected);
        Assertions.assertNotNull(actual);
        if (expected.compareTo(actual) >= 0)
        {
            Assertions.fail("Contract violation expected greater than:<"+expected+"> but was:<"+actual+">");
        }
    }

    private void assertCompareToEquals(final T expected, final T actual)
    {
        Assertions.assertNotNull(expected);
        Assertions.assertNotNull(actual);
        if (expected.compareTo(actual) != 0)
        {
            Assertions.fail("Contract violation expected <"+actual+"> to be equal to:<"+expected+">");
        }
    }
}
