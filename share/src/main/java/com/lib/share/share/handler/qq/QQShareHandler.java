package com.lib.share.share.handler.qq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.lib.share.share.ImageDecoder;
import com.lib.share.share.ShareImage;
import com.lib.share.share.ShareListener;
import com.lib.share.share.handler.Handler;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.List;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tengtao on 2018/4/8.
 */

public abstract class QQShareHandler extends Handler {


    protected Tencent mTencent;
    protected IUiListener mIUiListener;

    public QQShareHandler(Context context, String appId) {
        super(context);
        mTencent = Tencent.createInstance(appId, context.getApplicationContext());

    }

    @Override
    public void shareMedia(final int platform, final String title, final String targetUrl, final String summary, final ShareImage shareImage, final Activity activity, final ShareListener listener) {
        Observable.fromEmitter(new Action1<Emitter<String>>() {
            @Override
            public void call(Emitter<String> emitter) {
                try {
                    emitter.onNext(ImageDecoder.decode(activity, shareImage));
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
                        if (listener != null) {
                            listener.shareRequest(platform);
                        }

                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String imageUrl) {
                        doShareMedia(platform, title, targetUrl, summary, imageUrl, activity,
                                listener);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        activity.finish();
                        if (listener != null) {
                            listener.shareFailure(platform, new Exception(throwable));
                        }
                    }
                });
    }

    protected abstract void doShareMedia(int platform, String title, String targetUrl, String summary, String thumbUrl, Activity activity, ShareListener listener);

    @Override
    public void shareImage(final int platform, final ShareImage shareImage, final Activity activity, final ShareListener listener) {
        Observable.fromEmitter(new Action1<Emitter<String>>() {


            @Override
            public void call(Emitter<String> emitter) {

                try {
                    emitter.onNext(ImageDecoder.decode(activity, shareImage));
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
                        if (listener != null) {
                            listener.shareRequest(platform);
                        }
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String localPath) {

                        doShareImage(platform, localPath, activity, listener);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        activity.finish();
                        if (listener != null) {
                            listener.shareFailure(platform, new Exception(throwable));
                        }
                    }
                });
    }

    /**
     * 开始分享图片
     *
     * @param platform
     * @param localPath
     * @param activity
     * @param listener
     */
    protected abstract void doShareImage(int platform, String localPath, Activity activity, ShareListener listener);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_QQ_SHARE || resultCode == Constants.REQUEST_QZONE_SHARE || resultCode == Constants.REQUEST_OLD_SHARE) {
                Tencent.handleResultData(data, mIUiListener);
            }
        }
    }

    @Override
    public boolean isInstall(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            return false;
        }

        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (TextUtils.equals(info.packageName.toLowerCase(), "com.tencent.mobileqq")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void recycle() {
        if (mTencent != null) {
            mTencent.releaseResource();
            mTencent = null;
        }
    }


    protected IUiListener buildProxyListener(int platform, ShareListener listener) {
        if (listener != null) {
            listener.shareRequest(platform);
        }

        return new QQUiListener(platform, listener);
    }


    protected class QQUiListener implements IUiListener {

        private int platform;
        private ShareListener listener;

        public QQUiListener(int platform, ShareListener listener) {
            this.platform = platform;
            this.listener = listener;
        }

        @Override
        public void onComplete(Object o) {
            if (listener != null) {
                listener.shareSuccess(platform);
            }
        }

        @Override
        public void onError(UiError uiError) {
            if (listener != null) {
                listener.shareFailure(platform, new Exception(uiError.errorMessage + "\n" + uiError.errorDetail));
            }

        }

        @Override
        public void onCancel() {
            if (listener != null) {
                listener.shareCancel(platform);
            }
        }
    }

}
