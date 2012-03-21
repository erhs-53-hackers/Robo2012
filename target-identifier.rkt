#! /usr/bin/env racket

#lang racket

(require rackunit
         plot/utils ; for degrees->radians, radians->degrees
         )

; number of pixels from top to bottom of camera's field of view
(define vertical-field-of-view-pixels 480)
; number of radians from top to bottom of camera's field of view
(define vertical-field-of-view-radians (degrees->radians (+ 35 (/ 1 4))))
;camera tilts back from vertical by this many radians
(define camera-tilt (degrees->radians 12))
; height in inches of camera from the ground
(define camera-height 54)

; height in inches from the upper side of a target
;  to the lower side of that target
(define target-height 18)

; height in inches of each side of the each target from the ground
(define top-target-upper-height 118)
(define top-target-lower-height (- top-target-upper-height target-height))

(define middle-target-upper-height 64)
(define middle-target-lower-height
  (- middle-target-upper-height target-height))

(define bottom-target-upper-height 48)
(define bottom-target-lower-height
  (- bottom-target-upper-height target-height))

; height from level with the camera to each side of each target:
(define opposite-top-upper
  (- top-target-upper-height camera-height))
(define opposite-top-lower
  (- top-target-lower-height camera-height))
(define opposite-middle-upper
  (- middle-target-upper-height camera-height))
(define opposite-middle-lower
  (- middle-target-lower-height camera-height))
(define opposite-bottom-upper
  (- bottom-target-upper-height camera-height))
(define opposite-bottom-lower
  (- bottom-target-lower-height camera-height))

(define (radians->pixels radians)
  (* (/ radians
        vertical-field-of-view-radians)
     vertical-field-of-view-pixels))

(define (pixels->radians pixels)
  (* (/ pixels
        vertical-field-of-view-pixels)
     vertical-field-of-view-radians))

; pixel that corresponds to level in camera's field of view
;  note: pixels are counted in reverse:
;  top = 0, bottom = vertical-field-of-view-pixels
(define level-pixel
  (+ (/ vertical-field-of-view-pixels 2)
     (radians->pixels camera-tilt)))

; given particle's center pixel and bounding-height in pixels,
;  return the upper or lower pixel
(define (center-and-bounding-height->upper-pixel center bounding-height)
  (- center (/ bounding-height 2)))
(define (center-and-bounding-height->lower-pixel center bounding-height)
  (+ center (/ bounding-height 2)))

(define (pixel->pixels-to-level pixel)
  (- level-pixel pixel))

(define (adjacent opposite radians)
  (/ opposite
     (tan radians)))

(provide (all-defined-out))
