//
// Created by huzongyao on 2019/9/21.
//

#ifndef FACEMORPHME_MORPHUTILS_H
#define FACEMORPHME_MORPHUTILS_H

#include <opencv2/opencv.hpp>

using namespace cv;

class MorphUtils {
public:
    static void getImageCornerPoints(int width, int height, std::vector<Point2f> &points);

    static void getTrianglesPoints(Subdiv2D &subDiv, std::vector<Point2f> &points);

    /**
     * calculate the out point by src and dst and alpha
     * @param src
     * @param dst
     * @param out  out = src * (1- alpha) + dst * alpha
     * @param alpha
     */
    static void getPointsWithAlpha(std::vector<Point2f> &src, std::vector<Point2f> &dst,
                                   std::vector<Point2f> &out, float alpha);

    // Warps and alpha blends triangular regions from img1 and img2 to img
    static void morphTriangle(Mat &img1, Mat &img2, Mat &img, std::vector<Point2f> &t1,
                              std::vector<Point2f> &t2, std::vector<Point2f> &t, float alpha);

protected:
    // Apply affine transform calculated using srcTri and dstTri to src
    static void applyAffineTransform(Mat &warpImage, Mat &src, std::vector<Point2f> &srcTri,
                                     std::vector<Point2f> &dstTri);
};


#endif //FACEMORPHME_MORPHUTILS_H
