# Emojify

## Introduction

This Android app builds upon the ML Kit Quickstart app using its [face detection](https://developers.google.com/ml-kit/vision/face-detection/android) feature to transform human facial expressions into emojis! The purpose is to help users identify the emotions of others in real time. Emojis have been shown to improve digital communication, so applying these familiar faces to human faces in real time can assist those who might struggle with reading the emotions of others in their physical world. 

<img src="../screenshots/quickstart-picker.png" width="220"/> <img src="../screenshots/quickstart-image-labeling.png" width="220"/> <img src="../screenshots/quickstart-object-detection.png" width="220"/> <img src="../screenshots/quickstart-pose-detection.png" width="220"/>

## How the app works

This app uses the camera preview as input and utilizes the Face Detection API to return information that can be used to map against different emojis. The values used for this mapping include:
* Probability of smiling
* Probability of left and/or right eye being open
* Point positions of the lip contour

Depending on the probability of smiling in addition to the "strength" of the smile using the middle and corner lip positions, different variations of smiling emojis are applied over the face. If one eye is closed, the wink emoji is used. If both eyes are closed, the sleeping emoji is used.

## Limitations

Originally the idea was to have this application work using AR glasses, making it a tool that can integrate seamlessly as one walks around wearing the glasses. Unfortunately, we were unable to get this working using the glasses. Another limitation was the lip contour data and the difficulty with accurately capturing frowns from these point positions. The point positions along the lip were not as helpful as we thought to detect "sad" faces. Perhaps the "face mesh" feature of the ML Kit would have proven to be more useful.

### Group members
* Jessica Draper
* Fady German
* Fadime Konuk
* Moreno Marrara