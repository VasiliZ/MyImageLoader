package com.github.vasiliz.myimageloader;

import android.content.Context;
import android.widget.ImageView;
import android.widget.VideoView;

import java.lang.ref.WeakReference;

public final class ImageRequestModel {

    private String mUrl;
    private WeakReference<ImageView> mPointImage;
    private VideoView mViewWeakReference;
    private int mWidth;
    private int mHeight;
    private Context mContext;

    public WeakReference<ImageView> getPointImage() {
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
        mViewWeakReference = pBuilder.mVideoView;
    }

    public VideoView getViewWeakReference() {
        return mViewWeakReference;
    }

    public Context getContext() {
        return mContext;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(final String pUrl) {
        mUrl = pUrl;
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
        private VideoView mVideoView;
        private Context mContext;

        public Builder(final ImageLoader pImageLoader) {
            mImageLoader = pImageLoader;
        }

        public Builder with(final Context pContext) {
            mContext = pContext;
            return this;
        }

        public Builder load(final String pValue) {
            mUrl = pValue;
            return this;
        }

        public void into(final ImageView pValue) {
            mPointImage = new WeakReference<>(pValue);
            mImageLoader.enqueue(this.build());
        }

        public void into(final VideoView pVideoView) {
            mVideoView = pVideoView;
            mImageLoader.enqueueVideo(this.build());
        }

        ImageRequestModel build() {
            return new ImageRequestModel(this);
        }

    }

}
