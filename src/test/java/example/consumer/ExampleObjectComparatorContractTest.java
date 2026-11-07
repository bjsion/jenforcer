package example.consumer;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import org.jenforcer.contracts.ComparatorContractTest;

class ExampleObjectComparatorContractTest extends ComparatorContractTest<ExampleObject>
{
    @Override
    protected Comparator<? super ExampleObject> createComparator()
    {
        return Comparator.comparingInt(ExampleObject::value);
    }

    @Override
    protected ExampleObject createLessValue()
    {
        return objectWithValue(1, "Less", ExampleObject.Category.STANDARD, List.of("first"));
    }

    @Override
    protected ExampleObject createEqualValue1()
    {
        return objectWithValue(2, "Equal one", ExampleObject.Category.STANDARD, List.of("first"));
    }

    @Override
    protected ExampleObject createEqualValue2()
    {
        return objectWithValue(2, "Equal two", ExampleObject.Category.PREMIUM, List.of("second"));
    }

    @Override
    protected ExampleObject createGreaterValue()
    {
        return objectWithValue(3, "Greater", ExampleObject.Category.ARCHIVED, List.of("third"));
    }

    private ExampleObject objectWithValue(
            final int value,
            final String name,
            final ExampleObject.Category category,
            final List<String> tags)
    {
        return new ExampleObject(value, name, category, Instant.parse("2026-01-01T00:00:00Z"), tags);
    }
}
