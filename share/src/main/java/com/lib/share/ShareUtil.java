package com.lib.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.lib.share.share.ShareContentType;
import com.lib.share.share.ShareImage;
import com.lib.share.share.ShareListener;
import com.lib.share.share.SharePlatform;
import com.lib.share.share.handler.IShareHandler;
import com.lib.share.share.handler.qq.QQFriendShareHandler;
import com.lib.share.share.handler.qq.QZoneShareHandler;
import com.lib.share.share.handler.weibo.WeiboShareHandler;
import com.lib.share.share.handler.wx.WXHandler;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * Created by tengtao on 2018/4/8.
 */

public class ShareUtil {


    public static final int TYPE = 798;


    public static ShareListener mShareListener;
    private static IShareHandler mShareHandler;
    @SharePlatform.Platform
    private static int mPlatform;

    @ShareContentType.Platform
    private static int mType;


    private static String mText;
    private static String mTitle;
    private static String mSummary;
    private static ShareImage mShareImage;


    static ShareConfig shareConfig;
    private static String mTargetUrl;

    public static void init(Context context, ShareConfig config) {
        shareConfig = config;
        if (!TextUtils.isEmpty(config.getWeiboId())) {
            WbSdk.install(context.getApplicationContext(), new AuthInfo(context.getApplicationContext(), config.getWeiboId(), config.getWb_redirect_url(), config.getWb_scope()));
        }
    }


    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 微博分享会同时回调onActivityResult和onNewIntent， 而且前者返回的intent为null
        if (mShareHandler != null && data != null) {
            mShareHandler.onActivityResult(requestCode, resultCode, data);
        } else if (data == null) {
            if (mPlatform != SharePlatform.WEIBO) {
//                Handle the result, but the data is null, please check you app id
            }
        } else {
//            Unknown error
        }
    }

    static void action(Activity activity) {

        mShareHandler = getShareHandler(mPlatform, activity);
        if (mShareHandler == null) {
            activity.finish();
            return;
        }

        if (!mShareHandler.isInstall(activity) && mPlatform != SharePlatform.WEIBO) {
            mShareListener.shareFailure(mPlatform, new Exception(activity.getString(R.string.share_not_install)));
            activity.finish();
            return;
        }


        if (mType == ShareContentType.TYPE_TEXT) {
            mShareHandler.shareText(mPlatform, mText, activity, mShareListener);
        } else if (mType == ShareContentType.TYPE_IMAGE) {
            mShareHandler.shareImage(mPlatform, mShareImage, activity, mShareListener);
        }else if (mType==ShareContentType.TYPE_MEDIA){
            mShareHandler.shareMedia(mPlatform,mTitle,mTargetUrl,mSummary,mShareImage,activity,mShareListener);
        }else {
            activity.finish();
        }


    }

    private static IShareHandler getShareHandler(int platform, Activity activity) {
        if (platform == SharePlatform.QQ) {
            return new QQFriendShareHandler(activity, shareConfig.getQqId());
        } else if (platform == SharePlatform.QZONE) {
            return new QZoneShareHandler(activity, shareConfig.getQqId());
        } else if (platform == SharePlatform.WX || platform == SharePlatform.WX_TIMELINE) {
            return new WXHandler(activity, shareConfig.getWxId());
        } else if (platform == SharePlatform.WEIBO) {
            return new WeiboShareHandler(activity);
        }
        return null;
    }

    public static void shareText(Context context, @SharePlatform.Platform int platform, String text,
                                 ShareListener listener) {
        if (!isPlatformValid(platform)) {
            if (listener != null) {
                listener.shareFailure(platform, new Exception("未初始化或不支持该平台分享"));
            }
            return;
        }
        mType = ShareContentType.TYPE_TEXT;
        mText = text;
        mPlatform = platform;
        mShareListener = listener;
        context.startActivity(ShareActivity.newInstance(context, TYPE));
    }

    public static void shareImage(Context context, @SharePlatform.Platform final int platform,
                                  final String urlOrPath, ShareListener listener) {
        mType = ShareContentType.TYPE_IMAGE;
        mPlatform = platform;
        mShareImage = new ShareImage(urlOrPath);
        mShareListener = listener;
        context.startActivity(ShareActivity.newInstance(context, TYPE));
    }

    public static void shareImage(Context context, @SharePlatform.Platform final int platform,
                                  final Bitmap bitmap, ShareListener listener) {
        mType = ShareContentType.TYPE_IMAGE;
        mPlatform = platform;
        mShareImage = new ShareImage(bitmap);
        mShareListener = listener;
        context.startActivity(ShareActivity.newInstance(context, TYPE));
    }


    public static void shareMedia(Context context, @SharePlatform.Platform int platform,
                                  String title, String summary, String targetUrl, Bitmap thumb, ShareListener listener) {
        mType = ShareContentType.TYPE_MEDIA;
        mPlatform = platform;
        mShareImage = new ShareImage(thumb);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener =listener;

        context.startActivity(ShareActivity.newInstance(context, TYPE));
    }

    public static void shareMedia(Context context, @SharePlatform.Platform int platform,
                                  String title, String summary, String targetUrl, String thumbUrlOrPath,
                                  ShareListener listener) {
        mType = ShareContentType.TYPE_MEDIA;
        mPlatform = platform;
        mShareImage = new ShareImage(thumbUrlOrPath);
        mSummary = summary;
        mTargetUrl = targetUrl;
        mTitle = title;
        mShareListener =listener;

        context.startActivity(ShareActivity.newInstance(context, TYPE));
    }

    private static void recycle() {

        mTitle = null;
        mSummary = null;
        mShareListener = null;
        mTargetUrl=null;

        // bitmap recycle
        if (mShareImage != null
                && mShareImage.getBitmap() != null
                && !mShareImage.getBitmap().isRecycled()) {
            mShareImage.getBitmap().recycle();
        }
        mShareImage = null;

        if (mShareHandler != null) {
            mShareHandler.recycle();
        }
        mShareHandler = null;


    }


    private static boolean isPlatformValid(int platform) {
        if (shareConfig == null) return false;

        switch (platform) {
            case SharePlatform.QQ:
            case SharePlatform.QZONE:
                return !TextUtils.isEmpty(shareConfig.getQqId());
            case SharePlatform.WX:
            case SharePlatform.WX_TIMELINE:
                return !TextUtils.isEmpty(shareConfig.getWxId()) && !TextUtils.isEmpty(shareConfig.getWxsecret());
            case SharePlatform.WEIBO:
                return !TextUtils.isEmpty(shareConfig.getWeiboId());
            default:
                return false;
        }
    }
}
