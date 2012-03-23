#! /usr/bin/env racket

#lang racket

(require rackunit
         "target-identifier.rkt")

(define top-center-mass-y 50)
(define top-bounding-rect-height 98)

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

; center-and-bounding-height->lower
; given the center pixel and the bounding height in pixels for a particle,
;   return the lower most pixel for the particle
(check-equal? (center-and-bounding-height->lower-pixel
               top-center-mass-y
               top-bounding-rect-height)
              99
              "go from center and bounding height to particle lower pixel")
; center-and-bounding-height->upper
; given the center pixel and bounding height in pixels for a particle,
;   return the upper most pixel for the particle
(check-equal? (center-and-bounding-height->upper-pixel
               top-center-mass-y
               top-bounding-rect-height)
              1
              "go from center and bounding height to particle upper pixel")

; given a pixel, return how many pixels from it to level
;   in the camera's field of view
(check-equal? (pixel->pixels-to-level 0)
              level-pixel)
(check-equal? (pixel->pixels-to-level 1)
              (- level-pixel 1))

(define top-particle-lower-pixel
  (center-and-bounding-height->lower-pixel
   top-center-mass-y
   top-bounding-rect-height))
(define top-particle-lower-radians
  (pixels->radians (pixel->pixels-to-level top-particle-lower-pixel)))
(define top-particle-upper-pixel
  (center-and-bounding-height->upper-pixel
   top-center-mass-y top-bounding-rect-height))
(define top-particle-upper-radians
  (pixels->radians (pixel->pixels-to-level top-particle-upper-pixel)))
; given an opposite and an angle, calculate the adjacent
(check-= (adjacent opposite-top-upper top-particle-upper-radians)
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

; given the center and bounding height of a particle,
;   return whether it's the top target or not
(check-true (top-target? top-center-mass-y top-bounding-rect-height))
(check-false (top-target? (+ 40 top-center-mass-y) top-bounding-rect-height)
             "not top target if it's 40 pixels lower and same size")
(check-false (top-target? (+ 14 top-center-mass-y)
                          (+ 28 top-bounding-rect-height))
             "not top target if it's 28 pixels bigger and same upper height")
