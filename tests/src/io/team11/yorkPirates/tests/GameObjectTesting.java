package io.team11.yorkPirates.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.engteam14.yorkpirates.GameObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class GameObjectTesting {

    @Test
    public void testSetMaxHealth(){

        //Array<Texture> collegeSprites = new Array<>();
        //collegeSprites.add(new Texture("alcuin.png"));

        GameObject gameObject = new GameObject(null, 60, 60, 60, 60, 60, "Alcuin");
        gameObject.setMaxHealth(100);

        assertEquals(gameObject.maxHealth, 100);
    }

}
