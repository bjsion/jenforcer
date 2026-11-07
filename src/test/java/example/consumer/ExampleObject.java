package example.consumer;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

final class ExampleObject implements Comparable<ExampleObject>, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;

    private final int value;
    private final String name;
    private final Category category;
    private final Instant createdAt;
    private final List<String> tags;

    ExampleObject(
            final int value,
            final String name,
            final Category category,
            final Instant createdAt,
            final List<String> tags)
    {
        this.value = value;
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.tags = List.copyOf(tags);
    }

    int value()
    {
        return value;
    }

    @Override
    public int compareTo(final ExampleObject other)
    {
        Objects.requireNonNull(other, "other must not be null");
        return Integer.compare(value, other.value);
    }

    @Override
    public boolean equals(final Object other)
    {
        return other instanceof ExampleObject that
                && value == that.value
                && name.equals(that.name)
                && category == that.category
                && createdAt.equals(that.createdAt)
                && tags.equals(that.tags);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value, name, category, createdAt, tags);
    }

    @Override
    public ExampleObject clone()
    {
        try
        {
            return (ExampleObject) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError(e);
        }
    }

    enum Category
    {
        STANDARD,
        PREMIUM,
        ARCHIVED
    }
}
