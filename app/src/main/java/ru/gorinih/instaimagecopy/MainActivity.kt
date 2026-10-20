package ru.gorinih.instaimagecopy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import ru.gorinih.instaimagecopy.ui.theme.InstaImageCopyTheme
import kotlin.time.Duration.Companion.milliseconds

const val testUrl = "https://phet-dev.colorado.edu/html/build-an-atom/0.0.0-3/simple-text-only-test-page.html"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstaImageCopyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        url = testUrl,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(url: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var onClick by remember { mutableStateOf(true) }
    val viewModel: MainViewModel = koinViewModel()
    LaunchedEffect(onClick) {
        if (!onClick) {
            delay(10_000.milliseconds)
            onClick = true
        }
    }
    Text(
        text = "получение строки /r $url",
        modifier = modifier
    )
    Button(
        enabled = onClick,
        onClick = {
            onClick = !onClick
            scope.launch {
                viewModel.getWebData(url)
            }
        }
    ) {
        Text(text = "Получить")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InstaImageCopyTheme {
        Greeting("Android")
    }
}