package com.summer.demo.module.emoji

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.summer.demo.R
import com.summer.helper.adapter.SRecycleAdapter
import com.summer.helper.listener.OnReturnObjectClickListener
import com.summer.helper.utils.SUtils

/**
 * Created by xiaqiliang on 2017/9/7.
 */
class EmojiItemAdapter(context: Context?, private val onEmojiSelectedListener: OnReturnObjectClickListener?) : SRecycleAdapter(context) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.emoji_item_layout, parent, false)
        return TabViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hd = holder as TabViewHolder
        val iconEntity = items[position] as IconEntity
        val uri = iconEntity.emojiPath
        if (!TextUtils.isEmpty(uri)) {
            SUtils.setPic(hd.emojiView, iconEntity.emojiPath)
        } else {
            if (iconEntity.res != 0) SUtils.setPicResource(hd.emojiView, iconEntity.res)
        }
        bindEmojiClickListener(hd.rlEmojiLayout, iconEntity)
    }

    private fun bindEmojiClickListener(view: RelativeLayout?, iconEntity: IconEntity) {
        view!!.setOnClickListener(View.OnClickListener {
            if (onEmojiSelectedListener != null) {
                if (iconEntity.res == 0) {
                    return@OnClickListener
                }
                onEmojiSelectedListener.onClick(iconEntity)
            }
        })
    }

    protected inner class TabViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        var emojiView: ImageView = view.findViewById(R.id.emojiView)
        var rlEmojiLayout: RelativeLayout = view.findViewById(R.id.rl_emoji_layout)
    }

    init {
        notifyDataSetChanged()
    }
}