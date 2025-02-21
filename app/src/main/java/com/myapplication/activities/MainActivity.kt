package com.myapplication.activities
//https://techenum.com/android-recyclerview-insert-update-delete/
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.myapplication.DatabaseHandler
import com.myapplication.R
import com.myapplication.fragments.FragmentNeeds
import com.myapplication.fragments.FragmentList
import com.myapplication.models.ModelStore
import com.myapplication.utils.storeNamesIntoRecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.fragment_needs.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val needs = FragmentNeeds()
        val list = FragmentList()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flMainFragment, list)
            commit()
        }

        nvMainActivity.setOnItemSelectedListener {
            when(it.itemId){
                R.id.nvNeeds -> replaceFragment(needs)
                R.id.nvList -> replaceFragment(list)
            }
            true
        }
    }


    //--------------------------------------------------------------------------
    //-----------------------FRAGMENTS FUNCTIONS--------------------------------
    //--------------------------------------------------------------------------
    private fun replaceFragment(fragment: Fragment){
        if (fragment != null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flMainFragment, fragment)
            transaction.commit()
        }
    }




}

