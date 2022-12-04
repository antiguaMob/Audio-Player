package com.antigua.myaudioplayer.data.repository

import com.antigua.myaudioplayer.data.ContentResolverHelper
import com.antigua.myaudioplayer.data.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject
constructor(private val contentResolverHelper: ContentResolverHelper) {
    suspend fun getAudioData():List<Audio> = withContext(Dispatchers.IO){
        contentResolverHelper.getAudioData()
    }
}