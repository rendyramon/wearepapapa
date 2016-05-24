package com.hotcast.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dlodlo.dvr.sdk.unity.DvrUnityActivity;
import com.hotcast.vr.bean.LocalBean2;
import com.hotcast.vr.bean.MediaDownloadManager;
import com.hotcast.vr.tools.SharedPreUtil;
import com.hotcast.vr.tools.UnityTools;
import com.hotcast.vr.u3d.UnityPlayerActivity;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by lostnote on 15/11/23.
 */
public class ListLocalActivity extends BaseActivity {
    @InjectView(R.id.tv_title)
    TextView title;
    @InjectView(R.id.iv_return)
    ImageView iv_return;
    //    @InjectView(R.id.head)
//    RelativeLayout head;
    @InjectView(R.id.lv)
    GridView lv;
    @InjectView(R.id.bt_editor)
    Button bt_editor;
    //    @InjectView(R.id.fab)
//    FloatingActionButton fab;
    @InjectView(R.id.cache_no)
    ImageView cache_no;
    @InjectView(R.id.tv_downloded)
    TextView tv_downloded;
    @InjectView(R.id.tv_downloding)
    TextView tv_downloding;
    public boolean isFirstR = true;
    private boolean editor = false;
    int edit = 1;

    @OnClick(R.id.bt_editor)
    void clickEditor() {
        if (edit % 2 == 0) {
            editor = false;
        } else {
            editor = true;
        }
        edit++;
//        adapter.notify();
        lv.setAdapter(new HuancunAdapter());
        adapter.notifyDataSetChanged();
        System.out.println("你点击了编辑 editor = " + editor);

    }

    @OnClick(R.id.iv_return)
    void onReturn() {
        finish();
    }

    BitmapUtils bu;
    private HuancunAdapter adapter;

    //    private MyAdapter adapter;
    @Override
    public int getLayoutId() {
        return R.layout.view_local_list;
    }

    DbUtils db;
    List<LocalBean2> list = null;

