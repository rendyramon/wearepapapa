package com.hotcast.vr.tools;

public class Constants {
    //    地址前缀
    public static final String SERVER_URL = "http://api2.hotcast.cn/index.php?r=";
    //    频道列表
    public static final String CHANNEL_LIST = SERVER_URL + "/app2/channel/get-list";
    //    频道下节目集列表
    public static final String PROGRAM_LIST = SERVER_URL + "/app2/channel/get-video";
    //    节目详情
    public static final String DETAIL = SERVER_URL + "/app2/videoset/get-detail";
    //    播放地址
    public static final String PLAY_URL = SERVER_URL + "/app2/videoset/play";
    //    轮播推荐位
    public static final String ROLL = SERVER_URL + "/app2/recommend/get-roll";
    //    专题推荐位
    public static final String SPECIAL = SERVER_URL + "/app2/recommend/get-subjects";
    //    版本更新接口
    public static final String URL_UPDATE = SERVER_URL + "/app2/info/update";
    public static final String RELATION = SERVER_URL + "/app2/videoset/get-relation";
    //  发送短信
    public static final String SENDMESSAG = SERVER_URL + "/member/sms/send-message";
    //    验证注册码
    public static final String CHECKMESSAG = SERVER_URL + "/member/sms/check-message";

    //    注册
    public static final String REGIST = SERVER_URL + "/member/user/register";
    //    登录
    public static final String LOGIN = SERVER_URL + "/member/user/login";
    //    注销
    public static final String LOGOUT = SERVER_URL + "/member/user/logout";
    //    更改用户名
    public static final String RENAME = SERVER_URL + "/member/user/set-username";
    //    忘记密码
    public static final String FORGET = SERVER_URL + "/member/user/forget-password";
    //上传头像
    public static final String UPHEAD = SERVER_URL + "/member/user/upload-avatar";
    //    修改密码
    public static final String CHANGPASSWORD = SERVER_URL + "/member/user/reset-password";
    //    获取用户信息
    public static final String INFO = SERVER_URL + "/member/user/info";
    //获取评论
    public static final String PINGLUN = SERVER_URL + "/member/comment/get-comment";


//    地址前缀
    public static final String SERVER = "http://api.hotcast.cn/index.php?r=v20";
//    首页地址接口
    //    分类菜单接口
    public static final String URL_CLASSIFY_TITLTE = SERVER + "/channel/get-channel";
    public static final String URL_VR_PLAY = SERVER + "/channel/get-row-video";
    //    提交评论
    public static final String SENDPINGLUN = SERVER_URL + "/member/comment/send-comment";
    public static final String GETPINGLUN = SERVER_URL + "/member/comment/get-comment";
//    建议反馈
    public static final String FEEDBACK = SERVER_URL + "/app2/info/feed-back";

}