package com.example.mymusic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData


class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val musicRepository: MusicRepository = MusicRepository(application)

    val musicList: LiveData<List<SongData>> = musicRepository.getMusicList()
}
