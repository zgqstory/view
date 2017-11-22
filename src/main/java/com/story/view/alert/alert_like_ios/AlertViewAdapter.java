package com.story.view.alert.alert_like_ios;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.story.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称：AlertViewAdapter
 * 类描述：弹出框按钮列表适配器
 * 创建人：story
 * 创建时间：2017/11/22 17:49
 */

public class AlertViewAdapter extends BaseAdapter {
    private List<String> mDatas = new ArrayList<>();
    private List<String> mDestructive;
    public AlertViewAdapter(List<String> data, List<String> destructive){
        if (data != null) {
            this.mDatas.addAll(data);
        }
        this.mDestructive =destructive;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.alert_like_ios_button, null);
            holder = createHolder(convertView);
            convertView.setTag(holder);
        }
        else{
            holder = (Holder) convertView.getTag();
        }
        String data = mDatas.get(position);
        holder.UpdateUI(parent.getContext(), data);
        return convertView;
    }

    private Holder createHolder(View view){
        return new Holder(view);
    }

    private class Holder {
        private TextView tvAlert;

        Holder(View view){
            tvAlert = view.findViewById(R.id.tv_button);
        }

        void UpdateUI(Context context, String data){
            tvAlert.setText(data);
            if (mDestructive!= null && mDestructive.contains(data)){
                tvAlert.setTextColor(ContextCompat.getColor(context, R.color.alert_like_ios_txt_destructive));
            }
            else{
                tvAlert.setTextColor(ContextCompat.getColor(context, R.color.alert_like_ios_txt_others));
            }
        }
    }
}
