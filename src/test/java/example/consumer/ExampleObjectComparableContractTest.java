package example.consumer;

import java.time.Instant;
import java.util.List;

import org.jenforcer.contracts.ComparableContractTest;

class ExampleObjectComparableContractTest extends ComparableContractTest<ExampleObject>
{
    @Override
    protected ExampleObject createLessInstance()
    {
        return new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }

    @Override
    protected ExampleObject createEqualInstance()
    {
        return new ExampleObject(2, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }

    @Override
    protected ExampleObject createGreaterInstance()
    {
        return new ExampleObject(3, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }
}
