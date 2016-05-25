package com.hotcast.vr.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.hotcast.vr.tools.Constants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * Created by lostnote on 16/5/25.
 */
public class WXEntryActiviy extends Activity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        System.out.println("---resp.errCode:" + resp.errCode + ",resp.errStr:" + resp.errStr);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //分享成功
                System.out.println("---分享成功了");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //分享取消
                System.out.println("---分享取消");
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //分享拒绝
                System.out.println("---分享拒绝");
                break;
        }
    }
}
