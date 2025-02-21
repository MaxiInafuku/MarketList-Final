package com.myapplication.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.DatabaseHandler
import com.myapplication.R
import com.myapplication.adapters.AdapterProductsFromStore
import com.myapplication.interfaces.OnProductClickListener
import com.myapplication.models.ModelProduct
import com.myapplication.utils.getProductsFromStore
import com.myapplication.utils.normalizeString
import kotlinx.android.synthetic.main.activity_store.*
import kotlinx.android.synthetic.main.dialog_product_amount.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlin.collections.ArrayList

class ActivityStore : AppCompatActivity() {
    private lateinit var productsFromStore : RecyclerView
    private lateinit var adapterProductsFromStore : AdapterProductsFromStore
    private var myOnProductClickListener = object : OnProductClickListener {
        override fun onUpdate(position: Int, product: ModelProduct) {
            updateProductDialog(product)
        }

        override fun onDelete(product: ModelProduct) {
            deleteProductDialog(product)
        }

        override fun plusOne(product: ModelProduct) {
            plusOneProduct(product)
        }

        override fun minusOne(product: ModelProduct) {
            minusOneProduct(product)
        }

        override fun manualUpdate(product: ModelProduct) {
            manualProductAmountDialog(product)
        }

    }


    //---------------------------------------------------------
    //--------------------ON SCREEN EVENTS---------------------
    //---------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val storeId : Int = intent.getIntExtra("STORE_ID", 0)

        //Initialize RecyclerView
        //-----------------------

        productsFromStore = rvProducts

        var allProducts : ArrayList<ModelProduct> = getProductsFromStore(this, storeId)

        if (allProducts.isNotEmpty()) {
            tvEmptyStoreProducts.visibility = View.GONE
            productsFromStore.visibility = View.VISIBLE

            productsFromStore.layoutManager = LinearLayoutManager(this)
            productsFromStore.setHasFixedSize(true)

        } else {
            tvEmptyStoreProducts.visibility = View.VISIBLE
            productsFromStore.visibility = View.GONE
        }

        adapterProductsFromStore = AdapterProductsFromStore(this, allProducts, myOnProductClickListener)
        productsFromStore.adapter = adapterProductsFromStore

