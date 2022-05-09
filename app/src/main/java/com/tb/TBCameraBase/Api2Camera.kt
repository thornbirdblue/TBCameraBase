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
    private val TAG = "Api2Camera"

    private var mCameraManager: CameraManager
    
    private lateinit var mPreviewSurface: Surface 
    private lateinit var mCameraDevice: CameraDevice    

    private lateinit var mCurrentCaptureSession: CameraCaptureSession


    private val mCameraStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            mCameraDevice = camera
            Log.d(TAG, "STARTUP_REQUIREMENT Done opening camera.")

	    Preview()
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
        override fun onConfigured(session: CameraCaptureSession) {
            mCurrentCaptureSession = session
            Log.d(TAG, "capture session onConfigured().")
            PreviewCaptureRequest()
        }

        override fun onReady(session: CameraCaptureSession) {
            Log.d(TAG, "capture session onReady().")
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(TAG, "onConfigureFailed")
        }
    }

    private val mCaptureCallback: CaptureCallback = object : CaptureCallback() {
        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                Log.d(TAG, "App control to first frame: ")
        }
    }

    fun openCamera() {
        Log.d(TAG, "STARTUP_REQUIREMENT opening camera ")
	
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
    }

    fun stopPreview(){

    }

    private fun Preview(){
        Log.d(TAG, "Prviewing ... ")
	val outputSurfaces:MutableList<Surface?> = ArrayList(1)

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
