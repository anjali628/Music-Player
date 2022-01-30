package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {
    lateinit var binding:ActivityPlaylistDetailsBinding
    lateinit var adapter: MusicAdapter

    companion object{
        var currentPlaylistPos:Int=-1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding= ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos=intent.extras?.get("index")as Int
        try{
        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist=
            checkPlaylist(playlist = PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist)}
        catch (e:Exception){
            Toast.makeText(this,"Something Went Wrong!!",Toast.LENGTH_SHORT).show()
        }
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager=LinearLayoutManager(this)
      /*  PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.addAll(MainActivity.MusicListMA)
        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.shuffle()
    */
        adapter= MusicAdapter(this,PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist,playlistDetails = true)
        binding.playlistDetailsRV.adapter=adapter
        binding.backBtnPD.setOnClickListener{
            finish()
        }
        binding.shuffleBtnPD.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class", "PlaylistDetailsShuffle")

            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this,SelectionActivity::class.java))
        }
        binding.removeAllBtnPD.setOnClickListener {
            if(adapter.itemCount>0) {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Remove")
                    .setMessage("Do you want to remove all songs from playlist?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                        adapter.refreshPlaylist()
                        dialog.dismiss()
                        binding.shuffleBtnPD.visibility=View.INVISIBLE
                        binding.moreInfoPD.text="Playlist is Empty"
                        binding.playlistImgPD.setImageResource(R.drawable.clear_icon)

                        /*binding.playlistImgPD.visibility=View.INVISIBLE
                        binding.moreInfoPD.visibility=View.INVISIBLE
               */     }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)



                //adapter.notifyDataSetChanged()

            }
            else
                Toast.makeText(this,"There is no song in the playlist!!\n First add some songs",Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text=PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text="Total ${adapter.itemCount} Songs.\n\n" +
                "Created On:\n${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount>0)
        {
            Glide.with(this)
                .load(PlaylistActivity.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.newww).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility=View.VISIBLE

        }
        adapter.notifyDataSetChanged()

        //for storing favourites data using shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)

        editor.apply()

/*

        val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE)

        PlaylistActivity.musicPlaylist=MusicPlaylist()

        val jsonStringPlaylist=editor.getString("MusicPlaylist",null)
        */
/*  val typeTokenPlaylist=object :TypeToken<MusicPlaylist>(){}.type
*//*


        if(jsonStringPlaylist!=null)
        {
            val dataPlaylist:MusicPlaylist =
                GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
            PlaylistActivity.musicPlaylist=dataPlaylist
        }
*/

    }

}