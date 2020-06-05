package com.summer.demo.selectmorepic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Util {

	Context context;
	public static int screenWidth = 0;
	public static int screenHeight = 0;

	public Util(Context context) {
		this.context=context;
	}

	/**
	 *  获取照片集ַ
	 * @return
	 */
	public ArrayList<String>  getPhotoList(Context context){
    	Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	Uri uri = intent.getData();
    	ArrayList<String> list = new ArrayList<String>();
    	String[] proj ={MediaStore.Images.Media.DATA};
    	Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);//managedQuery(uri, proj, null, null, null);
    	try {

        	while(cursor.moveToNext()){
        		String path =cursor.getString(0);
        		/* 删除掉20KB以下的照片，很多是废图*/
        		int len = getPicSize(path);
        		if(len/1024 > 20){
            		list.add(new File(path).getAbsolutePath());
        		}
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cursor.close();
		}
		return list;
    }

	/**
	 * 获取文件的大小
	 * @param path
	 * @return
	 */
	private int getPicSize(String path ){
		int fileLen = 0;
		File dF = new File(new File(path).getAbsolutePath());
		FileInputStream fis;
		try {
			fis = new FileInputStream(dF);
			fileLen = fis.available(); //这就是文件大小
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		return fileLen;

	}


	public List<CommonFile> getFileList(Context context){
		List<CommonFile> data=new ArrayList<CommonFile>();
		String filename="";
		List<String> photoList=getPhotoList(context);
		List<String> retulist=new ArrayList<String>();
		if (photoList!=null) {
			Set set = new TreeSet();
			String []str;
			for (int i = 0; i < photoList.size(); i++) {
				retulist.add(getfileinfo(photoList.get(i)));
			}
			for (int i = 0; i < retulist.size(); i++) {
				set.add(retulist.get(i));
			}
			str= (String[]) set.toArray(new String[0]);
			for (int i = 0; i < str.length; i++) {
				filename=str[i];
				CommonFile ftl= new CommonFile();
				ftl.filename=filename;
				data.add(ftl);
			}

			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < photoList.size(); j++) {
					if (data.get(i).filename.equals(getfileinfo(photoList.get(j)))) {
						ImageInfo info = new ImageInfo();
						info.setPath(photoList.get(j));
						data.get(i).filecontent.add(info);
					}
				}
			}
		}
		return data;
	}

	public Bitmap getPathBitmap(Uri imageFilePath,int dw,int dh)throws FileNotFoundException{
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        Bitmap pic = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageFilePath),
                null, op);

        int wRatio = (int) Math.ceil(op.outWidth / (float) dw);
        int hRatio = (int) Math.ceil(op.outHeight / (float) dh);

        if (wRatio > 1 && hRatio > 1) {
            if (wRatio > hRatio) {
                op.inSampleSize = wRatio;
            } else {
                op.inSampleSize = hRatio;
            }
        }
        op.inJustDecodeBounds = false;
        pic = BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(imageFilePath), null, op);

        return pic;
	}

	public String getfileinfo(String data){
		String filename[]= data.split("/");
		if (filename!=null) {
			return filename[filename.length-2];
		}
		return null;
	}

	public void imgExcute(ImageView imageView,ImgCallBack icb, String... params){
		LoadBitAsynk loadBitAsynk=new LoadBitAsynk(imageView,icb);
		loadBitAsynk.execute(params);
	}

	public class LoadBitAsynk extends AsyncTask<String, Integer, Bitmap>{

		ImageView imageView;
		ImgCallBack icb;

		LoadBitAsynk(ImageView imageView,ImgCallBack icb){
			this.imageView=imageView;
			this.icb=icb;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bitmap=null;
			try {
				if (params!=null) {
					for (int i = 0; i < params.length; i++) {
						bitmap=getPathBitmap(Uri.fromFile(new File(params[i])), 200, 200);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result!=null) {
				icb.resultImgCall(imageView, result);
			}
		}


	}

	/**
	 * xml里是dp，但是到代码里是px，这个方法让传进去的dp仍然保持dp
	 *
	 * @param context
	 * @param value
	 *            传的dp值
	 * @return
	 */
	public static int getDip(Context context, int value) {
		int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, value, context.getResources()
						.getDisplayMetrics());
		return pageMargin;
	}

	/**
	 * 初始化当前屏幕分辨率
	 */
	public static void initScreenDisplayMetrics(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		Util.screenHeight = metric.heightPixels;
		Util.screenWidth = metric.widthPixels;
	}

}
