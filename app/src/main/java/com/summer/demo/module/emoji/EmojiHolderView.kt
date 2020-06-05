package com.summer.demo.module.emoji

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.summer.demo.R
import com.summer.demo.module.base.viewpager.Holder
import com.summer.demo.module.emoji.source.Source
import com.summer.helper.listener.OnReturnObjectClickListener
import com.summer.helper.view.NRecycleView

class EmojiHolderView(var listener: OnReturnObjectClickListener) : Holder<Source?> {
    var hd: ViewHolder? = null
    var emojiItemAdapter: EmojiItemAdapter? = null

    override fun createView(context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.emoji_panel_item, null)
        hd = ViewHolder(view)
        emojiItemAdapter = EmojiItemAdapter(context, listener)
        hd!!.nvContainer!!.adapter = emojiItemAdapter
        return view
    }


    class ViewHolder(view: View) {
        var nvContainer: NRecycleView = view.findViewById(R.id.nv_container)

        init {
            nvContainer!!.setGridView(7)
        }
    }

    override fun UpdateUI(context: Context?, position: Int, data: Source?) {
        emojiItemAdapter!!.notifyDataChanged(data!!.list)
    }

}