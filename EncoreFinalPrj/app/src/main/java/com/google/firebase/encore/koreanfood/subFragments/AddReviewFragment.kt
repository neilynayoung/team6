package com.google.firebase.encore.koreanfood

import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import org.jetbrains.anko.find

class AddReviewFragment: Fragment(), View.OnClickListener {

    // 전역 변수 -> MainActivity에서 사용하기 위해 생성!
//    var reviewAddBtn: Button? = null
//    var textInput: TextInputEditText? = null
//    var addReviewImageView: ImageView? = null

    // 사용자가 입력한 후기
    var review: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_addreview, container, false)

        // UI 구성요소 추가
        val reviewAddBtn: Button = view.find(R.id.reviewAddBtn)
        val textInput: TextInputEditText = view.find(id = R.id.textInput)
        val addReviewImageView: ImageView = view.find(R.id.addReviewImageView)

        // 전역 변수에 값 넣어줌 (SubActivity에서 newInstance() 통해 객체 생성 후 UI에 접근 가능하게)
//        this.reviewAddBtn = reviewAddBtn
//        this.textInput = textInput
//        this.addReviewImageView = addReviewImageView

        Log.d("사용자 이름(addReview)", (activity as SubActivity).mUsername)

        // PhotoFragment -> SubActivity -> AddReviewFragment 경로 통해 찍은 사진을 가져옴
        val bitmap = (activity as SubActivity).bitmap

        // 그림이 없을 때는 아무 일도 안 일어나도록!
        if ( bitmap != null){
            addReviewImageView.setImageBitmap(bitmap)
        }

        // 우선 리뷰 추가 버튼 비활성화
        reviewAddBtn.isEnabled = false

        // 찍은 사진이 존재하고, 후기를 입력했을 때만 DB에 추가되게 하기
        textInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().trim { it <= ' ' }.length > 0 && bitmap != null) {
                    Log.d("addReview", "조건만족")
                    reviewAddBtn.isEnabled = true
                } else {
                    Log.d("addReview", "조건불만족")
                    reviewAddBtn.isEnabled = false
                }
            }
        })
        // cloud FireStore에 데이터 추가
        reviewAddBtn.setOnClickListener {
            (activity as SubActivity).createData(textInput.text.toString(), (activity as SubActivity).mUsername, "abcd")
        }

            return view
        } // end of OnCreateView



    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance(): AddReviewFragment = AddReviewFragment()
    }

}