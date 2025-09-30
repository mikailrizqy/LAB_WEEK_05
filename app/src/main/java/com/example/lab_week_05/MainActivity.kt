package com.example.lab_week_05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView // <-- PERBAIKAN 1: Tambahkan import ini
import android.widget.TextView
import com.example.lab_week_05.api.CatApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import com.example.lab_week_05.model.ImageData
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    private val apiResponseView: TextView by lazy {
        findViewById(R.id.api_response)
    }

    private val imageResultView: ImageView by lazy {
        findViewById(R.id.image_result)
    }

    // PERBAIKAN 2: Pastikan file com.example.lab_week_05.ImageLoader.kt dan com.example.lab_week_05.GlideLoader.kt ada di package yang benar
    private val imageLoader: ImageLoader by lazy {
        GlideLoader(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCatImageResponse()
    }

    private fun getCatImageResponse() {
        val call = catApiService.searchImages(1, "full")
        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
            }

            override fun onResponse(
                call: Call<List<ImageData>>,
                response: Response<List<ImageData>>
            ) {
                if (response.isSuccessful) {
                    val images = response.body()
                    val firstImageObject = images?.firstOrNull()

                    // 1. Tetap tampilkan gambar menggunakan Glide
                    val imageUrl = firstImageObject?.imageUrl.orEmpty()
                    if (imageUrl.isNotBlank()) {
                        imageLoader.loadImage(imageUrl, imageResultView)
                    }

                    // --- BAGIAN KODE UNTUK ASSIGNMENT ---

                    // 2. Ambil nama ras dari objek. Jika tidak ada, gunakan "Unknown"
                    val catBreed = firstImageObject?.breeds?.firstOrNull()?.name ?: "Unknown"

                    // 3. Tampilkan nama ras di TextView
                    apiResponseView.text = getString(R.string.image_placeholder, catBreed)

                } else {
                    Log.e(
                        MAIN_ACTIVITY, "Failed to get response\n" +
                                response.errorBody()?.string().orEmpty()
                    )
                }
            }
        })
    }
}