package com.myapplication.adapters

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.DatabaseHandler
import com.myapplication.R
import com.myapplication.models.ModelProduct
import kotlinx.android.synthetic.main.item_product_list.view.*

class AdapterProductsList (
    val context: Context,
    val itemsProductsList : ArrayList<ModelProduct>
        ) : RecyclerView.Adapter<AdapterProductsList.ProductsListViewHolder>() {
            inner class ProductsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val clProductListItem : ConstraintLayout = view.clProductListItem
                val tvProductListItem = view.tvProductListItem
                val tvProductListAmount = view.tvProdutListAmount
                val cbProductListPurchased = view.cbProductListPruchased
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_list, parent, false)
        return ProductsListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsProductsList.size
    }

    override fun onBindViewHolder(holder: ProductsListViewHolder, i: Int) {
        val itemProductList = itemsProductsList[i]

        val tvProductName = holder.tvProductListItem
        val cbPurchased = holder.cbProductListPurchased

        tvProductName.text = itemProductList.productName


        cbPurchased.isChecked = itemProductList.productPurchased == 1

        holder.tvProductListAmount.text = itemProductList.productAmount

        cbPurchased.isChecked = purchasedCheck(itemProductList)
        //strikes through according to the status of "purchased" in db
        strikeThrough(tvProductName, cbPurchased.isChecked)

        //Changes the status if user touches checkbox
        cbPurchased.setOnCheckedChangeListener{ _, isChecked ->
            strikeThrough(tvProductName, isChecked)
            purchasedChangeState(itemProductList.productId, isChecked)
        }

    }

    //--------------------------------------------------------------------------------------
    //--------------------------------METHODS-----------------------------------------------
    //--------------------------------------------------------------------------------------

    //----------CB CHECKBUTTON-----------
    //-----------------------------------
    private fun purchasedCheck(product: ModelProduct) : Boolean {
        return product.productPurchased == 1
    }


    private fun strikeThrough(tvName : TextView, checked : Boolean) {
        if (checked) {
            tvName.paintFlags = tvName.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tvName.paintFlags = tvName.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }


    private fun purchasedChangeState(productId : Int, checked : Boolean) {
        val db = DatabaseHandler(context)
        db.writableDatabase
        if (checked) {
            db.updatePurchasedCheckBox(productId, 1)
        }else{
            db.updatePurchasedCheckBox(productId, 0)
        }
        db.close()
    }
}





























