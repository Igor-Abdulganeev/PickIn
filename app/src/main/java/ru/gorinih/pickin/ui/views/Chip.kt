package ru.gorinih.pickin.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Created by Igor Abdulganeev on 18.11.2026
 */

@Composable
fun ChipItem(
    text: String,
    index: Int,
    isSelected: Boolean,
    leadIcon: @Composable (() -> Unit)? = null,
    onClick: (Int) -> Unit,
) {
    FilterChip(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        onClick = { onClick(index) },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        enabled = true,
        selected = isSelected,
        leadingIcon = leadIcon,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,

            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,

            ),
        border = null
    )
}
