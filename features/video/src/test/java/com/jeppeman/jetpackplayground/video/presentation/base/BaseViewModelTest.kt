package com.jeppeman.jetpackplayground.video.presentation.base

import com.jeppeman.jetpackplayground.common.presentation.BaseViewModel
import com.jeppeman.jetpackplayground.video.presentation.util.invokeMethod
import kotlinx.coroutines.*
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

abstract class BaseViewModelTest<TViewModel : BaseViewModel> {

    private lateinit var spyViewModel: TViewModel
    protected abstract val viewModel: TViewModel

    abstract fun before()

    protected fun TViewModel.onInitialize() = invokeMethod("onInitialize")
    protected fun TViewModel.onCreate() = invokeMethod("onCreate")
    protected fun TViewModel.onStart() = invokeMethod("onStart")
    protected fun TViewModel.onResume() = invokeMethod("onResume")
    protected fun TViewModel.onPause() = invokeMethod("onPause")
    protected fun TViewModel.onStop() = invokeMethod("onStop")
    protected fun TViewModel.onDestroy() = invokeMethod("onDestroy")
    protected fun TViewModel.onCleared() = invokeMethod("onCleared")

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Before
    fun setUp() {
        before()
        val testDispatcher = object : CoroutineDispatcher(), Delay {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                block.run()
            }

            override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
                continuation.resume(Unit)
            }

        }

        Dispatchers.setMain(testDispatcher)
        spyViewModel = spy(viewModel)
    }

    @Test
    fun firstOnCreate_ShouldTriggerOnInitialize() {
        spyViewModel.onCreate()

        verify(spyViewModel).onInitialize()
    }

    @Test
    fun secondOnCreate_ShouldNotTriggerOnInitialize() {
        spyViewModel.onCreate()
        spyViewModel.onCreate()

        verify(spyViewModel, times(1)).onInitialize()
    }
}