package example.consumer;

import java.time.Instant;
import java.util.List;

import org.jenforcer.contracts.CloneableContractTest;

class ExampleObjectCloneableContractTest extends CloneableContractTest<ExampleObject>
{
    @Override
    public ExampleObject createValue()
    {
        return new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }

    @Override
    protected ExampleObject cloneObject(final ExampleObject value)
    {
        return value.clone();
    }
}
