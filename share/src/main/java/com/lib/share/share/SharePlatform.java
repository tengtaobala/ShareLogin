package com.lib.share.share;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tengtao on 2018/4/8.
 */

public class SharePlatform {
    @IntDef({DEFAULT, QQ, QZONE, WEIBO, WX, WX_TIMELINE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platform {
    }

    public static final int DEFAULT = 0;
    public static final int QQ = 1;
    public static final int QZONE = 2;
    public static final int WX = 3;
    public static final int WX_TIMELINE = 4;
    public static final int WEIBO = 5;


    public static String getPlatformValue(@Platform int platform) {
        switch (platform) {
            case QQ:
                return "QQ";
            case QZONE:
                return "QQ空间";
            case WX:
                return "微信好友";
            case WX_TIMELINE:
                return "微信朋友圈";
            case WEIBO:
                return "新浪微博";
            default:
                return "默认";

        }
    }
}
