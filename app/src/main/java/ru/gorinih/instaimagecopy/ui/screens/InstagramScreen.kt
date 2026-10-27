package ru.gorinih.instaimagecopy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.gorinih.instaimagecopy.data.remote.InstagramWebView

/**
 * TODO тестовый
 */
@Composable
fun InstagramScreen(
    modifier: Modifier = Modifier,
) {
    val testUrl =
        "https://phet-dev.colorado.edu/html/build-an-atom/0.0.0-3/simple-text-only-test-page.html"
    val instagramUrl = "https://www.instagram.com/"
    val postUrl =
        "https://www.instagram.com/p/DesFNjnDQgL/?img_index=1"

    var url by remember { mutableStateOf(testUrl) }
    var pageStatus by remember { mutableStateOf("Готово") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = {
                    url = testUrl
                    pageStatus = "Загрузка тестовой страницы..."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Тест")
            }

            Button(
                onClick = {
                    url = instagramUrl
                    pageStatus = "Загрузка Instagram..."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Instagram")
            }

            Button(
                onClick = {
                    url = postUrl
                    pageStatus = "Загрузка публикации..."
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Пост")
            }
        }

        Text(
            text = pageStatus,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        InstagramWebView(
            url = url,
            onHtmlLoaded = { html ->
                pageStatus = "Получен HTML: ${html.length} символов"
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}