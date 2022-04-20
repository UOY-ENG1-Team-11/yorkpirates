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

public class NewGameScreen extends ScreenAdapter {
    private final YorkPirates game;
    private final GameScreen nextGame;
    private final Stage stage;

    private final TextField textBox;
    private final Table difficulties;
    private final Table buttons;
    private final Label title;
    private final Label reminder;

    private boolean difficultyChosen = false;
    private static int difficulty;
    
    /**
     * Initialises the title screen, as well as relevant textures and data it may contain.
     * @param game  Passes in the base game class for reference.
     */
    public NewGameScreen(YorkPirates game){
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

        // Generate textbox
        textBox = new TextField("Name (optional)", skin, "edges");
        textBox.setAlignment(Align.center);
        textBox.setOnlyFontChars(true);
        textBox.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                textBox.setText("");
            }});

        // Generate Title
        title = new Label("New Game Creation" , skin);
        
        // Generate difficulty reminder
        reminder = new Label("Please Select A Difficulty", skin);
        reminder.setVisible(false);
        
        // Generate buttons
        ImageTextButton startButton = new ImageTextButton("Start Game", skin);
        ImageTextButton backButton = new ImageTextButton("Back", skin);
        
        ImageTextButton easyButton = new ImageTextButton("Easy", skin);
        ImageTextButton normalButton = new ImageTextButton("Normal", skin);
        ImageTextButton hardButton = new ImageTextButton("Hard", skin);
        
        startButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (difficultyChosen == true) {
                	gameStart();
                }
                else {
                	reminder.setVisible(true);
                }
            }
        });
          
        easyButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	if (difficultyChosen == true) {
            		difficultyChosen = false;
            		normalButton.setVisible(true);
            		hardButton.setVisible(true);
            	}
            	else {
            		difficulty = 1;
            		difficultyChosen = true;
            		reminder.setVisible(false);
            		normalButton.setVisible(false);
            		hardButton.setVisible(false);
            	}
            }
        });
        
        normalButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	if (difficultyChosen == true) {
            		difficultyChosen = false;
            		normalButton.setVisible(true);
            		hardButton.setVisible(true);
            	}
            	else {
            		difficulty = 2;
            		difficultyChosen = true;
            		reminder.setVisible(false);
            		easyButton.setVisible(false);
            		hardButton.setVisible(false);
            	}
            }
        });
        
        hardButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	if (difficultyChosen == true) {
            		difficultyChosen = false;
            		easyButton.setVisible(true);
            		normalButton.setVisible(true);
            	}
            	else {
            		difficulty = 4;
            		difficultyChosen = true;
            		reminder.setVisible(false);
            		easyButton.setVisible(false);
            		normalButton.setVisible(false);
            	}
            }
        });
           
        
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	game.setScreen(new TitleScreen(game));
            }
        });

        // Create difficulty selection subtable
        difficulties = new Table();
        difficulties.add(easyButton).pad(100);
        difficulties.add(normalButton).pad(100);
        difficulties.add(hardButton).pad(100);
        difficulties.add().row();
        difficulties.add().expand();
        difficulties.add(reminder);
        
        // Create button selection subtable
        buttons = new Table();
        buttons.add(startButton).pad(25).expand();
        buttons.add(backButton).pad(25).expand();
        
        // starts filling in the table
        table.row();
        table.add(title).pad(100);
        
        // Add textbox to table
        table.row();
        Table textBoxFiller = new Table();
        textBoxFiller.add().expand().padRight(stage.getWidth()/3);
        textBoxFiller.add(textBox).expand().fillX();
        textBoxFiller.add().expand().padLeft(stage.getWidth()/3);
        if(YorkPirates.DEBUG_ON) textBoxFiller.debug();
        table.add(textBoxFiller).expand().fill();

        // adds difficulties to table
        table.row();
        table.add(difficulties).expand();
        
        // Add buttons to table
        table.row();
        table.add(buttons).pad(100);

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
        update();

        // Render background
        ScreenUtils.clear(0f, 0f, 0f, 1.0f);
        nextGame.render(delta);

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
        nextGame.sounds.menu_button();
        nextGame.setPlayerName(playerName);
        nextGame.setDifficulty(difficulty);
        game.setScreen(nextGame);
    }
    
}
