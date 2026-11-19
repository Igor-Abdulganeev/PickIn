package ru.gorinih.pickin.ui.views

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import ru.gorinih.pickin.ui.theme.PickInTheme

/**
 * Created by Igor Abdulganeev on 18.11.2026
 */


@Composable
fun ChipPanelSelectTypeList(
    selected: () -> Int,
    textChips: ImmutableList<String>,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    needIcon: Boolean = false,
) {
    val select = selected()
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 20.dp)
            ),
        content = {
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            textChips.forEachIndexed { index, text ->
                ChipItem(
                    text = text,
                    index = index,
                    isSelected = index == select,
                    onClick = onClick,
                    leadIcon = if(needIcon) {
                        {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                            )
                        }
                    } else null
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewChipPanelSelectTypeList() {
    val listChips = listOf("Чип 1", "Новый чип", "Чипец")
    PickInTheme {
        ChipPanelSelectTypeList(
            selected = { 1 },
            textChips = listChips.toImmutableList(),
            onClick = {},
            needIcon = true
        )
    }
}
