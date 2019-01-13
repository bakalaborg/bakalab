package michaelbrabec.bakalab.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import michaelbrabec.bakalab.items.UkolItem;
import michaelbrabec.bakalab.R;

public class UkolyBasicAdapter extends RecyclerView.Adapter<UkolyBasicAdapter.MyViewHolder> {

    public List<UkolItem> ukolyList;

    public UkolyBasicAdapter(List<UkolItem> ukolyList) {
        this.ukolyList = ukolyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ukol_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final UkolItem ukolyItem = ukolyList.get(position);

        if (position == ukolyList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        boolean expanded = ukolyItem.isExpanded();
        if (expanded) {
            holder.popis.setMaxLines(Integer.MAX_VALUE);
            holder.popis.setEllipsize(null);
        } else {
            holder.popis.setMaxLines(2);
            holder.popis.setEllipsize(TextUtils.TruncateAt.END);
        }

        holder.predmet.setText(ukolyItem.getPredmet());
        holder.nakdy.setText(ukolyItem.getNakdy().substring(0, 12));
        holder.popis.setText(ukolyItem.getPopis());

        switch(ukolyItem.getStatus()){
            case "probehlo":
                holder.symbol.setImageResource(R.drawable.ic_done_all);
                break;
            case "aktivni":
                holder.symbol.setImageResource(R.drawable.ic_drag_handle);
                ImageViewCompat.setImageTintList(holder.symbol, ColorStateList.valueOf(Color.parseColor("#d32f2f")));
                break;
            case "pozde":
                holder.symbol.setImageResource(R.drawable.ic_done);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return ukolyList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView predmet, nakdy, popis;
        public ImageView symbol;
        public RelativeLayout root_view;
        public View divider;

        public MyViewHolder(View view) {
            super(view);
            predmet = view.findViewById(R.id.predmet);
            nakdy = view.findViewById(R.id.nakdy);
            popis = view.findViewById(R.id.popis);
            symbol = view.findViewById(R.id.symbol);
            divider = view.findViewById(R.id.divider);
            root_view = view.findViewById(R.id.root_container);

        }
    }
}