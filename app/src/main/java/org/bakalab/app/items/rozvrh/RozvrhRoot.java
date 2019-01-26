package org.bakalab.app.items.rozvrh;

import org.bakalab.app.items.znamky.ZnamkaPredmet;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "results", strict = false)
public class RozvrhRoot {
    public RozvrhRoot() {
        super();
    }

    @Element(required = false)
    private Rozvrh rozvrh;

    public Rozvrh getRozvrh() {
        return rozvrh;
    }
}
