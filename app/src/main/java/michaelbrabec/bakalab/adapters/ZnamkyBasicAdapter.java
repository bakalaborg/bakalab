package michaelbrabec.bakalab.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import michaelbrabec.bakalab.items.ZnamkaItem;
import michaelbrabec.bakalab.R;

public class ZnamkyBasicAdapter extends RecyclerView.Adapter<ZnamkyBasicAdapter.MyViewHolder> {

    public List<ZnamkaItem> znamkyList;

    public ZnamkyBasicAdapter(List<ZnamkaItem> znamkyList) {
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
        final ZnamkaItem znamkyItem = znamkyList.get(position);

        if (position == znamkyList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        boolean expanded = znamkyItem.isExpanded();
        if (expanded) {
            holder.poznamka.setMaxLines(Integer.MAX_VALUE);
            holder.poznamka.setEllipsize(null);
        } else {
            holder.poznamka.setMaxLines(2);
            holder.poznamka.setEllipsize(TextUtils.TruncateAt.END);
        }

        holder.znamka.setText(znamkyItem.getZnamka());
        holder.vaha.setText(znamkyItem.getVaha());
        holder.popis.setText(znamkyItem.getPopis());
        holder.poznamka.setText(znamkyItem.getPoznamka());
        holder.datum.setText(znamkyItem.getDatum().substring(0, 12));
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