package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Enemy_Wave;
import com.engteam14.yorkpirates.Player;
import com.engteam14.yorkpirates.WeatherManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class WeatherManagerTesting {

    @Test
    public void testCreateWave() {
        WeatherManager weather = new WeatherManager(null);
        Player player = new Player(null, null, 1, 60, 60, 60, 60, "Player");
        weather.createWave(player, 100, 200);
        Enemy_Wave wave = weather.waves.get(0);

        assertEquals(wave.x, 100, 0);
        assertEquals(wave.y, 200, 0);
        assertEquals(wave.target, player);
    }

    @Test
    public void testWeatherManagerJson() {
        WeatherManager weather = new WeatherManager(null);
        Player player = new Player(null, null, 1, 60, 60, 60, 60, "Player");
        weather.createWave(player, 100, 200);
        weather.badWeather = true;
        JsonValue json = weather.toJson();
        assertTrue(json.getBoolean("badWeather"));
        assertFalse(json.getBoolean("weatherPass"));
        assertEquals(json.getFloat("lastWeatherChange"), 0, 0);
        weather.fromJson(null, json);
        assertTrue(weather.badWeather);
        assertFalse(WeatherManager.weatherPass);
        assertEquals(weather.waves.get(0).x, 100, 0);
        assertEquals(weather.waves.get(0).y, 200, 0);
    }
}
