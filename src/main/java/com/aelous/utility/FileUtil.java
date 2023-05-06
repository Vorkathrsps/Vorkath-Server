package com.aelous.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class FileUtil {

    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    public static byte[] readFile(String name) {
        try {
            RandomAccessFile raf = new RandomAccessFile(name, "r");
            try (raf) {
                ByteBuffer buf =
                    raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
                if (buf.hasArray()) {
                    return buf.array();
                } else {
                    byte[] array = new byte[buf.remaining()];
                    buf.get(array);
                    return array;
                }
            }
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    private static byte[] getGZBuffer(String file) throws Exception {
        File f = new File(file);
        if (!f.exists())
            return null;
        byte[] buffer = new byte[(int) f.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        dis.readFully(buffer);
        dis.close();
        byte[] gzipInputBuffer = new byte[999999];
        int bufferlength = 0;
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
        do {
            if (bufferlength == gzipInputBuffer.length) {
                logger.warn("Error inflating data.\nGZIP buffer overflow.");
                break;
            }
            int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
            if (readByte == -1)
                break;
            bufferlength += readByte;
        } while (true);
        byte[] inflated = new byte[bufferlength];
        System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
        buffer = inflated;
        if (buffer.length < 10)
            return null;
        return buffer;
    }

    public static byte[] getDecompressedBuffer(String file) {
        try {
            byte[] buffer = getGZBuffer(file);
            return CompressionUtil.gunzip(buffer);
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    public static void writeTextToDir(String text, String directory) {
        //Write text to the txt file
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(directory, true))) {
                out.write(text);
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addAddressToClaimedList(String IP, String MAC, Set<String> IPToWrite, Set<String> MACToWrite, String directory) {
        IPToWrite.add(IP);
        MACToWrite.add(MAC);

        //Write IP to txt file
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(directory, true))) {
                out.write(IP);
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Write MAC to txt file
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(directory, true))) {
                out.write(MAC);
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean claimed(String IP, String MAC, String directory) {
        return count(IP, MAC, directory) >= 1;
    }

    public static boolean existingPlayer(String IP, String MAC) throws FileNotFoundException {
        int count = 0;
        File dir = new File("./data/saves/characters");
        File[] files = dir.listFiles();
        Scanner sc;
        if (files != null) {
            for (File profile : files) {
                sc = new Scanner(profile);
                String input;
                StringBuilder sb = new StringBuilder();
                while (sc.hasNextLine()) {
                    input = sc.nextLine();
                    sb.append(input).append(" ");
                }
                if (sb.toString().contains(IP) || sb.toString().contains(MAC)) {
                    count++;
                }
            }
        }
        return count > 1;
    }

    private static int count(String IP, String MAC, String directory) {
        int count = 0;
        try (LineNumberReader r = new LineNumberReader(new FileReader(directory))) {
            String line;
            while ((line = r.readLine()) != null) {
                for (String element : line.split(" ")) {
                    //Either IP or MAC matches
                    if (element.equalsIgnoreCase(IP) || element.equalsIgnoreCase(MAC)) {
                        count++;
                        //System.out.println("IP or MAC found at line " + r.getLineNumber());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
}
