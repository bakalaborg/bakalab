package org.bakalab.app.items.skoly;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "municipalityInfo", strict = false)
public class MestoSkola {
    @Element(required = false)
    public String name;
    @Element
    public Integer schoolCount;
}
