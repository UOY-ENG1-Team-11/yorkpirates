package com.engteam14.yorkpirates;

import java.util.ArrayList;
import java.util.PriorityQueue;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Boat extends GameObject {
	
	private static final float SPEED =70f;

	public Boat(Array<Texture> frames, float fps, float x, float y, float width, float height, String team) {
		super(frames, fps, x, y, width, height, team);
		// TODO Auto-generated constructor stub
	}
	
	public void pathTo(TiledMap map, float x, float y) {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Terrain");
		Node[][] nodeMap = new Node[layer.getWidth()][layer.getHeight()];
		float cx = this.x + width/2;
		float cy = this.y + height/2;
		System.out.println(layer.getWidth() + ", " + layer.getHeight());
		for(int mx = 0; mx < layer.getWidth(); mx++) {
			for(int my = 0; my < layer.getHeight(); my++) {
				if(layer.getCell(mx, my) == null) {
					float h = (float) Math.sqrt(Math.pow(x/16 - mx, 2) + Math.pow(y/16 - my, 2));
					Node node = new Node(new Vector2(mx,my),  h);
					nodeMap[mx][my] = node;
				}
			}
		}
		for(int mx = 0; mx < layer.getWidth(); mx++) {
			for(int my = 0; my < layer.getHeight(); my++) {
				if(nodeMap[mx][my] != null) {
					boolean left = mx - 1 > 0 && nodeMap[mx-1][my] != null;
					boolean right = mx + 1 < layer.getWidth() && nodeMap[mx+1][my] != null;
					boolean up = my + 1 < layer.getHeight() && nodeMap[mx][my+1] != null;
					boolean down = my - 1 > 0 && nodeMap[mx][my-1] != null;
					if(left) {
						nodeMap[mx][my].addNeighbor(1, nodeMap[mx-1][my]);
						nodeMap[mx-1][my].addNeighbor(1, nodeMap[mx][my]);
						if(up && nodeMap[mx-1][my+1] != null) {
							nodeMap[mx][my].addNeighbor(1.414f, nodeMap[mx-1][my+1]);
							nodeMap[mx-1][my+1].addNeighbor(1.414f, nodeMap[mx][my]);
						}
						if(down && nodeMap[mx-1][my-1] != null) {
							nodeMap[mx][my].addNeighbor(1.414f, nodeMap[mx-1][my-1]);
							nodeMap[mx-1][my-1].addNeighbor(1.414f, nodeMap[mx][my]);
						}
					}
					if(right) {
						nodeMap[mx][my].addNeighbor(1, nodeMap[mx+1][my]);
						nodeMap[mx+1][my].addNeighbor(1, nodeMap[mx][my]);
						if(up && nodeMap[mx+1][my+1] != null) {
							nodeMap[mx][my].addNeighbor(1.414f, nodeMap[mx+1][my+1]);
							nodeMap[mx+1][my+1].addNeighbor(1.414f, nodeMap[mx][my]);
						}
						if(down && nodeMap[mx+1][my-1] != null) {
							nodeMap[mx][my].addNeighbor(1.414f, nodeMap[mx+1][my-1]);
							nodeMap[mx+1][my-1].addNeighbor(1.414f, nodeMap[mx][my]);
						}
					}
					if(up) {
						nodeMap[mx][my].addNeighbor(1, nodeMap[mx][my+1]);
						nodeMap[mx][my+1].addNeighbor(1, nodeMap[mx][my]);
					}
					if(down) {
						nodeMap[mx][my].addNeighbor(1, nodeMap[mx][my-1]);
						nodeMap[mx][my-1].addNeighbor(1, nodeMap[mx][my]);
					}
				}
			}
		}
		Node start = nodeMap[(int) Math.floor(cx/16)][(int) Math.floor(cy/16)];
		Node target = nodeMap[(int) Math.floor(x/16)][(int) Math.floor(y/16)];
		PriorityQueue<Node> closedList = new PriorityQueue<Node>();
		PriorityQueue<Node> openList = new PriorityQueue<Node>();
		if (start != null) {
			start.g = 0;
			openList.add(start);
			while(!openList.isEmpty()) {
				Node n = openList.peek();
				if(n == target) {
					break;
				}	
				for(Node.Edge edge : n.neighbors) {
					float totalWeight = n.g + edge.weight;
					if(!openList.contains(edge.node) && !closedList.contains(edge.node)) {
						edge.node.parent = n;
						edge.node.g = totalWeight;
						edge.node.f = edge.node.g + edge.node.h;
						openList.add(edge.node);
					} else if(totalWeight < edge.node.g) {
						edge.node.parent = n;
						edge.node.g = totalWeight;
						edge.node.f = edge.node.g + edge.node.h;
						if(closedList.contains(edge.node)) {
							closedList.remove(edge.node);
							openList.add(edge.node);
						}
					}
				}
				openList.remove(n);
				closedList.add(n);
			}
			Node nextNode = target.parent;
			if(nextNode != null) {
				//
				int horizontal = (int) Math.signum(Math.floor(nextNode.pos.x + 0.5 - cx/16));
				int vertical = (int) Math.signum(Math.floor(nextNode.pos.y + 0.5 - cy/16));
				move(SPEED * horizontal, SPEED * vertical);
				updateHitboxPos();
			}
		}
	}
	
	private class Node implements Comparable<Node> {
		public Vector2 pos;

		public Node parent = null;
		public ArrayList<Edge> neighbors;
		
		public float f = Float.MAX_VALUE;
		public float g = Float.MAX_VALUE;
		public float h;
		
		Node(Vector2 pos, float h) {
			this.pos = pos;
			this.h = h;
			this.neighbors = new ArrayList<Edge>();
		}
		
		@Override
		public int compareTo(Node n) {
			return Float.compare(this.f, n.f);
		}
		
		public void addNeighbor(float weight, Node node) {
			neighbors.add(new Edge(weight, node));
		}
		
		public class Edge {
			Edge(float weight, Node node) {
				this.weight = weight;
                this.node = node;
			}
			
			public float weight;
			public Node node;
		}
	}

}
