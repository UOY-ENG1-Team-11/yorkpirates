package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;

public class Player extends GameObject {

    // Player constants
    private static final int POINT_FREQUENCY = 1000; // How often the player gains points by moving.
    private static final float CAMERA_SLACK = 0.1f; // What percentage of the screen the player can move in before the camera follows.
    public static final float SPEED = 70f; // Player movement speed.
    private static final int HEALTH = 200;

    // Invincibility Checker
    private boolean invincible = false;

    // Upgrade check variables
    public boolean AtkSpdBought = false;
    public boolean AtkDmgBought = false;
    public boolean SpdBought = false;

    // Player Multipliers
    public float playerProjectileDamageUpgrade = 1;
    public int playerAttackSpeedUpgrade = 1;
    public float playerSpeedUpgrade = 1f;

    public float playerProjectileDamageMultiplier = 1f; // Player Projectile damage Multiplier
    public int playerAttackSpeedMultiplier = 1 * playerAttackSpeedUpgrade; // Player Projectile Fire Rate Multiplier
    public float playerSpeedMultiplier = 1f * playerSpeedUpgrade; // Player Movement Speed Multiplier

    // Movement calculation values
    private int previousDirectionX;
    private int previousDirectionY;
    private float distance;
    private long lastMovementScore;

    // Player Values
    public HealthBar playerHealth;
    private float splashTime;
    private float timeLastHit;
    private boolean doBloodSplash = false;
    public long lastShotFired;

    public boolean alive = true;

    // Time variable declarations
    public static float atkSpdTime = Float.MAX_VALUE;
    public static float dmgUpTime = Float.MAX_VALUE;
    public static float invincibleTime = Float.MAX_VALUE;
    public static float speedUpTime = Float.MAX_VALUE;

    /**
     * Generates a player object within the game with animated frame(s) and a hit-box.
     *
     * @param frames The animation frames, or a single sprite.
     * @param fps    The number of frames to be displayed per second.
     * @param x      The x coordinate within the map to initialise the object at.
     * @param y      The y coordinate within the map to initialise the object at.
     * @param width  The size of the object in the x-axis.
     * @param height The size of the object in the y-axis.
     * @param team   The team the player is on.
     */
    public Player(YorkPirates game, Array<Texture> frames, float fps, float x, float y, float width, float height, String team) {
        super(frames, fps, x, y, width, height, team);
        lastMovementScore = 0;
        splashTime = 0;
        lastShotFired = 0;

        // Generate health
        setMaxHealth(HEALTH);
        Array<Texture> sprites = null;
        if (frames != null) {
            sprites = new Array<>();
            sprites.add(game.textureHandler.getTexture("allyHealthBar"));
        }
        playerHealth = new HealthBar(this, sprites);
    }

