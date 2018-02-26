package com.meilitech.zhongyi.resource.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {
    public static final String UTF8_BOM = "\uFEFF";

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static boolean decompress(File compressFile) {

        try {
            int buffersize = 1024;
            String compressPath = compressFile.getAbsolutePath();
            String fileName = compressFile.getName();
            String decompressPath = compressFile.getParent();

            if (fileName.matches(".*.tar.gz$")) {

                /** create a TarArchiveInputStream object. **/

                FileInputStream fin = new FileInputStream(compressFile.getAbsoluteFile());
                BufferedInputStream in = new BufferedInputStream(fin);
                GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
                TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);

                TarArchiveEntry entry = null;

                /** Read the tar entries using the getNextEntry method **/

                while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {

                    System.out.println("Extracting: " + entry.getName());

                    /** If the entry is a directory, create the directory. **/

                    if (entry.isDirectory()) {
                        continue;
                    }
                    /**
                     * If the entry is a file,write the decompressed file to the disk
                     * and close destination stream.
                     **/
                    else {
                        int count;
                        byte data[] = new byte[buffersize];
                        String decompressfileName = fileName.replace(".tar.gz", "");
                        FileOutputStream fos = new FileOutputStream(Paths.get(decompressPath, decompressfileName).toAbsolutePath().toString());
                        BufferedOutputStream dest = new BufferedOutputStream(fos,
                                buffersize);
                        while ((count = tarIn.read(data, 0, buffersize)) != -1) {
                            dest.write(data, 0, count);
                        }
                        dest.close();
                    }
                }

                /** Close the input stream **/

                tarIn.close();


            } else if (fileName.matches(".*.gz$")) {
                InputStream fin = Files.newInputStream(Paths.get(compressPath));
                BufferedInputStream bin = new BufferedInputStream(fin);
                GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bin);


                String decompressfileName = fileName.replace(".gz", "");
                OutputStream fout = Files.newOutputStream(Paths.get(decompressPath, decompressfileName));
                final byte[] buffer = new byte[buffersize];
                int n = 0;
                while (-1 != (n = gzIn.read(buffer))) {
                    fout.write(buffer, 0, n);
                }


                fout.close();
                gzIn.close();

            } else {
                return false;
            }

            return true;

        } catch (IOException e) {
            return false;
        }
    }

}
