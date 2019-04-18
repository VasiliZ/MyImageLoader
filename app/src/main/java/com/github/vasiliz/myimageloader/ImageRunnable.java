package com.github.vasiliz.myimageloader;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.github.vasiliz.myimageloader.streams.FileInputStreamProvider;
import com.github.vasiliz.myimageloader.streams.HttpInputStreamProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageRunnable implements Runnable {

    private final ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void run() {
        LoadImageResultModel resultModel = null;

        try {
            final ImageRequestModel imageRequestModel = imageLoader.getImageQueue().takeFirst();
            resultModel = new LoadImageResultModel(imageRequestModel);
            Bitmap bitmap = getBitmapOnMemCache(imageRequestModel);
            if (bitmap == null) {
                bitmap = downLoadImage(imageRequestModel);
            }
            resultModel.setBitmap(bitmap);
            setBitmapOnView(resultModel);
        } catch (final Exception pE) {
            if (resultModel != null) {
                resultModel.setException(pE);
            }
        }
    }

    private Bitmap getBitmapOnMemCache(final ImageRequestModel imageRequestModel) {
        Bitmap bitmapLRU;
        synchronized (imageLoader.getSync()) {
            bitmapLRU = imageLoader.getLruCache().get(imageRequestModel.getUrl());
            if (bitmapLRU != null) {
                return bitmapLRU;
            } else {
                bitmapLRU = getBitmapOnDiskCache(imageRequestModel);
            }
        }
        return bitmapLRU;
    }

    private void setBitmapOnView(final LoadImageResultModel resultModel) {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                imageLoader.setImageOnBitmap(resultModel);
            }
        };
        handler.post(runnable);
    }

    private Bitmap getBitmapOnDiskCache(final ImageRequestModel imageRequestModel) {
        final Bitmap bitmap;
        if (imageLoader.getIDiskCache() != null) {
            try {
                final File file = imageLoader.getIDiskCache().getFile(imageRequestModel.getUrl());
                if (file != null) {
                    final InputStream inputStream = new FileInputStreamProvider().get(file);
                    bitmap = imageLoader.getResizedBitmap(inputStream, imageRequestModel.getWidth(), imageRequestModel.getHeight());

                    if (bitmap != null) {
                        return bitmap;
                    } else {
                        downLoadImage(imageRequestModel);
                    }
                }
            } catch (final Exception e) {
                e.fillInStackTrace();
            }
        }
        return null;
    }

    private Bitmap downLoadImage(final ImageRequestModel imageRequestModel) {

        Bitmap bitmap = null;
        try {
            final InputStream inputStream = new HttpInputStreamProvider().get(imageRequestModel.getUrl());
            bitmap = imageLoader.getResizedBitmap(inputStream, imageRequestModel.getWidth(), imageRequestModel.getHeight());
        } catch (final IOException e) {
            e.fillInStackTrace();
        }
        if (bitmap != null) {
            imageLoader.cacheBitmap(imageRequestModel, bitmap);
            return bitmap;
        }
        return null;
    }
}
