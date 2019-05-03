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

    public static void closeStream(final InputStream pInputStream){
        if (pInputStream!=null){
            try {
                pInputStream.close();
            } catch (final IOException pE) {
                pE.fillInStackTrace();
            }
        }
    }

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

    private synchronized Bitmap getBitmapOnMemCache(final ImageRequestModel imageRequestModel) {
        Bitmap bitmapLRU;

        bitmapLRU = imageLoader.getLruCache().get(imageRequestModel.getUrl());
        if (bitmapLRU != null) {
            return bitmapLRU;
        } else {
            bitmapLRU = getBitmapOnDiskCache(imageRequestModel);
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
        InputStream inputStream = null;
        if (imageLoader.getIDiskCache() != null) {
            try {
                final File file = imageLoader.getIDiskCache().getFile(imageRequestModel.getUrl());
                if (file != null) {
                    inputStream = new FileInputStreamProvider().get(file);
                    bitmap = imageLoader.getResizedBitmap(inputStream, imageRequestModel.getWidth(), imageRequestModel.getHeight());

                    if (bitmap != null) {
                        return bitmap;
                    } else {
                        downLoadImage(imageRequestModel);
                    }
                }
            } catch (final Exception e) {
                e.fillInStackTrace();
            } finally {
                closeStream(inputStream);
            }
        }
        return null;
    }

    private Bitmap downLoadImage(final ImageRequestModel imageRequestModel) {

        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = new HttpInputStreamProvider().get(imageRequestModel.getUrl());
            bitmap = imageLoader.getResizedBitmap(inputStream, imageRequestModel.getWidth(), imageRequestModel.getHeight());
        } catch (final IOException e) {
            e.fillInStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException pE) {
                    pE.fillInStackTrace();
                }
            }
        }
        if (bitmap != null) {
            imageLoader.cacheBitmap(imageRequestModel, bitmap);
            return bitmap;
        }
        return null;
    }
}
