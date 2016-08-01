package ru.bagrusss.selfchat.util

import android.database.Cursor
import android.database.DataSetObserver
import android.support.v7.widget.RecyclerView

/**
 * Created by bagrusss.
 */
abstract class CursorAdapterRecycler<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<VH>() {
    private var mCursor: Cursor? = null
    private var mDataValid: Boolean = false
    private var mRowIdColumn: Int = 0
    private var mDataSetObserver: DataSetObserver? = null

    fun constructor(cursor: Cursor?) {
        mCursor = cursor
        mDataValid = cursor != null
        mRowIdColumn = if (mDataValid) mCursor!!.getColumnIndex("_id") else -1
        mDataSetObserver = NotifyingDataSetObserver()
        if (mCursor != null) {
            mCursor!!.registerDataSetObserver(mDataSetObserver)
        }
    }

    override fun getItemCount(): Int {
        if (mDataValid && mCursor != null) {
            return mCursor!!.getCount()
        }
        return 0
    }

    fun getCursotAt(position: Int) {
        mCursor?.moveToPosition(position)
    }

    override fun getItemId(position: Int): Long {
        if (mDataValid && mCursor != null && mCursor!!.moveToPosition(position)) {
            return mCursor!!.getLong(mRowIdColumn)
        }
        return 0
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    abstract fun onBindViewHolder(viewHolder: VH, cursor: Cursor)

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        if (!mDataValid) {
            throw IllegalStateException("this should only be called when the cursor is valid")
        }
        if (!mCursor!!.moveToPosition(position)) {
            throw IllegalStateException("couldn't move cursor to position " + position)
        }
        onBindViewHolder(viewHolder, mCursor!!)
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.
     */

    fun swapCursor(newCursor: Cursor?) {
        if (newCursor === mCursor) {
            return
        }
        val oldCursor = mCursor
        if (mDataSetObserver != null) {
            oldCursor?.unregisterDataSetObserver(mDataSetObserver)
            oldCursor?.close()
        }
        mCursor = newCursor
        if (mCursor != null) {
            if (mDataSetObserver != null) {
                mCursor!!.registerDataSetObserver(mDataSetObserver)
            }
            mRowIdColumn = newCursor!!.getColumnIndexOrThrow("_id")
            mDataValid = true
            notifyDataSetChanged()
        } else {
            mRowIdColumn = -1
            mDataValid = false
            notifyDataSetChanged()
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }

    private inner class NotifyingDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            mDataValid = true
            notifyDataSetChanged()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            mDataValid = false
            notifyDataSetChanged()
        }
    }
}