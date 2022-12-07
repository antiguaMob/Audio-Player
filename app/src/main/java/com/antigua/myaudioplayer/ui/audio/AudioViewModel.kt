package com.antigua.myaudioplayer.ui.audio

import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antigua.myaudioplayer.data.model.Audio
import com.antigua.myaudioplayer.data.repository.AudioRepository
import com.antigua.myaudioplayer.media.constants.K
import com.antigua.myaudioplayer.media.exoplayer.MediaPlayerServiceConnection
import com.antigua.myaudioplayer.media.exoplayer.currentPosition
import com.antigua.myaudioplayer.media.exoplayer.isPlaying
import com.antigua.myaudioplayer.media.service.MediaPlayerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val repository: AudioRepository,
    serviceConnection: MediaPlayerServiceConnection,
): ViewModel() {

    var audioList = mutableStateListOf<Audio>()
    val currentPlayingAudio = serviceConnection.currentPlayingAudio
    private val isConnected = serviceConnection.isConnected
    lateinit var rootMediaId: String
    var currentPlayBackPosition by mutableStateOf(0L)
    private var updatePosition = true
    private val playbackState = serviceConnection.playBackState
    val isAudioPlaying: Boolean
        get() = playbackState.value?.isPlaying == true

    private val subscriptionCallback = object :MediaBrowserCompat.SubscriptionCallback(){
        override fun onChildrenLoaded(
            parentId: String,
            children: MutableList<MediaBrowserCompat.MediaItem>
        ) {
            super.onChildrenLoaded(parentId, children)
        }
    }

    private val serviceConnection = serviceConnection.also {

    }

    val currentDuration = MediaPlayerService.currentDuration
    var currentAudioProgress = mutableStateOf(0f)

    init {
        viewModelScope.launch {
            audioList += getAndFormatAudioData()
            isConnected.collect{
                if(it){
                   rootMediaId = serviceConnection.rootMediaId
                   serviceConnection.playBackState.value?. apply {
                       currentPlayBackPosition = position
                   }
                    serviceConnection.subscribe(rootMediaId,subscriptionCallback)
                }
            }
        }

    }

    private suspend fun  getAndFormatAudioData(): List<Audio>{
        return  repository.getAudioData().map {
            val displayName = it.displayName.substringBefore(".")
            val artist = if(it.artist.contains("<unknown>"))
                "Unknown Artist" else  it.artist
            it.copy(
                displayName = displayName,
                artist = artist,
            )
        }
    }

    fun playAudio(currentAudio: Audio){
        serviceConnection.playAudio(audioList)
        if (currentAudio.id == currentPlayingAudio.value?.id){
            if(isAudioPlaying){
                serviceConnection.transportControls.pause()
            } else {
                serviceConnection.transportControls.play()
            }


        } else {
            serviceConnection.transportControls
                .playFromMediaId(
                    currentAudio.id.toString(),
                    null
                )
        }
    }

    fun stopPlayback(){
        serviceConnection.transportControls.stop()
    }

    fun fastForward(){
        serviceConnection.fastForward()
    }

    fun rewind(){
        serviceConnection.rewind()
    }

    fun skipToNext(){
        serviceConnection.skipToNext()
    }

    fun seekTo(value: Float){
        serviceConnection.transportControls.seekTo(
            (currentDuration * value / 100f).toLong()
        )
    }

    private fun updatePlayBack(){
        viewModelScope.launch {
            val position = playbackState.value?.currentPosition ?: 0

            if(currentPlayBackPosition != position){
                currentPlayBackPosition = position
            }
            if(currentDuration > 0){
                currentAudioProgress.value = (
                        currentPlayBackPosition.toFloat()
                        / currentDuration.toFloat() * 100f
                        )
            }

            delay(K.PLAYBACK_UPDATE_INTERVAL)
            if(updatePosition){
                updatePlayBack()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unSubscribe(
            K.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback(){

            }
        )
        updatePosition = false
    }
}