package com.engteam14.yorkpirates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Projectile extends GameObject {
    private static final float collegeProjectileDamage = 20f; // College Projectile damage.
    private static final float playerProjectileDamage = 20f; // Player Projectile damage

    private final float maxDistance; // Projectile max range.
    private final GameObject origin;

    private final float dx; //x component to add every frame
    private final float dy; //y component to add every frame
    public final float projectileSpeed; // Projectile movement speed.


    /**
     * Generates a projectile object within the game with animated frame(s) and a hit-box.
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     * @param origin The object which the projectile originates from.
     * @param goal_x The x coordinate within the map the object is moving towards.
     * @param goal_y The y coordinate within the map the object is moving towards.
     * @param team   The team of the projectile.
     */
    public Projectile(Array<Texture> frames, float fps, float range, GameObject origin, float goal_x, float goal_y, String team) {
        super(frames, fps, origin.x, origin.y, 5f, 5f, team);
        this.origin = origin;

        // Speed calculations
        if (Objects.equals(team, GameScreen.playerTeam)) {
            projectileSpeed = 150f;
        } else {
            projectileSpeed = 50f;
        }

        // Movement calculations
        float changeInX = goal_x - origin.x;
        float changeInY = goal_y - origin.y;
        float scaleFactor = max(abs(changeInX), abs(changeInY));
        dx = changeInX / scaleFactor;
        dy = changeInY / scaleFactor;

        maxDistance = range;
    }

    /**
     * Generates a projectile object within the game from a saved JsonValue.
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     * @param json   The JsonValue to read the projectile's properties from.
     */
    public Projectile(Array<Texture> frames, float fps, JsonValue json) {
        super(frames, fps, json);
        dx = json.getFloat("dx");
        dy = json.getFloat("dy");
        projectileSpeed = json.getFloat("speed");
        maxDistance = json.getFloat("maxDistance");
        origin = new GameObject(null, 0, json);
    }

    /**
     * Called once per frame. Used to perform calculations such as projectile movement and collision detection.
     *
     * @param screen The main game screen.
     */
    public void update(GameScreen screen) {

        // Movement Calculations
        float xMove = projectileSpeed * dx;
        float yMove = projectileSpeed * dy;
        move(xMove, yMove);

        // Hit calculations
        if (team == screen.playerTeam) {
            for (int i = 0; i < screen.colleges.size; i++) {
                if (overlaps(screen.colleges.get(i).hitBox)) {
                    if (!Objects.equals(team, screen.colleges.get(i).team)) { // Checks if projectile and college are on the same team
                        screen.colleges.get(i).takeDamage(screen, ((playerProjectileDamage * (Player.playerProjectileDamageMultiplier)) / screen.getDifficulty()), team);
                    }
                    destroy(screen);
                } else {
                    for (int n = 0; n < screen.colleges.get(i).boats.size; n++) {
                        if (overlaps(screen.colleges.get(i).boats.get(n).hitBox)) {
                            if (!Objects.equals(team, screen.colleges.get(i).team)) { // Checks if projectile and boat are on the same time
                                screen.colleges.get(i).boats.get(n).takeDamage(screen, playerProjectileDamage * (Player.playerProjectileDamageMultiplier), team);
                            }
                            destroy(screen);
                        }
                    }
                }
            }
        } else {
            if (overlaps(screen.getPlayer().hitBox)) {
                if (!Objects.equals(team, GameScreen.playerTeam)) { // Checks if projectile and player are on the same team
                    screen.getPlayer().takeDamage(screen, collegeProjectileDamage * screen.getDifficulty(), team);
                }
                destroy(screen);
            }
        }

        // Destroys after max travel distance
        if (Math.sqrt(Math.pow(origin.x - x, 2) + Math.pow(origin.y - y, 2)) > maxDistance) destroy(screen);
    }

    @Override
    public JsonValue toJson() {
        JsonValue json = super.toJson();
        json.addChild("dx", new JsonValue(dx));
        json.addChild("dy", new JsonValue(dy));
        json.addChild("speed", new JsonValue(projectileSpeed));
        json.addChild("maxDistance", new JsonValue(maxDistance));
        json.addChild("origin", origin.toJson());
        return json;
    }
   
    
    /*
    /**
     * Called when colliding with a damage increase power-up.
     * @param screen            The main game screen.
     
    public void increaseDamage(GameScreen screen) {
    	playerProjectileDamage = 400f;
    }
    */

    /**
     * Called when the projectile needs to be destroyed.
     *
     * @param screen The main game screen.
     */
    private void destroy(GameScreen screen) {
        screen.projectiles.removeValue(this, true);
    }
}
