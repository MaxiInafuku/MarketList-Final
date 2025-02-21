package com.myapplication.utils

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.DatabaseHandler
import com.myapplication.adapters.AdapterProductsFromStore
import com.myapplication.adapters.AdapterProductsList
import com.myapplication.adapters.AdapterStores
import com.myapplication.adapters.AdapterStoresList
import com.myapplication.fragments.FragmentNeeds
import com.myapplication.interfaces.OnProductClickListener
import com.myapplication.models.ModelProduct
import com.myapplication.models.ModelStore

//---------------------------------------------------------------------
//---------------------------STORES UTILS------------------------------
//---------------------------------------------------------------------

fun getStores(context: Context) : ArrayList<ModelStore> {
    val databaseHandler = DatabaseHandler(context)
    val storeList : ArrayList<ModelStore> = databaseHandler.viewStores()

    return storeList
}

fun getNonEmptyStores(context: Context) : ArrayList<ModelStore> {
    val databaseHandler = DatabaseHandler(context)
    val storesIds : ArrayList<Int> = databaseHandler.nonEmptyStoresIds()

    return databaseHandler.viewNonEmptyStores(storesIds)

}

//
fun storeNamesIntoRecyclerView(rvList: RecyclerView, tvEmpty : TextView, context: Context) {
    //I wanna show only stores where there are "non-zero" products
    val nonEmptyStores = getNonEmptyStores(context)

    if (nonEmptyStores.size > 0){
        rvList.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        rvList.layoutManager = LinearLayoutManager(context)

        val itemAdapterStoresList = AdapterStoresList(context, nonEmptyStores)
        rvList.adapter = itemAdapterStoresList

    }
    else {
        rvList.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }
}


//---------------------------------------------------------------------
//-------------------------PRODUCTS UTILS------------------------------
//---------------------------------------------------------------------
fun getProductsFromStore(context: Context, storeId: Int) : ArrayList<ModelProduct> {
    val databaseHandler = DatabaseHandler(context)
    val productList : ArrayList<ModelProduct> = databaseHandler.viewProductsStore(storeId)

    return productList
}

fun productNamesIntoRecyclerView(rvList: RecyclerView, tvEmpty: TextView, context: Context, storeId: Int) {
    val allProductsFromStore = getProductsFromStore(context, storeId)

    if (allProductsFromStore.size > 0) {
        rvList.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE

        rvList.layoutManager = LinearLayoutManager(context)

        val productsOnList = ArrayList<ModelProduct>()

        //I want only to show products whose amount is not zero. Zero means "no need to buy".
        for (product in allProductsFromStore) {
            val zero : Float = 0f
            if (product.productAmount != zero.toString()) {
                productsOnList.add(product)
            }
        }
        val itemAdapterProducts = AdapterProductsList(context, productsOnList)

        rvList.adapter = itemAdapterProducts

    }
    else{
        rvList.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }
}


































