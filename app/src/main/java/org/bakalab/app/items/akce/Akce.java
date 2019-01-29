package org.bakalab.app.items.akce;

import org.bakalab.app.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "hod", strict = false)
public class Akce {

    public Akce() {
        super();
    }

    @Element(required = false)
    private String nazev;

    @Element(required = false)
    private String datum;

    @Element(required = false)
    private String cas;

    @Element(required = false)
    private String popis;

    @Element(required = false)
    private int zobrazit;

    @Element(required = false)
    private String proucitele;

    @Element(required = false)
    private String protridy;

    @Element(required = false)
    private String promistnosti;

    public String getNazev() {
        return nazev;
    }

    public String getParsedDate() {
        String date = Utils.parseDate(getDatum(), "yyyyMMdd", "dd. MM. yyyy");

        if(getCas() != null)
            return date + ", " + getCas();
        else
            return date;
    }

    public String getDatum() {
        return datum;
    }

    public String getCas() {
        return cas;
    }

    public String getPopis() {
        return popis;
    }

    public int getZobrazit() {
        return zobrazit;
    }

    public String getProucitele() {
        return proucitele;
    }

    public String getProtridy() {
        return protridy;
    }

    public String getPromistnosti() {
        return promistnosti;
    }
}
