/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java.facedetector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.GraphicOverlay.Graphic;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;

import java.util.Locale;

/**
 * Graphic instance for rendering emojis within the associated graphic overlay view.
 */
public class FaceGraphic extends Graphic {
  private static final String JOY_EMOJI = "\uD83D\uDE02"; // 😂
  private static final String GRINNING_EMOJI = "\uD83D\uDE00"; // 😀
  private static final String SMILING_EMOJI = "\uD83D\ude42"; // 🙂
  private static final String NEUTRAL_EMOJI = "\uD83D\uDE10"; // 😐
  private static final String FROWN_EMOJI = "\uD83D\uDE41"; // 🙁
  private static final String WINKING_EMOJI = "\uD83D\uDE09"; // 😉
  private static final String SLEEPING_EMOJI = "\uD83D\uDE34"; // 😴
  private static final float FACE_POSITION_RADIUS = 8.0f;
  private static final float ID_TEXT_SIZE = 30.0f;
  private static final float BOX_STROKE_WIDTH = 5.0f;

  private final Paint facePositionPaint;
  private volatile Face face;
  private float topLipDistance;
  private float bottomLipDistance;

  FaceGraphic(GraphicOverlay overlay, Face face) {
    super(overlay);

    this.face = face;

    facePositionPaint = new Paint();
  }

  /** Draws the emojis over the faces depending on expression features. */
  @Override
  public void draw(Canvas canvas) {

    Face face = this.face;
    if (face == null) {
      return;
    }

    // Get center points of box around face
    float x = translateX(face.getBoundingBox().centerX());
    float y = translateY(face.getBoundingBox().centerY());

    // Calculate helpful positions
    float xOffset = scale(face.getBoundingBox().width() / 2.0f);
    float yOffset = scale(face.getBoundingBox().height() / 2.0f);
    float left = x - xOffset;
    float top = y - yOffset;
    float lineHeight = ID_TEXT_SIZE + BOX_STROKE_WIDTH;
    float yLabelOffset = (face.getTrackingId() == null) ? 0 : -lineHeight;

    // Calculate face size
    float faceWidth = face.getBoundingBox().width();
    float faceHeight = face.getBoundingBox().height();

    // Paint object for debugging labels
    Paint labelPaint = new Paint();
    labelPaint.setColor(Color.WHITE);
    labelPaint.setTextSize(ID_TEXT_SIZE);

    // Gets mouth shape information to estimate size of smile or frown
    for (FaceContour contour : face.getAllContours()) {
      if (contour.getFaceContourType() == 9) { // UPPER_LIP_BOTTOM

        // get corner and middle coordinates of bottom lip
        PointF top_corner = contour.getPoints().get(0);
        PointF top_middle = contour.getPoints().get(4);

        // calculate distance between y positions of points
        topLipDistance = top_middle.y - top_corner.y;

        // for debugging
//        canvas.drawText(
//                "TOP Distance: " + topLipDistance,
//                left,
//                top += yLabelOffset,
//                labelPaint
//        );
//        yLabelOffset += lineHeight;
      }
      if (contour.getFaceContourType() == 11) { // LOWER_LIP_BOTTOM

        // get corner and middle coordinates of bottom lip
        PointF bottom_corner = contour.getPoints().get(0);
        PointF bottom_middle = contour.getPoints().get(4);

        // calculate distance between y positions of points
        bottomLipDistance = bottom_middle.y - bottom_corner.y;

        // for debugging
//        canvas.drawText(
//                "BOTTOM Distance: " + bottomLipDistance,
//                left,
//                top += yLabelOffset,
//                labelPaint
//        );
//        yLabelOffset += lineHeight;
      }
//      for (PointF point : contour.getPoints()) {
//        canvas.drawCircle(
//                translateX(point.x), translateY(point.y), FACE_POSITION_RADIUS, facePositionPaint);
//      }
    }

    // Determine which emoji to display
    String emoji = NEUTRAL_EMOJI;
    boolean eyesWinking = face.getLeftEyeOpenProbability() < 0.5 || face.getRightEyeOpenProbability() < 0.5;
    boolean eyesClosed = face.getLeftEyeOpenProbability() < 0.5  && face.getRightEyeOpenProbability() < 0.5;

    if (face.getSmilingProbability() != null && !eyesWinking && !eyesClosed) {
      if (face.getSmilingProbability() > 0.99 && bottomLipDistance > 20) {
        emoji = JOY_EMOJI;
      } else if (face.getSmilingProbability() > 0.75 && bottomLipDistance > 12){
        emoji = GRINNING_EMOJI;
      } else if (face.getSmilingProbability() > 0.5) {
        emoji = SMILING_EMOJI;
      } else if (face.getSmilingProbability() < 0.5 && topLipDistance < -1) {
        emoji = FROWN_EMOJI;
      }
    }
    else if (face.getLeftEyeOpenProbability() != null && face.getRightEyeOpenProbability() != null) {
      if (eyesClosed) {
        emoji = SLEEPING_EMOJI;
      } else if (eyesWinking) {
        emoji = WINKING_EMOJI;
      }
    }

    // Emoji paint object
    Paint emojiPaint = new Paint();
    float emojiSize = (faceWidth + faceHeight) * 1.7f;
    emojiPaint.setTextSize(emojiSize);
    emojiPaint.setAlpha(180);

    // Draw the emoji
    canvas.drawText(
            emoji,
            x - (emojiSize / 1.7f), // remove 3*
            y + emojiSize / 2.5f, // + not -
            emojiPaint);

  }

}
