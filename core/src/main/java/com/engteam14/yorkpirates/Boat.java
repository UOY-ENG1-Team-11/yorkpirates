package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
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

	/**
     * Generates an enemy boat within the game with animated frame(s) and a hit-box.
     * @param game			The main class.
     * @param frames    	The animation frames, or a single sprite.
     * @param fps       	The number of frames to be displayed per second.
     * @param x         	The x coordinate within the map to initialise the boat at.
     * @param y         	The y coordinate within the map to initialise the boat at.
     * @param width     	The size of the boat in the x-axis.
     * @param height    	The size of the boat in the y-axis.
     * @param team      	The team the player is on.
     * @param patrol		An array of Vector positions for the boat to move between.
     * @param collegeName	The name of the college this boat belongs to.
     */
	public Boat(YorkPirates game, Array<Texture> frames, float fps, float x, float y, float width, float height, String team, Vector2[] patrol, String collegeName) {
		super(frames, fps, x, y, width, height, team);
		this.patrol = patrol;
		this.collegeName = collegeName;
		setMaxHealth(100);
		Array<Texture> sprites = new Array<>();
        sprites.add(game.textureHandler.getTexture("allyHealthBar"));
        boatHealth = new HealthBar(this,sprites);
	}
	
	/**
     * Generates an enemy boat within the game with animated frame(s) and a hit-box using properties from a JSON.
     * @param game			The main class.
     * @param frames    	The animation frames, or a single sprite.
     * @param fps       	The number of frames to be displayed per second.
     * @param json			The JsonValue to load the boat's properties from
     * @param collegeName	The name of the college this boat belongs to.
     */
	public Boat(YorkPirates game, Array<Texture> frames, float fps, JsonValue json, String collegeName) {
		super(frames, fps, json);
		this.collegeName = collegeName;
		fromJson(json);
		Array<Texture> sprites = new Array<>();
        sprites.add(game.textureHandler.getTexture("allyHealthBar"));
        boatHealth = new HealthBar(this,sprites);
	}
	
	/**
     * Called once per frame. Used to movement perform calculations.
     * @param screen	The main game screen.
     * @param x			The player's x-coordinate.
     * @param y			The player's y-coordinate.
     */
	public void update(GameScreen screen, float x, float y){
        Vector2 oldPos = new Vector2(this.x,this.y); // Stored for next-frame calculations

        // Get input movement
        int horizontal = 0;
        int vertical = 0;
        int shootFrequency = 700;
        if(Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) <= DETECT_RANGE && this.team != GameScreen.playerTeam) {
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
	        horizontal = (int) Math.signum(Math.floor(patrol[patrolIndex].x - this.x));
	        vertical = (int) Math.signum(Math.floor(patrol[patrolIndex].y - this.y));
        }

        // Calculate collision && movement
        if (horizontal != 0 || vertical != 0){
            move(SPEED *horizontal, SPEED *vertical);
            rotation = (float) Math.toDegrees(Math.atan2(vertical, horizontal));
            if (!safeMove(screen.getMain().edges)) {    // Collision
                Vector2 newPos = new Vector2(x, y);
                this.x = oldPos.x;
                if (!safeMove(screen.getMain().edges)) {
                    this.x = newPos.x;
                    this.y = oldPos.y;
                    if (!safeMove(screen.getMain().edges)) {
                        this.x = oldPos.x;
                    }
                }
            }
        }
        updateHitboxPos();
    }
	
	/**
	 * Called when a projectile hits the boat.
	 * @param screen			The main game screen.
	 * @param damage			The damage dealt by the projectile
	 * @param projectileTeam	The team of the projectile
	 */
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
            int lootGained = 4;
            screen.loot.Add(lootGained);
            boatHealth = null;
            destroy(screen);
        }
	}
	
	/**
     * Moves the boat within the x and y-axis of the game world.
     * @param x     The amount to move the boat within the x-axis.
     * @param y     The amount to move the boat within the y-axis.
     */
	@Override
    public void move(float x, float y){
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
        boatHealth.move(this.x, this.y + height/2 + 2f); // Healthbar moves with boat
    }
	
	/**
	 * Called when the boat needs to be destroyed.
	 * @param screen	The main game screen. 
	 */
	private void destroy(GameScreen screen) {
		screen.getCollege(collegeName).boats.removeValue(this, true);
	}
	
	/**
     *  Calculate if the current boat position is safe to be in.
     * @param edges A 2d array containing safe/unsafe positions to be in.
     * @return      If the current position is safe.
     */
	private Boolean safeMove(Array<Array<Boolean>> edges){
        return (
                        edges.get((int)((y+height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y+height/2)/16)).get((int)((x-width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x+width/2)/16)) &&
                        edges.get((int)((y-height/2)/16)).get((int)((x-width/2)/16))
        );
    }
	
	/**
	 * Called when drawing the boat.
	 * @param batch			The batch to draw the boat with.
	 * @param elapsedTime	The current time the game has been running for. 
	 */
	@Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // Generates the sprite
        Texture frame = anim.getKeyFrame((currentHealth/maxHealth > 0.66f) ? 0 : ((currentHealth/maxHealth > 0.33f) ? 2 : 1), true);
        // Draws sprite and health-bar
        batch.draw(frame, x - width/2, y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
        batch.setShader(null);
        if(!(boatHealth == null)) boatHealth.draw(batch, 0);
    }
	
	/** 
     * Saves all the boat's properties in JSON format.
     * @return	A JsonValue containing all the boat's properties.
     */
	@Override
	public JsonValue toJson() {
		JsonValue json = super.toJson();
		json.addChild("rotation", new JsonValue(rotation));
		JsonValue jPatrol = new JsonValue(JsonValue.ValueType.object);
		for(int i = 0; i < patrol.length; i++) {
			JsonValue point = new JsonValue(JsonValue.ValueType.object);
			point.addChild("x", new JsonValue(patrol[i].x));
			point.addChild("y", new JsonValue(patrol[i].y));
			jPatrol.addChild(i + "", point);
		}
		json.addChild("patrol", jPatrol);
		json.addChild("patrolIndex", new JsonValue(patrolIndex));
		return json;
	}
	
	/** 
     * Sets all properties to those contained in the passed JsonValue.
     * @param json	The root JsonValue containing the boat properties.
     */
	@Override
	public void fromJson(JsonValue json) {
		super.fromJson(json);
		rotation = json.getFloat("rotation");
		JsonValue jPatrol = json.get("patrol");
		patrol = new Vector2[jPatrol.size];
		for(int i = 0; i < jPatrol.size; i++) {
			patrol[i] = new Vector2(jPatrol.get(i + "").getFloat("x"), jPatrol.get(i + "").getFloat("y"));
		}
		patrolIndex = json.getInt("patrolIndex");
	}
	
	/*
	 * A* BROKEN
	 * 
	 * public void pathTo(TiledMap map, float x, float y) {
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
