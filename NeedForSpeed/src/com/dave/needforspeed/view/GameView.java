package com.dave.needforspeed.view;

import java.io.IOException;
import java.util.Hashtable;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dave.needforspeed.main.MainActivity;
import com.dave.needforspeed.main.R;

public class GameView extends SurfaceView implements Callback{


	private MainActivity mainActivity;
	private SurfaceHolder surfaceHolder;
	
	private MediaPlayer enginePlayer;
	
	private Hashtable<Integer, Integer> skillSoundId;
	
	private SoundPool skillSoundPool;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GameView(MainActivity context) {
		super(context);
		this.mainActivity = context;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		enginePlayer = MediaPlayer.create(mainActivity, R.raw.sound_engine);
		enginePlayer.setLooping(true);
		skillSoundId = new Hashtable<Integer, Integer>();
		skillSoundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
		
		skillSoundId.put(0, skillSoundPool.load(mainActivity, R.raw.sound_skill_0, 1));
		skillSoundId.put(1, skillSoundPool.load(mainActivity, R.raw.sound_skill_1, 1));
		skillSoundId.put(2, skillSoundPool.load(mainActivity, R.raw.sound_skill_2, 1));
		skillSoundId.put(3, skillSoundPool.load(mainActivity, R.raw.sound_skill_3, 1));
		skillSoundId.put(4, skillSoundPool.load(mainActivity, R.raw.sound_skill_4, 1));
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("game surface changed-----width:" + width + "height:" + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		enginePlayer.start();
		
		System.out.println("game surface created");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("game surface destroyed");
		if(enginePlayer.isPlaying())
			enginePlayer.pause();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(y > 380) {
				if(x < 160) {
					skillSoundPool.play(skillSoundId.get(0), 1, 1, 1, 0, 1);
				} else if(x < 320){
					skillSoundPool.play(skillSoundId.get(1), 1, 1, 1, 0, 1);
				} else if(x < 480){
					skillSoundPool.play(skillSoundId.get(2), 1, 1, 1, 0, 1);
				} else if(x < 640){
					skillSoundPool.play(skillSoundId.get(3), 1, 1, 1, 0, 1);
				} else {
					skillSoundPool.play(skillSoundId.get(4), 1, 1, 1, 0, 1);
				}
			} else {
				mainActivity.setContentViewById(MainActivity.VIEW_HOME);
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

}
