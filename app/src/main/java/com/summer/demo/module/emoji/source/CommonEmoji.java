package com.summer.demo.module.emoji.source;

import android.content.Context;
import com.summer.demo.R;
import com.summer.demo.module.emoji.EmojiData;
import com.summer.demo.module.emoji.IconEntity;
import com.summer.demo.module.emoji.MyEmojiService;

import java.util.Map;

/**
 * 普通表情
 * Created by xiaqiliang on 2017/9/7.
 */
public class CommonEmoji extends Source {

	@Override
	public void initIcon(Context context, Map<String, Integer> map) {
		for (int i = 0; i < EmojiData.commonEmoji.length; i++){

			IconEntity iconEntity = MyEmojiService.getInstance(context).addEmojiToMap(context,
					i+1+"",EmojiData.commonEmoji[i],map);
			getList().add(iconEntity);
		}
		//加几个空的
		for(int i = 0; i < 4;i++){
			IconEntity entity = new IconEntity();
			getList().add(entity);
		}
		IconEntity entity = new IconEntity();
		entity.setName("[]");
		entity.setRes(R.drawable.input_icon_deleting);
		getList().add(entity);
	}

	@Override
	public void setMenuRes() {
		setMenuResId(R.drawable.input_emoji_1);
	}

	@Override
	public void setEmojiKey() {
		setEmojiKey("ilv_e_1_0");
	}

}
