package com.lib.share.share.handler.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lib.share.share.ShareListener;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

/**
 * Created by tengtao on 2018/4/8.
 */

public class QZoneShareHandler extends QQShareHandler {
    public QZoneShareHandler(Context context, String appId) {
        super(context, appId);
    }

    @Override
    public void shareText(int platform, String text, Activity activity, ShareListener listener) {
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, text);
        mIUiListener = buildProxyListener(platform, listener);
        mTencent.publishToQzone(mActivity, params, mIUiListener);
    }


    @Override
    protected void doShareMedia(int platform, String title, String targetUrl, String summary, String thumbUrl, Activity activity, ShareListener listener) {
        final Bundle params = new Bundle();
        final ArrayList<String> image = new ArrayList<>();
        image.add(thumbUrl);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, image);
        mIUiListener = buildProxyListener(platform, listener);
        mTencent.shareToQzone(activity, params, mIUiListener);
    }


    @Override
    protected void doShareImage(int platform, String localPath, Activity activity, ShareListener listener) {
        final Bundle params = new Bundle();
        final ArrayList<String> image = new ArrayList<>();
        image.add(localPath);
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, image);
        mIUiListener = buildProxyListener(platform, listener);
        mTencent.publishToQzone(activity, params, mIUiListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
    }
}
