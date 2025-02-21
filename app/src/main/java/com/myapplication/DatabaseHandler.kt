package com.myapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.myapplication.models.ModelProduct
import com.myapplication.models.ModelStore
import com.myapplication.utils.normalizeString
import java.lang.RuntimeException

class DatabaseHandler (context: Context) : SQLiteOpenHelper (context, DATABASE_NAME, null, DATABASE_VERSION) {

    //--------------------------------------------------------------------
    //------------------------TABLE COLUMNS-----------------------------
    //--------------------------------------------------------------------

    companion object {
        private const val DATABASE_NAME = "MarketListDatabase"
        private const val DATABASE_VERSION = 1

        //Common to all
        private const val KEY_ID = "id"

        //Stores Table
        private const val STORES = "stores"
        private const val STORE_NAME = "store_name"

        //Products Table
        private const val PRODUCTS = "products"
        private const val PRODUCT_STORE = "product_store"
        private const val PRODUCT_NAME = "product_name"
        private const val AMOUNT = "amount"
        private const val ON_LIST = "on_list"
        private const val PURCHASED = "purchased"
        private const val IMPORTANT = "important"
    }

    //--------------------------------------------------------------------
    //------------------------TABLE CREATION------------------------------
    //--------------------------------------------------------------------

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_TABLE_STORES: String = (
                "CREATE TABLE $STORES(" +
                        "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$STORE_NAME TEXT UNIQUE" +
                        ")"
                )

        val CREATE_TABLE_PRODUCTS: String = (
                "CREATE TABLE $PRODUCTS(" +
                        "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "$PRODUCT_STORE INTEGER," +
                        "$PRODUCT_NAME TEXT," +
                        "$AMOUNT TEXT," +
                        "$ON_LIST INTEGER," + //En verdad es boolean, valdrá 0 false o 1 true
                        "$PURCHASED INTEGER," +
                        "$IMPORTANT INTEGER" +
                        ")"
                )

        db?.execSQL(CREATE_TABLE_STORES)
        db?.execSQL(CREATE_TABLE_PRODUCTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $STORES")
        db.execSQL("DROP TABLE IF EXISTS $PRODUCTS")
    }


    //--------------------------------------------------------------------
    //------------------------STORES METHOD-------------------------------
    //--------------------------------------------------------------------

    //-------ADD NEW STORE--------
    fun addStore(store: ModelStore) : Long {
        val db = this.writableDatabase

        val newStore = ContentValues()
        newStore.put(STORE_NAME, normalizeString(store.storeName))

        try {
            val success = db.insertOrThrow(STORES, null, newStore)
            db.close()
            return success
        } catch (e:RuntimeException){
            val success : Long = -2
            db.close()
            return success
        }
    }

    //-------UPDATE STORE---------
    fun updateStore(store: ModelStore) : Int {
        val db = this.writableDatabase

        val updatedStore = ContentValues()
        updatedStore.put(STORE_NAME, normalizeString(store.storeName))

        try {
            val success = db.update(STORES, updatedStore, KEY_ID + "=" + store.idStore, null)
            db.close()
            return success
        } catch (e: RuntimeException){
            val success = -2
            db.close()
            return success
        }

    }

    //-------DELETE STORE---------
    fun deleteStore(store: ModelStore) : Int {
        val db = this.writableDatabase

        val success = db.delete(STORES, KEY_ID + "=" + store.idStore, null)

        db.close()
        return success
    }


    //------READ ALL STORES-----

