package org.bakalab.app.adapters;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.bakalab.app.R;
import org.bakalab.app.items.znamky.Znamka;

import java.util.List;

public abstract class ZnamkyAdapter extends Adapter {

    public List<Znamka> dataSet;

    @SuppressWarnings("unchecked")
    protected ZnamkyAdapter(List<Znamka> dataSet) {
        super(R.layout.item_znamka, (List<Object>)(List<?>)dataSet);
        this.dataSet = dataSet;
    }

    @Override
    public void onEveryItem(Object item, int position, View holder) {
        Znamka z = (Znamka)item;

        TextView znamka = holder.findViewById(R.id.znamka);
        TextView popis = holder.findViewById(R.id.popis);
        TextView poznamka = holder.findViewById(R.id.poznamka);
        TextView vaha = holder.findViewById(R.id.vaha);
        View divider = holder.findViewById(R.id.divider);
        TextView datum = holder.findViewById(R.id.datum);

        if (position == dataSet.size() + 1) {
            divider.setVisibility(View.GONE);
        }

        boolean expanded = z.isExpanded();
        if (expanded) {
            poznamka.setMaxLines(Integer.MAX_VALUE);
            poznamka.setEllipsize(null);
            popis.setMaxLines(Integer.MAX_VALUE);
            popis.setEllipsize(null);
        } else {
            poznamka.setMaxLines(2);
            poznamka.setEllipsize(TextUtils.TruncateAt.END);
            popis.setMaxLines(1);
            popis.setEllipsize(TextUtils.TruncateAt.END);
        }

        znamka.setText(z.getZnamka());
        vaha.setText(z.getVaha());
        popis.setText(z.getCaption());
        poznamka.setText(z.getPoznamka());
        datum.setText(z.getDatum().substring(0, 12));

    }
}
