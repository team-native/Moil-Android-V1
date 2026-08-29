package com.example.moil.feature.image.module.data.remote

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class ImageApiServiceContractTest {
    private val mockWebServer = MockWebServer()
    private val json = Json { ignoreUnknownKeys = true }
    private lateinit var imageApiService: ImageApiService

    @Before
    fun setUp() {
        mockWebServer.start()
        imageApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ImageApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `프로필 이미지 업로드는 image multipart field를 사용한다`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body("{\"success\":true,\"status\":0,\"message\":\"ok\",\"data\":{\"imagePath\":\"/image/avatar\"}}")
                .build(),
        )

        imageApiService.uploadProfileImage(
            MultipartBody.Part.createFormData(
                "image",
                "avatar.jpg",
                "image-bytes".toRequestBody("image/jpeg".toMediaType()),
            ),
        )

        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/image-upload", request.target)
        assertTrue(request.headers["Content-Type"].orEmpty().startsWith("multipart/form-data"))
        assertTrue(request.body?.utf8().orEmpty().contains("name=\"image\""))
        assertTrue(request.body?.utf8().orEmpty().contains("filename=\"avatar.jpg\""))
    }
}
