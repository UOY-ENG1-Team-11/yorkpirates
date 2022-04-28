package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;    
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue.ValueType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
 
public class GameScreen extends ScreenAdapter {	
    // Team name constants
    public static final String playerTeam = "PLAYER";
    public static final String enemyTeam = "ENEMY";
    public static final String neutralTeam = "NEUTRAL";
    
    // Powerup type name constants
    public static final String attackSpeed = "ATKSPD";
    public static final String damageUp = "DMGUP";
    public static final String healthUp = "HEALTH";
    public static final String invincible = "INVINCIBLE";
    public static final String speedUp = "SPEED";
    
    // Power-ups timers
    public PowerUpsTimer AtkSpdTimer;
    public PowerUpsTimer AtkDmgTimer;
    public PowerUpsTimer InvincibleTimer;
    public PowerUpsTimer SpeedTimer;
    
	// Difficulty
	public int difficulty;
    
    // Score managers
    public ScoreManager points;
    public ScoreManager loot;

    // Colleges
    public Array<College> colleges;
    public Array<Projectile> projectiles;
    

    // Weather
    public WeatherManager weather;

    // Consumables
    public Array<PowerUps> powerups;


    // Sound
    public final Music music;
    public final SoundManager sounds;

    // Main classes
    private final YorkPirates game;

    // Player
    public final Player player;
    private String playerName;
    private Vector3 followPos;
    private boolean followPlayer = false;

    // UI & Camera
    private final HUD gameHUD;
    private final SpriteBatch HUDBatch;
    private final OrthographicCamera HUDCam;
    private final FitViewport viewport;

    // Tilemap
    private final TiledMap tiledMap;
    private final OrthogonalTiledMapRenderer tiledMapRenderer;

    // Trackers
    private float elapsedTime = 0;
    private boolean isPaused = false;
    private float lastPause = 0;

    /**
     * Initialises the main game screen, as well as relevant entities and data.
     * @param game  Passes in the base game class for reference.
     */
    public GameScreen(YorkPirates game){
		this.game = game;
        playerName = "Player";
        

        // Initialise points and loot managers
        points = new ScoreManager();
        loot = new ScoreManager();
        
        // Initialise power-ups manager
        AtkSpdTimer = new PowerUpsTimer();
        AtkDmgTimer = new PowerUpsTimer();
        InvincibleTimer = new PowerUpsTimer();
        SpeedTimer = new PowerUpsTimer();


        // Initialise HUD
        HUDBatch = new SpriteBatch();
        HUDCam = new OrthographicCamera();
        HUDCam.setToOrtho(false, game.camera.viewportWidth, game.camera.viewportHeight);
        viewport = new FitViewport( Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), HUDCam);
        gameHUD =  new HUD(this);

        //initialise sound
        sounds = new SoundManager();
        music = Gdx.audio.newMusic(Gdx.files.internal("Pirate1_Theme1.ogg"));
        music.setLooping(true);
        music.setVolume(0);
        music.play();

        // Initialise sprites array to be used generating GameObjects
        Array<Texture> sprites = new Array<>();
        
        // Load some textures
        getMain().textureHandler.loadTexture("homeArrow", Gdx.files.internal("homeArrow.png"));
        getMain().textureHandler.loadTexture("allyArrow", Gdx.files.internal("allyArrow.png"));
        getMain().textureHandler.loadTexture("allyHealthBar", Gdx.files.internal("allyHealthBar.png"));
        getMain().textureHandler.loadTexture("enemyHealthBar", Gdx.files.internal("enemyHealthBar.png"));
        getMain().textureHandler.loadTexture("questArrow", Gdx.files.internal("questArrow.png"));
        getMain().textureHandler.loadTexture("tempProjectile", Gdx.files.internal("tempProjectile.png"));
        getMain().textureHandler.loadTexture("enemyWave", Gdx.files.internal("enemy_wave.png"));
        getMain().textureHandler.loadTexture("badWeather", Gdx.files.internal("badWeather.png"));
        
        
        
