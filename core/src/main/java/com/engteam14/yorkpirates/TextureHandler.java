package com.engteam14.yorkpirates;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.HashMap;

public class TextureHandler {

    private HashMap<String, Texture> textures;
    private HashMap<String, TextureAtlas> textureAtlas;

    /**
     * Creates a handler for all textures loaded in the game.
     */
    public TextureHandler() {
        textures = new HashMap<String, Texture>();
        textureAtlas = new HashMap<String, TextureAtlas>();
    }

    /**
     * Loads a texture from file and stores it.
     *
     * @param key  The name to save the texture under.
     * @param file The file the texture is stored in.
     */
    public Texture loadTexture(String key, FileHandle file) {
        Texture tex = new Texture(file);
        if (textures.containsKey(key)) {
            textures.get(key).dispose();
        }
        textures.put(key, tex);
        return tex;
    }

    /**
     * Removes a texture from the saved list and disposes of it .
     *
     * @param key The name of the texture to unload.
     */
    public void unloadTexture(String key) {
        if (textures.containsKey(key)) {
            textures.get(key).dispose();
        }
        textures.remove(key);
    }

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    /**
     * Loads a texture atlas from file and stores it.
     *
     * @param key  The name to save the texture atlas under.
     * @param file The file the texture atlas is stored in.
     * @return
     */
    public TextureAtlas loadTextureAtlas(String key, FileHandle file) {
        TextureAtlas texA = new TextureAtlas(file);
        if (textureAtlas.containsKey(key)) {
            textureAtlas.get(key).dispose();
        }
        textureAtlas.put(key, texA);
        return texA;
    }

    /**
     * Removes a texture atlas from the saved list and disposes of it .
     *
     * @param key The name of the texture atlas to unload.
     */
    public void unloadTextureAtlas(String key) {
        if (textureAtlas.containsKey(key)) {
            textureAtlas.get(key).dispose();
        }
        textureAtlas.remove(key);
    }

    public TextureAtlas getTextureAtlas(String key) {
        return textureAtlas.get(key);
    }

    /**
     * Called when disposing all assets stored in the handler
     */
    public void dispose() {
        for (Texture tex : textures.values()) {
            tex.dispose();
        }
        for (TextureAtlas texA : textureAtlas.values()) {
            texA.dispose();
        }
        textures.clear();
        textureAtlas.clear();
    }
}
