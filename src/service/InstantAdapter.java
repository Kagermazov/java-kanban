package service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter writer, Instant startTime) throws IOException {
        if (startTime != null) {
            writer.value(String.valueOf(startTime));
        } else {
            writer.nullValue();
        }
    }

    @Override
    public Instant read(JsonReader reader) throws IOException {
        return Instant.parse(reader.nextString());
    }
}
