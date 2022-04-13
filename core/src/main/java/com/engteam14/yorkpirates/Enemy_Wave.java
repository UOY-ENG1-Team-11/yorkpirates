package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import java.lang.Math;

public class Enemy_Wave extends GameObject {
	
	//Movement and coordinate values
	public static final int MAX_WAVES = 5; //maximum number of wave instances
	private static final float projectileDamage = 20f; //damage dealt by the wave
	
	private final float speed  = 80f;
	private final float maxDistance = 8000; //how far the wave can move before disappearing
	private final float xComponent; //what value to add to x coordinate every frame
	private final float yComponent; //what value to add to y coordinate every frame
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
		super(frames, fps, x, y, 5f, 5f, "NEUTRAL");
		
		// Movement calculations
        float changeInX = target.x - x;
        float changeInY = target.y - y;
        float scaleFactor = max(abs(changeInX),abs(changeInY));
        xComponent = changeInX / scaleFactor;
        yComponent = changeInY / scaleFactor;
        this.target = target;
	}
	
	/**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     * @param screen    The main game screen.
     */
	public void update(GameScreen screen) {
		if (state == "move"){
	        float xMove = speed*xComponent;
	        float yMove = speed*yComponent;
	        distanceTravelled += speed;
	        move(xMove, yMove);
	        if (overlaps(target.hitBox)){
	            target.takeDamage(screen,projectileDamage,team);
	            destroy(screen);
	        }
		}
        // Destroys after max travel distance
        if(distanceTravelled > maxDistance) {destroy(screen);}
	}
	/**
     * Called when the wave needs to be destroyed.
     * @param screen    The main game screen.
     */
    private void destroy(GameScreen screen){
    	count -= 1;
        screen.enemy_waves.removeValue(this,true);
    }
}