package org.bakalab.app.items.znamky;

import android.util.Log;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import java.util.List;
import java.util.Locale;

@Root(strict = false)
public class Predmet {

    public Predmet() {
        super();
    }

    @Element(required = false)
    private String nazev;

    @Element(required = false)
    private String zkratka;

    @Element(required = false)
    private String prumer;

    @ElementList(required = false)
    private List<Znamka> znamky;

    private String pocet;

    @Commit
    private void onCommit() {

        pocet = String.valueOf(znamky.size());

        if (prumer == null  || prumer.isEmpty()) {
            // Mesto ma sice zakazane zobrazovani prumeru ale mi ho vypocitame
            double citatel = 0, jmenovatel = 0;
            for(Znamka znamka : znamky) {
                String formattedZnamka = znamka.getZnamka();
                int vaha;
                double zn;

                if(znamka.getZnamka().trim().endsWith("-")) {
                    formattedZnamka = znamka.getZnamka().substring(0, 1).concat(".5");
                    Log.e(znamka.getPred(), formattedZnamka);
                }

                try {
                    zn = Double.parseDouble(formattedZnamka);
                    vaha = Integer.parseInt(znamka.getVaha());
                } catch (NumberFormatException e) {
                    continue;
                }

                citatel += vaha*zn;
                jmenovatel += vaha;
            }

            if (jmenovatel != 0) {
                prumer = String.format(Locale.ENGLISH, "%.2f", citatel/jmenovatel);
            } else {
                prumer = null;
            }

        }
    }

    public String getNazev() {
        return nazev;
    }

    public String getZkratka() {
        return zkratka;
    }

    public String getPrumer() {
        if (prumer == null)
            return "-";
        return prumer;
    }

    public List<Znamka> getZnamky() {
        return znamky;
    }

    public String getPocet() {
        return pocet;
    }
}
