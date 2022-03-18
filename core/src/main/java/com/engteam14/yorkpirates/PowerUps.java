package com.engteam14.yorkpirates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

import static java.lang.Math.*;

public class PowerUps extends Consumables{

	/**
     * Generates a power-up object within the game with a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     */
    public PowerUps(YorkPirates game, Array<Texture> sprites, float x, float y, float scale, String powerType) {
        super(sprites, 0, x, y, sprites.get(0).getWidth()*scale, sprites.get(0).getHeight()*scale, powerType);

        
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
        		System.out.println(this.powerType);
        		screen.getPlayer().increaseAttackSpeed(screen);
        		destroy(screen);
        	}
        	
        	// Damage power-up
        	if (this.powerType == "DMGUP") {
        		System.out.println(this.powerType);
        		screen.getPlayer().increaseDamage(screen);
        		destroy(screen);
        	}
        	
        	// Health power-up
        	if (this.powerType == "HEALTH") {
        		System.out.println(this.powerType);
        		screen.getPlayer().increaseHealth(screen);
        		destroy(screen);
        	}
        	
        	// Invincible power-up
        	if (this.powerType == "INVINCIBLE") {
        		System.out.println(this.powerType);
        		screen.getPlayer().setInvincible(screen);
        		destroy(screen);
        	}
        	
        	// Speed power-up
        	if (this.powerType == "SPEED") {
        		System.out.println(this.powerType);
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
}
