export SDK_PATH=/root/android-sdk
export NDK_PATH=/root/android-ndk-r15c

rm -rf android_build
mkdir android_build
cd android_build

cmake -DANDROID_SDK=${SDK_PATH} \
-DANDROID_NDK=${NDK_PATH} \
-DCMAKE_TOOLCHAIN_FILE=${NDK_PATH}/build/cmake/android.toolchain.cmake \
-DANDROID_STL=c++_shared \
-DANDROID_ABI="armeabi-v7a with NEON" \
-DCMAKE_BUILD_TYPE=RELEASE \
-DBUILD_ANDROID_PROJECTS=OFF \
-DBUILD_SHARED_LIBS=OFF \
-DBUILD_opencv_calib3d=ON \
-DBUILD_opencv_core=ON \
-DBUILD_opencv_imgcodecs=ON \
-DBUILD_opencv_imgproc=ON \
-DBUILD_opencv_dnn=ON \
-DBUILD_opencv_features2d=ON \
-DBUILD_opencv_flann=ON \
-DBUILD_opencv_highgui=OFF \
-DBUILD_opencv_java_bindings_generator=OFF \
-DBUILD_opencv_ml=ON \
-DBUILD_opencv_objdetect=ON \
-DBUILD_opencv_photo=ON \
-DBUILD_opencv_shape=ON \
-DBUILD_opencv_stitching=ON \
-DBUILD_opencv_superres=ON \
-DBUILD_opencv_ts=OFF \
-DBUILD_opencv_video=OFF \
-DBUILD_opencv_videoio=OFF \
-DBUILD_opencv_videostab=OFF \
-DBUILD_opencv_world=OFF \
-DBUILD_ZLIB=OFF \
-DBUILD_JPEG=ON \
-DWITH_JPEG=ON \
-DBUILD_JASPER=OFF \
-DWITH_JASPER=OFF \
-DBUILD_PNG=ON \
-DWITH_PNG=ON \
-DBUILD_WEBP=OFF \
-DWITH_WEBP=OFF \
-DBUILD_TIFF=OFF \
-DWITH_TIFF=OFF \
-DBUILD_OPENEXR=OFF \
-DWITH_OPENEXR=OFF \
-DWITH_IMGCODEC_HDR=ON \
-DWITH_IMGCODEC_SUNRASTER=ON \
-DWITH_IMGCODEC_PXM=ON \
-DBUILD_PROTOBUF=OFF \
-DWITH_PROTOBUF=OFF \
-DCV_TRACE=OFF \
-DCMAKE_INSTALL_PREFIX=./native \
$@ ..

cmake --build . --config Release --target install -- -j8
