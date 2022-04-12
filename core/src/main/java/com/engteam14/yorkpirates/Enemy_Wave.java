package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import java.lang.Math;

public class Enemy_Wave extends GameObject {
	
	//Movement and coordinate values
	public static final int MAX_WAVES = 5; //maximum number of wave instances
	private final float speed  = 5;
	private int lifespan = 300; //how many frames wave will exist for
	private final float xComponent; //what value to add to x coordinate every frame
	private final float yComponent; //what value to add to y coordinate every frame
	private String state = "move";
	private float elapsed_time = 0;
	
	
	/**
     * Generates a generic object within the game with animated frame(s) and a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param width     The size of the object in the x-axis.
     * @param height    The size of the object in the y-axis.
     */
	public Enemy_Wave(YorkPirates game, Array<Texture> frames, float fps, float x, float y, float width, float height) {
		super(frames, fps, x, y, width, height, "NEUTRAL");
		float dir = 0; //Calculate angle between start pos and target pos here and set it
		//Angles representing compass directions are 360 or 0/90/180/270 for NESW
		
		//precalculate the proportion of speed to assign to the x and y components based
		//on the angle
		xComponent = (float)Math.sin(dir)*speed;
		yComponent = (float)Math.cos(dir)*speed;
	}
	
	public void update() {
		if (state == "move"){
			x += xComponent;
			y += yComponent;
			//check collision at new position
			//if colliding with player, do damage, set state to splash
			state = "splash";
			//else
			lifespan -= 1;
			//if lifespan <= 0, hide this instance
		}
		if (state == "splash") {
			//play splash animation and hide instance when finished
			hide();
		}
		if (state == "decay") {
			//play decay animation and hide instance when finished
			hide();
		}
		draw();
	}
	
	/**
	 * Hide this wave instance outside the map.
	 * Hiding instead of destroying instance as number of waves is fixed and it's
	 * slightly more performant to just store them outside the map rather than
	 * creating/destroying them repeatedly. Performance boost is more noticeable if
	 * we regularly reach the MAX_WAVES cap, spawning in new waves as soon as the old
	 * ones have finished.
	 */
	public void hide() {
		state = "hidden";
	}
}