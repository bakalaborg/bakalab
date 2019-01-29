package org.bakalab.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bakalab.app.R;
import org.bakalab.app.items.akce.Akce;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class AkceBasicAdapter extends RecyclerView.Adapter<AkceBasicAdapter.MyViewHolder> {

    public List<Akce> akceList;

    public AkceBasicAdapter(List<Akce> akceList) {
        this.akceList = akceList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_akce, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Akce akceItem = akceList.get(position);
        holder.datum.setText(akceItem.getParsedDate());
        holder.proucitele.setText(akceItem.getProucitele());
        holder.protridy.setText(akceItem.getProtridy());
        holder.nazev.setText(akceItem.getNazev());

        if (position == akceList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return akceList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView datum, proucitele, protridy, nazev;
        public RelativeLayout root_view;
        public View divider;

        public MyViewHolder(View view) {
            super(view);
            datum = view.findViewById(R.id.datum);
            proucitele = view.findViewById(R.id.proucitele);
            protridy = view.findViewById(R.id.protridy);
            nazev = view.findViewById(R.id.nazev);
            divider = view.findViewById(R.id.divider);
            root_view = view.findViewById(R.id.root_container);

        }
    }
}