package ru.bagrusss.selfchat.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import org.jetbrains.anko.find
import org.jetbrains.anko.inputMethodManager
import ru.bagrusss.selfchat.R

class ChatActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    var mFabMenu: FloatingActionMenu? = null
    var mFabGeo: FloatingActionButton? = null
    var mFabAlbum: FloatingActionButton? = null
    var mFabCamera: FloatingActionButton? = null
    var mFabText: FloatingActionButton? = null

    var mSendButton: ImageView? = null
    var mTextMessage: EditText? = null
    var mMessageView: View? = null

    var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mFabMenu = find(R.id.fab_menu)

        mFabGeo = find(R.id.fab_geo)
        mFabAlbum = find(R.id.fab_album)
        mFabCamera = find(R.id.fab_camera)
        mFabText = find(R.id.fab_text)
        mRecyclerView = find(R.id.chat_recycler)

        mFabGeo?.setOnClickListener(this)
        mFabAlbum?.setOnClickListener(this)
        mFabCamera?.setOnClickListener(this)
        mFabText?.setOnClickListener(this)

        mSendButton = find(R.id.send_button)
        mTextMessage = find(R.id.text_message)

        mTextMessage?.addTextChangedListener(this)
        mSendButton?.setOnClickListener {
            sendMessage(mTextMessage?.text.toString())
            mMessageView?.visibility = View.GONE
            mFabMenu?.showMenu(true)
            val imm = inputMethodManager
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }

        mMessageView = find(R.id.message_view)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_geo -> {

            }
            R.id.fab_album -> {

            }
            R.id.fab_camera -> {

            }
            R.id.fab_text -> {
                mMessageView?.visibility = View.VISIBLE
                mFabMenu?.hideMenu(true)
                mTextMessage?.requestFocus()
                val imm = inputMethodManager
                imm.showSoftInput(mTextMessage, 0)
            }
        }
        mFabMenu?.close(true)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    private fun sendMessage(msg: String) {

    }

    override fun onBackPressed() {

    }

}

