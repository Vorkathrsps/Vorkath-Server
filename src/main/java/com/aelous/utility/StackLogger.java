package com.aelous.utility;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class StackLogger extends PrintStream {


    private final FileOutputStream writer;
    private final String outputType;
    private String line;
    private static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public StackLogger(OutputStream out, String fileName, String type) throws FileNotFoundException {
        super(out);
        outputType = type;
        this.writer = fileName == null ? null : new FileOutputStream(new File(fileName), true);
    }

    @Override
    public void print(String message) {
        Throwable throwable = new Throwable();
        String name = "unknown";
        if (throwable.getStackTrace().length > 2) {
            name = throwable.getStackTrace()[2].getFileName().replace(".java", "");
        }
        log(name, message);
    }

    @Override
    public void print(boolean message) {
        Throwable throwable = new Throwable();
        String name = throwable.getStackTrace()[2].getFileName().replace(".java", "");
        log(name, message);
    }

    @Override
    public void print(int message) {
        Throwable throwable = new Throwable();
        String name = throwable.getStackTrace()[2].getFileName().replace(".java", "");
        log(name, message);
    }

    public void log(String className, Object message) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, h:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        String time = dateFormat.format(calendar.getTime());
        //If the class name is Throwable or the outputType is set to "err", that means it's an error.
        if (className.equals("Throwable") || outputType.equals("err")) {
            line = "[" + time + "] " + "[" + Thread.currentThread().getName() + "] " + "[" + className + "] [ERROR] " + message;
        } else {
            line = "[" + time + "] " + "[" + Thread.currentThread().getName() + "] " + "[" + className + "] [INFO] " + message;
        }
        super.print(line);
        //Queue the IO to run on a different thread.
        singleThreadExecutor.submit(() -> {
            if (writer != null) {
                try {
                    //Write a byte array of the String of the log line plus the newline character to the log file.
                    writer.write((line + '\n').getBytes(), 0, line.length() + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //We may need to  change this to non-static in the future but for now we'll make it static.
    public static void enableStackLogger() {
        try {
            System.setOut(new StackLogger(System.out, null, "out"));
            System.setErr(new StackLogger(System.err, null, "err"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
