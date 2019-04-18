package com.github.vasiliz.myimageloader.streams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileInputStreamProvider implements IStreamContract<File> {

    private static final int PART_OF_FILE = 4096;

    @Override
    public InputStream get(final File pUrl) throws IOException {
        return new BufferedInputStream(new FileInputStream(pUrl), PART_OF_FILE);
    }
}
