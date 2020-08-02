package com.jeppeman.jetpackplayground.video.presentation.detail

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.snackbar.Snackbar
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.common.presentation.animation.SimpleTransitionListener
import com.jeppeman.jetpackplayground.common.presentation.extensions.*
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import com.jeppeman.jetpackplayground.video.presentation.widget.*
import com.jeppeman.jetpackplayground.video_resources.R
import java.io.Serializable
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import com.jeppeman.jetpackplayground.lib_common_presentation.R as RCommon

internal class ViewPropertyDelegate<T : View>(@IdRes private val id: Int) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = when (thisRef) {
        is Activity -> thisRef.findViewById(id)
        is Fragment -> thisRef.view?.findViewById(id)
        is RecyclerView.ViewHolder -> thisRef.itemView.findViewById(id)
        else -> throw IllegalArgumentException("ViewPropertyDelegate can not be used in class ${thisRef?.javaClass?.name}")
    }
}

internal inline fun <reified T : View, TActivity : Activity> TActivity.view(@IdRes id: Int) = ViewPropertyDelegate<T>(id)
internal inline fun <reified T : View, TFragment : Fragment> TFragment.view(@IdRes id: Int) = ViewPropertyDelegate<T>(id)
internal inline fun <reified T : View, TViewHolder : RecyclerView.ViewHolder> TViewHolder.view(@IdRes id: Int) = ViewPropertyDelegate<T>(id)

@Suppress("UNCHECKED_CAST")
internal class ArgDelegate<T : Any>(private val key: String, private val type: KClass<T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = with(thisRef as Fragment) {
        requireArguments().run {
            when (type) {
                Boolean::class -> getBoolean(key) as T
                Int::class -> getInt(key) as T
                Long::class -> getLong(key) as T
                Short::class -> getShort(key) as T
                Byte::class -> getByte(key) as T
                String::class -> getString(key) as T
                Parcelable::class -> getParcelable<Parcelable>(key) as T
                Serializable::class -> getSerializable(key) as T
                else -> throw IllegalArgumentException("$type is not a supported type for bundles")
            }
        }
    }
}

internal inline fun <reified T : Any, TFragment : Fragment> TFragment.arg(key: String) = ArgDelegate(key, T::class)

class VideoDetailFragment : BaseFragment<VideoDetailViewModel>() {
    private var isLoading = false
    private var loadingFinishedPending = false
    override val layoutRes = R.layout.fragment_video_detail


    private val appBar: ViewGroup? by view(R.id.appBar)
    private val landscapeNonVideoRoot: ViewGroup? by view(R.id.landscapeNonVideoRoot)
    private val landscapeVideoRoot: MotionLayout? by view(R.id.landscapeVideoRoot)
    private val actionContainer: MotionLayout? by view(R.id.actionContainer)
    private val contentContainer: MotionLayout? by view(R.id.contentContainer)
    private val videoContainer: MotionLayout? by view(R.id.videoContainer)
    private val videoOverlay: MotionLayout? by view(R.id.videoOverlay)
    private val videoView: TextureView? by view(R.id.videoView)
    private val close: View? by view(R.id.close)
    private val fastForward: View? by view(R.id.fastForward)
    private val mainAction: PlayButtonFab? by view(R.id.mainAction)
    private val fullScreenPlay: PlayButton? by view(R.id.fullScreenPlay)
    private val fullScreen: View? by view(R.id.fullScreen)
    private val fullScreenClose: View? by view(R.id.fullScreenClose)
    private val fullScreenFastForward: View? by view(R.id.fullScreenFastForward)
    private val fullScreenVolume: ToggleSoundButton? by view(R.id.fullScreenVolume)
    private val collapsingToolbar: CollapsingToolbarLayout? by view(R.id.collapsingToolbar)
    private val coordinator: CoordinatorLayout? by view(R.id.coordinator)
    private val loaderContainer: MotionLayout? by view(R.id.loaderContainer)
    private val arc: ArcView? by view(R.id.arc)
    private val videoDescription: TextView? by view(R.id.videoDescription)
    private val currentProgressText: TextView? by view(R.id.currentProgressText)
    private val videoProgress: SeekBar? by view(R.id.videoProgress)
    private val videoLengthText: TextView? by view(R.id.videoLengthText)
    private val backdropImage: ImageView? by view(R.id.backdropImage)
    private val videoThumb: ImageView? by view(R.id.videoThumb)
    private val rewind: ImageView? by view(R.id.rewind)
    private val fullScreenRewind: ImageView? by view(R.id.fullScreenRewind)

