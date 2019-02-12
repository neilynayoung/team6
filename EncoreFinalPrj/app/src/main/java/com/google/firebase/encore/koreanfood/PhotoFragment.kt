package com.google.firebase.encore.koreanfood

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.jetbrains.anko.find

class PhotoFragment: Fragment(), View.OnClickListener {

    var imageView: ImageView? = null
    var resultText: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_photo, container, false)

        val btn: Button = view.find(R.id.btn)
        val imageView: ImageView = view.find(R.id.iv)
        val resultText: TextView = view.find(R.id.resultText)

        // 전역 변수에 값 넣어줌
        this.imageView = imageView
        this.resultText = resultText

        btn.setOnClickListener{(activity as MainActivity).showPictureDialog()}

        return view
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance(): PhotoFragment = PhotoFragment()
    }

}