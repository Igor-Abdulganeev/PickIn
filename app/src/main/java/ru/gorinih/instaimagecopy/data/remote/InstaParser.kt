package ru.gorinih.instaimagecopy.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * webview пока не используется
 */

class InstaParser(
    private val context: Context
) {

    var webView: WebView? = null
        private set

    suspend fun fetchHtml(url: String): String {
        if (webView == null) webView = createWebView()
        println("WEBVIEW = $webView")

        return suspendCancellableCoroutine { continuation ->
            webView?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, loadedUrl: String?) {
                    super.onPageFinished(view, loadedUrl)

                    view?.evaluateJavascript(
                        "(function() { return document.documentElement.outerHTML; })();",
                        object : ValueCallback<String> {
                            override fun onReceiveValue(htmlJson: String?) {
                                if (continuation.isActive) {
                                    val cleanHtml = parseJsonString(htmlJson)
                                    continuation.resume(
                                        cleanHtml
                                    ) { cause, _, _ -> }
                                }
                            }
                        }
                    )
                }

                @Deprecated("Deprecated in Java")
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    if (continuation.isActive && failingUrl == url) {
                        continuation.resume("")
                    }
                }
            }

            // Формируем заголовки, идентичные тем, что отправляет реальный Chrome на телефоне
            val headers = HashMap<String, String>().apply {
                put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                put("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                put("Sec-Fetch-Dest", "document")
                put("Sec-Fetch-Mode", "navigate")
                put("Sec-Fetch-Site", "none")
                put("Sec-Fetch-User", "?1")
                put("Upgrade-Insecure-Requests", "1")
            }
            webView?.loadUrl(url, headers)

            continuation.invokeOnCancellation {
                webView?.stopLoading()
            }
        }


    }

    private fun parseJsonString(htmlJson: String?): String {
        if (htmlJson == null) return ""
        return htmlJson
            .trim('"')
            .replace("\\u003C", "<")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView {
        webView?.let { return it }
        val newWebView: WebView = WebView(context).apply {
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                userAgentString = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Mobile Safari/537.36"

                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this@apply, true)

            }
            println("CREATED configuration webview")
            if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_ENABLE)) {
                WebSettingsCompat.setSafeBrowsingEnabled(settings, true)
            }
        }
        webView = newWebView
        return newWebView
    }
}