package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    lateinit var binding:ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="About"
        binding.aboutText.text=aboutText()

    }
    private fun aboutText():String
    {
        return "Developed By: Anjali Bindal" +
                "\n\nIf you want to provide feedback, I will love to hear that." +
                "\n\nIt is a feature-rich application that allows user to meet the requirements that a Music Player application must have. The application is completely user-friendly and easy to use for all the users." +
                "\n\nYou will surely enjoy listening the songs when you will once use the application. All the best features are implemented in the application for meeting the user criterias." +
                "\nI hope you like my application." +
                "\n\nThank You!" +
                "\nHave a nice day :)"
    }
}