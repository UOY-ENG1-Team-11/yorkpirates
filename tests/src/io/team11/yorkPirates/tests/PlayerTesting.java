package io.team11.yorkPirates.tests;

import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class PlayerTesting {


    @Test
    public void playerTakeDamage(){

        float damage = 100;

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.takeDamage(null, damage, "Enemy");
        assertTrue(player.currentHealth == 100);
        player.takeDamage(null, damage, null);
        assertTrue(player.alive == false);

    }

    @Test
    public void invinciblePlayerTakeDamage(){

        float damage = 50;

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");

        player.setInvincible(null);
        player.takeDamage(null, damage, "Enemy");
        assertTrue(player.currentHealth == 200);


    }

    @Test
    public void increasePlayerAttackSpeed(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.increaseAttackSpeed(null);
        assertTrue(player.playerAttackSpeedMultiplier == 2 && player.atkSpdTime != 0);

    }

    @Test
    public void increasePlayerDamage(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.increaseDamage(null);
        assertTrue(player.playerProjectileDamageMultiplier == 10 && player.dmgUpTime != 0);

    }

    @Test
    public void increasePlayerHealth(){
        float damage = 75;

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.takeDamage(null, damage, "Enemy");
        player.increaseHealth(null);
        assertTrue(player.currentHealth == player.maxHealth-damage+50);
        player.increaseHealth(null);
        assertTrue(player.currentHealth == player.maxHealth);

    }

    @Test
    public void increasePlayerSpeed(){

        float playerTotalMoveSpeed;

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.increaseSpeed(null);

        playerTotalMoveSpeed = player.SPEED*player.playerSpeedMultiplier;
        assertTrue(playerTotalMoveSpeed == player.SPEED*1.5 && player.speedUpTime != 0);

    }

    @Test
    public void upgradePlayerAttackSpeed(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.upgradeAttackSpeed(null);
        assertTrue(player.playerAttackSpeedUpgrade == 2 && player.AtkSpdBought == true);

    }

    @Test
    public void upgradePlayerAttackDamage(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.upgradeAttackDamage(null);
        assertTrue(player.playerProjectileDamageUpgrade == 5 && player.AtkDmgBought == true);

    }

    @Test
    public void upgradePlayerSpeed(){

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");
        player.upgradeSpeed(null);
        assertTrue(player.playerSpeedMultiplier* player.playerSpeedUpgrade == 1.5 && player.SpdBought == true);
        player.increaseSpeed(null);
        assertTrue(player.playerSpeedMultiplier* player.playerSpeedUpgrade == 1.5*1.5);


    }

    @Test
    public void testPlayerJson() {
        Player.AtkDmgBought = false;
        Player.AtkSpdBought = false;
        Player.SpdBought = false;

        Player player = new Player(null, null, 60 , 60, 60, 60, 60, "Player");

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
