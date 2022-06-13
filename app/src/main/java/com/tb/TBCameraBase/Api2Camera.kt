package com.tb.TBCameraBase

import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.ImageWriter
import android.os.ConditionVariable
import android.view.Surface

import android.util.Log

import java.io.IOException
import java.util.*

/**
 * Created by thornbird on 2022/05/07.
 */
class Api2Camera(mCM: CameraManager){
    private val TAG = "TBCameraBase"
    
    private var isPreview = false
    private var isOpen = false

    private var mCameraManager: CameraManager
    
    private lateinit var mPreviewSurface: Surface 
    private lateinit var mCameraDevice: CameraDevice    

    private lateinit var mCurrentCaptureSession: CameraCaptureSession


    private val mCameraStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
	    isOpen = true

            Log.d(TAG, "STARTUP  Done opening camera.")
	    tryToPreview()
        }

        override fun onClosed(camera: CameraDevice) {
            Log.d(TAG, "onClosed: Done Closing camera ")
        }

        override fun onDisconnected(camera: CameraDevice) {
            Log.d(TAG, "onDisconnected")
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e(TAG, "CameraDevice onError error val is $error. ")
        }
    }

    private val mSessionStateCallback: CameraCaptureSession.StateCallback = object : CameraCaptureSession.StateCallback() {
	override fun onConfigured(session: CameraCaptureSession){
            Log.d(TAG, "capture session onConfigured().")
	}

	override fun onConfigureFailed(session: CameraCaptureSession){
            Log.d(TAG, "ERROR: capture session onConfigureFailed().")
	}

        override fun onReady(session: CameraCaptureSession) {
	    super.onReady(session)
        
	    Log.d(TAG, "capture session onReady().")
            mCurrentCaptureSession = session
            
	    PreviewCaptureRequest()
        }
    }

    private val mCaptureCallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                Log.d(TAG, "App control to frame: ")
        }
    }

    fun openCamera() {
        Log.d(TAG, "STARTUP opening camera ")
	
	mCameraManager.openCamera("0", mCameraStateCallback, null)
    }

    fun closeCamera() {
        Log.d(TAG, "Closing camera ")
        
        mCurrentCaptureSession!!.abortCaptures()
        mCameraDevice!!.close()
    }


    fun startPreview(mSurface: Surface) {
        Log.d(TAG, "Start Prview ... ")

	mPreviewSurface = mSurface
	isPreview = true
	tryToPreview()
    }

    fun stopPreview(){

    }

    private fun tryToPreview(){
	if(isPreview&&isOpen)
		Preview()
    }

    private fun Preview(){
        Log.d(TAG, "Prviewing ... ")
	val outputSurfaces:MutableList<Surface?> = ArrayList(4)

        outputSurfaces.add(mPreviewSurface);
        
	// It used to be: this needed to be posted on a Handler.
	mCameraDevice!!.createCaptureSession(outputSurfaces, mSessionStateCallback, null);

    }

    private fun PreviewCaptureRequest() {
        Log.d(TAG, "PreviewCaptureRequest...")
        try {
            val b1 = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            b1.addTarget(mPreviewSurface)

            mCurrentCaptureSession!!.setRepeatingRequest(b1.build(), mCaptureCallback, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Could not access camera for issuePreviewCaptureRequest.")
        }
    }


    init {
        mCameraManager = mCM
    }
}
