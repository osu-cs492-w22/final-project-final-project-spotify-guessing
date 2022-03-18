package com.example.finalapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.android.appremote.api.Connector


const val SCORE_PREFIX = "Score: "

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val ClientID: String = "74d7ce7a3dc24285bade132ac8b23d7b"
    private var mSpotifyapp: SpotifyAppRemote? = null
    private val redirectUri = "https://localhost/callback/"

    private var isPlaying = true
    private var timeRemaing = -1
    private var guessesRemaining = 0
    private  var roundsRemaining = 0

    private val timer = object: CountDownTimer(30000,1000){
        override fun onTick(p0: Long) {
            timeRemaing = (p0 / 1000).toInt()
            findViewById<TextView>(R.id.timer_remaining).text = ("Seconds remaining: " + timeRemaing)
        }

        override fun onFinish() {
            stopRound()
        }

    }

    private val rtimer = object: CountDownTimer(5000,1000){
        override fun onTick(p0: Long) {

        }

        override fun onFinish() {
            var test = findViewById<TextView>(R.id.track_Description)
            test.visibility = View.INVISIBLE
            NextTrack()
        }

    }


    private lateinit var guessBoxET: EditText
    lateinit var songTitle: String
    lateinit var songAndArtist : String
    private var userScore: Int = 0
    var totalGuesses: Int = 0

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
        totalGuesses = guesses
        val rounds = sharedPrefs.getInt(
            getString(R.string.pref_rounds_key),
            3
        )
        if (genre != null) {
            Log.d("Genre", genre)
        }

        guessBoxET = findViewById(R.id.et_guess_box)
        findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
        findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, totalGuesses.toString())

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

        guessesRemaining = guesses
        roundsRemaining = rounds

        val guessBtn: Button = findViewById(R.id.btn_guess)
        guessBtn.setOnClickListener {
            var guess = guessBoxET.text.toString().lowercase()
            var correctAnswer = findViewById<TextView>(R.id.track_Description)
            var altCorrectAnswer = songTitle
            var altCorrectAnswerTwo = songAndArtist

            //correctAnswer.visibility = View.INVISIBLE
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

            // For just song title guess
            var altCorrectAnswerStr: String = altCorrectAnswer.lowercase()
            altCorrectAnswerStr = altCorrectAnswerStr.replace("'", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace(",", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace(" ", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace("\n", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace("?", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace("!", "")
            altCorrectAnswerStr = altCorrectAnswerStr.replace(".", "")

            // For just artist and song title guess
            var altCorrectAnswerTwoStr: String = altCorrectAnswerTwo.lowercase()
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace("'", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace(",", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace(" ", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace("\n", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace("?", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace("!", "")
            altCorrectAnswerTwoStr = altCorrectAnswerTwoStr.replace(".", "")

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
                roundsRemaining -= 1
                guessesRemaining = totalGuesses
                NextTrack()
            }
            else if (guess == altCorrectAnswerStr) {
                Log.d("answer", "Correct!!")
                userScore += 5
                findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
                findViewById<EditText>(R.id.et_guess_box).text.clear()
                hideKeyboard()
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    R.string.guess_correct,
                    Snackbar.LENGTH_LONG
                ).show()
                roundsRemaining -= 1
                guessesRemaining = totalGuesses
                NextTrack()
            }
            else if (guess == altCorrectAnswerTwoStr) {
                Log.d("answer", "Correct!!")
                userScore += 7
                findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
                findViewById<EditText>(R.id.et_guess_box).text.clear()
                hideKeyboard()
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    R.string.guess_correct,
                    Snackbar.LENGTH_LONG
                ).show()
                roundsRemaining -= 1
                guessesRemaining = totalGuesses
                NextTrack()
            }
            else {
                Log.d("answer", "incorrect!!")
                findViewById<EditText>(R.id.et_guess_box).text.clear()
                hideKeyboard()
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    R.string.guess_incorrect,
                    Snackbar.LENGTH_LONG
                ).show()
                guessesRemaining -= 1
            }
            // Decrement the number of guesses remaining
            if(guessesRemaining == 0) {
                guessesRemaining = totalGuesses
                roundsRemaining -= 1
                correctAnswer.visibility = View.VISIBLE
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    "Next Round Starting soon",
                    Snackbar.LENGTH_SHORT
                ).show()
                rtimer.start()
                //NextTrack()
            }

            findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, guessesRemaining.toString())

            if(roundsRemaining == 0){
                // Display end of game message
                val endDialog = EndGameDialogFragment(userScore)
                endDialog.show(supportFragmentManager, "game over")
                newGame()
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
            else if (genre == "good music") {
                playlistURI = "spotify:playlist:2s6LAffUTjkLboi1aN8CCq"
            }
            it.playerApi.setShuffle(true)
            it.playerApi.play(playlistURI)
            val random = (0..45).random()
            it.playerApi.seekToRelativePosition((random * 1000).toLong())
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val trackName: String = it.track.name
                songTitle = trackName
                val album: String = it.track.album.name
                val artist: String = it.track.artist.name
                val icon = it.track.imageUri.raw
                songAndArtist = artist.plus(", ").plus(trackName)
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
            R.id.action_info -> {
                Snackbar.make(
                    findViewById(R.id.constraint_layout),
                    "Scoring: Artist, Title, Album = 10pts. Artist, Title = 7pts. Title = 5pts.",
                    Snackbar.LENGTH_LONG
                ).show()
                true
            }
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
                if(isPlaying)
                    item.icon = AppCompatResources.getDrawable(this,R.drawable.action_play)
                else
                    item.icon = AppCompatResources.getDrawable(this,R.drawable.action_pause)

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

    private fun newGame() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val genre = sharedPrefs.getString(
            getString(R.string.pref_playlist_key),
            null
        )
        val guesses = sharedPrefs.getInt(
            getString(R.string.pref_guesses_key),
            3

        )
        totalGuesses = guesses
        val rounds = sharedPrefs.getInt(
            getString(R.string.pref_rounds_key),
            3
        )
        if (genre != null) {
            Log.d("Genre", genre)
        }

        userScore = 0

        guessBoxET = findViewById(R.id.et_guess_box)
        findViewById<TextView>(R.id.tv_user_score).text = "$SCORE_PREFIX$userScore"
        findViewById<TextView>(R.id.guesses_remaining).text = getString(R.string.num_guesses, totalGuesses.toString())
        guessesRemaining = totalGuesses
        roundsRemaining = rounds

        if (genre != null) {
            connected(genre)
        }
    }
    override fun onResume() {
        super.onResume()
        newGame()
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
