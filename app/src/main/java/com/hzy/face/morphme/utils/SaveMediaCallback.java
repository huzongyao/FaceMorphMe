package com.hzy.face.morphme.utils;

import java.io.File;

public interface SaveMediaCallback {
    void onSuccess(File dst);

    void onFail();
}
