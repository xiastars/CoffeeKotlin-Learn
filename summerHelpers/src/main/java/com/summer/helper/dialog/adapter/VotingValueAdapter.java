package com.summer.helper.dialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malata.summer.helper.R;
import com.summer.helper.adapter.SRecycleAdapter;
import com.summer.helper.listener.OnSimpleClickListener;


/**
 * Created by xiaqiliang on 2017/3/22.
 */
public class VotingValueAdapter extends SRecycleAdapter {
    String[] names;
    OnSimpleClickListener listener;
    int selectIndex = 0;

    public VotingValueAdapter(Context context, String[] datas, OnSimpleClickListener listener) {
        super(context);
        this.names = datas;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_votingvalue,parent,false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TabViewHolder hd = (TabViewHolder) holder;
        hd.tvName.setText(names[position]+"积分");
        int coin = Integer.parseInt(names[position]);
/*        if(HxqUser.USER_INTEGRAL >= coin){
            if(position == selectIndex){
                hd.llParent.setBackgroundResource(R.drawable.so_white_st_redd4_45);
                hd.tvName.setTextColor(context.getResources().getColor(R.color.red_d4));
            }else{
                hd.llParent.setBackgroundResource(R.drawable.so_white45_st_grey);
                hd.tvName.setTextColor(context.getResources().getColor(R.color.grey_66));
            }
            hd.llParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(position);
                    notifyPosition(position);
                }
            });
        }else{
            hd.llParent.setBackgroundResource(R.drawable.so_greyca_45);
            hd.tvName.setTextColor(Color.WHITE);
        }*/
    }

    public void notifyPosition(int position){
        selectIndex = position;
        notifyDataSetChanged();
    }

    private class TabViewHolder extends RecyclerView.ViewHolder{

        private TextView tvName;
        private LinearLayout llParent;

        public TabViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            llParent = (LinearLayout) itemView.findViewById(R.id.ll_parent);
        }
    }

    @Override
    public int getItemCount() {
        return names.length;
    }
}
