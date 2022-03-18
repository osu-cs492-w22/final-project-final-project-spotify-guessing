package com.example.finalapp

import android.app.Activity
import android.app.Activity.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.finalapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector
import com.spotify.protocol.types.Image
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import org.w3c.dom.Text
import java.util.*

const val SCORE_PREFIX = "Score: "

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val ClientID: String = "74d7ce7a3dc24285bade132ac8b23d7b"
    private var mSpotifyapp: SpotifyAppRemote? = null
    private val redirectUri = "https://localhost/callback/"

    private var isPlaying = true
    private var timeRemaing = -1
    private val timer = object: CountDownTimer(30000,1000){
        override fun onTick(p0: Long) {
            timeRemaing = (p0 / 1000).toInt()
            findViewById<TextView>(R.id.timer_remaining).text = ("Seconds remaining: " + timeRemaing)
        }

        override fun onFinish() {
            stopRound()
        }

    }

    private lateinit var guessBoxET: EditText
    private var userScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Store default game settings
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val genre = sharedPrefs.getString(
            getString(R.string.pref_playlist_key),
            null
        )
        val guesses = sharedPrefs.getInt(
            getString(R.string.pref_guesses_key),
            3

        )
        val rounds = sharedPrefs.getInt(
            getString(R.string.pref_rounds_key),
            3
        )
        if (genre != null) {
            Log.d("Genre", genre)
        }

        guessBoxET = findViewById(R.id.et_guess_box)
        findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
        findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, guesses.toString())

        val connectionParams = ConnectionParams.Builder(ClientID)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                mSpotifyapp = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                if (genre != null) {
                    connected(genre)
                }
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })

        var guessesRemaining = guesses

        val guessBtn: Button = findViewById(R.id.btn_guess)
        guessBtn.setOnClickListener {
            var guess = guessBoxET.text.toString().lowercase()
            var correctAnswer = findViewById<TextView>(R.id.track_Description)
            correctAnswer.visibility = View.VISIBLE
            var correctAnswerStr: String = correctAnswer.text.toString().lowercase()
            // Remove white spacing, commas, apostrophes to help prevent "wrong" answers with slightly off grammar
            // For correct answer
            correctAnswerStr = correctAnswerStr.replace("'", "")
            correctAnswerStr = correctAnswerStr.replace(",", "")
            correctAnswerStr = correctAnswerStr.replace(" ", "")
            correctAnswerStr = correctAnswerStr.replace("\n", "")
            correctAnswerStr = correctAnswerStr.replace("?", "")
            correctAnswerStr = correctAnswerStr.replace("!", "")
            correctAnswerStr = correctAnswerStr.replace(".", "")
            // For user's guess
            guess = guess.replace("'", "")
            guess = guess.replace(",", "")
            guess = guess.replace(" ", "")
            guess = guess.replace("\n", "")
            guess = guess.replace("?", "")
            guess = guess.replace("!", "")
            guess = guess.replace(".", "")
            Log.d("answer", guess)
            Log.d("answer", correctAnswerStr)
            if (guess == correctAnswerStr) {
                Log.d("answer", "Correct!!")
                userScore += 10
                findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
                findViewById<EditText>(R.id.et_guess_box).text.clear()
                hideKeyboard()
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    R.string.guess_correct,
                    Snackbar.LENGTH_LONG
                ).show()
//                mSpotifyapp?.let { it.playerApi.skipNext() }
                NextTrack()
            } else {
                Log.d("answer", "incorrect!!")
                findViewById<EditText>(R.id.et_guess_box).text.clear()
                hideKeyboard()
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    R.string.guess_incorrect,
                    Snackbar.LENGTH_LONG
                ).show()
            }
            // Decrement the number of guesses remaining
            if(guessesRemaining > 1) {
                guessesRemaining -= 1
                findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, guessesRemaining.toString())
            } else {
                Log.d("game over", "reset game")
            }
        }
    }

    private fun connected(genre : String) {
        mSpotifyapp?.let {
            var playlistURI = "spotify:playlist:37i9dQZF1DZ06evO4BaAkp"

            // Play a playlist
            if (genre == "rock"){
                playlistURI = "spotify:playlist:37i9dQZF1EQpj7X7UK8OOF"
            }
            else if (genre == "pop") {
                playlistURI = "spotify:playlist:37i9dQZF1EQncLwOalG3K7"
            }
            else if (genre == "country") {
                playlistURI = "spotify:playlist:14oOrIpv9kuNm2lRcJwQdY"
            }
            else if (genre == "rap") {
                playlistURI = "spotify:playlist:1tSgd8fWqF5fgMbEpXsPG3"
            }
            it.playerApi.setShuffle(true)
            it.playerApi.play(playlistURI)
            val random = (0..45).random()
            it.playerApi.seekToRelativePosition((random * 1000).toLong())
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
        timer.start()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.action_share -> {
                shareScore()
                true
            }
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

    override fun onResume() {
        super.onResume()
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val genre = sharedPrefs.getString(
            getString(R.string.pref_playlist_key),
            null
        )
        val guesses = sharedPrefs.getInt(
            getString(R.string.pref_guesses_key),
            3

        )
        val rounds = sharedPrefs.getInt(
            getString(R.string.pref_rounds_key),
            3
        )
        if (genre != null) {
            Log.d("Genre", genre)
        }

        guessBoxET = findViewById(R.id.et_guess_box)
        findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
        findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, guesses.toString())

        if (genre != null) {
            connected(genre)
        }



    }

    private fun shareScore() {
        val shareScore = "Score: $userScore"
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareScore)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(intent, null))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRound()
        timer.cancel()
        mSpotifyapp?.let {
            SpotifyAppRemote.disconnect(it)
        }
        //PlayandResume()
    }

    private fun stopRound(){
        mSpotifyapp?.let {
            it.playerApi.pause()
        }
        isPlaying = false;
    }

    private fun NextTrack(){
        mSpotifyapp?.let {
            it.playerApi.skipNext()
            timer.start()
        }
    }

    private fun PlayandResume()
    {
        mSpotifyapp?.let {

            if(isPlaying) {
                it.playerApi.pause()
                isPlaying = false
                timer.cancel()
            }
            else
            {
                it.playerApi.resume()
                isPlaying = true
                timer.start()
            }
        }
    }

    // https://stackoverflow.com/questions/41790357/close-hide-the-android-soft-keyboard-with-kotlin
    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
