package com.example.mymusic

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MusicAdapter(
    private val context: Context,
    private val itemClickListener: OnItemClickListener,
    initialList: List<SongData> = emptyList()
) : ListAdapter<SongData, MusicAdapter.MyHolder>(SongDataDiffCallback()) {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.song_name)
        val album = view.findViewById<TextView>(R.id.song_album)
        val image = view.findViewById<ImageView>(R.id.imageView4)

        init {
            view.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_view, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val music = getItem(position)
        holder.title.text = music.title
        holder.album.text = music.album
        val list = music.artUri
        Log.d("zzzz", "onBindViewHolder: $list")
        Glide.with(context)
            .load(Uri.parse(list))
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(holder.image)
    }

    class SongDataDiffCallback : DiffUtil.ItemCallback<SongData>() {
        override fun areItemsTheSame(oldItem: SongData, newItem: SongData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongData, newItem: SongData): Boolean {
            return oldItem == newItem
        }
    }
}



