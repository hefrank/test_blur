package cn.ppamy.testrs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created on 6/24/16.
 */
public class Utils {
    /**
     * 截屏的代码，android5.0的代码中截屏的类由 Surface 改成了 SurfaceControl
     *
     * 使用的时候需要system权限，且需要<uses-permission android:name="android.permission.READ_FRAME_BUFFER"/>
     * */
    public static boolean takeScreenShot(Context c, String imagePath){
        if(imagePath.equals("")){
            imagePath = Environment.getExternalStorageDirectory()+ File.separator+"testScreenshot.png" ;
        }
        Bitmap mScreenBitmap = null;
        mScreenBitmap = takeScreenShot(c);
        if (mScreenBitmap == null) {
            return false ;
        }

        try {
            FileOutputStream out = new FileOutputStream(imagePath);
            mScreenBitmap.compress(Bitmap.CompressFormat. PNG, 100, out);

        } catch (Exception e) {


            return false ;
        }

        return true ;
    }

    public static Bitmap takeScreenShot(Context c){
        long t0 = System.currentTimeMillis();
        Bitmap mScreenBitmap = null;
        WindowManager mWindowManager;
        DisplayMetrics mDisplayMetrics;
        Display mDisplay;

        mWindowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);

        float[] dims = {mDisplayMetrics.widthPixels , mDisplayMetrics.heightPixels };

        long t1 = System.currentTimeMillis();
        try {
            Class sfs = Class.forName("android.view.SurfaceControl");
            try {
                Method shot = sfs.getMethod("screenshot",int.class,int.class);
                try {
                    mScreenBitmap = (Bitmap) shot.invoke(null,(int) dims[0], ( int) dims[1]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        Log.w("takeScreenShot","takeScreenShot cost time "+(t2-t0)+";reflect cost time is "+(t2-t1));
        return mScreenBitmap;
    }

    /**
     * 获取bitmap的blur
     * */
    public static Bitmap getBlurBitmap(Context c,Bitmap bkg, float radius) {
        long t0 = System.currentTimeMillis();
        Bitmap overlay = Bitmap.createBitmap( bkg.getWidth(), bkg.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bkg, 0, 0 , null);
        RenderScript rs = RenderScript.create(c);
        Allocation overlayAlloc = Allocation.createFromBitmap( rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create( rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        rs.destroy();
        long t1 = System.currentTimeMillis();
        Log.w("takeScreenShot","takeScreenShot getBlurBitmap cost time is "+(t1-t0));
        return overlay;
    }
}
