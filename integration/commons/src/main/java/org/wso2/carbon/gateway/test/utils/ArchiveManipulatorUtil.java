/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.wso2.carbon.gateway.test.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ArchiveManipulatorUtil {

    private static final Logger log = LoggerFactory.getLogger(ArchiveManipulatorUtil.class);

    public static void extractFile(String sourceFilePath, String extractedDir) throws IOException {
        FileOutputStream fileoutputstream = null;
        String fileDestination = extractedDir + File.separator;
        byte[] buf = new byte[1024];
        ZipInputStream zipinputstream = null;

        try {
            zipinputstream = new ZipInputStream(new FileInputStream(sourceFilePath));
            ZipEntry zipentry = zipinputstream.getNextEntry();

            while (true) {
                if (zipentry != null) {
                    String e = fileDestination + zipentry.getName();
                    e = e.replace('/', File.separatorChar);
                    e = e.replace('\\', File.separatorChar);
                    File newFile = new File(e);
                    if (zipentry.isDirectory()) {
                        if (!newFile.exists()) {
                            newFile.mkdirs();
                        }

                        zipentry = zipinputstream.getNextEntry();
                        continue;
                    }

                    File resourceFile = new File(e.substring(0, e.lastIndexOf(File.separator)));
                    if (resourceFile.exists() || resourceFile.mkdirs()) {
                        fileoutputstream = new FileOutputStream(e);

                        int n;
                        while ((n = zipinputstream.read(buf, 0, 1024)) > -1) {
                            fileoutputstream.write(buf, 0, n);
                        }

                        fileoutputstream.close();
                        zipinputstream.closeEntry();
                        zipentry = zipinputstream.getNextEntry();
                        continue;
                    }
                }

                zipinputstream.close();
                return;
            }
        } catch (IOException var15) {
            log.error("Error on archive extraction ", var15);
            throw new IOException("Error on archive extraction ", var15);
        } finally {
            if (fileoutputstream != null) {
                fileoutputstream.close();
            }

            if (zipinputstream != null) {
                zipinputstream.close();
            }
        }
    }
}