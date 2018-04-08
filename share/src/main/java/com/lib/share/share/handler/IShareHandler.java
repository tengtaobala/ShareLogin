package com.lib.share.share.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lib.share.share.ShareImage;
import com.lib.share.share.ShareListener;
import com.lib.share.share.SharePlatform;

/**
 * Created by tengtao on 2018/4/8.
 */

public interface IShareHandler {
    /**
     * 分享文字
     *
     * @param platform
     * @param text
     * @param listener
     */
    void shareText(@SharePlatform.Platform int platform, String text, Activity activity, ShareListener listener);

    void shareMedia(@SharePlatform.Platform int platform, String title, String targetUrl, String summary,
                    ShareImage shareImage, Activity activity, ShareListener listener);

    void shareImage(@SharePlatform.Platform int platform, ShareImage shareImage,
                    Activity activity, ShareListener listener);


    void onActivityResult(int requestCode, int resultCode, Intent data);

    boolean isInstall(Context context);

    void recycle();

}
