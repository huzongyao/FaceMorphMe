//
// Created by huzongyao on 2019/9/24.
//

#ifndef FACEMORPHME_SUBDIV2DINDEX_H
#define FACEMORPHME_SUBDIV2DINDEX_H

#include <opencv2/opencv.hpp>

using namespace cv;

class Subdiv2DIndex : public Subdiv2D {
public:
    Subdiv2DIndex(Rect rectangle);

    void getTrianglesIndices(std::vector<int> &triangleList) const;
};


#endif //FACEMORPHME_SUBDIV2DINDEX_H
