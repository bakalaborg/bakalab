package org.bakalab.app.items.ukoly;

import org.bakalab.app.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

@Root(name = "ukol", strict = false)
public class Ukol {

    public Ukol() {
        super();
    }

    @Element(required = false)
    private String predmet;

    @Element(required = false)
    private String zadano;

    @Element(required = false)
    private String nakdy;

    @Element(required = false)
    private String popis;

    @Element(required = false)
    private String status;

    private boolean isExpanded;

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getPredmet() {
        return predmet;
    }

    public String getZadano() {
        return zadano;
    }

    public String getNakdy() {
        return nakdy;
    }

    public String getPopis() {
        return popis;
    }

    public String getStatus() {
        return status;
    }

    @Commit
    private void proccessUkol() {
        popis = popis.replace("<br />", "\n");
        nakdy = Utils.parseDate(nakdy, "yyMMddHHmm", "dd. MM. yyyy");

    }
}
