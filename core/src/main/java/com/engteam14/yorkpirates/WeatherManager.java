package com.engteam14.yorkpirates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Random;

//WeatherManeger is part of new requirement: UR.WEATHER
public class WeatherManager {

    public boolean badWeather = false;
    public static boolean weatherPass = false;
    public Array<Enemy_Wave> waves;
    private float lastWeatherChange = 0;
    private final Array<Texture> waveTex;

    private final Random rand;

    /**
     * Creates the weather manager for waves and the current weather.
     *
     * @param waveTex The texture to use for waves spawned.
     */
    public WeatherManager(Array<Texture> waveTex) {
        waves = new Array<Enemy_Wave>();
        rand = new Random();
        this.waveTex = waveTex;
    }

    /**
     * Called once per frame. Used to update waves and the weather state.
     *
     * @param screen      The main game screen.
     * @param elapsedTime The current time the game has been running for.
     * @param playerX     The player's x-coordinate.
     * @param playerY     The player's y-coordinate.
     */
    public void update(GameScreen screen, float elapsedTime, float playerX, float playerY) {
        for (Enemy_Wave wave : waves) {
            wave.update(screen, badWeather);
        }
        if (!badWeather) {
            if (rand.nextInt(130) == 1) { //Randomly create new waves
                float xOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
                float yOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
                createWave(screen.player, playerX + xOffset, playerY + yOffset);
            }
            //After 30 seconds of good weather, randomly change to bad
            if (elapsedTime > lastWeatherChange + 30 && rand.nextInt(500) == 1) {
                badWeather = true;
                weatherPass = true;
                lastWeatherChange = elapsedTime;
                TextureAtlas atlas = screen.getMain().textureHandler.getTextureAtlas("YorkPiratesSkin");
                Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
                skin.addRegions(atlas);
                screen.getHUD().table.setBackground(skin.getDrawable("Selection"));
            }
        } else {
            if (rand.nextInt(50) == 1) { //In bad weather randomly create waves at a higher rate
                float xOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
                float yOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
                createWave(screen.player, playerX + xOffset, playerY + yOffset);
            }
            //After 10 seconds of bad weather, randomly change back
            if (elapsedTime > lastWeatherChange + 10 && rand.nextInt(50) == 1) {
                badWeather = false;
                weatherPass = false;
                lastWeatherChange = elapsedTime;
                screen.getHUD().table.setBackground(new BaseDrawable());
            }
        }

    }

    /**
     * Called to draw the waves.
     *
     * @param batch       The batch to draw the waves with.
     * @param elapsedTime The current time the game has been running for.
     */
    public void draw(SpriteBatch batch, float elapsedTime) {
        for (Enemy_Wave wave : waves) {
            wave.draw(batch, elapsedTime);
        }
    }

    /**
     * Creates a wave.
     *
     * @param player The player.
     * @param x      The x-coordinate to spawn the wave at.
     * @param y      The y-coordinate to spawn the wave at.
     */
    public Enemy_Wave createWave(Player player, float x, float y) {
        Enemy_Wave wave = new Enemy_Wave(waveTex, 0, player, x, y);
        waves.add(wave);
        return wave;
    }


    /**
     * Saves all the weather manager's properties in JSON format.
     *
     * @return A JsonValue containing all the weather manager's properties.
     */
    public JsonValue toJson() {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        json.addChild("badWeather", new JsonValue(badWeather));
        json.addChild("weatherPass", new JsonValue(weatherPass));
        json.addChild("lastWeatherChange", new JsonValue(lastWeatherChange));
        JsonValue jWaves = new JsonValue(JsonValue.ValueType.object);
        for (int i = 0; i < waves.size; i++) {
            jWaves.addChild(i + "", waves.get(i).toJson());
        }
        json.addChild("waves", jWaves);
        return json;
    }

    /**
     * Sets all properties to those contained in the passed JsonValue.
     *
     * @param json The root JsonValue containing the weather manager properties.
     */
    public void fromJson(GameScreen screen, JsonValue json) {
        waves.clear();
        badWeather = json.getBoolean("badWeather");
        weatherPass = json.getBoolean("weatherPass");
        if (screen != null) {
            if (badWeather) {
                TextureAtlas atlas = screen.getMain().textureHandler.getTextureAtlas("YorkPiratesSkin");
                Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
                skin.addRegions(atlas);
                screen.getHUD().table.setBackground(skin.getDrawable("Selection"));
            } else {
                screen.getHUD().table.setBackground(new BaseDrawable());
            }
        }
        lastWeatherChange = json.getFloat("lastWeatherChange");
        JsonValue wave = json.get("waves").child();
        while (wave != null) {
            waves.add(new Enemy_Wave(waveTex, 0, wave));
            wave = wave.next();
        }
    }
}
