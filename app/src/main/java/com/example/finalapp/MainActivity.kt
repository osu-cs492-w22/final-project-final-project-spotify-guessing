package com.example.finalapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.finalapp.databinding.ActivityMainBinding
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val ClientID: String = "0e94fc1ee6bf47ff84b2f72bfed235b9"
    private var mSpotifyapp: SpotifyAppRemote? = null
    private val redirectUri = "com.localhost.Spotifyguessinggame://callback"

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
            it.playerApi.play(playlistURI)
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val trackName: String = it.track.name
                val icon = it.track.imageUri

                Glide.with(this)
                    .load(icon)
                    .into(findViewById(R.id.iv_track_icon))

                findViewById<TextView>(R.id.track_Description).text =
                    trackName
            }


        }

    }

    override fun onStop() {
        super.onStop()
        mSpotifyapp?.let {
            SpotifyAppRemote.disconnect(it)
        }

    }
}