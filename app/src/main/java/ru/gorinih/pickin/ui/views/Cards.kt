package ru.gorinih.pickin.ui.views

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.EmergencyRecording
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.LocalImagePreview
import ru.gorinih.pickin.ui.models.MediaItem

/**
 * Created by Igor Abdulganeev on 30.10.2026
 */

@Composable
fun ItemMediaCard(
    media: MediaItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val preview = LocalImagePreview.current ?: return

    val imageRequest = remember(context, media.previewUrl) {
        ImageRequest.Builder(context)
            .data(media.previewUrl)
            .placeholderMemoryCacheKey(media.previewUrl)
            .build()
    }
    val isCurrentPreview =
        preview.transitionPreviewUrl == media.previewUrl

    val isPreviewOpen =
        preview.selectedPreviewUrl == media.previewUrl

    var isLoadImage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            shape = RoundedCornerShape(size = 20.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier.weight(1f),
                imageVector = if (media.isVideo) Icons.Outlined.EmergencyRecording else Icons.Outlined.Photo,
                contentDescription = null,
                tint = if (media.isVideo) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary
            )
            when (isLoadImage) {
                true -> Icon(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            onClick = {
                                onClick(media.previewUrl)
                            }
                        ),
                    imageVector = if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )

                false -> Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }

        }

        with(preview.sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(size = 20.dp))
                    .combinedClickable(
                        enabled = true,
                        onClick = {
                            if (isLoadImage)
                                onClick(media.previewUrl)
                        },
                        onLongClick = {
                            if (isLoadImage)
                                preview.onLongClick(media.previewUrl)
                        }
                    )
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = media.author,
                    placeholder = painterResource(R.drawable.instplaceholder),
                    error = painterResource(R.drawable.ic_launcher_background),
                    contentScale = ContentScale.FillWidth,
                    alpha = if (isSelected) 0.5f else 1f,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(size = 20.dp)
                        )
                        .clip(RoundedCornerShape(size = 20.dp)),
                    onSuccess = {
                        isLoadImage = true
                    },
                    onError = {
                        isLoadImage = false
                    }
                )

                if (isCurrentPreview) {

                    AsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.instplaceholder),
                        error = painterResource(R.drawable.ic_launcher_background),
                        contentScale = ContentScale.FillWidth,
                        alpha = if (isSelected) 0.5f else 1f,
                        modifier = Modifier
                            .sharedElementWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(
                                    key = media.previewUrl
                                ),
                                visible = !isPreviewOpen,
                                boundsTransform = { _, _ ->
                                    spring(stiffness = Spring.StiffnessLow)
                                }
                            )
                            .fillMaxSize()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(size = 20.dp)
                            )
                            .clip(RoundedCornerShape(size = 20.dp))
                    )
                }
            }
        }
    }
}
