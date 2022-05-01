package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;

public class HUD {

    // Stage
    public Stage stage;
    public final Table table;

    // Tutorial
    private final Table tutorial;
    private final Cell<Image> tutorialImg;
    private final Label tutorialLabel;
    private boolean tutorialComplete = false;
    private boolean canEndGame = false;

    public static boolean shopTutorialComplete = false;

    // Player counters
    private final Label score;
    private final Label loot;
    
    // Power-ups
    private final Table powerupsTable;
    private final Table AtkSpdTable;
    private final Label AtkSpdTimer;
    private final Table AtkDmgTable;
    private final Label AtkDmgTimer;
    private final Table InvincibleTable;
    private final Label InvincibleTimer;
    private final Table SpeedTable;
    private final Label SpeedTimer;

    // Weather

    private final Table weatherTable;
    private final Table topLeft;

    // Player tasks
    private final Label tasksTitle;
    private final CheckBox collegesTask;
    private final CheckBox movementTask;
    private final CheckBox pointsTask;

    private final int DISTANCE_GOAL = MathUtils.random(55,65)*10;
    private final int POINT_GOAL = MathUtils.random(13,18)*10;

    private final int DISTANCE_REWARD = MathUtils.random(17,23);
    private final int POINT_REWARD = MathUtils.random(13,17);

