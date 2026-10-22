package ru.gorinih.instaimagecopy

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
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.screens.EnterUrlScreen
import ru.gorinih.instaimagecopy.ui.screens.SettingsScreen
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme

class ImageCopyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstaImageCopyTheme {
                val viewModel: ImageCopyViewModel = koinViewModel()
                val imageCopyState = viewModel.imageCopyState
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
                                onChangeUrl = { newUrl -> viewModel.onChangeUrl(newUrl = newUrl) },
                                onDownloadUrl = { viewModel.getWebData() }
                            )
                        if (imageCopyState.blocks[ScreenBlocks.SETTINGS] == true)
                            SettingsScreen()
                    }
                }
            }
        }
    }
}