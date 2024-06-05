package com.example.timer.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.go_with_finish).setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
            finish()
        }
        findViewById<Button>(R.id.go).setOnClickListener {
            startActivity(Intent(this, MainActivity2::class.java))
        }
    }
}