package com.dave.needforspeed.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dave.needforspeed.assistant.Assistant;
import com.dave.needforspeed.main.MainActivity;
import com.dave.needforspeed.main.R;

@SuppressLint("ViewConstructor")
public class HomeView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder surfaceHolder;
	
	private MainActivity mainActivity;
	private Bitmap bmp_back;
	private Bitmap[][] a_bmp_btn;
		
	private boolean loop_flag;
	
	private boolean visible;

	private Paint paint;
	
	/**
	 * 按到的键的下标值
	 */
	private int touchBtn = -1;
	
	private Rect[] btnRect;
	
	public HomeView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity = mainActivity;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);
		btnRect = new Rect[3];
		for (int i = 0; i < 3; i++) {
			btnRect[i] = new Rect(540, 170 + i * 80, 720, 220 + i * 80);
		}
		initBitmap();
		System.out.println("back size----width:" + bmp_back.getWidth() + ";   height:" + bmp_back.getHeight());
	}

	private void initBitmap() {
		a_bmp_btn = new Bitmap[3][2];
		a_bmp_btn[0][0] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_btn_start);
		a_bmp_btn[0][1] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_cbtn_start);
		a_bmp_btn[1][0] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_btn_mycar);
		a_bmp_btn[1][1] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_cbtn_mycar);
		a_bmp_btn[2][0] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_btn_quit);
		a_bmp_btn[2][1] = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_cbtn_quit);
		bmp_back = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.home_back);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		System.out.println("home surface changed-----width:" + width + "height:" + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("home surface created");
		MainActivity.screenWidth = getWidth();
		MainActivity.screenHeight = getHeight();
		loop_flag = true;
		visible = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("home surface destroyed");
		loop_flag = false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchBtn = Assistant.pointInRect(x, y, btnRect);
//			loop_flag = false;
//			mainActivity.setContentViewById(MainActivity.VIEW_GAME);
			break;
		case MotionEvent.ACTION_MOVE:
			if(touchBtn != -1 && Assistant.pointInRect(x, y, btnRect) != touchBtn)
				touchBtn = -1;
			break;
		case MotionEvent.ACTION_UP:
			if(Assistant.pointInRect(x, y, btnRect) == touchBtn)
				handleTouchBtn(touchBtn);
			touchBtn = -1;
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 去到下一个界面
	 * @param viewIndex 0：游戏界面；1：我的车库；2：退出游戏
	 */
	private void handleTouchBtn(int viewIndex) {
		visible = false;
		switch (viewIndex) {
		case 0:
			mainActivity.setContentViewById(MainActivity.VIEW_GAME);
			break;
		case 1:
			mainActivity.setContentViewById(MainActivity.VIEW_GARAGE);
			break;
		case 2:
			mainActivity.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void run() {
		while (loop_flag) {
			if(visible)
				drawSelf();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void drawSelf() {
		Canvas canvas = surfaceHolder.lockCanvas();
		if(canvas != null) {
			canvas.drawColor(Color.CYAN);
			canvas.drawBitmap(bmp_back, 0, 0, paint);
			for (int i = 0; i < 3; i++) {
				int color = paint.getColor();
				paint.setColor(0xff01bad8);
				paint.setStyle(Style.FILL);
				canvas.drawRect(btnRect[i], paint);
				paint.setColor(color);
				if(touchBtn == i) 
					canvas.drawBitmap(a_bmp_btn[i][1], 580, 180 + i * 80, paint);
				else 
					canvas.drawBitmap(a_bmp_btn[i][0], 580, 180 + i * 80, paint);
			}
			canvas.save();
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

}
