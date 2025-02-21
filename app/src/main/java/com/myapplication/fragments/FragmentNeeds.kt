package com.myapplication.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.DatabaseHandler
import com.myapplication.R
import com.myapplication.adapters.AdapterStores
import com.myapplication.interfaces.OnStoreClickListener
import com.myapplication.models.ModelStore
import com.myapplication.utils.getStores
import com.myapplication.utils.normalizeString
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.fragment_needs.*

class FragmentNeeds : Fragment() {
    private lateinit var rvAllStores : RecyclerView
    private lateinit var adapterStores : AdapterStores
    private var myOnStoreClickListener = object : OnStoreClickListener {
        override fun onUpdateStore(position: Int, store: ModelStore) {
            updateStoreDialog(store)
        }

        override fun onDeleteStore(store: ModelStore) {
            deleteStoreDialog(store)
        }

    }


    //---------------------LAYOUT INFLATION-------------------------------
    //--------------------------------------------------------------------
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_needs, container, false)
    }

    //---------------------ON SCREEN ACTIONS------------------------------
    //--------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize RecyclerView
        //-----------------------
        rvAllStores = rvStores

        var allStores : ArrayList<ModelStore> = getStores(requireContext())

        if (allStores.isNotEmpty()) {
            tvEmptyStores.visibility = View.GONE
            rvAllStores.visibility = View.VISIBLE

            rvAllStores.layoutManager = LinearLayoutManager(requireContext())
            rvAllStores.setHasFixedSize(true)
        } else{
            tvEmptyStores.visibility = View.VISIBLE
            rvAllStores.visibility = View.GONE
        }

        adapterStores = AdapterStores(requireContext(), allStores, myOnStoreClickListener)
        rvAllStores.adapter = adapterStores

        //Button Add Place
        //----------------
        btnAddPlace.setOnClickListener{
            var newStore = etStoreName.text.toString()
            val databaseHandler = DatabaseHandler(requireContext())
            val repeatedStore : Long = -2

            //This is done because when there are no stores, adding a new store does not change the visibility of the recycler view, so despite new stores being added, they don't become visible.
            if (newStore.isNotEmpty() && allStores.isNotEmpty()) {
                addStore(newStore, databaseHandler, repeatedStore)

            }else if (newStore.isNotEmpty()) {
                addStore(newStore, databaseHandler, repeatedStore)

                tvEmptyStores.visibility = View.GONE
                rvAllStores.visibility = View.VISIBLE


                rvAllStores.layoutManager = LinearLayoutManager(requireContext())
                rvAllStores.setHasFixedSize(true)
            }
        }
    }


    //--------------------------------------------------------------------------
    //--------------------------STORE METHODS-----------------------------------
    //--------------------------------------------------------------------------
    //Add Store
    private fun addStore(newStore: String, databaseHandler : DatabaseHandler, repeatedStore : Long){
        val status = databaseHandler.addStore(ModelStore(0,newStore))

        if (status > -1) {
            Toast.makeText(
                requireContext(),
                "Lugar agregado.",
                Toast.LENGTH_LONG
            ).show()

            val addedStore = ModelStore(status.toInt(), normalizeString(newStore))
            adapterStores.addStore(addedStore)

            etStoreName.text.clear()

        }else if (status == repeatedStore){
            Toast.makeText(
                requireContext(),
                "¡Lugar repetido!",
                Toast.LENGTH_SHORT
            ).show()
        }else {
            Toast.makeText(
                requireContext(),
                "Debe completarse el nombre del lugar.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    //Update Dialog
    fun updateStoreDialog(store: ModelStore){
        val updateStoreDialog = Dialog(requireContext())
        updateStoreDialog.setCancelable(false)
        updateStoreDialog.setTitle("Modificar Lugar de Compras")

        //layout
        updateStoreDialog.setContentView(R.layout.dialog_update)

        //Aparezcan con campos anteriores
        updateStoreDialog.etUpdate.setText(store.storeName)

        //Botón de update
        updateStoreDialog.btnUpdateProductName.setOnClickListener{
            val updateStore = updateStoreDialog.etUpdate.text.toString()

            val databaseHandler = DatabaseHandler(requireContext())

            if (updateStore.isNotEmpty()) {
                val updatedStore = ModelStore(store.idStore, updateStore)

                val status = databaseHandler.updateStore(updatedStore)
                if(status >-1) {
                    Toast.makeText(
                        requireContext(),
                        "Lugar Modificado.",
                        Toast.LENGTH_SHORT).show()

                    adapterStores.updateStore(updatedStore)
                    updateStoreDialog.dismiss()

                } else if(status == -2) {
                    Toast.makeText(
                        requireContext(),
                        "¡Lugar Repetido!",
                        Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    "Debe completarse el nombre del lugar.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        updateStoreDialog.btnCancelUpdateProductName.setOnClickListener {
            updateStoreDialog.dismiss()
            Toast.makeText(
                requireContext(),
                "Cancelado.",
                Toast.LENGTH_SHORT
            ).show()
        }
        updateStoreDialog.show()

    }


    //Delete Alert Dialog
    fun deleteStoreDialog(store: ModelStore){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar Lugar de compras")
        builder.setMessage("Esta seguro que desea eliminar ${store.storeName}? Se eliminaran todos los datos de compra del mismo!")
        builder.setIcon(R.drawable.ic_warning)
        val tag = "imFragmentNeeds"

        builder.setPositiveButton("Si") {
                dialogInterface: DialogInterface, which->
            val databaseHandler = DatabaseHandler(requireContext())
            val status = databaseHandler.deleteStore(ModelStore(store.idStore, ""))
            val status2 = databaseHandler.deleteProductsFromStore(store)
            if (status > -1 && status2 > -1) {
                Toast.makeText(
                    requireContext(),
                    "Lugar Eliminado.",
                    Toast.LENGTH_SHORT
                ).show()

                adapterStores.removeStore(store)
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") {
                dialogInterface, which -> dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}