package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.lang.Math;

public class Enemy_Wave extends GameObject {
	
	//Movement and coordinate values
	public static final int MAX_WAVES = 5; //maximum number of wave instances
	private static final float projectileDamage = 20f; //damage dealt by the wave
	
	private final float speed  = 60f;
	private final float maxDistance = 8000; //how far the wave can move before disappearing
	private final float xComponent; //what value to add to x coordinate every frame
	private final float yComponent; //what value to add to y coordinate every frame
	private final float rotation;
	private GameObject target; //reference to target game object
	
	private String state = "move"; //state the wave is in
	private float distanceTravelled = 0; //current distance travelled
	
	public static int count = 0; //how many wave instances exist
	
	
	
	/**
     * Generates a generic object within the game with animated frame(s) and a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param width     The size of the object in the x-axis.
     * @param height    The size of the object in the y-axis.
     */
	public Enemy_Wave(Array<Texture> frames, float fps, GameObject target, float x, float y) {
		super(frames, fps, x, y, 25f, 25f, "NEUTRAL");
		
		// Movement calculations
        float changeInX = target.x - x;
        float changeInY = target.y - y;
        float scaleFactor = max(abs(changeInX),abs(changeInY));
        xComponent = changeInX / scaleFactor;
        yComponent = changeInY / scaleFactor;
        rotation = (float) Math.toDegrees(Math.atan2(yComponent, xComponent));
        this.target = target;
	}
	
	public Enemy_Wave(Array<Texture> frames, float fps, JsonValue json) {
		super(frames, fps, json);
		xComponent = json.getFloat("xComponent");
		yComponent = json.getFloat("yComponent");
		rotation = json.getFloat("rotation");
		target = new GameObject(null, 0, json.get("target"));
		distanceTravelled = json.getFloat("distanceTravelled");
	}
	
	/**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     * @param screen    The main game screen.
     */
	public void update(GameScreen screen, boolean badWeather) {
		if (state == "move"){
	        float xMove = speed*xComponent*(badWeather ? 1.3f : 1);
	        float yMove = speed*yComponent*(badWeather ? 1.3f : 1);
	        distanceTravelled += speed*(badWeather ? 1.3f : 1);
	        move(xMove, yMove);
	        if (overlaps(target.hitBox)){
	            target.takeDamage(screen,projectileDamage,team);
	            destroy(screen);
	        }
		}
        // Destroys after max travel distance
        if(distanceTravelled > maxDistance) {destroy(screen);}
	}
	
	@Override
	public void draw(SpriteBatch batch, float elapsedTime) {
		Texture frame = anim.getKeyFrame(elapsedTime, true);
		batch.draw(frame, x-width/2, y-height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
	}
	/**
     * Called when the wave needs to be destroyed.
     * @param screen    The main game screen.
     */
    private void destroy(GameScreen screen){
    	count -= 1;
        screen.weather.waves.removeValue(this,true);
    }
    
    @Override
    public JsonValue toJson() {
    	JsonValue json = super.toJson();
    	json.addChild("xComponent", new JsonValue(xComponent));
    	json.addChild("yComponent", new JsonValue(yComponent));
    	json.addChild("rotation", new JsonValue(rotation));
    	json.addChild("target", target.toJson());
    	json.addChild("distanceTravelled", new JsonValue(distanceTravelled));
    	return json;
    }
}