    @Inject
    override lateinit var viewModel: VideoDetailViewModel

    @Inject
    lateinit var appUiContainer: AppUiContainer

    private fun enterFullscreen() {
        if (context?.resources?.getBoolean(R.bool.landscape) == true) {
            appUiContainer.enterFullscreen()
        }
        appBar?.apply {
            clipToPadding = false
            clipChildren = false
        }
        landscapeVideoRoot?.setVisible(true)
        landscapeNonVideoRoot?.setVisible(false)
        actionContainer?.apply {
            loadLayoutDescription(R.xml.video_detail_fab_container_scene)
            setTransition(R.id.end, R.id.fullScreen)
            transitionToEnd()
        }
        contentContainer?.apply {
            setTransition(R.id.start, R.id.end)
            transitionToEnd()
        }
        videoContainer?.apply {
            setTransition(R.id.videoPlaying, R.id.end)
            setTransitionListener(SimpleTransitionListener(onComplete = { _, _ ->
                appUiContainer.enterFullscreen()
                viewModel.onLandscapeTransitionFinished()
            }))
            transitionToEnd()
        }

        landscapeVideoRoot?.transitionToEnd()
    }

    private fun exitFullscreen() {
        appUiContainer.exitFullscreen()
        appBar?.apply {
            clipToPadding = true
            clipChildren = true
        }
        landscapeVideoRoot?.setVisible(false)
        landscapeNonVideoRoot?.setVisible(true)
        actionContainer?.apply {
            setTransition(R.id.end, R.id.start)
            transitionToEnd()
        }
        contentContainer?.apply {
            setTransition(R.id.end, R.id.start)
            transitionToEnd()
        }
    }

    private fun onScreenModeChanged(screenMode: ScreenMode) {
        when (screenMode) {
            ScreenMode.FULLSCREEN -> enterFullscreen()
            else -> exitFullscreen()
        }
    }

    private fun stateChanged(state: VideoDetailViewModel.State) {
        updateStatefulUi(state)

        when (state) {
            is VideoDetailViewModel.InitState -> transitionToStart()
            is VideoDetailViewModel.LoadingState -> transitionToLoading()
            is VideoDetailViewModel.LoadingFinishedState -> transitionToLoadingFinished()
            is VideoDetailViewModel.PlayingState -> transitionToPlaying(state.initial)
            is VideoDetailViewModel.PausedState -> transitionToPaused()
            is VideoDetailViewModel.CompletedState -> transitionToCompleted()
            is VideoDetailViewModel.ErrorState -> transitionToError()
        }
    }

    private fun updateStatefulUi(state: VideoDetailViewModel.State) {
        videoView?.setVisible(state.videoVisible)
        listOf(close, fastForward, fullScreen).forEach { fab ->
            fab?.tag = state.subActionsTag
        }
    }

    private fun toggleOverlay(visible: Boolean) {
        if (visible) {
            videoOverlay?.transitionToEnd()
        } else {
            videoOverlay?.transitionToStart()
        }
    }

    private fun setTitle(title: String) {
        collapsingToolbar?.title = title
    }

    private fun toggleSoundButton(soundOn: Boolean) {
        fullScreenVolume?.soundState = soundOn.asSoundState()
    }

    private fun transitionToError() {
        isLoading = false
        transitionToLoadingFinished()
        coordinator?.let { layout ->
            Snackbar.make(layout, R.string.video_detail_video_failed_to_load, Snackbar.LENGTH_INDEFINITE)
                    .apply {
                        setAction(R.string.video_detail_dismiss_snackbar) { dismiss() }
                        show()
                    }
        }
    }

    private fun transitionToStart() {
        mainAction?.runPauseToPlay()
        fullScreenPlay?.runPauseToPlay()
        videoContainer?.transitionToStart()
        actionContainer?.transitionToStart()
    }

    private fun transitionToLoadingFinished() {
        if (isLoading) {
            loadingFinishedPending = true
            return
        }
        loaderContainer?.apply {
            setTransitionListener(
                    SimpleTransitionListener(
                            onComplete = { _, _ -> viewModel.onLoadingAnimationFinished() }
                    )
            )
            arc?.apply {
                registerLoadingCompleteListener {
                    setTransition(R.id.end, R.id.back)
                    transitionToEnd()
                }
                loading = false
            }
        }
    }

    private fun transitionToPaused() {
        mainAction?.runPauseToPlay()
        fullScreenPlay?.runPauseToPlay()
    }

    private fun transitionToCompleted() {
        mainAction?.markCompleted()
        fullScreenPlay?.markCompleted()
    }

