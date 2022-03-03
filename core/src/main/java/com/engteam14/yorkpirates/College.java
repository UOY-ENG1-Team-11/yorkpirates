package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Objects;

import static java.lang.Math.abs;

public class College extends GameObject {

    public static int capturedCount = 0;

    private HealthBar collegeBar;
    private Indicator direction;

    private float splashTime;
    private long lastShotFired;
    private final String collegeName;
    private final Array<Texture> collegeImages;
    private Array<Texture> boatTexture;
    public Array<Boat> boats;
    private Array<Float> boatRotations;

    private boolean doBloodSplash = false;

    /**
     * Generates a college object within the game with animated frame(s) and a hit-box.
     * @param x         The x coordinate within the map to initialise the object at.
     * @param y         The y coordinate within the map to initialise the object at.
     * @param name      The name of the college.
     * @param team      The team the college is on.
     */
    public College(YorkPirates game, Array<Texture> sprites, float x, float y, float scale, String name, String team, Player player, Texture boatTexture){
        super(sprites, 0, x, y, sprites.get(0).getWidth()*scale, sprites.get(0).getHeight()*scale, team);

        this.boatTexture = new Array<>();
        this.boats = new Array<>();
        this.boatRotations = new Array<>();
        this.boatTexture.add(boatTexture);
        collegeImages = new Array<>();
        for(int i = 0; i < sprites.size; i++) {
            collegeImages.add(sprites.get(i));
        }

        splashTime = 0;
        setMaxHealth(2000);
        lastShotFired = 0;
        collegeName = name;

        Array<Texture> healthBarSprite = new Array<>();
        Array<Texture> indicatorSprite = new Array<>();
        if(Objects.equals(team, GameScreen.playerTeam)){
            if(Objects.equals(name, "Home")){
                indicatorSprite.add(game.textureHandler.getTexture("homeArrow"));
            }else{
                indicatorSprite.add(game.textureHandler.getTexture("allyArrow"));
            }
            healthBarSprite.add(game.textureHandler.getTexture("allyHealthBar"));

        }else{
            healthBarSprite.add(game.textureHandler.getTexture("enemyHealthBar"));
            indicatorSprite.add(game.textureHandler.getTexture("questArrow"));
        }
        collegeBar = new HealthBar(this,healthBarSprite);
        direction = new Indicator(this,player,indicatorSprite);
    }

    /**
     * Called once per frame. Used to perform calculations such as collision.
     * @param screen    The main game screen.
     */
    public void update(GameScreen screen){
        direction.move();
        float playerX = screen.getPlayer().x;
        float playerY = screen.getPlayer().y;
        boolean nearPlayer = abs(this.x - playerX) < (Gdx.graphics.getWidth()/15f) && abs(this.y - playerY) < (Gdx.graphics.getHeight()/10f);
        
        if(nearPlayer || screen.isPaused()){
            direction.setVisible(false);

            if(!Objects.equals(team, GameScreen.playerTeam)) { // Checks if the college is an enemy of the player
                // How often the college can shoot.
                int shootFrequency = 1000;
                if (TimeUtils.timeSinceMillis(lastShotFired) > shootFrequency){
                    lastShotFired = TimeUtils.millis();
                    Array<Texture> sprites = new Array<>();
                    sprites.add(screen.getMain().textureHandler.getTexture("tempProjectile"));
                    screen.projectiles.add(new Projectile(sprites, 0, this, playerX, playerY, team));
                    screen.sounds.cannon();
                }
            }else if(Objects.equals(collegeName, "Home")){
                boolean victory = true;
                for(int i = 0; i < screen.colleges.size; i++) {
                    if(!Objects.equals(screen.colleges.get(i).team, GameScreen.playerTeam)){
                        victory = false;
                    }
                }
                if(victory){
                    screen.getHUD().setGameEndable();
                    if(Gdx.input.isKeyPressed(Input.Keys.ENTER)) screen.gameEnd(true);
                }
            }
        }else{
            direction.setVisible(true);
        }

        if(doBloodSplash){
            if(splashTime > 1){
                doBloodSplash = false;
                splashTime = 0;
            }else{
                splashTime += 1;
            }
        }
    }

    /**
     * Called when a projectile hits the college.
     * @param screen            The main game screen.
     * @param damage            The damage dealt by the projectile.
     * @param projectileTeam    The team of the projectile.
     */
    @Override
    public void takeDamage(GameScreen screen, float damage, String projectileTeam){
        currentHealth -= damage;
        doBloodSplash = true;

        if(currentHealth > 0){
            collegeBar.resize(currentHealth);
            screen.sounds.damage();
        }else{
            if(!Objects.equals(team, GameScreen.playerTeam)){ // Checks if the college is an enemy of the player
                // College taken over
            	screen.sounds.death();
                int pointsGained = 50;
                screen.points.Add(pointsGained);
                int lootGained = 15;
                screen.loot.Add(lootGained);

                Array<Texture> healthBarSprite = new Array<>();
                Array<Texture> indicatorSprite = new Array<>();
                healthBarSprite.add(screen.getMain().textureHandler.getTexture("allyHealthBar"));
                indicatorSprite.add(screen.getMain().textureHandler.getTexture("allyArrow"));
                boatTexture.clear();
                boatTexture.add(screen.getPlayer().anim.getKeyFrame(0f));

                Array<Texture> sprites = new Array<>();
                sprites.add(collegeImages.get(1));
                changeImage(sprites,0);

                collegeBar.changeImage(healthBarSprite,0);
                currentHealth = maxHealth;
                collegeBar.resize(currentHealth);
                College.capturedCount++;
                direction.changeImage(indicatorSprite,0);
                team = GameScreen.playerTeam;
            }else{
                // Destroy college
                collegeBar = null;
                direction = null;
                destroy(screen);
            }
        }
    }

    /**
     * Called when the college needs to be destroyed.
     * @param screen    The main game screen.
     */
    private void destroy(GameScreen screen){
        screen.colleges.removeValue(this,true);
    }

    /**
     * Called when drawing the object.
     * @param batch         The batch to draw the object within.
     * @param elapsedTime   The current time the game has been running for.
     */
    @Override
    public void draw(SpriteBatch batch, float elapsedTime){
        if(doBloodSplash)   batch.setShader(shader); // Set red shader to the batch
        else                batch.setShader(null);

        // Draw college
        batch.draw(anim.getKeyFrame(elapsedTime, true), x - width/2, y - height/2, width, height);

        // Draw boats before college so under
        batch.setShader(null);
        for(int i = 0; i < boats.size; i++){
            GameObject boat = boats.get(i);
            batch.draw(boatTexture.get(0), boat.x+boat.height, boat.y, 0,0, boat.width, boat.height, 1f, 1f, boatRotations.get(i), 0, 0, boatTexture.get(0).getWidth(), boatTexture.get(0).getHeight(), false, false);
        }

        collegeBar.draw(batch, 0);
        direction.draw(batch,0);
    }

    /**
     * Add a boat to this college.
     * @param x The x position of the new boat relative to the college.
     * @param y The y position of the new boat relative to the college.
     */
    public void addBoat(float x, float y, float rotation){
        boats.add(new Boat(boatTexture, 0, this.x+x, this.y+y, 25, 12, team));
        boatRotations.add(rotation);
    }
}
