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

### Configuring the database

- Create file into the program directory called config.cfg.
- To modify the database's url enter the line: DB_URL={new datbase's url}
  (Default database url is jdbc:oracle:thin:@aramis.inf.elte.hu:1521:eszakigrid97)
- To create the database structure in an empty database and fill with default elements enter the line: NEW_DATA_BASE=true
  (Dont worry the database will be filled only after once, when the program try to acces the database)

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

### Testing

#### From the terminal

```shell
mvn clean test
```

#### From IntelliJ IDEA

- **Select configuration:** clean test
- **Run**

## Contributors

- Attila Kovács (Ati810)
- Barnabás Ágoston (agostonbarna)
- Gergő Kovács (kovgeri01)
- Loránd Fazakas (bioneyez)
