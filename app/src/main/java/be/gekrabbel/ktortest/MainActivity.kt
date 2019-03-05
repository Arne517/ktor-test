package be.gekrabbel.ktortest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.HttpClient
import io.ktor.client.call.HttpClientCall
import io.ktor.client.call.call
import io.ktor.client.call.receive
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.BadResponseStatusException
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    private val httpClient: HttpClient by lazy {
        HttpClient(OkHttp) {

            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launch {
            callWorks()
            callDoesntWork()
        }
    }

    private suspend fun callWorks() {
        val call: HttpClientCall = httpClient.call {
            method = HttpMethod.Post

            url(Url("https://google.com"))

            body = "Test"
        }

        val response: String = try {
            call.response.receive()
        } catch (e: BadResponseStatusException) {
            "Error ${e.statusCode.value}"
        }
        println(response)
    }

    private suspend fun callDoesntWork() {
        val call: HttpClientCall = httpClient.call {
            method = HttpMethod.Post

            url(Url("https://google.com"))

            body = MyCustomObject(message = "Hello World")
        }

        val response: String = try {
            call.response.receive()
        } catch (e: BadResponseStatusException) {
            "Error ${e.statusCode.value}"
        }
        println(response)
    }
}

data class MyCustomObject(val message: String)