package net.gerosyab.magic8ball.view;

import net.gerosyab.magic8ball.util.MyLog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BackView extends SurfaceView implements SurfaceHolder.Callback {
	
	boolean DEBUG = true;
	
	boolean isSurfaceChanged = false;
	boolean isOpenning;
	boolean isClosing;
	boolean isTouched;
	
	int surfaceWidth;
	int surfaceHeight;
	
	// center dimension value for canvas
	float cx;
	float cy;
	
	// outer ball radius - black ball
	float outerRadius;
	
	// reflect radius - gray ball
	float reflectRadius;
	// reflect rectf
	RectF reflectRectF;
	
	// inner ball radius - msg floating area 
	float innerOuterRadius;
	float innerInnerRadius;
	
	// letter stroke width
	float strokeWidth;
	
	Paint blackPaint;
	Paint reflectPaint;
	Paint innerOuterPaint;
	Paint innerInnerPaint;
	
	Options opts;
    private float reflectionBoundary = 15;
    private float reflectionDegree = 0;
    private boolean reflectionIncrease = true;
    
    DisplayMetrics metrics;
    Display display;
    
	
	public BackView(Context context) {
		super(context);
		if(!isInEditMode()) init(context);
	}
	
	public BackView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) init(context);
	}

	public BackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) init(context);
	}
	
	private void init(Context context){
		getHolder().addCallback(this);
		
		blackPaint = new Paint();
		reflectPaint = new Paint();
		innerOuterPaint = new Paint();
		innerInnerPaint = new Paint();
		reflectRectF = new RectF();
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isInEditMode()) return;
		
		// draw outer ball
		canvas.drawCircle(cx, cy, outerRadius, blackPaint);
		
		// draw reflection
		if (reflectionIncrease) {
			reflectionDegree = reflectionDegree + 0.1f;
			if (reflectionDegree >= reflectionBoundary) {
				reflectionIncrease = false;
			}
		} else {
			reflectionDegree = reflectionDegree - 0.1f;
			if (reflectionDegree <= -reflectionBoundary) {
				reflectionIncrease = true;
			}
		}
		canvas.drawArc(reflectRectF, 135 + reflectionDegree, 180, true, reflectPaint);
		
		// draw innerOuter circle
		canvas.drawCircle(cx, cy, innerOuterRadius, innerOuterPaint);
		
		// draw innerInner circle		
		canvas.drawCircle(cx, cy, innerInnerRadius, innerInnerPaint);
		
		invalidate();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		MyLog.d("BackViewHolder", "surfaceCreated, holder id : " + holder.toString());
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		
		MyLog.d("BackViewHolder", "surfaceChanged called, holder id : " + holder.toString());
		
		isSurfaceChanged = true;
		
		surfaceWidth = width;
		surfaceHeight = height;
		
		cx = surfaceWidth / 2;
		cy = surfaceHeight / 2;

		outerRadius = (float) (surfaceWidth * 0.75);
		reflectRadius = (float) (outerRadius * 0.95);
		innerOuterRadius = (float) (outerRadius * 0.5);
		innerInnerRadius = (float) (outerRadius * 0.45);
		strokeWidth = (float) (innerOuterRadius * 0.1);
		
		isSurfaceChanged = true;

		blackPaint.setColor(Color.BLACK);
		blackPaint.setAntiAlias(true);
		blackPaint.setStyle(Paint.Style.FILL);
		
		reflectPaint.setColor(Color.argb(45, 255, 255, 255));
		reflectPaint.setAntiAlias(true);
		reflectPaint.setStyle(Paint.Style.FILL);
		reflectRectF.set(cx - reflectRadius, cy - reflectRadius, cx + reflectRadius, cy + reflectRadius);
		
		innerOuterPaint.setColor(Color.rgb(20, 20, 20));
		innerOuterPaint.setAntiAlias(true);
		
		innerInnerPaint.setColor(Color.rgb(5, 20, 60));
		innerInnerPaint.setAntiAlias(true);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		MyLog.d("BackViewHolder", "surfaceDestroyed, holder id : " + holder.toString());
	}
     
}
