package com.envizioners.dumpy.ownerclient.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.envizioners.dumpy.ownerclient.R

object FragmentUtils {

    fun refreshFragment(context: Context?){
        context.let { ctx ->
            val fragmentManager = (ctx as AppCompatActivity).supportFragmentManager
            fragmentManager.let { fm ->
                val currentFragment = fm.findFragmentById(R.id.fragment_container)
                currentFragment?.let { frm ->
                    val fragmentTransaction = fm.beginTransaction()
                    fragmentTransaction.detach(frm)
                    fragmentTransaction.attach(frm)
                    fragmentTransaction.commit()
                }
            }
        }
    }
}