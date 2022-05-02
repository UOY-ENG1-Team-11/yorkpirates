package io.team11.yorkPirates.tests;

import com.badlogic.gdx.Gdx;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(GdxTestRunner.class)
public class AssetTesting {
    @Test
    public void testShipAssetExists() {
        assertTrue("This test will only pass when the ship.png asset exists.",
                Gdx.files.internal("ship1.png").exists());
    }
}
