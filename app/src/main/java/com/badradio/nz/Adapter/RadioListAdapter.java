package com.badradio.nz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badradio.nz.Models.RadioList;
import com.badradio.nz.Activity.PlayerActivity;
import com.badradio.nz.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RadioListAdapter extends RecyclerView.Adapter<RadioListAdapter.ViewHolder> {

   private List<RadioList> radioLists;
   private Context context;

   public RadioListAdapter(List<RadioList> radioLists,Context context){
       this.radioLists=radioLists;
       this.context=context;
   }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_Radio_name;
        public TextView tv_Radio_desc;
        public ImageView img_station;
        public RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_Radio_name=(TextView)itemView.findViewById(R.id.tv_station_name);
            tv_Radio_desc=(TextView)itemView.findViewById(R.id.tv_starion_desc);
            img_station= itemView.findViewById(R.id.img_station);
            layout= itemView.findViewById(R.id.rlt_layout);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(context).inflate(R.layout.radio_single, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

       final String StationName = radioLists.get(i).getName();
        final String StationDesc = radioLists.get(i).getDesc();
        final String StationImage = radioLists.get(i).getImageURL();
        final String StationLongDesc = radioLists.get(i).getLongDesc();
        final String url = radioLists.get(i).getStreamURL();

       viewHolder.tv_Radio_name.setText(StationName);
       viewHolder.tv_Radio_desc.setText(StationDesc);

        Picasso.get().load(StationImage).placeholder(R.drawable.badradio_logo).into(viewHolder.img_station);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, PlayerActivity.class);
                //Sending station details
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url",url);
                intent.putExtra("StationImage",StationImage);
                intent.putExtra("StationName",StationName);
                intent.putExtra("StationLongDesc",StationLongDesc);
                intent.putExtra("StationDesc",StationDesc);
                intent.putExtra("type","new");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return radioLists.size();
    }


}
