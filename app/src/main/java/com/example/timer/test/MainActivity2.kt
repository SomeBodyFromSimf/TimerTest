package com.example.timer.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        findViewById<Button>(R.id.go_back).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.start_new).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}