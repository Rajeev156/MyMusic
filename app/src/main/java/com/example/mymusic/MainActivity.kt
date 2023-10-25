package com.example.mymusic


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MusicAdapter.OnItemClickListener {
//    private val permissionRequestCode = 123
    private lateinit var musicAdapter: MusicAdapter
    private lateinit var mainActivityBinding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        showRecyclerView()
        showRecyclerView()

        mainActivityBinding.nav.setOnClickListener { view->
            openNavigationDrawer(view)
        }

        mainActivityBinding.btnShuffle.setOnClickListener {
            // Create an intent to start PlayerActivity
            val intent = Intent(this, PlayerActivity::class.java)

            // Add an extra to indicate shuffle mode (true for shuffle, false for normal)
            intent.putExtra("shuffle_mode", true) // Set it to true for shuffle mode, false for normal

            // Start PlayerActivity
            startActivity(intent)

        }


    }

    override fun onItemClick(position: Int) {
        val selectedSong = musicAdapter.currentList[position]

        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("selected_song", selectedSong) // Pass the selected song to PlayerActivity
        startActivity(intent)
    }


    private fun showRecyclerView() {
        mainActivityBinding.recyclerView.setHasFixedSize(true)
        mainActivityBinding.recyclerView.setItemViewCacheSize(13)
        mainActivityBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        musicAdapter = MusicAdapter(this,this, emptyList())
        mainActivityBinding.recyclerView.adapter = musicAdapter

        viewModel.musicList.observe(this) { musicList ->
            musicAdapter.submitList(musicList)
        }
    }
    //Function For Open Navigation Drawer
    @SuppressLint("ClickableViewAccessibility")
    private fun openNavigationDrawer(view: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(
            R.layout.header, null
        )
        val pw = PopupWindow(
            layout,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            true
        )

        val about_btn = layout.findViewById<LinearLayout>(R.id.ll_about)
        about_btn.setOnClickListener{
            startActivity(Intent(this,about::class.java))
        }
        val close = layout.findViewById<ImageView>(R.id.iv_close)
        close.setOnClickListener { pw.dismiss() }

        val exit = layout.findViewById<LinearLayout>(R.id.ll_logout)
        exit.setOnClickListener{
            showExitDialog()
            pw.dismiss()
        }
        layout.setOnTouchListener { _, _ ->
            pw.dismiss()
            true
        }
        pw.showAtLocation(view, Gravity.START, 0, 0)
    }
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Exit")
        builder.setMessage("Are you sure you want to exit the application?")

        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            // Handle the user's choice to exit the app
            finish() // This will close the activity and exit the app
        }

        builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
            // Handle the user's choice not to exit the app
            dialogInterface.dismiss() // Close the dialog
        }

        val dialog = builder.create()
        dialog.show()
    }
    }


