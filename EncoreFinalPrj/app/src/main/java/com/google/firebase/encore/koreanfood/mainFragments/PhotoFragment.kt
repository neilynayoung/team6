package com.google.firebase.encore.koreanfood

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import java.io.ByteArrayOutputStream

const val IMAGE_URI_KEY = "IMAGE_URI"
const val BITMAP_WIDTH = "BITMAP_WIDTH"
const val BITMAP_HEIGHT = "BITMAP_HEIGHT"

class PhotoFragment: Fragment(), View.OnClickListener {

    // 전역 변수 -> MainActivity에서 사용하기 위해 생성
    var imageview: ImageView? = null
    var resultText: TextView? = null
    var resultProbText: TextView? = null
    var goSubBtn: Button? = null

    // 회원 이름, 음식 이름, 확률, 이미지를 byte array로 전환한 형태, bitmap 이미지
    var mUsername: String? = null
    var foodName: String? = null
    var prob: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_photo, container, false)

        val btn: Button = view.find(R.id.btn)
        val goSubBtn: Button = view.find(R.id.goSubBtn)

        // PhotoFragment 내 UI 요소 초기화
        val imageview: ImageView = view.find(R.id.iv)
        val resultText: TextView = view.find(R.id.resultText)
        val resultProbText: TextView = view.find(R.id.resultProbText)

        // 전역 변수에 값 넣어줌
        this.imageview = imageview
        this.resultText = resultText
        this.resultProbText = resultProbText
        this.goSubBtn = goSubBtn

        // MainActivity에서 PhotoFragment로 값 전달
        mUsername = (activity as MainActivity).mUsername
        Log.d("photo 사용자이름 =====", mUsername)
        // 카메라 버튼 선택 시
        btn.setOnClickListener{(activity as MainActivity).showPictureDialog()}

        goSubBtn.setOnClickListener { bitmapCompressToIntent() }

        return view
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun onStart() {
//        super.onStart()
//    }

//    override fun onPause() {
//        super.onPause()
//        foodName = resultText?.text.toString()
//        prob = resultProbText?.text.toString()
//
//        Log.d("photoFrag 온퍼우스", foodName)
//        Log.d("prob 온퍼우스", prob)
//    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        outState.putString("foodName", foodName)
//        outState.putString("prob", prob)
////        outState.putByteArray("bitmap_bytes", bytes)
////
//        Log.d("photoFragment 음식저장", foodName.toString())
//        Log.d("photoFragment 확률저장", prob.toString())
////        Log.d("photoFragment 바이트저장", bytes.toString())
//
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        this.foodName = savedInstanceState?.getString("foodName")
//        this.prob = savedInstanceState?.getString("prob")
//        this.bytes = savedInstanceState?.getByteArray("bitmap_bytes")
//
//        Log.d("photoFragment 음식", foodName.toString())
//        Log.d("photoFragment 확률", prob.toString())
//        Log.d("photoFragment 그림바이트", bytes.toString())
//
//        if (bytes != null){
//            this.bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes!!.size)
//            imageview?.setImageBitmap(bitmap)
//        }
//        if (foodName != null){
//            resultText?.text = foodName
//        }
//        if (prob != null){
//            resultProbText?.text = prob
//        }

    }

    fun bitmapCompressToIntent() {
        // 1. bitmap 원본 이미지 파일을 압축 후 byteArray 형태로 변환
        // 2. byteArray를 Intent 통해서 SubActivity로 전달
        val bitmap: Bitmap? = (activity as MainActivity).bitmap

        var stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var bytes = stream.toByteArray()

        startActivity<SubActivity>(
            "bitmapBytes" to bytes,
            "mUsername" to mUsername
        )
    }

//    fun saveData(foodName: String, prob: String, bitmap: Bitmap){
//        val pref = PreferenceManager.getDefaultSharedPreferences()
//
//
//    }


    companion object {
        fun newInstance(): PhotoFragment = PhotoFragment()
    }

}