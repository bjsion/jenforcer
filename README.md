# jenforcer - Contract Test Enforceer

Jenforcer provides reusable JUnit 6 test contracts for checking common Java
type contracts:

- `EqualsContractTest` checks `equals` and `hashCode`.
- `ComparableContractTest` checks natural ordering.
- `ComparatorContractTest` checks custom ordering.
- `CloneableContractTest` checks shallow cloning.
- `SerializabilityContractTest` checks Java serialization.

## Requirements

- Java 25 or later
- Maven 3.9 or later

## Use in a project

After publication, add the library as a test dependency:

```xml
<dependency>
    <groupId>org.jenforcer</groupId>
    <artifactId>jenforcer-contract-tests</artifactId>
    <version>0.1.0</version>
    <scope>test</scope>
</dependency>
```

Extend the contract that matches the type being tested and provide representative
instances. For example:

```java
class MoneyEqualsTest extends EqualsContractTest<Money>
{
    @Override
    public Money createValue()
    {
        return new Money("USD", 100);
    }

    @Override
    protected Money[] createNotEqualValues()
    {
        return new Money[] {
            new Money("EUR", 100),
            new Money("USD", 200)
        };
    }
}
```

Put these subclasses in the consumer project's test source tree. Run them with
JUnit Platform, for example with `mvn test`.

For `ComparableContractTest`, implement `createLessInstance`,
`createEqualInstance`, and `createGreaterInstance`. For `CloneableContractTest`,
implement `createValue` and `cloneObject`. For `SerializabilityContractTest`,
implement `createInstance`.

For `ComparatorContractTest`, implement `createComparator`,
`createLessValue`, `createEqualValue1`, `createEqualValue2`, and
`createGreaterValue`. The two equivalent values must be distinct instances but
do not have to be equal according to `equals`.

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
