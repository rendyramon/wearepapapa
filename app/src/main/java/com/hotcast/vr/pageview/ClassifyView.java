package com.hotcast.vr.pageview;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.R;
import com.hotcast.vr.adapter.MyPagerAdapter;
import com.hotcast.vr.bean.ChanelData;
import com.hotcast.vr.bean.Channel;
import com.hotcast.vr.bean.Channeler;
import com.hotcast.vr.bean.Classify;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.pagerindicator.TabPageIndicator;
import com.hotcast.vr.tools.Constants;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Md5Utils;
import com.hotcast.vr.tools.TokenUtils;
import com.hotcast.vr.tools.Utils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by lostnote on 15/11/17.
 */
public class ClassifyView extends BaseView {
    @InjectView(R.id.pager)
    ViewPager pager;

    @InjectView(R.id.indicator)
    TabPageIndicator indicator;
    @ViewInject(R.id.iv_noNet)
    ImageView iv_noNet;
    @InjectView(R.id.tv_title)
    TextView title;
    @ViewInject(R.id.iv_noNetCollect)
    ImageView iv_noNetCollect;

    @ViewInject(R.id.ll_container)
    LinearLayout ll_container;

    private MyPagerAdapter adapter;
    private int curTabIndex = -1;
    private FloatingActionButton fab_home;

    private List<ChanelData> classifies;
    private int size;
    private BaseView[] views;
    private List<View> vs;
    private List<String> titles;

    private String requestUrl;

    public ClassifyView(BaseActivity activity){
        super(activity, R.layout.layout_classify);
        requestUrl = Constants.CHANNEL_LIST;
        title.setText(activity.getResources().getString(R.string.main_classify));
        init();
    }
    @Override
    public void init() {
        fab_home = (FloatingActionButton) rootView.findViewById(R.id.fab_home);
        fab_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickVrMode();
            }
        });
        iv_noNetCollect = (ImageView) rootView.findViewById(R.id.iv_noNetCollect);
        ll_container= (LinearLayout) rootView.findViewById(R.id.ll_container);
        iv_noNetCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNetData();
            }
        });
        if (checkRequest()) {
            getNetData();
        }
        super.init();
    }

    private void initListView() {
        DbUtils db = DbUtils.create(activity);

        for (int i = 0 ; i < size; i++){
            ChanelData classify = classifies.get(i);
            try {
                db.delete(Classify.class, WhereBuilder.b("id", "==", classifies.get(i).getId()));
                db.save(classify);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        views = new BaseView[size];
        L.e("ClassifyView viewa.lenght = " + size);
        titles = new ArrayList<>();

        vs = new ArrayList<>();
        for (int i = 0;i < size; i++){
            titles.add(classifies.get(i).getTitle());
//            System.out.println("---classifies.get(i).getType = " + classifies.get(i).getType());
            switch (classifies.get(i).getType()){
                case "big":
                    views[i] = new ClassifyListView(activity,classifies.get(i).getId());
                    System.out.println("---classifies " + classifies.get(i).getType());
                    break;
                case "small":
                    views[i] = new ClassifyGridView(activity,classifies.get(i).getId());
                    System.out.println("---classifies " + classifies.get(i).getType());
                    break;
            }
            vs.add(views[i].getRootView());
        }
        if (vs != null && vs.size() > 0) {
            if (adapter == null) {
                adapter = new MyPagerAdapter(vs, titles);
                pager.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            //指针需要和ViewPager进出绑定，即指针指向那页，ViewPager页处在那页
            indicator.setViewPager(pager);
            L.e("指针绑定ViewPager");
            System.out.println("指针绑定ViewPager");
            indicator.setCurrentItem(0);
        }else {
            iv_noNet.setVisibility(View.VISIBLE);
        }
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
//                classify_radio.check(checkedId[position]);
                indicator.setCurrentItem(position);
                BaseView baseView = views[position];
                baseView.init();
                baseView.getRootView();
                clickTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        BaseView baseView = views[0];
        baseView.init();
        clickTab(0);

    }
    private void clickTab(int index){
        if(index != curTabIndex){
            changeViewIndex(index);
            curTabIndex = index;
        }
    }
    private void changeViewIndex(int index){
        views[index].init();
    }

    private void getNetData() {
        System.out.println("---version = " + BaseApplication.version + "-platform = " + BaseApplication.platform);
        RequestParams params = new RequestParams();
        params.addBodyParameter("token", TokenUtils.createToken(activity));
        params.addBodyParameter("version", BaseApplication.version);
        params.addBodyParameter("platform", BaseApplication.platform);
        activity.httpPost(requestUrl, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                bProcessing = true;
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                bDataProcessed = true;
                bProcessing = false;
                ll_container.setVisibility(View.VISIBLE);
                iv_noNetCollect.setVisibility(View.GONE);
                L.e("---ClassifyView  responseInfo:" + responseInfo.result);
                setViewData(responseInfo.result);

            }

            @Override
            public void onFailure(HttpException e, String s) {
                bDataProcessed = false;
                bProcessing = false;
                activity.showToast(activity.getResources().getString(R.string.st_error));
                System.out.println("---请求数据失败 classifyView");
            }
        });
    }
    Channel channel;


    private void setViewData(String json) {
        if (Utils.textIsNull(json)) {
            iv_noNet.setVisibility(View.VISIBLE);
        }else {
            Channeler channeler = new Gson().fromJson(json, Channeler.class);
            if ("success".equals(channeler.getMessage())||0 <= channeler.getCode() && channeler.getCode() <= 10){
                channel = channeler.getData();
                classifies = channel.getData();
                BaseApplication.channel = channel;
                size = classifies.size();
                L.e("---adapter size=" + classifies.size());
                initListView();
            }else {
                activity.showToast(activity.getResources().getString(R.string.st_error));
            }

        }
    }

}