    private fun startLoaderTransition() {
        loaderContainer?.apply {
            setTransition(R.id.start, R.id.end)
            setTransitionListener(SimpleTransitionListener(onComplete = { _, _ ->
                isLoading = false
                arc?.loading = true
                if (loadingFinishedPending) {
                    transitionToLoadingFinished()
                }
            }))
            transitionToEnd()
        }
    }

    private fun transitionToLoading() {
        isLoading = true
        actionContainer?.apply {
            loadLayoutDescription(R.xml.video_detail_fab_fire_scene)
            setTransition(R.id.start, R.id.end)
            var hasStartedLoaderTransition = false
            val actionTransitionListener = SimpleTransitionListener(
                    onChange = { _, _, _, progress ->
                        if (progress >= 0.5 && !hasStartedLoaderTransition) {
                            startLoaderTransition()
                            hasStartedLoaderTransition = true
                        }
                    },
                    onComplete = { _, _ ->
                        if (!hasStartedLoaderTransition) {
                            startLoaderTransition()
                            hasStartedLoaderTransition = true
                        }
                    }
            )
            setTransitionListener(actionTransitionListener)
            transitionToEnd()
        }
    }

    private fun transitionToPlaying(initial: Boolean) {
        if (initial) {
            actionContainer?.apply {
                loadLayoutDescription(R.xml.video_detail_fab_container_scene)
                setTransition(R.id.start, R.id.end)
                transitionToEnd()
            }
            videoContainer?.transitionToState(R.id.videoPlaying)
        }

        mainAction?.runPlayToPause()
        fullScreenPlay?.runPlayToPause()
    }

    private fun progressTextChanged(text: String) {
        currentProgressText?.text = text
    }

    private fun videoLengthChanged(length: Long) {
        videoProgress?.max = length.toInt()
    }

    private fun videoLengthTextChanged(text: String) {
        videoLengthText?.text = text
    }

    private fun progressChanged(progress: Long) {
        videoProgress?.progress = progress.toInt()
    }

    private fun initiateViews() {
        videoDescription?.text = viewModel.video.subtitle
        listOf(backdropImage, videoThumb).forEach { imageView ->
            imageView?.apply {
                transitionName = viewModel.video.id
                setImageUrl(viewModel.video.thumb)
            }
        }
        collapsingToolbar?.setCollapsingToolbarFont(RCommon.font.signatra)

        listOf<View?>(fastForward, fullScreenFastForward).forEach {
            it?.setOnClickListener { viewModel.onFastForwardClick() }
        }
        listOf<View?>(close, fullScreenClose).forEach {
            it?.setOnClickListener { viewModel.onCloseVideoClick() }
        }
        listOf<View?>(mainAction, fullScreenPlay).forEach {
            it?.setOnClickListener { viewModel.onPlayClick() }
        }
        listOf<View?>(rewind, fullScreenRewind).forEach {
            it?.setOnClickListener { viewModel.onRewindClick() }
        }
        fullScreenFastForward?.setOnClickListener { viewModel.onFastForwardClick() }
        fullScreenVolume?.setOnClickListener { viewModel.onVolumeClick() }
        fullScreen?.setOnClickListener { viewModel.onFullscreenClick() }
        videoOverlay?.setOnClickListener { viewModel.onOverlayClick() }
        videoProgress?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.setVideoProgress(progress.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        videoView?.onSurfaceAvailable { surfaceTexture ->
            viewModel.videoDetailPlayer.attachSurface(Surface(surfaceTexture))
        }
    }

    private fun setupListeners() {
        val lifecycleOwner = this
        viewModel.apply {
            currentProgressText.observe(lifecycleOwner, ::progressTextChanged)
            videoLengthText.observe(lifecycleOwner, ::videoLengthTextChanged)
            screenMode.observe(lifecycleOwner, ::onScreenModeChanged)
            videoLength.observe(lifecycleOwner, ::videoLengthChanged)
            videoProgress.observe(lifecycleOwner, ::progressChanged)
            overlayVisible.observe(lifecycleOwner, ::toggleOverlay)
            soundOn.observe(lifecycleOwner, ::toggleSoundButton)
            state.observe(lifecycleOwner, ::stateChanged)
            title.observe(lifecycleOwner, ::setTitle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exitFullscreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.videoDetailPlayer.clearSurface()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.landscape = context.resources.getBoolean(R.bool.landscape)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        initiateViews()

        val sharedElementTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.video_detail_shared_element_transition)

        sharedElementReturnTransition = sharedElementTransition
        sharedElementEnterTransition = sharedElementTransition
    }
}