package org.bakalab.app.items.skoly;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "municipality", strict = false)
public class Mesto {
    @Element (required = false)
    public String name;
    @ElementList(name = "schools")
    public List<Skola> skoly;
}
