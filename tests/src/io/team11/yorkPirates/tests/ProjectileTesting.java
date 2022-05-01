package io.team11.yorkPirates.tests;

import com.engteam14.yorkpirates.Player;
import com.engteam14.yorkpirates.Projectile;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class ProjectileTesting {


    @Test
    public void projectileCreationTest(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "PLAYER");

        Projectile projectile = new Projectile(null, 60, 60, player, 64, 64, "PLAYER");
        Projectile projectile2 = new Projectile(null, 60, 60, player, 64, 64, "ENEMY");

        assertTrue(projectile.projectileSpeed == 150f && projectile2.projectileSpeed == 50f);
    }

}
