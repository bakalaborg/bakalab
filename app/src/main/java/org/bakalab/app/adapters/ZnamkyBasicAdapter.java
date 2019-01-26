package org.bakalab.app.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import org.bakalab.app.R;
import org.bakalab.app.items.znamky.Znamka;

public class ZnamkyBasicAdapter extends RecyclerView.Adapter<ZnamkyBasicAdapter.MyViewHolder> {

    public List<Znamka> znamkyList;

    public ZnamkyBasicAdapter(List<Znamka> znamkyList) {
        this.znamkyList = znamkyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.znamka_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Znamka znamka = znamkyList.get(position);

        if (position == znamkyList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        boolean expanded = znamka.isExpanded();
        if (expanded) {
            holder.poznamka.setMaxLines(Integer.MAX_VALUE);
            holder.poznamka.setEllipsize(null);
            holder.popis.setMaxLines(Integer.MAX_VALUE);
            holder.popis.setEllipsize(null);
        } else {
            holder.poznamka.setMaxLines(2);
            holder.poznamka.setEllipsize(TextUtils.TruncateAt.END);
            holder.popis.setMaxLines(1);
            holder.popis.setEllipsize(TextUtils.TruncateAt.END);
        }

        holder.znamka.setText(znamka.getZnamka());
        holder.vaha.setText(znamka.getVaha());
        holder.popis.setText(znamka.getCaption());
        holder.poznamka.setText(znamka.getPoznamka());
        holder.datum.setText(znamka.getDatum().substring(0, 12));
    }

    @Override
    public int getItemCount() {
        return znamkyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView znamka, popis, vaha, poznamka, datum;
        public RelativeLayout root_view;
        public View divider;

        public MyViewHolder(View view) {
            super(view);
            znamka = view.findViewById(R.id.znamka);
            popis = view.findViewById(R.id.popis);
            poznamka = view.findViewById(R.id.poznamka);
            vaha = view.findViewById(R.id.vaha);
            divider = view.findViewById(R.id.divider);
            datum = view.findViewById(R.id.datum);
            root_view = view.findViewById(R.id.root_container);

        }
    }
}