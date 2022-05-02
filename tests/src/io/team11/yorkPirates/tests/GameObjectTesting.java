package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.GameObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class GameObjectTesting {

    @Test
    public void testSetMaxHealth() {

        GameObject gameObject = new GameObject(null, 60, 60, 60, 60, 60, "Alcuin");
        gameObject.setMaxHealth(100);

        assertEquals(gameObject.maxHealth, 100);
    }

    @Test
    public void testSetHitbox() {

        float width = 60;
        float height = 60;

        GameObject gameObject = new GameObject(null, 60, 60, 60, width, height, "Alcuin");
        gameObject.setHitbox();

        assertTrue(gameObject.hitBox.width == width && gameObject.hitBox.height == height);

    }

    @Test
    public void testTakeDamage() {

        float width = 60;
        float height = 60;

        float damage = 50;

        GameObject gameObject = new GameObject(null, 60, 60, 60, width, height, "Alcuin");

        gameObject.setMaxHealth(100);
        gameObject.takeDamage(null, damage, null);

        assertTrue(gameObject.currentHealth == 50);

    }

    @Test
    public void testOverLap() {
        float width = 60;
        float height = 60;

        GameObject gameObjectOne = new GameObject(null, 60, 60, 60, width, height, "Alcuin");
        gameObjectOne.setHitbox();

        GameObject gameObjectTwo = new GameObject(null, 60, 60, 60, width, height, "Goodricke");
        gameObjectTwo.setHitbox();

        gameObjectOne.updateHitboxPos();
        gameObjectTwo.updateHitboxPos();

        assertTrue(gameObjectOne.overlaps(gameObjectTwo.hitBox));

    }

    @Test
    public void testJson() {
        GameObject object = new GameObject(null, 0, 20, 20, 16, 16, "Player");
        JsonValue json = object.toJson();
        assertEquals(json.getFloat("x"), object.x, 0);
        assertEquals(json.getFloat("y"), object.y, 0);
        assertEquals(json.getFloat("width"), object.width, 0);
        assertEquals(json.getFloat("height"), object.height, 0);
        assertEquals(json.getInt("maxHealth"), object.maxHealth);
        assertEquals(json.getFloat("currentHealth"), object.currentHealth, 0);
        assertEquals(json.getString("team"), object.team);

        GameObject object2 = new GameObject(null, 0, json);
        assertEquals(object2.x, object.x, 0);
        assertEquals(object2.y, object.y, 0);
        assertEquals(object2.width, object.width, 0);
        assertEquals(object2.height, object.height, 0);
        assertEquals(object2.maxHealth, object.maxHealth);
        assertEquals(object2.currentHealth, object.currentHealth, 0);
        assertEquals(object2.team, object.team);
    }

}
