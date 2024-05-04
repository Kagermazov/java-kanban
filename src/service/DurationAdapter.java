package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter writer, Duration taskDuration) throws IOException {
        writer.value(String.valueOf(taskDuration));
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        String line = reader.nextString();

        if (line.equals("null")) {
            return null;
        } else {
            return Duration.parse (line);
        }
    }
}
