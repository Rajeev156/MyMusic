package com.example.mymusic

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.random.Random


class PlayerViewModel(private val appContext: Context, application: Application) :
    AndroidViewModel(application) {
    private val favoriteSongs = mutableListOf<SongData>()
    // Define LiveData properties for UI updates
    val currentSong: LiveData<SongData> get() = _currentSong
    val playbackPosition: LiveData<Int> get() = _playbackPosition

    private val _favoriteSongsLiveData = MutableLiveData<List<SongData>>()
    val favoriteSongsLiveData: LiveData<List<SongData>> = _favoriteSongsLiveData

    private val _currentSong = MutableLiveData<SongData>()
    private val _playbackPosition = MutableLiveData<Int>()
    private val playlist: MutableList<SongData> = mutableListOf()

    val musicRepository: MusicRepository = MusicRepository(application)
    val musicList: LiveData<List<SongData>> = musicRepository.getMusicList()

    private var currentSongIndex: Int = 0

    private var mediaPlayer: MediaPlayer = MediaPlayer() // Initialize the mediaPlayer

    init {
        mediaPlayer.setOnCompletionListener {
            playNextSong()
        }
    }



    private val handler = Handler()
    private val playbackUpdateRunnable = object : Runnable {
        override fun run() {
            // Update the playback position in the LiveData
            _playbackPosition.postValue(mediaPlayer.currentPosition)

            // Schedule the task to run again after a short delay
            handler.postDelayed(this, PLAYBACK_UPDATE_INTERVAL)
        }
    }
    fun isSongInFavorites(song: SongData): Boolean {
        return favoriteSongs.contains(song)
    }

    fun playInLoop(){
        mediaPlayer.isLooping = true
    }
    fun playRandomSong() {
        if (playlist.isNotEmpty()) {
            val randomIndex = (0 until playlist.size).random()
            val randomSong = playlist[randomIndex]
            playSong(randomSong)
        }
    }

    fun addToFavorites(song: SongData) {
        if (!favoriteSongs.contains(song)) {
            favoriteSongs.add(song)
            // Notify observers that the list of favorite songs has changed
            _favoriteSongsLiveData.value = favoriteSongs
        }
    }
    // Method to get the list of favorite songs
    fun getFavoriteSongs(): List<SongData> {
        return favoriteSongs
    }

    fun startPlaybackProgressUpdates() {
        handler.postDelayed(playbackUpdateRunnable, PLAYBACK_UPDATE_INTERVAL)
    }

    fun addSongsToPlaylist(songs: List<SongData>) {
        playlist.addAll(songs)
    }

    fun playSong(song: SongData) {
        // Play the specified song
        mediaPlayer.reset()
        val songUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id
        )
        mediaPlayer.setDataSource(appContext, songUri)
        mediaPlayer.prepare()
        mediaPlayer.start()
        _currentSong.postValue(song)
        _playbackPosition.postValue(0)
        startPlaybackProgressUpdates()
    }

    fun playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % playlist.size
        val nextSong = playlist[currentSongIndex]
        playSong(nextSong)
    }

    fun getCurrentSongDuration(): Int {
        return mediaPlayer.duration
    }

    fun playPreviousSong() {
        currentSongIndex = (currentSongIndex - 1 + playlist.size) % playlist.size
        val previousSong = playlist[currentSongIndex]
        playSong(previousSong)
    }

    fun pauseSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    // Resume playback
    fun resumeSong() {
        mediaPlayer.start()
    }

    // Stop playback and release resources
    fun stopSong() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }


    companion object {
        // Define the interval for updating the playback position (e.g., every 1 second)
        private const val PLAYBACK_UPDATE_INTERVAL = 1000L
    }
}


