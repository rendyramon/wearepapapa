package com.hotcast.vr.pageview;


import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.WebViewActivity;
import com.hotcast.vr.bean.Datas;
import com.hotcast.vr.bean.HomeBean;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.bean.HomeSubject;
import com.hotcast.vr.bean.RollBean;
import com.hotcast.vr.bean.RollLister;
import com.hotcast.vr.bean.Roller;
import com.hotcast.vr.pullrefreshview.PullToRefreshBase;
import com.hotcast.vr.pullrefreshview.PullToRefreshListView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONObject;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;


/**
 * Created by lostnote on 15/11/17.
 */
public class HomeView2 extends BaseView {


    @ViewInject(R.id.dots_ll)
    private LinearLayout dots_ll;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;


    private FloatingActionButton fab_home;
    private ProgressBar progressBar4;
    private MyBaseAdapter myBaseAdapter;
    //    需要传递给ViewPager去显示的图片关联文字说明
//    private List<String> titleList = new ArrayList<String>();

    //    传递图片对应的url地址的集合
    private List<String> urlImgList = new ArrayList<String>();
    //    放置点得集合
    private List<View> viewList = new ArrayList<View>();

//    private List<HomeSubject> subjects;

    private View layout_roll_view;
    private LinearLayout ll_top_news_viewpager;
    private com.hotcast.vr.pullrefreshview.PullToRefreshListView ptrlv_lv_item_news;
    private String requestUrl;
    List<ItemView> itemViews;
    DbUtils db;

    public HomeView2(BaseActivity activity) {
        super(activity, R.layout.layout_home);//根布局
        requestUrl = Constants.ROLL;
        db = DbUtils.create(activity);
        init();
    }


    private void initListView() {
        layout_roll_view = View.inflate(activity, R.layout.layout_roll_view, null);//轮播图
        ViewUtils.inject(this, layout_roll_view);
//        ((RelativeLayout.LayoutParams)layout_roll_view.getLayoutParams()).addRule();
        ll_top_news_viewpager = (LinearLayout) layout_roll_view.findViewById(R.id.ll_top_news_viewpager);
        rootView = View.inflate(activity, R.layout.frag_item_news, null);
        TextView title = (TextView) rootView.findViewById(R.id.tv_title);
        title.setText(activity.getResources().getString(R.string.homw_title));
        ViewUtils.inject(this, rootView);
        fab_home = (FloatingActionButton) rootView.findViewById(R.id.fab_home);
        progressBar4 = (ProgressBar) rootView.findViewById(R.id.progressBar4);
        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickVrMode();
            }
        });
        ptrlv_lv_item_news = (PullToRefreshListView) rootView.findViewById(R.id.lv_item_news);
        //下拉加载的事件屏蔽
        ptrlv_lv_item_news.setPullLoadEnabled(false);
        //包含下拉刷新，上拉加载操作
        ptrlv_lv_item_news.setScrollLoadEnabled(true);
        ptrlv_lv_item_news.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //下拉刷新
                Toast.makeText(activity, "下拉刷新了数据", Toast.LENGTH_SHORT).show();
                // TODO: 15/11/22  在这里执行相应的访问网络的操作
                urlImgList.clear();
//                titleList.clear();
                getNetData();
                getSubject();

            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                hideRefreshView();
            }
        });

        ptrlv_lv_item_news.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

//                Toast.makeText(activity, "listview条目被点击了", Toast.LENGTH_SHORT).show();

            }
        });
        hideRefreshView();
