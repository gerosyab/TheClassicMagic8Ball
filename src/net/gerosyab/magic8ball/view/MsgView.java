package net.gerosyab.magic8ball.view;

import java.util.ArrayList;

import net.gerosyab.magic8bal.data.StaticData;
import net.gerosyab.magic8ball.activity.MainActivity;
import net.gerosyab.magic8ball.util.MyLog;
import net.gerosyab.magic8ball.util.MyRandom;
import net.gerosyab.magic8ball.util.Shaker;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MsgView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Context context;
	
	private Shaker shaker;
    
	boolean isSurfaceChanged = false;
	
	int width;
	int height;
	
	// center dimension value for canvas
	float cx;
	float cy;
	
	//msg bitmap cx, cy
	float bcx;
	float bcy;
	
	//origin of msg bitmap
	float x;
	float y;
	float xCon;
	float yCon;
	
	// android coordinate system is like this
	// left top is (0, 0), right bottom is (x, y)
	// so first quadrant is placed in right-downside, not right-upside.
	// second is left-downside, third is left-upside and fourth is right-upside
	// roll as clockwise
	// in the temporary coordinate system (cx, cy is 0, 0) the coordinate must be transferred as below
	// x : bcx - cx
	// y : bcy - cy
	float bcxCon;
	float bcyCon;
	
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
	
	Paint characterPaint;
	Paint msgPaint;
	
	// for debugging
	Paint debugPaint;
	Paint debugTextPaint;
	Paint debugCenterTracePaint;
	Paint debugCirclePaint;
	ArrayList<Point> points;
	float cxcyBoundaryRadius;
	float cxcyBoundaryRadiusSquare;
	
	Options opts;
	Bitmap bitmap, resized;
	
	float nBitmapHalfWidth, nBitmapHalfHeight;
	float nBitmapStartX, nBitmapStartY;
	float nTempCenterX, nTempCenterY;
	
	float nMsgTriangleWidth, nMsgTriangleHeight;
	
	boolean rIncrease = false;
	boolean isBoundaryOut = false;
	
    private float mSensorX;
    private float mSensorY;
    private float mSensorZ;
    
    float angle;
    float radian;

    float scale = 0;
    boolean scaleIncrease = true;
    
    private float rBoundary = 5;
    private float degree = 0;
    
    private boolean appearFlag = false;
    
    // I can't remeber how did I get this value...
    int [] alphaValueTable = {
			0, 1, 3, 6, 10, 16, 24, 34, 46, 60,
			76, 94, 114, 127, 141, 161, 179, 195, 209, 221,
			231, 239, 245, 249, 252, 254, 255
			};
    
    int alpha = 0;
    int alphaIndex = 0;
    
    // x_n : 0.809+ n * 0.0074
    // y_n : log_2-(x_n-1)? (n = 26, y = 1)
    // n : 0 ~ 26
    float [] scaleValueTable = {
    		0.855192408f, 0.861081942f, 0.866947531f, 0.872789368f,	0.878607646f,
    		0.884402553f, 0.890174277f,	0.895923002f, 0.901648911f,	0.907352184f,
    		0.913033f, 	  0.918691534f, 0.924327962f, 0.929942454f,	0.935535181f,
    		0.941106311f, 0.94665601f, 	0.952184443f, 0.957691771f,	0.963178156f,
    		0.968643756f, 0.974088728f, 0.979513226f, 0.984917405f,	0.990301417f,
    		0.99566541f, 1f
    };
    int scaleIndex = 0;
    
    // for turn on/off debugging mode when the view is showing
    int touchCount = 0;
    int touchCountMax = 10;
    int touchArea = 250;
    boolean touchAreaCheck = false;
    
	private MsgThread thread;
	
	public int msgIdx;
	
	public MsgView(Context context) {
		super(context);
		if(!isInEditMode()) init(context);
	}
	
	public MsgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if(!isInEditMode()) init(context);
	}

	public MsgView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(!isInEditMode()) init(context);
	}
	
	private void init(Context context){
		MyLog.d("MsgView", "init");
		
		this.context = context;
		getHolder().addCallback(this);
		
		characterPaint = new Paint();
		reflectRectF = new RectF();
		msgPaint = new Paint();
		
		debugPaint = new Paint();
		debugTextPaint = new Paint();
		debugCenterTracePaint = new Paint();
		debugCirclePaint = new Paint();
		points = new ArrayList<Point>();
		
	}

	// to implement hidden debugging mode
	// turning on/off by touching right-top side of the view more than 10 times 
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		
		switch(action){
		case MotionEvent.ACTION_DOWN:
			if(width - touchArea < event.getX() && event.getX() < width && 0 < event.getY() && event.getY() < touchArea){
				touchAreaCheck = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(touchAreaCheck){
				touchAreaCheck = false;
				touchCount++;
				if(touchCount > touchCountMax){
					touchCount = 0;
					StaticData.setViewDebuggingMode(!StaticData.VIEW_DEBUG);
				}
			}
			
			break;
		}
		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(isInEditMode()) return;
		
		if (StaticData.VIEW_DEBUG) {
			// hold 300 numbers of recent bitmddap center point
			// works as queue - FIFO
			points.add(new Point((int)bcx, (int)bcy));
			if (points.size() > 300) {
				points.remove(0);
			}
		}
		
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
		
		
		update();
		
		canvas.rotate(degree, bcx, bcy);
		
		if(StaticData.VIEW_DEBUG) canvas.drawCircle(bcx,  bcy, nBitmapHalfWidth, debugCirclePaint);

		if(appearFlag){
			msgPaint.setAlpha(alphaValueTable[alphaIndex]);
			alphaIndex++;
        	
        	canvas.scale(scaleValueTable[scaleIndex], scaleValueTable[scaleIndex], bcx, bcy);
        	scaleIndex++;
        	
        	if(alphaIndex >= alphaValueTable.length){
        		appearFlag = false;
        	}
		}
		
		
        canvas.drawBitmap(resized, x, y, msgPaint);
        
        if(StaticData.VIEW_DEBUG) {
        	canvas.drawCircle(bcx,  bcy,  10, debugCenterTracePaint);
			canvas.drawCircle(cx, cy, cxcyBoundaryRadius, debugPaint);
		}
        
        canvas.rotate(-degree, bcx, bcy);
        
        if(StaticData.VIEW_DEBUG){
        	canvas.drawRect(width - touchArea, 0, width, touchArea, debugPaint);
        	
            canvas.drawText("mSensorX : " + mSensorX, 50, 100, debugTextPaint);
            canvas.drawText("mSensorY : " + mSensorY, 50, 150, debugTextPaint);
            canvas.drawText("mSensorZ : " + mSensorY, 50, 200, debugTextPaint);
            canvas.drawText("x : " + x, 50, 250, debugTextPaint);
            canvas.drawText(", y : " + y, 400, 250, debugTextPaint);
            canvas.drawText("bcx : " + bcx, 50, 300, debugTextPaint);
            canvas.drawText(", bcy : " + bcy, 400, 300, debugTextPaint);
            canvas.drawText("bcxCon : " + bcxCon, 50, 350, debugTextPaint);
            canvas.drawText(", bcyCon : " + bcyCon, 400, 350, debugTextPaint);
            canvas.drawText("isBoundaryOut : " + isBoundaryOut + ", cx : " + cx + ", cy : " + cy, 50, 400, debugTextPaint);
        }
		
        if(StaticData.VIEW_DEBUG){
        	// draw the trace of msgView
        	
        	int length = points.size();
        	
            int blue = 0;
            boolean increase = true;
            if(length > 1){
	            int i = 1;
	            Point point = points.get(0);
	            int prevX = point.x;
	            int prevY = point.y;
	            debugCenterTracePaint.setColor(Color.rgb(255, 0, 0));
	            do{
	            	point = points.get(i);
	            	canvas.drawLine(prevX, prevY, point.x, point.y, debugCenterTracePaint);
		            prevX = point.x;
		            prevY = point.y;
		            i++;
		            if(increase){
		            	blue++;
		            	if(blue > 255){
		            		blue--;
		            		increase = false;
		            	}
		            }
		            else{
		            	blue--;
		            	if(blue < 0){
		            		blue++;
		            		increase = true;
		            	}
		            }
		            debugCenterTracePaint.setColor(Color.rgb(255, blue, 0));
	            }while(i < length);
            }
        }
	}
	
	/*
	 * calculate the coordination value using accelerometer
	 * if coordination is out of circular boundary, just let it in
	 * 
	 * it is kind of simulation, very rough thing
	 */
    public void update() {
    	mSensorX = shaker.getSx();
    	mSensorY = shaker.getSy();
    	mSensorZ = shaker.getSz();
    	
    	bcxCon += mSensorX;
    	bcyCon -= mSensorY;
    	
        if(Math.pow(bcxCon, 2) + Math.pow(bcyCon, 2) >= cxcyBoundaryRadiusSquare){
            isBoundaryOut = true;


            if(bcxCon != 0 && bcyCon != 0){
                radian = (float) Math.atan2(bcyCon, bcxCon);
            }

            bcxCon = (float) (Math.cos(radian) * cxcyBoundaryRadius);
            bcyCon = (float) (Math.sin(radian) * cxcyBoundaryRadius);
        }
        else{
            isBoundaryOut = false;
        }
        
        //center x, y coordinate value of msg bitmap
        bcx = bcxCon + cx;
        bcy = bcyCon + cy;
    	
        x = bcx - nBitmapHalfWidth;
        y = bcy - nBitmapHalfHeight;
    }

	private void setNewMsg(int index){
		MyLog.d("MsgView", "setNewMsg");
		
		bitmap = BitmapFactory.decodeResource(getResources(), StaticData.msgID[index], opts);
		resized = Bitmap.createScaledBitmap(bitmap, (int) nMsgTriangleWidth, (int) nMsgTriangleHeight, true);
		
        appearFlag = true;
        alphaIndex = 0;
        scaleIndex = 0;
        
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		MyLog.d("MsgViewHolder", "surfaceCreated, isSurfaceChanged : " + isSurfaceChanged + ", holder id : " + holder.toString());
		
		thread = new MsgThread(holder);
		thread.setLoop(true);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		MyLog.d("MsgViewHolder", "surfaceChanged called, isSurfaceChanged : " + isSurfaceChanged + ", holder id : " + holder.toString());
		
		shaker = MainActivity.getShagerInstance();
		
		isSurfaceChanged = true;
		
		this.width = width;
		this.height = height;
		
		outerRadius = (float) (width * 0.75);
		reflectRadius = (float) (outerRadius * 0.95);
		innerOuterRadius = (float) (outerRadius * 0.5);
		innerInnerRadius = (float) (outerRadius * 0.45);
		strokeWidth = (float) (innerOuterRadius * 0.1);
		cxcyBoundaryRadius = (float) (innerInnerRadius * 0.2);
		cxcyBoundaryRadiusSquare = cxcyBoundaryRadius * cxcyBoundaryRadius;
		
		nMsgTriangleWidth = nMsgTriangleHeight = innerOuterRadius * 1.2f;
		
		nBitmapHalfWidth = nMsgTriangleWidth / 2;
		nBitmapHalfHeight = nMsgTriangleHeight / 2;
		
		cx = width / 2;
		cy = height / 2;
		
		x = cx - nBitmapHalfWidth;
		y = cy - nBitmapHalfHeight;
		
		bcx = cx;
		bcy = cy;
		
		bcxCon = 0;
		bcyCon = 0;
		
		debugPaint.setColor(Color.YELLOW);
		debugPaint.setAntiAlias(true);
		debugPaint.setStrokeWidth(2);
		debugPaint.setStyle(Paint.Style.STROKE);
		
		debugTextPaint.setColor(Color.WHITE);
		debugTextPaint.setAntiAlias(true);
		debugTextPaint.setStrokeWidth(2);
		debugTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		debugTextPaint.setTextSize(35);
		
		debugCenterTracePaint.setColor(Color.RED);
		debugCenterTracePaint.setAntiAlias(true);
		debugCenterTracePaint.setStrokeWidth(5);
		debugCenterTracePaint.setStyle(Paint.Style.STROKE);
		debugCenterTracePaint.setTextSize(35);
		
		debugCirclePaint.setColor(Color.GREEN);
		debugCirclePaint.setAntiAlias(true);
		debugCirclePaint.setStrokeWidth(7);
		debugCirclePaint.setStyle(Paint.Style.STROKE);
		debugCirclePaint.setTextSize(35);
		
		characterPaint.setColor(Color.BLACK);
		characterPaint.setAntiAlias(true);
		characterPaint.setStrokeWidth(strokeWidth);
		characterPaint.setStyle(Paint.Style.STROKE);
		
		msgPaint.setAntiAlias(true);
		msgPaint.setFilterBitmap(true);
		msgPaint.setDither(true);
		
		notifyMsgChanged();
		
		isSurfaceChanged = true;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		MyLog.d("MsgViewHolder", "surfaceDestroyed, isSurfaceChanged : " + isSurfaceChanged + ", holder id : " + holder.toString());

		boolean retry = true;
		thread.setLoop(false);

		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				e.getStackTrace();
			}
		}
	}
	
	public void notifyMsgChanged() {
		MyLog.d("MsgView", "notifyMsgChanged");
//		int index = MyRandom.getNum();
		setNewMsg(msgIdx);
	}
	
	public void setMsgIdx(int index){
		MyLog.d("MsgView", "setMsgIdx, msg index : " + index);
		this.msgIdx = index;
	}
	
	public class MsgThread extends Thread {

		SurfaceHolder holder;
		Canvas canvas;
		boolean running = false;
		
		public MsgThread(SurfaceHolder holder) {
			this.holder = holder;
			running = true;
		}
		
		@Override
		public void run() {
			super.run();
			
			while(running){
				try {
					canvas = holder.lockCanvas();
					synchronized(holder){
						postInvalidate();
					}
					
				} catch(Exception e){
					e.printStackTrace();
					MyLog.d("MsgView", e.getMessage());
					
				} finally {
					if(canvas != null){
						holder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		
		public void setLoop(boolean isRunning){
			this.running = isRunning;
		}
		
		public boolean isRunning(){
			return running;
		}
	}
}
