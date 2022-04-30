package com.engteam14.yorkpirates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class PowerUps extends GameObject{
	
	public String powerType;

	/**
     * Generates a power-up object within the game with a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     */
    public PowerUps(Array<Texture> sprites, float x, float y, float scale, String powerType) {
        super(sprites, 0, x, y, sprites.get(0).getWidth()*scale, sprites.get(0).getHeight()*scale, "");
        this.powerType = powerType;
    }

    /**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     * @param screen    The main game screen.
     */
    public void update(GameScreen screen){
        // Check for collision with power-ups
        if (overlaps(screen.getPlayer().hitBox)){
        	
        	// Attack speed power-up
        	if (this.powerType == "ATKSPD") {
        		screen.getPlayer().increaseAttackSpeed(screen);
        		destroy(screen);
        	}
        	
        	// Damage power-up
        	if (this.powerType == "DMGUP") {
        		screen.getPlayer().increaseDamage(screen);
        		destroy(screen);
        	}
        	
        	// Health power-up
        	if (this.powerType == "HEALTH") {
        		screen.getPlayer().increaseHealth(screen);
        		destroy(screen);
        	}
        	
        	// Invincible power-up
        	if (this.powerType == "INVINCIBLE") {
        		screen.getPlayer().setInvincible(screen);
        		destroy(screen);
        	}
        	
        	// Speed power-up
        	if (this.powerType == "SPEED") {
        		screen.getPlayer().increaseSpeed(screen);
        		destroy(screen);
        	}
        }
    }

    /**
     * Called when the power-up needs to be destroyed.
     * @param screen    The main game screen.
     */
    private void destroy(GameScreen screen){
        screen.powerups.removeValue(this,true);
    }
    
    /** 
     * Saves all the powerup's properties in JSON format.
     * @return	A JsonValue containing all the powerup's properties.
     */
    @Override
    public JsonValue toJson() {
    	JsonValue json = super.toJson();
    	json.addChild("powerType", new JsonValue(powerType));
    	return json;
    }
}
