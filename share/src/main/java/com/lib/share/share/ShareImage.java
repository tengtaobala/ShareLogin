package com.lib.share.share;

import android.graphics.Bitmap;

/**
 * Created by tengtao on 2018/4/8.
 */

public class ShareImage {
    private Bitmap mBitmap;
    private String mPathOrUrl;

    public ShareImage(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public ShareImage(String pathOrUrl) {
        mPathOrUrl = pathOrUrl;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public String getPathOrUrl() {
        return mPathOrUrl;
    }

    public void setPathOrUrl(String pathOrUrl) {
        mPathOrUrl = pathOrUrl;
    }
}
