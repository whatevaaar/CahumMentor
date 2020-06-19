package com.cahum.mentor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.cahum.mentor.control.DBManager

class Menu : AppCompatActivity() {
    val dbManager= DBManager()
    private lateinit var botonMenu: Button
    private lateinit var botonChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    private fun clickMenu(){}

}
