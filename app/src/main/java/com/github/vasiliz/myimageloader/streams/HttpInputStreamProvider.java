package com.github.vasiliz.myimageloader.streams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpInputStreamProvider implements IStreamContract<String> {

    public InputStream get(final String pUrl) throws IOException {
        final HttpURLConnection urlConnection = (HttpURLConnection) (new URL(pUrl)).openConnection();
        return urlConnection.getInputStream();
    }

}
