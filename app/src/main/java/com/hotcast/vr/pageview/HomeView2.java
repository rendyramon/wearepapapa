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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.bean.Datas;
import com.hotcast.vr.bean.HomeBean;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.bean.HomeSubject;
import com.hotcast.vr.bean.RollBean;
import com.hotcast.vr.pullrefreshview.PullToRefreshBase;
import com.hotcast.vr.pullrefreshview.PullToRefreshListView;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
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

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by lostnote on 15/11/17.
 */
public class HomeView2 extends BaseView {

    @ViewInject(R.id.top_news_title)
    private TextView top_news_title;

    @ViewInject(R.id.dots_ll)
    private LinearLayout dots_ll;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;


    private FloatingActionButton fab_home;
    private ProgressBar progressBar4;
    private MyBaseAdapter myBaseAdapter;
    //    需要传递给ViewPager去显示的图片关联文字说明
    private List<String> titleList = new ArrayList<String>();

    //    传递图片对应的url地址的集合
    private List<String> urlImgList = new ArrayList<String>();
    //    放置点得集合
    private List<View> viewList = new ArrayList<View>();

//    private List<HomeSubject> subjects;

    private View layout_roll_view;
    private LinearLayout ll_top_news_viewpager;
    private com.hotcast.vr.pullrefreshview.PullToRefreshListView ptrlv_lv_item_news;
    //    private ItemView itemView;
    private String requestUrl;
    //    private boolean bPullDown = true;
    List<ItemView> itemViews;
    DbUtils db;

    public HomeView2(BaseActivity activity) {
        super(activity, R.layout.layout_home);//根布局
//        requestUrl = Constants.URL_HOME;
        requestUrl = Constants.ROLL;
        db = DbUtils.create(activity);
        init();
    }


    private void initListView() {
        layout_roll_view = View.inflate(activity, R.layout.layout_roll_view, null);//轮播图
        ViewUtils.inject(this, layout_roll_view);
        ll_top_news_viewpager = (LinearLayout) layout_roll_view.findViewById(R.id.ll_top_news_viewpager);
        rootView = View.inflate(activity, R.layout.frag_item_news, null);
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
                titleList.clear();
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
        params.addBodyParameter("token", "123");
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
        rollBeans = new Gson().fromJson(json, new TypeToken<List<RollBean>>() {
        }.getType());

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
//        ptrlv_lv_item_news.getRefreshableView().setOnScrollListener(new PauseOnScrollListener(BaseApplication.bu, false, true));
        progressBar4.setVisibility(View.GONE);

    }


    private void getNetData() {
        if (Utils.textIsNull(requestUrl)) {
            hideRefreshView();
            return;
        }
        String url = requestUrl;
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", "123");
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
                setViewData(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                //隐藏底部加载更多 、顶部刷新的ui；
                iv_noNet.setVisibility(View.VISIBLE);
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
            try {
                roll = new Gson().fromJson(json, RollBean.class);
//                homeRolls = homeBean.getHome_roll();
                //        HomeSubject的集合
                if (datasList == null) {
                    datasList = new ArrayList<>();
                } else {
                    datasList.clear();
                }
                datasList = roll.getData();
//                System.out.println("---datasList = " + datasList);

            } catch (IllegalStateException e) {
                activity.showToast("解析出现错误，请刷新数据");
            }
            try {
                db.save(roll);
            } catch (DbException e) {
                e.printStackTrace();
            }
            //初始化viewpager
//            L.e("HomeView2 viewList.sixe() = " + viewList.size());

        }

        RollViewPager rollViewPager = new RollViewPager(activity, viewList, new RollViewPager.onPageClick() {
            @Override
            public void onclick(int i) {
                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra("videoset_id", datasList.get(i).getMedia_id());
//                    intent.putExtra("resource", datasList.get(i).getResource());
                activity.startActivity(intent);
//                Toast.makeText(activity, "position = " + i, Toast.LENGTH_SHORT).show();
            }
        });
        System.out.println("---335 datasList = " + datasList);
//    subjects=homeBean.getHome_subject();
//        subjects.addAll(homeBean.getHome_subject());
        urlImgList.clear();
        titleList.clear();
        for (int i = 0; i < datasList.size(); i++) {
//            RollBean homeRoll = datasList.get(i);
            Datas datas = datasList.get(i);
            System.out.println("---343 datas = " + datas);
//            for (int j = 0; j < datas.getData().size(); j++) {
//                Datas subject = datas.getData().get(j);

            urlImgList.add(datas.getImage());
            titleList.add(datas.getTitle());
            System.out.println("---349 urlImg = " + datas.getImage());
            System.out.println("---350 title = " + datas.getTitle());
//            }
        }
//        System.out.println("---urlImgList = " + urlImgList.size());
//        System.out.println("---titleList = " + titleList.size());


        initDot();

        rollViewPager.initTitleList(top_news_title, titleList);
        rollViewPager.initImgUrlList(urlImgList);
        rollViewPager.startRoll();
        ll_top_news_viewpager.removeAllViews();
        ll_top_news_viewpager.addView(rollViewPager);
        //就是个listView
        if (ptrlv_lv_item_news.getRefreshableView().getHeaderViewsCount() < 1) {
            ptrlv_lv_item_news.getRefreshableView().addHeaderView(layout_roll_view);
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
            System.out.println("---尺寸:"+itemViews.size());
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

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
            view.setLayoutParams(layoutParams);
            layoutParams.setMargins(5, 0, 10, 0);
            dots_ll.addView(view);
            viewList.add(view);
        }
    }

}