//        getNetData();
//        getSubject();

    }


    public void hideRefreshView() {
        ptrlv_lv_item_news.onPullDownRefreshComplete();
        ptrlv_lv_item_news.onPullUpRefreshComplete();
    }

    @Override
    public void init() {
//        initListView();
        getNetData();
        getSubject();
        if (bFirstInit) {
            //第一次打开，则初始化view，后续只要刷新数据就行
            initListView();
        }

        if (checkRequest()) {
            getNetData();
            getSubject();
        }
        super.init();
    }

    private void getSubject() {
        if (Utils.textIsNull(requestUrl)) {
            hideRefreshView();
            return;
        }
        String url = Constants.SPECIAL;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(activity));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        System.out.println("---version = " + BaseApplication.version + " --platform = " + BaseApplication.platform);
        activity.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                bDataProcessed = true;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                if (iv_noNet == null) {
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                    iv_noNet.setVisibility(View.GONE);
                } else {
                    iv_noNet.setVisibility(View.GONE);
                }
                hideRefreshView();
                L.e("HomeView2 responseInfo:" + responseInfo.result);
                setViewSubject(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                if (iv_noNet == null) {
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                    iv_noNet.setVisibility(View.VISIBLE);
                } else {
                    iv_noNet.setVisibility(View.VISIBLE);
                }
                activity.showToast("网络连接异常");
                System.out.println("---onFailure "+s);
                hideRefreshView();

            }
        });

    }

    //    放置底部item条目数据的集合
    List<RollBean> rollBeans;

    private void setViewSubject(String json) {
        //填充listView
        if (itemViews == null) {
            itemViews = new ArrayList<>();
        } else {
            itemViews.clear();
        }
        RollLister rollLister = new Gson().fromJson(json,RollLister.class);
        if ("success".equals(rollLister.getMessage())||0 <= rollLister.getCode() && rollLister.getCode() <= 10){
            rollBeans = rollLister.getData();
            for (int i = 0; i < rollBeans.size(); i++) {
                ItemView itemView = new ItemView(activity);
                itemView.setItemList(activity, rollBeans.get(i),i);
                itemViews.add(itemView);
            }

            if (myBaseAdapter == null) {
                if (rollBeans != null) {
                    myBaseAdapter = new MyBaseAdapter();
                }
                ptrlv_lv_item_news.getRefreshableView().setAdapter(myBaseAdapter);
            } else {
                myBaseAdapter.notifyDataSetChanged();
            }
            progressBar4.setVisibility(View.GONE);
        }else {
         activity.showToast("亲，网络数据获取失败了T_T");
        }
    }


    private void getNetData() {
        if (Utils.textIsNull(requestUrl)) {
            hideRefreshView();
            return;
        }
        String url = requestUrl;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(activity));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        activity.httpPost(url, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                bDataProcessed = true;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                hideRefreshView();
                L.e("HomeView2 responseInfo:" + responseInfo.result);
                System.out.println("---HomeView2 responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println("---HomeView2 getNetData onFailure "+s);
                bDataProcessed = false;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                if (iv_noNet == null){
                    iv_noNet = (ImageView) getRootView().findViewById(R.id.iv_noNet);
                    iv_noNet.setVisibility(View.VISIBLE);
                }else {
                    iv_noNet.setVisibility(View.VISIBLE);
                }
                activity.showToast("网络连接异常");
                hideRefreshView();

            }
        });

    }

    //    List<HomeRoll> homeRolls;
//    HomeBean homeBean;
    RollBean roll;
    List<Datas> datasList;

    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            iv_noNet.setVisibility(View.VISIBLE);
//            return;
        } else {
            Roller roller = new Gson().fromJson(json, Roller.class);
            if ("success".equals(roller.getMessage())||0 <= roller.getCode() && roller.getCode() <= 10){
                roll = roller.getData();
                if (datasList == null) {
                    datasList = new ArrayList<>();
                } else {
                    datasList.clear();
                }
                datasList = roll.getData();
                System.out.println("---datasList="+datasList);
                RollViewPager rollViewPager = new RollViewPager(activity, viewList, new RollViewPager.onPageClick() {
                    @Override
                    public void onclick(int i) {
                        Intent intent;
                        switch (datasList.get(i).getType()){
                            case "videoset":
                                intent = new Intent(activity, DetailActivity.class);
                                intent.putExtra("videoset_id", datasList.get(i).getMedia_id());
                                activity.startActivity(intent);
                                break;
                            case "web":
                                intent = new Intent(activity,WebViewActivity.class);
                                intent.putExtra("rec_ur",datasList.get(i).getRec_url());
                                System.out.println("--rec_ur="+datasList.get(i).getRec_url());
                                activity.startActivity(intent);
                                break;
                        }


                    }
                });

                urlImgList.clear();
                if (datasList!=null){
                    for (int i = 0; i < datasList.size(); i++) {
                        Datas datas = datasList.get(i);
                        urlImgList.add(datas.getImage());
                    }
                }
                initDot();

                rollViewPager.initImgUrlList(urlImgList);
                rollViewPager.startRoll();
                ll_top_news_viewpager.removeAllViews();
                ll_top_news_viewpager.addView(rollViewPager);
                //就是个listView
                if (ptrlv_lv_item_news.getRefreshableView().getHeaderViewsCount() < 1) {
                    ptrlv_lv_item_news.getRefreshableView().addHeaderView(layout_roll_view);
                }
            }else {
                activity.showToast("亲，网络数据获取失败啦T_T");
            }
        }
    }

    class MyBaseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return rollBeans.size();
        }

        @Override
        public Object getItem(int i) {
            return rollBeans.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = new ItemView(activity);
//            }
//            ((ItemView) convertView).setItemList(activity, rollBeans.get(position));
//            System.out.println("---尺寸:"+itemViews.size());
            if (convertView==null || ((ItemView)convertView).getPositon()!=position) {
                convertView = itemViews.get(position);
            }
            ptrlv_lv_item_news.getRefreshableView().clearDisappearingChildren();
            return convertView;
        }
    }


    private void initDot() {
        dots_ll.removeAllViews();
        viewList.clear();

        for (int i = 0; i < urlImgList.size(); i++) {
            View view = new View(activity);
            if (i == 0) {
                view.setBackgroundResource(R.drawable.dot_focus);
            } else {
                view.setBackgroundResource(R.drawable.dot_normal);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15, 15);
            view.setLayoutParams(layoutParams);
            layoutParams.setMargins(5, 0, 20, 0);
            dots_ll.addView(view);
            viewList.add(view);
        }
    }

}
