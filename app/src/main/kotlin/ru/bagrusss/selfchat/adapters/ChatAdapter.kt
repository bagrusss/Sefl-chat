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
import ru.bagrusss.selfchat.util.CursorAdapterRecycler

/**
 * Created by bagrusss.
 */
class ChatAdapter : CursorAdapterRecycler<RecyclerView.ViewHolder>() {

    val TYPE_TEXT = 1
    val TYPE_IMAGE = 2
    val TYPE_DATE = 3

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

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, cursor: Cursor) {
    }

    override fun getItemViewType(position: Int): Int {
        var c = getCursotAt(position)
        return super.getItemViewType(position)
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