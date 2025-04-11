package com.example.myhappyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myhappyplaces.R
import com.example.myhappyplaces.activities.AddHappyPlaceActivity
import com.example.myhappyplaces.activities.HappyPlaceDetailActivity
import com.example.myhappyplaces.activities.MainActivity
import com.example.myhappyplaces.database.DatabaseHandler
import com.example.myhappyplaces.models.HappyPlaceModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


open class HappyPlacesAdapter (
    private val context: Context,
    private var list: ArrayList<HappyPlaceModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener?= null

    //Inflates the item views which is designed in xml layout file
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_happy_place,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        //TODO check if it's done right
        if (holder is MyViewHolder) {
            holder.itemView.findViewById<ImageView>(R.id.iv_place_image).setImageURI(Uri.parse(model.image))
            holder.itemView.findViewById<TextView>(R.id.tvTitle).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tvDescription).text = model.description

            holder.itemView.setOnClickListener{
                if(onClickListener!=null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }


    fun removeAt(position: Int){
        val dbHandler = DatabaseHandler(context)
        val isDeleted = dbHandler.deleteHappyPlace(list[position])
        if(isDeleted>0){
            list.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity:: class.java)
        intent.putExtra(MainActivity.EXTRA_PLACES_DETAILS, list[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }


    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }


    interface OnClickListener {
        fun onClick(position: Int, model: HappyPlaceModel)
    }


    //A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}