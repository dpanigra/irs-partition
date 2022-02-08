package com.secureai;

public class Config {
    public static final String DEFAULT_SEED = "42";

//    public static final double COST_WEIGHT = 0.4;
//    public static final double TIME_WEIGHT = 0.6;
    public static final double COST_WEIGHT = 0;
    public static final double TIME_WEIGHT = 1;
    
    public static int SEED = Integer.parseInt(DEFAULT_SEED);    

    // Console colors
    public static final String RESET = "\033[0m";       // Text Reset
    public static final String RED = "\033[0;31m";      // RED
    public static final String GREEN = "\033[0;32m";    // GREEN
}
