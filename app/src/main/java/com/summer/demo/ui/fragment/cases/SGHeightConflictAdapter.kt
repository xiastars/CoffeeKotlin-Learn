package com.summer.demo.ui.fragment.cases

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import com.summer.demo.R
import com.summer.helper.utils.SUtils
import com.summer.helper.utils.TipDialog

/**
 * Created by summer on 2016年12月14日 16:24.
 */

class SGHeightConflictAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var deleteState = false
        internal set

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHodler = holder as MyViewHolder
        if (deleteState) {
            viewHodler.ivDelete.visibility = View.VISIBLE
        } else {
            viewHodler.ivDelete.visibility = View.GONE
        }
        viewHodler.ivDelete.setOnClickListener {
            val dialog = TipDialog(context, "删除该项", object : TipDialog.DialogAfterClickListener {
                override fun onSure() {

                }

                override fun onCancel() {
                    cancelDeleteState(false)
                }
            })
            dialog.show()
        }
    }


    fun cancelDeleteState(state: Boolean) {
        this.deleteState = state
        notifyDataSetChanged()
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var rlParentLayout: LinearLayout? = null
        internal var ivNav: ImageView
        internal var rlDownload: RelativeLayout
        internal var ivDelete: ImageView
        internal var rlIcon: RelativeLayout
        internal var tvName: TextView

        init {
            tvName = view.findViewById<View>(R.id.tv_book_name) as TextView
            ivDelete = view.findViewById<View>(R.id.iv_delete) as ImageView
            ivNav = view.findViewById<View>(R.id.iv_book_icon) as ImageView
            rlDownload = view.findViewById<View>(R.id.rl_download) as RelativeLayout
            rlIcon = view.findViewById<View>(R.id.item_left_rl) as RelativeLayout
            SUtils.clickTransColor(rlIcon)
        }
    }

    override fun getItemCount(): Int {
        return 102
    }

    override fun onCreateViewHolder(parent: ViewGroup, arg1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_sgheight, parent,
                false))
    }

}