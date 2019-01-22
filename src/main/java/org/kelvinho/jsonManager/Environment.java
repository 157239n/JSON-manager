package org.kelvinho.jsonManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.annotation.Nonnull;

@SuppressWarnings({"unused", "WeakerAccess"})
class Environment {
    public static class IO {
        @Deprecated
        public static void writeObject(@Nonnull final String file, @Nonnull final Object e) {
            try {
                FileOutputStream fileOut = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(e);
                out.close();
                fileOut.close();
            } catch (IOException i) {
                i.printStackTrace();
                throw new RuntimeException("something went wrong while writing this object");
            }
        }

        @Deprecated
        public static Object readObject(@Nonnull final String file) {
            Object ans = null;
            try {
                FileInputStream fileIn = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                ans = in.readObject();
                in.close();
                fileIn.close();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (ClassNotFoundException c) {
                System.out.println("Can't read an object");
                c.printStackTrace();
            }
            return ans;
        }

        public static String readText(@Nonnull final String fileName) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
                String line = reader.readLine();
                StringBuilder text = new StringBuilder(line);
                while (!line.equals("")) {
                    line = reader.readLine();
                    text.append(line);
                }
                return text.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        public static void writeText(@Nonnull final String fileName, @Nonnull final String text) {
            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
                writer.print(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static void writeLongText(@Nonnull String text) {
            int maxLogSize = 1000;
            for (int i = 0; i <= text.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > text.length() ? text.length() : end;
                System.out.println(text.substring(start, end));
            }
        }
    }
}
