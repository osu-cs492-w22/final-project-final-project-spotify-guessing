package com.example.finalapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.TextView
import com.example.finalapp.databinding.ActivityMainBinding
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector
import com.spotify.protocol.types.ImageUri


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //    private val ClientID: String = "0e94fc1ee6bf47ff84b2f72bfed235b9"
    private val ClientID: String = "74d7ce7a3dc24285bade132ac8b23d7b"
    private var mSpotifyapp: SpotifyAppRemote? = null
    //    private val redirectUri = "com.localhost.Spotifyguessinggame://callback"
    private val redirectUri = "https://localhost/callback/"

    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val connectionParams = ConnectionParams.Builder(ClientID)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                mSpotifyapp = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    /*override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(ClientID)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                mSpotifyapp = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }*/

    private fun connected() {
        mSpotifyapp?.let {
            // Play a playlist
            val playlistURI = "spotify:album:7ebnxkx8HZNvtTB3me1S9C"
            it.playerApi.setShuffle(true)
            it.playerApi.play(playlistURI)
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val trackName: String = it.track.name
                val icon = it.track.imageUri.raw






                findViewById<TextView>(R.id.track_Description).text =
                    trackName
            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_play ->{
                PlayandResume()
                true
            }
            R.id.action_skip->{
                NextTrack()
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mSpotifyapp?.let {
            SpotifyAppRemote.disconnect(it)
        }

    }


    private fun NextTrack(){
        mSpotifyapp?.let {
            it.playerApi.skipNext()
        }
    }

    private fun PlayandResume()
    {
        mSpotifyapp?.let {

            if(isPlaying) {
                it.playerApi.pause()
                isPlaying = false
            }
            else
            {
                it.playerApi.resume()
                isPlaying = true
            }
        }
    }
}
