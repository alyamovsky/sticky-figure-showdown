package com.alyamovsky.sfs.resource;

public class Constants {
    public static final String GAME_TITLE = "Sticky Figure Showdown";
    public static final float WORLD_WIDTH = 80f;
    public static final float WORLD_HEIGHT = 48.0f;
    public static final float WORLD_SCALE = 0.05f;
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 480;

    public static final float PLAYER_1_START_POSITION_X = 16f;
    public static final float PLAYER_1_START_POSITION_Y = 15f;
    public static final float PLAYER_2_START_POSITION_X = 51f;
    public static final float PLAYER_2_START_POSITION_Y = 15f;
    public static final float RING_MIN_X = 7f;
    public static final float RING_MAX_X = 60f;
    public static final float RING_MIN_Y = 4f;
    public static final float RING_MAX_Y = 21f;
    private static final float RING_SLOPE_DEGREES = 75.0f;
    public static final float RING_SLOPE = (float) Math.tan(Math.toRadians(Constants.RING_SLOPE_DEGREES));
}
