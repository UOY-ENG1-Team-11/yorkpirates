package com.engteam14.yorkpirates;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

public class HealthBar extends GameObject {

    private final float startWidth;

    /**
     * Generates a health bar object within the game.
     *
     * @param entity The college which this bar is attached to.
     * @param frames The animation frames, or a single sprite.
     */
    public HealthBar(GameObject entity, Array<Texture> frames) {
        super(frames, 0, entity.x, entity.y + entity.height / 2 + 2f, entity.width, 2f, entity.team);

        startWidth = entity.width;
        setMaxHealth(entity.maxHealth);
    }

    /**
     * Resizes the bar to match the new health value.
     *
     * @param currentValue The current health value of the attached object
     */
    public void resize(float currentValue) {
        currentHealth = currentValue;
        this.width = startWidth * (currentValue / maxHealth);
    }

    /**
     * Moves the object within the x and y-axis of the game world.
     *
     * @param x The value to set the object to within the x-axis.
     * @param y The value to set the object to within the y-axis.
     */
    @Override
    public void move(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
