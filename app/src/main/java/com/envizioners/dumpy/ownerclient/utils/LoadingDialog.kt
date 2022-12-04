package com.envizioners.dumpy.ownerclient.utils

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.envizioners.dumpy.ownerclient.R

class LoadingDialog {

    private val activity: Activity
    private val dialog: AlertDialog
    private val loadingDialogLayout: ConstraintLayout
    private val loadingScreenText: TextView

    constructor(activity: Activity){
        this.activity = activity
        val builder: AlertDialog.Builder = AlertDialog.Builder(this.activity)
        val inflater = this.activity.layoutInflater
        val baseView = inflater.inflate(R.layout.loading_dialog, null)
        this.loadingScreenText = baseView.findViewById(R.id.loadingDialogText)
        this.loadingDialogLayout = baseView.findViewById(R.id.loadingDialogLayout)
        builder.setView(baseView)
        builder.setCancelable(false)
        this.dialog = builder.create()
    }

    fun startLoading() = dialog.show()
    fun dismissLoading() = dialog.dismiss()
    fun setLoadingScreenText(loadingScreenText: String){
        this.loadingScreenText.text = loadingScreenText
    }
}