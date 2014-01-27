package com.dave.needforspeed.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.dave.needforspeed.view.GameView;
import com.dave.needforspeed.view.GarageView;
import com.dave.needforspeed.view.HomeView;

public class MainActivity extends Activity {
	public static float screenWidth;
	public static float screenHeight;
	
	public static final int VIEW_GAME = 2;
	public static final int VIEW_HOME = 3;
	public static final int VIEW_GARAGE = 4;

	private HomeView homeView;
	private GameView gameView;
	private GarageView garageView;
	
	private ChangeViewHandler changeViewHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
		homeView = new HomeView(this);
		gameView = new GameView(this);
		garageView = new GarageView(this);
		changeViewHandler = new ChangeViewHandler();
		setContentView(homeView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK://返回键
			 // 创建退出对话框  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            // 设置对话框标题  
            isExit.setTitle("系统提示");  
            // 设置对话框消息  
            isExit.setMessage("确定要退出吗");  
            isExit.setButton(DialogInterface.BUTTON_POSITIVE,  "确定", new exitButtonListener());
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new exitButtonListener());
            // 显示对话框  
            isExit.show();  
			break;

		default:
			break;
		}
		//返回值很重要：返回true表示你已经处理了，就不会在onKeyDown的事件中继续传递，所以相当于在当前activity中，所有的keyevent都被拦截了。
		//返回 false 与 super.onKeyDown(keyCode, event)效果一样。不过建议用调用super。
		return true;
	}
	
	class exitButtonListener implements  android.content.DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				finish();//销毁对应的activity
//				System.exit(0);//直接退出系统，在android中每一个应用对应一个虚拟机，该方法就是直接停止虚拟机
				break;

			default:
				break;
			}
		}
	}

	public void setContentViewById(int viewId) {
		Message msg = new Message();
		msg.what = viewId;
		changeViewHandler.sendMessage(msg);
	}
	
	@SuppressLint("HandlerLeak")
	class ChangeViewHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case VIEW_GAME:
				setContentView(gameView);
				break;
			case VIEW_HOME:
				setContentView(homeView);
				break;
			case VIEW_GARAGE:
				setContentView(garageView);
				break;
			}
			super.handleMessage(msg);
		}
	}


}
