package com.digitalsan.mustangviews;


import com.digitalsan.android_mustang_views.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class KnobView extends View {

	//Context
	Context mContext;
	
	// Center point of the view,
	// Since perfect square, X==Y
	private int centerPoint;

	//Circle paints
	private Paint circleBackPaint;
	private Paint circleOutlinePaint;
	private Paint circleNotchPaint;
	private Paint circleLinkPaint;
	private Paint circleCenterPaint;
	private Paint circleTextPaint;
	
	//Click Sounds
	SoundPool soundPool;
	int sound;



	//Number of notches on dial
	int notchCount = 10;


	public KnobView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		// TODO Auto-generated constructor stub
		init();
	}

	public KnobView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// TODO Auto-generated constructor stub
		init();
	}

	public KnobView(Context context) {
		super(context);
		mContext = context;
		// TODO Auto-generated constructor stub
		init();
	}

	protected void init(){
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		sound = soundPool.load(mContext, R.raw.click_sound, 1);
		
		circleBackPaint = new Paint();
		circleBackPaint.setStyle(Style.FILL);
		circleBackPaint.setColor(Color.GRAY);
		circleBackPaint.setAntiAlias(true);

		circleOutlinePaint = new Paint();
		circleOutlinePaint.setStyle(Style.STROKE);
		circleOutlinePaint.setColor(Color.BLACK);
		circleOutlinePaint.setAntiAlias(true);
		circleOutlinePaint.setStrokeWidth(20);
		
		circleNotchPaint = new Paint();
		circleNotchPaint.setStyle(Style.STROKE);
		circleNotchPaint.setColor(Color.BLACK);
		circleNotchPaint.setStrokeWidth(30);
		circleNotchPaint.setAntiAlias(true);

		circleLinkPaint = new Paint();
		circleLinkPaint.setStyle(Style.STROKE);
		circleLinkPaint.setColor(Color.YELLOW);
		circleLinkPaint.setStrokeWidth(10);
		circleLinkPaint.setAntiAlias(true);
		
		circleCenterPaint = new Paint();
		circleCenterPaint.setStyle(Style.FILL);
		circleCenterPaint.setColor(Color.BLACK);
		circleCenterPaint.setAntiAlias(true);
		
		circleTextPaint = new Paint();
		circleTextPaint.setStyle(Style.FILL);
		circleTextPaint.setColor(Color.WHITE);
		circleTextPaint.setAntiAlias(true);
		circleTextPaint.setTextAlign(Align.CENTER);
		circleTextPaint.setTextSize(60);
		

	}


	@Override
	protected void onDraw(Canvas canvas) {
		drawKnob(canvas);
		//super.onDraw(canvas);
	}


	protected void drawKnob(Canvas canvas){
		canvas.save();
		if( rotateKnob ){
			canvas.rotate(lastCalcAngle, centerPoint, centerPoint);
		}
		
		
		// Square so both sides same dimensions
		int sideCanvasDims = canvas.getHeight();
		int centerOfCanvas = sideCanvasDims/2;
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, centerOfCanvas, circleBackPaint);
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, centerOfCanvas, circleOutlinePaint);

		int rotateDegress = 360/notchCount;

		int notchSize = centerOfCanvas/3;
		//canvas.save();
		for(int i=0; i<notchCount; i++){
			canvas.drawLine(centerOfCanvas, notchSize, centerOfCanvas, notchSize*2, circleNotchPaint);
			canvas.rotate(rotateDegress,centerOfCanvas,centerOfCanvas);
		}
		canvas.restore();
		
		//Draw center that does not spin
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, notchSize, circleCenterPaint);
		canvas.drawText("VOLUME", centerOfCanvas, centerOfCanvas, circleTextPaint);
		

		//Debugging
		//drawLinkingLine(canvas);
		//drawOuter(canvas);
	}



	//Provide the size we want
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Set what we ultimately would like to be
		int desiredWidth = 300;
		int desiredHeight = 300;

		int widthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		//Get our sizes passed by parent
		int widthMeasureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
		int heightMeasureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);

		//Parent is asking what the dimensions we want to be
		if( widthMeasureSpecMode == MeasureSpec.UNSPECIFIED && heightMeasureSpecMode == MeasureSpec.UNSPECIFIED){
			setMeasuredDimension(desiredWidth, desiredHeight);
			Log.d("SeekButton", "UNSPECIFIED ON BOTH PASSED TO US: Width:" + getMeasuredWidth() + "  Height:" + getMeasuredHeight());
			return;
		} 


		//What both sides should be set to
		int heightSideDims, widthSideDims;

		//Find largest size our parent says we can be
		if( widthMeasureSpecMode == MeasureSpec.UNSPECIFIED ){
			heightSideDims = heightMeasureSpecHeight;
			widthSideDims = heightMeasureSpecHeight;
			Log.d("SeekButton", "UNSPECIFIED ON WIDTH PASSED TO US: Width:" + widthSideDims + "  Height:" + heightSideDims);
		}else if ( heightMeasureSpecMode  == MeasureSpec.UNSPECIFIED ) {
			widthSideDims = widthMeasureSpecWidth;
			heightSideDims = widthMeasureSpecWidth;
			Log.d("SeekButton", "UNSPECIFIED ON HEIGHT PASSED TO US: Width:" + widthSideDims + "  Height:" + heightSideDims);
		}else {

			//We are being limited on both sides
			//Find the smallest limitation and set both sides
			//to that dimension, because we want to be a perfect square
			int smallestSide = Math.min(heightMeasureSpecHeight, widthMeasureSpecWidth);
			heightSideDims = smallestSide;
			widthSideDims = smallestSide;

			Log.d("SeekButton", "NO UNSPECIFIED PASSED TO US: Width:" + widthSideDims + "  Height:" + heightSideDims);
		}

		//Set out final size
		setMeasuredDimension(widthSideDims, heightSideDims);
		Log.d("SeekButton", "Dimensions PASSED TO US: Width:" + widthMeasureSpecWidth + "  Height:" + heightMeasureSpecHeight);

		centerPoint = widthSideDims/2;
		Log.d("SeekButton", "Center point of view:" + centerPoint);

		return;
	}


	private Point downPoint, upPoint;
	private Point startOuterPoint;
	private boolean drawLink = false;
	private int drawAngle = 0;
	private int lastCalcAngle = 0;
	private boolean rotateKnob=false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub



		if( event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_MOVE){
			//Calculate distance from center
			float xPoint = Math.abs(centerPoint -  event.getX());
			float yPoint = Math.abs(centerPoint -  event.getY());
			double cDistance = Math.sqrt( (xPoint*xPoint) + (yPoint*yPoint) );	
			//Log.d("digitalsan", "DOWN Distance from center:" + (int)cDistance);

			downPoint = new Point();
			upPoint = new Point();

			downPoint.x = (int)event.getX();
			downPoint.y = (int)event.getY();




			double xDistCent = downPoint.x - centerPoint;
			double yDistCent = centerPoint - downPoint.y;
			double atan = Math.atan2(xDistCent, yDistCent);


			double angle = Math.toDegrees(atan);
			if(angle < 0){
				angle += 360;
			}

			if(event.getAction() == MotionEvent.ACTION_DOWN ||  Math.abs( angle - lastCalcAngle ) >5 ){
				lastCalcAngle = (int) angle;
				Log.d("digitalsan", "Point on cartesian plain: " + xDistCent + " - " + yDistCent);
				Log.d("digitalsan", "Tangent: " + atan);
				Log.d("digitalsan", "Degrees: " + angle);
				rotateKnob=true;
				invalidate();
				soundPool.play(sound, 1.0f, 1.0f, 1, 0, 1);
			}
			//-----------------------------------------------------

			drawLink = false;
			invalidate();
			//Log.d("digitalsan", "Outer circumfrence point:" + (int)startOuterPoint.x + " - " + (int)startOuterPoint.y);

			return true;
		}


		if( event.getAction() == MotionEvent.ACTION_UP ){
			//Calculate distance from center
			float xPoint = Math.abs(centerPoint -  event.getX());
			float yPoint = Math.abs(centerPoint -  event.getY());
			double cDistance = Math.sqrt( (xPoint*xPoint) + (yPoint*yPoint) );
			Log.d("digitalsan", "UP Distance from center:" + (int)cDistance + "\n");

			upPoint.x = (int)event.getX();
			upPoint.y = (int)event.getY();
			drawLink = true;

			invalidate();
			return true;
		}

		super.onTouchEvent(event);
		return true;
	}


	private void drawOuter(Canvas canvas){
		if(startOuterPoint == null)
			return;

		canvas.drawCircle(startOuterPoint.x, startOuterPoint.y, 50f, circleLinkPaint);
	}

	private void drawLinkingLine(Canvas canvas){
		if(downPoint ==null || upPoint==null || !drawLink)
			return;

		canvas.drawLine(downPoint.x, downPoint.y, upPoint.x, upPoint.y, circleLinkPaint);	
	}



}
