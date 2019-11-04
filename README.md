
[![CircleCI](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master.svg?style=svg)](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master)

<a href='https://play.google.com/store/apps/details?id=com.jeppeman.jetpackplayground&gl=SE&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img height="40" width="123" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png'/></a>

# android-jetpack-playground

A small video player pet project with the purpose of exploring cutting edge Android development.
Some areas of exploration:
* Dynamic Feature Modules with Fragments as the UI entry point
* MotionLayout
* Coroutines
* Jetpack testing, mainly isolated fragment unit tests that run both on device and the JVM with the same source code

Dynamic Feature Modules with Fragments as the UI entry point
---
With dynamic features, and feature modularisation in general on Android, the most common pattern for top level navigation seems to be firing intents in on one way or another: implicitly, manually declaring the fully qualified name of the activity class, or app links. This makes sense in most cases and allows for nice decoupling, however, I wanted to see if the top level navigation from dynamic feature to dynamic feature could be fragment based, that is, having a single activity application and the UI entry point to each dynamic feature be a fragment. This would allow for a more seamless user experience as we could take full control over the transitions from screen to screen. Below is a gif of how it turned out:

<img src="https://raw.githubusercontent.com/jeppeman/jetpackplayground-media/master/dynamic_install.gif" width="224" height="400" />

When we try to navigate to the Video feature we detect that it is not installed, we therefore install it and immediately after navigate to it by gaining access to it's UI entry point. The experience becomes a lot more seamless than it would have been with activities.

