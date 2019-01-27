package org.bakalab.app.items.rozvrh;

import org.bakalab.app.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
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

        /*
            we have to start the counting at 1 because there's a null variable at the beginning that the parser gets from <pocet> tag
            also we can't just map captions to times using a hashmap because free classes and special classes (eg. třídnická hodina)
            don't return a caption variable
         */
        int position = 1;
        for(RozvrhHodina hodina : hodiny){
            RozvrhHodinaCaption mRozvrhHodinaCaption = captionsList.get(position);
            hodina.setBegintime(mRozvrhHodinaCaption.getBegintime());
            hodina.setEndtime(mRozvrhHodinaCaption.getEndtime());
            position++;
        }
    }
}
