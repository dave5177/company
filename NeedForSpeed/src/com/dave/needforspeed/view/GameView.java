package com.dave.needforspeed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dave.needforspeed.main.MainActivity;

public class GameView extends SurfaceView implements Callback{


	private MainActivity mainActivity;
	private SurfaceHolder surfaceHolder;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GameView(MainActivity context) {
		super(context);
		this.mainActivity = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("game surface changed-----width:" + width + "height:" + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("game surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("game surface destroyed");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mainActivity.setContentViewById(MainActivity.VIEW_HOME);
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
