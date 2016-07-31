package ru.bagrusss.selfchat.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.bagrusss.selfchat.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.floating_menu)
    }
}

