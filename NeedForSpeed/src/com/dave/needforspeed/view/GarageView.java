package com.dave.needforspeed.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dave.needforspeed.main.MainActivity;
import com.dave.needforspeed.main.R;

public class GarageView extends SurfaceView implements Callback, Runnable {

	private MainActivity mainActivity;
	private SurfaceHolder surfaceHolder;

	private Paint paint;

	private Bitmap bmp_back;
	private Bitmap[] bmpArr_car;

	private boolean loop_flag;
	private boolean visible;
	
	private int showCarIndex;
	
	/**
	 * 当前车子x坐标。
	 */
	private float x_t_car;

	/**
	 * 移动时已成为历史的车子的x轴坐标
	 */
	private float x_l_car;
	
	/**
	 * 按键后车子移动的状态。1：按右键，车子向左移动；2：按左键，车子向右移动。
	 */
	private int carMove;

	public GarageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GarageView(MainActivity context) {
		super(context);
		this.mainActivity = context;

		paint = new Paint();
		paint.setAntiAlias(true);

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		initBitmap();
	}

	private void initBitmap() {
		bmp_back = BitmapFactory.decodeResource(getResources(),
				R.drawable.garage_back);

		bmpArr_car = new Bitmap[9];
		bmpArr_car[0] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_0);
		bmpArr_car[1] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_1);
		bmpArr_car[2] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_2);
		bmpArr_car[3] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_3);
		bmpArr_car[4] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_4);
		bmpArr_car[5] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_5);
		bmpArr_car[6] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_6);
		bmpArr_car[7] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_7);
		bmpArr_car[8] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_8);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("game surface changed-----width:" + width
				+ "height:" + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		loop_flag = true;
		visible = true;
		new Thread(this).start();

		System.out.println("game surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		loop_flag = false;
		System.out.println("game surface destroyed");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mainActivity.setContentViewById(MainActivity.VIEW_HOME);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
			
		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	public void drawSelf() {
		Canvas canvas = surfaceHolder.lockCanvas();
		if (canvas != null) {
			canvas.drawBitmap(bmp_back, 0, 0, paint);
			canvas.drawBitmap(bmpArr_car[showCarIndex], 340, 160, paint);
			
			surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void run() {
		while (loop_flag) {
			if (visible) {
				logic();
				drawSelf();
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void logic() {
		if (carMove == 1) {
			x_l_car -= 100;
			x_t_car -= 100;
			if (x_t_car <= 320) {
				x_t_car = 320;
				carMove = 0;
			}
		} else if (carMove == 2) {
			x_l_car += 100;
			x_t_car += 100;
			if (x_t_car >= 320) {
				x_t_car = 320;
				carMove = 0;
			}
		}
	}

}
