package michaelbrabec.bakalab.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import michaelbrabec.bakalab.R;
import michaelbrabec.bakalab.items.RozvrhItem;

public class RozvrhBasicAdapter extends RecyclerView.Adapter<RozvrhBasicAdapter.MyViewHolder> {

    public List<RozvrhItem> rozvrhList;

    public RozvrhBasicAdapter(List<RozvrhItem> rozvrhList) {
        this.rozvrhList = rozvrhList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rozvrh_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final RozvrhItem rozvrhItem = rozvrhList.get(position);

        if (position == rozvrhList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        if(position % 2 == 1){
            holder.rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_a);
        }else{
            holder.rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_b);
        }

        /*boolean expanded = rozvrhItem.isExpanded();
        if (expanded) {
            holder.porozvrh.setMaxLines(Integer.MAX_VALUE);
            holder.porozvrh.setEllipsize(null);
            holder.popis.setMaxLines(Integer.MAX_VALUE);
            holder.popis.setEllipsize(null);
        } else {
            holder.porozvrh.setMaxLines(2);
            holder.porozvrh.setEllipsize(TextUtils.TruncateAt.END);
            holder.popis.setMaxLines(1);
            holder.popis.setEllipsize(TextUtils.TruncateAt.END);
        }*/

        if(rozvrhItem.getItemType() == 0){
            holder.begintime.setText(rozvrhItem.getBegintime());
            holder.endtime.setText(rozvrhItem.getEndtime());
            holder.pr.setText(rozvrhItem.getPr());
            holder.zkruc.setText(rozvrhItem.getZkruc());
            holder.zkrmist.setText(rozvrhItem.getZkrmist());
            holder.tema.setText(rozvrhItem.getTema());
            holder.datum.setVisibility(View.INVISIBLE);
        }else if(rozvrhItem.getItemType() == 1){
            holder.datum.setVisibility(View.VISIBLE);
            holder.begintime.setText(rozvrhItem.getZkratka());
            holder.datum.setText(rozvrhItem.getDatum());
            holder.endtime.setText("");
            holder.pr.setText("");
            holder.zkruc.setText("");
            holder.zkrmist.setText("");
            holder.tema.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return rozvrhList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView begintime, endtime, pr, zkruc, zkrmist, tema, datum;
        public ImageView rozvrhBackground;
        public RelativeLayout root_view;
        public View divider;

        public MyViewHolder(View view) {
            super(view);
            begintime = view.findViewById(R.id.begintime);
            endtime = view.findViewById(R.id.endtime);
            pr = view.findViewById(R.id.pr);
            zkruc = view.findViewById(R.id.zkruc);
            zkrmist = view.findViewById(R.id.zkrmist);
            tema = view.findViewById(R.id.tema);
            datum = view.findViewById(R.id.datum);
            divider = view.findViewById(R.id.divider);
            root_view = view.findViewById(R.id.root_container);
            rozvrhBackground = view.findViewById(R.id.rozvrhBackground);

        }
    }
}