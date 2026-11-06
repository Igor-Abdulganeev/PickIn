package ru.gorinih.pickin.data.remote

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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
    saveHtmlToFile: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                val desktopUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0.0.0 Safari/537.36"

                settings.userAgentString = desktopUA

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false

                clearCache(true)

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(
                        view: WebView,
                        finishedUrl: String,
                    ) {
                        super.onPageFinished(view, finishedUrl)
                     /*   view.evaluateJavascript(
                            "(function() { return document.documentElement.outerHTML; })();"
                        ) { result ->
                            try {
                                val html = org.json.JSONTokener(result).nextValue() as String
                                saveHtmlToFile(html)
                            } catch (e: Exception) {
                                Log.e("InstagramWeb", "Failed to save HTML", e)
                            }
                        } */
/*
                        Log.e("InstagramWeb", "Page finished: $url")
                        Log.e("InstagramWeb", "Current URL: ${view.url}")

                        view.evaluateJavascript(
                            "JSON.stringify(document.documentElement.outerHTML)"
                        ) { value ->
                            if (value != null && value != "null") {
                                runCatching {
                                    Json.decodeFromString<String>(value)
                                }.onSuccess(saveHtmlToFile) //onHtmlLoaded
                            }
                        }

 */
                        Log.d(
                            "InstagramWeb",
                            "Page finished: callbackUrl=$finishedUrl, currentUrl=${view.url}"
                        )

                        if (!finishedUrl.startsWith("https://www.instagram.com/")) {
                            Log.d("InstagramWeb", "Skip non-Instagram page: $finishedUrl")
                            return
                        }

                        view.evaluateJavascript(
                            "JSON.stringify(document.documentElement.outerHTML)"
                        ) { result ->
                            if (result != null && result != "null") {
                                runCatching {
                                    Json.decodeFromString<String>(result)
                                }.onSuccess { html ->
                                    saveHtmlToFile(html)
                                        // onHtmlLoaded(html)
                                }.onFailure {
                                    Log.e("InstagramWeb", "Failed to decode HTML", it)
                                }
                            }
                        }

                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        /*
                        val url = request.url.toString()

                        if (url.startsWith("intent://")) {
                            Log.d("InstagramWeb", "Blocked intent: $url")
                            return true
                        }

                         */

                        return false
                    }

                    override fun onPageStarted(
                        view: WebView,
                        url: String,
                        favicon: android.graphics.Bitmap?
                    ) {
                        super.onPageStarted(view, url, favicon)
                        Log.d("InstagramWeb", "Page started: $url")
                    }

                    override fun onReceivedError(
                        view: WebView,
                        request: WebResourceRequest,
                        error: android.webkit.WebResourceError
                    ) {
                        super.onReceivedError(view, request, error)

                        Log.e(
                            "InstagramWeb",
                            "Load error: url=${request.url}, " +
                                    "code=${error.errorCode}, " +
                                    "description=${error.description}, " +
                                    "mainFrame=${request.isForMainFrame}"
                        )
                    }

                    override fun onReceivedHttpError(
                        view: WebView,
                        request: WebResourceRequest,
                        response: android.webkit.WebResourceResponse
                    ) {
                        super.onReceivedHttpError(view, request, response)

                        Log.e(
                            "InstagramWeb",
                            "HTTP error: url=${request.url}, " +
                                    "status=${response.statusCode}, " +
                                    "mainFrame=${request.isForMainFrame}"
                        )
                    }

                    override fun onRenderProcessGone(
                        view: WebView?,
                        detail: RenderProcessGoneDetail?
                    ): Boolean {
                        return super.onRenderProcessGone(view, detail)
                    }
                }

                webChromeClient = WebChromeClient()
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()

                loadUrl(url)
            }
        },
        update = { webView ->
            if (webView.url != url) {
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()

                webView.loadUrl(url)
            }
        },
    )
}