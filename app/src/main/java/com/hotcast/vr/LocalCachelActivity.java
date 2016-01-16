package com.hotcast.vr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hotcast.vr.bean.LocalBean;
import com.hotcast.vr.imageView.Image3DSwitchView;
import com.hotcast.vr.imageView.Image3DView;
import com.hotcast.vr.pageview.LocalListView;
import com.hotcast.vr.pageview.RefreshListView;
import com.hotcast.vr.pageview.VrListView;
import com.hotcast.vr.tools.VedioBitmapUtils;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class LocalCachelActivity extends BaseActivity {
    @InjectView(R.id.container1)
    RelativeLayout container1;
    @InjectView(R.id.container2)
    RelativeLayout container2;

    private LocalListView view1, view2;
    private com.hotcast.vr.imageView.Image3DSwitchView img3D, img3D2;
    private String requestUrl;
    private int type;
    Spannable span;
    String page;
    TextView tv_page1;
    TextView tv_page2;
    TextView tv_title1;
    TextView tv_title2;
    TextView tv_desc1;
    TextView tv_desc2;
    TextView tv_pecent;
    TextView tv_speed;
    int index = 1;
    /**
     * 控件宽度
     */
    public static int mWidth;
    //缓存的视频名称（不包含前缀地址）
    private List<LocalBean> dbList;
    DbUtils db;
    BitmapUtils bitmapUtils;
    private DetailReceiver receiver;
    //db原始尺寸
    private int trueSize;

    @Override
    public int getLayoutId() {
        return R.layout.activity_vr_list;
    }

    @Override
    public void init() {
        receiver = new DetailReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(START);
        filter.addAction(DOWNLOADING);
        filter.addAction(FINISH);
        filter.addAction(PAUSE);
        registerReceiver(receiver, filter);
        speeds = new HashMap<>();
        db = DbUtils.create(this);

        if (dbList.size() > 0 && dbList.size() < 5) {
            trueSize = dbList.size();
            addSelfToFive();
        }
        BaseApplication.size = dbList == null ? 0 : dbList.size();
    }

    public void addSelfToFive() {
        dbList.addAll(dbList);
        if (dbList.size() < 5) {
            addSelfToFive();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        view1 = new LocalListView(this);
        view2 = new LocalListView(this);
        if (dbList.size() <= 0) {
            view1.hideOrShowCache_no(true);
            view2.hideOrShowCache_no(true);
        } else {
            //显示逻辑
            view1.hideOrShowCache_no(false);
            view2.hideOrShowCache_no(false);
            System.out.println("---数据库：" + dbList.size());
            init3DView();
        }
    }

    public void init3DView() {
        bitmapUtils = new BitmapUtils(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        img3D = (Image3DSwitchView) view1.getRootView().findViewById(R.id.id_sv);
        img3D2 = (Image3DSwitchView) view2.getRootView().findViewById(R.id.id_sv);
        tv_page1 = (TextView) view1.getRootView().findViewById(R.id.tv_page);
        tv_page2 = (TextView) view2.getRootView().findViewById(R.id.tv_page);

        tv_title1 = (TextView) view1.getRootView().findViewById(R.id.tv_title);
        tv_title2 = (TextView) view2.getRootView().findViewById(R.id.tv_title);
//        tv_desc1 = (TextView) view1.getRootView().findViewById(R.id.tv_desc);
//        tv_desc2 = (TextView) view2.getRootView().findViewById(R.id.tv_desc);
        page = index + "/" + trueSize;
        span = new SpannableString(page);
        span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_page1.setText(span);
        tv_page2.setText(span);
        tv_title1.setText(dbList.get(index - 1).getTitle());
        tv_title2.setText(dbList.get(index - 1).getTitle());
        if (dbList.get(index - 1).getCurState() == 3) {
            view1.hideOrShowLoading(false);
            view2.hideOrShowLoading(false);
        } else {
            view1.hideOrShowLoading(true);
            view2.hideOrShowLoading(true);
        }
        for (int i = 0; i < dbList.size(); i++) {
            final int index = i;
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imgurl = dbList.get(i).getImage();
//            if (imgurl == null || imgurl.equals("")) {
////                dbList.get(i).setLocalBitmap(VedioBitmapUtils.getMicroVedioBitmap(dbList.get(i).getLocalurl()));
////                image3DView.setImageBitmap(VedioBitmapUtils.getMicroVedioBitmap(dbList.get(i).getLocalurl()));
//                bitmapUtils.display(image3DView, imgurl);
//            } else {
            bitmapUtils.display(image3DView, imgurl);
//            }
            image3DView.setLayoutParams(params);
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickVedio(index);
                }
            });
            img3D.addView(image3DView);
        }
        for (int i = 0; i < dbList.size(); i++) {
            final int index = i;
            Image3DView image3DView = new Image3DView(this);
            image3DView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String imgurl = dbList.get(i).getImage();
//            if (imgurl == null || imgurl.equals("")) {
////                dbList.get(i).setLocalBitmap(VedioBitmapUtils.getMicroVedioBitmap(dbList.get(i).getLocalurl()));
////                image3DView.setImageBitmap(VedioBitmapUtils.getMicroVedioBitmap(dbList.get(i).getLocalurl()));
//                image3DView.setImageResource(R.drawable.ic_launcher);
//            } else {
            bitmapUtils.display(image3DView, imgurl);
//            }
            image3DView.setLayoutParams(params);
            image3DView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickVedio(index);
                }
            });
            img3D2.addView(image3DView);
        }
        allChange();
        container1.addView(view1.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container2.addView(view2.getRootView(), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private boolean editor = false;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";

    public void clickVedio(int i) {
        String localurl = dbList.get(i).getLocalurl();
        File file;
        if (localurl != null) {
            file = new File(localurl);
        } else {
            file = new File(" ");
        }
        if (!editor) {
            if (localurl != null && file.exists()) {
//                        System.out.println("---本地地址：" + localurl + "---url:" + list.get(i).getUrl());
                Intent intent = new Intent(LocalCachelActivity.this, PlayerVRActivityNew.class);
                intent.putExtra("play_url", localurl);
                intent.putExtra("splite_screen", false);
                LocalCachelActivity.this.startActivity(intent);
            } else if (localurl == null) {
                if (dbList.get(i).getCurState() == -1) {
                    System.out.print("---重新下载");
                    Intent intent = new Intent(START);
                    LocalCachelActivity.this.sendBroadcast(intent);
                    try {
                        db.delete(dbList.get(i));
                        dbList.get(i).setCurState(0);
                        db.save(dbList.get(i));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                } else if (dbList.get(i).getCurState() == 0) {
                    System.out.print("---暂停");
                    try {
                        db.delete(dbList.get(i));
                        dbList.get(i).setCurState(-1);
                        db.save(dbList.get(i));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(PAUSE);
                    intent.putExtra("index", i);
                    sendBroadcast(intent);
                }
            }
        } else {
//            try {
//                if (list.get(i).getLocalurl() != null) {
//                    delete(list.get(i).getLocalurl());
//                }
//                db.delete(list.get(i));
//                list = db.findAll(LocalBean.class);
//
//            } catch (DbException e) {
//                e.printStackTrace();
//            }
//            if (list != null) {
//                adapter = new HuancunAdapter();
//                lv.setAdapter(adapter);
//            } else {
//                bt_editor.setVisibility(View.GONE);
//                cache_no.setVisibility(View.VISIBLE);
//                startRefreshList();
//            }
//            System.out.println("****显示差号****删除一个item");
        }
    }

    @Override
    public void getIntentData(Intent intent) {
        dbList = (ArrayList<LocalBean>) getIntent().getSerializableExtra("dbList");
    }

    public void allChange() {
        img3D.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D2.scrollBy(dix, 0);
                img3D2.refreshImageShowing();
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D2.scrollToNext();
                ++index;
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D2.scrollToPrevious();
                --index;
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D2.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }
        });
        img3D2.setOnMovechangeListener(new Image3DSwitchView.OnMovechangeListener() {
            @Override
            public void OnMovechange(int dix) {
                System.out.println("-----1執行");
                img3D.scrollBy(dix, 0);
                img3D.refreshImageShowing();
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Next() {
                System.out.println("-----2執行");
                img3D.scrollToNext();
                ++index;
                if (index <= trueSize) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = 1;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Previous() {
                System.out.println("-----3執行");
                img3D.scrollToPrevious();
                --index;
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }

            @Override
            public void Back() {
                System.out.println("-----4執行");
                img3D.scrollBack();
//                tv_page1.setText(index+"/"+vrPlays.size());
//                tv_page2.setText(index + "/" + vrPlays.size());
                if (index > 0) {
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                } else {
                    index = trueSize;
                    page = index + "/" + trueSize;
                    span = new SpannableString(page);
                    span.setSpan(new ForegroundColorSpan(LocalCachelActivity.this.getResources().getColor(R.color.material_blue_500)),
                            0, page.length() - 1 - ("" + trueSize).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_page1.setText(span);
                    tv_page2.setText(span);
                    tv_title1.setText(dbList.get(index - 1).getTitle());
                    tv_title2.setText(dbList.get(index - 1).getTitle());
                }
                if (dbList.get(index - 1).getCurState() == 3) {
                    view1.hideOrShowLoading(false);
                    view2.hideOrShowLoading(false);
                } else {
                    view1.hideOrShowLoading(true);
                    view2.hideOrShowLoading(true);
                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    //刷新电影下载状况
                    RefreshDownLoading();
                    break;
            }
        }
    };
    Map<String, String> speeds;//下载速度的集合
    long pecent = 0;
    String action;

    public void RefreshDownLoading() {

//        String indexSpeed = speeds.get(dbList.get(index-1).getUrl());
//        view1.RefreshTextView(indexSpeed);
//        view2.RefreshTextView(indexSpeed);

    }

    /**
     * 这是接受下载进度的广播接受者
     */
    class DetailReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String speed = "";
            action = intent.getAction();
            String play_url = intent.getStringExtra("play_url");//電影的下載地址，作為電影的唯一标识
            if ("DOWNLOADING".equals(action)) {
                //执行相应操作
                long total = intent.getLongExtra("total", -1);//电影总大小
                long current = intent.getLongExtra("current", -1);//电影当前进度
                if (pecent == 0) {
                    pecent = current;
                } else {
                    speed = (current - pecent) / 1024 + "KB/S" + " 已下载" + (current * 100) / total + "%";
                    pecent = current;
                    speeds.put(play_url, speed);
                    mHandler.sendEmptyMessage(100);
                }
                System.out.println("---接收到的信息：" + speed);
//
            } else if ("FINISH".equals(action)) {
//                下載完畢，執行下載完畢的邏輯
                speeds.put(play_url, "FINISH");
                String localurl = intent.getStringExtra("localurl");
                System.out.println("----接受到的網絡地址:" + play_url);
                for (int i = 0; i < dbList.size(); i++) {
                    System.out.println("----集合中的網絡地址:" + dbList.get(i).getUrl());
                    if (dbList.get(i).getUrl().equals(play_url)) {
                        try {
                            db.delete(dbList.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        dbList.get(i).setLocalurl(localurl);
                        dbList.get(i).setCurState(2);//下载完成
                        System.out.println("----接收到信息地址:" + localurl);

                        try {
                            db.save(dbList.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                System.out.println("---接收到的信息：" + "FINISH");
//                if (adapter != null) {
//                    adapter.notifyDataSetInvalidated();
//                } else {
//                    adapter = new HuancunAdapter();
//                    lv.setAdapter(adapter);
//                }
            } else if ("PAUSE".equals(action)) {
//                String speed = speeds.get(play_url);
//                intent.getIntExtra("index", -1);
//                speeds.put(play_url, "PAUSE " + speed);
//                if (adapter != null) {
//                    adapter.notifyDataSetInvalidated();
//                    adapter.notifyDataSetChanged();
//                } else {
//                    adapter = new HuancunAdapter();
//                    lv.setAdapter(adapter);
//                }

            }
        }
    }
}
