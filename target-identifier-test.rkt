#! /usr/bin/env racket

#lang racket

(require rackunit
         plot/utils ; for degrees->radians, radians->degrees
         "target-identifier.rkt")

(define top-particle (particle 50 98))

; convert between radians and pixels
; radians->pixels
; given a number of radians,
;   return the corresponding number of pixels in the camera's picture
(check-equal? (radians->pixels vertical-field-of-view-radians)
              (exact->inexact vertical-field-of-view-pixels)
              "convert full field of view from radians to pixels")
(check-equal? (radians->pixels (/ vertical-field-of-view-radians 2))
              (exact->inexact (/ vertical-field-of-view-pixels 2))
              "convert half field of view from radians to pixels")
(check-equal? (radians->pixels 0)
              0
              "convert beginning of field of view from radians to pixels")

; pixels->radians
; given a number of pixels in the camera's picture
;   return the corresponding number of radians
(check-equal? (pixels->radians vertical-field-of-view-pixels)
              vertical-field-of-view-radians
              "convert full field of view from pixels to radians")
(check-equal? (pixels->radians (/ vertical-field-of-view-pixels 2))
              (/ vertical-field-of-view-radians 2)
              "convert half field of view from pixels to radians")
(check-equal? (pixels->radians 0)
              0
              "convert beginning of field of view from pixels to radians")

; target->lower-pixel
; given the center pixel and the bounding height in pixels for a particle,
;   return the lower most pixel for the particle
(check-equal? (particle->lower-pixel top-particle)
              99
              "go from particle to lower pixel")
; particle->upper-pixel
; given the particle,
;   return the upper most pixel for the particle
(check-equal? (particle->upper-pixel top-particle)
              1
              "go from particle to upper pixel")

; given a pixel, return how many pixels from it to level
;   in the camera's field of view
(check-equal? (pixel->elevation-pixels 0)
              level-pixel)
(check-equal? (pixel->elevation-pixels 1)
              (- level-pixel 1))

(define top-particle-lower-radians
  (pixels->radians
   (pixel->elevation-pixels (particle->lower-pixel top-particle))))
(define top-particle-upper-radians
  (pixels->radians
   (pixel->elevation-pixels (particle->upper-pixel top-particle))))
; given an opposite and an angle, calculate the adjacent
(check-= (adjacent opposite-top-lower top-particle-lower-radians)
         112
         1
         "calculate right triangle adjacent given opposite and angle")

; given opposites and angles, calculate adjacents
(for ([calculated-adjacent
       (adjacents opposite-top-lower top-particle-lower-radians
                  opposite-top-upper top-particle-upper-radians)]
      [actual-adjacent '(112 112)])
     (check-= calculated-adjacent
              actual-adjacent
              1
              "calculate adjacents given opposites and angles"))

; given two adjacent calculations, indicate if they are close enough together
; TODO: calculate these adjacents by going from distance to expected
;       pixel center and bounding box, rounding to nearest, then from those
;       back to adjacents
(define adjacent0 111.44905507822986)
(define adjacent1 112.54600409595365)
(check-true (adjacents-close-enough? (list adjacent0 adjacent1))
            "the adjacents are close enough to be the same triangle")
(check-false (adjacents-close-enough? (list (* 1.2 adjacent0) adjacent1))
             "the adjacents are too different to be the same triangle")

(check-= (radians->degrees
          (particle->lower-elevation-radians (particle 280 122)))
         4.5828125
         1
         (string-append "convert from center and bounding height"
                        " in pixels to lower elevation in radians"))
(check-= (radians->degrees
          (particle->upper-elevation-radians (particle 280 122)))
         13.5421875
         1
         (string-append "convert from center and bounding height"
                        " in pixels to upper elevation in radians"))

; given the center and bounding height of a particle,
;   return whether it's the top target or not
(check-true (top-target? top-particle)
            "top target should be identified as such")
(check-false (top-target?
              (particle (+ 14 (particle-center-y top-particle))
                        (+ 28 (particle-bounding-height top-particle))))
             "not top target if it's 28 pixels bigger and same upper height")

(define middle-particle (particle 280 122))
(check-true (middle-target? middle-particle)
             "middle target should be identified as such")
(check-false (middle-target? top-particle)
             "top target is not the middle target")

(define bottom-particle (particle 506 123))
(check-true (bottom-target? bottom-particle)
             "bottom target should be identified as such")
(check-false (bottom-target? top-particle)
             "top target is not the bottom target")
