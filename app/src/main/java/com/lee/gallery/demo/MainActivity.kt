package com.lee.gallery.demo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wjx.gallery.GalleryFinal
import com.wjx.gallery.MimeType
import com.wjx.gallery.entity.CaptureStrategy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_test.setOnClickListener {
            GalleryFinal.with(this)
                .choose(MimeType.ofAll())
                .capture(true)
                .maxSelectable(1)
                .captureStrategy(
                    CaptureStrategy(true, "com.lee.gallery.demo.fileprovider", "test")
                )
                .imageCrop(true)
                .forResult(1001)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == 1001) {
//            Log.e("crop", GalleryFinal.obtainCropResult(data).toString())
//            iv_test.setImageURI(GalleryFinal.obtainCropResult(data))
        }
    }
}
