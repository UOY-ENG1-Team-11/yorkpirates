package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonValue.ValueType;

public class GameObject {

    public float x;
    public float y;
    public float width;
    public float height;

    public int maxHealth;
    public float currentHealth;

    public String team;
    Texture sprite;
    public Rectangle hitBox;
    Animation<Texture> anim;

    ShaderProgram shader;

    /**
     * Generates a generic object within the game with animated frame(s) and a hit-box.
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     * @param x      The x coordinate within the map to initialise the object at.
     * @param y      The y coordinate within the map to initialise the object at.
     * @param width  The size of the object in the x-axis.
     * @param height The size of the object in the y-axis.
     * @param team   The team the object is on.
     */
    public GameObject(Array<Texture> frames, float fps, float x, float y, float width, float height, String team) {
        if (frames != null) {
            shader = new ShaderProgram(Gdx.files.internal("red.vsh"), Gdx.files.internal("red.fsh"));
            changeImage(frames, fps);
        }
        this.x = x;
        this.y = y;
        this.team = team;
        this.width = width;
        this.height = height;
        setHitbox();
    }

    /**
     * Generates a generic object within the game from a loaded json (New requirement: UR.SAVE_LOAD)
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     * @param json   The jsonValue containing the object's properties
     */
    public GameObject(Array<Texture> frames, float fps, JsonValue json) {
        if (frames != null) {
            changeImage(frames, fps);
        }
        fromJson(json);
    }

    /**
     * Called when the image needs to be changed or set.
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     */
    void changeImage(Array<Texture> frames, float fps) {
        sprite = frames.get(0);
        anim = new Animation<>(fps == 0 ? 0 : (1f / fps), frames);
    }

    /**
     * Called when the health of the object needs to be set.
     *
     * @param mh The health value for the object
     */
    public void setMaxHealth(int mh) {
        maxHealth = mh;
        currentHealth = maxHealth;
    }

    /**
     * Called when a projectile hits the object.
     *
     * @param screen         The main game screen.
     * @param damage         The damage dealt by the projectile.
     * @param projectileTeam The team of the projectile.
     */
    public void takeDamage(GameScreen screen, float damage, String projectileTeam) {
        currentHealth -= damage;
    }

    /**
     * Moves the object within the x and y-axis of the game world.
     *
     * @param x The amount to move the object within the x-axis.
     * @param y The amount to move the object within the y-axis.
     */
    public void move(float x, float y) {
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
    }

    /**
     * Sets the object's hit-box, based upon it's x, y, width and height values.
     */
    public void setHitbox() {
        hitBox = new Rectangle();
        updateHitboxPos();
        hitBox.width = width;
        hitBox.height = height;
    }

    /**
     * Updates the object's hit-box location to match the object's rendered location.
     */
    public void updateHitboxPos() {
        hitBox.x = x - width / 2;
        hitBox.y = y - height / 2;
    }

    /**
     * Checks if this object overlaps with another.
     *
     * @param rect The other object to be checked against.
     * @return True if overlapping, false otherwise.
     */
    public boolean overlaps(Rectangle rect) {
        updateHitboxPos();
        return hitBox.overlaps(rect);
    }

    /**
     * Called when drawing the object.
     *
     * @param batch       The batch to draw the object within.
     * @param elapsedTime The current time the game has been running for.
     */
    public void draw(SpriteBatch batch, float elapsedTime) {
        batch.draw(anim.getKeyFrame(elapsedTime, true), x - width / 2, y - height / 2, width, height);
    }

    /**
     * Saves all the object's properties in JSON format. (New requirement: UR.SAVE_LOAD)
     *
     * @return A JsonValue containing all the object's properties.
     */
    public JsonValue toJson() {
        JsonValue json = new JsonValue(ValueType.object);
        json.addChild("x", new JsonValue(x));
        json.addChild("y", new JsonValue(y));
        json.addChild("width", new JsonValue(width));
        json.addChild("height", new JsonValue(height));
        json.addChild("maxHealth", new JsonValue(maxHealth));
        json.addChild("currentHealth", new JsonValue(currentHealth));
        json.addChild("team", new JsonValue(team));
        return json;
    }

    /**
     * Sets all properties to those contained in the passed JsonValue. (New requirement: UR.SAVE_LOAD)
     *
     * @param json The root JsonValue containing the object properties.
     */
    public void fromJson(JsonValue json) {
        x = json.getFloat("x");
        y = json.getFloat("y");
        width = json.getFloat("width");
        height = json.getFloat("height");
        maxHealth = json.getInt("maxHealth");
        currentHealth = json.getFloat("currentHealth");
        team = json.getString("team");
        setHitbox();
    }

}