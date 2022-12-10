package com.antigua.myaudioplayer


import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antigua.myaudioplayer.ui.audio.AudioViewModel
import com.antigua.myaudioplayer.ui.audio.HomeScreen
import com.antigua.myaudioplayer.ui.theme.MyAudioPlayerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAudioPlayerTheme {
                val permissionState = rememberPermissionState(
                    permission = READ_EXTERNAL_STORAGE
                )

                val lifecycleOwner = LocalLifecycleOwner.current

                DisposableEffect(key1 = lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            permissionState.launchPermissionRequest()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)

                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (permissionState.hasPermission) {
                        val audioViewModel = viewModel(
                            modelClass = AudioViewModel::class.java
                        )
                        val audiolist = audioViewModel.audioList
                        HomeScreen(
                            progress = audioViewModel.currentAudioProgress.value,
                            onProgressChange = {
                                  audioViewModel.seekTo(it)
                            },
                            isAudioPlaying =audioViewModel.isAudioPlaying,
                            audioList = audiolist,
                            currentPlayingAudio = audioViewModel.currentPlayingAudio.value,
                            onStart = { audioViewModel.playAudio(it) },
                            onItemClick = {
                                audioViewModel.playAudio(it)
                            },
                            onNext = {
                                audioViewModel.skipToNext()
                            }
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "Grant permission first to usethis app")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyAudioPlayerTheme {

        Greeting("Android")
    }
}