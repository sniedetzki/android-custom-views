package com.digitalsan.mustangviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;

public class SeekButton extends View{


	RectF rectf = new RectF(0, 0, 300, 300);
	Paint paint = new Paint();
	
	//Views canvas
	Canvas canvas = null;
	
	RectF rectf2;

	// Arrow Buttons
	Path hButtonPath, vButtonPath;
	PathShape hSeekShape, vSeekShape;
	ShapeDrawable hButtonDrawable, vButtonDrawable;	
	Paint arrowButtonPaint;

	//Regions for touch buttons
	Region leftButtonReg, topButtonReg, rightButtonReg, bottomButtonReg;

	//Center Seek Button
	RectF centerSeek;
	Paint textPaint;

	Path arrowPath,arrowPathH;

	public SeekButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// TODO Auto-generated constructor stub
	}

	public SeekButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// TODO Auto-generated constructor stub
	}

	public SeekButton(Context context) {
		super(context);
		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		// TODO Auto-generated constructor stub
	}


	//Provide the size we want
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMeasureSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMeasureSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		//Get our sizes passed by parent
		int widthMeasureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
		int heightMeasureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);

		//Parent is asking what the dimensions want to be
		if( widthMeasureSpecMode == MeasureSpec.UNSPECIFIED && heightMeasureSpecMode == MeasureSpec.UNSPECIFIED){
			setMeasuredDimension(300, 200);
			Log.d("SeekButton", "UNSPECIFIED ON BOTH PASSED TO US: Width:" + getMeasuredWidth() + "  Height:" + getMeasuredHeight());
			return;
		} 


		//What both sides should be set to
		int heightSideDims, widthSideDims;

		//Find largest size our parent says we can be
		if( widthMeasureSpecMode == MeasureSpec.UNSPECIFIED ){
			heightSideDims = heightMeasureSpecHeight;
			widthSideDims = 3*(heightSideDims/2);
			Log.d("SeekButton", "UNSPECIFIED ON WIDTH PASSED TO US: Width:" + widthSideDims + "  Height:" + heightSideDims);
		}else if ( heightMeasureSpecMode  == MeasureSpec.UNSPECIFIED ) {
			widthSideDims = widthMeasureSpecWidth;
			heightSideDims = widthSideDims*(2/3);
			Log.d("SeekButton", "UNSPECIFIED ON HEIGHT PASSED TO US: Width:" + widthSideDims + "  Height:" + heightSideDims);
		}else {
			if( ((double)2*widthMeasureSpecWidth/3) <= heightMeasureSpecHeight ){
				heightSideDims = (int) ((double)2*widthMeasureSpecWidth/3);
				widthSideDims = widthMeasureSpecWidth;
				Log.d("SeekButton", "NO UNSPECIFIED PASSED TO US, WIDTH*2/3 fits: Width:" + widthSideDims + "  Height:" + heightSideDims);
			}else{
				heightSideDims = heightMeasureSpecHeight;
				widthSideDims = 3*(heightSideDims/2);
				Log.d("SeekButton", "NO UNSPECIFIED PASSED TO US, , WIDTH*2/3 does NOT fits: Width:" + widthSideDims + "  Height:" + heightSideDims);
			}

		}


		//Set out final size
		setMeasuredDimension(widthSideDims, heightSideDims);
		Log.d("SeekButton", "Dimensions PASSED TO US: Width:" + widthMeasureSpecWidth + "  Height:" + heightMeasureSpecHeight);

		drawButtons();
		return;
	}



	@SuppressLint("NewApi")
	private void drawButtons(){

		//Always 3w X 2h

		int widthOfDraw = getMeasuredWidth();
		int heightOfDraw = getMeasuredHeight();

		//Main calculation, width of centerbox
		//fraction of total width
		float widthCenterBoxFraction = (float).45;

		// Center BOX Width
		float widthCenterBox = widthCenterBoxFraction * widthOfDraw;
		float leftCenterBox = (widthOfDraw-widthCenterBox)/2;
		float rightCenterBox = widthOfDraw-leftCenterBox;

		// Center BOX Height
		float heightCenterBox = 2*(widthCenterBox/3);
		float topCenterBox = (heightOfDraw-heightCenterBox)/2;
		float bottomCenterBox = heightOfDraw -topCenterBox;

		//-----------------------------------------
		
		//Build center box
		centerSeek = new RectF(leftCenterBox, topCenterBox, 
				rightCenterBox, bottomCenterBox);

		//-----------------------------------------
		
		/** Top Seek Button **/
		hButtonPath = new Path();

		//Draw top edge of horizontal seeks
		hButtonPath.moveTo(0, 0);
		hButtonPath.lineTo(widthOfDraw, 0);

		//Draw bottom edge of horizontal seeks
		hButtonPath.lineTo(rightCenterBox, topCenterBox);
		hButtonPath.lineTo(leftCenterBox,topCenterBox);

		//Close horizontal seeks
		hButtonPath.lineTo(0, 0);
		hButtonPath.close();

		//----------------------------------------

		/** Left/Right Seek Button **/
		vButtonPath = new Path();

		//Draw Left edge of vertical seeks
		vButtonPath.moveTo(0, 0);
		vButtonPath.lineTo(0, heightOfDraw);

		//Draw Right edge of vertical seeks
		vButtonPath.lineTo(leftCenterBox, bottomCenterBox);
		vButtonPath.lineTo(leftCenterBox, topCenterBox);

		//Close vertical seeks
		vButtonPath.lineTo(0, 0);
		vButtonPath.close();

		//-----------------------------------------

		//Arrows for seek buttons
		float arrowSide = leftCenterBox/3;
		float arrowCenterY = heightOfDraw/2;
		arrowPath = new Path();
		arrowPath.moveTo(arrowSide, arrowCenterY);
		arrowPath.lineTo(arrowSide*2, arrowCenterY-arrowSide);
		arrowPath.lineTo(arrowSide*2, arrowCenterY+arrowSide);
		arrowPath.close();

		float arrowHorzSide = topCenterBox/3;
		float arrowHorzCenterY = widthOfDraw/2;
		arrowPathH = new Path();
		arrowPathH.moveTo(arrowHorzSide, arrowHorzCenterY);
		arrowPathH.lineTo(arrowHorzSide*2, arrowHorzCenterY-arrowHorzSide);
		arrowPathH.lineTo(arrowHorzSide*2, arrowHorzCenterY+arrowHorzSide);
		arrowPathH.close();

		//------------------------------------------

		//Shapes used to draw
		hSeekShape = new PathShape(hButtonPath, getMeasuredWidth(), getMeasuredHeight());
		vSeekShape = new PathShape(vButtonPath, getMeasuredWidth(), getMeasuredHeight());
		
		//------------------------------------------

		//Gradiet to add depth to seek center
		int colors[] = {Color.BLACK, Color.GRAY};
		GradientDrawable gd = new GradientDrawable();
		gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		gd.setGradientCenter(0, 0);
		gd.setColors(colors);

		//Gradients for buttons
		LinearGradient hSeekGradient = new LinearGradient(0, 0, 0, heightOfDraw/2, Color.BLACK, Color.GRAY, TileMode.CLAMP);
		LinearGradient vSeekGradient = new LinearGradient(0, 0, widthOfDraw/2, 0, Color.BLACK, Color.GRAY, TileMode.CLAMP);

		//------------------------------------------
		
		//Paint for seek buttons
		arrowButtonPaint = new Paint();
		arrowButtonPaint.setStyle(Paint.Style.FILL);
		arrowButtonPaint.setAlpha(128);
		arrowButtonPaint.setStrokeWidth(1);
		
		//-------------------------------------------
		
		//Setup shapre for buttons
		hButtonDrawable = new ShapeDrawable(hSeekShape);
		hButtonDrawable.getPaint().set(arrowButtonPaint);
		hButtonDrawable.getPaint().setShader(hSeekGradient);

		vButtonDrawable = new ShapeDrawable(vSeekShape);
		hButtonDrawable.getPaint().set(arrowButtonPaint);
		vButtonDrawable.getPaint().setShader(vSeekGradient);

		hButtonDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
		vButtonDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());

		//---------------------------------------------
		
		//Paint for center 'seek'
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(250);
		textPaint.setTextAlign(Align.CENTER);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Log.d("SeekButton", "Measured Width:" + getMeasuredWidth() + "  Height:" + getMeasuredHeight());
		
		int top = getTop();
		Log.d("Top:", "Top:" + top);

		//Expose canvas at view level
		this.canvas = canvas;

		Rect rect = canvas.getClipBounds();

		int strokeWidth= 67;

		//-----------------------------------
		//Draw buttons
		canvas.save();

		//TOP/LEFT BUTTON
		hButtonDrawable.draw(canvas);
		vButtonDrawable.draw(canvas);

		//Bottom/RIGHT Button
		canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);;
		hButtonDrawable.draw(canvas);
		vButtonDrawable.draw(canvas);
		
		canvas.restore();

		//----------------------------------------

		canvas.save();
		//hButtonShape.getPaint().setStyle(Style.STROKE);
		//hButtonShape.getPaint().setColor(Color.BLACK);
		//hButtonShape.getPaint().setAlpha(255);
		//hButtonShape.getPaint().setStrokeWidth(5);
		//hButtonShape.getPaint().setShader(null);
		//vButtonShape.getPaint().setStyle(Style.STROKE);
		//vButtonShape.getPaint().setColor(Color.BLACK);
		//vButtonShape.getPaint().setAlpha(255);
		//vButtonShape.getPaint().setStrokeWidth(5);
		//vButtonShape.getPaint().setShader(null);


		//TOP/LEFT BUTTON
		//hButtonShape.draw(canvas);
		//vButtonShape.draw(canvas);	




		//Touch setup
		RectF buttonRectF = new RectF();
		vButtonPath.computeBounds(buttonRectF, true);
		leftButtonReg = new Region();
		leftButtonReg.setPath(vButtonPath, new Region((int)buttonRectF.left, (int)buttonRectF.top,(int)buttonRectF.right, (int)buttonRectF.bottom));





		//Bottom/RIGHT Button
		canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);;
		//hButtonShape.draw(canvas);
		//vButtonShape.draw(canvas);
		canvas.restore();



		RadialGradient rd1 = new RadialGradient(getWidth()/2, getHeight()/2, getWidth(), Color.BLACK, Color.GRAY, TileMode.CLAMP);
		Paint thePaint = new Paint();
		thePaint.setShader(rd1);
		canvas.drawRect(centerSeek, thePaint);

		thePaint.setStyle(Style.STROKE);
		thePaint.setColor(Color.BLACK);
		thePaint.setStrokeWidth(2);
		thePaint.setShader(null);
		canvas.drawRect(centerSeek, thePaint);
		//bps.draw(this.canvas, hButtonShape.getPaint());

		drawRectText("SEEK", canvas, centerSeek);


		canvas.drawPath(arrowPath, textPaint);
		canvas.save();
		canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		canvas.drawPath(arrowPath, textPaint);
		canvas.restore();

		canvas.save();
		canvas.rotate(90);
		canvas.drawPath(arrowPathH, textPaint);
		canvas.rotate(180, getMeasuredWidth()/2, getMeasuredHeight()/2);
		canvas.drawPath(arrowPathH, textPaint);
		canvas.restore();



		//hButtonShape.draw(canvas);
		//setBackgroundDrawable(sd);
		//canvas.drawRect(rectf2, paint);


		return;

		/** check for zone 1 to 3
		if(zone >= 1 && zone <= 3)
		{
			drawTouchZones(zone);
		}**/
	}





	private void drawRectText(String text, Canvas canvas, RectF r) {

		textPaint.setTextSize(100);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setShadowLayer(100, 10, 10, Color.GRAY);
		int width = (int) r.width();

		int xPos = (canvas.getWidth() / 2);
		int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ; 


		int numOfChars = textPaint.breakText(text,true,width,null);
		int start = (text.length()-numOfChars)/2;
		canvas.drawText(text,start,numOfChars,xPos,yPos,textPaint);
		//canvas.drawText(text,start,numOfChars,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Bitmap b = Bitmap.createBitmap( getWidth(), getHeight(), Bitmap.Config.ARGB_8888);  
		Canvas c = new Canvas(b);

		PointerCoords pc = new PointerCoords();
		event.getPointerCoords(0, pc);

		this.draw(c);

		int color = b.getPixel((int)pc.x, (int)pc.y);
		Log.d("SeekButton", "Color under touch:" + color);

		//onTouch(this, event);



		String TAG = "Touch Test";

		Point point = new Point();
		point.x = (int) event.getX();
		point.y = (int) event.getY();

		invalidate();
		Log.d(TAG, "point: " + point);

		if(leftButtonReg.contains((int)point.x,(int) point.y)  && event.getAction() == MotionEvent.ACTION_DOWN){
			Log.d(TAG, "Touch IN");
			textPaint.setColor(Color.BLUE);
			return true;
		}else {
			Log.d(TAG, "Touch OUT");
			textPaint.setColor(Color.WHITE);

		}

		Log.d(TAG, "TEST");
		invalidate();



		return true;
		//return super.onTouchEvent(event);
	}

	protected void drawTouchZones(Integer zone)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setColor(Color.WHITE);
		paint.setAlpha(75);

		Path path = new Path();

		if(zone == 1) {
			path.moveTo(150,150);
			path.lineTo(150,0);
			path.arcTo(rectf, 270, 120);
			path.close();
		} else if(zone == 2) {
			path.moveTo(150,150);
			path.arcTo(rectf, 30, 120);
			path.lineTo(150,150);
			path.close();
		} else if(zone == 3) {
			path.moveTo(150,0);
			path.lineTo(150,150);
			path.arcTo(rectf, 150, 120);
			path.close();
		}

		canvas.drawPath(path, paint);       
	}


}
