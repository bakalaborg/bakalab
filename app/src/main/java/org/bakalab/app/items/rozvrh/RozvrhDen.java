package org.bakalab.app.items.rozvrh;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.List;

@Root(name = "den", strict = false)
public class RozvrhDen {

    public RozvrhDen() {
        super();
    }

    @Element(required = false)
    private String zkratka;

    @Element(required = false)
    private String datum;

    @ElementList(required = false)
    private List<RozvrhHodina> hodiny;

    public String getZkratka() {
        return zkratka;
    }

    public String getDatum() {
        return datum;
    }

    public List<RozvrhHodina> getHodiny() { return hodiny; }

    public void fixTimes(List<RozvrhHodinaCaption> captionsList) {
        HashMap<String, RozvrhHodinaCaption> captionsDictionary = new HashMap<>();
        for(RozvrhHodinaCaption caption : captionsList)
            captionsDictionary.put(caption.getCaption(), caption);

        for(RozvrhHodina hodina : hodiny){
            RozvrhHodinaCaption mRozvrhHodinaCaption = captionsDictionary.get(hodina.getCaption());
            hodina.setBegintime(mRozvrhHodinaCaption.getBegintime());
            hodina.setEndtime(mRozvrhHodinaCaption.getEndtime());
        }
    }
}
