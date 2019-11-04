
[![CircleCI](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master.svg?style=svg)](https://circleci.com/gh/jeppeman/android-jetpack-playground/tree/master)
# android-jetpack-playground

A small video player pet project with the purpose of exploring cutting edge Android development.
Some areas of exploration:
* Single activity setup with dynamic feature modules
* MotionLayout
* Coroutines
* Jetpack testing, mainly isolated fragment unit tests that run both on device and the JVM with the same source code

Single activity setup with dynamic feature modules
---
<img src="https://raw.githubusercontent.com/jeppeman/jetpackplayground-media/master/dynamic_install.gif" width="224" height="400" />

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
Getting the instrumented tests to run on Travis was pretty annoying though, but that's a different story. <br />
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
