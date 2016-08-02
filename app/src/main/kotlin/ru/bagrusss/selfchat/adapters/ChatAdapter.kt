package ru.bagrusss.selfchat.adapters

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find
import ru.bagrusss.selfchat.R
import ru.bagrusss.selfchat.data.HelperDB
import ru.bagrusss.selfchat.util.CursorAdapterRecycler

/**
 * Created by bagrusss.
 */
class ChatAdapter : CursorAdapterRecycler<RecyclerView.ViewHolder>() {

    val TYPE_TEXT = HelperDB.TYPE_TEXT
    val TYPE_IMAGE = HelperDB.TYPE_IMAGE
    val TYPE_DATE = HelperDB.TYPE_DATE

    abstract class BaseHolder(v: View) : RecyclerView.ViewHolder(v) {
        var timeDateView: TextView? = null
    }

    open class DateHolder : BaseHolder {
        constructor(v: View) : super(v) {
            timeDateView = v.find(R.id.date_view)
        }
    }

    open class TextHolder : BaseHolder {
        var msgText: TextView

        constructor(v: View) : super(v) {
            timeDateView = v.find(R.id.time_view)
            msgText = v.find(R.id.text_msg)
        }

    }

    open class ImageHolder : BaseHolder {
        var image: ImageView

        constructor(v: View) : super(v) {
            timeDateView = v.find(R.id.time_view)
            image = v.find(R.id.img_view)
        }
    }

    fun inflateView(parent: ViewGroup, res: Int): View {
        return LayoutInflater.from(parent.context).inflate(res, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, c: Cursor) {
        val type = c.getInt(c.getColumnIndex(HelperDB.TYPE))
        when (type) {
            TYPE_TEXT -> bindText(holder as TextHolder, c)
            TYPE_DATE -> bindDate(holder as DateHolder, c)
        }
    }

    private fun bindDate(holder: DateHolder, c: Cursor) {
        holder.timeDateView?.text = c.getString(c.getColumnIndex(HelperDB.DATA))
    }

    fun bindText(holder: TextHolder, c: Cursor) {
        holder.msgText.text = c.getString(c.getColumnIndex(HelperDB.DATA))
        holder.timeDateView?.text = c.getString(c.getColumnIndex(HelperDB.TIME))
    }

    override fun getItemViewType(position: Int): Int {
        val c = getCursorAt(position)
        val type = c!!.getInt(c.getColumnIndex(HelperDB.TYPE))
        return type
    }

    override fun onCreateViewHolder(p: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val holder: BaseHolder
        when (type) {
            TYPE_TEXT -> holder = TextHolder(inflateView(p, R.layout.item_text_msg))
            TYPE_IMAGE -> holder = ImageHolder(inflateView(p, R.layout.item_with_photo))
            else -> holder = DateHolder(inflateView(p, R.layout.item_date))
        }
        return holder
    }

}