package com.google.firebase.encore.koreanfood

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SubActivity : AppCompatActivity() {

    // AddViewFragment에서 사용
    var mUsername: String? = null
    var bitmap: Bitmap? = null

    // Cloud Firestore
    var firestore_sub: FirebaseFirestore? = null

    // firebase storage
    var firebaseStorage: FirebaseStorage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)

        // firebase storage
        firebaseStorage = FirebaseStorage.getInstance()

        // 네비게이션 하단 바 액션 등록
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when(item.itemId) {
                // 음식 관련 정보 나오는 창
                R.id.navigation_info -> {
                    val infoFragment = InfoFragment.newInstance()
                    openFragment(infoFragment)
                    return@OnNavigationItemSelectedListener true
                }

                // 후기 추가하는 창
                R.id.navigation_addReview -> {
                    val addReviewFragment = AddReviewFragment.newInstance()
                    openFragment(addReviewFragment)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        } // end of mOnNavigationItemSelectedListener

        // 처음 화면은 AddReviewFragment로!
        openFragment(AddReviewFragment.newInstance())

        // 하단 네비게이션 바에 액션 등록
        val bottomNavigation: BottomNavigationView = findViewById(R.id.subnavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //// PhotoFragment Intent 로부터 bytesArray, 사용자 이름 받아오기

        // 1. bytesArray를 받아와 bitmap 형태로 변환
        val bytes = intent.getByteArrayExtra("bitmapBytes")
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // 2. 사용자 이름 받아오기
        mUsername = intent.getStringExtra("mUsername").toString()
        firestore_sub = FirebaseFirestore.getInstance()

        } // end of onCreate

    // cloud database에 데이터 추가하는 메서드
    fun createData(text: String?, name: String?, photoUrl: String?) {

        var user_review = UserReview(text, name, photoUrl)
        firestore_sub?.collection("user_review")?.document()?.set(user_review)?.addOnCompleteListener {
                task ->
            if(task.isSuccessful) {
                Toast.makeText(this, "저장 성공", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.sub_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

}

