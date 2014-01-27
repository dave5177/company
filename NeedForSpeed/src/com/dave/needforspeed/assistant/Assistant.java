package com.dave.needforspeed.assistant;

import android.graphics.Rect;

/**
 * @author Dave
 * ������
 */
public class Assistant {
	
	/**
	 * ���Ƿ�����ĳ��������
	 * @param xPoint ��x
	 * @param yPoint ��y
	 * @param rect ����
	 * @return 
	 */
	public static boolean isPointInRect(float xPoint, float yPoint, Rect rect) {
		if(xPoint >= rect.left && xPoint <= rect.right && yPoint >= rect.top && yPoint <= rect.bottom) {
			return true;
		}
		return false;
	}
	
	/**
	 * ���Ƿ����ھ����������ĳ��������
	 * @param xPoint ��x
	 * @param yPoint ��y
	 * @param rectArray ��������
	 * @return �����Ǹ�������������±�ֵ
	 */
	public static int pointInRect(float xPoint, float yPoint, Rect[] rectArray) {
		for (int i = 0; i < rectArray.length; i++) {
			if(isPointInRect(xPoint, yPoint, rectArray[i]))
				return i;
		}
		return -1;
	}
}
