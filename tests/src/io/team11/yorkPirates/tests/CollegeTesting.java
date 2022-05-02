package io.team11.yorkPirates.tests;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.engteam14.yorkpirates.Boat;
import com.engteam14.yorkpirates.College;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(GdxTestRunner.class)
public class CollegeTesting {

    @Test
    public void collegeTakeDamage() {

        float damage = 100;
        float destroy = 1000;

        College college = new College(null, null, 60, 60, 2, "Alcuin", "Alcuin", null, null);
        college.takeDamage(null, damage, null);

        assertTrue(college.currentHealth == college.maxHealth - damage);

        college.takeDamage(null, destroy, null);

        assertTrue(college.wasCaptured == true);
    }

    @Test
    public void testAddBoat() {
        College college = new College(null, null, 60, 60, 2, "Alcuin", "Alcuin", null, null);
        college.addBoat(null, 20, 30, 50, new Vector2[]{new Vector2(100, 200)});

        Boat boat = college.boats.get(0);

        assertEquals(boat.x, 80, 0);
        assertEquals(boat.y, 90, 0);
        assertEquals(boat.collegeName, "Alcuin");
    }

    @Test
    public void testCollegeJson() {
        College college = new College(null, null, 60, 60, 2, "Alcuin", "Alcuin", null, null);
        college.addBoat(null, 20, 30, 50, new Vector2[]{new Vector2(100, 200)});
        college.wasCaptured = true;

        JsonValue json = college.toJson();
        assertEquals(json.getString("collegeName"), "Alcuin");
        assertEquals(json.getBoolean("wasCaptured"), true);

        college.fromJson(json);
        assertEquals(college.collegeName, "Alcuin");
        assertTrue(college.wasCaptured);
        assertFalse(college.boats.isEmpty());
    }
}
