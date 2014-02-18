package com.dave.needforspeed.assistant;

import android.graphics.Rect;

/**
 * @author Dave
 * 辅助类
 */
public class Assistant {
	
	/**
	 * 车辆等级，0为未拥有
	 */
	public static int[] carLevel = {1, 0, 0, 0, 0, 0, 0, 0, 0};
	
	/**
	 * 点是否落在某个矩形里
	 * @param xPoint 点x
	 * @param yPoint 点y
	 * @param rect 矩形
	 * @return 
	 */
	public static boolean isPointInRect(float xPoint, float yPoint, Rect rect) {
		if(xPoint >= rect.left && xPoint <= rect.right && yPoint >= rect.top && yPoint <= rect.bottom) {
			return true;
		}
		return false;
	}
	
	/**
	 * 点是否落在矩形数组里的某个矩形里
	 * @param xPoint 点x
	 * @param yPoint 点y
	 * @param rectArray 矩形数组
	 * @return 落在那个矩形数组里的下标值
	 */
	public static int pointInRect(float xPoint, float yPoint, Rect[] rectArray) {
		for (int i = 0; i < rectArray.length; i++) {
			if(isPointInRect(xPoint, yPoint, rectArray[i]))
				return i;
		}
		return -1;
	}
	
}
