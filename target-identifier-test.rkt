#! /usr/bin/env racket

#lang racket

(require rackunit
         "target-identifier.rkt")

; convert between radians and pixels
; radians->pixels
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
(check-equal? (pixels->radians vertical-field-of-view-pixels)
              vertical-field-of-view-radians
              "convert full field of view from pixels to radians")
(check-equal? (pixels->radians (/ vertical-field-of-view-pixels 2))
              (/ vertical-field-of-view-radians 2)
              "convert half field of view from pixels to radians")
(check-equal? (pixels->radians 0)
              0
              "convert beginning of field of view from pixels to radians")

; center-and-bounding-height->upper
(check-equal? (center-and-bounding-height->upper-pixel 40 20)
              30
              "go from center and bounding height to particle top pixel")
; center-and-bounding-height->lower
(check-equal? (center-and-bounding-height->lower-pixel 40 20)
              50
              "go from center and bounding height to particle bottom pixel")

; given a pixel, return how many pixels from it to level
(check-equal? (pixel->pixels-to-level 30)
              (- level-pixel 30))

; given an opposite and an angle, calculate the adjacent
(check-= (adjacent opposite-top-upper
                   (pixels->radians (pixel->pixels-to-level 0)))
         112.5
         (/ 1 3)
         "")