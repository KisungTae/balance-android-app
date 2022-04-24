package com.beeswork.balance.ui.loginactivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.databinding.ActivityLoginBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.exception.InvalidSocialLoginException
import com.beeswork.balance.internal.util.MessageSource
import com.beeswork.balance.internal.util.Navigator
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainactivity.MainActivity
import com.beeswork.balance.ui.registeractivity.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class LoginActivity : BaseActivity(), KodeinAware {
    override val kodein by closestKodein()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val viewModelFactory: LoginViewModelFactory by instance()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val signInWithGoogleActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result.data?.let { intent ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                viewModel.socialLogin(account.id, account.idToken, LoginType.GOOGLE)
            } catch (e: ApiException) {
                showLoginErrorDialog(InvalidSocialLoginException())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind()
        setupGoogleSignIn()
        showLoginErrorMessage()
    }

    private fun bind() = lifecycleScope.launch {
        observeLoginLiveData()
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogleActivityResult.launch(mGoogleSignInClient.signInIntent)
        }
    }

    private fun showLoginErrorMessage() {
        val message = intent.getStringExtra(BundleKey.ERROR_MESSAGE)
        if (message != null) {
            val title = getString(R.string.error_title_invalid_login)
            ErrorDialog.show(title, message, supportFragmentManager)
        }
    }

    private fun observeLoginLiveData() {
        viewModel.loginLiveData.observe(this) { uiState ->
            if (uiState.shouldLogin) {
                showLoginErrorDialog(uiState.exception)
            } else if (!uiState.profileExists) {
                Navigator.finishToActivity(this@LoginActivity, RegisterActivity::class.java)
            } else {
                Navigator.finishToActivity(this@LoginActivity, MainActivity::class.java)
            }
        }
    }

    private fun showLoginErrorDialog(exception: Throwable?) {
        val title = getString(R.string.error_title_login)
        val message = MessageSource.getMessage(this, exception)
        ErrorDialog.show(title, message, supportFragmentManager)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}