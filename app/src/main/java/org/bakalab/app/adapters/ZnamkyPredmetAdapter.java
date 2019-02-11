package org.bakalab.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import org.bakalab.app.R;
import org.bakalab.app.items.znamky.Predmet;

public class ZnamkyPredmetAdapter extends RecyclerView.Adapter<ZnamkyPredmetAdapter.MyViewHolder> {

    private List<Predmet> znamkyList;

    public void setResourceString(String resourceString) {
        this.resourceString = resourceString;
    }

    private String resourceString;

    public ZnamkyPredmetAdapter(List<Predmet> znamkyList) {
        this.znamkyList = znamkyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_znamka_predmet, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Predmet predmet = znamkyList.get(position);

        if (position == znamkyList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        holder.popis.setText(predmet.getNazev());
        holder.poznamka.setText(String.format(resourceString, predmet.getPrumer(), predmet.getPocet()));
    }

    @Override
    public int getItemCount() {
        return znamkyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView popis, poznamka;
        public View divider;

        public MyViewHolder(View view) {
            super(view);
            popis = view.findViewById(R.id.popis);
            poznamka = view.findViewById(R.id.poznamka);
            divider = view.findViewById(R.id.divider);

        }
    }
}