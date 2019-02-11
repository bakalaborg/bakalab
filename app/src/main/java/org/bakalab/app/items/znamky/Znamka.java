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

    @Element(required = false)
    private String pred;

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

    private boolean expanded;

    @Commit
    private void processZnamka() {

        datum = Utils.parseDate(datum, "yyMMdd", "dd. MM. yyyy");

        // Pokud je nazev znamky prazdny (dela to treba telocvik)
        if (caption == null || caption.trim().isEmpty()) {
            caption = pred;
        } else {
            caption = caption.trim();
        }

        if (poznamka == null || poznamka.isEmpty()) {
            poznamka = pred;
        } else {
            poznamka = pred + " — " + poznamka;
        }
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

    public String getPred() {
        return pred;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
