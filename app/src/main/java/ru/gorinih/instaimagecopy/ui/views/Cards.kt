package ru.gorinih.instaimagecopy.ui.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import ru.gorinih.instaimagecopy.R
import ru.gorinih.instaimagecopy.ui.models.MediaItem

/**
 * Created by Igor Abdulganeev on 30.10.2026
 */

@Composable
fun ItemMediaCard(
    media: MediaItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current
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
                tint = if (media.isVideo) Color.Red else Color.Magenta
            )
            Icon(
                modifier = Modifier.weight(1f),
                imageVector = if (isSelected) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                tint = if (isSelected) Color(red = 0.8f, green = 0.33f, blue = 0.0f) else Color.Gray
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(media.previewUrl)
                .placeholderMemoryCacheKey(media.previewUrl)
                .build(),
            contentDescription = media.author,
            placeholder = painterResource(R.drawable.instplaceholder),
            error = painterResource(R.drawable.pht),
            contentScale = ContentScale.FillWidth,
            alpha = if (isSelected) 0.5f else 1f,
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .clip(shape = RoundedCornerShape(size = 20.dp))
                .aspectRatio(1f)
                .clickable(enabled = true) {
                    onClick(media.previewUrl)
                }
        )
    }
}
