# jenforcer - Contract Test Enforceer

Jenforcer provides reusable JUnit 6 test contracts for checking common Java
type contracts:

- `EqualsContractTest` checks `equals` and `hashCode`.
- `ComparableContractTest` checks natural ordering.
- `ComparatorContractTest` checks custom ordering.
- `CloneableContractTest` checks shallow cloning.
- `SerializabilityContractTest` checks Java serialization.

## Requirements

- Java 25 or later to compile and run tests that use Jenforcer. The library is
  compiled for Java 25.
- JUnit Jupiter 6.1.3 or later in the test runtime. The quick-start dependency
  uses 6.1.3.
- Maven 3.9 or later for Maven builds. Gradle users need a Gradle version that
  supports running on Java 25 and uses the JUnit Platform.

## Quick start

Use Java 25 or later and JUnit Jupiter 6. Add these dependencies inside the
`<dependencies>` element of your `pom.xml`. Replace `0.1.0` with the release
version you want to use:

```xml
<dependencies>
    <dependency>
        <groupId>org.jenforcer</groupId>
        <artifactId>jenforcer-contract-tests</artifactId>
        <version>0.1.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>6.1.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

For example, define the type under test in `src/main/java/example/Product.java`:

```java
package example;

import java.io.Serializable;
import java.util.Objects;

public final class Product implements Comparable<Product>, Cloneable, Serializable
{
    private static final long serialVersionUID = 1L;

    private final int rank;
    private final String name;

    public Product(final int rank, final String name)
    {
        this.rank = rank;
        this.name = Objects.requireNonNull(name);
    }

    public int rank()
    {
        return rank;
    }

    public String name()
    {
        return name;
    }

    @Override
    public int compareTo(final Product other)
    {
        return Integer.compare(rank, other.rank);
    }

    @Override
    public Product clone() throws CloneNotSupportedException
    {
        return (Product) super.clone();
    }

    @Override
    public boolean equals(final Object other)
    {
        return other instanceof Product that
                && rank == that.rank
                && name.equals(that.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(rank, name);
    }
}
```

Create a contract test under `src/test/java/example`. This example checks
`Product`'s `equals` and `hashCode` implementations:

```java
package example;

import org.jenforcer.contracts.EqualsContractTest;

class ProductEqualsContractTest extends EqualsContractTest<Product>
{
    @Override
    public Product createValue()
    {
        return new Product(2, "beta");
    }

