package ru.gorinih.pickin.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AutoMode
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.EmergencyRecording
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import ru.gorinih.pickin.BuildConfig
import ru.gorinih.pickin.R
import ru.gorinih.pickin.ui.models.HelpAnimationSteps
import ru.gorinih.pickin.ui.models.ResultMessage
import ru.gorinih.pickin.ui.models.StatusJob
import ru.gorinih.pickin.ui.models.TypeDownload
import ru.gorinih.pickin.ui.models.TypeResult
import ru.gorinih.pickin.ui.theme.PickInTheme
import ru.gorinih.pickin.ui.views.ChipPanelSelectTypeList
import kotlin.time.Duration.Companion.milliseconds

/**
 * Created by Igor Abdulganeev on 02.11.2026
 */

@Composable
fun HelpScreen() {
    val state = rememberScrollState()
    val locale = Locale.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(color = MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(size = 20.dp)
            )
            .clip(shape = RoundedCornerShape(size = 20.dp))
            .verticalScroll(state = state)
    ) {
        Text(
            text = "${stringResource(R.string.help_app_name)}  ${
                stringResource(
                    R.string.help_version,
                    "${BuildConfig.VNAME}.${BuildConfig.VERSION_CODE}",
                    BuildConfig.FLAVOR_tier.toUpperCase(locale)
                )
            }",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.help_text_about),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.help_text_vpn_warning),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 4.dp),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.help_text_vpn),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.help_text_restriction),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.help_text_share),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        SampleShareLink()
        Text(
            text = stringResource(R.string.help_text_save),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.help_text_setting),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        SampleSettings()
        Text(
            text = stringResource(R.string.help_text_setting_auto),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        SampleSettingsAuto()
        Text(
            text = stringResource(R.string.help_text_setting_manual),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        SampleSettingsManual()
        Text(
            text = stringResource(R.string.help_text_preview),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.help_text_preview_photo),
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            Icon(
                modifier = Modifier,
                imageVector = Icons.Outlined.EmergencyRecording,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.errorContainer
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.help_text_preview_video),
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
            Icon(
                modifier = Modifier,
                imageVector = Icons.Outlined.Photo,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

        }
        Text(
            text = stringResource(R.string.help_text_difference),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.about_code_description))
                withLink(
                    LinkAnnotation.Url(
                        url = stringResource(R.string.about_code_url),
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                            )
                        )
                    )
                ) {
                    append(stringResource(R.string.about_code_name))
                }
                append(".")
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.about_copyright),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun SampleShareLink() {
    var stateAnimation by remember { mutableStateOf(HelpAnimationSteps.PAUSE) } // текущий шаг
    var stepNextAnimation by remember { mutableStateOf(HelpAnimationSteps.POST) } // следующий шаг (для пауз)

    val sharePost = remember { Animatable(1f) }
    val shareLink = remember { Animatable(1f) }
    val shareApp = remember { Animatable(1f) }
    suspend fun animationButton(pulsar: Animatable<Float, AnimationVector1D>) {
        repeat(3) {
            pulsar.animateTo(
                targetValue = 0.2f,
                animationSpec = tween(durationMillis = 200)
            )
            pulsar.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200)
            )
        }
    }

    val sharePostHeight = remember { Animatable(0f) }
    val shareAppHeight = remember { Animatable(0f) }
    val blurApp = remember { Animatable(0f) }
    suspend fun animationSharedBox(targetValue: Animatable<Float, AnimationVector1D>) {
        targetValue.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    suspend fun animationTurnOnBlur() {
        blurApp.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    suspend fun animationTurnOffBlur() {
        blurApp.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 100)
        )
    }

    var slideSelectWindow by remember { mutableStateOf(false) }
    var slideAppWindow by remember { mutableStateOf(false) }


    LaunchedEffect(stateAnimation) {
        when (stateAnimation) {
            HelpAnimationSteps.PAUSE -> {
                delay(1500.milliseconds)
                stateAnimation = stepNextAnimation
            }

            HelpAnimationSteps.POST -> { // мигаем кнопкой поста
                animationButton(sharePost)
                stateAnimation = HelpAnimationSteps.CHOICE_LINK
            }

            HelpAnimationSteps.CHOICE_LINK -> { // открываем меню ссылки
                animationSharedBox(sharePostHeight)
                delay(500.milliseconds)
                stateAnimation = HelpAnimationSteps.SHARE_LINK
            }

            HelpAnimationSteps.SHARE_LINK -> { // мигаем кнопкой шаринга ссылки
                animationButton(shareLink)
                stateAnimation = HelpAnimationSteps.APPS
            }

            HelpAnimationSteps.APPS -> { // открываем меню выбора приложения
                slideSelectWindow = true
                delay(700.milliseconds)
                stateAnimation = HelpAnimationSteps.SELECT_APP
            }

            HelpAnimationSteps.SELECT_APP -> { // мигаем кнопкой приложения
                animationButton(shareApp)
                stateAnimation = HelpAnimationSteps.PICKIN
            }

            HelpAnimationSteps.PICKIN -> {
                slideAppWindow = true
                animationTurnOnBlur()
                stepNextAnimation = HelpAnimationSteps.RESTART
                stateAnimation = HelpAnimationSteps.PAUSE
            }

            HelpAnimationSteps.RESTART -> {
                slideAppWindow = false
                slideSelectWindow = false
                animationTurnOffBlur()
                sharePostHeight.snapTo(0f)
                shareAppHeight.snapTo(0f)
                stepNextAnimation = HelpAnimationSteps.POST
                stateAnimation = HelpAnimationSteps.PAUSE
            }

        }

    }

    Box(modifier = Modifier.clipToBounds()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.BottomStart,
        ) {

            //instagram
            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.7f)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .blur((10 * blurApp.value).dp)
            ) {
                Image(
                    painterResource(R.drawable.insta_top),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
                            .size(30.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.like_icon),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.7f)
                        )
                    }
                    Text(
                        text = stringResource(R.string.help_label_count_like),
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    FilledIconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .size(30.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.message_icon),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.7f)
                        )
                    }
                    FilledIconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .size(30.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.reload_icon),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.5f)
                        )
                    }
                    FilledIconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .size(30.dp)
                            .graphicsLayer(
                                scaleX = sharePost.value,
                                scaleY = sharePost.value
                            )
                    ) {
                        Icon(
                            painterResource(R.drawable.share_icon),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    FilledIconButton(
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.background,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                            .size(30.dp)
                    ) {
                        Icon(
                            painterResource(R.drawable.bookmark_icon),
                            contentDescription = null,
                        )
                    }
                }
            }

            //share post
            Column(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.7f)
                    .height((170 * sharePostHeight.value).dp)
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .background(
                        shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
                        color = MaterialTheme.colorScheme.background
                    )
            ) {
                Box(
                    modifier = Modifier
                        .width(30.dp)
                        .height(11.dp)
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.help_label_find),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                            .size(30.dp),
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            painterResource(R.drawable.group_icon),
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.4f)
                        )

                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .size(40.dp)
                            .graphicsLayer(
                                scaleX = shareLink.value,
                                scaleY = shareLink.value
                            ),
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.5f)
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .size(40.dp),
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            Icons.Outlined.AutoMode,
                            contentDescription = null,
                            modifier = Modifier.aspectRatio(0.5f)
                        )
                    }
                    FilledIconButton(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 8.dp)
                            .size(40.dp),
                        onClick = {},
                        enabled = false,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Icon(
                            Icons.Outlined.Link,
                            contentDescription = null,
                            modifier = Modifier
                                .aspectRatio(0.5f)
                                .rotate(degrees = 135f)
                        )
                    }
                    Spacer(modifier = Modifier.size(40.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.help_label_share),
                        maxLines = 2,
                        style = TextStyle(
                            fontSize = 7.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .graphicsLayer(
                                scaleX = shareLink.value,
                                scaleY = shareLink.value
                            )
                            .weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.help_label_add_history),
                        maxLines = 2,
                        style = TextStyle(
                            fontSize = 7.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.help_label_copy),
                        maxLines = 2,
                        style = TextStyle(
                            fontSize = 7.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            //share app
            AnimatedVisibility(
                visible = slideSelectWindow,
                modifier = Modifier
                    .align(Alignment.BottomStart),
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 500)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.7f)
                        .height(220.dp)
                        .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        .background(
                            shape = RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp),
                            color = MaterialTheme.colorScheme.background
                        )
                        .blur((10 * blurApp.value).dp)
                ) {
                    Text(
                        text = stringResource(R.string.help_label_cancel),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.help_label_url),
                        style = TextStyle(
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp, vertical = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .padding(horizontal = 64.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .aspectRatio(0.7f)
                        )
                        Text(
                            text = stringResource(R.string.help_label_edit),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp),
                    ) {
                        Image(
                            painterResource(R.drawable.ic_launcher_playstore),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .size(30.dp)
                                .aspectRatio(0.8f)
                                .graphicsLayer(
                                    scaleX = shareApp.value,
                                    scaleY = shareApp.value
                                )
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .graphicsLayer(
                                    scaleX = shareApp.value,
                                    scaleY = shareApp.value
                                )
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    ),
                            )
                        }
                    }
                }
            }
        }

        //PickIn
        AnimatedVisibility(
            visible = slideAppWindow,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(top = 64.dp, bottom = 4.dp),
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 400)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(durationMillis = 400)
            )
        ) {
            EnterUrlScreen(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.7f)
                    .height(400.dp)
                    .padding(bottom = 8.dp, end = 16.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    .background(color = MaterialTheme.colorScheme.background),
                url = { " https://www.instagram.com/" },
                job = { StatusJob.DOWNLOAD_POST },
                result = {
                    ResultMessage(
                        message = R.string.button_text_research,
                        type = TypeResult.WORK
                    )
                },
                typeDownload = { TypeDownload.MANUAL },
                onChangeUrl = {},
                onDownloadUrl = {},
                onClear = {}
            )
        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun SampleShareLinkLightPreview() {
    PickInTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(size = 20.dp)
                )
                .clip(shape = RoundedCornerShape(size = 20.dp))
        ) {
            SampleShareLink()
        }
    }
}

