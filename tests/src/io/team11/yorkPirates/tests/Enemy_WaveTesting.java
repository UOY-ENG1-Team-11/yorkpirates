package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Enemy_Wave;
import com.engteam14.yorkpirates.GameObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class Enemy_WaveTesting {

    @Test
    public void testEnemy_WaveJson() {

        GameObject target = new GameObject(null, 0, 20, 20, 16, 16, "Enemy");
        Enemy_Wave wave = new Enemy_Wave(null, 0, target,100,100);

        JsonValue json = wave.toJson();
        assertEquals(json.getFloat("xComponent"), wave.xComponent, 0);
        assertEquals(json.getFloat("yComponent"), wave.yComponent, 0);
        assertEquals(json.getFloat("rotation"), wave.rotation, 0);
        assertEquals(json.get("target").getFloat("x"), target.x, 0);
        assertEquals(json.getFloat("distanceTravelled"), wave.distanceTravelled, 0);

        Enemy_Wave wave2 = new Enemy_Wave(null, 0, json);
        assertEquals(wave2.xComponent, wave.xComponent, 0);
        assertEquals(wave2.yComponent, wave.yComponent, 0);
        assertEquals(wave2.rotation, wave.rotation, 0);
        assertEquals(wave2.distanceTravelled, wave.distanceTravelled, 0);
    }
}
