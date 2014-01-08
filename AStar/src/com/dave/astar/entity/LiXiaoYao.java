package com.dave.astar.entity;

import com.dave.astar.hexagon.Hexagon;
import com.dave.astar.main.AStarView;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LiXiaoYao {
	private float x, y;
	
	private int state;
	
	private int color;

	/**
	 * Ä¿±êÁù±ßÐÎ
	 */
	private Hexagon targetHexagon;
	
	private int stepIndex;

	private int moveSpeed;

	private AStarView aStarView;
	
	public final static int STATE_MOVING = 1;
	public final static int STATE_WAITTING = 2;
	public final static int STATE_STOPED = 3;

	public LiXiaoYao(int x, int y, int color, AStarView aStarView) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.aStarView = aStarView;
	}
	
	public void drawSelf(Canvas canvas, Paint paint) {
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(x, y, 20, paint);
	}
	
	public void logic() {
		if(state == STATE_MOVING) {
			moveSpeed ++;
			if(moveSpeed > 1) {
				moveSpeed = 0;
				if(stepIndex > 0) {
					stepIndex --;
					setPosition(aStarView.route.get(stepIndex).getPosition());
				} else {
					setState(STATE_STOPED);
				}
			}
		}
	}
	
	private void setPosition(float[] position) {
		x = position[0];
		y = position[1];
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public float[] getPosition() {
		return new float[]{x, y};
	}

	public void changeTarget(Hexagon targetHexagon) {
		this.targetHexagon = targetHexagon;
	}

	public void setStepIndex(int stepIndex) {
		this.stepIndex = stepIndex;
	}

	
}
