package com.jeppeman.jetpackplayground.video

import android.os.AsyncTask
import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.appComponent
import com.jeppeman.jetpackplayground.video.platform.di.DaggerMockAppComponent
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest

class MockApplication : MainApplication() {
    override fun inject() {
        appComponent = DaggerMockAppComponent.factory().create(this)
        appComponent.inject(this)
    }

    companion object {
        val mockWebServer: MockWebServer = MockWebServer().apply {
            dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return when (request.path) {
                        "/gtv-videos-bucket/sample/videos-enhanced-c.json" -> MockResponse().setResponseCode(200).setBody(VIDEOS_RESPONSE_200)
                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        init {
            AsyncTask.execute {
                mockWebServer.start()
            }
        }
    }
}