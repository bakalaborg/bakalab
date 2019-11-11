package org.bakalab.app.items.skoly;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false)
public class SkolyRoot {
    @ElementList(name = "ArrayOfmunicipalityInfo", inline = true)
    public List<MestoSkola> mesta;
}
