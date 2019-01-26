package org.bakalab.app.items.znamky;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "predmet", strict = false)
public class ZnamkaPredmet {

    public ZnamkaPredmet() {
        super();
    }

    @Element(required = false)
    private String nazev;

    @Element(required = false)
    private String prumer;

    @ElementList(required = false)
    private List<Znamka> znamky;

    public String getNazev() {
        return nazev;
    }

    public String getPrumer() {
        return prumer;
    }

    public List<Znamka> getZnamky() {
        return znamky;
    }
}
