package com.nardah;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

public class TestMain {

    public static void main(String[] args) throws IOException {

        Frame.animationlist = new Frame[3000][];
         Frame.load(0,gunzip(Files.readAllBytes(Paths.get("./good.gz"))));
         //Frame.load(0,gunzip(Files.readAllBytes(Paths.get("./bad.gz"))));

        System.out.println("success!");

    }

    private static byte[] gunzip(byte[] bytes) throws IOException {
        /* create the streams */
        InputStream is = new GZIPInputStream(new ByteArrayInputStream(bytes));
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                /* copy data between the streams */
                byte[] buf = new byte[4096];
                int len = 0;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    os.write(buf, 0, len);
                }
            } finally {
                os.close();
            }

            /* return the uncompressed bytes */
            return os.toByteArray();
        } finally {
            is.close();
        }
    }

}
