package michaelbrabec.bakalab.items;

public class UkolItem {
    private String predmet, nakdy, popis, status;
    private boolean expanded;

    public UkolItem() {
    }


    public UkolItem(String predmet, String nakdy, String popis, String status) {
        this.predmet = predmet;
        this.nakdy = nakdy;
        this.popis = popis;
        this.status = status;
    }

    public String getPredmet() {
        return predmet;
    }

    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }

    public String getNakdy() {
        return nakdy;
    }

    public void setNakdy(String nakdy) {
        this.nakdy = nakdy;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
