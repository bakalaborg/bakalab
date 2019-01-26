package org.bakalab.app.items.rozvrh;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "hod", strict = false)
public class RozvrhHodina {
    public RozvrhHodina() {
        super();
    }

    private String begintime;

    private String endtime;

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

    @Element(required = false)
    private String caption;

    private boolean expanded;

    public String getBegintime() {
        return begintime;
    }

    public void setBegintime(String begintime) {
        this.begintime = begintime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

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

    public String getCaption() {
        return caption;
    } //lesson number / číslo hodiny

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
