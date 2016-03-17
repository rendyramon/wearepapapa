package com.hotcast.vr.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotcast.vr.R;
import com.hotcast.vr.bean.Pinglun;
import com.lidroid.xutils.BitmapUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by liurongzhi on 2016/3/16.
 */
public class PinglunAdapter extends BaseAdapter {
    Context context;
    BitmapUtils bitmapUtils;
    List<Pinglun.Data> datas;

    public List<Pinglun.Data> getDatas() {
        return datas;
    }

    public void setDatas(List<Pinglun.Data> datas) {
        this.datas = datas;
    }

    public PinglunAdapter(Context context, List<Pinglun.Data> datas) {
        this.datas = datas;
        this.context = context;
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.pinglun_item, null);
            holder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_lou = (TextView) convertView.findViewById(R.id.tv_lou);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        bitmapUtils.display(holder.iv_head, datas.get(position).getPicture());
        holder.tv_name.setText(datas.get(position).getUser());
        holder.tv_lou.setText(position + 1 + "楼");
        holder.tv_time.setText(new SimpleDateFormat("MM月dd日 HH:mm").format(new Date(datas.get(position).getTime())));
        holder.tv_content.setText(datas.get(position).getContent());
        return convertView;
    }

    class ViewHolder {
        ImageView iv_head;
        TextView tv_name;
        TextView tv_lou;
        TextView tv_time;
        TextView tv_content;
    }
}
