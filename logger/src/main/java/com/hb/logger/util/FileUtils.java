package com.hb.logger.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

    public static void zip(ArrayList<File> files, String zipFile) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFile);

            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[2048];

            for (int i = 0; i < files.size(); i++) {

                FileInputStream fi = new FileInputStream(files.get(i));
                origin = new BufferedInputStream(fi, 2048);
                ZipEntry entry = new ZipEntry(files.get(i).getAbsolutePath().substring(files.get(i).getAbsolutePath().lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, 2048)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.finish();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
