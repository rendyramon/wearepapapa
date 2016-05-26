/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotcast.vr.sbsplayer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.view.Surface;

public class VideoSurfaceView extends GLSurfaceView {
	private static final String TAG = "VideoSurfaceView";
	private static final int SLEEP_TIME_MS = 1000;
	private VideoRender mRenderer;
	private MediaPlayer mMediaPlayer = null;

	public boolean isSpliteScreen() {
		return spliteScreen;
	}

	public void setSpliteScreen(boolean spliteScreen) {
		this.spliteScreen = spliteScreen;
		if(null != mRenderer){
			mRenderer.setRenderSpliteScreen(spliteScreen);
		}
	}

	//	public static VideoSurfaceView that;
	private boolean spliteScreen;

	public VideoSurfaceView(Context context, MediaPlayer mp) {
		super(context);
//		that = this;
		setEGLContextClientVersion(2);
		mMediaPlayer = mp;
		mRenderer = new VideoRender(mMediaPlayer, this);
		mRenderer.setMediaPlayer(mMediaPlayer);
		setRenderer(mRenderer);
	}

	@Override
	public void onResume() {
		queueEvent(new Runnable() {
			public void run() {
				mRenderer.setMediaPlayer(mMediaPlayer);
			}
		});
		super.onResume();
	}

	public void startTest() throws Exception {
		Thread.sleep(SLEEP_TIME_MS);
		mMediaPlayer.start();
		Thread.sleep(SLEEP_TIME_MS * 5);
		mMediaPlayer.setSurface(null);
		while (mMediaPlayer.isPlaying()) {
			Thread.sleep(SLEEP_TIME_MS);
		}
	}

	/**
	 * A GLSurfaceView implementation that wraps TextureRender. Used to render
	 * frames from a video decoder to a View.
	 */
	private static class VideoRender implements Renderer, SurfaceTexture.OnFrameAvailableListener {
		private static String TAG = "VideoRender";
		private TextureRender mTextureRender;
		private SurfaceTexture mSurfaceTexture;
		private boolean updateSurface = false;
		private boolean renderSpliteScreen;

		public boolean isRenderSpliteScreen() {
			return renderSpliteScreen;
		}

		public void setRenderSpliteScreen(boolean renderSpliteScreen) {
			this.renderSpliteScreen = renderSpliteScreen;
			mTextureRender.setSpliteScreen(renderSpliteScreen);
		}


		public void setMediaPlayer(MediaPlayer mMediaPlayer) {
			this.mMediaPlayer = mMediaPlayer;
		}

		private MediaPlayer mMediaPlayer;
		private VideoSurfaceView videoSurfaceView;

		public VideoRender(MediaPlayer mMediaPlayer, VideoSurfaceView videoSurfaceView) {
			this.mMediaPlayer = mMediaPlayer;
			this.videoSurfaceView = videoSurfaceView;
			mTextureRender = new TextureRender(mMediaPlayer, videoSurfaceView);
		}

		public void onDrawFrame(GL10 glUnused) {
			synchronized (this) {
				if (updateSurface) {
					mSurfaceTexture.updateTexImage();
					updateSurface = false;
				}
			}
			mTextureRender.drawFrame(mSurfaceTexture);
		}

		public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		}

		public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
			mTextureRender.surfaceCreated();
			/*
			 * Create the SurfaceTexture that will feed this textureID, and pass
			 * it to the MediaPlayer
			 */
			mSurfaceTexture = new SurfaceTexture(mTextureRender.getTextureId());
			mSurfaceTexture.setOnFrameAvailableListener(this);
			Surface surface = new Surface(mSurfaceTexture);
			mMediaPlayer.setSurface(surface);
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.start();
			surface.release();
			
			synchronized (this) {
				updateSurface = false;
			}
		}

		synchronized public void onFrameAvailable(SurfaceTexture surface) {
			updateSurface = true;
		}
	} // End of class VideoRender.
} // End of class VideoSurfaceView.