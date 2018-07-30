
package org.tensorflow.demo;

import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import org.tensorflow.demo.AutoFitTextureView;
import org.tensorflow.demo.CameraConnectionFragment;
import org.tensorflow.demo.R;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;

import java.io.IOException;
import java.util.List;

/**
 * Created by mgo983 on 7/18/18.
 */

public class PhotoDetectorFragment extends Fragment {

    private static final Logger LOGGER = new Logger();

    private Size desiredSize;

    /**
     * The layout identifier to inflate for this Fragment.
     */
    private int layout;
    private ConnectionCallback connectionCallback;


    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView textureView;

    public PhotoDetectorFragment(      final ConnectionCallback connectionCallback,
                                       final int layout, final Size desiredSize) {
        this.connectionCallback = connectionCallback;
        this.layout = layout;
        this.desiredSize = desiredSize;
    }

    public PhotoDetectorFragment() {

    }




    /**
     * {@link android.view.TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                    //textureView.setAspectRatio(s.height, s.width);

                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            };



    @Override
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Callback for Activities to use to initialize their data once the
     * selected preview size is known.
     */
    public interface ConnectionCallback {
        void onPreviewSizeChosen(Size size, int cameraRotation);
    }

    public static PhotoDetectorFragment newInstance(
            final ConnectionCallback callback,
            //final ImageReader.OnImageAvailableListener imageListener,
            final int layout,
            final Size inputSize) {
        return new PhotoDetectorFragment(callback, layout, inputSize);
    }



}
