package com.lib.share.share.handler.wx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.lib.share.ShareUtil;
import com.lib.share.share.ImageDecoder;
import com.lib.share.share.ShareImage;
import com.lib.share.share.ShareListener;
import com.lib.share.share.SharePlatform;
import com.lib.share.share.handler.IShareHandler;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by tengtao on 2018/4/8.
 */

public class WXHandler implements IShareHandler {
    /**
     * 微信分享限制thumb image必须小于32Kb，否则点击分享会没有反应
     */
    private IWXAPI mIWXAPI;
    private static final int THUMB_SIZE = 32 * 1024 * 8;

    private static final int TARGET_SIZE = 200;
    private int mPlatform;



    public WXHandler(Context context, String appId) {
        mIWXAPI = WXAPIFactory.createWXAPI(context, appId, true);
        mIWXAPI.registerApp(appId);
    }

    @Override
    public void shareText(int platform, String text, Activity activity, ShareListener listener) {
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage message = new WXMediaMessage();
        message.mediaObject = textObject;
        message.description = text;

        sendMessage(platform, message, buildTransaction("text"));
    }

    @Override
    public void shareMedia(final int platform, final String title, final String targetUrl, final String summary, final ShareImage shareImage, final Activity activity, final ShareListener listener) {

        Observable.fromEmitter(new Action1<Emitter<byte[]>>() {

            @Override
            public void call(Emitter<byte[]> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(activity, shareImage);
                    emitter.onNext(ImageDecoder.compress2Byte(imagePath, TARGET_SIZE, THUMB_SIZE));
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
                .subscribe(new Action1<byte[]>() {
                    @Override
                    public void call(byte[] bytes) {
                        WXWebpageObject webpageObject = new WXWebpageObject();
                        webpageObject.webpageUrl = targetUrl;

                        WXMediaMessage message = new WXMediaMessage(webpageObject);
                        message.title = title;
                        message.description = summary;
                        message.thumbData = bytes;

                        sendMessage(platform, message, buildTransaction("webPage"));
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

    @Override
    public void shareImage(final int platform, final ShareImage shareImage, final Activity activity, final ShareListener listener) {
        Observable.fromEmitter(new Action1<Emitter<Pair<Bitmap, byte[]>>>() {
            @Override
            public void call(Emitter<Pair<Bitmap, byte[]>> emitter) {
                try {
                    String imagePath = ImageDecoder.decode(activity, shareImage);
                    emitter.onNext(Pair.create(BitmapFactory.decodeFile(imagePath),
                            ImageDecoder.compress2Byte(imagePath, TARGET_SIZE, THUMB_SIZE)));
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, Emitter.BackpressureMode.BUFFER)
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
                .subscribe(new Action1<Pair<Bitmap, byte[]>>() {
                    @Override
                    public void call(Pair<Bitmap, byte[]> pair) {
                        WXImageObject imageObject = new WXImageObject(pair.first);

                        WXMediaMessage message = new WXMediaMessage();
                        message.mediaObject = imageObject;
                        message.thumbData = pair.second;

                        sendMessage(platform, message, buildTransaction("image"));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        mIWXAPI.handleIntent(data, new IWXAPIEventHandler() {
            @Override
            public void onReq(BaseReq baseReq) {

            }

            @Override
            public void onResp(BaseResp baseResp) {
                switch (baseResp.errCode) {
                    case BaseResp.ErrCode.ERR_OK:
                        ShareUtil.mShareListener.shareSuccess(mPlatform);
                        break;
                    case BaseResp.ErrCode.ERR_USER_CANCEL:
                        ShareUtil.mShareListener.shareCancel(mPlatform);
                        break;
                    default:
                        ShareUtil.mShareListener.shareFailure(mPlatform, new Exception(baseResp.errStr));
                }
            }
        });
    }

    @Override
    public boolean isInstall(Context context) {
        return mIWXAPI.isWXAppInstalled();
    }

    @Override
    public void recycle() {
        if (mIWXAPI != null) {
            mIWXAPI.detach();
        }
    }


    private void sendMessage(int platform, WXMediaMessage message, String transaction) {
        mPlatform = platform;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = message;
        req.scene = platform == SharePlatform.WX_TIMELINE ? SendMessageToWX.Req.WXSceneTimeline
                : SendMessageToWX.Req.WXSceneSession;
        mIWXAPI.sendReq(req);
    }

    private String buildTransaction(String type) {
        return System.currentTimeMillis() + type;
    }
}
