package com.engteam14.yorkpirates;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;

public class WeatherManager {
	
	public boolean badWeather = false;
	public Array<Enemy_Wave> waves;
	private float lastWeatherChange = 0;
	private Array<Texture> waveTex;
	
	private Random rand;
	
	public WeatherManager(Array<Texture> waveTex) {
		waves = new Array<Enemy_Wave>();
		rand = new Random();
		this.waveTex = waveTex;
	}
	
	public void update(GameScreen screen, float elapsedTime, float playerX, float playerY) {
		for(Enemy_Wave wave : waves) {
			wave.update(screen, badWeather);
		}
		if(!badWeather) {
			if(rand.nextInt(130) == 1) {
				float xOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
				float yOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
				createWave(screen, playerX + xOffset, playerY + yOffset);
			}
			if(elapsedTime > lastWeatherChange + 30 && rand.nextInt(500) == 1) {
				badWeather = true;
				lastWeatherChange = elapsedTime;
				TextureAtlas atlas = screen.getMain().textureHandler.getTextureAtlas("YorkPiratesSkin");
		        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
		        skin.addRegions(atlas);
				screen.getHUD().table.setBackground(skin.getDrawable("Selection"));
			}
		} else {
			if(rand.nextInt(70) == 1) {
				float xOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
				float yOffset = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 100);
				createWave(screen, playerX + xOffset, playerY + yOffset);
			}
			if(elapsedTime > lastWeatherChange + 10 && rand.nextInt(50) == 1) {
				badWeather = false;
				lastWeatherChange = elapsedTime;
				screen.getHUD().table.setBackground(new BaseDrawable());
			}
		}
		
	}
	
	public void draw(SpriteBatch batch, float elapsedTime) {
		for(Enemy_Wave wave : waves) {
			wave.draw(batch, elapsedTime);
		}
	}
	
	public Enemy_Wave createWave(GameScreen screen, float x, float y) {	
		Enemy_Wave wave = new Enemy_Wave(waveTex, 0, screen.player, x, y);
		waves.add(wave);
		return wave;
	}
	
	public JsonValue toJson() {
    	JsonValue json = new JsonValue(JsonValue.ValueType.object);
    	json.addChild("badWeather", new JsonValue(badWeather));
    	json.addChild("lastWeatherChange", new JsonValue(lastWeatherChange));
    	JsonValue jWaves = new JsonValue(JsonValue.ValueType.object);
    	for(int i = 0; i < waves.size; i++) {
    		jWaves.addChild(i + "", waves.get(i).toJson());
    	}
    	json.addChild("waves", jWaves);
    	return json;
    }
	
	public void fromJson(GameScreen screen, JsonValue json) {
		waves.clear();
		badWeather = json.getBoolean("badWeather");
		if(badWeather) {
			TextureAtlas atlas = screen.getMain().textureHandler.getTextureAtlas("YorkPiratesSkin");
	        Skin skin = new Skin(Gdx.files.internal("Skin/YorkPiratesSkin.json"), atlas);
	        skin.addRegions(atlas);
			screen.getHUD().table.setBackground(skin.getDrawable("Selection"));
		} else {
			screen.getHUD().table.setBackground(new BaseDrawable());
		}
		lastWeatherChange = json.getFloat("lastWeatherChange");
		JsonValue wave = json.get("waves").child();
		while(wave != null) {
			waves.add(new Enemy_Wave(waveTex, 0, wave));
			wave = wave.next();
		}
	}
}
