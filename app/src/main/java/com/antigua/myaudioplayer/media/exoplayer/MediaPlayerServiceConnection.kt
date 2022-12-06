package com.antigua.myaudioplayer.media.exoplayer

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.mutableStateOf
import com.antigua.myaudioplayer.data.model.Audio
import com.antigua.myaudioplayer.media.constants.K
import com.antigua.myaudioplayer.media.service.MediaPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MediaPlayerServiceConnection @Inject constructor(
    @ApplicationContext context: Context
) {
    private val _playBackState: MutableStateFlow<PlaybackStateCompat?> =
        MutableStateFlow(null)

    val playBackState: StateFlow<PlaybackStateCompat?>
        get() = _playBackState

    private val _isConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean>
        get() = _isConnected

    val currentPlayingAudio = mutableStateOf<Audio?>(null)

    lateinit var  mediaControllerCompat: MediaControllerCompat

    private val mediaBrowserServiceCallback = MediaBrowserConnectionCallback(context)
    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context,MediaPlayerService::class.java),
        mediaBrowserServiceCallback,
        null
    ).apply {
        connect()
    }

    private var audioList = listOf<Audio>()

    val rootMediaId: String
        get() = mediaBrowser.root

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaControllerCompat.transportControls

    fun playAudio(audios:List<Audio>){
        audioList = audios
        mediaBrowser.sendCustomAction(K.START_MEDIA_PLAY_ACTION, null,null)
    }

    fun fastForward(seconds:Int = 10 ){
        playBackState.value?.currentPosition?.let {
            transportControls.seekTo(it + seconds * 1000)
        }
    }

    fun rewind(seconds:Int = 10 ){
        playBackState.value?.currentPosition?.let {
            transportControls.seekTo(it - seconds * 1000)
        }
    }

    fun skipToNext(){
        transportControls.skipToNext()
    }

    fun subscribe(
        parentId:String,
        callback: MediaBrowserCompat.SubscriptionCallback
    ){
       mediaBrowser.subscribe(parentId,callback)
    }

    fun unSubscribe(
        parentId:String,
        callback: MediaBrowserCompat.SubscriptionCallback
    ){
        mediaBrowser.unsubscribe(parentId,callback)
    }

    fun refreshMediaBrowserChildren(){
        mediaBrowser.sendCustomAction(K.REFRESH_MEDIA_PLAY_ACTION,null,null)
    }


    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ): MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            _isConnected.value = true
            mediaControllerCompat = MediaControllerCompat(
                context,
                mediaBrowser.sessionToken,
            ).apply {
                registerCallback(MediaControllerCallBack())
            }
        }

        override fun onConnectionSuspended() {
            _isConnected.value = false
        }

        override fun onConnectionFailed() {
            _isConnected.value = false
        }
    }

    private inner class MediaControllerCallBack: MediaControllerCompat.Callback(){
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            _playBackState.value = state
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            currentPlayingAudio.value = metadata?.let { data ->
                audioList.find {
                    it.id.toString() == data.description.mediaId
                }
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaBrowserServiceCallback.onConnectionSuspended()
        }


    }
}