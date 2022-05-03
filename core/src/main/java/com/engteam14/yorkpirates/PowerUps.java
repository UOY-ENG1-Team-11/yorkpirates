package com.engteam14.yorkpirates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

//All of PowerUps class is part of new requirement: UR.POWER_UPS
public class PowerUps extends GameObject {

    public String powerType;

    /**
     * Generates a power-up object within the game with a hit-box.
     *
     * @param sprites   The animation frames, or a single sprite.
     * @param x         The x-coordinate of the powerup.
     * @param y         The y-coordinate of the powerup.
     * @param scale     The size of the powerup.
     * @param powerType The type of powerup.
     */
    public PowerUps(Array<Texture> sprites, float x, float y, float scale, String powerType) {
        super(sprites, 0, x, y, sprites != null ? sprites.get(0).getWidth() * scale : 50 * scale, sprites != null ? sprites.get(0).getHeight() * scale : 50 * scale, "");
        this.powerType = powerType;
    }

    /**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     *
     * @param screen The main game screen.
     */
    public void update(GameScreen screen) {
        // Check for collision with power-ups
        if (overlaps(screen.getPlayer().hitBox)) {
            // Attack speed power-up
            if (this.powerType.equals("ATKSPD")) {
                screen.getPlayer().increaseAttackSpeed(screen, screen.getElapsedTime());
                destroy(screen);
            }

            // Damage power-up
            if (this.powerType.equals("DMGUP")) {
                screen.getPlayer().increaseDamage(screen, screen.getElapsedTime());
                destroy(screen);
            }

            // Health power-up
            if (this.powerType.equals("HEALTH")) {
                screen.getPlayer().increaseHealth(screen);
                destroy(screen);
            }

            // Invincible power-up
            if (this.powerType.equals("INVINCIBLE")) {
                screen.getPlayer().setInvincible(screen, screen.getElapsedTime());
                destroy(screen);
            }

            // Speed power-up
            if (this.powerType.equals("SPEED")) {
                screen.getPlayer().increaseSpeed(screen, screen.getElapsedTime());
                destroy(screen);
            }
        }
    }

    /**
     * Called when the power-up needs to be destroyed.
     *
     * @param screen The main game screen.
     */
    private void destroy(GameScreen screen) {
        screen.powerups.removeValue(this, true);
    }

    /**
     * Saves all the powerup's properties in JSON format.
     *
     * @return A JsonValue containing all the powerup's properties.
     */
    @Override
    public JsonValue toJson() {
        JsonValue json = super.toJson();
        json.addChild("powerType", new JsonValue(powerType));
        return json;
    }
}