    /**
     * Generates a HUD object within the game that controls elements of the UI.
     * @param screen    The game screen which this is attached to.
     */
    public HUD(GameScreen screen){
        // Generate skin
        TextureAtlas atlas = screen.getMain().textureHandler.getTextureAtlas("YorkPiratesSkin");
        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
        skin.addRegions(atlas);

        // Generate stage and table
        stage = new Stage(screen.getViewport());
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.setTouchable(Touchable.enabled);
        if(YorkPirates.DEBUG_ON) table.setDebug(true);

        // Create menu button
        ImageButton menuButton = new ImageButton(skin, "Menu");

        menuButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	screen.sounds.menu_button();
                screen.gamePause();
            }
        });

        // Create Weather Box (New Requirement)
        Image badWeather = new Image(screen.getMain().textureHandler.loadTexture("stormWeather", Gdx.files.internal("badWeather.png")));

        // Create tutorial actors
        Image tutorialImg = new Image(screen.getMain().keyboard.getKeyFrame(0f));
        tutorialImg.setScaling(Scaling.fit);
        tutorialLabel = new Label("WASD or Arrow Keys\n to Move.", skin);

        // Create score related actors
        Image coin = new Image(screen.getMain().textureHandler.loadTexture("gold", Gdx.files.internal("loot.png")));
        Image star = new Image(screen.getMain().textureHandler.loadTexture("points", Gdx.files.internal("points.png")));
        coin.setScaling(Scaling.fit);
        star.setScaling(Scaling.fit);
        loot = new Label(screen.loot.GetString(), skin);
        score = new Label(screen.points.GetString(), skin);
        loot.setFontScale(1.2f);
        score.setFontScale(1.2f);
        
        // Create power-up related actors (New Requirement)
        Image AtkSpd = new Image(screen.getMain().textureHandler.loadTexture("AtkSpd", Gdx.files.internal("UpAtkSpd.png")));
        Image AtkDmg = new Image(screen.getMain().textureHandler.loadTexture("AtkDmg", Gdx.files.internal("UpDmg.png")));
        Image Invincible = new Image(screen.getMain().textureHandler.loadTexture("Invincible", Gdx.files.internal("UpInvincible.png")));
        Image Speed = new Image(screen.getMain().textureHandler.loadTexture("Speed", Gdx.files.internal("UpSpeed.png")));
        
        AtkSpd.setScaling(Scaling.fit);
        AtkDmg.setScaling(Scaling.fit);
        Invincible.setScaling(Scaling.fit);
        Speed.setScaling(Scaling.fit);
        
        AtkSpdTimer = new Label(screen.AtkSpdTimer.GetString(), skin);
        AtkDmgTimer = new Label(screen.AtkDmgTimer.GetString(), skin);
        InvincibleTimer = new Label(screen.InvincibleTimer.GetString(), skin);
        SpeedTimer = new Label(screen.SpeedTimer.GetString(), skin);
        
        AtkSpdTimer.setFontScale(1.2f);
        AtkDmgTimer.setFontScale(1.2f);
        InvincibleTimer.setFontScale(1.2f);
        SpeedTimer.setFontScale(1.2f);
        
        
        // Create task related actors
        tasksTitle = new Label(screen.getPlayerName() + "'s Tasks:", skin);
        tasksTitle.setFontScale(0.5f, 0.5f);
        collegesTask = new CheckBox("", skin);
        movementTask = new CheckBox("", skin);
        pointsTask = new CheckBox("", skin);
        collegesTask.setChecked(true);
        movementTask.setChecked(true);
        pointsTask.setChecked(true);
        collegesTask.setDisabled(true);
        movementTask.setDisabled(true);
        pointsTask.setDisabled(true);

        // Create player tracker
        Table tracker = new Table();
        tracker.setBackground(new TextureRegionDrawable(new TextureRegion(screen.getMain().textureHandler.loadTexture("transparent", Gdx.files.internal("transparent.png")))));
        if(YorkPirates.DEBUG_ON) tracker.debug();

        // Add score to player tracker
        Table scores = new Table();
        scores.add(star).padRight(20);
        scores.add(score).padRight(20);
        scores.add(coin).padRight(20);
        scores.add(loot).padRight(20);
        if(YorkPirates.DEBUG_ON) scores.setDebug(true);
        tracker.add(scores);

        // Add tasks to player tracker
        tracker.row();
        tracker.add(tasksTitle).pad(1);
        tracker.row();
        tracker.add(collegesTask).left().pad(5);
        tracker.row();
        tracker.add(movementTask).left().pad(5);
        tracker.row();
        tracker.add(pointsTask).left().pad(5);

        // Add power-up display (New Requirement)
        
        AtkSpdTable = new Table();
        AtkSpdTable.row();
        AtkSpdTable.add(AtkSpd).padRight(20);
        AtkSpdTable.add(AtkSpdTimer).padRight(20);
        if(YorkPirates.DEBUG_ON) AtkSpdTable.setDebug(true);
        
        AtkDmgTable = new Table();
        AtkDmgTable.row();
        AtkDmgTable.add(AtkDmg).padRight(20);
        AtkDmgTable.add(AtkDmgTimer).padRight(20);
        if(YorkPirates.DEBUG_ON) AtkDmgTable.setDebug(true);
        
        InvincibleTable = new Table();
        InvincibleTable.row();
        InvincibleTable.add(Invincible).padRight(20);
        InvincibleTable.add(InvincibleTimer).padRight(20);
        if(YorkPirates.DEBUG_ON) InvincibleTable.setDebug(true);
        
        SpeedTable = new Table();
        SpeedTable.row();
        SpeedTable.add(Speed).padRight(20);
        SpeedTable.add(SpeedTimer).padRight(20);
        if(YorkPirates.DEBUG_ON) SpeedTable.setDebug(true);
        
        // Create tutorial placeholder
        tutorial = new Table();
        tutorial.setBackground(tracker.getBackground());
        this.tutorialImg = tutorial.add(tutorialImg).expand().fill().minSize(200f).maxSize(500f);
        tutorial.row();
        tutorial.add(tutorialLabel);
        if(YorkPirates.DEBUG_ON) tutorial.setDebug(true);

        // Create Weather Table (New Requirement)
        weatherTable = new Table();
        weatherTable.add(badWeather).size(150);

        // Create Top Left Table
        topLeft = new Table();
        topLeft.add(menuButton).size(150).left().top().pad(25);
        topLeft.add(weatherTable).size(150).pad(25);


        // Start main table

        // Add menu button to table
        table.row();
        table.add(topLeft).left().top().pad(25);
        
        // Add power-ups to table
        powerupsTable = new Table();
        table.add().expand();
        powerupsTable.add(AtkSpdTable).right().top();
        powerupsTable.add(InvincibleTable).right().top();
        powerupsTable.row();
        powerupsTable.add(AtkDmgTable).right().top();
        powerupsTable.add(SpeedTable).right().top();
        table.add(powerupsTable).right().top();
        
        // Add tutorial to table
        table.row();
        table.add(tutorial.pad(100f));

        // Add tracker to table
        table.add().expand();
        table.add(tracker);


        
        // Add table to the stage
        stage.addActor(table);
    }

    /**
     * Called to render the HUD elements
     * @param screen    The game screen which this is attached to.
     */
    public void renderStage(GameScreen screen){
        Gdx.input.setInputProcessor(stage);
        stage.draw();
        
        // Update the score and loot
        score.setText(screen.points.GetString());
        loot.setText(screen.loot.GetString());

        // Update the powerups timer
        AtkSpdTimer.setText(screen.AtkSpdTimer.GetString());
        AtkDmgTimer.setText(screen.AtkDmgTimer.GetString());
        InvincibleTimer.setText(screen.InvincibleTimer.GetString());
        SpeedTimer.setText(screen.SpeedTimer.GetString());
        
        // Calculate which part of the tutorial to show
        if(screen.getPlayer().getDistance() < 2){
            // Movement tutorial
            Image newimg = new Image(screen.getMain().keyboard.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialComplete = false;
        } else if(!tutorialComplete){
            // Shooting tutorial
            Image newimg = new Image(screen.getMain().mouse.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialLabel.setText("Click to shoot.");
        }
        else if (screen.loot.Get() >= 10 && shopTutorialComplete != true) { // New Requirement
            tutorial.setVisible(true);
            tutorialLabel.setText("Open the shop via the menu or press P");
        }else if(canEndGame) {
            // Able to end the game
            tutorial.setVisible(true);
            Image newimg = new Image(screen.getMain().enter.getKeyFrame(screen.getElapsedTime(), true));
            newimg.setScaling(Scaling.fit);
            tutorialImg.setActor(newimg);
            tutorialLabel.setText("Press Enter to end game.");
            canEndGame = false;
        } else {
            // Tutorial complete
            tutorial.setVisible(false);
        }

        // Decide on and then display main player goal
        if(College.capturedCount >= screen.colleges.size-1){
            collegesTask.setText("Return home to win.");
        } else {
            collegesTask.setText("Capture all colleges:  "+Math.min(College.capturedCount, screen.colleges.size-1)+"/"+(screen.colleges.size-1)+"  ");
        }

        // Distance related task calculations
        if(screen.getPlayer().getDistance() > DISTANCE_GOAL && movementTask.isChecked()) { screen.loot.Add(DISTANCE_REWARD); }
        movementTask.setChecked(screen.getPlayer().getDistance() < DISTANCE_GOAL);
        movementTask.setText("Move "+DISTANCE_GOAL+"m:  "+Math.min((int)(screen.getPlayer().getDistance()), DISTANCE_GOAL)+"/"+DISTANCE_GOAL+"  ");

        // Points related task calculations
        if(screen.points.Get() > POINT_GOAL && pointsTask.isChecked()) { screen.loot.Add(POINT_REWARD); }
        pointsTask.setChecked(screen.points.Get() < POINT_GOAL);
        pointsTask.setText("Get "+POINT_GOAL+" points:  "+Math.min(screen.points.Get(), POINT_GOAL)+"/"+POINT_GOAL+"  ");
        
        // Attack Speed Timer Calls
        if(Player.atkSpdTime > 0) {
        	AtkSpdTimer.setVisible(true);
        	AtkSpdTable.setVisible(true);
        	screen.AtkSpdTimer.startTimer();
        } else {
        	AtkSpdTimer.setVisible(false);
        	AtkSpdTable.setVisible(false);
        }
        
        // Attack Damage Timer Calls
        if(Player.dmgUpTime > 0) {
        	AtkDmgTimer.setVisible(true);
        	AtkDmgTable.setVisible(true);
        	screen.AtkDmgTimer.startTimer();
        } else {
        	AtkDmgTimer.setVisible(false);
        	AtkDmgTable.setVisible(false);
        }
        
        // Invincible Timer Calls
        if(Player.invincibleTime > 0) {
        	InvincibleTimer.setVisible(true);
        	InvincibleTable.setVisible(true);
        	screen.InvincibleTimer.startTimer();
        } else {
        	InvincibleTimer.setVisible(false);
        	InvincibleTable.setVisible(false);
        }
        
        // Attack Damage Timer Calls
        if(Player.speedUpTime > 0) {
        	SpeedTimer.setVisible(true);
        	SpeedTable.setVisible(true);
        	screen.SpeedTimer.startTimer();
        } else {
        	SpeedTimer.setVisible(false);
        	SpeedTable.setVisible(false);
        }

        // New Requirement
        if(WeatherManager.weatherPass == true){
            weatherTable.setVisible(true);
        } else {
            weatherTable.setVisible(false);
        }
    }

    public void updateName(GameScreen screen) { tasksTitle.setText(screen.getPlayerName() +"'s Tasks:"); }

    public void endTutorial() { tutorialComplete = true; }

    public void setGameEndable() {canEndGame = true; }
}
