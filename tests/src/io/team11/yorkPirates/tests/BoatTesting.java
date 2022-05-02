package io.team11.yorkPirates.tests;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Boat;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class BoatTesting {
    @Test
    public void testTakeDamage() {

        float damage = 50;

        Boat boat = new Boat(null, null, 0, 0, 0, 25, 12, "Enemy", new Vector2[]{}, "Alcuin");

        boat.takeDamage(null, damage, "Player");
        assertTrue(boat.currentHealth == boat.maxHealth - damage);
    }

    @Test
    public void testBoatMove() {
        Boat boat = new Boat(null, null, 0, 0, 0, 25, 12, "Enemy", new Vector2[]{}, "Alcuin");
        boat.move(100, 200);

        //Test player moved
        assertTrue(boat.x == 100 * Gdx.graphics.getDeltaTime());
        assertTrue(boat.y == 200 * Gdx.graphics.getDeltaTime());

        //Test healthbar moved with player
        assertTrue(boat.boatHealth.x == boat.x);
        assertTrue(boat.boatHealth.y == boat.y + +boat.height / 2 + 2f);
    }

    @Test
    public void testBoatSafemove() {
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

        Boat boat = new Boat(null, null, 0, 0, 0, 25, 12, "Enemy", new Vector2[]{}, "Alcuin");
        assertFalse(boat.safeMove(edges));
        boat.x = 32;
        boat.y = 32;
        assertTrue(boat.safeMove(edges));
    }

    @Test
    public void testBoatJson() {
        Boat boat = new Boat(null, null, 0, 0, 0, 25, 12, "Enemy", new Vector2[]{new Vector2(100, 200)}, "Alcuin");
        boat.rotation = 40f;
        JsonValue json = boat.toJson();
        assertEquals(json.getFloat("rotation"), boat.rotation, 0);
        JsonValue point = json.get("patrol").child();
        assertEquals(point.getFloat("x"), boat.patrol[0].x, 0);
        assertEquals(point.getFloat("y"), boat.patrol[0].y, 0);
        assertEquals(json.getInt("patrolIndex"), 0);
        Boat boat2 = new Boat(null, null, 0, json, "Alcuin");
        assertEquals(boat2.rotation, boat.rotation, 0);
        assertEquals(boat2.patrol[0].x, boat.patrol[0].x, 0);
        assertEquals(boat2.patrol[0].y, boat.patrol[0].y, 0);
    }
}
