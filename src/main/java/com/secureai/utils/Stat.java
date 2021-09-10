package com.secureai.utils;

import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Stat<T> {
    @Getter
    private List<Timestamped<T>> history;
    private List<Double> seconds;
    private double startTime;
    @Getter
    private BufferedWriter bufferedWriter;
    private FileWriter fileWriter;

    public Stat() {
        this.history = new ArrayList<>();
        this.seconds = new ArrayList<>();
    }

    public Stat(String filePath, boolean directory) {
        this();

        try {
            File f = new File(filePath);
            if (directory) f.mkdirs();
            if (f.isDirectory()) f = new File(FileUtils.firstAvailableFolder(filePath, "out") + "/stat.csv");
            else if (f.exists()) f.delete();
            (f.isDirectory() ? f : f.getParentFile()).mkdirs();

            this.fileWriter = new FileWriter(f);
            this.bufferedWriter = new BufferedWriter(this.fileWriter);


            if (this.bufferedWriter != null) {
                try {
                    this.bufferedWriter.write("Timestamp, Seconds, Reward, Steps\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stat(String filePath) {
        this(filePath, false);
    }

    public Stat(String filePath, long startTime) {

        this(filePath, false);
        this.startTime = startTime;
    }

    public Stat<T> append(T value) {
        Timestamped<T> t = new Timestamped<>(value);
        this.history.add(t);

        if (this.bufferedWriter != null) {
            try {
                this.bufferedWriter.write(t.getTimestamp() + ", " + t.getValue() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public Stat<T> append(T value, boolean b, int steps) {
        Timestamped<T> t = new Timestamped<>(value);
        double sec = ((double)System.nanoTime()-this.startTime)/1000000000;

        this.history.add(t);
        this.seconds.add(sec);

        if (this.bufferedWriter != null) {
            try {
                this.bufferedWriter.write(t.getTimestamp() + ", " + sec + ", " + t.getValue() + ", "+ steps +"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }


    public void print() {
        System.out.println(this.toString());
    }

    public void write(String filePath) {
        try {
            Files.write(Paths.get(filePath), this.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (this.bufferedWriter != null) {
            try {
                this.bufferedWriter.close();
                this.fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void flush() {
        if (this.bufferedWriter != null) {
            try {
                this.bufferedWriter.flush();
                this.fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return history.stream().map(item -> item.getTimestamp() + ", " + item.getValue()).collect(Collectors.joining("\n"));
    }
}
