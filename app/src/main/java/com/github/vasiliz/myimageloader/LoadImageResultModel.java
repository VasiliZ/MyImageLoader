package com.github.vasiliz.myimageloader;

import android.graphics.Bitmap;

public class LoadImageResultModel {

    private final ImageRequestModel mItemRequestModel;
    private Bitmap mBitmap;
    private Exception mException;

    LoadImageResultModel(final ImageRequestModel pItemRequestModel) {
        mItemRequestModel = pItemRequestModel;
    }

    ImageRequestModel getItemRequestModel() {
        return mItemRequestModel;
    }

    Bitmap getBitmap() {
        return mBitmap;
    }

    void setBitmap(final Bitmap pBitmap) {
        mBitmap = pBitmap;
    }

    public Exception getException() {
        return mException;
    }

    void setException(final Exception pException) {
        mException = pException;
    }


}
