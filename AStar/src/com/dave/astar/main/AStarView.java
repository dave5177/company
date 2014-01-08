package com.dave.astar.main;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.dave.astar.entity.LiXiaoYao;
import com.dave.astar.hexagon.Hexagon;

public class AStarView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder surfaceHolder;
	private Paint paint;
	private int screen_width;
	private int screen_height;
	private LiXiaoYao liXiaoYao;// ��������ң
	private ArrayList<Hexagon> openList;//A*�㷨�����б�
	private ArrayList<Hexagon> closeList;//A*�㷨�ر��б�
	public ArrayList<Hexagon> route;//·��
	private Hexagon targetHexagon;//Ŀ��������
	private Hexagon thisHexagon;//��ǰ��������������
	private Hexagon[][] mapHexagons;//��ͼ����������
	private final static int[][] map = {//��ͼ����
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, },
			{ 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 0, },
			{ 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, },
			{ 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, },
			{ 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 0, },
			{ 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, },
			{ 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, },
			{ 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, },
			{ 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, },
			{ 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, }, };

	private final static float startX = -10;//�����ε�ͼ��ʼx����
	private final static float startY = 10;//�����ε�ͼ��ʼy����
	private final static float SIDE_LENGTH = 50;//�����α߳�

	/**
	 * ÿ����Ƭ��
	 */
	private int patchesPerCol;

	/**
	 * ÿ����Ƭ��
	 */
	private int patchesPerRow;

	public AStarView(Context context) {
		super(context);
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
		paint = new Paint();
		paint.setAntiAlias(true);

		patchesPerCol = map.length;
		patchesPerRow = map[0].length;
		mapHexagons = new Hexagon[map.length][map[0].length];
		route = new ArrayList<Hexagon>();
		openList = new ArrayList<Hexagon>();
		closeList = new ArrayList<Hexagon>();
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		screen_width = getWidth();
		screen_height = getHeight();
		System.out.println("widht----" + screen_width + "\nheight----"
				+ screen_height);
		initMap();
		liXiaoYao = new LiXiaoYao(138, 401, (Color.RED >> 1), this);
		new Thread(this).start();
	}

	private void initMap() {
		int outLength = mapHexagons.length;
		for (int i = 0; i < outLength; i++) {
			int inLength = mapHexagons[i].length;
			float lineStartX = 0;
			if (i % 2 == 0)
				lineStartX = startX;
			else
				lineStartX = (float) (startX + Math.sin(Math.PI / 3)
						* SIDE_LENGTH);
			float lineY = startY + i * SIDE_LENGTH * 3 / 2;
			for (int j = 0; j < inLength; j++) {
				Hexagon hexagon = null;
				if (map[i][j] == 0) {
					hexagon = new Hexagon((float) (lineStartX + j
							* Math.sin(Math.PI / 3) * SIDE_LENGTH * 2), lineY,
							SIDE_LENGTH, Hexagon.TYPE_VERTICAL,
							Hexagon.EFFECT_PASS);
				} else {
					hexagon = new Hexagon((float) (lineStartX + j
							* Math.sin(Math.PI / 3) * SIDE_LENGTH * 2), lineY,
							SIDE_LENGTH, Hexagon.TYPE_VERTICAL,
							Hexagon.EFFECT_HINDER);
				}
				if (j > 0) {
					mapHexagons[i][j - 1].addNeighbor(hexagon);
					hexagon.addNeighbor(mapHexagons[i][j - 1]);
				}
				if (i % 2 == 0) {
					if (i > 0) {
						if (j > 0) {
							mapHexagons[i - 1][j - 1].addNeighbor(hexagon);
							hexagon.addNeighbor(mapHexagons[i - 1][j - 1]);
						}
						mapHexagons[i - 1][j].addNeighbor(hexagon);
						hexagon.addNeighbor(mapHexagons[i - 1][j]);
					}
				} else {
					mapHexagons[i - 1][j].addNeighbor(hexagon);
					hexagon.addNeighbor(mapHexagons[i - 1][j]);
					if (j < patchesPerRow - 1) {
						mapHexagons[i - 1][j + 1].addNeighbor(hexagon);
						hexagon.addNeighbor(mapHexagons[i - 1][j + 1]);
					}
				}
				mapHexagons[i][j] = hexagon;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	}

	@Override
	public void run() {
		while (true) {
			liXiaoYao.logic();
			mainDraw();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void mainDraw() {
		Canvas canvas = surfaceHolder.lockCanvas();
		canvas.drawColor(Color.GREEN);
		for (int i = 0; i < patchesPerCol; i++) {
			for (int j = 0; j < patchesPerRow; j++) {
				mapHexagons[i][j].drawSelf(canvas, paint);
			}
		}
		liXiaoYao.drawSelf(canvas, paint);
		canvas.save();
		// canvas.restore();
		surfaceHolder.unlockCanvasAndPost(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int[] target = getPositionPatch(x, y);
			targetHexagon = mapHexagons[target[0]][target[1]];
			if(targetHexagon.canPass()) {//��ĵ��ǿ��Ե����
				float[] position = liXiaoYao.getPosition();
				int[] patch = getPositionPatch(position[0], position[1]);
				thisHexagon = mapHexagons[patch[0]][patch[1]];
				thisHexagon.setFatherHexagon(null);//����ǰ��ĸ��ڵ�����Ϊ��
				liXiaoYao.changeTarget(targetHexagon);
				
				searchRoute();
			}
			System.out.println("������꣺" + x + "," + y);
			System.out.println("�����Σ�" + target[0] + "," + target[1]);
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("move");
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("̧�����꣺" + x + "," + y);
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * �õ������������Σ�������Ե����ı����жϣ���Ҫ����ȷ�Ŀ����Լ��޸ģ�
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] getPositionPatch(float x, float y) {
		float sin60 = (float) Math.sin(Math.PI / 3);
		int[] result = new int[] { -1, -1 };
		int col = 0;
		int row = (int) ((y - (mapHexagons[0][0].getPosition()[1] - SIDE_LENGTH + SIDE_LENGTH / 4)) * 2 / (3 * SIDE_LENGTH));
		
		if (row > map.length - 1)
			return result;
		
		if (row % 2 == 0) {
			col = (int) ((x - (startX - SIDE_LENGTH * sin60)) / (2 * sin60 * SIDE_LENGTH));
		} else {
			col = (int) ((x - startX) / (2 * sin60 * SIDE_LENGTH));
		}
		if (col > map[0].length - 1)
			return result;
		result[0] = row;
		result[1] = col;
		
		return result;
	}

	/**
	 * ĳ���ڵ��Ƿ��ڿ����б��ÿ�α������˷Ѽ������ڣ������Ż���
	 * @param hexagon
	 * @return
	 */
	public boolean isAtOpenlList(Hexagon hexagon) {
		for (Hexagon openHex : openList) {
			if(openHex == hexagon) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ĳ���ڵ��Ƿ��ڹر��б��ͬ���������˷ѣ�
	 * @param hexagon
	 * @return
	 */
	public boolean isAtCloseList(Hexagon hexagon) {
		for (Hexagon closeHex : closeList) {
			if(closeHex == hexagon) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * ���һ���ڵ㵽�����б��ڵ��f��ֵ��͵���ǰ�棬���뷨����
	 * @param hexagon
	 */
	public void addToOpenList(Hexagon hexagon) {
		int length = openList.size();
		boolean added = false;
		for (int i = 0; i < length; i++) {
			Hexagon openHex = openList.get(i);
			if(hexagon.getfValue() <= openHex.getfValue()) {
				openList.add(i, hexagon);
				System.out.println("��ӵ������б�λ��---" + i);
				added = true;
				break;
			}
		}
		if(!added) {//ѭ������û�б������f��ֵ�ڵ㣬���뵽���
			openList.add(hexagon);
			System.out.println("��ӵ������б�ĩβ");
		}
	}
	
	/**
	 * Ѱ·
	 */
	public void searchRoute() {
		Hexagon nowHexagon = thisHexagon;
		nowHexagon.reset();
		addToOpenList(nowHexagon);
		boolean finded = false;
		while(!finded) {
			openList.remove(nowHexagon);//����ǰ�ڵ��openList���Ƴ�
			closeList.add(nowHexagon);//����ǰ�ڵ���ӵ��ر��б���
			LinkedList<Hexagon> neighbors = nowHexagon.getNeighborList();//��ȡ��ǰ�����ε�����������
			System.out.println("��ǰ���ڽڵ���----" + neighbors.size());
			for(Hexagon neighbor : neighbors) {
				if(neighbor == targetHexagon) {//�ҵ�Ŀ��ڵ�
					System.out.println("�ҵ�Ŀ���");
					finded = true;
					neighbor.setFatherHexagon(nowHexagon);
				}
				if(isAtCloseList(neighbor) || !neighbor.canPass()) {//�ڹر��б���
					System.out.println("�޷�ͨ���������ڹر��б�");
					continue;
				}
				
				if(isAtOpenlList(neighbor)) {//�ýڵ��Ѿ��ڿ����б���
					System.out.println("���ڿ����б��ж��Ƿ���ĸ��ڵ�");
					float assueGValue = neighbor.computeGValue(nowHexagon) + nowHexagon.getgValue();//�������ӵ�ǰ�ڵ���룬�ýڵ��g��ֵ
					if(assueGValue < neighbor.getgValue()) {//�����g��ֵС����ԭ����g��ֵ
						openList.remove(neighbor);//��������ýڵ���openList��λ��
						neighbor.setgValue(assueGValue);//��������g��ֵ
						addToOpenList(neighbor);//��������openList��
					}
				} else {//û���ڿ����б���
					System.out.println("���ڿ����б����");
					neighbor.sethValue(neighbor.computeHValue(targetHexagon));//���������h��ֵ
					neighbor.setgValue(neighbor.computeGValue(nowHexagon) + nowHexagon.getgValue());//����ýڵ��g��ֵ������ǰ�ڵ��g��ֵ���ϵ�ǰ�ڵ��g��ֵ��
					addToOpenList(neighbor);//��ӵ������б���
					neighbor.setFatherHexagon(nowHexagon);//����ǰ�ڵ�����Ϊ�ýڵ�ĸ��ڵ�
				}
			}
			
			if(openList.size() <= 0) {
				System.out.println("�޷������Ŀ��");
				break;
			} else {
				nowHexagon = openList.get(0);//�õ�f��ֵ��͵Ľڵ�����Ϊ��ǰ�ڵ�
			}
		}
		openList.removeAll(openList);
		closeList.removeAll(closeList);
		
		if(finded) {//�ҵ���·�ߴ���·�߼���
			route.removeAll(route);
			Hexagon hex = targetHexagon;
			while (hex != thisHexagon) {
				route.add(hex);//���ڵ���ӵ�·���б���
				
				Hexagon fatherHex = hex.getFatherHexagon();//��Ŀ��ڵ㿪ʼ��Ѱ���ڵ������Ҫ��·��
				hex = fatherHex;
			}
			route.add(hex);
			
			
			liXiaoYao.setState(LiXiaoYao.STATE_MOVING);
			liXiaoYao.setStepIndex(route.size() - 1);
		}
//		resetMap();
	}

	/**
	 * ����map���������еĵ�ͼ�ڵ㣬�����Ż���
	 * Ѱ·��ʱ����Ҫ����
	 */
	private void resetMap() {
		int length = mapHexagons.length;
		for (int i = 0; i < length; i++) {
			int inSize = mapHexagons[i].length;
			for (int j = 0; j < inSize; j++) {
				mapHexagons[i][j].reset();
			}
		}
	}
	
}