## The setup
The entry point of each dynamic feature is registered in a common library module as an interface, which looks like this:
```kotlin
interface Feature<T> {
    fun getEntryPoint(): Fragment
    fun inject(dependencies: T)
}
```
`getEntryPoint()` will return the `Fragment` that is the UI entrypoint for the feature, and `inject()` will provide the feature with it's necessary dependencies.
All dynamic feature module definitions must then be an extension of this interface, the video feature in this project is defined as follows in the common library module:
```kotlin
// In the common library module
interface VideoFeature : Feature<VideoFeature.Dependencies> {
    interface Dependencies {
        val okHttpClient: OkHttpClient
        val context: Context
        val handler: Handler
        val backgroundDispatcher: CoroutineDispatcher
    }
}
```
The implementation of this interface will then reside in the actual dynamic feature module, for the video feature it looks like this:
```kotlin
// In the actual dynamic feature module
class VideoFeatureImpl : VideoFeature {
    override fun getEntryPoint(): Fragment = createVideoFragment()

    override fun inject(dependencies: VideoFeature.Dependencies) {
        videoComponent = DaggerVideoComponent.factory()
                .create(dependencies, this)
    }
}
```
Before navigating to a feature we need to check whether it is installed first, for this I've created a class called `FeatureManager`, which esentially wraps `SplitInstallManager` from the play core library and exposes API:s to interact with features based on their types, it looks like this:
```kotlin
interface FeatureManager {
    fun <T : Feature<D>, D> getFeature(featureType: KClass<T>, dependencies: D): T?
    fun <T : Feature<*>> installFeature(featureType: KClass<T>, onStateUpdate: (InstallState) -> Unit)
    fun <T : Feature<*>> isFeatureInstalled(featureType: KClass<T>): Boolean

    sealed class InstallState(val featureInfo: Feature.Info) {
        class Downloading(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installing(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class RequiresUserConfirmation(val sender: IntentSender?, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Failed(val code: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installed(featureInfo: Feature.Info) : InstallState(featureInfo)
    }
}
```
So if we want to navigate to the Video feature we could do something like the following:
```kotlin 
val isVideoInstalled = featureManager.isFeatureInstalled<VideoFeature>()
if (isVideoInstalled) {
    val feature = featureManager.getFeature<VideoFeature, VideoFeature.Dependencies>(dependencies)
    val featureFragment: Fragment = feature.getEntryPoint()
    addFragment(featureFragment)
} else {
    featureManager.installFeature<VideoFeature> { state: FeatureManager.InstallState ->
        ...
    }
}
```
A dynamic feature module can not be declared as a dependency from any other module, therefore `VideoFeatureImpl` can not be instantiated through normal means from anywhere outsite of the video dynamic feature module. So how does the `FeatureManager` then create instances of the features? It can do it through either reflection, or using a <a href="https://developer.android.com/reference/java/util/ServiceLoader">`ServiceLoader`</a>. The latter has the nice benefit of removing reflection from the runtime; newer versions of `R8` (Google's new code shrinker) will try to make a compiler optimization which replaces calls to `ServiceLoader.load(VideoFeature::class.java)` with `Arrays.asList(new VideoFeatureImpl())`, you can find the `R8` source code which does this <a href="https://r8.googlesource.com/r8/+/b027e3c0b123bd4a9397ff210e40293c1381d1a8/src/main/java/com/android/tools/r8/ir/optimize/ServiceLoaderRewriter.java">here</a>.

MotionLayout
---
This is a really nice tool, complex animations can be created in a fairly simple and declarative way.
The editor is also available in the latest canary version of Android Studio, that should help with a
lot of the pain points right now, such as the slow workflow when declaring KeyFrames. Not sure
if it is worth investing that much time into this tool given that it doesn't seem to be very compatible
with Jetpack Compose (which appears to be the future), then again, I'm sure that is something they're
aware of at Google and are trying to solve given how much time and resources they've seem to have
poured in to this.
Below are a few silly animations from the project that showcases MotionLayout.

<a target="_blank" href="https://www.youtube.com/watch?v=wGbnyM_hJSQ"><img src="https://raw.githubusercontent.com/jeppeman/jetpackplayground-media/master/loader.gif" width="224" height="400" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href="https://www.youtube.com/watch?v=mcBy2lza8zM"><img src="https://raw.githubusercontent.com/jeppeman/jetpackplayground-media/master/fullscreen.gif" width="224" height="400" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href="https://www.youtube.com/watch?v=UZVqhDmo8M4"><img src="https://raw.githubusercontent.com/jeppeman/jetpackplayground-media/master/panel.gif" width="224" height="400" /></a>

Isolated fragment testing for both on-device and JVM with the same source code
---
After having heard of the write-once-run-everywhere ambitions from the Google IO testing presentations I was very excited.
Although Nitrogen is not released yet, I really wanted to take Robolectric 4.0 out for a spin.
My ambition was to have fragment unit tests in a shared test folder that would run both instrumented
and with Robolectric; since I have some fairly complex UI with animations and orientation changes
in the project I thought this would be a tall order, but it was actually achievable in the end with
some tinkering. I needed to create a custom shadow for `MotionLayout`
(<a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/test/java/com/jeppeman/jetpackplayground/shadows/ShadowMotionLayout.kt">here</a>)
in order to make it work with Robolectric, but apart from that it was mostly smooth sailing.
Isolating fragment tests has also been quite messy historically, but the new `FragmentScenario` simplifies
it substantially. Here is an example of a fragment unit test from the project (runs on both JVM and device):
```kotlin
@Test
fun whenPlaying_clickFastForward_ShouldDelegateToViewModel() {
    launch {
        whenever(mockPlayingState.initial).thenReturn(true)
        whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
    }

    onView(withId(R.id.fastForward)).check(matches(isVisibleToUser())).perform(click())

    verify(viewModel).onFastForwardClick()
}
```
`launch` is a helper method that calls the new `FragmentScenario.launchInContainer()` under the hood.
 The source can be found <a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/sharedTest/java/com/jeppeman/jetpackplayground/ui/base/BaseFragmentTest.kt">here</a> and <a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/sharedTest/java/com/jeppeman/jetpackplayground/ui/videodetail/VideoDetailFragmentTest.kt">here</a>.
