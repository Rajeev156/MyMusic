package com.example.mymusic

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MusicRepository(private val application: Application) {
    fun getMusicList(): LiveData<List<SongData>> {
        val musicLiveData = MutableLiveData<List<SongData>>()

        val musicList = mutableListOf<SongData>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        application.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathC = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdC = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID).toString()

            val uri = Uri.parse("content://media/external/audio/albumart")


            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                Log.d("vvvvv", "getMusicList: $title")
                val album = cursor.getString(albumColumn)
                val duration = cursor.getString(durationColumn)
                val path = cursor.getString(pathC)
                val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                musicList.add(SongData(title, album, id, duration,path,artUriC))
            }
        }

        musicLiveData.postValue(musicList)
        return musicLiveData
    }
}
