package ru.gorinih.instaimagecopy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.instaimagecopy.ui.models.ImagePreview
import ru.gorinih.instaimagecopy.ui.models.LocalImagePreview
import ru.gorinih.instaimagecopy.ui.models.ScreenBlocks
import ru.gorinih.instaimagecopy.ui.screens.EnterUrlScreen
import ru.gorinih.instaimagecopy.ui.screens.HelpScreen
import ru.gorinih.instaimagecopy.ui.screens.PreviewScreen
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
                var selectedPreviewUrl by rememberSaveable { mutableStateOf("") }

                LaunchedEffect(incomingIntentUriState.value) {
                    incomingIntentUriState.value?.let {
                        viewModel.onChangeUrl(it, true)
                        incomingIntentUriState.value = null
                    }
                }
                SharedTransitionLayout {
                    AnimatedVisibility(
                        visible = true,
                        label = "MainScreenScope",
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val previewerContext =
                            remember(selectedPreviewUrl, this, this@SharedTransitionLayout) {
                                ImagePreview(
                                    sharedTransitionScope = this@SharedTransitionLayout,
                                    animatedVisibilityScope = this,
                                    selectedPreviewUrl = selectedPreviewUrl,
                                    onLongClick = { previewUrl ->
                                        selectedPreviewUrl = previewUrl
                                    }
                                )
                            }

                        CompositionLocalProvider(LocalImagePreview provides previewerContext) {
                            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {

                                    Column(modifier = Modifier.fillMaxSize()) {
/*
                        InstagramScreen(
                            saveHtml = {html ->
                                viewModel.sHtml(html)
                            }
                        )
*/
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            ElevatedButton(
                                                onClick = {
                                                    viewModel.rotateVisibilitySettings()
                                                },
                                            ) { Text(text = stringResource(R.string.button_text_settings)) }
                                            ElevatedButton(
                                                onClick = {
                                                    viewModel.rotateVisibilityHelp()
                                                },
                                            ) { Text(text = stringResource(R.string.button_text_help)) }
                                        }
                                        if (imageCopyState.blocks[ScreenBlocks.INPUT] == true)
                                            EnterUrlScreen(
                                                url = { imageCopyState.url },
                                                job = { imageCopyState.job },
                                                result = { imageCopyState.result },
                                                typeDownload = { imageCopyState.selectedTypeDownload },
                                                onChangeUrl = { newUrl ->
                                                    viewModel.onChangeUrl(
                                                        newUrl = newUrl
                                                    )
                                                },
                                                onDownloadUrl = { viewModel.getPostData() },
                                                onClear = { viewModel.clear() }
                                            )
                                        AnimatedVisibility(
                                            visible = imageCopyState.blocks[ScreenBlocks.HELP] == true
                                        ) {
                                            HelpScreen()
                                        }
                                        AnimatedVisibility(
                                            visible = imageCopyState.blocks[ScreenBlocks.SETTINGS] == true
                                        ) {
                                            SettingsScreen(
                                                selectedDownloadType = { imageCopyState.selectedTypeDownload },
                                                selectedMediaType = { imageCopyState.selectedMediaDownload },
                                                onClickDownloadType = {
                                                    viewModel.onChangeTypeDownload(
                                                        it
                                                    )
                                                },
                                                onClickMediaType = {
                                                    viewModel.onChangeMediaDownload(
                                                        it
                                                    )
                                                }
                                            )
                                        }
                                        AnimatedVisibility(
                                            visible = imageCopyState.blocks[ScreenBlocks.PREVIEW] == true
                                        ) {
                                            PreviewScreen(
                                                listMedia = imageCopyState.listMedia.toImmutableList(),
                                                onStartDownload = {
                                                    viewModel.getSelectedData()
                                                },
                                                onClickMedia = {
                                                    viewModel.selectData(it)
                                                }
                                            )
                                        }
                                    }

                                    AnimatedVisibility(
                                        visible = selectedPreviewUrl.isNotEmpty(),
                                        enter = fadeIn(tween(400)),
                                        exit = fadeOut(tween(200)),
                                        label = "ImagePreviewScope",
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        val overlayVisibilityScope = this
                                        val context = LocalContext.current
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.3f))
                                                .clickable(
                                                    onClick = { selectedPreviewUrl = "" }
                                                ),
                                            contentAlignment = Alignment.Center) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(selectedPreviewUrl)
                                                    .placeholderMemoryCacheKey(selectedPreviewUrl)
                                                    .crossfade(false)
                                                    .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.FillWidth,
                                                modifier = Modifier
                                                    .sharedElement(
                                                        sharedContentState = rememberSharedContentState(
                                                            selectedPreviewUrl
                                                        ),
                                                        animatedVisibilityScope = overlayVisibilityScope,
                                                        boundsTransform = { _, _ -> spring(stiffness = Spring.StiffnessLow) }
                                                    )
                                                    .fillMaxWidth(0.9f)
                                            )
                                        }
                                    }

                                }
                            }
                        }
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
        val sharedText = when (intent.action) {
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