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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    fun bitmapCompressToIntent() {
        // 1. bitmap 원본 이미지 파일을 압축 후 byteArray 형태로 변환
        // 2. byteArray를 Intent 통해서 SubActivity로 전달
        val bitmap: Bitmap? = (activity as MainActivity).bitmap
        var stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        var bytes = stream.toByteArray()

        // 저장된 이미지의 절대 경로
        var photoPath: String? = (activity as MainActivity).photoPath

        startActivity<SubActivity>(
            "bitmapBytes" to bytes,
            "mUsername" to mUsername,
            "photoPath" to photoPath
        )
    }

    companion object {
        fun newInstance(): PhotoFragment = PhotoFragment()
    }

}