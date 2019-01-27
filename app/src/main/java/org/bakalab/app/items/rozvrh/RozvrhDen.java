package org.bakalab.app.items.rozvrh;

import org.bakalab.app.utils.Utils;
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

    public String getDay() { return Utils.parseDate(datum, "yyyyMMdd", "d"); }

    public List<RozvrhHodina> getHodiny() { return hodiny; }

    public void fixTimes(List<RozvrhHodinaCaption> captionsList) {
        if(hodiny == null)
            return;

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
