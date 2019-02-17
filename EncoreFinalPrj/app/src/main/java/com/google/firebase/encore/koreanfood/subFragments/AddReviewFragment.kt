package com.google.firebase.encore.koreanfood

import android.graphics.Bitmap
import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.encore.koreanfood.DataModel.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_addreview.*
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddReviewFragment: Fragment(), View.OnClickListener {

    private var auth: FirebaseAuth? = null

    // Cloud Firestore
    var firestore: FirebaseFirestore? = null

    // firebase storage
     var firebaseStorage: FirebaseStorage? = null
     // var photoStorageRef: StorageReference? = null

    // 사진 비트맵
    var bitmap: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_addreview, container, false)

        // UI 구성요소 추가
        val reviewAddBtn: Button = view.find(R.id.reviewAddBtn)
        val textInput: TextInputEditText = view.find(id = R.id.textInput)
        val addReviewImageView: ImageView = view.find(R.id.addReviewImageView)

        // firebase
        auth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // PhotoFragment -> SubActivity -> AddReviewFragment 경로 통해 찍은 사진을 가져옴
        bitmap = (activity as SubActivity).bitmap


        // 그림이 없을 때는 아무 일도 안 일어나도록!
        if ( bitmap != null){
            addReviewImageView.setImageBitmap(bitmap)
        }

        // 우선 리뷰 추가 버튼 비활성화
        reviewAddBtn.isEnabled = false

        // 찍은 사진이 존재하고, 후기를 입력했을 때만 버튼이 활성화되게 하기
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
            contentUpload()
        }
            return view
        } // end of OnCreateView


    fun contentUpload() {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"
        // 음식 사진의 경로
        val photoPath = (activity as SubActivity).photoPath
        var photoFile = Uri.fromFile(File(photoPath))

        // firebaseStorage 사진 저장 장소 참조
        val photoStorageRef = firebaseStorage?.reference?.child("photos")?.child(imageFileName)

        Log.d("사진파일참조 ", photoStorageRef.toString())

        photoStorageRef?.putFile(photoFile)?.addOnCompleteListener { task ->
            toast("Upload Success")
            if (task.isSuccessful){
                val contentDTO = ContentDTO()
                // 사진 주소
                contentDTO.photoUrl = photoStorageRef.toString()
                // 유저의 UID
                contentDTO.uid = auth?.currentUser?.uid
                // 사진의 리뷰
                contentDTO.review = textInput.text.toString()
                // 유저의 아이디
                contentDTO.userId = auth?.currentUser?.email
                // 게시물 업로드 시간
                contentDTO.timestamp = System.currentTimeMillis()

                // 게시물에 데이터 생성
                firestore?.collection("photos")?.document()?.set(contentDTO)
                // ShareFragment 로 이동..?
                startActivity<MainActivity>()

            }



        }?.addOnFailureListener {
            toast("Upload failure")
        }
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        fun newInstance(): AddReviewFragment = AddReviewFragment()
    }

}