# Project Tools - Library Project

## Development Setup

### Requirements

- Git
- Java Development Kit (JDK) 8
- JAVA_HOME is set to the location of the JDK 8
- Apache Maven
- Optional: IntelliJ Idea

### Cloning

```shell
git clone git@github.com:Ati810/ProjectToolsGroup.git && cd ProjectToolsGroup
```

### Development in IntelliJ IDEA

- Open > *Select the project location*
- Reimport All Maven Projects:
  - if the "Maven projects need to be imported" popup appears then choose **Import Changes**,
  - else: Help > Find Action > **Reimport All Maven Projects** (or click the refresh button in the Maven Projects pane)

### Building

#### From the terminal

```shell
mvn clean install
```

#### From IntelliJ IDEA

- **Select configuration:** clean install
- **Run**

### Running

#### From the terminal

```shell
mvn exec:java
```

#### From IntelliJ IDEA

- **Select configuration:** exec:java
- **Run**

### Building & Running

#### From the terminal

```shell
mvn clean install exec:java
```

#### From IntelliJ IDEA

- **Select configuration:** clean install exec:java
- **Run**

## Contributors

- Attila Kovács (Ati810)
- Barnabás Ágoston (agostonbarna)
- Gergő Kovács (kovgeri01)
- Loránd Fazakas (bioneyez)
