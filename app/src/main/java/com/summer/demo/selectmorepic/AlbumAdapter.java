package com.summer.demo.selectmorepic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.summer.demo.R;
import com.summer.helper.utils.SUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends BaseAdapter{

	private Context context;
	private	List<ImageInfo> list;
	private List<ImageInfo> selectedList ;
	private ViewHolder holder;
	private FrameLayout.LayoutParams params;

	public AlbumAdapter(Context context) {
		this.context=context;
		selectedList = new ArrayList<ImageInfo>();
	}

	public List<ImageInfo> getSelectedImage(){
		return selectedList;
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
		holder.imageAddView.setVisibility(View.GONE);
		holder.imageView.setVisibility(View.VISIBLE);
		final ImageInfo imageInfo = list.get(position);
		SUtils.setPicWithSize(holder.imageView,imageInfo.getPath(),240);
		holder.selected.setVisibility(imageInfo.isSelected()==true ? View.VISIBLE : View.GONE);
		holder.parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(imageInfo.isSelected()==false){
					holder.selected.setVisibility(View.VISIBLE);
					imageInfo.setSelected(true);
					selectedList.add(imageInfo);

				}else{
					holder.selected.setVisibility(View.GONE);
					imageInfo.setSelected(false);
					selectedList.remove(imageInfo);
				}
				notifyDataSetChanged();
			}
			});


		return convertView;
	}

	private ViewHolder getViewHolder(View convertView){
		holder = new ViewHolder();
		holder.imageView =(ImageView)convertView.findViewById(R.id.item_album);
		holder.selected = (RelativeLayout)convertView.findViewById(R.id.item_selected);
		holder.parent = (FrameLayout)convertView.findViewById(R.id.parent);
		holder.imageAddView = (ImageView)convertView.findViewById(R.id.item_add);

		int pageMargin = Util.getDip(context,4);
		int width =(Util.screenWidth-pageMargin)/3;
		params =new FrameLayout.LayoutParams(width,width);
		holder.imageView.setLayoutParams(params);
		holder.selected.setLayoutParams(params);
		holder.imageAddView.setLayoutParams(params);
		return holder;
	}

	class ViewHolder{
		private ImageView imageView;
		private RelativeLayout selected;
		private FrameLayout parent;
		private ImageView imageAddView;
	}



}
