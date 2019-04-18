package com.github.vasiliz.myimageloader.diskCache;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface IDiskCache {

    void setContext(Context pContext);

    File getFile(String pImageUrl) throws IOException, NoSuchAlgorithmException;

    void save(String pUrl, Bitmap pBitmap) throws IOException;
}
