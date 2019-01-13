package michaelbrabec.bakalab.items;

public class ZnamkaItem {
    private String znamka, popis, vaha, datum, poznamka;
    private boolean expanded;

    public ZnamkaItem() {
    }


    public ZnamkaItem(String znamka, String popis, String vaha, String datum, String poznamka) {
        this.znamka = znamka;
        this.popis = popis;
        this.vaha = vaha;
        this.datum = datum;
    }

    public String getZnamka() {
        return znamka;
    }

    public void setZnamka(String znamka) {
        this.znamka = znamka;
    }

    public String getVaha() {
        return vaha;
    }

    public void setVaha(String vaha) {
        this.vaha = vaha;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public String getPoznamka() {
        return poznamka;
    }

    public void setPoznamka(String poznamka) {
        this.poznamka = poznamka;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
