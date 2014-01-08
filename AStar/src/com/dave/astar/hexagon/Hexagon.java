package com.dave.astar.hexagon;

import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;


/**
 * @author Dave
 * 地形六边形
 */
public class Hexagon {
	
	/**
	 * 中心点坐标
	 */
	private float xCenter, yCenter;
	
	/**
	 * 边长
	 */
	private float side;
	
	/**
	 * 摆放方式
	 */
	private int type;
	
	/**
	 * 作用
	 */
	private int effect;
	
	/**
	 * 顶点
	 */
	private float[] vertex;
	
	/**
	 * 相邻的六边形
	 */
	private LinkedList<Hexagon> neighborList;
	
	/**
	 * 父节点
	 */
	private Hexagon fatherHexagon;

	private Path path;
	
	/**
	 * 寻路g估值（从上一节点到本节点的估值）
	 */
	private float gValue;
	

	/**
	 * 寻路h估值（从本节点到目标节点的估值）
	 */
	private float hValue;
	
	/**
	 * 寻路f估值（最终的估值是h估值和g估值的和）
	 */
	private float fValue;
	
	/**
	 * 垂直摆放
	 */
	public static final int TYPE_VERTICAL = 1;
	
	/**
	 * 水平摆放
	 */
	public static final int TYPE_HORIZONTAL = 2;
	
	/**
	 * 作为平地无障碍物完全通过
	 */
	public static final int EFFECT_PASS = 0x00001;
	
	/**
	 * 作为障碍物无法通过
	 */
	public static final int EFFECT_HINDER = 0x00002;
	
	public static final int COLOR_PASS = 0xffffffff;
	public static final int COLOR_HINDER = 0xff909090;

	/**
	 * @param xCenter 中心点x坐标
	 * @param yCenter 中心点y坐标
	 * @param side 边长
	 * @param type 类型（垂直或者水平）
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
	 * 路径初始化
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
		//////////////////////////////////调试显示各个值////////////////////////////
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
	 * 是否可以通过
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

	public void reset() {//重置寻路数据
		gValue = 0;
		hValue = 0;
		fValue = 0;
		fatherHexagon = null;
	}

}
