package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends ScreenAdapter {
    private final YorkPirates game;
    private final GameScreen nextGame;
    private final Stage stage;

    private final TextField textBox;
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

        // Generate textbox
        textBox = new TextField("Name (optional)", skin, "edges");
        textBox.setAlignment(Align.center);
        textBox.setOnlyFontChars(true);
        textBox.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                textBox.setText("");
            }});

        // Generate buttons
        ImageTextButton startButton = new ImageTextButton("Play", skin);
        ImageTextButton quitButton = new ImageTextButton("Exit Game", skin, "Quit");

        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                gameStart();
            }
        });
        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.quit();
            }
        });

        // Add title to table
        titleCell = table.add(title).expand();

        // Add textbox to table
        table.row();
        Table textBoxFiller = new Table();
        textBoxFiller.add().expand().padRight(stage.getWidth()/3);
        textBoxFiller.add(textBox).expand().fillX();
        textBoxFiller.add().expand().padLeft(stage.getWidth()/3);
        if(YorkPirates.DEBUG_ON) textBoxFiller.debug();
        table.add(textBoxFiller).expand().fill();

        // Add buttons to table
        table.row();
        table.add(startButton).expand();
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
            gameStart();
        }
    }

    /**
     * Is called to create a new game screen.
     */
    private void gameStart(){
        // Get player name
        String playerName;
        if ( textBox.getText().equals("Name (optional)") || textBox.getText().equals("")) {
            playerName = "Player";

        } else{
            playerName = textBox.getText();
        }
        // Set player name and unpause game
        nextGame.setPaused(false);
        nextGame.setPlayerName(playerName);
        game.setScreen(nextGame);
    }
}
