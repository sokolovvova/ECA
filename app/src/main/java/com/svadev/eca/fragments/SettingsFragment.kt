package com.svadev.eca.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.svadev.eca.MainViewModel
import com.svadev.eca.R
import kotlinx.android.synthetic.main.settings_fragment.view.*
import java.text.SimpleDateFormat
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SettingsFragment: Fragment() {
    private lateinit var model: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_fragment,container,false)

        model = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        model.updateAuthCharacter()
        model.changeTitle("Settings")
        view.loginButton.setOnClickListener {
            model.vmFragmentChanger(5)
        }
        model.authCharacterLd.observe(viewLifecycleOwner){
            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            view.viewExpiresOn.text = formatter.format(it.expires_in)
            view.viewCharacterName.text = it.CharacterName
        }
        return view
    }
}