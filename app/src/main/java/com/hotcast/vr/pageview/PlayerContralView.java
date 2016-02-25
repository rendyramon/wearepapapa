package com.hotcast.vr.pageview;

import android.os.Handler;
import android.os.Message;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotcast.vr.BaseActivity;
import com.hotcast.vr.BaseApplication;
import com.hotcast.vr.PlayerContrallerInterface;
import com.hotcast.vr.PlayerVRActivityNew;
import com.hotcast.vr.R;
import com.hotcast.vr.tools.DensityUtils;
import com.hotcast.vr.tools.L;
import com.hotcast.vr.tools.Utils;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by joey on 8/12/15.
 */
public class PlayerContralView extends BaseView {

    @InjectView(R.id.btnBack)
    ImageView btnBack;
    @InjectView(R.id.tv_name)
    TextView tv_name;
    @InjectView(R.id.btnPlay)
    ImageView btnPlay;
    @InjectView(R.id.seekbar)
    DiscreteSeekBar seekbar;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.view)
    View view;
    @InjectView(R.id.ivTouchMode)
    ImageView touchMode;
    @InjectView(R.id.ivSplitScreen)
    ImageView splitScreen;
    @InjectView(R.id.choiceclarity)
    TextView choiceclarity;
    @InjectView(R.id.tv_uhd)
    TextView tv_uhd;
    @InjectView(R.id.tv_hd)
    TextView tv_hd;
    @InjectView(R.id.tv_sd)
    TextView tv_sd;
    @InjectView(R.id.ll_choice)
    LinearLayout ll_choice;

    @OnClick(R.id.btnBack)
    void clickBack() {
        activity.finish();
    }
    boolean isshow = false;

    public void setCanclick(boolean canclick) {
        this.canclick = canclick;
    }

    boolean canclick = true;
    boolean canclick1 = false;
    boolean canclick2 = false;
    boolean canclick3 = false;

    public void setCanclick1(boolean canclick1) {
        this.canclick1 = canclick1;
    }

    public void setCanclick2(boolean canclick2) {
        this.canclick2 = canclick2;
    }

    public void setCanclick3(boolean canclick3) {
        this.canclick3 = canclick3;
    }

    @OnClick(R.id.choiceclarity)
    void choiceclarity(){
        if (!canclick1){
            tv_sd.setVisibility(View.GONE);
        }
        if (!canclick2){
            tv_hd.setVisibility(View.GONE);
        }
        if (!canclick3){
            tv_uhd.setVisibility(View.GONE);
        }
//        tv_sd.setEnabled(canclick1);
//        tv_hd.setEnabled(canclick2);
//        tv_uhd.setEnabled(canclick3);
        if (!isshow) {
            ll_choice.setVisibility(View.VISIBLE);
            isshow = true;
        }else {
            ll_choice.setVisibility(View.GONE);
            isshow = false;
        }
        if(null != changeMode){
            changeMode.clickChoiceClarity();
        }
    }
    @OnClick(R.id.tv_sd)
    void tv_sd(){
        if(null != changeMode){
            choiceclarity.setText(BaseApplication.clarityText);
            changeMode.clickSd();
        }
    }
    @OnClick(R.id.tv_hd)
    void tv_hd(){
        if(null != changeMode){
            choiceclarity.setText(BaseApplication.clarityText);
            changeMode.clickHd();
        }
    }

    @OnClick(R.id.tv_uhd)
    void tv_uhd(){
        if(null != changeMode){
            choiceclarity.setText(BaseApplication.clarityText);
            changeMode.clickUhd();
        }
    }

    @OnClick(R.id.ivSplitScreen)
    void clickSplite(){
        if(null != changeMode){
            changeMode.clickSplitScreen();
        }

    }
