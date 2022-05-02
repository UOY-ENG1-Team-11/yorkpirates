package io.team11.yorkPirates.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Player;
import com.engteam14.yorkpirates.YorkPirates;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(GdxTestRunner.class)
public class PlayerTesting {

    @Test
    public void testPlayerSafemove() {
        /*
         * Create an edge array that looks like:
         * f t t
         * t t t
         * f t f
         * */
        Array<Array<Boolean>> edges = new Array<Array<Boolean>>();
        Array<Boolean> row1 = new Array<Boolean>();
        row1.add(false);
        row1.add(true);
        row1.add(false);
        Array<Boolean> row2 = new Array<Boolean>();
        row2.add(true);
        row2.add(true);
        row2.add(true);
        Array<Boolean> row3 = new Array<Boolean>();
        row3.add(false);
        row3.add(true);
        row3.add(true);

        edges.add(row1);
        edges.add(row2);
        edges.add(row3);

        Player player = new Player(mock(YorkPirates.class), null, 60, 1, 1, 10, 10, "Player");
        assertFalse(player.safeMove(edges));
        player.x = 32;
        player.y = 32;
        assertTrue(player.safeMove(edges));
    }

    @Test
    public void testPlayerMove() {
        Player player = new Player(mock(YorkPirates.class), null, 60, 60, 60, 60, 60, "Player");
        player.move(100, 200);

        //Test player moved
        assertTrue(player.x == 60 + 100 * Gdx.graphics.getDeltaTime());
        assertTrue(player.y == 60 + 200 * Gdx.graphics.getDeltaTime());

        //Test healthbar moved with player
        assertTrue(player.playerHealth.x == player.x);
        assertTrue(player.playerHealth.y == player.y + +player.height / 2 + 2f);
    }

    @Test
    public void playerTakeDamage() {

        float damage = 100;

        Player player = new Player(mock(YorkPirates.class), null, 60, 60, 60, 60, 60, "Player");
        player.takeDamage(null, damage, "Enemy");
        assertTrue(player.currentHealth == 100);
        player.takeDamage(null, damage, null);
        assertTrue(player.alive == false);

    }

    @Test
    public void invinciblePlayerTakeDamage() {

        float damage = 50;

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");

        player.setInvincible(null);
        player.takeDamage(null, damage, "Enemy");
        assertTrue(player.currentHealth == 200);


    }

    @Test
    public void increasePlayerAttackSpeed() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.increaseAttackSpeed(null);
        assertTrue(player.playerAttackSpeedMultiplier == 2 && player.atkSpdTime != 0);

    }

    @Test
    public void increasePlayerDamage() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.increaseDamage(null);
        assertTrue(player.playerProjectileDamageMultiplier == 10 && player.dmgUpTime != 0);

    }

    @Test
    public void increasePlayerHealth() {
        float damage = 75;

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.takeDamage(null, damage, "Enemy");
        player.increaseHealth(null);
        assertTrue(player.currentHealth == player.maxHealth - damage + 50);
        player.increaseHealth(null);
        assertTrue(player.currentHealth == player.maxHealth);

    }

    @Test
    public void increasePlayerSpeed() {

        float playerTotalMoveSpeed;

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.increaseSpeed(null);

        playerTotalMoveSpeed = player.SPEED * player.playerSpeedMultiplier;
        assertTrue(playerTotalMoveSpeed == player.SPEED * 1.5 && player.speedUpTime != 0);

    }

    @Test
    public void upgradePlayerAttackSpeed() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.upgradeAttackSpeed(null);
        assertTrue(player.playerAttackSpeedUpgrade == 2 && player.AtkSpdBought == true);

    }

    @Test
    public void upgradePlayerAttackDamage() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.upgradeAttackDamage(null);
        assertTrue(player.playerProjectileDamageUpgrade == 5 && player.AtkDmgBought == true);

    }

    @Test
    public void upgradePlayerSpeed() {

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");
        player.upgradeSpeed(null);
        assertTrue(player.playerSpeedMultiplier * player.playerSpeedUpgrade == 1.5 && player.SpdBought == true);
        player.increaseSpeed(null);
        assertTrue(player.playerSpeedMultiplier * player.playerSpeedUpgrade == 1.5 * 1.5);


    }

    @Test
    public void testPlayerJson() {
        Player.AtkDmgBought = false;
        Player.AtkSpdBought = false;
        Player.SpdBought = false;

        Player player = new Player(null, null, 60, 60, 60, 60, 60, "Player");

        player.upgradeAttackDamage(null);
        player.upgradeSpeed(null);

        JsonValue json = player.toJson();
        assertFalse(json.getBoolean("AtkSpdBought"));
        assertTrue(json.getBoolean("AtkDmgBought"));
        assertTrue(json.getBoolean("SpdBought"));

        assertEquals(json.getFloat("distance"), player.getDistance(), 0);

        player.fromJson(json);
    }

}
