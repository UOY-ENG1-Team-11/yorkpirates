package io.team11.yorkPirates.tests;

import com.engteam14.yorkpirates.GameObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class GameObjectTesting {

    @Test
    public void testSetMaxHealth(){

        GameObject gameObject = new GameObject(null, 60, 60, 60, 60, 60, "Alcuin");
        gameObject.setMaxHealth(100);

        assertEquals(gameObject.maxHealth, 100);
    }

    @Test
    public void testSetHitbox(){

        float width = 60;
        float height = 60;

        GameObject gameObject = new GameObject(null, 60, 60, 60, width, height, "Alcuin");
        gameObject.setHitbox();

        assertTrue(gameObject.hitBox.width == width && gameObject.hitBox.height == height);

    }

    @Test
    public void testTakeDamage(){

        float width = 60;
        float height = 60;

        float damage = 50;

        GameObject gameObject = new GameObject(null, 60, 60, 60, width, height, "Alcuin");

        gameObject.setMaxHealth(100);
        gameObject.takeDamage(null, damage, null);

        assertTrue(gameObject.currentHealth == 50);

    }

    @Test
    public void testOverLap(){
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



}