//    public PlayerContralView(BaseActivity activity, int id,String clarityText){
//        super(activity,id);
//        choiceclarity.setText(clarityText);
//    }
    public void setSplitScreen(boolean isSplitScreen){
        LinearLayout.LayoutParams params =(LinearLayout.LayoutParams) touchMode.getLayoutParams();
        LinearLayout.LayoutParams params2 =(LinearLayout.LayoutParams) splitScreen.getLayoutParams();

        if (isSplitScreen){
            splitScreen.setImageResource(R.mipmap.screen);
            params.setMargins(DensityUtils.dp2px(getActivity(),5),0,DensityUtils.dp2px(getActivity(),10),0);
            params2.setMargins(0,0,DensityUtils.dp2px(getActivity(),10),0);

        }else {
            splitScreen.setImageResource(R.mipmap.split);
            params.setMargins(DensityUtils.dp2px(getActivity(),25),0,DensityUtils.dp2px(getActivity(),40),0);
            params2.setMargins(0,0,DensityUtils.dp2px(getActivity(),40),0);
        }
        touchMode.setLayoutParams(params);
        splitScreen.setLayoutParams(params2);
    }
    @OnClick(R.id.ivTouchMode)
    void clickTouchMode(){
        if(null != changeMode){
            changeMode.clickTouchMode();
        }
    }
    public void setTouchMode(boolean isTouch){
        if ( isTouch){
            touchMode.setImageResource(R.mipmap.rotate);
        }else {
            touchMode.setImageResource(R.mipmap.touch_mode);
        }
    }

    @OnClick(R.id.btnPlay)
    void clickPlay(){
        if(isPlaying()){
            if(curStatus == STATUS_PLAYING){
                setStatusStop();
                if(mPlayerContrallerInterface != null){
                    mPlayerContrallerInterface.pause();
                }
            }else{
                setStatusPlay();
                if(mPlayerContrallerInterface != null){
                    mPlayerContrallerInterface.play();
                }
            }
        }
    }

    private boolean isPlaying;
    private int totalDuration;
    private int curTime;
    private int min = 0, max = 0;
    private String totalTimeStr;
    private boolean bTouched;
//    private boolean isShowing;
    private int curStatus = STATUS_IDEL;

    private int seekPos;
    public static final int STATUS_IDEL = -1;
    public static final int STATUS_PLAYING = 1;
    public static final int STATUS_STOP = 0;
    public static final int TYPE_360 = 111, TYPE_3D = 222, TYPE_IMAX = 333;
    private int type;

    private ChangeModeListener changeMode;


    public void setChangeMode(ChangeModeListener changeMode) {
        this.changeMode = changeMode;
    }


    public PlayerContralView(BaseActivity activity, int type,String clarityText) {
        super(activity, R.layout.player_contral);
        choiceclarity.setEnabled(canclick);
        this.type = type;
        switch (type){
            case TYPE_360:
                splitScreen.setVisibility(View.VISIBLE);
                touchMode.setVisibility(View.VISIBLE);
                break;
            case TYPE_3D:
                splitScreen.setVisibility(View.GONE);
                touchMode.setVisibility(View.GONE);
                break;
            case TYPE_IMAX:
                splitScreen.setVisibility(View.VISIBLE);
                touchMode.setVisibility(View.GONE);
                break;
        }
        choiceclarity.setText(clarityText);
        seekbar.setIndicatorPopupEnabled(false);
        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar discreteSeekBar, int i, boolean b) {
                L.e("seek pos=" + i);
                seekPos = i;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                setbTouched(true);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar discreteSeekBar) {
                setbTouched(false);
                if (null != mPlayerContrallerInterface) {
                    mPlayerContrallerInterface.seekTo(seekPos);
                }
            }
        });
    }


    private boolean isbTouched() {
        return bTouched;
    }

    private void setbTouched(boolean bTouched) {
        this.bTouched = bTouched;
    }

    public void setStatusPlay() {
        btnPlay.setImageResource(R.mipmap.stop);
        curStatus = STATUS_PLAYING;
    }

    public void setStatusStop() {
        btnPlay.setImageResource(R.mipmap.play);
        curStatus = STATUS_STOP;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String cur = getTimeString(curTime);
            if (Utils.textIsNull(totalTimeStr)) {
                totalTimeStr = getTimeString(totalDuration);
            }
            tvTime.setText(cur + "/" + totalTimeStr);
            if(!isbTouched()){
                seekbar.setProgress(curTime);
            }
        }
    };
    public void setCurTime(int curTime) {
        this.curTime = curTime;
        handler.sendEmptyMessage(0);
    }


    public void setmPlayerContrallerInterface(PlayerContrallerInterface mPlayerContrallerInterface) {
        this.mPlayerContrallerInterface = mPlayerContrallerInterface;
    }

    private PlayerContrallerInterface mPlayerContrallerInterface;

    private boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }
    public void setTitle(String title){
        tv_name.setText(title);
    }

    private String getTimeString(int time) {
        return getHstr(time) + ":" + getMstr(time) + ":" + getSstr(time);
    }

    private String getHstr(int time) {
        int h = time / 60 / 60;
        return String.format("%02d", h);
    }

    private String getMstr(int time) {
        int m = time / 60 % 60;
        return String.format("%02d", m);
    }

    private String getSstr(int time) {
        int s = time % 60;
        return String.format("%02d", s);
    }

    public void setMin(int min) {
        this.min = min;
        seekbar.setMin(min);
    }

    public void setMax(int max) {
        this.max = max;
        seekbar.setMax(max);
    }

    public void show() {
        view.setVisibility(View.VISIBLE);
    }

    public void hide() {
        view.setVisibility(View.GONE);
    }

//    @Override
//    public void setText(String clarityText) {
//        choiceclarity.setText(clarityText);
//    }
}
