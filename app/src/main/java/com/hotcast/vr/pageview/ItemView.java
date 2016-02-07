package com.hotcast.vr.pageview;


import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.DetailActivity;
import com.hotcast.vr.bean.Datas;
import com.hotcast.vr.bean.HomeRoll;
import com.hotcast.vr.bean.HomeSubject;
import com.hotcast.vr.bean.RollBean;
import com.lidroid.xutils.BitmapUtils;

import com.hotcast.vr.R;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.core.BitmapSize;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class ItemView extends LinearLayout {

    private ImageView titleimg;
    private TextView title;
    private LinearLayout ll_img;
    private HorizontalScrollView hs_view;
    private Context context;
    private int positon;
    private List<HomeRoll> homeRolls;
    private static BitmapUtils bu;

    public interface MovieImgClickLisenter {
        public void movieImgClick();
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public ItemView(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }

    /**
     * ��ʼ��View�ķ���
     *
     * @param context
     */
    public void initView(Context context) {
        if (BaseApplication.bu == null) {
            bu = new BitmapUtils(context);
            BaseApplication.bu = bu;
        } else {
            bu = BaseApplication.bu;
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_view, this);
        titleimg = (ImageView) findViewById(R.id.titleimg);
        title = (TextView) findViewById(R.id.title);
        ll_img = (LinearLayout) findViewById(R.id.ll_img);
        hs_view = (HorizontalScrollView) findViewById(R.id.hs_view);
//        hs_view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("--- 条目被点击了");
//            }
//        });
    }

    public int getscrollL() {
        return hs_view.getScrollX();
    }

    public void setItemList(Context context, RollBean item, int i) {
        setPositon(i);
        String titleurl = item.getLogo();
        String titleText = item.getTitle();
//        初始化条目头信息
        title.setText(titleText);
        bu.display(titleimg, titleurl);

//        初始化下面的横向滑动条目
        setItemMovies(context, item.getData());

    }

    public void setItemMovies(final Context context, List<Datas> rolls) {
        ll_img.removeAllViews();
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 30, 0);
        for (int i = 0; i < rolls.size(); i++) {
            final Datas roll = rolls.get(i);
            System.out.println("---ItemView  roll---" + roll);

            LinearLayout contentView = (LinearLayout) View.inflate(context,
                    R.layout.item_item_img, null);
            ImageView iv_movie = (ImageView) contentView
                    .findViewById(R.id.iv_movie);

            if (roll.getImage() != null) {
                bu.display(iv_movie, roll.getImage());
            }
            TextView tv_movie = (TextView) contentView
                    .findViewById(R.id.tv_movie);
            tv_movie.setText(roll.getTitle());
            iv_movie.setFocusable(true);
            iv_movie.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("videoset_id", roll.getMedia_id());
//                    intent.putExtra("resource",roll.getResource());
                    context.startActivity(intent);
                    System.out.println("---ItemView 条目被点击了---");
                }
            });
            contentView.setLayoutParams(params);
            ll_img.addView(contentView);
        }
    }

    public void setOnMovieImgClickLisenter(MovieImgClickLisenter movieClickLisenter) {
        movieClickLisenter.movieImgClick();
    }


    public void setTitleImg(int id) {
        titleimg.setBackgroundResource(id);
    }

    /**
     * ���ñ����������ݺʹ�С
     *
     * @param text
     * @param size :�����0.0,������ΪĬ�ϴ�С
     */
    public void setTitleText(String text, float size) {
        title.setText(text);
        if (size != 0.0) {

        }
        title.setTextSize(size);
    }
}