@Composable
fun SampleSettings() {
    val listOfDownloadTypes = listOf(
        stringResource(R.string.list_type_download_first),
        stringResource(R.string.list_type_download_second)
    ).toImmutableList()

    NotUsabilityBox {
        ChipPanelSelectTypeList(
            selected = { 0 },
            textChips = listOfDownloadTypes,
            onClick = {},
            text = stringResource(R.string.header_type_download_label)
        )
    }

}

@Composable
fun SampleSettingsAuto() {
    val listOfMediaTypes = listOf(
        stringResource(R.string.list_media_download_first),
        stringResource(R.string.list_media_download_second),
        stringResource(R.string.list_media_download_third)
    ).toImmutableList()
    NotUsabilityBox {
        ChipPanelSelectTypeList(
            selected = { 0 },
            textChips = listOfMediaTypes,
            onClick = {},
            text = stringResource(R.string.header_media_download_label)
        )
    }
}

@Composable
fun SampleSettingsManual() {
    NotUsabilityBox {
        ChipPanelSelectTypeList(
            selected = { 0 },
            textChips = listOf(stringResource(R.string.setting_key_auto_complete)).toImmutableList(),
            onClick = {},
            needIcon = true
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SampleShareLinkNightPreview() {
    PickInTheme {
        HelpScreen()
    }
}

@Composable
fun NotUsabilityBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent(PointerEventPass.Initial)
                        .changes
                        .forEach { it.consume() }
                }
            }
        },
        content = content
    )
}