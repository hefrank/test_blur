package cn.ppamy.testrs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private ImageView mIvBG;
    private TextView mTvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvBG = (ImageView) findViewById(R.id.iv_ct);
        mTvDesc = (TextView) findViewById(R.id.tv_desc);

        if (mIvBG != null && mTvDesc != null) {
            ViewTreeObserver observer =
                    mTvDesc.getViewTreeObserver();
            if (observer != null) {
                observer.addOnPreDrawListener(mPreDrawListener);
            }
        }
    }

    private ViewTreeObserver.OnPreDrawListener mPreDrawListener =
            new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    ViewTreeObserver observer = mTvDesc.getViewTreeObserver();
                    if (observer != null) {
                        observer.removeOnPreDrawListener(this);
                    }
                    Drawable drawable = mIvBG.getDrawable();
                    if (drawable != null &&
                                drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (bitmap != null) {
                            blur(bitmap, mTvDesc, 15);
                        }
                    }
                    return true;
                }
            };

    private void blur(Bitmap bkg, View view, float radius) {
        Log.d(TAG,"DO BLUR radius is "+radius);
        Bitmap overlay = Bitmap.createBitmap( view.getMeasuredWidth(), view.getMeasuredHeight() + 80,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.drawBitmap(bkg, -view.getLeft(), -view.getTop() - 40, null);
        RenderScript rs = RenderScript.create(this);
        Allocation overlayAlloc = Allocation.createFromBitmap( rs, overlay);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create( rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(radius);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(overlay);
        view.setBackground(new BitmapDrawable(getResources(), overlay));
        rs.destroy();
    }
}
