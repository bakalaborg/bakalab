package org.bakalab.app.items.ukoly;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Root(name = "ukoly", strict = false)
public class UkolyList {

    public UkolyList() {
        super();
    }

    @ElementList(required = false)
    private List<Ukol> ukoly;

    @Commit
    private void sortByNakdy() {
        Collections.sort(ukoly, new Comparator<Ukol>() {
            DateFormat f = new SimpleDateFormat("dd. MM. yyyy", Locale.ENGLISH);

            @Override
            public int compare(Ukol o1, Ukol o2) {
                try {
                    return f.parse(o2.getNakdy()).compareTo(f.parse(o1.getNakdy()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    public List<Ukol> getUkoly() {
        return ukoly;
    }
}
