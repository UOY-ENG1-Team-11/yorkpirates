package io.team11.yorkPirates.tests;

import com.engteam14.yorkpirates.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class CollegeTesting {

    @Test
    public void collegeTakeDamage(){

        float damage = 100;
        float destroy = 1000;

        College college = new College(null, null, 60, 60, 2, "Alcuin", "Alcuin", null, null);
        college.takeDamage(null, damage, null);

        assertTrue(college.currentHealth == college.maxHealth - damage);

        college.takeDamage(null, destroy, null);

        assertTrue(college.wasCaptured == true);
    }

}
