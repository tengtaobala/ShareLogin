package com.lib.share;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by tengtao on 2018/4/8.
 */

public class ShareActivity extends Activity {

    private static final String TYPE = "share_activity_type";
    private int mType;
    private boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirst=true;
        mType = getIntent().getIntExtra(TYPE, 0);

        if (mType == ShareUtil.TYPE) {
            ShareUtil.action(this);
        } else {
            // handle 微信回调
//            LoginUtil.handleResult(-1, -1, getIntent());
            ShareUtil.onActivityResult(-1,-1,getIntent());
            finish();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst){
            isFirst=false;
        }else{
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mType == ShareUtil.TYPE) {
            ShareUtil.onActivityResult(-1, -1,intent);
        }
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareUtil.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    public static Intent newInstance(Context context, int type) {
        Intent intent = new Intent(context, ShareActivity.class);
        if (context instanceof Application) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(TYPE, type);
        return intent;
    }
}
