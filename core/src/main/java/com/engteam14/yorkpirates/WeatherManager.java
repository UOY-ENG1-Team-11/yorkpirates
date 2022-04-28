package com.engteam14.yorkpirates;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Array;

public class WeatherManager {
	
	public boolean badWeather = false;
	public Array<Enemy_Wave> waves;
	private float lastWeatherChange = 0;
	
	private Random rand;
	
	public WeatherManager() {
		waves = new Array<Enemy_Wave>();
		rand = new Random();
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
		Array<Texture> sprites = new Array<Texture>();
		sprites.add(screen.getMain().textureHandler.getTexture("enemyWave"));
		Enemy_Wave wave = new Enemy_Wave(sprites, 0, screen.player, x, y);
		waves.add(wave);
		return wave;
	}
}
