package org.bakalab.app.items.akce;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "results", strict = false)
public class AkceRoot {
    public AkceRoot() {
        super();
    }

    @ElementList(required = false)
    private List<Akce> akceall;

    public List<Akce> getAkceall() {
        return akceall;
    }
}
