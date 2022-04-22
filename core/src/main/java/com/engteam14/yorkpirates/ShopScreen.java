package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx; 
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;

public class ShopScreen extends ScreenAdapter {

    private final YorkPirates game;
    private final GameScreen screen;
    private final Stage shopStage;
    private final Table shop;
    private final Table coinCount;
    private final Label loot;
    
        
    public ShopScreen(YorkPirates game, GameScreen screen){
        this.game = game;
        this.screen = screen;
                
        // Generate skin
        TextureAtlas atlas = game.textureHandler.getTextureAtlas("YorkPiratesSkin");
        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
        skin.addRegions(atlas);

        // Generate stage and table
        shopStage = new Stage(screen.getViewport());
        Gdx.input.setInputProcessor(shopStage);
        Table table = new Table();
        table.setFillParent(true);
        table.setTouchable(Touchable.enabled);
        table.setBackground(skin.getDrawable("Selection"));
        if(YorkPirates.DEBUG_ON) table.setDebug(true);
        
        // Create Gold Indicator
        Image coin = new Image(screen.getMain().textureHandler.loadTexture("loot", Gdx.files.internal("loot.png")));
        coin.setScaling(Scaling.fit);
        loot = new Label(screen.loot.GetString(), skin);
        loot.setFontScale(2f);
        
        // Create Shop Icons
        Image atkSpdIcon = new Image(screen.getMain().textureHandler.loadTexture("AtkSpdShop", Gdx.files.internal("ShopAtkSpd.png")));;
        atkSpdIcon.setScaling(Scaling.fit);
        if (Player.AtkSpdBought == true) {
        	System.out.println("Setting");
        	Texture atkSpdIconNew = (screen.getMain().textureHandler.loadTexture("AtkSpdShopBought", Gdx.files.internal("ShopSold.png")));;
        	atkSpdIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(atkSpdIconNew)));
        }
        Image atkDmgIcon = new Image(screen.getMain().textureHandler.loadTexture("AtkDmgShop", Gdx.files.internal("ShopAtkDmg.png")));;
        atkDmgIcon.setScaling(Scaling.fit);
        if (Player.AtkDmgBought == true) {
        	System.out.println("Setting");
        	Texture atkDmgIconNew = (screen.getMain().textureHandler.loadTexture("AtkDmgShopBought", Gdx.files.internal("ShopSold.png")));;
    		atkDmgIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(atkDmgIconNew)));
        }
        Image spdIcon = new Image(screen.getMain().textureHandler.loadTexture("SpdShop", Gdx.files.internal("ShopSpd.png")));;
        spdIcon.setScaling(Scaling.fit);
        if (Player.SpdBought == true) {
        	System.out.println("Setting");
        	Texture spdIconNew = (screen.getMain().textureHandler.loadTexture("SpdShopBought", Gdx.files.internal("ShopSold.png")));;
    		spdIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(spdIconNew)));
        }
        
        // Create Shop Buttons
        TextButton buyAtkSpd = new TextButton("Buy", skin);
        buyAtkSpd.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	screen.sounds.menu_button();
            	if (screen.loot.Get() >= 10 && Player.AtkSpdBought == false) {
            		screen.getPlayer().upgradeAttackSpeed(screen);
            		screen.loot.Add(-10);
            		Texture atkSpdIconNew = (screen.getMain().textureHandler.loadTexture("AtkSpdShopBought", Gdx.files.internal("ShopSold.png")));;
            		atkSpdIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(atkSpdIconNew)));
            	}
            }
        });
        TextButton buyAtkDmg = new TextButton("Buy", skin);
        buyAtkDmg.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	screen.sounds.menu_button();
            	if (screen.loot.Get() >= 10 && Player.AtkDmgBought == false) {
            		screen.getPlayer().upgradeAttackDamage(screen);
            		screen.loot.Add(-10);
            		Texture atkDmgIconNew = (screen.getMain().textureHandler.loadTexture("AtkDmgShopBought", Gdx.files.internal("ShopSold.png")));;
            		atkDmgIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(atkDmgIconNew)));
            	}
            }
        });
        TextButton buySpd = new TextButton("Buy", skin);
        buySpd.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	screen.sounds.menu_button();
            	if (screen.loot.Get() >= 10 && Player.SpdBought == false) {
            		screen.getPlayer().upgradeSpeed(screen);
            		screen.loot.Add(-10);
            		Texture spdIconNew = (screen.getMain().textureHandler.loadTexture("SpdShopBought", Gdx.files.internal("ShopSold.png")));;
            		spdIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(spdIconNew)));
            	}
            }
        });


        // Generate title texture
        Texture titleT = game.textureHandler.loadTexture("shop", Gdx.files.internal("shop.png"));
        Image title = new Image(titleT);
        title.setScaling(Scaling.fit);

        // Generate Exit button
        TextButton quit = new TextButton("Exit", skin);
        quit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
            	screen.sounds.menu_button();
                gameContinue();

            }
        });

        // Create shop table
        shop = new Table();
        shop.add(atkSpdIcon).pad(100);
        shop.add(atkDmgIcon).pad(100);
        shop.add(spdIcon).pad(100);
        shop.add().row();
        shop.add(buyAtkSpd).pad(25);
        shop.add(buyAtkDmg).pad(25);
        shop.add(buySpd).pad(25);
        if(YorkPirates.DEBUG_ON) shop.setDebug(true);
        
        coinCount = new Table();
        coinCount.add(coin).size(150).left().top().pad(25);
        coinCount.add(loot);
        if(YorkPirates.DEBUG_ON) coinCount.setDebug(true);
        
        // Add title to table
        table.row();
        table.add(coinCount).size(350).left().top();
        table.add(title).expand();
        table.add().expand();

        // Add shop icons to table
        table.row();
        table.add().expand();
        table.add(shop);
        
        // Add return to game button
        table.row();
        table.add().expand();
        table.add(quit).expand();

        // Add table to the stage
        shopStage.addActor(table);
    }

    /**
     * Is called once every frame. Runs update() and then renders the title screen.
     * @param delta The time passed since the previously rendered frame.
     */
    @Override
    public void render(float delta){
        Gdx.input.setInputProcessor(shopStage);
        update();
        ScreenUtils.clear(0.6f, 0.6f, 1.0f, 1.0f);
        screen.render(delta); // Draws the gameplay screen as a background
        shopStage.draw(); // Draws the stage
        
        loot.setText(screen.loot.GetString());
    }

    /**
     * Is called once every frame. Used for calculations that take place before rendering.
     */
    private void update(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            gameContinue();
        }
    }

    /**
     * Generates a HUD object within the game that controls elements of the UI.
     */
    private void gameContinue() {
        screen.setPaused(false);
        game.setScreen(screen);
    }
}
