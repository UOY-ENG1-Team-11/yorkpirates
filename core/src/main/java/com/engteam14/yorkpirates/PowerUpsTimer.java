package com.engteam14.yorkpirates;

import com.badlogic.gdx.utils.TimeUtils;

public class PowerUpsTimer {

    private static final int POWERUPTIME = 10;

    private long timeSince;
    private int timeLeft;
    private long timeStart;

    /**
     * Initialises a Timer.
     */
    public PowerUpsTimer() {
        timeLeft = 0;
    }

    public void getTime() {
        timeStart = TimeUtils.millis();
    }

    /**
     * Adds an integer value to the score.
     */
    public void startTimer() {
        //System.out.println(timeStart);
        timeSince = TimeUtils.timeSinceMillis(timeStart) / 1000;
        timeLeft = POWERUPTIME - Math.toIntExact(timeSince);
    }

    /**
     * Gets the score value in string form.
     *
     * @return the time remaining.
     */
    public String GetString() {
        if (timeLeft <= 0) {
            return "10";
        } else {
            return Long.toString(timeLeft);
        }
    }
}
