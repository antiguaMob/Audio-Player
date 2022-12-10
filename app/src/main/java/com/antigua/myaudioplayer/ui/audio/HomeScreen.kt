package com.antigua.myaudioplayer.ui.audio

import android.widget.AdapterView.OnItemClickListener
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.antigua.myaudioplayer.data.model.Audio
import com.antigua.myaudioplayer.ui.theme.MyAudioPlayerTheme
import kotlin.math.floor

private val dummyAudioList = listOf(
    Audio(
        uri = "".toUri(),
        displayName = "Kotlin Programming",
        id =0L,
        artist = "Hood",
        data = "",
        duration = 12345,
        title = "Android Programming"
    ),
    Audio(
        uri = "".toUri(),
        displayName = "Kotlin Programming",
        id =0L,
        artist = "Hood",
        data = "",
        duration = 34345,
        title = "Android Programming"
    ),
    Audio(
        uri = "".toUri(),
        displayName = "Kotlin Programming",
        id =0L,
        artist = "Hood",
        data = "",
        duration = 8745,
        title = "Android Programming"
    ),
    Audio(
        uri = "".toUri(),
        displayName = "Kotlin Programming",
        id =0L,
        artist = "Hood",
        data = "",
        duration = 1357,
        title = "Android Programming"
    ),
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    isAudioPlaying: Boolean,
    audioList: List<Audio>,
    currentPlayingAudio: Audio?,
    onStart: (Audio) -> Unit,
    onItemClick: (Audio) -> Unit ,
    onNext: () -> Unit,
){
    val scaffoldState = rememberBottomSheetScaffoldState()
    val animatedHeight by animateDpAsState(
        targetValue = if (currentPlayingAudio == null) 0.dp
        else BottomSheetScaffoldDefaults.SheetPeekHeight
    )

    BottomSheetScaffold(
        sheetContent = {
            currentPlayingAudio?.let{ currentPlayingAudio ->
                BottomBarPlayer(
                    progress = progress,
                    onProgressChange = onProgressChange,
                    audio = currentPlayingAudio,
                    isAudioPlaying = isAudioPlaying ,
                    onStart = { onStart.invoke(currentPlayingAudio) },
                    onNext = {onNext.invoke()}
                )
            }
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = animatedHeight
    )  {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 56.dp)
        ){
           items(audioList){ audio: Audio ->
               AudioItem(audio = audio, onItemClick = {onItemClick.invoke(audio)})
           }
        }
    }
}

@Composable
fun AudioItem(
    audio: Audio,
    onItemClick: (id:Long) -> Unit,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onItemClick.invoke(audio.id) },
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = .5f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(8.dp)
            ) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = audio.displayName,
                    style = MaterialTheme.typography.h6,
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = audio.artist,
                    style = MaterialTheme.typography.subtitle1,
                    overflow = TextOverflow.Clip,
                    maxLines = 1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = .5f)
                )
            }
            Text(text = timeStampToDuration(audio.duration.toLong()))
            Spacer(modifier = Modifier.size(8.dp))
        }
    }
}
private fun timeStampToDuration(position: Long):String{
    val totalSeconds = floor(position / 1E3).toInt()
    val minutes  = totalSeconds / 60
    val remainingSeconds = totalSeconds - (minutes * 60)

    return  if(position < 0) "--:--"
    else "%d:%02d".format(minutes,remainingSeconds)
}

@Composable
fun BottomBarPlayer(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    audio: Audio,
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArtistInfo(
                audio = audio,
                modifier = Modifier.weight(1f)
            )
             MediaPlayerController(
                 isAudioPlaying =isAudioPlaying,
                 onStart = { onStart.invoke()},
                 onNext = { onNext.invoke()}
             )

        }
        Slider(
            value = progress,
            onValueChange ={onProgressChange.invoke(it)},
            valueRange = 0f..100f
        )
    }
}

@Composable
fun ArtistInfo(
    modifier: Modifier = Modifier,
    audio: Audio
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerIconItem(
            icon = Icons.Default.MusicNote,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            ),
        ) {

        }
        Spacer(modifier = Modifier.size(4.dp))
        Column {
            Text(
                text = audio.title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6,
                overflow = TextOverflow.Clip,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = audio.artist,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.subtitle1,
                overflow = TextOverflow.Clip,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PlayerIconItem (
    modifier: Modifier =Modifier,
    icon: ImageVector,
    border: BorderStroke? = null,
    backgroundColor: Color = MaterialTheme.colors.surface,
    color : Color = MaterialTheme.colors.onSurface,
    onClick: () -> Unit,
){
    Surface (
        shape = CircleShape,
        border = border,
        modifier = Modifier
            .clip(CircleShape)
            .clickable {
                onClick.invoke()
            },
        contentColor = color,
        color = backgroundColor,
      ) {
      Box(
          modifier = modifier.padding(4.dp),
          contentAlignment = Alignment.Center,
      ){
          Icon(imageVector = icon, contentDescription = null)
      }
        
    }
}


@Composable
fun MediaPlayerController(
    isAudioPlaying: Boolean,
    onStart: () -> Unit,
    onNext: () -> Unit,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(56.dp)
            .padding(4.dp)
            ){
        PlayerIconItem(
            icon = if (isAudioPlaying) Icons.Default.Pause
            else Icons.Default.PlayArrow,
            backgroundColor = MaterialTheme.colors.primary
        ) {
            onStart.invoke()
        }
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = Icons.Default.SkipNext,
            contentDescription = null,
            modifier = Modifier.clickable {
                onNext.invoke()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview(){
    MyAudioPlayerTheme {
        BottomBarPlayer(
            progress = 50f,
            onProgressChange = {},
            audio = dummyAudioList[0],
            isAudioPlaying = true,
            onStart = { }) {
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview(){
    MyAudioPlayerTheme {
        HomeScreen(
            progress = 50f,
            onProgressChange = {},
            isAudioPlaying = true,
            audioList = dummyAudioList ,
            currentPlayingAudio = dummyAudioList[0],
            onStart = {},
            onItemClick = {}
        ) {

        }
    }
}