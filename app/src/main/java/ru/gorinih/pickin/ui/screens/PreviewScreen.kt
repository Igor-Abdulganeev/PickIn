package ru.gorinih.pickin.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.MediaItem
import ru.gorinih.pickin.ui.models.StatusJob
import ru.gorinih.pickin.ui.views.DividerHorizontalTransparent
import ru.gorinih.pickin.ui.views.ElevateButton
import ru.gorinih.pickin.ui.views.ItemMediaCard

/**
 * Created by Igor Abdulganeev on 30.10.2026
 */

@Composable
fun PreviewScreen(
    listMedia: ImmutableList<MediaItem>,
    job: () -> StatusJob,
    onStartDownload: () -> Unit,
    onClickMedia: (String) -> Unit
) {
    val listPreviewState = rememberLazyGridState()
    val selectedMedia = rememberSaveable { mutableStateListOf<String>() }

    if (listMedia.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(size = 20.dp)
                )
        ) {

            ElevateButton(
                text = { Text(text = stringResource(R.string.button_text_selected_download)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 8.dp),
                enabled = selectedMedia.isNotEmpty() && job() == StatusJob.WAITING_JOB,
                onSavedClick = onStartDownload
            )
            DividerHorizontalTransparent(modifier = Modifier.padding(vertical = 8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = listPreviewState
            ) {
                items(listMedia, key = { it.previewUrl }) { item ->
                    ItemMediaCard(
                        media = item,
                        isSelected = selectedMedia.contains(item.previewUrl),
                        onClick = { previewUrl ->
                            when (selectedMedia.contains(previewUrl)) {
                                true -> selectedMedia.remove(previewUrl)
                                false -> selectedMedia.add(previewUrl)
                            }
                            onClickMedia(previewUrl)
                        }
                    )
                }
            }
        }
    }
}


