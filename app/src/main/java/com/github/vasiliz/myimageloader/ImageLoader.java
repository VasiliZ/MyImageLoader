package com.github.vasiliz.myimageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.github.vasiliz.myimageloader.diskCache.CacheBitmap;
import com.github.vasiliz.myimageloader.diskCache.IDiskCache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;

public final class ImageLoader {

    private final ThreadPoolExecutor mThreadPoolExecutor;
    private final Object mSync;
    private final LruCache<String, Bitmap> mLruCache;
    private final LinkedBlockingDeque<ImageRequestModel> mImageQueue;
    private static final int MAX_MEMORY_FOR_LRU_CACHE = 1024 * 1024 * 8;

    IDiskCache getIDiskCache() {
        return mIDiskCache;
    }

    private final IDiskCache mIDiskCache;

    private static final class ImageLoaderHolder {

        private static final ImageLoader INSTANCE = new ImageLoader();

    }

    public static ImageLoader getInstance() {
        return ImageLoaderHolder.INSTANCE;
    }

    private ImageLoader() {

        mSync = new Object();

        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool((int) (Runtime.getRuntime().availableProcessors() * 1.5));
        mImageQueue = new LinkedBlockingDeque<>();
        mLruCache = new LruCache<String, Bitmap>(getLruCacheSize()) {

            @Override
            protected int sizeOf(@NonNull final String key, @NonNull final Bitmap value) {
                return key.length() + value.getByteCount();
            }
        };

        mIDiskCache = new CacheBitmap();
    }

    LinkedBlockingDeque<ImageRequestModel> getImageQueue() {
        //noinspection AssignmentOrReturnOfFieldWithMutableType
        return mImageQueue;
    }

    LruCache<String, Bitmap> getLruCache() {
        return mLruCache;
    }

    private int getLruCacheSize() {
        return (int) Math.min((Runtime.getRuntime().maxMemory() / 4), (MAX_MEMORY_FOR_LRU_CACHE));
    }

    public ImageRequestModel.Builder with(final Context pContext) {
        mIDiskCache.setContext(pContext);
        return new ImageRequestModel.Builder(this).with(pContext);
    }

    void enqueue(final ImageRequestModel pImageRequestModel) {
        final ImageView imageView = pImageRequestModel.getPointImage().get();

        if (imageView == null) {
            return;
        }

        imageView.setImageBitmap(null);

        if (checkImageSize(pImageRequestModel)) {
            imageView.setTag(pImageRequestModel.getUrl());
            mImageQueue.addFirst(pImageRequestModel);
            dispatchToLoadImage();
        } else {
            waitImageRequest(pImageRequestModel);
        }
    }

    private void waitImageRequest(final ImageRequestModel pImageRequestModel) {
        final ImageView imageView = pImageRequestModel.getPointImage().get();

        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                final ImageView image = pImageRequestModel.getPointImage().get();

                if (image == null) {
                    return true;
                }

                image.getViewTreeObserver().removeOnPreDrawListener(this);

                if (image.getHeight() > 0 && image.getWidth() > 0) {
                    pImageRequestModel.setWidth(image.getWidth());
                    pImageRequestModel.setHeight(image.getHeight());

                    enqueue(pImageRequestModel);
                }
                return true;

            }
        });

    }

    private boolean checkImageSize(final ImageRequestModel pImageRequestModel) {

        if (pImageRequestModel.getWidth() > 0 && pImageRequestModel.getHeight() > 0) {
            return true;
        }

        final ImageView imageView = pImageRequestModel.getPointImage().get();

        if (imageView != null && imageView.getWidth() > 0 && imageView.getHeight() > 0) {
            pImageRequestModel.setHeight(imageView.getHeight());
            pImageRequestModel.setWidth(imageView.getWidth());
            return true;
        }

        return false;

    }

    private void dispatchToLoadImage() {
        final Runnable imageRunnable = new ImageRunnable();
        mThreadPoolExecutor.execute(imageRunnable);

    }

    void setImageOnBitmap(final LoadImageResultModel pLoadImageResultModel) {
        if (pLoadImageResultModel != null) {
            final ImageRequestModel imageRequestModel = pLoadImageResultModel.getItemRequestModel();
            final ImageView imageView = imageRequestModel.getPointImage().get();
            if (imageView != null) {
                final Object tagImageView = imageView.getTag();
                if (tagImageView != null && tagImageView.equals(imageRequestModel.getUrl())) {
                    imageView.setImageBitmap(pLoadImageResultModel.getBitmap());
                }
            }
        }
    }

    Bitmap getResizedBitmap(final InputStream pInputStream,
                            final int pWidth,
                            final int pHeight)
            throws IOException {

        final BitmapFactory.Options options = new BitmapFactory.Options();

        final ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream(pInputStream.available());
        final byte[] part = new byte[1 << 16];
        int bytesForReading;

        while ((bytesForReading = pInputStream.read(part)) > 0) {
            byteArrayOutputStream.write(part, 0, bytesForReading);
        }

        final byte[] bytes = byteArrayOutputStream.toByteArray();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        options.inSampleSize = calculateSampleSize(options, pWidth, pHeight);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

    }

    private static int calculateSampleSize(final BitmapFactory.Options pOptions,
                                           final int pReqWidth, final int pReqHeigth) {
        final int height = pOptions.outHeight;
        final int width = pOptions.outWidth;
        int size = 1;

        if (height > pReqHeigth || width > pReqWidth) {
            final int divHeigth = height / 2;
            final int divWidth = width / 2;

            while (divWidth / size >= pReqWidth && divHeigth / size > pReqWidth) {
                size *= 2;
            }
        }
        return size;
    }

    void cacheBitmap(final ImageRequestModel pImageRequestModel, final Bitmap pBitmap) {
        synchronized (mSync) {
            mLruCache.put(pImageRequestModel.getUrl(), pBitmap);

            try {
                if (mIDiskCache != null) {
                    mIDiskCache.save(pImageRequestModel.getUrl(), pBitmap);
                }
            } catch (final IOException pE) {
                pE.fillInStackTrace();
            }
        }
    }

}
