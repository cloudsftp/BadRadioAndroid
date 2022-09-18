package com.badradio.nz.adapter

import android.content.Context
import com.badradio.nz.models.RadioList
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.RelativeLayout
import com.badradio.nz.R
import android.view.ViewGroup
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.badradio.nz.activity.PlayerActivity

class RadioListAdapter(private val radioLists: List<RadioList>, private val context: Context) : RecyclerView.Adapter<RadioListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_Radio_name: TextView
        var tv_Radio_desc: TextView
        var img_station: ImageView
        var layout: RelativeLayout // TODO: Use Binding

        init {
            tv_Radio_name = itemView.findViewById<View>(R.id.tv_station_name) as TextView
            tv_Radio_desc = itemView.findViewById<View>(R.id.tv_starion_desc) as TextView
            img_station = itemView.findViewById(R.id.img_station)
            layout = itemView.findViewById(R.id.rlt_layout)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(context).inflate(R.layout.radio_single, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val StationName = radioLists[i].name
        val StationDesc = radioLists[i].desc
        val StationImage = radioLists[i].imageURL
        val StationLongDesc = radioLists[i].longDesc
        val url = radioLists[i].streamURL
        viewHolder.tv_Radio_name.text = StationName
        viewHolder.tv_Radio_desc.text = StationDesc
        Picasso.get().load(StationImage).placeholder(R.drawable.badradio_logo).into(viewHolder.img_station)
        viewHolder.layout.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            //Sending station details
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("url", url)
            intent.putExtra("StationImage", StationImage)
            intent.putExtra("StationName", StationName)
            intent.putExtra("StationLongDesc", StationLongDesc)
            intent.putExtra("StationDesc", StationDesc)
            intent.putExtra("type", "new")
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return radioLists.size
    }
}