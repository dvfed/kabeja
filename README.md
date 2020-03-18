# Kabeja

Kabeja is a library and tool for parsing DXF files and conversion to SVG/PNG/JPEG/PDF.

It is licensed under the Apache Software License 2.0.

The original project and documentation can be found [here](http://kabeja.sourceforge.net).

## Current state
The original Kabeja project has not been updated since 2010. Third parties have since worked on the library. Most importantly the build system was changed from Ant to Maven. Unfortunately this left parts of the program in a partially broken state. The library part itself works but _the GUI is broken and the CLI works with some restrictions_.

This fork is intended to make Kabeja run on Java 11. The sources should be Java 8 compatible though.

## Limitations
Not all DXF entities are supported yet. Text entities are still problematic.

### Supported entities
* Arc
* Attrib
* Polyline
* Circle
* Line
* Blocks/Insert
* Text
* MText
* LWPolyline
* Solid
* Trace
* Ellipse
* Dimension
* Image
* Leader
* XLine
* Ray
* Hatch
* Spline
* MLine
* XData (The *accu:rate* fork adds limited XData support)

## CLI
In the Kabeja-folder try:
* Help and pipeline list:

  `java -jar launcher.jar -cli --help`

* Convert to svg

  `java -jar launcher.jar -cli -pipeline svg -in samples/dxf/draft1.dxf -o result.svg`

* Convert to pdf|jpeg|png|...

  `java -jar launcher.jar -cli -pipeline <pdf|jpeg|png> -in samples/dxf/draft1.dxf`

If you experience OOM problems, try `java -Xmx256m`

## GUI
The GUI is currently broken.

## DXF
DXF module can be used as a standalone library. It depends on the core only.
* Build and install Kabeja into local maven repository. See INSTALL.md for instructions.

* Add `tfly-kabeja-dxf` dependency to your project.
```
<dependency>
    <groupId>org.kabeja</groupId>
    <artifactId>tfly-kabeja-dxf</artifactId>
    <version>0.5.2</version>
</dependency>
```

* Add some code.
```
public class DXFExtractor {

    public static void main(String[] args) {
        File file = new File("file1.dxf");

        DXFExtractor extractor = new DXFExtractor();

        try (InputStream in = new FileInputStream(file)) {
            extractor.read(in, "Layer1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(InputStream in, String layerid) {
        DXFParser parser = (DXFParser) DXFParserBuilder.createDefaultParser();
        try {
            DraftDocument doc = parser.parse(in, new HashMap<>());

            Layer layer = doc.getLayer(layerid);

            // get all polylines from the layer
            List<Polyline> plines = layer.getEntitiesByType(Type.TYPE_POLYLINE);

            // work with the first polyline
            doSomething(plines.get(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void doSomething(Polyline pline) {
        // iterate over all vertex of the polyline
        for (int i = 0; i < pline.getVertexCount(); i++) {
            Vertex vertex = pline.getVertex(i);

            // do something
            System.out.printf("x = %f, y = %f%n", vertex.getPoint().getX(), vertex.getPoint().getY());
        }
    }
}
```

## Cocoon, Inkscape
These modules considered outdated and are not supported in this fork.
