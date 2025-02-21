package com.myapplication.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.myapplication.DatabaseHandler
import com.myapplication.R
import com.myapplication.utils.getProductsFromStore
import com.myapplication.utils.productNamesIntoRecyclerView
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.dialog_purchase_done.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.lang.StringBuilder

class ActivityProductList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        val storeId : Int = intent.getIntExtra("STORE_ID",0)

        btnDone.setOnClickListener {
            purchasedFinishedWarning(this, storeId)
        }

        productNamesIntoRecyclerView(rvProductsList, tvEmpty, this, storeId)
    }

    //Compras finalizadas
    private fun purchasedFinishedWarning(context : Context, storeId : Int) {
        val doneDialog = Dialog(this)
        doneDialog.setCancelable(false)

        doneDialog.setContentView(R.layout.dialog_purchase_done)

        //Make an array of string with the names of every non-purchased product
        val missingProducts : ArrayList<String> = ArrayList()
        val productsInStore = getProductsFromStore(context, storeId)

        for (product in productsInStore) {
            if (product.productPurchased == 0 && product.productAmount != 0f.toString()) {
                missingProducts.add(product.productName)
            }
        }


        //Puts every missing product name in the corresponding TextView
        if (missingProducts.isNotEmpty()){
            val stringConcatenator = StringBuilder()
            for (i in missingProducts){
                stringConcatenator.append("| $i |")
            }
            doneDialog.tvMissingItems.text = stringConcatenator

            doneDialog.tvNotBought.text = "No se compraro los siguientes items:"
        } else {
            doneDialog.tvMissingItems.text = ""

            doneDialog.tvNotBought.text = ""
        }


        doneDialog.btnTrulyDone.setOnClickListener {
            //Restart amounts of products to 0 and their purchased status to 0 as well (False)
            val databaseHandler = DatabaseHandler(this)

            val status = databaseHandler.cleanPurchasedProducts(productsInStore)

            if (status > -1) {
                //productNamesIntoRecyclerView(rvProductsList, tvEmpty, this, storeId, tag)
            }

            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        doneDialog.btnCancelDone.setOnClickListener {
            doneDialog.dismiss()
        }

        doneDialog.show()
    }

}


/*btnDone.setOnClickListener {
    val products = getProductsFromStore(this, storeId)

    val databaseHandler = DatabaseHandler(this)

    val status = databaseHandler.cleanPurchasedProducts(products)

    if (status > -1) {
        Toast.makeText(this, "DONE", Toast.LENGTH_SHORT).show()
        productNamesIntoRecyclerView(rvProductsList, tvEmpty, this, storeId, tag)
    }

    val intent = Intent(applicationContext, MainActivity::class.java)
    startActivity(intent)
    finish()
}*/