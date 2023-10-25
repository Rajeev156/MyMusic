package com.example.mymusic

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CustomListAdapter(context: Context, resource: Int, objects: List<SongData>) :
    ArrayAdapter<SongData>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = convertView ?: inflater.inflate(R.layout.fav_view, parent, false)

        val song = getItem(position)
        val titleTextView = view.findViewById<TextView>(R.id.song_name)
        val durationTextView = view.findViewById<TextView>(R.id.timer)

        // Set the song information in your custom layout
        titleTextView.text = song?.title

        durationTextView.text =song?.duration // You'll need to format the duration

        // Add more TextViews and set other song information as needed

        return view
    }

    // Add a function to format the duration if needed
    private fun formatDuration(duration: Long?): String {
        // Implement your duration formatting logic here
        // For example, converting milliseconds to minutes and seconds
        // and formatting it as "mm:ss"
        return "0:00"
    }
}
