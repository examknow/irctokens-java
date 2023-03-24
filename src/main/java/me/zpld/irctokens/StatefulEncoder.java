package me.zpld.irctokens;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatefulEncoder {
    private String encoding;
    private byte[] buffer;
    private List<Line> bufferedLines;

    public StatefulEncoder(String encoding) {
        this.encoding = encoding;
        clear();
    }
    
    public StatefulEncoder() {
    	this("UTF-8");
    }

    public void clear() {
        buffer = new byte[0];
        bufferedLines = new ArrayList<>();
    }

    public byte[] pending() {
        return buffer;
    }

    public void push(Line line) {
        String formattedLine = line.getFormatted() + "\r\n";
        byte[] encodedLine;
		try {
			encodedLine = formattedLine.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}
        buffer = concat(buffer, encodedLine);
        bufferedLines.add(line);
    }

    public List<Line> pop(int byte_count) {
        int sent = 0;
        int index = 0;
        while (sent < byte_count && index < buffer.length) {
            if (buffer[index] == '\n') {
                sent++;
            }
            index++;
        }
        byte[] sentBytes = new byte[index];
        System.arraycopy(buffer, 0, sentBytes, 0, index);
        buffer = Arrays.copyOfRange(buffer, index, buffer.length);
        List<Line> sentLines = new ArrayList<>();
        for (int i = 0; i < sent; i++) {
            sentLines.add(bufferedLines.remove(0));
        }
        return sentLines;
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }
}
