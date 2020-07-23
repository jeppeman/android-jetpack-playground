package com.jeppeman.jetpackplayground.common.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jeppeman.globallydynamic.globalsplitcompat.GlobalSplitCompat

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalSplitCompat.installActivity(this)
    }
}