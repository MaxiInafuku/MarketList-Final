package com.myapplication.interfaces

import com.myapplication.models.ModelStore

interface OnStoreClickListener {
    fun onUpdateStore(position: Int, store: ModelStore)

    fun onDeleteStore(store: ModelStore)
}