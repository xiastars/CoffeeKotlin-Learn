package com.summer.helper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.summer.helper.recycle.SmartRecyclerView;

/**
 * 自定义RecycleView
 *
 * @author xiaqiliang
 */
public class SRecycleView extends SmartRecyclerView implements ScrollableHelper.ScrollableContainer {


    public SRecycleView(Context context) {
        super(context);

    }

    public SRecycleView(Context context, AttributeSet attri) {
        super(context, attri);
    }


    @Override
    public View getScrollableView() {
        return null;
    }
}
