package org.bakalab.app.items.main;

import org.bakalab.app.items.ukoly.UkolyList;
import org.bakalab.app.items.znamky.ZnamkyRoot;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(strict = false, name = "results")
public class MainScreen {

    @Element(required = false, name = "xmlznamky")
    private List<ZnamkyRoot> znamkaPredmetyLists;

    @Element(required = false, name = "xmlukoly")
    private List<UkolyList> ukolyLists;

//    @Element(required = false, name = "xmlrozvrhakt")
//    private List<Rozvrh> rozvrhList;

    //TODO Return next hour

}
