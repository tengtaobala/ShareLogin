package com.share.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.lib.share.ShareConfig
import com.lib.share.ShareUtil
import com.lib.share.share.ShareListener
import com.lib.share.share.SharePlatform
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val config=ShareConfig()
        config.qqId=Keys.qqId
        config.wxId=Keys.wxId
        config.wxsecret=Keys.wxsecret
        config.weiboId=Keys.weiboId
        ShareUtil.init(this,config)
        val imgUrl="http://c.hiphotos.baidu.com/image/pic/item/aa18972bd40735fa04058f9c92510fb30f24081a.jpg"
        val targetUlr="https://www.baidu.com/"
        btn_QQ_share.setOnClickListener({
            ShareUtil.shareText(
                    this,
                    SharePlatform.QQ,
                    "测试分享",
                    listener
            )
        })


        btn_QQ_image_share.setOnClickListener({
            val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
            ShareUtil.shareImage(this,SharePlatform.QQ,bitmap,listener)
        })


        btn_QQ_web_share.setOnClickListener({
            ShareUtil.shareMedia(this,SharePlatform.QQ,"分享title","分享简要",targetUlr,imgUrl,listener)
        })








        btn_wx_share.setOnClickListener({
            ShareUtil.shareText(
                    this,
                    SharePlatform.WX_TIMELINE,
                    "测试分享",
                    listener
            )

        })

        btn_wx_image_share.setOnClickListener({
            ShareUtil.shareImage(this,SharePlatform.WX_TIMELINE,imgUrl,listener)

        })

        btn_wx_web_share.setOnClickListener({
            ShareUtil.shareMedia(this,SharePlatform.WX_TIMELINE,"分享title","分享简要",targetUlr,imgUrl,listener)
        })

        btn_weibo_share.setOnClickListener({
            ShareUtil.shareText(
                    this,
                    SharePlatform.WEIBO,
                    "测试分享",
                    listener
            )
        })
        btn_weibo_image_share.setOnClickListener({
            ShareUtil.shareImage(this,SharePlatform.WEIBO,imgUrl,listener)

        })

        btn_weibo_web_share.setOnClickListener({
            ShareUtil.shareMedia(this,SharePlatform.WEIBO,"分享title","分享简要",targetUlr,imgUrl,listener)
        })
    }


    val listener =object : ShareListener {
        override fun shareRequest(platform: Int) {
            Toast.makeText(this@MainActivity, "${SharePlatform.getPlatformValue(platform)}:开始分享", Toast.LENGTH_SHORT).show()
        }

        override fun shareSuccess(platform: Int) {
            Toast.makeText(this@MainActivity, "${SharePlatform.getPlatformValue(platform)}:分享成功", Toast.LENGTH_SHORT).show()
        }

        override fun shareFailure(platform: Int, e: Exception?) {
            Toast.makeText(this@MainActivity, "${SharePlatform.getPlatformValue(platform)}:分享失败", Toast.LENGTH_SHORT).show()

        }

        override fun shareCancel(platform: Int) {
            Toast.makeText(this@MainActivity, "${SharePlatform.getPlatformValue(platform)}:取消分享", Toast.LENGTH_SHORT).show()

        }

    }
}
