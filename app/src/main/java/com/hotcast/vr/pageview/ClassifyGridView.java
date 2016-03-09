package com.hotcast.vr.pageview;


import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.BaseAdapterHelper;
import com.hotcast.vr.adapter.QuickAdapter;
import com.hotcast.vr.bean.ChannelList;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by lostnote on 15/11/17.
 */
public class ClassifyGridView extends BaseView {
    @InjectView(R.id.grid)
    PullToRefreshGridView grid;
    @InjectView(R.id.fab)
    FloatingActionButton fab;
    @InjectView(R.id.progressBar2)
    ProgressBar progressBar2;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;

    private QuickAdapter adapter;
    private String requestUrl;
    private boolean bPullDown = true;
    private  String channel_id;
    private RequestParams params;
//    private int n = 1;
    private  int page = 1;
    private Intent intent;
    @OnClick(R.id.fab)
    void clickfab(){
        if (BaseApplication.classifies != null){
            activity.clickVrMode();
        }
    }
    BitmapUtils bitmapUtils;
    public ClassifyGridView(BaseActivity activity,String channel_id){
        super(activity, R.layout.layout_classify_grid);
        this.channel_id = channel_id;
        requestUrl = Constants.PROGRAM_LIST;
    }
    private void initListView(){
        grid.setMode(PullToRefreshBase.Mode.BOTH);
        grid.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = true;
                page = 1;
                getNetData(page);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                bPullDown = false;
                System.out.println("---ClassfyGridView() page" + page++);
                getNetData(page);
            }
        });


        adapter = new QuickAdapter<ChannelList>(activity, R.layout.item_grid) {
            @Override
            protected void convert(BaseAdapterHelper helper, ChannelList item) {
                bitmapUtils = new BitmapUtils(activity);
                ImageView iv = ButterKnife.findById(helper.getView(), R.id.iv_grid);
//                ViewUtils.setViewHeight(iv, ScreenUtils.getScreenWidth(activity) / 2);
                bitmapUtils.display(iv,item.getImage().get(0));
//                helper.setImageUrl(R.id.iv_grid, item.getImage());
                helper.setText(R.id.tv_grid, item.getTitle());
                helper.setText(R.id.desc, item.getDesc());
            }
        };

        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChannelList item = (ChannelList) adapter.getItem(i);
//                switch (item.getAction()){
//                    case "web":
//                        break;
//                    case "one":
//                        intent = new Intent(activity,DetailActivity.class);
//                        intent.putExtra("action","one");
//                        intent.putExtra("resource",item.getResource());
//                        L.e("ClassifyGridView " + item.getResource());
//                        activity.startActivity(intent);
//                        break;
//                    case "many":
                intent = new Intent(activity, DetailActivity.class);
//                        intent.putExtra("action","many");
                intent.putExtra("videoset_id", item.getId());
//                L.e("ClassifyGridView " + item.getResource());
                activity.startActivity(intent);
//                        break;
//                }
            }
        });

        // init fab
        fab.attachToListView(grid.getRefreshableView());
    }

    @Override
    public void init() {
        if(bFirstInit){
            initListView();
        }

        if (checkRequest()) {
            getNetData(page);
        }
        super.init();
    }

    private void getNetData(int page) {
//        if (Utils.textIsNull(requestUrl)) {
//            grid.onRefreshComplete();
//            return;
//        }
        params = new RequestParams();
        String str = activity.format.format(System.currentTimeMillis());
        params.addBodyParameter("token", Md5Utils.getMd5("hotcast-" + str + "-hotcast"));
        params.addBodyParameter("channel_id", channel_id);
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        params.addBodyParameter("page_size", String.valueOf(10));
        System.out.println("---channel_id = " + channel_id + " version = " + BaseApplication.version + " platform = " + BaseApplication.platform );
        if(!bPullDown){
            params.addBodyParameter("page", String.valueOf(page));
        }
        activity.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if (iv_noNet == null) {
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                }
                iv_noNet.setVisibility(View.GONE);
                bDataProcessed = true;
                bProcessing = false;
                grid.onRefreshComplete();
                L.e("responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
                if (iv_noNet == null) {
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                }
                iv_noNet.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println("---请求数据失败！！！");
                if (iv_noNet == null) {
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                    iv_noNet.setVisibility(View.VISIBLE);
                } else {
                    iv_noNet.setVisibility(View.VISIBLE);
                }
                bDataProcessed = false;
                bProcessing = false;
                grid.onRefreshComplete();

            }
        });
    }

    List<ChannelList> tmpList;
    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            return;
        }
        progressBar2.setVisibility(View.GONE);
        try {
            tmpList = new Gson().fromJson(json, new TypeToken<List<ChannelList>>() {
            }.getType());
        }catch (IllegalStateException e){
            activity.showToast("解析出现错误，请刷新数据");
        }

        DbUtils db = DbUtils.create(activity);
        try {
            db.delete(HomeRoll.class, WhereBuilder.b("channel_id", "==", channel_id));
        } catch (DbException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < tmpList.size(); i ++){
            ChannelList homeRoll = tmpList.get(i);
//            homeRoll.setChannel_id(channel_id);
            try {
                db.save(homeRoll);
            } catch (DbException e) {
                e.printStackTrace();
            }

        }
        if(bPullDown){
            adapter.addNewAll(tmpList);
        }else{
            if (tmpList.size()>0) {
                adapter.addAll(tmpList,grid);
                grid.smoothScrollBy(300,600);
            }
        }
        L.e("adapter size=" + adapter.getCount());
    }
}
