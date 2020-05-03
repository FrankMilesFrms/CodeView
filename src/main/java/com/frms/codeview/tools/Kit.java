package com.frms.codeview.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.util.Arrays;

/**
 * 这是一个工具类，另见 {@link com.frms.codeview.CodeView#init(Activity)}
 * @author  ： Frms, 3505826836@qq.com：
 * 创建时间 ：2020/2/9 18:21(ydt)
 */
public class Kit
{
    
    private static final String TAG = "Kit";
    
    /**
     * 获取图片资源
     * @param ctx context
     * @param res 资源名
     * @param x 宽度
     * @param y 高度
     * @return
     */
    public static Bitmap getBitmap(Context ctx, int res, int x, int y)
    {
        Bitmap a = Bitmap.createBitmap(BitmapFactory.decodeResource(ctx.getResources(), res));
        return zoomImg(a, x, y, 0);
    }
    
    /**
     * 获取图片资源
     * @param ctx
     * @param res
     * @param r
     * @return
     */
    public static Bitmap getBitmap(Context ctx, int res, float r)
    {
        
        Bitmap a = Bitmap.createBitmap(BitmapFactory.decodeResource(ctx.getResources(), res));
        return zoomImg(a, 0, 0, r);
    }
    
    /**
     * 获取图片资源
     * @param bm
     * @param newWidth
     * @param newHeight
     * @param r
     * @return
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight, float r) {
        
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        if(r != 0)
        {
            scaleWidth = r;
            scaleHeight = r;
        }
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    
    /**
     * 打印信息
     * @param a
     */
    public static void printout(Object... a)
    {
        Log.e(TAG, Arrays.toString(a));
    }
    
    /**
     * 打印信息
     * @param a
     */
    public static void printout(int[] a) {Log.e(TAG, Arrays.toString(a));}
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
}
