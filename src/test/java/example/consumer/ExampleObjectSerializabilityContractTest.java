package example.consumer;

import java.time.Instant;
import java.util.List;

import org.jenforcer.contracts.SerializabilityContractTest;

class ExampleObjectSerializabilityContractTest extends SerializabilityContractTest<ExampleObject>
{
    @Override
    protected ExampleObject createInstance()
    {
        return new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }
}
