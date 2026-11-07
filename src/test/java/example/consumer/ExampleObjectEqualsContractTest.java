package example.consumer;

import java.time.Instant;
import java.util.List;

import org.jenforcer.contracts.EqualsContractTest;

class ExampleObjectEqualsContractTest extends EqualsContractTest<ExampleObject>
{
    @Override
    public ExampleObject createValue()
    {
        return new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local"));
    }

    @Override
    protected ExampleObject[] createNotEqualValues()
    {
        return new ExampleObject[] {
            new ExampleObject(2, "Sample", ExampleObject.Category.STANDARD,
                    Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local")),
            new ExampleObject(1, "Different", ExampleObject.Category.STANDARD,
                    Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local")),
            new ExampleObject(1, "Sample", ExampleObject.Category.PREMIUM,
                    Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "local")),
            new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                    Instant.parse("2026-01-02T00:00:00Z"), List.of("featured", "local")),
            new ExampleObject(1, "Sample", ExampleObject.Category.STANDARD,
                    Instant.parse("2026-01-01T00:00:00Z"), List.of("featured", "remote"))
        };
    }
}