        // Initialise player
        sprites.add(getMain().textureHandler.loadTexture("ship1", Gdx.files.internal("ship1.png")), 
        		getMain().textureHandler.loadTexture("ship2", Gdx.files.internal("ship2.png")), 
        		getMain().textureHandler.loadTexture("ship3", Gdx.files.internal("ship3.png")));
        player = new Player(getMain(), sprites, 2, 821, 489, 32, 16, playerTeam);
        sprites.clear();
        followPos = new Vector3(player.x, player.y, 0f);
        game.camera.position.lerp(new Vector3(760, 510, 0f), 1f);
        
        //initalise a wave
        //enemy_waves.add(new Enemy_Wave());

        // Initialise tilemap
        tiledMap = new TmxMapLoader().load("FINAL_MAP.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        
        // Initialise weather
        weather = new WeatherManager();
        
        // Initialise colleges
        College.capturedCount = 0;
        colleges = new Array<>();
        College newCollege;
        Array<Texture> collegeSprites = new Array<>();

        // Add alcuin
        collegeSprites.add(getMain().textureHandler.loadTexture("alcuin", Gdx.files.internal("alcuin.png")), 
        		getMain().textureHandler.loadTexture("alcuin_2", Gdx.files.internal("alcuin_2.png")));
        newCollege = new College(getMain(), collegeSprites, 1492, 665, 0.5f,"Alcuin", enemyTeam, player, getMain().textureHandler.loadTexture("alcuin_boat", Gdx.files.internal("alcuin_boat.png")));
        newCollege.addBoat(game, 80, -20, 0, new Vector2[] {new Vector2(1572, 645), new Vector2(1700, 500)});
        //newCollege.addBoat(-50, -40, -150);
        //newCollege.addBoat(-40, -70, 0);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add derwent
        collegeSprites.add(getMain().textureHandler.loadTexture("derwent", Gdx.files.internal("derwent.png")), 
        		getMain().textureHandler.loadTexture("derwent_2", Gdx.files.internal("derwent_2.png")));
        newCollege = (new College(getMain(),collegeSprites, 1815, 2105, 0.8f,"Derwent", enemyTeam, player, getMain().textureHandler.loadTexture("derwent_boat", Gdx.files.internal("derwent_boat.png"))));
        //newCollege.addBoat(-70, -20, 60);
        //newCollege.addBoat(-70, -60, 70);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add langwith
        collegeSprites.add(getMain().textureHandler.loadTexture("langwith", Gdx.files.internal("langwith.png")), 
        		getMain().textureHandler.loadTexture("langwith_2", Gdx.files.internal("langwith_2.png")));
        newCollege = (new College(getMain(),collegeSprites, 1300, 1530, 1.0f,"Langwith", enemyTeam, player, getMain().textureHandler.loadTexture("langwith_boat", Gdx.files.internal("langwith_boat.png"))));
        //newCollege.addBoat(-150, -50, 60);
        //newCollege.addBoat(-120, -10, -60);
        //newCollege.addBoat(-10, -40, 230);
        //newCollege.addBoat(140, 10, 300);
        //newCollege.addBoat(200, 35, 135);
        colleges.add(newCollege);
        collegeSprites.clear();

        // Add goodricke
        collegeSprites.add(getMain().textureHandler.loadTexture("goodricke", Gdx.files.internal("goodricke.png")));
        colleges.add(new College(getMain(),collegeSprites, 700, 525, 0.7f,"Home",playerTeam,player, getMain().textureHandler.getTexture("ship1")));

        // Initialise projectiles array to be used storing live projectiles
        projectiles = new Array<>();
        
        // Initialise powerups array to be used for storing the power-ups
        powerups = new Array<PowerUps>();
        getMain().textureHandler.loadTexture("UpAtkSpd", Gdx.files.internal("UpAtkSpd.png"));
        getMain().textureHandler.loadTexture("UpDmg", Gdx.files.internal("UpDmg.png"));
        getMain().textureHandler.loadTexture("UpHealth", Gdx.files.internal("UpHealth.png"));
        getMain().textureHandler.loadTexture("UpInvincible", Gdx.files.internal("UpInvincible.png"));
        getMain().textureHandler.loadTexture("UpSpeed", Gdx.files.internal("UpSpeed.png"));
        
        createPowerUp(1160, 525, attackSpeed);
        createPowerUp(1160, 625, attackSpeed);
        createPowerUp(1080, 525, damageUp);
        createPowerUp(900, 525, healthUp);
        createPowerUp(90, 52, healthUp);
        createPowerUp(1020, 525, invincible);      
        createPowerUp(960, 525, speedUp);
    }

