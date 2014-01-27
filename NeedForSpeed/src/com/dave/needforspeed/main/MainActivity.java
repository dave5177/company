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
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//ȫ��
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//����
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
		case KeyEvent.KEYCODE_BACK://���ؼ�
			 // �����˳��Ի���  
            AlertDialog isExit = new AlertDialog.Builder(this).create();  
            // ���öԻ������  
            isExit.setTitle("ϵͳ��ʾ");  
            // ���öԻ�����Ϣ  
            isExit.setMessage("ȷ��Ҫ�˳���");  
            isExit.setButton(DialogInterface.BUTTON_POSITIVE,  "ȷ��", new exitButtonListener());
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ��", new exitButtonListener());
            // ��ʾ�Ի���  
            isExit.show();  
			break;

		default:
			break;
		}
		//����ֵ����Ҫ������true��ʾ���Ѿ������ˣ��Ͳ�����onKeyDown���¼��м������ݣ������൱���ڵ�ǰactivity�У����е�keyevent���������ˡ�
		//���� false �� super.onKeyDown(keyCode, event)Ч��һ�������������õ���super��
		return true;
	}
	
	class exitButtonListener implements  android.content.DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				finish();//���ٶ�Ӧ��activity
//				System.exit(0);//ֱ���˳�ϵͳ����android��ÿһ��Ӧ�ö�Ӧһ����������÷�������ֱ��ֹͣ�����
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
