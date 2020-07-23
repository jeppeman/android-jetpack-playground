package com.jeppeman.jetpackplayground.common.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jeppeman.globallydynamic.globalsplitcompat.GlobalSplitCompat
import dagger.android.support.AndroidSupportInjection

abstract class BaseFragment<TViewModel : LifecycleAwareCoroutineViewModel> : Fragment() {

    @get:LayoutRes
    protected abstract val layoutRes: Int
    abstract val viewModel: TViewModel

    protected open fun inject() {
        try {
            AndroidSupportInjection.inject(this)
        } catch (exception: Exception) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        GlobalSplitCompat.install(context)
//        Log.e("Yolo", this::class.java.canonicalName)
        return inflater.inflate(layoutRes, container, false)
    }
}