    /**
     * Is called once every frame. Runs update(), renders the game and then the HUD.
     * @param delta The time passed since the previously rendered frame.
     */
    @Override
    public void render(float delta){
        // Only update if not paused
        if(!isPaused) {
            elapsedTime += delta;
            update();
        }
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);
        ScreenUtils.clear(0.1f, 0.6f, 0.6f, 1.0f);
        
        // Gameplay drawing batch
        game.batch.begin();
        tiledMapRenderer.setView(game.camera); // Draw map first so behind everything
        tiledMapRenderer.render();

        // Draw Projectiles
        for(int i = 0; i < projectiles.size; i++) {
            projectiles.get(i).draw(game.batch, 0);
        }
        
        //Draw waves
        weather.draw(game.batch, 0);

        // Draw Consumables
        for(int i = 0; i < powerups.size; i++) {
            powerups.get(i).draw(game.batch, 0);
        }
        
        // Draw Player, Player Health and Player Name
        if(!isPaused) {
            player.drawHealthBar(game.batch);
            player.draw(game.batch, elapsedTime);
            HUDBatch.begin();
            Vector3 pos = game.camera.project(new Vector3(player.x, player.y, 0f));
            game.font.draw(HUDBatch, playerName, pos.x, pos.y + 170f, 1f, Align.center, true);
            HUDBatch.end();
        }

        // Draw Colleges
        for(int i = 0; i < colleges.size; i++) {
            colleges.get(i).draw(game.batch, 0);
        }
        game.batch.end();

