package com.lib.share.share;

/**
 * Created by tengtao on 2018/4/8.
 */

public  interface ShareListener  {

     void shareSuccess(int platform);

     void shareFailure(int platform,Exception e);

     void shareCancel(int platform);

     void shareRequest(int platform);


}
