package com.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.interfaces.OnProductClickListener
import com.myapplication.models.ModelProduct
import kotlinx.android.synthetic.main.item_product.view.*

class AdapterProductsFromStore (
    private val context: Context,
    private val allProducts : ArrayList<ModelProduct>,
    private val interfaceOnProductClickListener : OnProductClickListener
        ) : RecyclerView.Adapter<AdapterProductsFromStore.ProductsViewHolder>() {
            inner class ProductsViewHolder(view: View) : RecyclerView.ViewHolder(view){
                val tvProductName = view.tvProductName
                val ivMinusOneProduct = view.ivMinusOneProduct
                val ivPlusOneProduct = view.ivPlusOneProduct
                val tvProductAmount = view.tvProductAmount
                val ivEditProduct = view.ivEditProduct
                val ivDeleteProduct = view.ivDeleteProduct
            }

    override fun onBindViewHolder(holder: ProductsViewHolder, i: Int) {
        val product = allProducts[i]

        //Text
        holder.tvProductName.text = product.productName

        holder.tvProductAmount.text = product.productAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {

        val inflater = LayoutInflater.from(parent.context)                              //Creates inflater
        val view = inflater.inflate(R.layout.item_product, parent,false)     //Inflates
        val   holder = ProductsViewHolder(view)                                           //Creates the viewholder defined on onBindViewHolder

        holder.ivEditProduct.setOnClickListener {
            val position = holder.adapterPosition
            val product = allProducts[position]

            interfaceOnProductClickListener.onUpdate(position, product)
        }

        holder.ivDeleteProduct.setOnClickListener {
            val position = holder.adapterPosition
            val product = allProducts[position]

            interfaceOnProductClickListener.onDelete(product)
        }

        holder.ivPlusOneProduct.setOnClickListener {
            val position = holder.adapterPosition
            val product = allProducts[position]

            interfaceOnProductClickListener.plusOne(product)
        }

        holder.ivMinusOneProduct.setOnClickListener {
            val position = holder.adapterPosition
            val product = allProducts[position]

            interfaceOnProductClickListener.minusOne(product)
        }

        holder.tvProductAmount.setOnClickListener {
            val position = holder.adapterPosition
            val product = allProducts[position]

            interfaceOnProductClickListener.manualUpdate(product)
        }

        return holder
    }

    override fun getItemCount(): Int {
        return allProducts.size
    }

    //---------------------------------------------------------
    //-------------------RV CUD METHODS------------------------
    //---------------------------------------------------------
    //THIS METHODS DO NOT CUD ON THE DATABASE, THEY SOLE PURPOSE IS TO MODIFY THE LIST OF PRODUCTS IN THE RECYCLERVIEW

    fun addProduct(product : ModelProduct){
        allProducts.add(product)
        // notifyDataSetChanged() // this method is costly I avoid it whenever possible
        //The next method tells there's an item inserted at the last place and doesn't need to check the whole dataset
        notifyItemInserted(allProducts.size)
    }


    //To avoid notifyDataSetChanged(), we check the list of allProducts until we find the product's position. Then we replace it by the "product" input.
    //Notice the input product is the new updated product. Therefore, it won't be found in the list "allProducts". We later use the position to change the old product into the new one.
    fun updateProduct(newProduct: ModelProduct){
        for (oldProduct in allProducts){
            if (oldProduct.productId == newProduct.productId) {
                val position = allProducts.indexOf(oldProduct)
                allProducts[position] = newProduct

                notifyItemChanged(position)
                break
            }
        }
    }

    //For some reason beyond my comprehension, unlike the updateProduct fun, position must be stated with the newProduct, not the old one.
    fun plusMinusProductAmount(newProduct: ModelProduct, plusMinus: String) {
        for (oldProduct in allProducts){
            if (oldProduct.productId == newProduct.productId) {
                val position = allProducts.indexOf(newProduct)

                var parameter : Float = 0f

                if (plusMinus == "plus") {
                    parameter = 1f
                } else if (plusMinus == "minus") {
                    parameter = -1f
                }

                val plusOne = newProduct.productAmount.toFloat() + parameter
                allProducts[position] = ModelProduct(
                    newProduct.productId,
                    newProduct.productName,
                    plusOne.toString(),
                    newProduct.productOnList,
                    newProduct.productPurchased,
                    newProduct.productImportant,
                    newProduct.productStoreId
                )

                notifyItemChanged(position)
                break
            }
        }
    }


    //Must find products by their Id, for some reason product from database != product from allProduct lists. However their id is the same
    fun removeProduct(removedProduct: ModelProduct){
        for (product in allProducts){
            if (product.productId == removedProduct.productId){
                val position = allProducts.indexOf(product)
                allProducts.remove(product)

                notifyItemRemoved(position)
                break
            }
        }
    }


}




































