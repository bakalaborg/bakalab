package org.bakalab.app.items.znamky;

import org.bakalab.app.utils.Utils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

@Root(strict = false)
public class Znamka {

    public Znamka() {
        super();
    }

    @Element(required = false, name = "pred")
    private String predmet;

    @Element(required = false)
    private String znamka;

    @Element(required = false)
    private String datum;

    @Element(required = false)
    private String udeleno;

    @Element(required = false)
    private String vaha;

    @Element(required = false)
    private String caption;

    @Element(required = false)
    private String poznamka;

    private boolean isExpanded;

    @Commit
    private void processZnamka() {

        //TODO Jsem pridavat easter-eggy pro jovana
        datum = Utils.parseDate(datum, "yyMMdd", "dd. MM. yyyy");

        // Pokud je nazev znamky prazdny (dela to treba telocvik)
        if (caption.trim().isEmpty()) {
            caption = predmet;
        } else {
            caption = caption.trim();
        }

        if (poznamka.isEmpty()) {
            poznamka = predmet;
        } else {
            poznamka = predmet + " — " + poznamka;
        }
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getZnamka() {
        return znamka;
    }

    public String getDatum() {
        return datum;
    }

    public String getUdeleno() {
        return udeleno;
    }

    public String getVaha() {
        return vaha;
    }

    public String getCaption() {
        return caption;
    }

    public String getPoznamka() {
        return poznamka;
    }
}