    @Override
    protected Product[] createNotEqualValues()
    {
        return new Product[] {
            new Product(1, "alpha"),
            new Product(2, "BETA")
        };
    }
}
```

Jenforcer supplies the test methods; the subclass provides sample values that
should be equal and values that should not be equal to them.

## Test execution

Run all tests like: 
```sh
mvn test
```

or a single contract test, with:
```sh
mvn -Dtest=ProductEqualsContractTest test
```

For Gradle, add the dependencies and enable the JUnit Platform in
`build.gradle`:

```groovy
dependencies {
    testImplementation 'org.jenforcer:jenforcer-contract-tests:0.1.0'
    testImplementation 'org.junit.jupiter:junit-jupiter:6.1.3'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

Then run the full suite or an individual contract test:

```sh
./gradlew test
./gradlew test --tests example.ProductEqualsContractTest
```

If JUnit Jupiter is already configured in your project, keep its version
compatible with Jenforcer and ensure the Jupiter engine is available at test
runtime; you do not need to add the dependency a second time.

## Contract reference

| Contract | What it checks | Implement in your subclass | Notes |
|---|---|---|---|
| `EqualsContractTest<T>` | `equals` reflexivity, symmetry, transitivity, null and unrelated-object handling; unequal samples; and consistent `hashCode` values | `createValue()` and `createNotEqualValues()` | `createValue()` must return a fresh, non-null instance on each call. Supply at least one non-null unequal sample, distinct from the equal samples. |
| `ComparableContractTest<T>` | Natural ordering signs, consistency of equivalent values against other samples, ordering transitivity, and `compareTo(null)` behavior | `createLessInstance()`, `createEqualInstance()`, and `createGreaterInstance()` | The default `checkForEquality()` requires the two `createEqualInstance()` results to be distinct but equal according to `equals`; override it if that is not part of your type's contract. |
| `ComparatorContractTest<T>` | Comparator self-comparison, reverse signs, transitivity, and consistent ordering of comparator-equivalent values | `createComparator()`, `createLessValue()`, `createEqualValue1()`, `createEqualValue2()`, and `createGreaterValue()` | The two equivalent values must be distinct instances and compare as `0`, but need not be equal according to `equals`. This contract does not test null handling or consistency with `equals`. |
| `CloneableContractTest<T>` | Clones are distinct objects of the same class and have equal field values; the class exposes a public `clone()` method | `createValue()` and `cloneObject(value)` | `T` must implement `Cloneable`. Override `getExcludedFieldNames()` to ignore fields that should not be compared. |
| `SerializabilityContractTest<T>` | Serialization and deserialization produce a distinct, same-class object equal to the original; checks for a declared `serialVersionUID` field | `createInstance()` | `T` must implement `Serializable`, and the type's `equals` implementation must consider the round-tripped object equal to the original. Declare `serialVersionUID` as `private static final long` for serialization version control. |

## Contract examples

The examples below reuse `Product` from the quick start. Each code block is a
separate test source file under `src/test/java/example/`.

### Natural ordering

```java
package example;

import org.jenforcer.contracts.ComparableContractTest;

class ProductComparableContractTest extends ComparableContractTest<Product>
{
    @Override
    protected Product createLessInstance()
    {
        return new Product(1, "alpha");
    }

    @Override
    protected Product createEqualInstance()
    {
        return new Product(2, "beta");
    }

    @Override
    protected Product createGreaterInstance()
    {
        return new Product(3, "gamma");
    }
}
```

### Custom comparator

This comparator orders by name, ignoring case. The equivalent examples are
distinct Products that are not equal according to `equals`.

```java
package example;

import java.util.Comparator;

import org.jenforcer.contracts.ComparatorContractTest;

class ProductComparatorContractTest extends ComparatorContractTest<Product>
{
    @Override
    protected Comparator<? super Product> createComparator()
    {
        return Comparator.comparing(Product::name, String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    protected Product createLessValue()
    {
        return new Product(1, "alpha");
    }

    @Override
    protected Product createEqualValue1()
    {
        return new Product(2, "beta");
    }

    @Override
    protected Product createEqualValue2()
    {
        return new Product(3, "BETA");
    }

    @Override
    protected Product createGreaterValue()
    {
        return new Product(4, "gamma");
    }
}
```

### Cloning

`Product` exposes a public `clone()` method. The contract test delegates to it:

```java
package example;

import org.jenforcer.contracts.CloneableContractTest;

class ProductCloneableContractTest extends CloneableContractTest<Product>
{
    @Override
    public Product createValue()
    {
        return new Product(2, "beta");
    }

    @Override
    protected Product cloneObject(final Product value) throws CloneNotSupportedException
    {
        return value.clone();
    }
}
```

### Serialization

`Product` is serializable and implements value-based equality, allowing the
contract to check the round-tripped instance:

```java
package example;

import org.jenforcer.contracts.SerializabilityContractTest;

class ProductSerializabilityContractTest extends SerializabilityContractTest<Product>
{
    @Override
    protected Product createInstance()
    {
        return new Product(2, "beta");
    }
}
```

## Build

```sh
./mvnw verify
```

The build produces the library JAR together with source and Javadoc JARs.

## Publishing

Releases are published from version tags (`v*`) through GitHub Actions to
Maven Central and GitHub Packages. Configure these repository secrets:

- `CENTRAL_USERNAME` and `CENTRAL_PASSWORD`: Sonatype Central publishing token
- `CENTRAL_GPG_PRIVATE_KEY` and `CENTRAL_GPG_PASSPHRASE`: signing key and
  passphrase

The project also needs a verified Maven Central namespace for `org.jenforcer`.
Once the GitHub repository is created, replace `REPLACE_WITH_GITHUB_OWNER` in
the POM's SCM metadata. The publishing workflow uses GitHub's
`GITHUB_REPOSITORY` and `GITHUB_TOKEN` automatically for GitHub Packages.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
