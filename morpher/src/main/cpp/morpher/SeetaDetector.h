//
// Created by huzongyao on 2019/11/12.
//

#ifndef FACEMORPHME_SEETADETECTOR_H
#define FACEMORPHME_SEETADETECTOR_H

#include <FaceLandmarker.h>
#include <FaceDetector.h>

class SeetaDetector {
public:
    SeetaDetector(const char *fdPath, const char *flPath);

    SeetaFaceInfoArray detect(SeetaImageData &image);

    std::vector<SeetaPointF> mark81(SeetaImageData &image);

    void pushCorners(SeetaImageData &image, std::vector<SeetaPointF> &points);

    virtual ~SeetaDetector();

protected:
    seeta::FaceDetector *FD;
    seeta::FaceLandmarker *FL;
};


#endif //FACEMORPHME_SEETADETECTOR_H
