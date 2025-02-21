package com.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.activities.ActivityProductList
import com.myapplication.models.ModelStore
import kotlinx.android.synthetic.main.item_store_list.view.*

class AdapterStoresList (
    val context : Context,
    val itemStoresList : ArrayList<ModelStore>
        ) : RecyclerView.Adapter<AdapterStoresList.StoresListViewHolder>() {
            inner class StoresListViewHolder(view : View) : RecyclerView.ViewHolder(view) {
                val clItemStoreList : ConstraintLayout = view.clItemStoreList
                val tvStoresList = view.tvStoreList
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_store_list, parent, false)
        return StoresListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemStoresList.size
    }

    override fun onBindViewHolder(holder: StoresListViewHolder, i: Int) {
        val itemStoreList = itemStoresList[i]

        holder.tvStoresList.text = itemStoreList.storeName

        //Pass Info to ActivityStoreList
        holder.tvStoresList.setOnClickListener {
            Intent(context, ActivityProductList :: class.java).also {
                it.putExtra("STORE_ID", itemStoreList.idStore)
                startActivity(context,it,null)
            }
        }

        //Background color
        if(i%2 == 0) {
            holder.clItemStoreList.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
        }else{
            holder.clItemStoreList.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }


}