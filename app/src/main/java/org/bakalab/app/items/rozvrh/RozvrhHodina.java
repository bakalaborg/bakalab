package org.bakalab.app.items.rozvrh;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "hod", strict = false)
public class RozvrhHodina {
    public RozvrhHodina() {
        super();
    }

    /*@Element(required = false)
    private String begintime;

    @Element(required = false)
    private String endtime;*/

    @Element(required = false)
    private String zkrpr;

    @Element(required = false)
    private String pr;

    @Element(required = false)
    private String zkruc;

    @Element(required = false)
    private String uc;

    @Element(required = false)
    private String zkrmist;

    @Element(required = false)
    private String tema;

    private boolean expanded;

    /*public String getBegintime() {
        return begintime;
    }

    public String getEndtime() {
        return endtime;
    }*/

    public String getZkrpr() {
        return zkrpr;
    } //zkratka předmětu

    public String getPr() {
        return pr;
    } //předmět

    public String getZkruc() {
        return zkruc;
    } //zkratka učitele

    public String getUc() {
        return uc;
    } //učitel

    public String getZkrmist() {
        return zkrmist;
    } //zkratka místa

    public String getTema() {
        return tema;
    } //téma

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
