package util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;

import java.io.ByteArrayInputStream;

public class StringServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream byteArrayInputStream;

    public StringServletInputStream(String input) {
        this.byteArrayInputStream = new ByteArrayInputStream(input.getBytes());
    }

    @Override
    public boolean isFinished() {
        return byteArrayInputStream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }


    @Override
    public int read() throws IOException {
        return byteArrayInputStream.read();
    }
}
