//package com.example.final_app
//
//import android.os.Bundle
//import android.util.Log
//import com.google.android.material.snackbar.Snackbar
//import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
//import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import android.view.Menu
//import android.view.MenuItem
//import android.widget.TextView
//import com.example.finalapp.R
//import com.example.finalapp.databinding.ActivityMainBinding
//import com.spotify.android.appremote.api.ConnectionParams
//import com.spotify.android.appremote.api.SpotifyAppRemote
//import com.spotify.android.appremote.api.Connector
//import com.spotify.protocol.types.Track
//
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding
////    private val ClientID: String = "0e94fc1ee6bf47ff84b2f72bfed235b9"
//    private val ClientID: String = "74d7ce7a3dc24285bade132ac8b23d7b"
//    private var mSpotifyapp: SpotifyAppRemote? = null
////    private val redirectUri = "com.localhost.Spotifyguessinggame://callback"
//    private val redirectUri = "https://localhost/callback/"
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        val connectionParams = ConnectionParams.Builder(ClientID)
//            .setRedirectUri(redirectUri)
//            .showAuthView(true)
//            .build()
//
//        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                mSpotifyapp = appRemote
//                Log.d("MainActivity", "Connected! Yay!")
//                // Now you can start interacting with App Remote
//                connected()
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Log.e("MainActivity", throwable.message, throwable)
//                // Something went wrong when attempting to connect! Handle errors here
//            }
//        })
//    }
//
//    private fun connected() {
//        mSpotifyapp?.let {
//            // Play a playlist
//            val playlistURI = "spotify:album:7ebnxkx8HZNvtTB3me1S9C"
//            it.playerApi.play(playlistURI)
//            it.playerApi.subscribeToPlayerState().setEventCallback {
//                val trackName: String = it.track.name
//                val icon = it.track.imageUri
//
////                Glide.with(this)
////                    .load(icon)
////                    .into(findViewById(R.id.iv_track_icon))
//
//                findViewById<TextView>(R.id.track_Description).text =
//                    trackName
//            }
//
//
//        }
//
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_main,menu)
//
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId){
//            R.id.action_play ->{
//                mSpotifyapp?.let {
//                    it.playerApi.pause()
//
//                }
//                true
//            }
//            else ->super.onOptionsItemSelected(item)
//        }
//
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mSpotifyapp?.let {
//            SpotifyAppRemote.disconnect(it)
//        }
//
//    }
//}