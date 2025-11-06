package com.example.chat_app.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.chat_app.R
import android.view.View
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chat_app.data.db.remote.FirebaseDataSource
import com.example.chat_app.data.db.repository.CloudinaryConfig
import com.example.chat_app.util.forceHideKeyboard
import com.squareup.picasso.LruCache
import com.squareup.picasso.Picasso
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var imagesCacheDir: File  // Thư mục lưu ảnh vĩnh viễn
    }
    private lateinit var navView : BottomNavigationView
    private lateinit var mainProgressBar: ProgressBar
    private lateinit var mainToolbar: Toolbar
    private lateinit var notificationsBadge: BadgeDrawable

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainToolbar= findViewById(R.id.main_toolbar)
        mainProgressBar=findViewById(R.id.main_progressBar)
        navView=findViewById(R.id.nav_view)

        notificationsBadge=navView.getOrCreateBadge(R.id.navigation_notifications).apply { isVisible = false }

        setSupportActionBar(mainToolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener{_,destination, _  ->

            when(destination.id){
                R.id.profileFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.chatFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.startFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.loginFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.createAccountFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.keyFragment -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.showImageFragment -> {
                    supportActionBar?.show()
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                R.id.previewImageFragment -> {
                    supportActionBar?.show()
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()      // HIỆN toolbar
                    mainToolbar.visibility = View.VISIBLE
                    navView.visibility = View.VISIBLE
                }
            }
            showGlobalProgressBar(false)
            currentFocus?.rootView?.forceHideKeyboard()
        }

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_chats,
            R.id.navigation_notifications,
            R.id.navigation_users,
            R.id.navigation_friends,
            R.id.navigation_settings,
            R.id.startFragment,
            R.id.keyFragment
        ))

        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)

        CloudinaryConfig.init(applicationContext)
        imagesCacheDir = File(getExternalFilesDir(null), "images_cache")
        if (!imagesCacheDir.exists()) {
            imagesCacheDir.mkdirs()
            Log.d("ImageCache", "Created cache dir: ${imagesCacheDir.absolutePath}")
        }
        val picasso = Picasso.Builder(this)
            .memoryCache(LruCache(50 * 1024 * 1024)) // 50MB memory cache
            .build()
        Picasso.setSingletonInstance(picasso)
    }

    override fun onPause() {
        super.onPause()
        FirebaseDataSource.dbInstance.goOffline()
    }

    override fun onResume() {
        FirebaseDataSource.dbInstance.goOnline()
        super.onResume()
        setupViewModelObservers()
    }

    private fun setupViewModelObservers(){
        viewModel.userNotificationsList.observe(this, {
            Log.d("MainActivity", "userNotificationsList changed: $it")
            if (it != null) {
                if (it.size > 0) {
                    notificationsBadge.number = it.size
                    notificationsBadge.isVisible = true
                    Log.d("MainActivity", "Badge updated: ${it.size} notifications")
                } else {
                    notificationsBadge.isVisible = false
                }
            }
        })
    }

    fun showGlobalProgressBar(show: Boolean) {
        if (show) mainProgressBar.visibility = View.VISIBLE
        else mainProgressBar.visibility = View.GONE
    }




}