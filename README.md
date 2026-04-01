# Hermes
Hermes is a Java configuration library that provides a simple way to load configuration from multiple sources and formats.

## Features
- **Multiple formats:** Read configuration from YAML, TOML, JSON and Java properties
- **Multiple sources:** Combine configuration from files, environment variables and more.
- **Type safety:** Map configuration directly into records, POJOs or interfaces. 
- **Error accumulation:** All errors in one place.
- **Rich type support:** Built-in serializers/deserializers for common Java types.

## Getting started
### Requirements
Java 21+

### Installation
Maven:
```xml
<repository>
    <id>lacrid-releases</id>
    <url>https://maven.lacrid.dev/releases</url>
</repository>

<dependency>
  <groupId>dev.lacrid</groupId>
  <artifactId>hermes-core</artifactId>
  <version>0.1.1</version>
</dependency>
```

Gradle:
```groovy
maven("https://maven.lacrid.dev/releases")

implementation("dev.lacrid:hermes-core:0.1.1")
```

### Loading simple configuration
**`config.properties`**
```properties 
hostname=localhost
port=8080
```

```java
record SampleConfig(
    String hostname,
    int port
) {
}

HermesLoader hermes = HermesLoader.create(b -> b
    .source(FileSource.of("config.properties"))
);

SampleConfig config = hermes.load(SampleConfig.class);
```

### Binding complex configuration
**`config.yaml`**
```yaml 
database:
  hostname: localhost
  port: 6379
environment: prod
custom-region: eu-west-1
```

```java
@Config
interface ComplexConfig {
  Database database(); // multiple nested configs are supported
  
  String environment() {
    return "dev"; // default value
  }
  
  @Path("custom-region") // custom path
  String region();
}

@Config
class Database {
  private String hostname;
  private int port;
  
  public Database() {}
}

...

HermesLoader hermes = HermesLoader.create(builder -> builder
    .apply(new SnakeYamlModule()) // add yaml module
    .setNamingStrategy(NamingStrategies.KEBAB_CASE)
    .source(FileSource.of("config.yaml"))
);

ComplexConfig config = hermes.bind(ComplexConfig.class); // will be updated on config changes

```

## Supported formats
Add the module to your dependencies:
```xml
<dependency>
    <groupId>dev.lacrid</groupId>
    <!-- replace 'module' with the actual module name -->
    <artifactId>hermes-module</artifactId>
    <version>0.1.1</version>
</dependency>
```

Register the module while creating ```HermesLoader```:
```java
HermesLoader hermes = HermesLoader.create(builder -> builder
    .apply(new SnakeYamlModule())
);
```

| Format          | Module         | Extensions  |
|-----------------|----------------|-------------|
| YAML            | yaml-snakeyaml | .yaml, .yml |
| TOML            | toml-jackson   | .toml       |
| JSON            | json-gson      | .json       |
| Java Properties | built-in       | .properties |

TODO
