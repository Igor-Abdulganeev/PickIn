package ru.gorinih.instaimagecopy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.screens.EnterUrlScreen
import ru.gorinih.instaimagecopy.ui.screens.SettingsScreen
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme

class ImageCopyActivity : ComponentActivity() {

    private var incomingIntentUriState = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        incomingIntentUriState.value = extractUrl(intent)
        enableEdgeToEdge()
        setContent {
            InstaImageCopyTheme {
                val viewModel: ImageCopyViewModel = koinViewModel()
                val imageCopyState = viewModel.imageCopyState

                LaunchedEffect(incomingIntentUriState.value) {
                        incomingIntentUriState.value?.let {
                            viewModel.onChangeUrl(it)
                            incomingIntentUriState.value = null
                        }
                }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {

                        Button(
                            onClick = {
                                viewModel.rotateVisibilitySettings()
                            }
                        ) { Text(text = "Переключение экрана") }
                        if (imageCopyState.blocks[ScreenBlocks.INPUT] == true)
                            EnterUrlScreen(
                                url = { imageCopyState.url },
                                job = { imageCopyState.job },
                                result = { imageCopyState.result },
                                onChangeUrl = { newUrl -> viewModel.onChangeUrl(newUrl = newUrl) },
                                onDownloadUrl = { viewModel.getWebData() },
                            )
                        if (imageCopyState.blocks[ScreenBlocks.SETTINGS] == true)
                            SettingsScreen()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractUrl(intent)?.let {
            incomingIntentUriState.value = it
        }
    }

    private fun extractUrl(intent: Intent): String? {
        val sharedText = when(intent.action) {
            Intent.ACTION_SEND -> {
                intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            }

            Intent.ACTION_VIEW -> {
                intent.dataString
            }
            else -> null
        } ?: return null

        val url = Regex("""https://www\.instagram\.com/\S+""")
            .find(sharedText)
            ?.value
            ?.trimEnd('.', ',', '!', '?', ';', ':', ')', ']', '}')
            ?: return null

        return url.takeIf { it.startsWith("https://www.instagram.com/") }
    }
}