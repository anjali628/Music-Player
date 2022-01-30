package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File

class MainActivity : AppCompatActivity() {

    //creating binding object
    private lateinit var binding: ActivityMainBinding

    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var musicAdapter: MusicAdapter

    companion object{
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicListSearch:ArrayList<Music>
        var search: Boolean=false
        var themeIndex:Int=0
        val currentTheme= arrayOf(R.style.coolPink, R.style.coolBlue, R.style.coolPurple, R.style.coolGreen,R.style.coolBlack)

        val currentThemeNav= arrayOf(R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav,R.style.coolBlackNav)


        val currentGradient= arrayOf(R.drawable.gradient_pink,R.drawable.gradient_blue,R.drawable.gradient_purple,R.drawable.gradient_green,
            R.drawable.gradient_black)

        var sortOrder:Int=0
        val sortingList= arrayOf(MediaStore.Audio.Media.DATE_ADDED + " DESC", MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.SIZE + " DESC")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var themeEditor =getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex=themeEditor.getInt("themeIndex",0)


        //requestRuntimePermission()

        setTheme(currentThemeNav[themeIndex])

        //Initializing binding
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //for nav drawer
        toggle= ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(requestRuntimePermission()) {
            initializeLayout()

            //for retrieving favourites data using shared preferences
            Favourite_activity.favouriteSongs=ArrayList()
            val editor=getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString=editor.getString("FavouriteSongs",null)
            val typeToken=object :TypeToken<ArrayList<Music>>(){}.type

            if(jsonString!=null)
            {
                val data:ArrayList<Music> =GsonBuilder().create().fromJson(jsonString,typeToken)
                Favourite_activity.favouriteSongs.addAll(data)
            }


            PlaylistActivity.musicPlaylist=MusicPlaylist()

            val jsonStringPlaylist=editor.getString("MusicPlaylist",null)
          /*  val typeTokenPlaylist=object :TypeToken<MusicPlaylist>(){}.type
*/

            if(jsonStringPlaylist!=null)
            {
                val dataPlaylist:MusicPlaylist =GsonBuilder().create().fromJson(jsonStringPlaylist,MusicPlaylist::class.java)
                 PlaylistActivity.musicPlaylist=dataPlaylist
            }


        }

        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class", "MainActivity")

            startActivity(intent)
            //Toast.makeText(this@MainActivity,"Button Clicked",Toast.LENGTH_SHORT).show()
        }
        binding.favouritesBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, Favourite_activity::class.java))
        }

        binding.playlistsBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlaylistActivity::class.java))
        }
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId)
            {
                R.id.navFeedback -> startActivity(Intent(this@MainActivity, FeedbackActivity::class.java))
                R.id.navSettings -> startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                R.id.navAbout ->  startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                R.id.navExit -> //Toast.makeText(baseContext,"Feedback",Toast.LENGTH_SHORT).show()

                {
                    val builder=MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close the app?")
                        .setPositiveButton("Yes"){ _, _ ->
                            exitApplication()
                        }
                        .setNegativeButton("No"){dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog=builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }
    }

    //for requesting permission
    private fun requestRuntimePermission() :Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                //MusicListMA=getAllAudio()
                initializeLayout()
            }
                else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }



    @SuppressLint("SetTextI18n")
    private fun initializeLayout(){
        search=false
        val sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        sortOrder=sortEditor.getInt("sortOrder", 0)
        MusicListMA=getAllAudio()

        /*val musicList=ArrayList<String>()
        musicList.add("1 Song")
        musicList.add("2 Song")
        musicList.add("3 Song")
        musicList.add("4 Song")
        musicList.add("5 Song")*/
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(13)
        binding.musicRV.layoutManager=LinearLayoutManager(this@MainActivity)
        musicAdapter= MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter=musicAdapter

        binding.totalSongs.text="Total Songs : "+musicAdapter.itemCount

    }

    @SuppressLint("Recycle")
    private fun getAllAudio():ArrayList<Music>{
        val tempList=ArrayList<Music>()
        val selection=MediaStore.Audio.Media.IS_MUSIC+ "!=0"
        val projection= arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID)

        val cursor=this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
            /*MediaStore.Audio.Media.DATE_ADDED+ " DESC",*/
            sortingList[sortOrder], null)
        if (cursor!=null)
        {
            if(cursor.moveToFirst())
                do{
                    val titleC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                    val albumIDC=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri= Uri.parse("content://media/external/audio/albumart")
                    val artUriC=Uri.withAppendedPath(uri,albumIDC).toString()
                    val music=Music(id=idC,title=titleC,album=albumC,artist=artistC,path=pathC,duration=durationC,
                    artUri = artUriC)
                    val file= File(music.path)
                    if (file.exists())
                    {
                        tempList.add(music)
                    }

                }while(cursor.moveToNext())
                cursor.close()
        }
        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService!=null)
        {
           exitApplication()
        }


    }

    override fun onResume() {
        super.onResume()
        //for storing favourites data uding shared preferences
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(Favourite_activity.favouriteSongs)
        editor.putString("FavouriteSongs", jsonString)
        val jsonStringPlaylist = GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPlaylist)

        editor.apply()
        //for sorting
        val sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue=sortEditor.getInt("sortOrder", 0)
        if(sortOrder!=sortValue){
            sortOrder=sortValue
            MusicListMA=getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_view_menu,menu)
        //for setting gradient
        findViewById<LinearLayout>(R.id.linearLayoutNav)?.setBackgroundResource(currentGradient[themeIndex])
        val searchView=menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicListSearch= ArrayList()
                /*Toast.makeText(this@MainActivity,newText.toString(),Toast.LENGTH_SHORT).show()
          */      if(newText!=null){
                    val userInput=newText.lowercase()
                    for(song in MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            musicListSearch.add(song)
                    search=true
                    musicAdapter.updateMusicList(searchList = musicListSearch)

                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
}
