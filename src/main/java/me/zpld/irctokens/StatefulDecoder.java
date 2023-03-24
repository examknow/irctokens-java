package me.zpld.irctokens;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatefulDecoder {
    private Charset encoding;
    private Charset fallback;
    private byte[] buffer;

    public StatefulDecoder(String encoding, String fallback) {
        this.encoding = Charset.forName(encoding);
        this.fallback = Charset.forName(fallback);
        this.clear();
    }

    public StatefulDecoder() {
        this("UTF-8", "ISO-8859-1");
    }

    public void clear() {
        this.buffer = new byte[0];
    }

    public byte[] pending() {
        return this.buffer;
    }

    public List<Line> push(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }

        this.buffer = Arrays.copyOf(this.buffer, this.buffer.length + data.length);
        System.arraycopy(data, 0, this.buffer, this.buffer.length - data.length, data.length);
        List<Line> lines = new ArrayList<>();
        int start = 0;
        int end = 0;
        while (end < this.buffer.length) {
            if (this.buffer[end] == '\n') {
                byte[] lineBytes = Arrays.copyOfRange(this.buffer, start, end);
                Line line = tokenise(lineBytes, this.encoding, this.fallback);
                lines.add(line);
                start = end + 1;
            }
            end++;
        }
        this.buffer = Arrays.copyOfRange(this.buffer, start, end);
        return lines;
    }

    private Line tokenise(byte[] lineBytes, Charset encoding, Charset fallback) {
        String lineStr;
        try {
            lineStr = new String(lineBytes, encoding.name());
        } catch (Exception ex) {
            lineStr = new String(lineBytes, fallback);
        }
        try {
			return Line.tokenise(lineStr);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
}
