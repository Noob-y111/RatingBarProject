package com.example.ratingbarproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.setTouchOverListener(object : RatingBar.TouchOver {
            override fun touchOver(percent: Float) {
                val nPercent = "${(percent * 100).toInt()}%"
                Log.d(TAG, "touchOver: nPercent ==> $nPercent")
            }
        })

        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener {
            ratingBar.setLightColor(Color.RED)
        }
    }
}