export astrip=/root/android-ndk-r15c/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64/bin/arm-linux-androideabi-strip
${astrip} -g -S -d --strip-debug ./android_build/native/sdk/native/3rdparty/libs/armeabi-v7a/*.a
${astrip} -g -S -d --strip-debug ./android_build/native/sdk/native/staticlibs/armeabi-v7a/*.a


export astrip=/root/android-ndk-r15c/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/aarch64-linux-android-strip
${astrip} -g -S -d --strip-debug ./android_build/native/sdk/native/3rdparty/libs/arm64-v8a/*.a
${astrip} -g -S -d --strip-debug ./android_build/native/sdk/native/staticlibs/arm64-v8a/*.a
