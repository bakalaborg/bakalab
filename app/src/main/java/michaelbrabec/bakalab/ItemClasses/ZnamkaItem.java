package michaelbrabec.bakalab.ItemClasses;

public class ZnamkaItem {
    private String znamka, predmet, popis, vaha, datum;

    public ZnamkaItem() {
    }

    public ZnamkaItem(String znamka, String predmet, String popis, String vaha, String datum) {
        this.znamka = znamka;
        this.predmet = predmet;
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

    public String getPredmet() {
        return predmet;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }
}
