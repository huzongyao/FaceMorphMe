# FaceMorphMe
FaceMorph 在Android平台的实现

#### Details
* FaceMorph是指将一张人脸照片平滑过渡到另一张，
算法主要是参考这篇文章：[Face Morph Using OpenCV](https://www.learnopencv.com/face-morph-using-opencv-cpp-python/)。

* 图像的平滑过渡在电影中有广泛应用，图像过渡的思想也很简单，可以用改变透明度来实现，场景过渡时添加若干半透明中间图片，
透明度参数alpha取0-1，假设需要过渡图片A和图片B，中间图片C=A*(1-alpha) + B*alpha，alpha从0过渡到1，图片便可以从A平滑过渡到B。

* 不过要是对于人像，我们如果能在过度透明度的同时，也慢慢过渡人物五官的位置，这样会让我们得到更加平滑的过渡效果，
如果我找一张我很多年前拍的照片，和我现在的照片做Morph，我甚至可以发现我的相貌哪些地方发生了变化。

##### Process:
1. 人脸识别，人脸关键点识别
 * 人脸关键点识别一般可以采用[Dlib](http://dlib.net/)或者
 [Stasm](http://www.milbo.users.sonic.net/stasm/) + [OpenCV](https://opencv.org/),
 * 使用Dlib的话可以参照[face_landmark_detection](http://dlib.net/face_landmark_detection_ex.cpp.html)
 来获取人脸68个关键点，不依赖于OpenCV，但是需要61M的模型文件。
 * 使用Stasm可以参考示例[minimal](http://www.milbo.users.sonic.net/stasm/minimal.html)
 使用它可以获取77个人脸关键点, 只需要OpenCV的几个xml的模型文件，
 体积比dlib的模型小，比较适合移动端设备使用，所以我选择了这个。接口如下：
```c
int stasm_search_single(     // wrapper for stasm_search_auto and friends
    int*         foundface,  // out: 0=no face, 1=found face
    float*       landmarks,  // out: x0, y0, x1, y1, ..., caller must allocate
    const char*  img,        // in: gray image data, top left corner at 0,0
    int          width,      // in: image width
    int          height,     // in: image height
    const char*  imgpath,    // in: image path, used only for err msgs and debug
    const char*  datadir);   // in: directory of face detector files
```

2. 点集的三角剖分
 * 所谓Delaunay三角剖分，是一种算法，研究如何把一个离散几何剖分成不均匀的三角形网格，这就是离散点的三角剖分问题，
 散点集的三角剖分，对数值分析以及图形学来说，都是极为重要的一项处理技术。
```c
Rect rect(0, 0, info.width, info.height);
Subdiv2D subDiv(rect);
subDiv.insert(facePts);
std::vector<Point2f> trianglePts;
MorphUtils::getTrianglesPoints(subDiv, trianglePts);
```

3. 三角形变换以及透明度变换
* Alpha变化的同时，加上人脸关键点位置的变换，使得变换更加平滑。
``` c
// 填充凸多边形
void fillConvexPoly(InputOutputArray img, InputArray points,
                 const Scalar& color, int lineType = LINE_8,
                 int shift = 0);
// 仿射变换
void warpAffine( InputArray src, OutputArray dst,
                  InputArray M, Size dsize,
                  int flags = INTER_LINEAR,
                  int borderMode = BORDER_CONSTANT,
                  const Scalar& borderValue = Scalar());
```

#### Reference
1. https://www.learnopencv.com/face-morph-using-opencv-cpp-python/
2. https://github.com/Yalantis/uCrop

### About Me
 * GitHub: [https://huzongyao.github.io/](https://huzongyao.github.io/)
 * ITEye博客：[https://hzy3774.iteye.com/](https://hzy3774.iteye.com/)
 * 新浪微博: [https://weibo.com/hzy3774](https://weibo.com/hzy3774)

### Contact To Me
 * QQ: [377406997](https://wpa.qq.com/msgrd?v=3&uin=377406997&site=qq&menu=yes)
 * Gmail: [hzy3774@gmail.com](mailto:hzy3774@gmail.com)
 * Foxmail: [hzy3774@qq.com](mailto:hzy3774@qq.com)
 * WeChat: hzy3774
