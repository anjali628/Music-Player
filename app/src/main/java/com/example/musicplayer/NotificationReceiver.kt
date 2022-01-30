package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false , context = context!!)//Toast.makeText(context,"Previous Clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.PLAY ->if(PlayerActivity.isPlaying)pauseMusic()else playMusic() //Toast.makeText(context,"Play Clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.NEXT -> prevNextSong(increment = true,context = context!!)//Toast.makeText(context,"Next Clicked", Toast.LENGTH_SHORT).show()
            ApplicationClass.EXIT -> {
               exitApplication()

            }
        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying=true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon,1F)
        PlayerActivity.binding.playPauseButtonPA.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying=false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon,0F)
        PlayerActivity.binding.playPauseButtonPA.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
    }

    private fun prevNextSong(increment:Boolean, context: Context){
        setSongPosition(increment=increment)
       /* PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        PlayerActivity.musicService!!.mediaPlayer!!.prepare()
        //PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.binding.playPauseButtonPA.setIconResource(R.drawable.pause_icon)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
*/
        PlayerActivity.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.newww).centerCrop())
            .into(PlayerActivity.binding.songImagePA)
        PlayerActivity.binding.songNamePA.text= PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        Glide.with(context)
            .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.newww).centerCrop())
            .into(NowPlaying.binding.songImgNP)
        NowPlaying.binding.songNameNP.text=PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        playMusic()
        PlayerActivity.fIndex= favouriteChecker(PlayerActivity.musicListPA[PlayerActivity.songPosition].id)
        if(PlayerActivity.isFavourite)PlayerActivity.binding.favouritesBtnPA.setImageResource(R.drawable.favorite_icon)
        else
            PlayerActivity.binding.favouritesBtnPA.setImageResource(R.drawable.favorite_empty_icon)
    }
}