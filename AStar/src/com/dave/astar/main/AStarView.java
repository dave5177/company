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
	private LiXiaoYao liXiaoYao;// 主角李逍遥
	private ArrayList<Hexagon> openList;//A*算法开启列表
	private ArrayList<Hexagon> closeList;//A*算法关闭列表
	public ArrayList<Hexagon> route;//路线
	private Hexagon targetHexagon;//目标六边形
	private Hexagon thisHexagon;//当前主角所处六边形
	private Hexagon[][] mapHexagons;//地图六边形数组
	private final static int[][] map = {//地图数组
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

	private final static float startX = -10;//六边形地图起始x坐标
	private final static float startY = 10;//六边形地图起始y坐标
	private final static float SIDE_LENGTH = 50;//六边形边长

	/**
	 * 每列面片数
	 */
	private int patchesPerCol;

	/**
	 * 每行面片数
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
			if(targetHexagon.canPass()) {//点的点是可以到达的
				float[] position = liXiaoYao.getPosition();
				int[] patch = getPositionPatch(position[0], position[1]);
				thisHexagon = mapHexagons[patch[0]][patch[1]];
				thisHexagon.setFatherHexagon(null);//将当前点的父节点设置为空
				liXiaoYao.changeTarget(targetHexagon);
				
				searchRoute();
			}
			System.out.println("点击坐标：" + x + "," + y);
			System.out.println("六边形：" + target[0] + "," + target[1]);
			break;
		case MotionEvent.ACTION_MOVE:
			System.out.println("move");
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("抬起坐标：" + x + "," + y);
			break;

		default:
			break;
		}
		return true;
	}

	/**
	 * 得到点所处六边形（这里初略的用四边形判断，需要更精确的可以自己修改）
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
	 * 某个节点是否在开启列表里（每次遍历很浪费计算周期，可以优化）
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
	 * 某个节点是否在关闭列表里（同样遍历很浪费）
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
	 * 添加一个节点到开启列表（节点的f估值最低的排前面，插入法排序）
	 * @param hexagon
	 */
	public void addToOpenList(Hexagon hexagon) {
		int length = openList.size();
		boolean added = false;
		for (int i = 0; i < length; i++) {
			Hexagon openHex = openList.get(i);
			if(hexagon.getfValue() <= openHex.getfValue()) {
				openList.add(i, hexagon);
				System.out.println("添加到开启列表位置---" + i);
				added = true;
				break;
			}
		}
		if(!added) {//循环结束没有比他大的f估值节点，插入到最后
			openList.add(hexagon);
			System.out.println("添加到开启列表末尾");
		}
	}
	
	/**
	 * 寻路
	 */
	public void searchRoute() {
		Hexagon nowHexagon = thisHexagon;
		nowHexagon.reset();
		addToOpenList(nowHexagon);
		boolean finded = false;
		while(!finded) {
			openList.remove(nowHexagon);//将当前节点从openList中移除
			closeList.add(nowHexagon);//将当前节点添加到关闭列表中
			LinkedList<Hexagon> neighbors = nowHexagon.getNeighborList();//获取当前六边形的相邻六边形
			System.out.println("当前相邻节点数----" + neighbors.size());
			for(Hexagon neighbor : neighbors) {
				if(neighbor == targetHexagon) {//找到目标节点
					System.out.println("找到目标点");
					finded = true;
					neighbor.setFatherHexagon(nowHexagon);
				}
				if(isAtCloseList(neighbor) || !neighbor.canPass()) {//在关闭列表里
					System.out.println("无法通过或者已在关闭列表");
					continue;
				}
				
				if(isAtOpenlList(neighbor)) {//该节点已经在开启列表里
					System.out.println("已在开启列表，判断是否更改父节点");
					float assueGValue = neighbor.computeGValue(nowHexagon) + nowHexagon.getgValue();//计算假设从当前节点进入，该节点的g估值
					if(assueGValue < neighbor.getgValue()) {//假设的g估值小于于原来的g估值
						openList.remove(neighbor);//重新排序该节点在openList的位置
						neighbor.setgValue(assueGValue);//从新设置g估值
						addToOpenList(neighbor);//从新排序openList。
					}
				} else {//没有在开启列表里
					System.out.println("不在开启列表，添加");
					neighbor.sethValue(neighbor.computeHValue(targetHexagon));//计算好他的h估值
					neighbor.setgValue(neighbor.computeGValue(nowHexagon) + nowHexagon.getgValue());//计算该节点的g估值（到当前节点的g估值加上当前节点的g估值）
					addToOpenList(neighbor);//添加到开启列表里
					neighbor.setFatherHexagon(nowHexagon);//将当前节点设置为该节点的父节点
				}
			}
			
			if(openList.size() <= 0) {
				System.out.println("无法到达该目标");
				break;
			} else {
				nowHexagon = openList.get(0);//得到f估值最低的节点设置为当前节点
			}
		}
		openList.removeAll(openList);
		closeList.removeAll(closeList);
		
		if(finded) {//找到后将路线存入路线集合
			route.removeAll(route);
			Hexagon hex = targetHexagon;
			while (hex != thisHexagon) {
				route.add(hex);//将节点添加到路径列表里
				
				Hexagon fatherHex = hex.getFatherHexagon();//从目标节点开始搜寻父节点就是所要的路线
				hex = fatherHex;
			}
			route.add(hex);
			
			
			liXiaoYao.setState(LiXiaoYao.STATE_MOVING);
			liXiaoYao.setStepIndex(route.size() - 1);
		}
//		resetMap();
	}

	/**
	 * 重置map（重置所有的地图节点，可以优化）
	 * 寻路的时候不需要调用
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
