package com.lib.share.share.handler.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lib.share.R;
import com.lib.share.share.ShareListener;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

/**
 * Created by tengtao on 2018/4/8.
 */

public class QQFriendShareHandler extends QQShareHandler {


    public QQFriendShareHandler(Context context, String appId) {
        super(context, appId);
    }

    @Override
    protected void doShareMedia(int platform, String title, String targetUrl, String summary, String thumbUrl, Activity activity, ShareListener listener) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, thumbUrl);
        mIUiListener=buildProxyListener(platform,listener);
        mTencent.shareToQQ(activity, params, mIUiListener);
    }

    @Override
    public void shareText(int platform, String text, Activity activity, ShareListener listener) {
        //qq 好友不支持分享纯文字
        activity.finish();
        if (listener != null) {
            listener.shareFailure(platform, new Exception(getString(R.string.qq_not_support_share_text)));
        }
    }




    @Override
    protected void doShareImage(int platform, String localPath, Activity activity, ShareListener listener) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, localPath);
        mIUiListener=buildProxyListener(platform,listener);
        mTencent.shareToQQ(activity, params, mIUiListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
    }
}
