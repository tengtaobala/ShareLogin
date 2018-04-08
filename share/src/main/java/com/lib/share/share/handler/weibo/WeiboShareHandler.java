package com.lib.share.share.handler.weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;

import com.lib.share.ShareUtil;
import com.lib.share.share.ImageDecoder;
import com.lib.share.share.ShareImage;
import com.lib.share.share.ShareListener;
import com.lib.share.share.handler.IShareHandler;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.WbUtils;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tengtao on 2018/4/8.
 */

public class WeiboShareHandler implements IShareHandler, WbShareCallback {
    private WbShareHandler mShareHandler;

    private static final int TARGET_SIZE = 1024;

    private static final int TARGET_LENGTH = 2097152;

    private int mPlatform;
    public WeiboShareHandler(Context context) {
        mShareHandler = new WbShareHandler((Activity) context);
        mShareHandler.registerApp();
        mShareHandler.setProgressColor(0xff33b5e5);
    }

    @Override
    public void shareText(int platform, String text, Activity activity, ShareListener listener) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        WeiboMultiMessage message = new WeiboMultiMessage();
        message.textObject = textObject;

        sendRequest(platform, message);
    }

    @Override
    public void shareMedia(int platform, String title, String targetUrl, String summary, ShareImage shareImage, Activity activity, ShareListener listener) {
        String content = String.format("%s %s", title, targetUrl);
        shareTextOrImage(platform,shareImage, content, activity, listener);
    }

    @Override
    public void shareImage(int platform, ShareImage shareImage, Activity activity, ShareListener listener) {
        shareTextOrImage(platform,shareImage, null, activity, listener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mShareHandler != null) {
            mShareHandler.doResultIntent(data, this);
        }
    }

    @Override
    public boolean isInstall(Context context) {
        return  WbUtils.isWeiboInstall(context);
    }

    @Override
    public void recycle() {
    }

    private void shareTextOrImage(final int platform, final ShareImage shareImageObject, final String text,
                                  final Activity activity, final ShareListener listener) {

        Observable.fromEmitter(new Action1<Emitter<Pair<String, byte[]>>>() {
            @Override
            public void call(Emitter<Pair<String, byte[]>> emitter) {
                try {
                    String path = ImageDecoder.decode(activity, shareImageObject);
                    emitter.onNext(Pair.create(path,
                            ImageDecoder.compress2Byte(path, TARGET_SIZE, TARGET_LENGTH)));
                    emitter.onCompleted();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.DROP)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (listener!=null) {
                            listener.shareRequest(platform);
                        }
                    }
                })
                .subscribe(new Action1<Pair<String, byte[]>>() {
                    @Override
                    public void call(Pair<String, byte[]> pair) {
                        ImageObject imageObject = new ImageObject();
                        imageObject.imageData = pair.second;
                        imageObject.imagePath = pair.first;

                        WeiboMultiMessage message = new WeiboMultiMessage();
                        message.imageObject = imageObject;
                        if (!TextUtils.isEmpty(text)) {
                            TextObject textObject = new TextObject();
                            textObject.text = text;

                            message.textObject = textObject;
                        }

                        sendRequest(platform, message);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        activity.finish();
                        if (listener!=null) {
                            listener.shareFailure(platform, new Exception(throwable));
                        }
                    }
                });
    }


    private void sendRequest(int platform, WeiboMultiMessage message) {
        mPlatform=platform;
        mShareHandler.shareMessage(message,false);
    }

    @Override
    public void onWbShareSuccess() {
        if (ShareUtil.mShareListener!=null){
            ShareUtil.mShareListener.shareSuccess(mPlatform);
        }
    }

    @Override
    public void onWbShareCancel() {
        if (ShareUtil.mShareListener!=null){
            ShareUtil.mShareListener.shareCancel(mPlatform);
        }
    }

    @Override
    public void onWbShareFail() {
        if (ShareUtil.mShareListener!=null){
            ShareUtil.mShareListener.shareFailure(mPlatform,new Exception("share failure"));
        }
    }
}
