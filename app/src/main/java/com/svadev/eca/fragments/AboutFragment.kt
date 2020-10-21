package com.svadev.eca.fragments

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.svadev.eca.MainViewModel
import com.svadev.eca.R
import kotlinx.android.synthetic.main.about_fragment.view.*

class AboutFragment: Fragment() {
    private lateinit var model: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.about_fragment,container,false)

        model = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        model.changeTitle("About")

        view.ppLink.movementMethod = LinkMovementMethod.getInstance()
        view.ccpLink.movementMethod = LinkMovementMethod.getInstance()

        return view
    }
}