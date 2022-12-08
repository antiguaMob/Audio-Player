package com.antigua.myaudioplayer.ui.audio

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antigua.myaudioplayer.data.model.Audio

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(){
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        sheetContent = {

        },
        scaffoldState = scaffoldState,
    )  {

    }
}

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
    ){
    Column {
        Row(
            modifier = Modifier.height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
                ) {

        }
    }
}

@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio
){
    Row (
        modifier = Modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
            ){
        /*TODO -> video time 13:33*/
    }
}