        //Add Product Button
        btnAddProduct.setOnClickListener {
            var productName = etProductName.text.toString()
            val databaseHandler = DatabaseHandler(this)


            //This is done because when there are no products, adding a new product does not change the visibility of the recycler view, so despite new products being added, they don't become visible.
            if (productName.isNotEmpty() && allProducts.isNotEmpty()) {
                addNewProduct(productName, storeId, databaseHandler)

            } else if (productName.isNotEmpty()) {
                addNewProduct(productName, storeId, databaseHandler)

                tvEmptyStoreProducts.visibility = View.GONE
                productsFromStore.visibility = View.VISIBLE

                productsFromStore.layoutManager = LinearLayoutManager(this)
                productsFromStore.setHasFixedSize(true)
            }
        }



    }


    //--------------------PRODUCT METHODS----------------------
    //---------------------------------------------------------
    //Add New product Function
    private fun addNewProduct(productName: String, storeId: Int, databaseHandler: DatabaseHandler){

        val status = databaseHandler.addProduct(ModelProduct(
            0,
            productName,
            0f.toString(),
            0,
            0,
            0,
            storeId)
        )

        if (status > -1) {
            Toast.makeText(
                this,
                "Producto agregado.",
                Toast.LENGTH_SHORT
            ).show()

            //Notice the mehtod insertOrThrow in database returns status. Status is the key of the added product OR -1 if something fails.
            val addedProduct = ModelProduct(
                status.toInt(),
                normalizeString(productName),
                0f.toString(),
                0,
                0,
                0,
                storeId)
            //Update RecyclerView
            adapterProductsFromStore.addProduct(addedProduct)
        } else {
            Toast.makeText(
                this,
                "No hay producto.",
                Toast.LENGTH_SHORT
            ).show()
        }
        etProductName.text.clear()
    }


    //Update Dialog
    fun updateProductDialog(modelProduct: ModelProduct){
        val updateProductDialog = Dialog(this)
        updateProductDialog.setCancelable(false)
        updateProductDialog.setTitle("Modificar Producto")

        //layout
        updateProductDialog.setContentView(R.layout.dialog_update)

        //Aparezcan con campos anteriores
        updateProductDialog.etUpdate.setText(modelProduct.productName)

        //Botón de update
        updateProductDialog.btnUpdateProductName.setOnClickListener{
            val updatedProductName = updateProductDialog.etUpdate.text.toString()

            if (updatedProductName.isNotEmpty()) {

                val databaseHandler = DatabaseHandler(this)

                val updatedProduct = ModelProduct(
                    modelProduct.productId,
                    updatedProductName,
                    modelProduct.productAmount,
                    modelProduct.productOnList,
                    modelProduct.productPurchased,
                    modelProduct.productImportant,
                    modelProduct.productStoreId
                )

                val status = databaseHandler.updateProductName(updatedProduct)

                if (status > -1){
                    Toast.makeText(
                        this,
                        "Producto Modificado.",
                        Toast.LENGTH_SHORT).show()

                    adapterProductsFromStore.updateProduct(updatedProduct)
                    updateProductDialog.dismiss()
                }
            }else{
                Toast.makeText(
                    this,
                    "Debe completarse el nombre del lugar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateProductDialog.btnCancelUpdateProductName.setOnClickListener {
            updateProductDialog.dismiss()
            Toast.makeText(
                this,
                "Cancelado.",
                Toast.LENGTH_SHORT
            ).show()
        }
        updateProductDialog.show()

    }


    //Delete Alert Dialog
    fun deleteProductDialog(modelProduct: ModelProduct) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Producto")
        builder.setMessage("¿Esta seguro que desea eliminar ${modelProduct.productName}?")
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton("Si") {
                dialogInterface: DialogInterface, which->
            val databaseHandler = DatabaseHandler(this)

            val deletedProduct = ModelProduct(
                modelProduct.productId,
                "",
                modelProduct.productAmount,
                modelProduct.productOnList,
                modelProduct.productPurchased,
                modelProduct.productImportant,
                modelProduct.productStoreId)

            val status = databaseHandler.deleteProduct(deletedProduct)
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Producto Eliminado.",
                    Toast.LENGTH_SHORT
                ).show()
                adapterProductsFromStore.removeProduct(deletedProduct)
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") {
            dialogInterface, which -> dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }



    //Plus One
    fun plusOneProduct(plusProduct: ModelProduct){
        val databaseHandler = DatabaseHandler(this)

        val status = databaseHandler.addOneProduct(ModelProduct(
            plusProduct.productId,
            plusProduct.productName,
            plusProduct.productAmount,
            plusProduct.productOnList,
            plusProduct.productPurchased,
            plusProduct.productImportant,
            plusProduct.productStoreId))

        if (status > -1){
            adapterProductsFromStore.plusMinusProductAmount(plusProduct, "plus")
        } else{
            Toast.makeText(
                applicationContext,
                "No es posible agregar (+1) a una fracción.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    //Minus One
    fun minusOneProduct(minusProduct: ModelProduct){
        val databaseHandler = DatabaseHandler(this)

        val status = databaseHandler.removeOneProduct(ModelProduct(
            minusProduct.productId,
            minusProduct.productName,
            minusProduct.productAmount,
            minusProduct.productOnList,
            minusProduct.productPurchased,
            minusProduct.productImportant,
            minusProduct.productStoreId))

        if (status > -1){
            adapterProductsFromStore.plusMinusProductAmount(minusProduct, "minus")
        } else if (status == -3) {
            Toast.makeText(
                applicationContext,
                "No se pueden seguir restando productos.",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            Toast.makeText(
                applicationContext,
                "No es posible restar (-1) a una fracción.",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    //Update Dialog
    fun manualProductAmountDialog(modelProduct: ModelProduct){
        val updateAmountDialog = Dialog(this)
        updateAmountDialog.setCancelable(true)

        //layout
        updateAmountDialog.setContentView(R.layout.dialog_product_amount)

        //Aparezcan con campos anteriores
        updateAmountDialog.tvProductNameDisplay.text = modelProduct.productName
        updateAmountDialog.etManualProductAmount.hint = modelProduct.productAmount

        //Botón de update
        updateAmountDialog.btnUpdateProductAmount.setOnClickListener{

            val updateProductAmountTest = updateAmountDialog.etManualProductAmount.text.toString()

            var updateProductAmount : String

            //This line is required because if the user inputs 0, 0.toString() gives an Int. I need the 0 to be a float and then convert it to String.... apparently 0.toString() and 0FLOAT.toString() are not the same.p
            if (updateProductAmountTest == "0") {
                updateProductAmount = 0f.toString()
            } else {
                updateProductAmount = updateProductAmountTest
            }

            val databaseHandler = DatabaseHandler(this)

            if (updateProductAmount.isNotEmpty()) {
                val newProduct = ModelProduct(
                    modelProduct.productId,
                    modelProduct.productName,
                    updateProductAmount,
                    modelProduct.productOnList,
                    modelProduct.productPurchased,
                    modelProduct.productImportant,
                    modelProduct.productStoreId)

                val status = databaseHandler.updateProductAmount(newProduct)
                if(status >-1) {
                    Toast.makeText(
                        this,
                        "Cantidad Modificada.",
                        Toast.LENGTH_SHORT).show()

                    //Recycler View Update
                    adapterProductsFromStore.updateProduct(newProduct)

                    updateAmountDialog.dismiss()
                }
            }else{
                Toast.makeText(
                    this,
                    "Debe completarse la cantidad.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateAmountDialog.btnCancelProductAmount.setOnClickListener {
            updateAmountDialog.dismiss()
            Toast.makeText(
                this,
                "Cancelado.",
                Toast.LENGTH_SHORT
            ).show()
        }
        updateAmountDialog.show()

    }


}





























