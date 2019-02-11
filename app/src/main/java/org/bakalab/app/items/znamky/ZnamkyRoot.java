package org.bakalab.app.items.znamky;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Root(strict = false)
public class ZnamkyRoot {

    public ZnamkyRoot() {
        super();
    }

    @ElementList(required = false, name = "predmety")
    private List<Predmet> predmety;

    private List<Znamka> sortedZnamky = new ArrayList<>();

    public List<Znamka> getSortedZnamky() {
        return sortedZnamky;
    }

    public List<Predmet> getPredmety() {
        return predmety;
    }

    @Commit
    public void sortZnamky() {

            for (Predmet predmet : predmety) {
            sortedZnamky.addAll(predmet.getZnamky());
        }

        Collections.sort(sortedZnamky, new Comparator<Znamka>() {
            DateFormat f = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

            @Override
            public int compare(Znamka o1, Znamka o2) {
                try {
                    return f.parse(o2.getDatum()).compareTo(f.parse(o1.getDatum()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

}
