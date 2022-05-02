package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.PowerUps;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class PowerUpsTesting {

    @Test
    public void testPowerUpsJson() {
        String powerType = "Test";
        PowerUps pu = new PowerUps(null, 100, 200, 0.2f, powerType);
        JsonValue json = pu.toJson();

        assertEquals(json.getString("powerType"), powerType);
    }
}
