## Download
There are no up-to-date binary packages.
Old versions can be found [here](http://kabeja.sourceforge.net).

## Building
Kabeja is structured as a multi-module Maven project.
The root POM with no profile selected builds the core and lancher.
Select `blocks` profile to build submodules.
Select `blocks` and `dist` to make a distribution with all the dependencies included.

```
# kabeja-core
mvn compile
mvn install

# DXF, SVG, ...
mvn -P blocks install

# single DXF submodule
mvn -P blocks -pl blocks/dxf -am install

# distribution (result will be placed to assembly/target)
mvn -P blocks,dist install
```

Note: A legacy library has to be installed before building. See `blocks/ui/install-legacy-lib.sh`.

### IDE like Eclipse/Netbeans/IDEA

You can import the sources as an existing Maven project.

Eclipse will automatically create a project for each Maven submodule.

## Debugging

Launch Kabeja, then start a remote debug session in your IDE attaching to `localhost:5005`:
```
#cli
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar launcher.jar -cli -in sample\dxf\draft1.dxf -pipeline svg

#gui
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 -jar launcher.jar
```
