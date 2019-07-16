package com.github.vasiliz.myimageloader;

import android.content.Context;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public final class ImageRequestModel {

    private final String mUrl;
    private final WeakReference<ImageView> mPointImage;
    private int mWidth;
    private int mHeight;
    private final Context mContext;

    WeakReference<ImageView> getPointImage() {
        return mPointImage;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    private ImageRequestModel(final Builder pBuilder) {
        mUrl = pBuilder.mUrl;
        mPointImage = pBuilder.mPointImage;
        mContext = pBuilder.mContext;
        mHeight = pBuilder.mHeight;
        mWidth = pBuilder.mWidth;
    }


    public Context getContext() {
        return mContext;
    }

    String getUrl() {
        return mUrl;
    }


    public void setWidth(final int pWidth) {
        mWidth = pWidth;
    }

    public void setHeight(final int pHeight) {
        mHeight = pHeight;
    }


    public static final class Builder {

        private final ImageLoader mImageLoader;
        private String mUrl;
        private WeakReference<ImageView> mPointImage;
        private Context mContext;
        private int mWidth;
        private int mHeight;

        Builder(final ImageLoader pImageLoader) {
            mImageLoader = pImageLoader;
        }

        Builder with(final Context pContext) {
            mContext = pContext;
            return this;
        }

        public Builder setWith(final int pWidth) {
            mWidth = pWidth;
            return this;
        }


        public Builder setHeight(final int pHeight) {
            mHeight = pHeight;
            return this;
        }

        public Builder load(final String pValue) {
            mUrl = pValue;
            return this;
        }

        public void into(final ImageView pImageView) {
            mPointImage = new WeakReference<>(pImageView);
            mImageLoader.enqueue(this.build());
        }

        ImageRequestModel build() {
            return new ImageRequestModel(this);
        }

        public int getWidth() {
            return mWidth;
        }

        public int getHeight() {
            return mHeight;
        }
    }

}
