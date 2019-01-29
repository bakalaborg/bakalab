package org.bakalab.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bakalab.app.R;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RozvrhBasicAdapter extends RecyclerView.Adapter<RozvrhBasicAdapter.MyViewHolder> {

    public List<Object> rozvrhList;

    public RozvrhBasicAdapter(List<Object> rozvrhList) {
        this.rozvrhList = rozvrhList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rozvrh, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position == rozvrhList.size() + 1) {
            holder.divider.setVisibility(View.GONE);
        }

        if(position % 2 == 1){
            holder.rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_a);
        }else{
            holder.rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_b);
        }

        if(getItemViewType(position) == 0){ //normal lesson
            try{
                final RozvrhHodina rozvrhItem = (RozvrhHodina)rozvrhList.get(position);
                holder.begintime.setText(rozvrhItem.getBegintime());
                holder.endtime.setText(rozvrhItem.getEndtime());

                if(rozvrhItem.getTyp().equals("A")){
                    //is třídnická
                    holder.pr.setText(rozvrhItem.getNazev());
                    holder.zkruc.setText("");
                    holder.zkrmist.setText("");
                    holder.tema.setText("");
                    holder.datum.setVisibility(View.INVISIBLE);
                }else{
                    //is regular (type H) or free (type X)
                    holder.pr.setText(rozvrhItem.getPr());
                    holder.zkruc.setText(rozvrhItem.getZkruc());
                    holder.zkrmist.setText(rozvrhItem.getZkrmist());
                    holder.tema.setText(rozvrhItem.getTema());
                    holder.datum.setVisibility(View.INVISIBLE);
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }else if(getItemViewType(position) == 1){ //day separator
            final RozvrhDen rozvrhItem = (RozvrhDen)rozvrhList.get(position);
            holder.datum.setVisibility(View.VISIBLE);
            holder.begintime.setText(rozvrhItem.getZkratka());
            holder.datum.setText(rozvrhItem.getDay());
        }

    }

    @Override
    public int getItemCount() {
        return rozvrhList.size();
    }

    @Override
    public int getItemViewType(final int position) {
        if(rozvrhList.get(position) instanceof RozvrhDen)
            return 1;

        return 0;
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