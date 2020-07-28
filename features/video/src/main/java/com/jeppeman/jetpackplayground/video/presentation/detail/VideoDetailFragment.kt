package com.jeppeman.jetpackplayground.video.presentation.detail

import android.content.Context
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.widget.SeekBar
import androidx.transition.TransitionInflater
import com.google.android.material.snackbar.Snackbar
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.common.presentation.animation.SimpleTransitionListener
import com.jeppeman.jetpackplayground.common.presentation.extensions.*
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import com.jeppeman.jetpackplayground.video.presentation.widget.asSoundState
import kotlinx.android.synthetic.main.fragment_video_detail.*
import kotlinx.android.synthetic.main.fragment_video_detail.view.*
import javax.inject.Inject
import com.jeppeman.jetpackplayground.lib_common_presentation.R as RCommon

class VideoDetailFragment : BaseFragment<VideoDetailViewModel>() {
    private var isLoading = false
    private var loadingFinishedPending = false
    override val layoutRes = R.layout.fragment_video_detail

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