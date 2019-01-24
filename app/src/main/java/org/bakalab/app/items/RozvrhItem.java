package org.bakalab.app.items;

public class RozvrhItem {
    private String begintime, endtime, zkrpr, pr, zkruc, uc, zkrmist, tema, zkratka, datum;
    //these terrible variable names are what the system returns, I'm just keeping them for consistency
    private int itemType = 0;
    private boolean expanded;

    public RozvrhItem() {
    }


    public RozvrhItem(String begintime, String endtime, String zkrpr, String pr, String zkruc, String uc, String zkrmist, String tema) {
        this.begintime = begintime;
        this.endtime = endtime;
        this.zkrpr = zkrpr;
        this.pr = pr;
        this.zkruc = zkruc;
        this.uc = uc;
        this.zkrmist = zkrmist;
        this.tema = tema;
    }

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

    public void setZkrpr(String zkrpr) {
        this.zkrpr = zkrpr;
    }

    public String getPr() {
        return pr;
    } //předmět

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getZkruc() {
        return zkruc;
    } //zkratka učitele

    public void setZkruc(String zkruc) {
        this.zkruc = zkruc;
    }

    public String getUc() {
        return uc;
    } //učitel

    public void setUc(String uc) {
        this.uc = uc;
    }

    public String getZkrmist() {
        return zkrmist;
    } //zkratka místa

    public void setZkrmist(String zkrmist) {
        this.zkrmist = zkrmist;
    }

    public String getTema() {
        return tema;
    } //zkratka místa

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getZkratka() {
        return zkratka;
    } //zkratka dne [day separators]

    public void setZkratka(String zkratka) {
        this.zkratka = zkratka;
    }

    public String getDatum() {
        return datum;
    } //date of the day [day separators]

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public int getItemType() { return itemType; } //whether the item is a class or a day separator (more types possible in the future, that's why it's an int)

    public void setItemType(int itemType) { this.itemType = itemType; }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
