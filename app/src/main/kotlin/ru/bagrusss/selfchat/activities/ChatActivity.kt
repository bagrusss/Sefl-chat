package ru.bagrusss.selfchat.activities

import android.app.LoaderManager
import android.app.ProgressDialog
import android.content.Context
import android.content.CursorLoader
import android.content.Intent
import android.content.Loader
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.*
import ru.bagrusss.selfchat.R
import ru.bagrusss.selfchat.adapters.ChatAdapter
import ru.bagrusss.selfchat.data.HelperDB
import ru.bagrusss.selfchat.eventbus.Message
import ru.bagrusss.selfchat.services.ServiceHelper
import ru.bagrusss.selfchat.util.getTime

class ChatActivity : AppCompatActivity(), View.OnClickListener, TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        val REQUEST_CODE = 10
        val PICK_IMAGE_REQUEST = 11
        val REQUEST_IMAGE_CAPTURE = 12
        val REQUEST_LOCATION = 13
    }

    var mFabMenu: FloatingActionMenu? = null
    var mFabGeo: FloatingActionButton? = null
    var mFabAlbum: FloatingActionButton? = null
    var mFabCamera: FloatingActionButton? = null
    var mFabText: FloatingActionButton? = null

    var mSendButton: ImageView? = null
    var mTextMessage: EditText? = null
    var mMessageView: View? = null

    var mRecyclerView: RecyclerView? = null
    var mAdapter: ChatAdapter? = null

    val KEY_EDITING = "edit_msg"
    val KEY_SERVER = "server"
    val mHandler = Handler()
    var mProgressDialog: ProgressDialog? = null

    class ChatLoader : CursorLoader {
        companion object {
            val ID = 15
        }

        constructor(c: Context) : super(c) {
        }

        override fun loadInBackground(): Cursor {
            return HelperDB.getInstance(context).getAllMessages()
        }
    }

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
            sendMessage(mTextMessage!!.text.toString())
            mTextMessage!!.setText("")
            mMessageView?.visibility = View.GONE
            mFabMenu!!.showMenu(true)
            inputMethodManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
        mSendButton?.isEnabled = false

        mMessageView = find(R.id.message_view)
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_EDITING)) {
                mMessageView!!.visibility = View.VISIBLE
                mFabMenu!!.hideMenuButton(false)
                if (!"".equals(mTextMessage!!.text.toString())) {
                    mSendButton?.isEnabled = false
                }
            }
        } else {
            alertServer(this)
        }
        val display = windowManager.defaultDisplay
        val p = Point()
        display.getSize(p)
        mAdapter = ChatAdapter(p.x, p.y)
        val lm = LinearLayoutManager(this)
        mRecyclerView?.layoutManager = lm
        mRecyclerView?.adapter = mAdapter

    }

    private fun alertServer(c: Context) {
        val serverDialog = alert {
            var e: EditText? = null
            val preferences = PreferenceManager
                    .getDefaultSharedPreferences(c)
            customView {
                val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
                lp.setMargins(resources.getDimensionPixelOffset(R.dimen.fab_margin),
                        0, resources.getDimensionPixelOffset(R.dimen.fab_margin), 0)
                e = editText()
                e?.layoutParams = lp
                e!!.setText(preferences.getString(KEY_SERVER, ""))
            }
            positiveButton(android.R.string.ok) {
                val server = e!!.text.toString()
                preferences.edit()
                        .putString(KEY_SERVER, server)
                        .apply()
                initRetrofit(server)
                dismiss()
            }
            title(R.string.enter_server)
        }
        serverDialog.cancellable(false)
        serverDialog.show()
        serverDialog.dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun updateMessages() {
        mProgressDialog = ProgressDialog.show(this, "", getString(R.string.loading), true)
        ServiceHelper.updateMessages(this, REQUEST_CODE)
    }

    private fun initRetrofit(server: String) {
        ServiceHelper.initRetrofit(this, server)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_geo -> {
                mProgressDialog?.show()
                mHandler.postDelayed({
                    selectLocation()
                }, 1000)
            }
            R.id.fab_album -> {
                selectImg()
            }
            R.id.fab_camera -> {
                makePhoto()
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

    private fun selectLocation() {
        startActivityForResult(Intent(this, MapActivity::class.java), REQUEST_LOCATION)
    }

    private fun makePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun selectImg() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_img)), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === RESULT_OK)
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    if (data != null && data.data != null) {
                        insertImage(data.data.toString())
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    if (data != null) {
                        val extras = data.extras
                        val bmp = extras.get("data") as Bitmap
                        saveBitmap(bmp)
                    }
                }
                REQUEST_LOCATION -> {
                    if (data != null) {
                        insertImage(data.data.toString())
                    }
                }

            }
        mProgressDialog?.dismiss()
    }

    private fun saveBitmap(bmp: Bitmap) {
        ServiceHelper.saveBMP(this, bmp, HelperDB.TYPE_IMAGE, getTime(), REQUEST_CODE)
    }

    private fun insertImage(uri: String) {
        ServiceHelper.saveBMPCompressed(this, uri, HelperDB.TYPE_IMAGE,
                getTime(), REQUEST_CODE)
    }

    private fun updateChat() {
        val loader = loaderManager.getLoader<Cursor>(ChatLoader.ID)
        if (loader == null) {
            loaderManager.initLoader(ChatLoader.ID, null, this)
        } else loader.forceLoad()
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, c: Cursor) {
        mAdapter?.swapCursor(c)
        mRecyclerView?.scrollToPosition(c.count - 1)
        mProgressDialog?.dismiss()
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {

    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        return ChatLoader(this)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.length == 0) {
            mSendButton?.isEnabled = false
        } else {
            mSendButton?.isEnabled = true
        }
    }

    private fun sendMessage(msg: String) {
        ServiceHelper.addData(this, msg, HelperDB.TYPE_TEXT, getTime(), REQUEST_CODE)
    }

    override fun onStart() {
        EventBus.getDefault().register(this)
        super.onStart()
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(m: Message) {
        if (m.reqCode == REQUEST_CODE) {
            if (m.status == Message.OK) {
                toast(android.R.string.ok)
                updateChat()
                mFabMenu!!.showMenuButton(false)
                mSendButton?.isEnabled = false
                mProgressDialog?.dismiss()
                return
            }
            if (m.status == Message.RETROFIT_READY) {
                loaderManager.initLoader(ChatLoader.ID, null, this)
                updateMessages()
                return
            }
            toast(m.errorText)
        }
    }

    override fun onDestroy() {
        HelperDB.closeDB()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mMessageView!!.visibility === View.VISIBLE) {
            outState.putBoolean(KEY_EDITING, true)
        }
    }

}

