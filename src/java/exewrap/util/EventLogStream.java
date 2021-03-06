package exewrap.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import exewrap.core.NativeMethods;

public class EventLogStream extends PrintStream {
	public static final int INFORMATION = 0;
	public static final int WARNING     = 1;
	public static final int ERROR       = 2;
	
	static {
		try {
			System.setOut(new EventLogStream(INFORMATION, new ByteArrayOutputStream()));
			System.setErr(new EventLogStream(WARNING, new ByteArrayOutputStream()));
		} catch(UnsupportedEncodingException ignore) {}
	}
	
	private int type;
	private ByteArrayOutputStream buffer;
	
	public EventLogStream(int type, ByteArrayOutputStream buffer) throws UnsupportedEncodingException {
		super(buffer, false, "UTF-8");
		this.type = type;
		this.buffer = buffer;
	}
	
	public void close() {
		flush();
	}
	
	public void flush() {
		if(buffer.size() == 0) {
			return;
		}
		try {
			String str = new String(buffer.toByteArray(), 0, buffer.size(), "UTF-8");
			NativeMethods.WriteEventLog(type, str);
		} catch(UnsupportedEncodingException ignore) {}
		buffer.reset();
	}
	
	public void write(byte[] b, int off, int len) {
		buffer.write(b, off, len);
	}
	
	public void write(byte[] b) throws IOException {
		buffer.write(b);
	}
	
	public void write(int b) {
		buffer.write(b);
	}
	
	public void print(String s) {
		if("".equals(s)) {
			buffer.reset();
		} else {
			super.print(s);
		}
	}
	
	public static String getStackTrace(Throwable t) {
		StringWriter s = new StringWriter();
		PrintWriter w = new PrintWriter(s);
		t.printStackTrace(w);
		w.flush();
		s.flush();
		return s.toString();
	}
}
