package com.engteam14.yorkpirates;

//All of PowerUpsTimer class is part of new requirement: UR.POWER_UPS
public class PowerUpsTimer {

    private static final int POWERUPTIME = 10;

    private float timeSince;
    private int timeLeft;
    private float timeStart = Float.MAX_VALUE;

    /**
     * Initialises a Timer.
     */
    public PowerUpsTimer() {
        timeLeft = 0;
    }

    public void getTime(float elapsedTime) {
        timeStart = elapsedTime;
    }

    /**
     * Adds an integer value to the score.
     */
    public void startTimer(float elapsedTime) {
        timeSince = elapsedTime - timeStart;
        timeLeft = (int) (POWERUPTIME - Math.floor(timeSince));
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
            return Integer.toString(timeLeft);
        }
    }
}
