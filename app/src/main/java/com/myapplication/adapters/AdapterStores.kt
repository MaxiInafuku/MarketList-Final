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
import com.myapplication.activities.ActivityStore
import com.myapplication.interfaces.OnStoreClickListener
import com.myapplication.models.ModelStore
import kotlinx.android.synthetic.main.item_edit_delete.view.*

class AdapterStores(
    private val context: Context,
    private val allStores : ArrayList<ModelStore>,
    private val interfaceOnStoreClickListener: OnStoreClickListener
) : RecyclerView.Adapter<AdapterStores.StoresViewHolder>() {
    inner class StoresViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val clItems : ConstraintLayout = view.clItems
        val tvItem = view.tvItem
        val ivEdit = view.ivEdit
        val ivDelte = view.ivDelete
    }

    override fun onBindViewHolder(holder: StoresViewHolder, i: Int) {
        val itemStore = allStores[i]

        //Name on holder
        holder.tvItem.text = itemStore.storeName

        //Pass Store Id to ActivityStore
        holder.tvItem.setOnClickListener{
            Intent(context, ActivityStore::class.java).also {
                it.putExtra("STORE_ID", itemStore.idStore)
                startActivity(context,it,null)
            }
        }

        //Background color
        if(i%2 == 0) {
            holder.clItems.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray))
        }else{
            holder.clItems.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): StoresViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_edit_delete, parent, false)

        val holder = StoresViewHolder(view)

        holder.ivEdit.setOnClickListener {
            val position = holder.adapterPosition
            val store = allStores[position]

            interfaceOnStoreClickListener.onUpdateStore(position, store)
        }

        holder.ivDelte.setOnClickListener {
            val position = holder.adapterPosition
            val store = allStores[position]

            interfaceOnStoreClickListener.onDeleteStore(store)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return allStores.size
    }


    //---------------------------------------------------------
    //-------------------RV CUD METHODS------------------------
    //---------------------------------------------------------
    //THIS METHODS DO NOT CUD ON THE DATABASE, THEY SOLE PURPOSE IS TO MODIFY THE LIST OF PRODUCTS IN THE RECYCLERVIEW
    fun addStore(store : ModelStore){
        allStores.add(store)
        notifyItemInserted(allStores.size)
    }

    fun updateStore(newStore : ModelStore){
        for (oldStore in allStores){
            if (oldStore.idStore == newStore.idStore) {
                val position = allStores.indexOf(oldStore)
                allStores[position] = newStore

                notifyItemChanged(position)
                break
            }
        }
    }

    fun removeStore(removedStore : ModelStore){
        for (store in allStores){
            if (store.idStore == removedStore.idStore){
                val position = allStores.indexOf(store)
                allStores.remove(store)

                notifyItemRemoved(position)
                break
            }
        }
    }


}


































