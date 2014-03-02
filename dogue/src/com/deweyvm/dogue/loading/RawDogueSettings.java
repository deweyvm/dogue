package com.deweyvm.dogue.loading;

import com.badlogic.gdx.utils.Json;
import com.deweyvm.dogue.common.data.Encoding;
import com.deweyvm.dogue.common.logging.Log;

import java.io.IOException;
import java.nio.file.*;

public class RawDogueSettings {
    public String username;
    public int port;
    public String password;
    public String host;
    public int pingFrequency;
    public int pingCheckFrequency;
    public int maxPongWait;
    public String logLocation;
    public int width;
    public int height;
    public RawDogueSettings() {

    }

    public static String settingsPath = "user_settings.json";
    public static RawDogueSettings fromFile() {
        try {
            final Json json = new Json();
            final Path path = Paths.get(settingsPath);
            if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                writeDefault(path);
                return fromFile();
            }

            final byte[] rawData = Files.readAllBytes(path);
            final String data = Encoding.fromBytes(rawData, rawData.length);
            return json.fromJson(RawDogueSettings.class, data);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    public static RawDogueSettings createDefault() {
        return DogueSettings.makeDefault();
    }

    public static void writeDefault(Path path) {
        final Json json = new Json();
        final String data = json.toJson(createDefault());
        try {
            Files.write(path, Encoding.toBytes(data), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            if (!exists(path)) {
                throw new RuntimeException("failed to write default settings");
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static boolean exists(Path path) {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    public void flush() {
        final Path path = Paths.get(settingsPath);
        final Json json = new Json();
        final String s = json.prettyPrint(this);
        try {
            Files.write(path, Encoding.toBytes(s), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException ioe) {
            Log.error("Failed to write settings");
        }
    }

}
