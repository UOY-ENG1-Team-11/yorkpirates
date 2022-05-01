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
    public static boolean AtkSpdBought = false;
    public static boolean AtkDmgBought = false;
    public static boolean SpdBought = false;
    
    // Player Multipliers
    public static float playerProjectileDamageUpgrade = 1;
    public static int playerAttackSpeedUpgrade = 1;
    public float playerSpeedUpgrade = 1f;
    
    public static float playerProjectileDamageMultiplier = 1f; // Player Projectile damage Multiplier
    public static int playerAttackSpeedMultiplier = 1 * playerAttackSpeedUpgrade; // Player Projectile Fire Rate Multiplier
    public float playerSpeedMultiplier = 1f * playerSpeedUpgrade; // Player Movement Speed Multiplier
    
    // Movement calculation values
    private int previousDirectionX;
    private int previousDirectionY;
    private float distance;
    private long lastMovementScore;
    
    // Player Values
    private HealthBar playerHealth;
    private float splashTime;
    private long timeLastHit;
    private boolean doBloodSplash = false;
    public long lastShotFired;

    public boolean alive = true;
    
    // Time variable declarations
    public static long atkSpdTime;
    public static long dmgUpTime;
    public static long invincibleTime;
    public static long speedUpTime;

    /**
     * Generates a player object within the game with animated frame(s) and a hit-box.
     * @param frames    The animation frames, or a single sprite.
     * @param fps       The number of frames to be displayed per second.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param width     The size of the object in the x-axis.
     * @param height    The size of the object in the y-axis.
     * @param team      The team the player is on.
     */
    public Player(YorkPirates game, Array<Texture> frames, float fps, float x, float y, float width, float height, String team){
        super(frames, fps, x, y, width, height, team);
        lastMovementScore = 0;
        splashTime = 0;
        lastShotFired = 0;

        // Generate health
        setMaxHealth(HEALTH);
        if (game != null) {
            Array<Texture> sprites = new Array<>();
            sprites.add(game.textureHandler.getTexture("allyHealthBar"));
            playerHealth = new HealthBar(this, sprites);
        }
    }

    /**
     * Called once per frame. Used to perform calculations such as player/camera movement.
     * @param screen    The main game screen.
     * @param camera    The player camera.
     */
    public void update(GameScreen screen, OrthographicCamera camera){
        Vector2 oldPos = new Vector2(x,y); // Stored for next-frame calculations

        // Get input movement
        int horizontal = ((Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) ? 1 : 0);
        int vertical = ((Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) ? 1 : 0)
                - ((Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) ? 1 : 0);

        // Calculate collision && movement
        if (horizontal != 0 || vertical != 0){
            move(SPEED*playerSpeedMultiplier *horizontal, SPEED*playerSpeedMultiplier *vertical);
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
        distance += Math.pow((Math.pow((x - oldPos.x),2f) + Math.pow((y - oldPos.y),2f)),0.5f)/10f;

        // Camera Calculations
        ProcessCamera(screen, camera);

        // Blood splash calculations
        if(doBloodSplash){
            if(splashTime > 1){
                doBloodSplash = false;
                splashTime = 0;
            }else{
                splashTime += 1;
            }
        }
        
        // Time Checks
        if (TimeUtils.timeSinceMillis(timeLastHit) > 10000){
        	//System.out.println(screen.getDifficulty());
            currentHealth += (0.03/screen.getDifficulty());
            if(currentHealth > maxHealth) currentHealth = maxHealth;
            playerHealth.resize(currentHealth);
        }
        
        if (TimeUtils.timeSinceMillis(atkSpdTime) > 10000) {
        	playerAttackSpeedMultiplier = 1 * playerAttackSpeedUpgrade;
        	atkSpdTime = 0;
        }
        
        if (TimeUtils.timeSinceMillis(dmgUpTime) > 10000) {
        	playerProjectileDamageMultiplier = 1f * playerProjectileDamageUpgrade;
        	dmgUpTime = 0;
        }
        
        if (TimeUtils.timeSinceMillis(invincibleTime) > 10000) {
        	invincible = false;
        	invincibleTime = 0;
        }
        
        if (TimeUtils.timeSinceMillis(speedUpTime) > 10000) {
        	playerSpeedMultiplier = 1f * playerSpeedUpgrade;
        	speedUpTime = 0;
        }      
        
        
    }

    /**
     *  Calculate if the current player position is safe to be in.
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
     * Moves the player within the x and y-axis of the game world.
     * @param x     The amount to move the player within the x-axis.
     * @param y     The amount to move the player within the y-axis.
     */
    @Override
    public void move(float x, float y){
        this.x += x * Gdx.graphics.getDeltaTime();
        this.y += y * Gdx.graphics.getDeltaTime();
        playerHealth.move(this.x, this.y + height/2 + 2f); // Healthbar moves with player
    }

    /**
     * Called when a projectile hits the college.
     * @param screen            The main game screen.
     * @param damage            The damage dealt by the projectile.
     * @param projectileTeam    The team of the projectile.
     */
    @Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam){
    	if(invincible == false) {
            timeLastHit = TimeUtils.millis();
            currentHealth -= damage;
            doBloodSplash = true;

            // Health-bar reduction
            if(currentHealth > 0){
                if (screen != null) {
                    playerHealth.resize(currentHealth);
                    screen.sounds.damage();
                }
            }else {
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
     * @param screen            The main game screen.
     */
    public void increaseAttackSpeed(GameScreen screen) {
    	playerAttackSpeedMultiplier *= 2;
    	// set back to 1
        if (screen != null) {
            screen.AtkSpdTimer.getTime();
        }
    	atkSpdTime = TimeUtils.millis();
    }
    
    /**
     * Called when colliding with a damage increase power-up.
     * @param screen            The main game screen.
     */
    public void increaseDamage(GameScreen screen) {
    	playerProjectileDamageMultiplier *= 10 ;
    	// set back to 20f
        if (screen != null) {
            screen.AtkDmgTimer.getTime();
        }
    	dmgUpTime = TimeUtils.millis();
    }
    
    /**
     * Called when colliding with a health power-up.
     * @param screen            The main game screen.
     */
    public void increaseHealth(GameScreen screen) {
        currentHealth += 50;
        if(currentHealth > maxHealth){ 
        	currentHealth = maxHealth;
        }
        if (screen != null) {
            playerHealth.resize(currentHealth);
        }
    }
    
    /**
     * Called when colliding with a speed power-up.
     * @param screen            The main game screen.
     */
    public void increaseSpeed(GameScreen screen){
    	playerSpeedMultiplier *= 1.5;
        if (screen != null) {
            screen.SpeedTimer.getTime();
        }
    	speedUpTime = TimeUtils.millis();
    }	
    
    /**
     * Called when colliding with an invincibilty power-up.
     * @param screen            The main game screen.
     */
    public void setInvincible(GameScreen screen) {
    	invincible = true;
        if (screen != null) {
            screen.InvincibleTimer.getTime();
        }
    	invincibleTime = TimeUtils.millis();
    }
    
    /**
     * Called when attack speed shop upgrade is bought.
     * @param screen            The main game screen.
     */
    public void upgradeAttackSpeed(GameScreen screen) {
    	playerAttackSpeedUpgrade = 2;
    	AtkSpdBought = true;
    }

    /**
     * Called when attack damage shop upgrade is bought.
     * @param screen            The main game screen.
     */
    public void upgradeAttackDamage(GameScreen screen) {
    	playerProjectileDamageUpgrade = 5;
    	AtkDmgBought = true;
    }
    
    /**
     * Called when speed shop upgrade is bought.
     * @param screen            The main game screen.
     */
    public void upgradeSpeed(GameScreen screen) {
    	playerSpeedUpgrade = 1.5f;
    	SpdBought = true;
    }


    /**
     * Called after update(), calculates whether the camera should follow the player and passes it to the game screen.
     * @param screen    The main game screen.
     * @param camera    The player camera.
     */
    private void ProcessCamera(GameScreen screen, OrthographicCamera camera) {
        Vector2 camDiff = new Vector2(x - camera.position.x, y - camera.position.y);
        screen.toggleFollowPlayer(Math.abs(camDiff.x) > camera.viewportWidth / 2 * CAMERA_SLACK || Math.abs(camDiff.y) > camera.viewportWidth / 2 * CAMERA_SLACK);
    }

    /**
     * Called when drawing the player.
     * @param batch         The batch to draw the player within.
     * @param elapsedTime   The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        // Generates the sprite
        Texture frame = anim.getKeyFrame((currentHealth/maxHealth > 0.66f) ? 0 : ((currentHealth/maxHealth > 0.33f) ? 2 : 1), true);
        if(doBloodSplash){
            batch.setShader(shader); // Set our grey-out shader to the batch
        } float rotation = (float) Math.toDegrees(Math.atan2(previousDirectionY, previousDirectionX));

        // Draws sprite and health-bar
        batch.draw(frame, x - width/2, y - height/2, width/2, height/2, width, height, 1f, 1f, rotation, 0, 0, frame.getWidth(), frame.getHeight(), false, false);
        batch.setShader(null);
    }
    /**
     * Draws the player's healthbar to the screen
     * @param batch	The sprite batch to draw the healthbar with
     */
    public void drawHealthBar(SpriteBatch batch){
        if(!(playerHealth == null)) playerHealth.draw(batch, 0);
    }

    public float getDistance() {
        return distance;
    }
    
    /** 
     * Saves all the players's properties in JSON format.
     * @return	A JsonValue containing all the player's properties.
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
    	return json;
    }
    
    /** 
     * Sets all properties to those contained in the passed JsonValue.
     * @param json	The root JsonValue containing the player properties.
     */
    @Override
    public void fromJson(JsonValue json) {
    	super.fromJson(json);
    	previousDirectionX = json.getInt("previousDirectionX");
    	previousDirectionY = json.getInt("previousDirectionY");
    	distance = json.getFloat("distance");
    	AtkSpdBought = json.getBoolean("AtkSpdBought");
    	AtkDmgBought = json.getBoolean("AtkDmgBought");
    	SpdBought = json.getBoolean("SpdBought");
    	playerProjectileDamageUpgrade = json.getFloat("DmgUpgrade");
    	playerAttackSpeedUpgrade = json.getInt("AtkSpdUpgrade");
    	playerSpeedUpgrade = json.getFloat("SpdUpgrade");
    	playerHealth.resize(currentHealth);
    	playerHealth.move(this.x, this.y + height/2 + 2f);
    }
}
