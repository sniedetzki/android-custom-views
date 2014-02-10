package com.digitalsan.mustangviews;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.digitalsan.android_mustang_views.R;

public class KnobView extends View implements OnGestureListener{

	//User Options
	private boolean rotateCenter = false;
	
	//Activity Context
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
	int sound;	//Resource id of sound

	//Number of notches on dial
	int notchCount = 10;
	
	//The number of actual 'clicks;
	//the dial has, aka: number of 
	//positions dial will snap to
	int clickCount = 10;
	double degPerClick = 360/clickCount;
	
	//How many degrees the user
	//must move, before animation 
	//happens, this is different from
	//actually triggering an event
	int degsToRotate = 2;
	
	
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
		circleNotchPaint.setStrokeCap(Cap.ROUND);

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
		
		// Square so both sides same dimensions
		//int sideCanvasDims = Math.min(canvas.getHeight(), canvas.getWidth());
		int sideCanvasDims = Math.min(canvas.getClipBounds().width(), canvas.getClipBounds().height());
		int centerOfCanvas = sideCanvasDims/2;
		
		//Get width of outline, we need to compensate for this
		//when we draw the outline, otherwise some of the outline
		//will exceed the canvas bounds, creating square in certain
		//spot on the the circle
		float outlineCompensation = circleOutlinePaint.getStrokeWidth() / 2;
		float outlineRadius = centerOfCanvas - outlineCompensation;
		
		// Should the canvas simulate rotation
		// Was display invalidated by user spinning
		// the dial?		MotionEvent.action_	
		if( rotateKnob ){
			lastDrawAngle+=animAngleDelta;
			canvas.rotate( (float)lastDrawAngle, centerOfCanvas, centerOfCanvas );
			rotateKnob = false;
		}		
		
		//Draw background of circle and ring around circle
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, centerOfCanvas, circleBackPaint);
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, outlineRadius, circleOutlinePaint);
		
		//Number of degrees to rotate for
		//each notch drawn on circle
		int rotateDegress = 360/notchCount;

		//Length of line that draws the notch
		int notchSize = centerOfCanvas/3;
		
		//Draw each notch
		for(int i=0; i<notchCount; i++){
			canvas.drawLine(centerOfCanvas, notchSize, centerOfCanvas, notchSize*2, circleNotchPaint);
			canvas.rotate(rotateDegress,centerOfCanvas,centerOfCanvas);
		}
		
		//Draw center depending on user preference
		if( !rotateCenter){
			canvas.restore();
		}
		
		canvas.drawCircle(centerOfCanvas, centerOfCanvas, notchSize, circleCenterPaint);
		canvas.drawText("VOLUME", centerOfCanvas, centerOfCanvas, circleTextPaint);
		
		canvas.restore();
		return;
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


	private boolean rotateKnob=false;
	private double lastCalcAngle = 0;
	private double lastAnimAngle = 0;
	private double lastDrawAngle = 0;
	private double animAngleDelta = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		//We do similar processing for DOWN and MOVE actions
		//Some processing is unique for each event and
		//specified within this if block
		if( event.getAction() == MotionEvent.ACTION_MOVE || 
				event.getAction() == MotionEvent.ACTION_DOWN){
			
			//Calculate rotation in rads
			double xDistCent = event.getX() - centerPoint;
			double yDistCent = centerPoint - event.getY();
			double atan = Math.atan2(xDistCent, yDistCent);

			//Calculate the angle in degrees
			//In degrees to use with rotating
			//the canvas when drawing.
			double angle = Math.toDegrees(atan);
			if(angle < 0){
				angle += 360;
			}

			//Store where user touched
			//Used to determine initial point point so wheel
			//doesn't 'jump' when touched.
			if( event.getAction() == MotionEvent.ACTION_DOWN ){
				lastAnimAngle = angle;
				return true;
			}
			
			//Get delta since between current angle
			//and the angle last time we animated the wheel
			animAngleDelta = angle - lastAnimAngle;
			
			//If user rotated enough,
			//trigger an animation of
			//the dial
			if( Math.abs(animAngleDelta) > 1 ){
				lastAnimAngle = angle;
				rotateKnob=true;
				invalidate();
			}
				
			//Get delta since between current angle
			//and the angle last time the wheel 'snapped'
			//to a fixed spot
			double clickAngleDelta = Math.abs( angle - lastCalcAngle );
			
			//If user rotated enough,
			//trigger a 'snap' to next point
			// on the wheel
			if( clickAngleDelta >  degPerClick ){
				lastCalcAngle = angle;
				soundPool.play(sound, 1.0f, 1.0f, 1, 0, 1);
			}
			return true;
		}


		if( event.getAction() == MotionEvent.ACTION_UP ){
			invalidate();
			return true;
		}

		super.onTouchEvent(event);
		return false;
	}

	//USER OPTION GETTERS/SETTERS Below
	public boolean getRotateCenter() {
		return rotateCenter;
	}

	public void setRotateCenter(boolean rotateCenter) {
		this.rotateCenter = rotateCenter;
	}

	//Gesture detection
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Toast.makeText(mContext, "Fling!", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}



}



/** Storage
 * 				Log.d("digitalsan", "Point on cartesian plain: " + xDistCent + " - " + yDistCent);
				Log.d("digitalsan", "Tangent: " + atan);
				Log.d("digitalsan", "Degrees: " + angle);
**/