    @SuppressLint("Range")
    fun viewStores() : ArrayList<ModelStore> {
        val storeList = ArrayList<ModelStore>()

        //Query to select all the stores from table
        val selectQuery = "SELECT * FROM $STORES"
        val db = this.writableDatabase
        var cursor : Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e:SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var storeName : String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                storeName = cursor.getString(cursor.getColumnIndex(STORE_NAME)).uppercase()

                val store = ModelStore(id,storeName)
                storeList.add(store)

            } while (cursor.moveToNext())
        }
        db.close()
        return storeList
    }



    //----------READ NON EMPTY STORES ID's ONLY-----------
    @SuppressLint("Range")
    fun nonEmptyStoresIds() : ArrayList<Int> {
        val nonEmptyStoreIds = ArrayList<Int>()

        //Query to select products to be bought
        val zero : String = 0f.toString()
        val db = this.writableDatabase
        var cursor : Cursor? = null
        val selectQuery = "SELECT * FROM $PRODUCTS WHERE $AMOUNT != $zero ORDER BY $PRODUCT_STORE"

        try{
            cursor = db.rawQuery(selectQuery, null)
        } catch (e:SQLiteException) {
            db.execSQL(selectQuery)
            return  ArrayList()
        }

        var id : Int
        var testId : Int

        if (cursor.moveToFirst()) {
            do {
                testId = cursor.getInt(cursor.getColumnIndex(PRODUCT_STORE))

                if (nonEmptyStoreIds.isEmpty()) {
                    id = testId
                    nonEmptyStoreIds.add(id)
                } else {
                    if (nonEmptyStoreIds.last() != testId) {
                        id = testId
                        nonEmptyStoreIds.add(id)
                    }
                }
            } while (cursor.moveToNext())
        }
        db.close()
        return nonEmptyStoreIds
    }

    //----------GET STORES BY ID-----------
    @SuppressLint("Range")
    fun viewNonEmptyStores(storesIds: ArrayList<Int>) : ArrayList<ModelStore> {
        val nonEmptyStrores = ArrayList<ModelStore>()

        val db = this.writableDatabase
        var cursor : Cursor? = null
        val selectQuery = "SELECT * FROM STORES"

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id : Int
        var testId : Int
        var store : String

        if (cursor.moveToFirst()) {
            do {
                testId = cursor.getInt(cursor.getColumnIndex(KEY_ID))

                for (i in storesIds) {
                    if (testId == i) {
                        id = i
                        store = cursor.getString(cursor.getColumnIndex(STORE_NAME))

                        val store = ModelStore(id, store)
                        nonEmptyStrores.add(store)
                    }
                }
            } while (cursor.moveToNext())
        }
        db.close()
        return nonEmptyStrores
    }

    //--------------------------------------------------------------------
    //------------------------PRODUCTS METHODS----------------------------
    //--------------------------------------------------------------------

    //-------ADD NEW PRODUCT--------
    fun addProduct(product: ModelProduct) : Long {
        val db = this.writableDatabase

        val newProduct = ContentValues()
        newProduct.put(PRODUCT_NAME, normalizeString(product.productName))
        newProduct.put(AMOUNT, product.productAmount)
        newProduct.put(ON_LIST, product.productOnList)
        newProduct.put(PURCHASED, product.productPurchased)
        newProduct.put(IMPORTANT, product.productImportant)
        newProduct.put(PRODUCT_STORE, product.productStoreId)


        try {
            val success = db.insertOrThrow(PRODUCTS, null, newProduct)
            db.close()
            return success
        } catch (e:RuntimeException){
            val success : Long = -2
            db.close()
            return success
        }
    }

    //-------UPDATE PRODUCT NAME---------
    fun updateProductName(product: ModelProduct) : Int {
        val db = this.writableDatabase

        val updateProductName = ContentValues()
        updateProductName.put(PRODUCT_NAME, normalizeString(product.productName))

        try {
            val success = db.update(PRODUCTS, updateProductName, KEY_ID + "=" + product.productId, null)
            db.close()
            return success
        } catch (e: RuntimeException){
            val success = -2
            db.close()
            return success
        }
    }


    //-------UPDATE PRODUCT AMOUNT---------
    fun updateProductAmount(product: ModelProduct) : Int {
        val db = this.writableDatabase

        val updateProductAmount = ContentValues()
        updateProductAmount.put(AMOUNT, normalizeString(product.productAmount))

        try {
            val success = db.update(PRODUCTS, updateProductAmount, KEY_ID + "=" + product.productId, null)
            db.close()
            return success
        } catch (e: RuntimeException){
            val success = -2
            db.close()
            return success
        }
    }

    //-------ADD ONE PRODUCT-----------
    fun addOneProduct(product: ModelProduct) : Int {
        val db = this.writableDatabase

        val updateProductAmount = ContentValues()


        try {
            val newAmount : Float = normalizeString(product.productAmount).toFloat() + 1
            updateProductAmount.put(AMOUNT, newAmount.toString())
            val success = db.update(PRODUCTS, updateProductAmount, KEY_ID + "=" + product.productId, null)
            db.close()
            return success
        } catch (e: RuntimeException){
            val success = -2
            db.close()
            return success
        }

    }

    //-------MINUS ONE PRODUCT---------
    fun removeOneProduct(product: ModelProduct) : Int {
        val db = this.writableDatabase

        val updateProductAmount = ContentValues()


        try {
            val newAmount : Float = normalizeString(product.productAmount).toFloat() - 1
            updateProductAmount.put(AMOUNT, newAmount.toString())

            if (newAmount < 0) {
                return -3
            }else{
                val success = db.update(PRODUCTS, updateProductAmount, KEY_ID + "=" + product.productId, null)
                db.close()
                return success
            }
        } catch (e: RuntimeException){
            val success = -2
            db.close()
            return success
        }

    }


    //-------DELETE PRODUCT--------------
    fun deleteProduct(product: ModelProduct) : Int {
        val db = this.writableDatabase

        val success = db.delete(PRODUCTS, KEY_ID + "=" + product.productId, null)

        db.close()
        return success
    }


    //------READ PRODUCTS FROM A SPECIFIC STORE-----
    //This first line makes the command cursor.getColumnIndex() avalible, don't know why
    @SuppressLint("Range")
    fun viewProductsStore(storeId: Int) : ArrayList<ModelProduct> {
        val selectQuery = "SELECT * FROM $PRODUCTS WHERE $PRODUCT_STORE=$storeId"
        val db = this.writableDatabase
        var cursor : Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e:SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        val productsList = ArrayList<ModelProduct>()

        if (cursor.moveToFirst()) {
            do{
                var productId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                var productName = cursor.getString(cursor.getColumnIndex(PRODUCT_NAME))
                var productAmount = cursor.getString(cursor.getColumnIndex(AMOUNT))
                var productOnList = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                var productPurchased = cursor.getInt(cursor.getColumnIndex(PURCHASED))
                var productImportant = cursor.getInt(cursor.getColumnIndex(IMPORTANT))
                var productStoreId = cursor.getInt(cursor.getColumnIndex(PRODUCT_STORE))

                val product = ModelProduct(productId, productName, productAmount, productOnList, productPurchased, productImportant, productStoreId)

                productsList.add(product)
            } while (cursor.moveToNext())
        }

        db.close()
        return productsList
    }

    //-------UPDATE PURCHASED STATUS---------------
    fun updatePurchasedCheckBox(productId : Int, intBoolean : Int) : Int {
        val db = this.writableDatabase

        val updatePurchased = ContentValues()
        updatePurchased.put(PURCHASED, intBoolean)

        val success = db.update(PRODUCTS, updatePurchased ,"$KEY_ID = $productId", null)

        db.close()

        return success
    }

    //----------CLEAN ALL PRODUCTS FROM STORE-----------
    fun cleanPurchasedProducts(productsInStore : ArrayList<ModelProduct>) : Int {
        val db = this.writableDatabase

        val tryForSuccess = ArrayList<Int>()

        for (product in productsInStore){
            val restart = ContentValues()
            restart.put(AMOUNT, 0f.toString())
            restart.put(PURCHASED, 0)

            tryForSuccess.add(db.update(PRODUCTS, restart, "$KEY_ID = ${product.productId}", null))
        }

        db.close()

        val success = tryForSuccess.last()

        return success
    }

    //--------DELETE ALL PRODUCTS FROM SPECIFIC STORE-------
    /*fun deleteProductsFromStore(store: ModelStore) {
        val storeId = store.idStore
        val db = this.writableDatabase
        val selectQuery = "SELECT $KEY_ID FROM $PRODUCTS WHERE $PRODUCT_STORE = $storeId"
        var cursor : Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }



        if (cursor != null) {
            if (cursor.moveToFirst()){
                do {
                    db.delete(PRODUCTS,  )
                }
            }
        }

    }*/

    fun deleteProductsFromStore(store: ModelStore) : Int {
        val storeId = store.idStore
        val db = this.writableDatabase
        val success = db.delete(PRODUCTS, "$PRODUCT_STORE=$storeId", null)

        db.close()

        return success
    }
}






































