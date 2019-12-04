package org.bakalab.app.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bakalab.app.R;
import org.bakalab.app.items.rozvrh.RozvrhDen;
import org.bakalab.app.items.rozvrh.RozvrhHodina;

import java.util.List;

public abstract class RozvrhAdapter extends Adapter {

    private List<Object> dataSet;

    protected RozvrhAdapter(List<Object> dataSet) {
        super(R.layout.item_rozvrh, dataSet);
        this.dataSet = dataSet;
    }

    @Override
    public void onEveryItem(Object item, int position, View holder) {

        TextView begintime, endtime, pr, zkruc, zkrmist, tema, datum;
        ImageView rozvrhBackground;
        RelativeLayout root_view;
        View divider;

        begintime = holder.findViewById(R.id.begintime);
        endtime = holder.findViewById(R.id.endtime);
        pr = holder.findViewById(R.id.pr);
        zkruc = holder.findViewById(R.id.zkruc);
        zkrmist = holder.findViewById(R.id.zkrmist);
        tema = holder.findViewById(R.id.tema);
        datum = holder.findViewById(R.id.datum);
        divider = holder.findViewById(R.id.divider);
        root_view = holder.findViewById(R.id.root_container);
        rozvrhBackground = holder.findViewById(R.id.rozvrhBackground);

        if (position == dataSet.size() + 1) {
            divider.setVisibility(View.GONE);
        }

        if(position % 2 == 1){
            rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_a);
        }else{
            rozvrhBackground.setImageResource(R.drawable.gradient_rozvrh_b);
        }

        if(getItemViewType(position) == 0){ //normal lesson
            try{
                final RozvrhHodina rozvrhItem = (RozvrhHodina)dataSet.get(position);
                begintime.setText(rozvrhItem.getBegintime());
                endtime.setText(rozvrhItem.getEndtime());

                if(rozvrhItem.getTyp().equals("A")){
                    //is třídnická
                    pr.setText(rozvrhItem.getNazev());
                    zkruc.setText("");
                    zkrmist.setText("");
                    tema.setText("");
                    datum.setVisibility(View.INVISIBLE);
                }else{
                    //is regular (type H) or free (type X)
                    pr.setText(rozvrhItem.getPr());
                    zkruc.setText(rozvrhItem.getZkruc());
                    zkrmist.setText(rozvrhItem.getZkrmist());
                    tema.setText(rozvrhItem.getTema());
                    datum.setVisibility(View.INVISIBLE);
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }else if(getItemViewType(position) == 1){ //day separator
            final RozvrhDen rozvrhItem = (RozvrhDen)dataSet.get(position);
            datum.setVisibility(View.VISIBLE);
            begintime.setText(rozvrhItem.getZkratka());
            datum.setText(rozvrhItem.getDay());
        }

    }

    @Override
    public int getItemViewType(final int position) {
        if(dataSet.get(position) instanceof RozvrhDen)
            return 1;

        return 0;
    }
}
