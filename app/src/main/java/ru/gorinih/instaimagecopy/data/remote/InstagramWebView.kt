package ru.gorinih.instaimagecopy.data.remote

import android.annotation.SuppressLint
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.serialization.json.Json

/**
 *  TODO тестовый
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InstagramWebView(
    url: String,
    onHtmlLoaded: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView,
                        finishedUrl: String,
                    ) {
                        super.onPageFinished(view, finishedUrl)
                        view.evaluateJavascript(
                            "JSON.stringify(document.documentElement.outerHTML)"
                        ) { value ->
                            if (value != null && value != "null") {
                                runCatching {
                                    Json.decodeFromString<String>(value)
                                }.onSuccess(onHtmlLoaded)
                            }
                        }
                    }

                    override fun onRenderProcessGone(
                        view: WebView?,
                        detail: RenderProcessGoneDetail?
                    ): Boolean {
                        return super.onRenderProcessGone(view, detail)
                    }
                }

                webChromeClient = WebChromeClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
        },
    )
}