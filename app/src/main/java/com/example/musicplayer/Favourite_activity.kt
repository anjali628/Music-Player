package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityFavouriteBinding

class Favourite_activity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: FavouriteAdapter

    companion object
    {
        var favouriteSongs:ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding= ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try{favouriteSongs= checkPlaylist(favouriteSongs)}catch(e:Exception){
            Toast.makeText(this,"Something Went Wrong!!",Toast.LENGTH_SHORT).show()
        }
        binding.backBtnFA.setOnClickListener{ finish()}
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager= GridLayoutManager(this, 4)
        adapter= FavouriteAdapter(this, favouriteSongs)
        binding.favouriteRV.adapter=adapter
        if(favouriteSongs.size<1) binding.shuffleBtnFA.visibility=View.INVISIBLE

        binding.shuffleBtnFA.setOnClickListener{
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class", "FavouriteShuffle")
            startActivity(intent)
        }
    }
}