package net.gerosyab.magic8ball.view;

import net.gerosyab.magic8ball.util.MyLog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class FrontView extends SurfaceView implements SurfaceHolder.Callback {
	
	boolean isSurfaceChanged = false;
	boolean isOpenning;
	boolean isClosing;
	boolean isTouched;
	
	// for reflection rotation
	private float rBoundary = 15;
    private float degree = 0;
    private boolean rIncrease = true;
	
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
	
	// inner ball radius - white ball
	float innerRadius;
	
	// '8' letter circle radius, 1 for upper, 2 for downer ball
	float charcterRadius1;
	float charcterRadius2;
	
	// letter stroke width
	float strokeWidth;
	
	Paint blackPaint;
	Paint reflectPaint;
	Paint whitePaint;
	Paint characterPaint;
	
	int blinkSpeed = 5;
	int blinkTerm = 5000;	// eye blinking term (ms), using with +/- random value
	long lastBlinkTime = 0;
	
	public FrontView(Context context) {
		super(context);
		getHolder().addCallback(this);
		init();
	}
	
	public FrontView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		init();
	}

	public FrontView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
		init();

	}
	
	private void init(){
		blackPaint = new Paint();
		reflectPaint = new Paint();
		whitePaint = new Paint();
		characterPaint = new Paint();
		reflectRectF = new RectF();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// draw outer ball
		canvas.drawCircle(cx, cy, outerRadius, blackPaint);
		
		// draw inner ball
		canvas.drawCircle(cx, cy, innerRadius, whitePaint);
		
		// draw character '8'
		canvas.drawCircle(cx, cy - charcterRadius1, charcterRadius1, characterPaint);
		canvas.drawCircle(cx, cy + charcterRadius2, charcterRadius2, characterPaint);
		
		// draw reflection
		if (rIncrease) {
			degree = degree + 0.1f;
			if (degree >= rBoundary) {
				rIncrease = false;
			}
		} else {
			degree = degree - 0.1f;
			if (degree <= -rBoundary) {
				rIncrease = true;
			}
		}
		canvas.drawArc(reflectRectF, 135 + degree, 180, true, reflectPaint);
		invalidate();
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		MyLog.d("FrontView", "surfaceCreated");
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		isSurfaceChanged = true;
		
		surfaceWidth = width;
		surfaceHeight = height;
		
		cx = surfaceWidth / 2;
		cy = surfaceHeight / 2 - surfaceHeight * 0.1f;
		
		outerRadius = (float) (surfaceWidth * 0.275);
		reflectRadius = (float) (outerRadius * 0.95);
		innerRadius = (float) (outerRadius * 0.425);
		charcterRadius1 = (float) (innerRadius * 0.225);
		charcterRadius2 = (float) (innerRadius * 0.25);
		strokeWidth = (float) (innerRadius * 0.1);
		
		isSurfaceChanged = true;

		blackPaint.setColor(Color.BLACK);
		blackPaint.setAntiAlias(true);
		blackPaint.setStyle(Paint.Style.FILL);
		
		reflectPaint.setColor(Color.argb(45, 255, 255, 255));
		reflectPaint.setAntiAlias(true);
		reflectPaint.setStyle(Paint.Style.FILL);
		reflectRectF.set(cx - reflectRadius, cy - reflectRadius, cx + reflectRadius, cy + reflectRadius);
		
		whitePaint.setColor(Color.WHITE);
		whitePaint.setAntiAlias(true);
		
		characterPaint.setColor(Color.BLACK);
		characterPaint.setAntiAlias(true);
		characterPaint.setStrokeWidth(strokeWidth);
		characterPaint.setStyle(Paint.Style.STROKE);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		MyLog.d("FrontView", "surfaceDestroyed");
	}
	
	public float getCx(){
		return cx;
	}
	
	public float getCy(){
		return cy;
	}
	
	public float getRadius(){
		return outerRadius;
	}
}
