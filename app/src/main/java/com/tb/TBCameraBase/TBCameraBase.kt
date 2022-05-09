package com.tb.TBCameraBase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.SurfaceView
import android.view.SurfaceHolder
import android.util.Log
import android.content.Context
import android.hardware.camera2.CameraManager

class TBCameraBase : AppCompatActivity(),SurfaceHolder.Callback {
	private val TAG = "TBCameraBase"
	
	private lateinit var mSurface: SurfaceView
	private lateinit var mApi2Cam: Api2Camera

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		val cm = getSystemService(Context.CAMERA_SERVICE) as CameraManager	
		mApi2Cam = Api2Camera(cm)

		mApi2Cam.openCamera()
		
		setContentView(R.layout.activity_main)
		
		mSurface = findViewById<View>(R.id.preview_view) as SurfaceView
		val mPreviewHolder = mSurface.getHolder()
		mPreviewHolder.addCallback(this)		
		
		Log.d(TAG,"onCreate Finished!")
	}

	override fun onStop() {
		mApi2Cam.closeCamera()
		Log.d(TAG,"onStop Finished!")
	}

	override fun surfaceCreated(holder: SurfaceHolder){
		mApi2Cam.startPreview(mSurface.getHolder().getSurface())
		
		Log.d(TAG,"surfaceCreated")
	}
	override fun surfaceDestroyed(holder: SurfaceHolder){
		mApi2Cam.stopPreview()
		
		Log.d(TAG,"surfaceDestroyed")
	}
	override fun surfaceChanged(holder: SurfaceHolder,format: Int,width: Int,height: Int){
		
		Log.d(TAG,"surfaceChanged!")
	}

}
