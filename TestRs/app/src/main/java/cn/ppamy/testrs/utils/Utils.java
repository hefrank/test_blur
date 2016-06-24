package cn.ppamy.testrs.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
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
            imagePath = Environment.getExternalStorageDirectory()+ File.separator+"Screenshot.png" ;
        }

        Bitmap mScreenBitmap = null;
        WindowManager mWindowManager;
        DisplayMetrics mDisplayMetrics;
        Display mDisplay;

        mWindowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);

        float[] dims = {mDisplayMetrics.widthPixels , mDisplayMetrics.heightPixels };

        try {
            Class sfs = Class.forName("android.view.SurfaceControl");

            try {
                Method[] kk = sfs.getMethods();
                for (Method jj:kk){
                    Log.w("kkk-kkk","-----------------"+jj.toString());
                }
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

//        mScreenBitmap = Surface.screenshot((int) dims[0], ( int) dims[1]);

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
}
