package com.github.vasiliz.myimageloader;

import android.graphics.Bitmap;

public class LoadImageResultModel {

    private final ImageRequestModel mItemRequestModel;
    private Bitmap mBitmap;
    private Exception mException;

    public LoadImageResultModel(final ImageRequestModel pItemRequestModel) {
        mItemRequestModel = pItemRequestModel;
    }

    public ImageRequestModel getItemRequestModel() {
        return mItemRequestModel;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(final Bitmap pBitmap) {
        mBitmap = pBitmap;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(final Exception pException) {
        mException = pException;
    }


}
