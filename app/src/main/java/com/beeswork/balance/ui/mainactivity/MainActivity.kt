package com.beeswork.balance.ui.mainactivity

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.common.BaseLocationActivity
import com.beeswork.balance.ui.mainviewpagerfragment.MainViewPagerFragment


class MainActivity : BaseLocationActivity(true) {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private val viewModelFactory: MainViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindUI()
    }

    private fun bindUI() = lifecycleScope.launch {
        observeWebSocketEventUIStateLiveData()
        supportFragmentManager.beginTransaction().add(R.id.fcvMain, MainViewPagerFragment()).commit()
    }

    private suspend fun observeWebSocketEventUIStateLiveData() {
        viewModel.webSocketEventUIStateLiveData.await().observe(this) { webSocketEventUIState ->
            if (webSocketEventUIState.shouldLogout) {
                val message = MessageSource.getMessage(this, webSocketEventUIState.exception)
                Navigator.finishToLoginActivity(this, message)
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        viewModel.connectStomp()
    }

    override fun onPause() {
        super.onPause()
//        viewModel.disconnectStomp()
    }

}