        // Draw HUD
        HUDBatch.setProjectionMatrix(HUDCam.combined);
        if(!isPaused) {
            // Draw UI
            gameHUD.renderStage(this);
            HUDCam.update();
        }
    }

    /**
     * Is called once every frame. Used for game calculations that take place before rendering.
     */
    private void update(){
        // Call updates for all relevant objects
        player.update(this, game.camera);
        for(int i = 0; i < colleges.size; i++) {
            colleges.get(i).update(this);
	        for(int n = 0; n < colleges.get(i).boats.size; n++) {
	            colleges.get(i).boats.get(n).update(this, player.x, player.y);
	        }
        }

        for(int i = 0; i < powerups.size; i++) {
            powerups.get(i).update(this);
        }
        
        // Check for projectile creation, then call projectile update
        int shootFrequency = 700/(Player.playerAttackSpeedMutliplier); // How often the player can shoot
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && TimeUtils.timeSinceMillis(player.lastShotFired) > shootFrequency){
        	player.lastShotFired = TimeUtils.millis();
        	sounds.cannon();
            Vector3 mouseVector = new Vector3(Gdx.input.getX(), Gdx.input.getY(),0);
            Vector3 mousePos = game.camera.unproject(mouseVector);
            Array<Texture> sprites = new Array<>();
            sprites.add(getMain().textureHandler.getTexture("tempProjectile"));
            projectiles.add(new Projectile(sprites, 0, player, mousePos.x, mousePos.y, playerTeam));
            sprites.clear();
            gameHUD.endTutorial();
        } 
        for(int i = projectiles.size - 1; i >= 0; i--) {
            projectiles.get(i).update(this);
        } 
        weather.update(this, elapsedTime, player.x, player.y);

        // Camera calculations based on player movement
        if(followPlayer) followPos = new Vector3(player.x, player.y, 0);
        if(Math.abs(game.camera.position.x - followPos.x) > 1f || Math.abs(game.camera.position.y - followPos.y) > 1f){
            game.camera.position.slerp(followPos, 0.1f);
        }

        // Call to pause the game
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && elapsedTime - lastPause > 0.1f){
            gamePause();
        }
        
        // Call to open the shop
        if(Gdx.input.isKeyJustPressed(Input.Keys.P) && elapsedTime - lastPause > 0.1f){
            gameShop();
        }
    }
    
    public void createPowerUp(float x, float y, String powerType) {
    	Array<Texture> powerupSprites = new Array<>();
    	switch(powerType) {
	    	case attackSpeed:
	    		powerupSprites.add(getMain().textureHandler.getTexture("UpAtkSpd"));
	    		break;
	    	case damageUp:
		    	powerupSprites.add(getMain().textureHandler.getTexture("UpDmg"));
		    	break;
	    	case healthUp:
	    		powerupSprites.add(getMain().textureHandler.getTexture("UpHealth"));
	    		break;
	    	case invincible:
	    		powerupSprites.add(getMain().textureHandler.getTexture("UpInvincible"));
	    		break;
	    	case speedUp:
	    		powerupSprites.add(getMain().textureHandler.getTexture("UpSpeed"));
	    		break;
    	}
    	PowerUps newPowerUp = (new PowerUps(powerupSprites, x, y, 0.2f, powerType));
        powerups.add(newPowerUp);
    }

    /**
     * Called to switch from the current screen to the pause screen, while retaining the current screen's information.
     */
    public void gamePause(){
        isPaused = true;
        game.setScreen(new PauseScreen(game,this));
    }
    
    public void gameShop() {
    	isPaused = true;
    	game.setScreen(new ShopScreen(game,this));
    }

    /**
     * Called to switch from the current screen to the end screen.
     * @param win   The boolean determining the win state of the game.
     */
    public void gameEnd(boolean win){
        game.setScreen(new EndScreen(game, this, win));
    }

    /**
     * Called to switch from the current screen to the title screen.
     */
    public void gameReset(){
        game.setScreen(new TitleScreen(game));
    }
    
    public void saveGame() {
    	JsonValue root = new JsonValue(ValueType.object);
    	
    	JsonValue general = new JsonValue(ValueType.object);
    	general.addChild("points", new JsonValue(points.Get()));
    	general.addChild("loot", new JsonValue(loot.Get()));
    	general.addChild("difficulty", new JsonValue(difficulty));
    	general.addChild("playerName", new JsonValue(playerName));
    	general.addChild("elapsedTime", new JsonValue(elapsedTime));
    	general.addChild("capturedCount", new JsonValue(College.capturedCount));
    	
    	root.addChild("general", general);
    	root.addChild("player", player.toJson());
    	
    	JsonValue jColleges = new JsonValue(ValueType.object);
    	for(College college : colleges) {
    		jColleges.addChild(college.collegeName, college.toJson());
    	}
    	root.addChild("colleges", jColleges);
    	
    	JsonValue proj = new JsonValue(ValueType.object);
    	for(int i = 0; i < projectiles.size; i++) {
    		proj.addChild(i + "", projectiles.get(i).toJson());
    	}
    	root.addChild("projectiles", proj);
    	
    	JsonValue pow = new JsonValue(ValueType.object);
    	for(int i = 0; i < powerups.size; i++) {
    		pow.addChild(i + "", powerups.get(i).toJson());
    	}
    	root.addChild("powerups", pow);
    	
    	FileHandle file = Gdx.files.local("savegame.json");
    	if(file.exists()) {
    		file.delete();
    	}
   
    	file.writeString(root.toJson(JsonWriter.OutputType.json), true);
    }
    
    public void loadGame() {
    	projectiles.clear();
    	powerups.clear();
    	JsonValue root = new JsonReader().parse(Gdx.files.local("savegame.json").readString());
    	JsonValue general = root.get("general");
    	points.Set(general.getInt("points"));
    	loot.Set(general.getInt("loot"));
    	difficulty = general.getInt("difficulty");
    	playerName = general.getString("playerName");
    	elapsedTime = general.getFloat("elapsedTime");
    	College.capturedCount = general.getInt("capturedCount");
    	player.fromJson(root.get("player"));
    	JsonValue college = root.get("colleges").child();
    	while(college != null) {
    		College c = getCollege(college.getString("collegeName"));
    		if(c != null) {
    			c.fromJson(this, college);
    		}
    		college = college.next();
    	}
    	Array<Texture> sprites = new Array<>();
        sprites.add(getMain().textureHandler.getTexture("tempProjectile"));
    	JsonValue projectile = root.get("projectiles").child();
    	while(projectile != null) {
    		projectiles.add(new Projectile(sprites, 0, projectile));
    		projectile = projectile.next();
    	}
    	JsonValue pow = root.get("powerups").child();
    	while(pow != null) {
    		createPowerUp(pow.getFloat("x"), pow.getFloat("y"), pow.getString("powerType"));
    		pow = pow.next();
    	}
    	game.camera.position.x = player.x;
    	game.camera.position.y = player.y;
    }

    /**
     * Used to encapsulate elapsedTime.
     * @return  Time since the current session started.
     */
    public float getElapsedTime() { return elapsedTime; }

    /**
     * Used to toggle whether the camera follows the player.
     * @param follow  Whether the camera will follow the player.
     */
    public void toggleFollowPlayer(boolean follow) { this.followPlayer = follow; }

    /**
     * Get the player's name for the current session.
     * @return  Player's name.
     */
    public String getPlayerName() { return playerName; }
    
    /**
     * Set the player's name.
     * @param playerName    Chosen player name.
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        gameHUD.updateName(this);
    }
    
    /**
     * Set the game difficulty.
     * @param difficulty    Chosen difficulty.
     */
    public void setDifficulty(Integer diff) {
        difficulty = diff;
    }
    
    /**
     * Get the game difficulty.
     * @param difficulty    Chosen difficulty.
     */
    public Integer getDifficulty() { return difficulty; }
    
    
    /**
     * Get the player.
     * @return  The player.
     */
    public Player getPlayer() { return player; }
    
    public College getCollege(String name) {
    	for(int i = 0; i < colleges.size; i++) {
    		if(colleges.get(i).collegeName.equalsIgnoreCase(name)) {
    			return colleges.get(i);
    		}
    	}
    	return null;
    }

    /**
     * Get the main game class.
     * @return  The main game class.
     */
    public YorkPirates getMain() { return game; }

    /**
     * Get the game's HUD.
     * @return  The HUD.
     */
    public HUD getHUD() { return gameHUD; }

    /**
     * Set whether the game is paused or not.
     * @param paused    Whether the game is paused.
     */
    public void setPaused(boolean paused) {
        if (!paused && isPaused) lastPause = elapsedTime;
        isPaused = paused;
    }

    /**
     * Gets whether the game is paused.
     * @return  True if the game is paused.
     */
    public boolean isPaused() { return  isPaused; }

    /**
     * Get the viewport.
     * @return  The viewport.
     */
    public FitViewport getViewport() { return viewport; }

    /**
     * Disposes of disposables when game finishes execution.
     */
    @Override
    public void dispose(){
        HUDBatch.dispose();
        tiledMap.dispose();
        music.dispose();
        sounds.dispose();
    }
}
