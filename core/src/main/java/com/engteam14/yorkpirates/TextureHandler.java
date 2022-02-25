package com.engteam14.yorkpirates;

import java.util.HashMap;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class TextureHandler {
	
	private HashMap<String, Texture> textures;
	private HashMap<String, TextureAtlas> textureAtlas;
	
	public TextureHandler() {
		textures = new HashMap<String, Texture>();
		textureAtlas = new HashMap<String, TextureAtlas>();
	}
	
	public Texture loadTexture(String key, FileHandle file) {
		Texture tex = new Texture(file);
		if(textures.containsKey(key)) {
			textures.get(key).dispose();
		}
		textures.put(key, tex);
		return tex;
	}
	
	public void unloadTexture(String key) {
		if(textures.containsKey(key)) {
			textures.get(key).dispose();
		}
	}
	
	public Texture getTexture(String key) {
		return textures.get(key);
	}
	
	public TextureAtlas loadTextureAtlas(String key, FileHandle file) {
		TextureAtlas texA = new TextureAtlas(file);
		if(textureAtlas.containsKey(key)) {
			textureAtlas.get(key).dispose();
		}
		textureAtlas.put(key, texA);
		return texA;
	}
	
	public void unloadTextureAtlas(String key) {
		if(textureAtlas.containsKey(key)) {
			textureAtlas.get(key).dispose();
		}
	}
	
	public TextureAtlas getTextureAtlas(String key) {
		return textureAtlas.get(key);
	}
	
	public void dispose() {
		for(Texture tex : textures.values()) {
			tex.dispose();
		}
		for(TextureAtlas texA : textureAtlas.values()) {
			texA.dispose();
		}
		textures.clear();
		textureAtlas.clear();
	}
}
