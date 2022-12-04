package com.envizioners.dumpy.ownerclient.fragments.biv

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.envizioners.dumpy.ownerclient.R
import com.github.piasy.biv.view.BigImageView


class ImageViewer : Fragment() {

    private lateinit var bigImageView: BigImageView
    private lateinit var backButton: Button
    private var uriString = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            uriString = it.getString("uriString").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.f_image_viewer, container, false)
        bigImageView = view.findViewById(R.id.mBigImage)
        backButton = view.findViewById(R.id.imgview_back)
        backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        with(bigImageView){
            showImage(Uri.parse(uriString))
        }
        return view
    }
}