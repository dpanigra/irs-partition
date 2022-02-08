package com.secureai;

public class Config {
    public static final String DEFAULT_SEED = "42";
    public static final String DEFAULT_COST_WEIGHT = "0";
    public static final String DEFAULT_TIME_WEIGHT = "1";

    public static int SEED = Integer.parseInt(DEFAULT_SEED);    
    public static double COST_WEIGHT = Double.parseDouble(DEFAULT_COST_WEIGHT);
    public static double TIME_WEIGHT = Double.parseDouble(DEFAULT_TIME_WEIGHT);


    // Console colors
    public static final String RESET = "\033[0m";       // Text Reset
    public static final String RED = "\033[0;31m";      // RED
    public static final String GREEN = "\033[0;32m";    // GREEN
}
