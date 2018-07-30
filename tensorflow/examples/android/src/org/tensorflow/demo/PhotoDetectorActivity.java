package org.tensorflow.demo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.tensorflow.demo.CameraActivity;
import org.tensorflow.demo.Classifier;
import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.OverlayView;
import org.tensorflow.demo.R;
import org.tensorflow.demo.TensorFlowMultiBoxDetector;
import org.tensorflow.demo.TensorFlowObjectDetectionAPIModel;
import org.tensorflow.demo.TensorFlowYoloDetector;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.photoSearch.ImageExplanationActivity;
import org.tensorflow.demo.tracking.MultiBoxTracker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Created by mgo983 on 7/11/18.
 */


    /*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

    /**
     * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
     * objects.
     */
    public abstract class PhotoDetectorActivity extends Activity{

        private int[] rgbBytes = null;
        protected int previewWidth = 0;
        protected int previewHeight = 0;
        private static final Logger LOGGER = new Logger();
        private byte[][] yuvBytes = new byte[3][];

        private Runnable imageConverter;
        private Runnable postInferenceCallback;

        private int yRowStride;

        private boolean debug = false;
        private Handler handler;



        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.image_dialog);

            ImageView imageView = findViewById(R.id.new_dialog);

            Intent intent = this.getIntent();
            String imageUrl = intent.getStringExtra(ImageExplanationActivity.EXTRA_DIALOG_IMAGE);
            Glide
                    .with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(imageView);

            //onPreview Frame
            //Bitmap b = ((BitmapDrawable)imageView.getBackground()).getBitmap();
//            Uri uri = Uri.parse(imageUrl);
            setFragment();
            Bitmap b = BitmapFactory.decodeFile(imageUrl);
            try {
                // Initialize the storage bitmaps once when the resolution is known.
                if (rgbBytes == null) {
                    //Camera.Size previewSize = imageView.getMeasuredHeight() camera.getParameters().getPreviewSize();
                    previewHeight = b.getHeight();
                    previewWidth = b.getWidth();
                    rgbBytes = new int[previewWidth * previewHeight];
                    onPreviewSizeChosen(new Size(previewWidth, previewHeight), 90);
                }
            } catch (final Exception e) {
                LOGGER.e(e, "Exception!" + "PreviewHeight: " + previewHeight + "PreviewWidth: " + previewWidth );
                return;
            }

            //convert image from imageView to bytes

            //Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] bytes = baos.toByteArray();


            lastPreviewFrame = bytes;
            yuvBytes[0] = bytes;
            yRowStride = previewWidth;

            imageConverter =
                    new Runnable() {
                        @Override
                        public void run() {
                            ImageUtils.convertYUV420SPToARGB8888(bytes, previewWidth, previewHeight, rgbBytes);
                        }
                    };

            /*postInferenceCallback =
                    new Runnable() {
                        @Override
                        public void run() {
                            camera.addCallbackBuffer(bytes);
                            isProcessingFrame = false;
                        }
                    };*/

            processImage();
        }

        //On Create
        private byte[] lastPreviewFrame;

        protected int[] getRgbBytes() {
            imageConverter.run();
            return rgbBytes;
        }

        protected void fillBytes(final Image.Plane[] planes, final byte[][] yuvBytes) {
            // Because of the variable row stride it's not possible to know in
            // advance the actual necessary dimensions of the yuv planes.
            for (int i = 0; i < planes.length; ++i) {
                final ByteBuffer buffer = planes[i].getBuffer();
                if (yuvBytes[i] == null) {
                    LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity());
                    yuvBytes[i] = new byte[buffer.capacity()];
                }
                buffer.get(yuvBytes[i]);
            }
        }

        public boolean isDebug() {
            return debug;
        }

        public void requestRender() {
            final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
            if (overlay != null) {
                overlay.postInvalidate();
            }
        }

        public void addCallback(final OverlayView.DrawCallback callback) {
            final OverlayView overlay = (OverlayView) findViewById(R.id.debug_overlay);
            if (overlay != null) {
                overlay.addCallback(callback);
            }
        }

        public void onSetDebug(final boolean debug) {}

        @Override
        public boolean onKeyDown(final int keyCode, final KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                    || keyCode == KeyEvent.KEYCODE_BUTTON_L1 || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                debug = !debug;
                requestRender();
                onSetDebug(debug);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        protected int getLuminanceStride() {
            return yRowStride;
        }

        protected byte[] getLuminance() {
            return yuvBytes[0];
        }

        protected synchronized void runInBackground(final Runnable r) {
            if (handler != null) {
                handler.post(r);
            }
        }

        protected void setFragment() {


            Fragment fragment;
                        PhotoDetectorFragment.newInstance(
                                new PhotoDetectorFragment.ConnectionCallback() {
                                    @Override
                                    public void onPreviewSizeChosen(Size size, int rotation) {
                                        previewHeight = size.getHeight();
                                        previewWidth = size.getWidth();
                                        PhotoDetectorActivity.this.onPreviewSizeChosen(size, rotation);
                                    }
                                },
                                getLayoutId(),
                                getDesiredPreviewFrameSize());

            }

        protected abstract void processImage();
        protected abstract void onPreviewSizeChosen(final Size size, final int rotation);
        protected abstract int getLayoutId();
        protected abstract Size getDesiredPreviewFrameSize();
    }


