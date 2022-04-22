package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends ScreenAdapter {
    private final YorkPirates game;
    private final GameScreen nextGame;
    private final Stage stage;

    private final Cell<Image> titleCell;

    private float elapsedTime = 0f;

    /**
     * Initialises the title screen, as well as relevant textures and data it may contain.
     * @param game  Passes in the base game class for reference.
     */
    public TitleScreen(YorkPirates game){
        this.game = game;

        // Generates main gameplay for use as background
        nextGame = new GameScreen(game);
        nextGame.setPaused(true);
        nextGame.setPlayerName("Player");

        // Generates skin
        TextureAtlas atlas = game.textureHandler.getTextureAtlas("YorkPiratesSkin");
        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
        skin.addRegions(atlas);

        // Generates stage and table
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();
        table.setFillParent(true);
        table.setBackground(skin.getDrawable("Selection"));
        if(YorkPirates.DEBUG_ON) table.setDebug(true);

        // Get title texture
        TextureRegion titleT = game.logo.getKeyFrame(0f);
        Image title = new Image(titleT);
        title.setScaling(Scaling.fit);

        // Generate buttons
        ImageTextButton startButton = new ImageTextButton("New Game", skin);
        ImageTextButton loadButton = new ImageTextButton("Load Game", skin);
        ImageTextButton quitButton = new ImageTextButton("Exit Game", skin, "Quit");

        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                newGameStart(false);
            }
        });
        
        loadButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                newGameStart(true);
            }
        });
        
        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.quit();
            }
        });

        // Add title to table
        titleCell = table.add(title).expand();

        // Add buttons to table
        table.row();
        table.add(startButton).expand();
        table.row();
        table.add(loadButton).expand();
        table.row();
        table.add(quitButton).expand();

        // Add table to the stage
        stage.addActor(table);
    }

    /**
     * Is called once every frame. Runs update() and then renders the title screen.
     * @param delta The time passed since the previously rendered frame.
     */
    @Override
    public void render(float delta){
        // Update values
        elapsedTime += delta;
        update();
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera.combined);

        // Render background
        ScreenUtils.clear(0f, 0f, 0f, 1.0f);
        nextGame.render(delta);

        // Animate title
        TextureRegion frame = game.logo.getKeyFrame(elapsedTime, true);
        titleCell.setActor(new Image(frame));

        // Draw UI over the top
        stage.draw();
    }

    /**
     * Is called once every frame to check for player input.
     */
    private void update(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            newGameStart(false);
        }
    }
    
    private void newGameStart(boolean loadGame) {
    	if(!loadGame) {
    		game.setScreen(new NewGameScreen(game));
    	} else {
    		GameScreen gs = new GameScreen(game);
    		gs.loadGame();
    		game.setScreen(gs);
    	}
    }
    
}
