package com.myapplication.models

class ModelProduct (
    val productId : Int,
    val productName : String,
    val productAmount : String,   //It's a String 'cuz it could be 1 1/2Kg or whatever
    val productOnList : Int,      //0 false 1 true, actually a Boolean but database have no Boolean type
    val productPurchased : Int,   //"Boolean"
    val productImportant : Int,
    val productStoreId : Int
        )