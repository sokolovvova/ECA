package com.svadev.eca.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.svadev.eca.MainActivity
import com.svadev.eca.MainViewModel
import com.svadev.eca.R
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment() {
    private lateinit var model: MainViewModel

    //eve sso auth
    private lateinit var clientId :String
    private val redirectUrl = "http://localhost/oauth-callback"
    private val scopes = "esi-contracts.read_corporation_contracts.v1%20esi-contracts.read_character_contracts.v1%20esi-universe.read_structures.v1"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        clientId = getString(R.string.eveSSOClientId)
        model = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        model.changeTitle("Login")


        view.webView.settings.javaScriptEnabled = true
        view.webView.loadUrl("https://login.eveonline.com/oauth/authorize?response_type=code&redirect_uri=$redirectUrl&client_id=$clientId&scope=$scopes")
        view.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url !== null) {
                    if (url.contains("code=")) {
                        val newUrl = url.replace("$redirectUrl?code=", "")
                        model.setEveAuthToken(newUrl)
                        (activity as MainActivity).showProgressBar(0)
                        model.vmFragmentChanger(4)
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                (activity as MainActivity).showProgressBar(1)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                (activity as MainActivity).showProgressBar(0)
                super.onPageFinished(view, url)
            }

        }
        return view
    }
    override fun onDestroyView() {
        view?.webView?.destroy()
        super.onDestroyView()
    }
}