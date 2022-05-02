package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Player;
import com.engteam14.yorkpirates.Projectile;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class ProjectileTesting {


    @Test
    public void projectileCreationTest() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "PLAYER");

        Projectile projectile = new Projectile(null, 60, 60, player, 64, 64, "PLAYER");
        Projectile projectile2 = new Projectile(null, 60, 60, player, 64, 64, "ENEMY");

        assertTrue(projectile.projectileSpeed == 150f && projectile2.projectileSpeed == 50f);
    }

    @Test
    public void testProjectileJson() {
        Player player = new Player(null, null, 0, 0, 0, 60, 60, "PLAYER");

        Projectile projectile = new Projectile(null, 60, 60, player, 32, 64, "PLAYER");
        JsonValue json = projectile.toJson();
        assertEquals(json.getFloat("dx"), 0.5f, 0);
        assertEquals(json.getFloat("dy"), 1f, 0);
        assertEquals(json.getFloat("speed"), projectile.projectileSpeed, 0);
        assertEquals(json.getFloat("maxDistance"), 60, 0);
        assertEquals(json.get("origin").getFloat("x"), 0 , 0);

        Projectile projectile2 = new Projectile(null, 0, json);
        assertEquals(projectile2.x, projectile.x, 0);
    }

}
