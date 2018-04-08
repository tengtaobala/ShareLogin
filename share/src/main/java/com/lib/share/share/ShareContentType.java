package com.lib.share.share;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by tengtao on 2018/4/8.
 */

public class ShareContentType {

    @IntDef({TYPE_IMAGE, TYPE_TEXT, TYPE_MEDIA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platform {
    }

    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_TEXT = 2;
    public final static int TYPE_MEDIA = 3;
}
