package com.summer.demo.selectmorepic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.summer.demo.R;
import com.summer.helper.utils.SUtils;

import java.util.List;

public class SelectedAdapter extends BaseAdapter{

	private Context context;
	private	List<ImageInfo> list;
	private ViewHolder holder;
	private FrameLayout.LayoutParams params;

	public SelectedAdapter(Context context) {
		this.context=context;
	}

    public void notifyDataChanged(List<ImageInfo> list){
    	this.list = list;
    	notifyDataSetChanged();
    }

	@Override
	public int getCount() {
		return  list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_photos, null);
			holder = getViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder)convertView.getTag();
		}
		if(position == 0){
    		holder.imageAddView.setVisibility(View.VISIBLE);
    		holder.imageView.setVisibility(View.GONE);
    		holder.selected.setVisibility(View.GONE);
		}else{
			holder.imageAddView.setVisibility(View.GONE);
    		holder.imageView.setVisibility(View.VISIBLE);
		}
		SUtils.setPicWithSize(holder.imageView,list.get(position).getPath(),240);

		return convertView;
	}

	private ViewHolder getViewHolder(View convertView){
		holder = new ViewHolder();
		holder.imageView =(ImageView)convertView.findViewById(R.id.item_album);
		holder.selected = (RelativeLayout)convertView.findViewById(R.id.item_selected);
		holder.imageAddView = (ImageView)convertView.findViewById(R.id.item_add);

		int pageMargin = SUtils.getDip(context,4);
		int width =(SUtils.screenWidth-pageMargin)/3;
		params =new FrameLayout.LayoutParams(width,width);
		holder.imageView.setLayoutParams(params);
		holder.selected.setLayoutParams(params);
		holder.imageAddView.setLayoutParams(params);
		return holder;
	}

	class ViewHolder{
		private ImageView imageView;
		private RelativeLayout selected;
		private ImageView imageAddView;
	}



}
