package io.team11.yorkPirates.tests;

import com.engteam14.yorkpirates.College;
import com.engteam14.yorkpirates.Indicator;
import com.engteam14.yorkpirates.Player;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunner.class)
public class IndicatorTesting {

    @Test
    public void testGradient() {
        College college = new College(null, null, 30, 60, 2, "Alcuin", "Alcuin", null, null);
        Player player = new Player(null, null, 0, 0, 0, 60, 60, "Player");

        Indicator indicator = new Indicator(college, player, null);
        indicator.setVisible(true);
        assertEquals(indicator.gradient.x, 0.5f, 0);
        assertEquals(indicator.gradient.y, 1f, 0);
    }
}
