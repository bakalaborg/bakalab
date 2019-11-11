package org.bakalab.app.items.skoly;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "schoolInfo", strict = false)
public class Skola {
    @Element
    public String id;
    @Element
    public String name;
    @Element
    public String schoolUrl;
}
