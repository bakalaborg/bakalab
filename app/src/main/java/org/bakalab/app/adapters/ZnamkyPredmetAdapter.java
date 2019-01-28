package org.bakalab.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import org.bakalab.app.R;
import org.bakalab.app.items.znamky.ZnamkaPredmet;

public class ZnamkyPredmetAdapter extends RecyclerView.Adapter<ZnamkyPredmetAdapter.MyViewHolder> {

    public List<ZnamkaPredmet> znamkyList;

    public ZnamkyPredmetAdapter(List<ZnamkaPredmet> znamkyList) {
        this.znamkyList = znamkyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ukol_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ZnamkaPredmet predmet = znamkyList.get(position);

        if (position == znamkyList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }


        holder.znamka.setText("");
        holder.vaha.setText("");
        holder.popis.setText(predmet.getNazev());
        holder.poznamka.setText(predmet.getPrumer());
        holder.datum.setText("");
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