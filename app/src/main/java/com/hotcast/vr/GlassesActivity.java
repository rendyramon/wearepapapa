package com.hotcast.vr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hotcast.vr.bean.Glass;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.TokenUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liurongzhi on 2016/3/19.
 */
public class GlassesActivity extends Activity implements View.OnClickListener {
    ListView lv_glasses;
    GlassesAdapter adapter;
    List<Glass.GlassesData> glasses;
    BitmapUtils bitmapUtils;
    LinearLayout glasses_head;
    ImageView iv_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glasses);
        bitmapUtils = new BitmapUtils(this);
        glasses_head = (LinearLayout) findViewById(R.id.glasses_head);
        TextView tv = (TextView) glasses_head.findViewById(R.id.tv_title);
        iv_return = (ImageView) glasses_head.findViewById(R.id.iv_return);
        iv_return.setOnClickListener(this);
        tv.setText(getResources().getString(R.string.VRglasses));
        glasses = new ArrayList<>();
        lv_glasses = (ListView) findViewById(R.id.lv_glasses);
        adapter = new GlassesAdapter();
        lv_glasses.setAdapter(adapter);
        getGlassesData();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
        }
    }

    public class GlassesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return glasses.size();
        }

        @Override
        public Object getItem(int position) {
            return glasses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(GlassesActivity.this, R.layout.item_glasses_lv, null);
                holder.iv_glass = (ImageView) convertView.findViewById(R.id.iv_glass);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                holder.bt_buy = (Button) convertView.findViewById(R.id.bt_buy);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (glasses.size() > 0) {
                holder.bt_buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GlassesActivity.this, WebViewActivity.class);
                        System.out.println("---getBuy_url" + glasses.get(position).getBuy_url());
                        intent.putExtra("rec_ur", glasses.get(position).getBuy_url());
                        startActivity(intent);
                    }
                });
                bitmapUtils.display(holder.iv_glass, glasses.get(position).getImage());
                holder.tv_name.setText(glasses.get(position).getTitle());
                holder.tv_price.setText("￥" + glasses.get(position).getPrice());
                holder.tv_desc.setText(glasses.get(position).getDesc());
            }
            return convertView;
        }
    }

    public class ViewHolder {
        Button bt_buy;
        ImageView iv_glass;
        TextView tv_name;
        TextView tv_price;
        TextView tv_desc;
    }

    public void getGlassesData() {
        String url = "http://api2.hotcast.cn/index.php?r=/app/glass/get-list";
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(this));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        this.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("---眼镜：" + responseInfo.result);
                Glass glass = new Gson().fromJson(responseInfo.result, Glass.class);
                glasses = glass.getData();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                System.out.println("---眼镜：" + error + msg);
            }
        });
    }

    public void httpPost(String url, RequestParams params, RequestCallBack callBack) {
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST, url, params, callBack);
    }
}
