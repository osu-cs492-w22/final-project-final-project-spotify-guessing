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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.TextView
import com.example.finalapp.databinding.ActivityMainBinding
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector
import com.spotify.protocol.types.ImageUri
import org.w3c.dom.Text

const val SCORE_PREFIX = "Score: "

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    //    private val ClientID: String = "0e94fc1ee6bf47ff84b2f72bfed235b9"
    private val ClientID: String = "74d7ce7a3dc24285bade132ac8b23d7b"
    private var mSpotifyapp: SpotifyAppRemote? = null
    //    private val redirectUri = "com.localhost.Spotifyguessinggame://callback"
    private val redirectUri = "https://localhost/callback/"

    private var isPlaying = true

    private lateinit var guessBoxET: EditText
    private var userScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guessBoxET = findViewById(R.id.et_guess_box)
        findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"

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
        val guessBtn: Button = findViewById(R.id.btn_guess)
        guessBtn.setOnClickListener {
            var guess = guessBoxET.text.toString().lowercase()
            var correctAnswer: String = findViewById<TextView>(R.id.track_Description).text.toString().lowercase()
            // Remove white spacing, commas, apostrophes to help prevent "wrong" answers with slightly off grammar
            // For correct answer
            correctAnswer = correctAnswer.replace("'", "")
            correctAnswer = correctAnswer.replace(",", "")
            correctAnswer = correctAnswer.replace(" ", "")
            correctAnswer = correctAnswer.replace("\n", "")
            correctAnswer = correctAnswer.replace("?", "")
            correctAnswer = correctAnswer.replace("!", "")
            correctAnswer = correctAnswer.replace(".", "")
            // For user's guess
            guess = guess.replace("'", "")
            guess = guess.replace(",", "")
            guess = guess.replace(" ", "")
            guess = guess.replace("\n", "")
            guess = guess.replace("?", "")
            guess = guess.replace("!", "")
            guess = guess.replace(".", "")
            Log.d("answer", guess)
            Log.d("answer", correctAnswer)
            if (guess == correctAnswer) {
                Log.d("answer", "Correct!!")
                userScore += 10
                findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
            } else {
                Log.d("answer", "incorrect!!")
            }
        }
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
            val playlistURI = "spotify:playlist:37i9dQZF1DZ06evO4BaAkp"
            it.playerApi.setShuffle(true)
            it.playerApi.play(playlistURI)
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val trackName: String = it.track.name
                val album: String = it.track.album.name
                val artist: String = it.track.artist.name
                val icon = it.track.imageUri.raw
                var wholeString: String = ""
                if (album == trackName) {
                    // prevent repetition for single releases with no album
                    wholeString = artist.plus(", ").plus(trackName)
                } else {
                    wholeString = artist.plus(", ").plus(trackName).plus(", ").plus(album)
                }

                findViewById<TextView>(R.id.track_Description).text =
                    wholeString
            }


        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
        PlayandResume()
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
