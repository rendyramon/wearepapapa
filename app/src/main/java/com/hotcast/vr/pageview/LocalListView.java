package com.hotcast.vr.pageview;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.R;
import com.hotcast.vr.bean.Details;
import com.hotcast.vr.imageView.Image3DSwitchView;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by liurongzhi on 2016/1/13.
 */
public class LocalListView extends BaseView {
    @InjectView(R.id.id_sv)
    Image3DSwitchView id_sv;
    ImageView cache_no;
    LinearLayout ll_downloading;
    TextView tv_pecent;
    TextView tv_speed;
    final String START = "START";
    final String DOWNLOADING = "DOWNLOADING";
    final String FINISH = "FINISH";
    final String PAUSE = "PAUSE";
    String play_url;
    String title;
    String media_id;
    private Details details;

    @OnClick(R.id.ivBack)
    void clickBack() {
        activity.finish();
    }

    public LocalListView(BaseActivity activity) {
        super(activity, R.layout.view_vr_gallery_cache);
        initView();
    }

    /**
     * @param flag true表示显示，false表示隐藏
     */
    public void hideOrShowCache_no(boolean flag) {
        if (flag) {
            cache_no.setVisibility(View.VISIBLE);
        } else {
            cache_no.setVisibility(View.GONE);
        }
    }

    public void hideOrShowLoading(boolean flag) {
        if (flag) {
            ll_downloading.setVisibility(View.VISIBLE);
        } else {
            ll_downloading.setVisibility(View.GONE);
        }
    }

    public void RefreshTextView(String text) {
        String[] texts = text.split(" ");
        tv_pecent.setText(texts[0]);
        tv_speed.setText(texts[1]);
    }

    public void initView() {
        cache_no = (ImageView) getRootView().findViewById(R.id.cache_no);
        ll_downloading = (LinearLayout) getRootView().findViewById(R.id.ll_downloading);
        tv_pecent = (TextView) getRootView().findViewById(R.id.tv_pecent);
        tv_speed = (TextView) getRootView().findViewById(R.id.tv_speed);
    }
}
