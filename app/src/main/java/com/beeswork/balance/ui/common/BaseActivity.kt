package com.beeswork.balance.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beeswork.balance.ui.loginactivity.LoginActivity
import com.beeswork.balance.ui.stepprofileactivity.StepProfileActivity

abstract class BaseActivity: AppCompatActivity() {


    fun finishToActivity(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.finish()
    }

    fun moveToLoginActivity() {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()
    }

//    fun moveToActivity(intent: Intent) {
//        println("moveToLoginActivity")
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        startActivity(intent)
//        this.finish()
//
//    }


}