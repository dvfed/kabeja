There are no up-to-date binary packages.

## Building Kabeja
Kabeja is structured as a multi-module Maven project.
The root POM only depends on the core.

```
# kabeja-core
mvn compile
mvn install

# DXF, SVG, ...
mvn -P blocks compile
mvn -P blocks install

# single submodule
mvn -P blocks -pl blocks/svg -am install
```

Note: before building a legacy lib has to be installed, see blocks/ui/install-legacy-lib.sh

## IDE like Eclipse/Netbeans:

You can import it as an existing Maven repository.

Eclipse will automatically create a project for each Maven submodule.
