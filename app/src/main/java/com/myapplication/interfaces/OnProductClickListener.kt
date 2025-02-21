package com.myapplication.interfaces

import com.myapplication.models.ModelProduct

interface OnProductClickListener {

    fun onUpdate(position: Int, model: ModelProduct)

    fun onDelete(product: ModelProduct)

    fun plusOne(product : ModelProduct)

    fun minusOne(product: ModelProduct)

    fun manualUpdate(product: ModelProduct)
}