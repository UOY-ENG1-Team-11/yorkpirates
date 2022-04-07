package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class Boat extends GameObject {
	
	private static final float SPEED =35f;
	private static final float DETECT_RANGE = 80f;
	private static final float STOP_RANGE = 40f;
	
	private float rotation;
	private HealthBar boatHealth;
	private Vector2[] patrol;
	private int patrolIndex = 0;
	private long lastShotFired = 0;
	public final String collegeName;

	public Boat(YorkPirates game, Array<Texture> frames, float fps, float x, float y, float width, float height, String team, Vector2[] patrol, String collegeName) {
		super(frames, fps, x, y, width, height, team);
		this.patrol = patrol;
		this.collegeName = collegeName;
		setMaxHealth(100);
		Array<Texture> sprites = new Array<>();
        sprites.add(game.textureHandler.getTexture("allyHealthBar"));
        boatHealth = new HealthBar(this,sprites);
	}
	
	public void update(GameScreen screen, float x, float y){
        Vector2 oldPos = new Vector2(x,y); // Stored for next-frame calculations

        // Get input movement
        int horizontal = 0;
        int vertical = 0;
        int shootFrequency = 700;
        if(Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) <= DETECT_RANGE) {
        	if(TimeUtils.timeSinceMillis(lastShotFired) > shootFrequency) {
        		lastShotFired = TimeUtils.millis();
	        	screen.sounds.cannon();
	            Array<Texture> sprites = new Array<>();
	            sprites.add(screen.getMain().textureHandler.getTexture("tempProjectile"));
	            screen.projectiles.add(new Projectile(sprites, 0, 64f, this, x, y, team));
        	}
        	if(Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) >= STOP_RANGE) {
		        horizontal = (int) Math.signum(Math.floor(x - this.x));
		        vertical = (int) Math.signum(Math.floor(y - this.y));
        	}
        } else {
	        if(patrol[patrolIndex].dst(this.x, this.y) < 8) {
	        	patrolIndex++;
	        	if(patrolIndex >= patrol.length) {
	        		patrolIndex = 0;
	        	}
	        }
	        horizontal = (int) Math.signum(patrol[patrolIndex].x - this.x);
	        vertical = (int) Math.signum(Math.floor(patrol[patrolIndex].y - this.y));
        }

        // Calculate collision && movement
        if (horizontal != 0 || vertical != 0){
            move(SPEED *horizontal, SPEED *vertical);
            rotation = (float) Math.toDegrees(Math.atan2(vertical, horizontal));
            if (!safeMove(screen.getMain().edges)) {    // Collision
                Vector2 newPos = new Vector2(x, y);
                x = oldPos.x;
                if (!safeMove(screen.getMain().edges)) {
                    x = newPos.x;
                    y = oldPos.y;
                    if (!safeMove(screen.getMain().edges)) {
                        x = oldPos.x;
                    }
                }
            }
        }
        updateHitboxPos();
    }
	
	@Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam){
        currentHealth -= damage;
        if(currentHealth > 0) {
        	boatHealth.resize(currentHealth);
        	screen.sounds.damage();
        } else {
        	screen.sounds.death();
            int pointsGained = 20;
            screen.points.Add(pointsGained);
            int lootGained = 5;
            screen.loot.Add(lootGained);
            boatHealth = null;
            destroy(screen);
        }
	}
	
	@Override
    public void move(float x, float y){
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
        boatHealth.move(this.x, this.y + height/2 + 2f); // Healthbar moves with boat
    }
	
	private void destroy(GameScreen screen) {
		screen.getCollege(collegeName).boats.removeValue(this, true);
	}
	
	private Boolean safeMove(Array<Array<Boolean>> edges){
        return (
                        edges.get((int)((y+height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y+height/2)/16)).get((int)((x-width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x-width/2)/16))
        );
    }
	
	@Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // Generates the sprite
        Texture frame = anim.getKeyFrame((currentHealth/maxHealth > 0.66f) ? 0 : ((currentHealth/maxHealth > 0.33f) ? 2 : 1), true);
        // Draws sprite and health-bar
        batch.draw(frame, x - width/2, y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
        batch.setShader(null);
        if(!(boatHealth == null)) boatHealth.draw(batch, 0);
    }
	
	/*public void pathTo(TiledMap map, float x, float y) {
		 * A* broken could fix later
		 * 
		 * TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("Terrain");
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
	
	 A* Node class
	 * 
	 * private class Node implements Comparable<Node> {
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
	}*/

}
