package com.example.mymusic

import android.content.Intent
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mymusic.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    private var appCloseHandler = Handler(Looper.getMainLooper())
    var musicService: MusicService? = null
    private var repeat = false
    var songPosition: Int = 0
    private lateinit var playerViewModel: PlayerViewModel
    lateinit var playerActivityBinding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playerActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_player)

        playerActivityBinding.btnBack.setOnClickListener {
            finish()
        }
        playerActivityBinding.btnShare.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            playerViewModel.musicList.observe(this) { musicList1 ->
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicList1[songPosition].path))
                startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
            }


        }

        playerActivityBinding.btnEqualiser.setOnClickListener {
            try {
                val intent = Intent(Settings.ACTION_SOUND_SETTINGS)
                startActivity(intent)
            }catch (e: Exception){Toast.makeText(this,  "Equalizer Feature not Supported!!", Toast.LENGTH_SHORT).show()}
        }

        //setup change seek bar change listner
        playerActivityBinding.seekBar.setOnSeekBarChangeListener(this)

        //Setup playerViewModel
        playerViewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory(application, applicationContext)
        ).get(PlayerViewModel::class.java)


        // Retrieve the extra indicating shuffle mode
        val isShuffleMode = intent.getBooleanExtra("shuffle_mode", false)

        // Add Soongs to Playlist (Your existing code)
        playerViewModel.musicList.observe(this) { musicList1 ->
            playerViewModel.addSongsToPlaylist(musicList1)
        }

        //Add Songs to Playlist

        if (isShuffleMode){
            playerViewModel.musicList.observe(this) { musicList1 ->
                playerViewModel.addSongsToPlaylist(musicList1)
                playerViewModel.playRandomSong()
                playerActivityBinding.btnPause.visibility = View.VISIBLE
                playerActivityBinding.btnPlay.visibility = View.INVISIBLE
            }
        }else{
            playerViewModel.musicList.observe(this) { musicList1 ->
                playerViewModel.addSongsToPlaylist(musicList1)
            }
        }



        // Get the selected song from intent and change the views
        val selectedSong = intent.getParcelableExtra<SongData>("selected_song")
        if (selectedSong != null) {
            playerViewModel.playSong(selectedSong)
            playerActivityBinding.btnPause.visibility = View.VISIBLE
            playerActivityBinding.btnPlay.visibility = View.INVISIBLE
        }

        // get the playback position and setup the seek bar position
        playerViewModel.playbackPosition.observe(this) { position ->
            // Update the progress bar position based on the playback position
            playerActivityBinding.seekBar.max = playerViewModel.getCurrentSongDuration()
            playerActivityBinding.seekBar.progress = position
        }

        // onclick farward
        playerActivityBinding.btnForward.setOnClickListener {

            if (isShuffleMode){
                // Play a random song from the playlist in shuffle mode
                playerViewModel.playRandomSong()
            }else{
                // Play the next song in normal mode
                playerViewModel.playNextSong()
            }
            playerActivityBinding.btnPlay.visibility = View.INVISIBLE
            playerActivityBinding.btnPause.visibility = View.VISIBLE

        }

        //on click backward
        playerActivityBinding.btnBackward.setOnClickListener {
            playerViewModel.playPreviousSong()
            playerActivityBinding.btnPlay.visibility = View.INVISIBLE
            playerActivityBinding.btnPause.visibility = View.VISIBLE
        }

        //on click pause
        playerActivityBinding.btnPause.setOnClickListener {
            playerActivityBinding.btnPlay.visibility = View.VISIBLE
            playerActivityBinding.btnPause.visibility = View.INVISIBLE
            playerViewModel.pauseSong()
        }

        //on click play
        playerActivityBinding.btnPlay.setOnClickListener {
            playerActivityBinding.btnPause.visibility = View.VISIBLE
            playerActivityBinding.btnPlay.visibility = View.INVISIBLE
            playerViewModel.resumeSong()
        }
        //on click repeat button
        playerActivityBinding.btnRepeat.setOnClickListener {
            if (!repeat){
                repeat = true
                playerViewModel.playInLoop()
                playerActivityBinding.btnRepeat.setColorFilter(Color.RED)
                Toast.makeText(this, "now iin loop", Toast.LENGTH_SHORT).show()
            }else{
                repeat = false
                playerActivityBinding.btnRepeat.setColorFilter(Color.BLUE)
                Toast.makeText(this, "repeat off", Toast.LENGTH_SHORT).show()
            }

        }

        playerActivityBinding.btnTimer.setOnClickListener {
            showTimeSelectionPopup()
        }

        // Set up click listener for the favorite icon
        playerActivityBinding.btnHeart.setOnClickListener {
            // Observe the current song LiveData to get the current song
            playerViewModel.currentSong.observe(this) { currentSong ->
                Log.d("ppppp", "onCreate: $currentSong")
                if (currentSong != null) {
                    // Add the current song to favorites
                    playerViewModel.addToFavorites(currentSong)
                    playerActivityBinding.btnHeart.setColorFilter(Color.RED)
                }
            }
        }


        // Observe LiveData for UI updates
        playerViewModel.currentSong.observe(this) { song ->
            // Update UI with the current song
            playerActivityBinding.songName.text = song.title

//            val uri = Uri.parse(song.artUri).toString()
            val uri = song.artUri

            Glide.with(this)
                .load(uri)
                .apply(RequestOptions()
                    .placeholder(R.drawable.music_player_icon_slash_screen) // Placeholder image in case the album art is not available
                    .centerCrop())
                .into(playerActivityBinding.songImage)
            // Get and display the song duration
            val durationInMillis = playerViewModel.getCurrentSongDuration()

            val formattedDuration =
                durationInMillis.let { convertMillisToMinutesAndSeconds(it.toLong()) }

            playerActivityBinding.endTime.text = formattedDuration

            // Update other UI elements
        }

        playerViewModel.playbackPosition.observe(this) { position ->
            // Update SeekBar position
            playerActivityBinding.seekBar.progress = position
            // Update other UI elements
        }

        // Set up click listeners for playback controls and favorite icon
        // ...
    }

    private fun showTimeSelectionPopup() {
        val timeOptions = arrayOf(" 15 minutes", "20 minutes", "30 minutes")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Time")
        builder.setItems(timeOptions) { _, which ->
            val selectedTime = when (which) {
                0 -> 15L * 60 * 1000  // 15 minutes in milliseconds
                1 -> 20L * 60 * 1000 // 20 minutes in milliseconds
                2 -> 30L * 60 * 1000 // 30 minutes in milliseconds
                else -> 1L * 60 * 1000 // Default to 15 minutes
            }
            Toast.makeText(this, "Selected time: ${selectedTime / 60000} minutes", Toast.LENGTH_SHORT).show()

            scheduleAppClosure(selectedTime)
        }

        builder.create().show()
    }
    fun getImgArt(path: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        return retriever.embeddedPicture
    }
    private fun scheduleAppClosure(delayMillis: Long) {
        appCloseHandler.postDelayed({
            finishAffinity() // Close all activities associated with the app
            System.exit(0) // Terminate the app process
        }, delayMillis)
    }

    override fun onDestroy() {
        playerViewModel.pauseSong()
        super.onDestroy()
    }


    private fun convertMillisToMinutesAndSeconds(durationInMillis: Long): String {
        val minutes = (durationInMillis / 1000 / 60).toInt()
        val seconds = ((durationInMillis / 1000) % 60).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        playerViewModel.resumeSong()
        playerActivityBinding.btnPause.visibility = View.VISIBLE
        playerActivityBinding.btnPlay.visibility = View.INVISIBLE
        }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.let {
            val newPosition = it.progress
            playerViewModel.seekTo(newPosition)
        }
    }

}


