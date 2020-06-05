package com.summer.helper.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 静态Bitmap处理
 * Created by xiastars on 2017/7/25.
 */

public class BitmapUtils {

    static Map<String, List<Bitmap>> bitmaps = new HashMap<>();
    private static BitmapUtils bitmapUtils = null;

    public static synchronized BitmapUtils getInstance() {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils();
        }
        return bitmapUtils;
    }

    /**
     * 设置静态图片
     *
     * @param view
     * @param holder
     * @param tag    tag为当前Activity的名称
     */
    public void setPic(ImageView view, int holder, String tag) {
        try {
            SoftReference<Bitmap> bitmap = new SoftReference<>(SUtils.decodeBackgoundBitmapFromResource(view.getContext().getResources(), holder, SUtils.screenWidth, SUtils.screenHeight));
            Bitmap b = bitmap.get();
            if (!b.isRecycled()) {
                view.setImageBitmap(b);
            }
            List<Bitmap> bits = bitmaps.get(tag);
            if (bits == null) {
                bits = new ArrayList<>();
            }
            bits.add(bitmap.get());
            bitmaps.put(tag, bits);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置静态图片
     *
     * @param view
     * @param tag  tag为当前Activity的名称
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void setPic(ImageView view, Bitmap bitmap, String tag) {
        try {
            if (!bitmap.isRecycled()) {
                view.setImageBitmap(bitmap);
            }
            List<Bitmap> bits = bitmaps.get(tag);
            if (bits == null) {
                bits = new ArrayList<>();
            }
            boolean isContain = false;
            if (bits != null) {
                Iterator iterator = bits.iterator();
                while ((iterator.hasNext())) {
                    Bitmap b = (Bitmap) iterator.next();
                    if (bitmap != null && !b.isRecycled() && !bitmap.isRecycled() && bitmap.sameAs(b)) {
                        bitmap.recycle();
                        view.setImageBitmap(b);
                        isContain = true;
                        break;
                    }
                }
            }
            if (!isContain) {
                bits.add(bitmap);
                bitmaps.put(tag, bits);
                view.setImageBitmap(bitmap);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void addBitmap(Bitmap bitmap, String tag) {
        List<Bitmap> bits = bitmaps.get(tag);
        if (bits == null) {
            bits = new ArrayList<>();
            bitmaps.put(tag, bits);
        }
        bits.add(bitmap);
    }

    public void addBitmap(Bitmap bitmap, Context context) {
        if(context == null){
            return;
        }
        String tag = context.getClass().getSimpleName();
        List<Bitmap> bits = bitmaps.get(tag);
        if (bits == null) {
            bits = new ArrayList<>();
            bitmaps.put(tag, bits);
        }
        bits.add(bitmap);

    }

    /**
     * 主要判断是不是同一个holder
     *
     * @param view
     * @param tag
     * @param holder
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public void checkContainBitmaps(ImageView view, String tag, int holder) {
        SoftReference<Bitmap> softBitmap = new SoftReference<>(SUtils.decodeBackgoundBitmapFromResource(view.getContext().getResources(), holder, SUtils.screenWidth, SUtils.screenHeight));
        Bitmap b = softBitmap.get();
        List<Bitmap> bits = bitmaps.get(tag);
        boolean isContain = false;
        if (bits != null) {
            Iterator iterator = bits.iterator();
            while ((iterator.hasNext())) {
                Bitmap bitmap = (Bitmap) iterator.next();
                if (bitmap != null && bitmap.sameAs(b)) {
                    b.recycle();
                    view.setImageBitmap(bitmap);
                    isContain = true;
                    break;
                }
            }
        }
        if (!isContain) {
            setPic(view, holder, tag);
        }
    }

    /**
     * 清除内存
     *
     * @param tag
     */
    public void clearBitmaps(String tag) {
        List<Bitmap> bits = bitmaps.get(tag);
        if (bits != null) {
            Iterator iterator = bits.iterator();
            while ((iterator.hasNext())) {
                Bitmap bitmap = (Bitmap) iterator.next();
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            bits.clear();
        }
        bitmaps.remove(tag);
        Logs.i("清除内存:" + tag);
    }

    public void clearAll() {
        bitmaps.clear();
    }

}
