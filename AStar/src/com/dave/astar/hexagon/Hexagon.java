package com.dave.astar.hexagon;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


/**
 * @author Dave
 * ����������
 */
public class Hexagon {
	
	/**
	 * ���ĵ�����
	 */
	private float xCenter, yCenter;
	
	/**
	 * �߳�
	 */
	private float side;
	
	/**
	 * �ڷŷ�ʽ
	 */
	private int type;
	
	/**
	 * ����
	 */
	private int effect;
	
	/**
	 * ����
	 */
	private float[] vertex;
	
	/**
	 * ���ڵ�������
	 */
	private LinkedList<Hexagon> neighborList;
	
	/**
	 * ���ڵ�
	 */
	private Hexagon fatherHexagon;

	private Path path;
	
	/**
	 * Ѱ·g��ֵ������һ�ڵ㵽���ڵ�Ĺ�ֵ��
	 */
	private float gValue;
	

	/**
	 * Ѱ·h��ֵ���ӱ��ڵ㵽Ŀ��ڵ�Ĺ�ֵ��
	 */
	private float hValue;
	
	/**
	 * Ѱ·f��ֵ�����յĹ�ֵ��h��ֵ��g��ֵ�ĺͣ�
	 */
	private float fValue;
	
	/**
	 * ��ֱ�ڷ�
	 */
	public static final int TYPE_VERTICAL = 1;
	
	/**
	 * ˮƽ�ڷ�
	 */
	public static final int TYPE_HORIZONTAL = 2;
	
	/**
	 * ��Ϊƽ�����ϰ�����ȫͨ��
	 */
	public static final int EFFECT_PASS = 0x00001;
	
	/**
	 * ��Ϊ�ϰ����޷�ͨ��
	 */
	public static final int EFFECT_HINDER = 0x00002;
	
	public static final int COLOR_PASS = 0xffffffff;
	public static final int COLOR_HINDER = 0xff909090;

	/**
	 * @param xCenter ���ĵ�x����
	 * @param yCenter ���ĵ�y����
	 * @param side �߳�
	 * @param type ���ͣ���ֱ����ˮƽ��
	 */
	public Hexagon(float xCenter, float yCenter, float side, int type, int effect) {
		this.xCenter = xCenter;
		this.yCenter = yCenter;
		this.side = side;
		this.type = type;
		this.effect = effect;
		
		neighborList = new LinkedList<Hexagon>();
		computeVertex();
		initPath();
	}
	
	/**
	 * ·����ʼ��
	 */
	private void initPath() {
		path = new Path();
		path.moveTo(vertex[0], vertex[1]);
		for (int i = 0; i < 5; i++) {
			path.lineTo(vertex[2 + 2 * i], vertex[3 + 2 * i]);
		}
		path.close();
}

	private void computeVertex() {
		vertex = new float[12];
		if(type == TYPE_HORIZONTAL) {
			vertex[0] = xCenter- side / 2;
			vertex[1] = (float) (yCenter - side * Math.sin(Math.PI / 3));
			vertex[2] = xCenter - side;
			vertex[3] = yCenter;
			vertex[4] = xCenter - side / 2;
			vertex[5] = (float) (yCenter + side * Math.sin(Math.PI / 3));
			vertex[6] = xCenter + side / 2;
			vertex[7] = (float) (yCenter + side * Math.sin(Math.PI / 3));
			vertex[8] = xCenter + side;
			vertex[9] = yCenter;
			vertex[10] = xCenter + side / 2;
			vertex[11] = (float) (yCenter - side * Math.sin(Math.PI / 3));
		} else if(type == TYPE_VERTICAL) {
			vertex[0] = xCenter;
			vertex[1] = yCenter - side;
			vertex[2] = (float) (xCenter - side * Math.sin(Math.PI / 3));
			vertex[3] = yCenter - side / 2;
			vertex[4] = (float) (xCenter - side * Math.sin(Math.PI / 3));
			vertex[5] = yCenter + side / 2;
			vertex[6] = xCenter;
			vertex[7] = yCenter + side;
			vertex[8] = (float) (xCenter + side * Math.sin(Math.PI / 3));
			vertex[9] = yCenter + side / 2;
			vertex[10] = (float) (xCenter + side * Math.sin(Math.PI / 3));
			vertex[11] = yCenter - side / 2;
		}
	}
	
	public void drawSelf(Canvas canvas, Paint paint) {
		paint.setColor(0xff000000);
		canvas.drawPath(path, paint);
		paint.setStyle(Paint.Style.FILL);
		if(effect == EFFECT_PASS) {
			paint.setColor(COLOR_PASS);
		} else if(effect == EFFECT_HINDER) {
			paint.setColor(COLOR_HINDER);
		}
		canvas.drawPath(path, paint);
		paint.setColor(Color.BLACK);
		//////////////////////////////////������ʾ����ֵ////////////////////////////
		canvas.drawText("g:" + gValue , xCenter - 40, yCenter - 20, paint);
		canvas.drawText("h:" + hValue , xCenter - 40, yCenter, paint);
		canvas.drawText("f:" + fValue , xCenter - 40, yCenter + 20, paint);
	}
	
	public void addNeighbor(Hexagon neightbor) {
		neighborList.add(neightbor);
	}

	public LinkedList<Hexagon> getNeighborList() {
		return neighborList;
	}

	public float[] getPosition() {
		return new float[] {xCenter, yCenter};
	}
	
	public float computeGValue(Hexagon fatherHex) {
		float[] position = fatherHex.getPosition();
		float gValue_temp = (yCenter - position[1]) * (yCenter - position[1]) + (xCenter - position[0]) * (xCenter - position[0]);
		return gValue_temp;
	}
	
	public float computeHValue(Hexagon targetHex) {
		float[] position = targetHex.getPosition();
		float hValue_temp = (yCenter - position[1]) * (yCenter - position[1]) + (xCenter - position[0]) * (xCenter - position[0]);
		return hValue_temp;
	}

	public Hexagon getFatherHexagon() {
		return fatherHexagon;
	}

	public void setFatherHexagon(Hexagon fatherHexagon) {
		this.fatherHexagon = fatherHexagon;
	}

	/**
	 * �Ƿ����ͨ��
	 * @return
	 */
	public boolean canPass() {
		if(effect == EFFECT_PASS) {
			return true;
		} else {
			return false;
		}
	}

	public float getgValue() {
		return gValue;
	}

	public void setgValue(float gValue) {
		this.gValue = gValue;
		fValue = gValue + hValue;
	}

	public float gethValue() {
		return hValue;
	}

	public void sethValue(float hValue) {
		this.hValue = hValue;
		fValue = gValue + hValue;
	}

	public float getfValue() {
		return fValue;
	}

	public void setfValue(float fValue) {
		this.fValue = fValue;
	}

	public boolean hasFather() {
		if(fatherHexagon == null) {
			return false;
		} else {
			return true;
		}
	}

	public void reset() {//����Ѱ·����
		gValue = 0;
		hValue = 0;
		fValue = 0;
		fatherHexagon = null;
	}

}
