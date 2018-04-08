package com.lib.share.share.handler;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by tengtao on 2018/4/8.
 */

public abstract class Handler implements IShareHandler {
    protected Context mContext;
    protected Activity mActivity;

    public Handler(Context context) {
        mContext = context;
        mActivity=(Activity)context;
    }


    protected String getString(@StringRes int resId){
        return mContext.getString(resId);
    }


}
