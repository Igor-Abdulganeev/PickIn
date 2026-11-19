package ru.gorinih.pickin.data.remote

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ru.gorinih.pickin.data.remote.HttpClientFactory.DESKTOP_UA
import ru.gorinih.pickin.data.remote.models.InstagramSession

/**
 * Created by Igor Abdulganeev on 17.11.2026
 */

private const val instagramUrl = "https://www.instagram.com/"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AuthWebView(
    modifier: Modifier = Modifier,
    onTakeSession: (InstagramSession?) -> Unit
) {
    var isDone by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(0)
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        userAgentString = DESKTOP_UA
                        useWideViewPort = true
                        loadWithOverviewMode = true
                        javaScriptCanOpenWindowsAutomatically = true
                        setSupportMultipleWindows(true)
                    }
                    val cookieManager = CookieManager.getInstance()
                    cookieManager.setAcceptCookie(true)
                    cookieManager.setAcceptThirdPartyCookies(this, true)

                    webViewClient = object : WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }

                        override fun onRenderProcessGone(
                            view: WebView?,
                            detail: RenderProcessGoneDetail?
                        ): Boolean {
                            return super.onRenderProcessGone(view, detail)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            if (!url.isNullOrBlank() && !isDone) {
                                    onTakeSession(takeSession(url))
                            }
                        }
                   }
                    webChromeClient = WebChromeClient()
                    cookieManager.removeAllCookies {
                        cookieManager.flush()
                        loadUrl(instagramUrl)
                    }
                }
            },
        )
    }
}

private fun takeSession(url: String): InstagramSession? {
    val cookieManager = CookieManager.getInstance()
    cookieManager.flush()
    val rawCookie = cookieManager.getCookie(url) ?: return null
    val cookieMap = rawCookie.split(";")
        .map { it.trim() }
        .filter { it.contains("=") }
        .associate {
            val chunks = it.split("=", limit = 2)
            chunks[0] to chunks[1]
        }
    val sessionId = cookieMap["sessionid"]
    val csrfToken = cookieMap["csrftoken"]

    return if (!sessionId.isNullOrEmpty() && !csrfToken.isNullOrEmpty())
        InstagramSession(
            csrfToken = csrfToken,
            rawCookies = rawCookie
        )
    else null
}