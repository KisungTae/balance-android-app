package com.beeswork.balance.ui.loginactivity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.beeswork.balance.R
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.databinding.ActivityLoginBinding
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.common.BaseActivity
import com.beeswork.balance.ui.dialog.ErrorDialog
import com.beeswork.balance.ui.mainactivity.MainActivity
import com.beeswork.balance.ui.stepprofileactivity.StepProfileActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.threeten.bp.OffsetDateTime


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
                showError(ExceptionCode.INVALID_SOCIAL_LOGIN_EXCEPTION, null)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

//        viewModel.mockSocialLogin()
//        moveToMainActivity()
        window?.statusBarColor = ContextCompat.getColor(this, R.color.Primary)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        bind()
//        setupGoogleSignIn()
//        observeLoginLiveData()
        val error = intent.getStringExtra(BundleKey.ERROR)
        val errorMessage = intent.getStringExtra(BundleKey.ERROR_MESSAGE)

        println("from login activity error: $error")
        println("from login activity error message: $errorMessage")
    }

    private fun observeLoginLiveData() {
        viewModel.loginLiveData.observe(this) {
            when (it.status) {
                Resource.Status.ERROR -> showError(it.error, it.errorMessage)
                Resource.Status.SUCCESS -> it.data?.let { data ->
                    if (data.profileExists) moveToMainActivity()
                    else moveToStepProfileActivity()
                }
                else -> println()
            }
        }
    }

    private fun moveToMainActivity() {
        finishToActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    private fun moveToStepProfileActivity() {
        finishToActivity(Intent(this@LoginActivity, StepProfileActivity::class.java))
    }

    private fun showError(error: String?, errorMessage: String?) {
        val errorTitle = getString(R.string.error_title_login)
        ErrorDialog(error, errorTitle, errorMessage).show(supportFragmentManager, ErrorDialog.TAG)
    }

    private fun bind() = lifecycleScope.launch {
        binding.btnGoogleSignIn.setOnClickListener {
            signInWithGoogleActivityResult.launch(mGoogleSignInClient.signInIntent)
        }
    }

    // refresh token, validate jwt token in splahsactivity, logout, check if login with different account, then remove data

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }
}