    DetailReceiver receiver;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    List<String> ids;

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
        bu = new BitmapUtils(ListLocalActivity.this);
        title.setText(getResources().getString(R.string.mine_cache));
        iv_return.setVisibility(View.VISIBLE);
        initListView();
    }

    static class ViewHolder {
        TextView tv_finish;
        ImageView iv_huancun_img;//預覽圖
        ImageView iv_huancun_sd;//暫停或下載中
        TextView tv_huancun_downpecent;//下載進度
        TextView tv_huancun_downspeed;//下載速度
        TextView tv_huancun_moviename;//影片名稱
        ImageView ib_delete;
    }

    private void initListView() {
        db = DbUtils.create(ListLocalActivity.this);
        try {
            list = db.findAll(LocalBean2.class);
            if (list == null) {
                tv_downloded.setVisibility(View.GONE);
                tv_downloding.setVisibility(View.GONE);
                bt_editor.setVisibility(View.GONE);
                cache_no.setVisibility(View.VISIBLE);
            } else {
                ids = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("---本地数据获取：" + list.get(i).getVid());
                    ids.add(list.get(i).getUrl());
                }
                tv_downloded.setVisibility(View.VISIBLE);
                tv_downloding.setVisibility(View.VISIBLE);
                cache_no.setVisibility(View.GONE);
                bt_editor.setVisibility(View.VISIBLE);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (list == null || list.size() <= 0) {
            bt_editor.setVisibility(View.GONE);
            cache_no.setVisibility(View.VISIBLE);
        } else {
            cache_no.setVisibility(View.GONE);
            bt_editor.setVisibility(View.VISIBLE);
            if (adapter != null) {
                lv.setAdapter(adapter);
            } else {
                adapter = new HuancunAdapter();
                lv.setAdapter(adapter);
            }
        }
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String localurl = list.get(i).getLocalurl();
                int state = list.get(i).getCurState();
                System.out.println("***state：" + state);
                File file;
                if (localurl != null) {
                    file = new File(localurl);
                    System.out.println("---文件存在" + editor);
                } else {
                    file = new File(" ");
                    System.out.println("---文件不存在");
                }
                if (!editor) {
                    if (state == 3) {
//                        Intent intent = new Intent(ListLocalActivity.this, PlayerVRActivityNew2.class);
//                        intent.putExtra("play_url", localurl);
//                        intent.putExtra("qingxidu", list.get(i).getQingxidu());
//                        intent.putExtra("title", list.get(i).getTitle());
//                        intent.putExtra("splite_screen", false);
//                        ListLocalActivity.this.startActivity(intent);
//                        System.out.println("---开始播放");
//                        System.out.println("***播放：" + localurl);

                        Intent intent;
                        if (UnityTools.getGlasses().equals("1")) {
                            intent = new Intent(ListLocalActivity.this, DvrUnityActivity.class);
                        } else {
                            intent = new Intent(ListLocalActivity.this, UnityPlayerActivity.class);
                        }
                        SharedPreUtil.getInstance(ListLocalActivity.this).add("nowplayUrl", localurl);
                        SharedPreUtil.getInstance(ListLocalActivity.this).add("qingxidu", list.get(i).getQingxidu() + "");
                        SharedPreUtil.getInstance(ListLocalActivity.this).add("sdurl", "");
                        SharedPreUtil.getInstance(ListLocalActivity.this).add("hdrul", "");
                        SharedPreUtil.getInstance(ListLocalActivity.this).add("uhdrul", "");
                        if (localurl.contains("_3d_interaction")) {
                            SharedPreUtil.getInstance(ListLocalActivity.this).add("type", "3d");
                        } else if (localurl.contains("_vr_interaction")) {
                            SharedPreUtil.getInstance(ListLocalActivity.this).add("type", "vr_interaction");
                        } else if (localurl.contains("_3d_noteraction")) {
                            SharedPreUtil.getInstance(ListLocalActivity.this).add("type", "3d_noteraction");
                        } else {
                            SharedPreUtil.getInstance(ListLocalActivity.this).add("type", "vr");
                        }
                        ListLocalActivity.this.startActivity(intent);
                    } else if (state == 2) {
                        LocalBean2 localBean = list.get(i);
                        System.out.println("***开始" + list.get(i).getUrl());
                        BaseApplication.downLoadManager.addTask(localBean.getUrl(), localBean.getUrl(), localBean.getTitle() + ".mp4", BaseApplication.VedioCacheUrl + localBean.getTitle() + ".mp4");
                        list.get(i).setCurState(1);
                        try {
                            db.saveOrUpdate(localBean);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (adapter != null) {
//                            adapter.notifyDataSetInvalidated();
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new HuancunAdapter();
                            lv.setAdapter(adapter);
                        }
                    } else if (state == 1) {
                        BaseApplication.downLoadManager.stopTask(list.get(i).getUrl());
                        System.out.println("***停止" + list.get(i).getUrl());
                        list.get(i).setCurState(4);
                        try {
                            db.saveOrUpdate(list.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new HuancunAdapter();
                            lv.setAdapter(adapter);
                        }
                    } else if (state == 4) {
                        System.out.println("***继续" + list.get(i).getUrl());
                        BaseApplication.downLoadManager.startTask(list.get(i).getUrl());
                        list.get(i).setCurState(1);
                        try {
                            db.saveOrUpdate(list.get(i));
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        } else {
                            adapter = new HuancunAdapter();
                            lv.setAdapter(adapter);
                        }
                    }
                } else {
                    try {
                        if (list.get(i).getLocalurl() != null) {
                            delete(list.get(i).getLocalurl(), list.get(i).getImage());
                        }
                        db.delete(list.get(i));
                        list = db.findAll(LocalBean2.class);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    if (list != null) {
                        adapter = new HuancunAdapter();
                        lv.setAdapter(adapter);
                    } else {
                        bt_editor.setVisibility(View.GONE);
                        cache_no.setVisibility(View.VISIBLE);
                        startRefreshList();
                    }
                    System.out.println("****显示差号****删除一个item");
                }
            }
        });
        startRefreshList();
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            List<LocalBean2> list = null;
            try {
                list = db.findAll(LocalBean2.class);
            } catch (DbException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            startRefreshList();
        }
    };

    private void startRefreshList() {
        if (MediaDownloadManager.isDownloading(this)) {
            handler.removeMessages(0);
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    @Override
    public void getIntentData(Intent intent) {

    }

    class HuancunAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LocalBean2 bean = list.get(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ListLocalActivity.this, R.layout.huancun_grid_item, null);
                holder.tv_finish = (TextView) convertView.findViewById(R.id.tv_finish);
                holder.iv_huancun_img = (ImageView) convertView.findViewById(R.id.iv_huancun_img);
                holder.iv_huancun_sd = (ImageView) convertView.findViewById(R.id.iv_huancun_sd);
                holder.ib_delete = (ImageView) convertView.findViewById(R.id.ib_delete);
                holder.tv_huancun_downpecent = (TextView) convertView.findViewById(R.id.tv_huancun_downpecent);
                holder.tv_huancun_downspeed = (TextView) convertView.findViewById(R.id.tv_huancun_downspeed);
                holder.tv_huancun_moviename = (TextView) convertView.findViewById(R.id.tv_huancun_moviename);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (!bean.getImage().equals(holder.iv_huancun_img.getTag())) {
                bu.display(holder.iv_huancun_img, bean.getImage());
                holder.iv_huancun_img.setTag(bean.getImage());
            }
            holder.tv_huancun_moviename.setText(bean.getTitle());
            String speed = speeds.get(bean.getUrl());
            holder.tv_finish.setVisibility(View.GONE);
            holder.iv_huancun_sd.setVisibility(View.VISIBLE);
            holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
            holder.tv_huancun_downspeed.setVisibility(View.VISIBLE);
            holder.tv_huancun_downpecent.setVisibility(View.VISIBLE);
//            System.out.println("---adapter：" + speed);
            if (isFirstR) {
                holder.tv_huancun_downpecent.setText("已下载" + bean.getPecent() + "%");
                isFirstR = false;
            } else {
                if (speed != null) {
                    if ("FINISH".equals(speed)) {
                        holder.tv_finish.setVisibility(View.VISIBLE);
                        holder.iv_huancun_sd.setVisibility(View.GONE);
                        holder.tv_huancun_downspeed.setVisibility(View.GONE);
                        holder.tv_huancun_downpecent.setVisibility(View.GONE);
                    } else if (speed.contains("PAUSE")) {
                        String[] strs = speed.split(" ");
                        holder.iv_huancun_sd.setVisibility(View.VISIBLE);
                        holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
                        holder.tv_huancun_downspeed.setText("0KB/S");
                        if (strs.length > 2) {
                            holder.tv_huancun_downpecent.setText(strs[2]);
                        } else {
                            holder.tv_huancun_downpecent.setText("已下载0%");
                        }
                    } else {
                        String[] strs = speed.split(" ");
                        holder.tv_huancun_downspeed.setText(strs[0]);
                        holder.tv_huancun_downpecent.setText(strs[1]);
                    }
                }
            }
            if (bean.getCurState() == 3) {
                holder.tv_finish.setVisibility(View.VISIBLE);
                holder.iv_huancun_sd.setVisibility(View.GONE);
                holder.tv_huancun_downspeed.setVisibility(View.GONE);
                holder.tv_huancun_downpecent.setVisibility(View.GONE);
            } else if (bean.getCurState() == 1) {
                holder.iv_huancun_sd.setVisibility(View.VISIBLE);
                holder.tv_huancun_downspeed.setVisibility(View.VISIBLE);
                holder.tv_huancun_downpecent.setVisibility(View.VISIBLE);
                holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_img);
            } else if (bean.getCurState() == 4 || bean.getCurState() == 2) {
                holder.iv_huancun_sd.setVisibility(View.VISIBLE);
                holder.tv_huancun_downspeed.setVisibility(View.VISIBLE);
                holder.tv_huancun_downpecent.setVisibility(View.VISIBLE);
                holder.iv_huancun_sd.setBackgroundResource(R.mipmap.huancun_sb);
            }
            if (editor) {
                holder.ib_delete.setVisibility(View.VISIBLE);
            } else {
                holder.ib_delete.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    Map<String, String> speeds;//下载速度的集合
    long pecent = 0;
    String action;
    String speed;
    long refreshTime = 0;

    public void adapterRefresh(String play_url, long current, long total) {
//        if (System.currentTimeMillis() - refreshTime > 1000) {
//
//        }
        refreshTime = System.currentTimeMillis();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new HuancunAdapter();
            lv.setAdapter(adapter);
        }
        int index = ids.indexOf(play_url);
        LocalBean2 localBean = null;
        try {
            localBean = db.findById(LocalBean2.class, play_url);
            if (localBean != null) {
                localBean.setSpeed((current - localBean.getCurrent()) / 1024 + "");
                localBean.setCurrent(current);
                localBean.setPecent(((current * 100) / total) + "");
                db.saveOrUpdate(localBean);
                list.get(index).setSpeed(localBean.getSpeed());
                list.get(index).setPecent(localBean.getPecent());
                list.get(index).setCurrent(current);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void adapterRefresh() {
//        if (System.currentTimeMillis() - refreshTime > 1000) {
//
//        }
        refreshTime = System.currentTimeMillis();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        } else {
            adapter = new HuancunAdapter();
            lv.setAdapter(adapter);
        }
    }

    /**
     * 这是接受下载进度的广播接受者
     */
    class DetailReceiver extends BroadcastReceiver {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            String play_url = intent.getStringExtra("play_url");//電影的下載地址，作為電影的唯一标识
            if ("DOWNLOADING".equals(action)) {
                //执行相应操作
                long total = intent.getLongExtra("total", -1);//电影总大小
                long current = intent.getLongExtra("current", -1);//电影当前进度
                if (pecent == 0) {
                    pecent = current;
                } else {
                    long s = (Math.abs((current - pecent))) / 1024 > 12000 ? 2000 + ((Math.abs((current - pecent))) / 10240000) : ((Math.abs((current - pecent))) / 1024);
                    speed = s + "KB/S" + " 已下载" + (current * 100) / total + "%";
                    pecent = current;
                    speeds.put(play_url, speed);
                }
                System.out.println("---接收到的信息：" + speed);
                adapterRefresh(play_url, current, total);
            } else if ("FINISH".equals(action)) {
//              下載完畢，執行下載完畢的邏輯
                speeds.put(play_url, "FINISH");
                String localurl = intent.getStringExtra("localurl");
                System.out.println("----FINISH接收到广播:" + play_url);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("----集合中的網絡地址:" + list.get(i).getUrl());
                    if (list.get(i).getUrl().equals(play_url)) {
                        try {
                            LocalBean2 localBean = db.findById(LocalBean2.class, play_url);
                            if (localBean != null) {
                                localBean.setCurState(3);
                                localBean.setLocalurl(localurl);
                                db.saveOrUpdate(localBean);
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }

                        list.get(i).setLocalurl(localurl);
                        list.get(i).setCurState(3);//下载完成
                        System.out.println("----接收到信息地址:" + localurl);
                    }
                }
                System.out.println("---接收到的信息：" + "FINISH");
                adapterRefresh();
            } else if ("PAUSE".equals(action)) {
                String speed = speeds.get(play_url);
                speeds.put(play_url, "PAUSE " + speed);
                adapterRefresh();
            } else if ("START".equals(action)) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUrl().equals(play_url)) {
                        list.get(i).setCurState(1);
                    }
                }
                adapterRefresh();
            } else if ("ERROR".equals(action)) {
                String speed = speeds.get(play_url);
                speeds.put(play_url, "PAUSE " + speed);
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getUrl().equals(play_url)) {
                        list.get(i).setCurState(4);
                    }
                }
                adapterRefresh();
            }
        }

    }

    public boolean delete(String fileName, String img) {
        System.out.println("---filename:" + fileName);

        //SDPATH目录路径，fileName文件名

        File file = new File(fileName.replace("file://",""));
        if (file == null || !file.exists() || file.isDirectory()) {
            System.out.println("---文件没有删除" + fileName);
            return false;
        }
        file.delete();
        File imgFile = new File(img);
        if (imgFile == null || !imgFile.exists() || imgFile.isDirectory()) {
            return false;
        }
        imgFile.delete();
        return true;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }
}
