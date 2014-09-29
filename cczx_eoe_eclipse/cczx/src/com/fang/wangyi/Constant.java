package com.fang.wangyi;

/**
 * Created by changliang on 14-6-16.
 * email: clemu@qq.com
 */
public interface Constant {

    public static final String SPN_USER = "spn_user";
    public static final String SPN_USER_ISLOGIN = "spn_user_islogin";
    public static final String SPN_USER_NAME = "spn_user_name";
    public static final String SPN_USER_PASSWORD = "spn_user_password";

    public static final int LOAD_SUCCESS = 0;

    public static final String HTTP_SERVER = "http://applenews.365jilin.com";//服务器地址
    public static final String HTTP_IMAGE = "http://pic.365jilin.com";//图片地址

    public static final String HTTP_PICS = "/plus/list.php?tid=1130";//图片列表
    public static final String HTTP_NEWS = "/plus/list.php?tid=1307";//新闻首页图片
    public static final String HTTP_TOPS = "/plus/list.php?tid=1131";//排行列表
    public static final String HTTP_ZTS = "/plus/list.php?tid=1132";//专题列表

    public static final String HTTP_TABS = "/xinwen/index.xml"; //栏目
    public static final String HTTP_ABOUT = ""; //关于
    public static final String HTTP_REGISTER = "";
    public static final String HTTP_LOGIN = "";

}

