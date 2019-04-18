package com.github.vasiliz.myimageloader.streams;

import java.io.IOException;
import java.io.InputStream;

public interface IStreamContract<T> {

    InputStream get(T pUrl) throws IOException;

}
