//
// Created by huzongyao on 2019/9/21.
//

#include "MorphUtils.h"
#include "NdkUtils.h"

/**
* 0---1---2
* |       |
* 7       3
* |       |
* 6---5---4
*/
void MorphUtils::getImageCornerPoints(int width, int height, std::vector<Point2f> &points) {
    float r = width - 1.0f, b = height - 1.0f;
    float hr = width / 2.f - 1.f;
    float hb = height / 2.f - 1.f;
    points.emplace_back(0, 0);
    points.emplace_back(hr, 0);
    points.emplace_back(r, 0);
    points.emplace_back(r, hb);
    points.emplace_back(r, b);
    points.emplace_back(hr, b);
    points.emplace_back(0, b);
    points.emplace_back(0, hb);
}

void MorphUtils::getTrianglesPoints(Subdiv2D &subDiv, std::vector<Point2f> &points) {
    std::vector<Vec6f> triangleList;
    subDiv.getTriangleList(triangleList);
    for (auto t : triangleList) {
        points.emplace_back(t[0], t[1]);
        points.emplace_back(t[2], t[3]);
        points.emplace_back(t[4], t[5]);
    }
}

void MorphUtils::getPointsWithAlpha(std::vector<Point2f> &src, std::vector<Point2f> &dst,
                                    std::vector<Point2f> &out, float alpha) {
    if (src.size() == dst.size()) {
        if (alpha > 1)alpha = 1;
        if (alpha < 0)alpha = 0;
        float alphaSrc = 1 - alpha;
        out.clear();
        for (int i = 0; i < src.size(); i++) {
            float outX = src[i].x * alphaSrc + dst[i].x * alpha;
            float outY = src[i].y * alphaSrc + dst[i].y * alpha;
            out.emplace_back(outX, outY);
        }
    } else {
        LOGE("Input Count Not Match[%d][%d]", src.size(), dst.size());
    }
}

void MorphUtils::morphTriangle(Mat &img1, Mat &img2, Mat &img, std::vector<Point2f> &t1,
                               std::vector<Point2f> &t2, std::vector<Point2f> &t, float alpha) {
    // Find bounding rectangle for each triangle
    Rect r = boundingRect(t); // new triangle rect
    Rect r1 = boundingRect(t1);
    Rect r2 = boundingRect(t2);

    // Offset points by left top corner of the respective rectangles
    std::vector<Point2f> t1Rect, t2Rect, tRect;
    std::vector<Point> tRectInt;
    for (int i = 0; i < 3; i++) {
        tRect.emplace_back(t[i].x - r.x, t[i].y - r.y);
        tRectInt.emplace_back(t[i].x - r.x, t[i].y - r.y); // for fillConvexPoly
        t1Rect.emplace_back(t1[i].x - r1.x, t1[i].y - r1.y);
        t2Rect.emplace_back(t2[i].x - r2.x, t2[i].y - r2.y);
    }

    // Get mask by filling triangle
    Mat mask = Mat::zeros(r.height, r.width, CV_32FC4);
    // fill the poly
    fillConvexPoly(mask, tRectInt, Scalar(1.0, 1.0, 1.0, 1.0), LINE_AA);

    // Apply warpImage to small rectangular patches
    Mat img1Rect, img2Rect;
    // copy original triangles image
    img1(r1).copyTo(img1Rect);
    img2(r2).copyTo(img2Rect);

    Mat warpImage1 = Mat::zeros(r.height, r.width, img1Rect.type());
    Mat warpImage2 = Mat::zeros(r.height, r.width, img2Rect.type());

    applyAffineTransform(warpImage1, img1Rect, t1Rect, tRect);
    applyAffineTransform(warpImage2, img2Rect, t2Rect, tRect);

    // Alpha blend rectangular patches
    Mat imgRect = (1.0 - alpha) * warpImage1 + alpha * warpImage2;

    // Copy triangular region of the rectangular patch to the output image
    multiply(imgRect, mask, imgRect);
    multiply(img(r), Scalar(1.0, 1.0, 1.0, 1.0) - mask, img(r));
    img(r) = img(r) + imgRect;
}

void MorphUtils::applyAffineTransform(Mat &warpImage, Mat &src, std::vector<Point2f> &srcTri,
                                      std::vector<Point2f> &dstTri) {
    // Given a pair of triangles, find the affine transform.
    Mat warpMat = getAffineTransform(srcTri, dstTri);
    // Apply the Affine Transform just found to the src image
    warpAffine(src, warpImage, warpMat, warpImage.size(), INTER_LINEAR, BORDER_REFLECT_101);
}

