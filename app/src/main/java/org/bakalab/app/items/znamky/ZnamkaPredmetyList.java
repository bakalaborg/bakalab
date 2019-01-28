package org.bakalab.app.items.znamky;

import android.util.Log;

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

@Root(name = "predmety", strict = false)
public class ZnamkaPredmetyList {

    public ZnamkaPredmetyList() {
        super();
    }

    @ElementList(required = false)
    private List<ZnamkaPredmet> predmety;

    public List<ZnamkaPredmet> getPredmety() {
        return predmety;
    }

    private List<Znamka> sortedZnamky = new ArrayList<>();

    public List<Znamka> getSortedZnamky() {
        return sortedZnamky;
    }

    @Commit
    public void sortZnamky() {

        for (ZnamkaPredmet znamkaPredmet : predmety) {
            sortedZnamky.addAll(znamkaPredmet.getZnamky());
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