    /**
     * Called once per frame. Used to perform calculations such as player/camera movement.
     *
     * @param screen The main game screen.
     * @param camera The player camera.
     */
    public void update(GameScreen screen, OrthographicCamera camera) {
        Vector2 oldPos = new Vector2(x, y); // Stored for next-frame calculations

        // Get input movement
        int horizontal = ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) ? 1 : 0);
        int vertical = ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) ? 1 : 0);

        // Calculate collision && movement
        if (horizontal != 0 || vertical != 0) {
            move(SPEED * playerSpeedMultiplier * horizontal, SPEED * playerSpeedMultiplier * vertical);
            previousDirectionX = horizontal;
            previousDirectionY = vertical;
            if (safeMove(screen.getMain().edges)) {
                if (TimeUtils.timeSinceMillis(lastMovementScore) > POINT_FREQUENCY) {
                    lastMovementScore = TimeUtils.millis();
                    screen.points.Add(1);
                }
            } else {    // Collision
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
        // Track distance travelled
        distance += Math.pow((Math.pow((x - oldPos.x), 2f) + Math.pow((y - oldPos.y), 2f)), 0.5f) / 10f;

        // Camera Calculations
        ProcessCamera(screen, camera);

        // Blood splash calculations
        if (doBloodSplash) {
            if (splashTime > 1) {
                doBloodSplash = false;
                splashTime = 0;
            } else {
                splashTime += 1;
            }
        }

        // Time Checks
        if (screen.getElapsedTime() > timeLastHit + 10) {
            //System.out.println(screen.getDifficulty());
            currentHealth += (0.03 / screen.getDifficulty());
            if (currentHealth > maxHealth) currentHealth = maxHealth;
            playerHealth.resize(currentHealth);
        }

        if (screen.getElapsedTime() > atkSpdTime + 10 && atkSpdTime < Float.MAX_VALUE) {
            playerAttackSpeedMultiplier = 1 * playerAttackSpeedUpgrade;
            atkSpdTime = Float.MAX_VALUE;
        }

        if (screen.getElapsedTime() > dmgUpTime + 10 && dmgUpTime < Float.MAX_VALUE) {
            playerProjectileDamageMultiplier = 1f * playerProjectileDamageUpgrade;
            dmgUpTime = Float.MAX_VALUE;
        }

        if (screen.getElapsedTime() > invincibleTime + 10 && invincibleTime < Float.MAX_VALUE) {
            invincible = false;
            invincibleTime = Float.MAX_VALUE;
        }

        if (screen.getElapsedTime() > speedUpTime + 10 && speedUpTime < Float.MAX_VALUE) {
            playerSpeedMultiplier = 1f * playerSpeedUpgrade;
            speedUpTime = Float.MAX_VALUE;
        }


    }

    /**
     * Calculate if the current player position is safe to be in.
     *
     * @param edges A 2d array containing safe/unsafe positions to be in.
     * @return If the current position is safe.
     */
    public Boolean safeMove(Array<Array<Boolean>> edges) {
        return (
                edges.get((int) ((y + height / 2) / 16)).get((int) ((x + width / 2) / 16)) &&
                        edges.get((int) ((y + height / 2) / 16)).get((int) ((x - width / 2) / 16)) &&
                        edges.get((int) ((y - height / 2) / 16)).get((int) ((x + width / 2) / 16)) &&
                        edges.get((int) ((y - height / 2) / 16)).get((int) ((x - width / 2) / 16))
        );
    }

    /**
     * Moves the player within the x and y-axis of the game world.
     *
     * @param x The amount to move the player within the x-axis.
     * @param y The amount to move the player within the y-axis.
     */
    @Override
    public void move(float x, float y) {
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
        playerHealth.move(this.x, this.y + height / 2 + 2f); // Healthbar moves with player
    }

    /**
     * Called when a projectile hits the college.
     *
     * @param screen         The main game screen.
     * @param damage         The damage dealt by the projectile.
     * @param projectileTeam The team of the projectile.
     */
    @Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam) {
        if (invincible == false) {
            timeLastHit = screen != null ? screen.getElapsedTime() : 0;
            currentHealth -= damage;
            doBloodSplash = true;

            // Health-bar reduction
            if (currentHealth > 0) {
                if (screen != null) {
                    playerHealth.resize(currentHealth);
                    screen.sounds.damage();
                }
            } else {
                playerHealth = null;
                alive = false;
                if (screen != null) {
                    screen.gameEnd(alive);
                }
            }
        }
    }

    /**
     * Called when colliding with an attack speed increase power-up.
     *
     * @param screen        The main game screen.
     * @param elapsedTime   The amount of time passed in the game
     */
    public void increaseAttackSpeed(GameScreen screen, float elapsedTime) {
        playerAttackSpeedMultiplier *= 2;
        // set back to 1
        if (screen != null) {
            screen.AtkSpdTimer.getTime(elapsedTime);
        }
        atkSpdTime = elapsedTime;
    }

    /**
     * Called when colliding with a damage increase power-up.
     *
     * @param screen        The main game screen.
     * @param elapsedTime   The amount of time passed in the game
     */
    public void increaseDamage(GameScreen screen, float elapsedTime) {
        playerProjectileDamageMultiplier *= 10;
        // set back to 20f
        if (screen != null) {
            screen.AtkDmgTimer.getTime(elapsedTime);
        }
        dmgUpTime = elapsedTime;
    }

    /**
     * Called when colliding with a health power-up.
     *
     * @param screen        The main game screen.
     */
    public void increaseHealth(GameScreen screen) {
        currentHealth += 50;
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
        if (screen != null) {
            playerHealth.resize(currentHealth);
        }
    }

    /**
     * Called when colliding with a speed power-up.
     *
     * @param screen        The main game screen.
     * @param elapsedTime   The amount of time passed in the game
     */
    public void increaseSpeed(GameScreen screen, float elapsedTime) {
        playerSpeedMultiplier *= 1.5;
        if (screen != null) {
            screen.SpeedTimer.getTime(elapsedTime);
        }
        speedUpTime = elapsedTime;
    }

    /**
     * Called when colliding with an invincibilty power-up.
     *
     * @param screen        The main game screen.
     * @param elapsedTime   The amount of time passed in the game.
     */
    public void setInvincible(GameScreen screen, float elapsedTime) {
        invincible = true;
        if (screen != null) {
            screen.InvincibleTimer.getTime(elapsedTime);
        }
        invincibleTime = elapsedTime;
    }

    /**
     * Called when attack speed shop upgrade is bought.
     *
     * @param screen The main game screen.
     */
    public void upgradeAttackSpeed(GameScreen screen) {
        playerAttackSpeedUpgrade = 2;
        AtkSpdBought = true;
    }

    /**
     * Called when attack damage shop upgrade is bought.
     *
     * @param screen The main game screen.
     */
    public void upgradeAttackDamage(GameScreen screen) {
        playerProjectileDamageUpgrade = 5;
        AtkDmgBought = true;
    }

    /**
     * Called when speed shop upgrade is bought.
     *
     * @param screen The main game screen.
     */
    public void upgradeSpeed(GameScreen screen) {
        playerSpeedUpgrade = 1.5f;
        SpdBought = true;
    }


    /**
     * Called after update(), calculates whether the camera should follow the player and passes it to the game screen.
     *
     * @param screen The main game screen.
     * @param camera The player camera.
     */
    private void ProcessCamera(GameScreen screen, OrthographicCamera camera) {
        Vector2 camDiff = new Vector2(x - camera.position.x, y - camera.position.y);
        screen.toggleFollowPlayer(Math.abs(camDiff.x) > camera.viewportWidth / 2 * CAMERA_SLACK || Math.abs(camDiff.y) > camera.viewportWidth / 2 * CAMERA_SLACK);
    }

    /**
     * Called when drawing the player.
     *
     * @param batch       The batch to draw the player within.
     * @param elapsedTime The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime) {
        // Generates the sprite
        Texture frame = anim.getKeyFrame((currentHealth / maxHealth > 0.66f) ? 0 : ((currentHealth / maxHealth > 0.33f) ? 2 : 1), true);
        if (doBloodSplash) {
            batch.setShader(shader); // Set our grey-out shader to the batch
        }
        float rotation = (float) Math.toDegrees(Math.atan2(previousDirectionY, previousDirectionX));

        // Draws sprite and health-bar
        batch.draw(frame, x - width / 2, y - height / 2, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
        batch.setShader(null);
    }

    /**
     * Draws the player's healthbar to the screen
     *
     * @param batch The sprite batch to draw the healthbar with
     */
    public void drawHealthBar(SpriteBatch batch) {
        if (!(playerHealth == null)) playerHealth.draw(batch, 0);
    }

    public float getDistance() {
        return distance;
    }

    /**
     * Saves all the players's properties in JSON format.
     *
     * @return A JsonValue containing all the player's properties.
     */
    @Override
    public JsonValue toJson() {
        JsonValue json = super.toJson();
        json.addChild("previousDirectionX", new JsonValue(previousDirectionX));
        json.addChild("previousDirectionY", new JsonValue(previousDirectionY));
        json.addChild("distance", new JsonValue(distance));
        json.addChild("AtkSpdBought", new JsonValue(AtkSpdBought));
        json.addChild("AtkDmgBought", new JsonValue(AtkDmgBought));
        json.addChild("SpdBought", new JsonValue(SpdBought));
        json.addChild("DmgUpgrade", new JsonValue(playerProjectileDamageUpgrade));
        json.addChild("AtkSpdUpgrade", new JsonValue(playerAttackSpeedUpgrade));
        json.addChild("SpdUpgrade", new JsonValue(playerSpeedUpgrade));
        json.addChild("atkSpdTime", new JsonValue(atkSpdTime));
        json.addChild("dmgUpTime", new JsonValue(dmgUpTime));
        json.addChild("invincibleTime", new JsonValue(invincibleTime));
        json.addChild("speedUpTime", new JsonValue(speedUpTime));
        return json;
    }

    /**
     * Sets all properties to those contained in the passed JsonValue.
     *
     * @param screen    The main game screen.
     * @param json      The root JsonValue containing the player properties.
     */
    public void fromJson(GameScreen screen, JsonValue json) {
        super.fromJson(json);
        previousDirectionX = json.getInt("previousDirectionX");
        previousDirectionY = json.getInt("previousDirectionY");
        distance = json.getFloat("distance");

        //Read shop items bought
        AtkSpdBought = json.getBoolean("AtkSpdBought");
        AtkDmgBought = json.getBoolean("AtkDmgBought");
        SpdBought = json.getBoolean("SpdBought");

        //Apply shop item effects
        playerProjectileDamageUpgrade = json.getFloat("DmgUpgrade");
        playerProjectileDamageMultiplier = 1f * playerProjectileDamageUpgrade;

        playerAttackSpeedUpgrade = json.getInt("AtkSpdUpgrade");
        playerAttackSpeedMultiplier = 1 * playerAttackSpeedUpgrade;

        playerSpeedUpgrade = json.getFloat("SpdUpgrade");
        playerSpeedMultiplier = 1f * playerSpeedUpgrade;

        invincible = false;

        //Read current powerups and apply them
        atkSpdTime = json.getFloat("atkSpdTime");
        if(atkSpdTime < Float.MAX_VALUE) {
            increaseAttackSpeed(screen, atkSpdTime);
        }
        dmgUpTime = json.getFloat("dmgUpTime");
        if(dmgUpTime < Float.MAX_VALUE) {
            increaseDamage(screen, dmgUpTime);
        }
        invincibleTime = json.getFloat("invincibleTime");
        if(invincibleTime < Float.MAX_VALUE) {
            setInvincible(screen, invincibleTime);
        }
        speedUpTime = json.getFloat("speedUpTime");
        if(speedUpTime < Float.MAX_VALUE) {
            increaseSpeed(screen, speedUpTime);
        }

        playerHealth.resize(currentHealth);
        playerHealth.move(this.x, this.y + height / 2 + 2f);
    }
}
