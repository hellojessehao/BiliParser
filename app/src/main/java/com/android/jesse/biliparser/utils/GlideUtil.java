package com.android.jesse.biliparser.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.android.jesse.biliparser.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * @Description: Glide图片加载工具类
 * @author: zhangshihao
 * @date: 2019/4/20
 */
public class GlideUtil {

    private static final String TAG = GlideUtil.class.getSimpleName();

    private static GlideUtil glideUtil;

    public static GlideUtil getInstance(){
        if(glideUtil == null){
            glideUtil = new GlideUtil();
        }
        return glideUtil;
    }

    public void loadImg(Context context, String url, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.emptyalbum)//图片加载出来前，显示的图片
                .fallback(R.mipmap.emptyalbum) //url为空的时候,显示的图片
                .error(R.mipmap.emptyalbum)//图片加载失败后，显示的图片
                .format(DecodeFormat.PREFER_RGB_565)
                .encodeFormat(Bitmap.CompressFormat.JPEG)
                .encodeQuality(60)
                ;
        if(isContextValide(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }
    //不降低画质
    public void loadOriImg(Context context, String url, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.emptyalbum)//图片加载出来前，显示的图片
                .fallback(R.mipmap.emptyalbum) //url为空的时候,显示的图片
                .error(R.mipmap.emptyalbum)//图片加载失败后，显示的图片
                ;
        if(isContextValide(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }

    public void loadImg(Context context, Uri uri, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.emptyalbum)//图片加载出来前，显示的图片
                .fallback(R.mipmap.emptyalbum) //url为空的时候,显示的图片
                .error(R.mipmap.emptyalbum)//图片加载失败后，显示的图片
                .format(DecodeFormat.PREFER_RGB_565)
                .encodeFormat(Bitmap.CompressFormat.JPEG)
                .encodeQuality(60)
                ;
        if(isContextValide(context)) {
            Glide.with(context).load(uri).apply(options).into(imageView);
        }
    }

    public void loadImg(Context context,int resId,ImageView imageView){
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.emptyalbum)//图片加载出来前，显示的图片
                .fallback(R.mipmap.emptyalbum) //url为空的时候,显示的图片
                .error(R.mipmap.emptyalbum);//图片加载失败后，显示的图片
        if(isContextValide(context)) {
            Glide.with(context).load(resId).apply(options).into(imageView);
        }
    }

    public void loadImg(Context context, File imgFile, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.emptyalbum)//图片加载出来前，显示的图片
                .fallback(R.mipmap.emptyalbum) //url为空的时候,显示的图片
                .error(R.mipmap.emptyalbum);//图片加载失败后，显示的图片
        if(isContextValide(context) && imgFile.exists()) {
            Glide.with(context).load(imgFile).apply(options).into(imageView);
        }
    }

    public void loadTrait(Context context,String url ,ImageView imageView){
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_touxiang)//图片加载出来前，显示的图片
                .fallback(R.mipmap.ic_touxiang) //url为空的时候,显示的图片
                .error(R.mipmap.ic_touxiang);//图片加载失败后，显示的图片
        if(isContextValide(context)) {
            Glide.with(context).load(url).apply(options).into(imageView);
        }
    }

    private boolean isContextValide(Context context){
        if(context == null){
            Log.i(TAG,"context == null");
            return false;
        }
        if(context instanceof Activity){
            if(((Activity)context).isDestroyed()){
                Log.i(TAG,"activity isDestroyed");
                return false;
            }
        }
        return true;
    }

}
