package com.dave.needforspeed.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.dave.needforspeed.assistant.Assistant;
import com.dave.needforspeed.main.MainActivity;
import com.dave.needforspeed.main.R;

@SuppressLint("Recycle")
public class GarageView extends SurfaceView implements Callback, Runnable {

	private MainActivity mainActivity;
	private SurfaceHolder surfaceHolder;

	private Paint paint;

	private Bitmap bmp_back;
	private Bitmap[] bmpArr_car;
	private Bitmap[] bmpArr_btn;

	private boolean loop_flag;
	private boolean visible;
	
	private int showCarIndex;
	
	/**
	 * 当前车子x坐标。
	 */
	private float x_car_start = 240;

	/**
	 * 按键后车子移动的状态。1：按右键，车子向左移动；2：按左键，车子向右移动。
	 */
	private int carMove;
	
	/**
	 * 触屏事件速率控制
	 */
	private VelocityTracker velocityTracker;
	private float mTouchDownX;
	
	/**
	 * 滑屏车辆移动速度
	 */
	private float velCarMove;
	
	private Rect btnRect;
	
	/**
	 * 按钮被按住
	 */
	private boolean btnTouching;
	
	public final static float SNAP_VEL = 600;

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

		velocityTracker = VelocityTracker.obtain();
		velocityTracker.computeCurrentVelocity(1000);//设置速度单位（1000ms移动多少个像素）
		
		btnRect = new Rect(660, 420, 780, 467);
		
		initBitmap();
	}

	private void initBitmap() {
		bmp_back = BitmapFactory.decodeResource(getResources(),
				R.drawable.garage_back);

		TypedArray carBmps = getResources().obtainTypedArray(R.array.cars);
		bmpArr_car = new Bitmap[9];
		for (int i = 0; i < 9; i++) {
			bmpArr_car[i] = ((BitmapDrawable)carBmps.getDrawable(i)).getBitmap();
		}
//		bmpArr_car[0] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_0);
//		bmpArr_car[1] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_1);
//		bmpArr_car[2] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_2);
//		bmpArr_car[3] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_3);
//		bmpArr_car[4] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_4);
//		bmpArr_car[5] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_5);
//		bmpArr_car[6] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_6);
//		bmpArr_car[7] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_7);
//		bmpArr_car[8] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_car_8);
		
		bmpArr_btn = new Bitmap[4];
		bmpArr_btn[0] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_btn_buy);
		bmpArr_btn[1] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_cbtn_buy);
		bmpArr_btn[2] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_btn_choose);
		bmpArr_btn[3] = BitmapFactory.decodeResource(getResources(), R.drawable.garage_cbtn_choose);
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
		velocityTracker.addMovement(event);//将时间添加到速度控制类中
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(carMove != 0) {
				carMove = 0;
			}
			mTouchDownX = x;
			
			if(Assistant.isPointInRect(x, y, btnRect))
				btnTouching = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if(btnTouching) {
				if(!Assistant.isPointInRect(x, y, btnRect))
					btnTouching = false;
			} else {
				scrollCarBy(x - mTouchDownX, 0);
				mTouchDownX = x;
			}
			break;
		case MotionEvent.ACTION_UP:
			if(btnTouching) {
				btnTouching = false;
			} else {
				velocityTracker.computeCurrentVelocity(1000);//每次获取速度前都需要设置单位
				float xVel = velocityTracker.getXVelocity();//得到x方向滑动速度
				System.out.println("滑动速度" + xVel);
				if(xVel > SNAP_VEL) {//右滑速度大于临界速度
					carMove = 2;
					velCarMove = 50;
					showCarIndex --;
				} else if (xVel < -SNAP_VEL) {//左滑速度大于临界速度
					carMove = 1;
					velCarMove = -50;
					showCarIndex ++;
				} else {
					float dis = (240 - x_car_start) % 540;
					if(dis <= 270) {
						carMove = 2;
						velCarMove = 30;
					} else if(dis >= - 270) {
						carMove = 1;
						velCarMove = -30;
					} else if(dis > 270) {
						carMove = 1;
						velCarMove = -30;
						showCarIndex ++;
					} else if(dis < -270) {
						carMove = 2;
						velCarMove = 30;
						showCarIndex --;
					}
				}
				if(showCarIndex < 0) {
					showCarIndex = 0;
					carMove = 1;
					velCarMove = -30;
				}else if(showCarIndex > 8) {
					showCarIndex = 8;
					carMove = 2;
					velCarMove = 30;
				}
			}
			break;
		}
		return true;
	}
	
	public void scrollCarBy(float x, float y) {
		if(x_car_start > 100 - 540 * 8 && x_car_start < 400)
			x_car_start += x;
	}

	public void drawSelf() {
		Canvas canvas = surfaceHolder.lockCanvas();
		if (canvas != null) {
			canvas.drawBitmap(bmp_back, 0, 0, paint);
			for (int i = 0; i < bmpArr_car.length; i++) {
				if(Assistant.carLevel[i] > 0) {
					paint.setAlpha(200);
				} else {
					paint.setAlpha(100);
				}
				canvas.drawBitmap(bmpArr_car[i], x_car_start + i * 540, 320, paint);
			}
			paint.setAlpha(255);
			
			int color = paint.getColor();
			paint.setColor(0xff01bad8);
			paint.setStyle(Style.FILL);
			canvas.drawRect(btnRect, paint);
			paint.setColor(color);
			
			if(Assistant.carLevel[showCarIndex] > 0) {
				if(btnTouching)
					canvas.drawBitmap(bmpArr_btn[3], 660, 420, paint);
				else
					canvas.drawBitmap(bmpArr_btn[2], 660, 420, paint);
			} else {
				if(btnTouching)
					canvas.drawBitmap(bmpArr_btn[1], 660, 420, paint);
				else
					canvas.drawBitmap(bmpArr_btn[0], 660, 420, paint);
			}
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
		if(carMove == 1) {
			x_car_start += velCarMove;
			if(velCarMove < -20)
				velCarMove += 1;
			if(x_car_start <= 240 - showCarIndex * 540) {
				x_car_start = 240 - showCarIndex * 540;
				carMove = 0;
			}
		} else if(carMove == 2) {
			x_car_start += velCarMove;
			if(velCarMove > 20)
				velCarMove -= 1;
			if(x_car_start >= 240 - showCarIndex * 540) {
				x_car_start = 240 - showCarIndex * 540;
				carMove = 0;
			}
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
	}
}
