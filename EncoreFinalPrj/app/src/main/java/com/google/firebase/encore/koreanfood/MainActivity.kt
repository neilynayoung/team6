package com.google.firebase.encore.koreanfood

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ml.custom.*
import com.google.firebase.ml.custom.model.FirebaseCloudModelSource
import com.google.firebase.ml.custom.model.FirebaseLocalModelSource
import com.google.firebase.ml.custom.model.FirebaseModelDownloadConditions
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

// MobileNetV2 tflite 모델 설명
/*
Input shape = [1, 224, 224, 3]
Output shape = [1, 150]
 */

class MainActivity : AppCompatActivity() {

    // main 하단 네비게이션 바 선택 액션 관련 설정

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            // 메인창
            R.id.navigation_main -> {

                return@OnNavigationItemSelectedListener true
            }
            // 사용자 후기 공유 창
            R.id.navigation_share -> {
                val shareFragment = ShareFragment.newInstance()
                openFragment(shareFragment)

                return@OnNavigationItemSelectedListener true
            }
            // 마이페이지 창
            R.id.navigation_mypage -> {
                val mypageFragment = MypageFragment.newInstance()
                openFragment(mypageFragment)

                return@OnNavigationItemSelectedListener  true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    // 사진 기능 관련 변수
    private var btn: Button? = null
    private var imageview: ImageView? = null
    private var resultText: TextView? = null

    private val GALLERY = 2
    private val CAMERA = 3

    // FIrebase 관련 변수

    // 1. 로그인 관련
    private var mUsername: String? = null
    private var mFirebaseAuth: FirebaseAuth? = null
    private var mAuthStateListener: FirebaseAuth.AuthStateListener? = null

    val RC_SIGN_IN = 1


    // 2. 머신러닝 모델 관련
    private lateinit var conditionsBuilder: FirebaseModelDownloadConditions.Builder
    private lateinit var inputOutputOptions: FirebaseModelInputOutputOptions

    private var interpreter: FirebaseModelInterpreter? = null
    // private var inputs: FirebaseModelInputs? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 하단 네비게이션 바 초기화
        var bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        // 사진 관련
        btn = findViewById<View>(R.id.btn) as Button
        imageview = findViewById<View>(R.id.iv) as ImageView
        resultText = findViewById<View>(R.id.resultText) as TextView
        btn!!.setOnClickListener { showPictureDialog() }

        // ML kit 관련 초기화
        initKoreanFoodClassifier()

        // 로그인 관련
        mUsername = ANONYMOUS
        mFirebaseAuth = FirebaseAuth.getInstance()

        mAuthStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null){
                onSignedInInitialize(user.displayName)
            } else {
                onSignedOutCleanUp()
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList<AuthUI.IdpConfig>(
                            AuthUI.IdpConfig.GoogleBuilder().build(),
                            AuthUI.IdpConfig.EmailBuilder().build()
                        ))
                        .setLogo(R.drawable.ic_photo_camera_gray_24dp)
                        .build(), RC_SIGN_IN)
            }
        } // end of mAuthStateListener

    } // end of OnCreate

    override fun onResume() {
        super.onResume()
        mFirebaseAuth!!.addAuthStateListener (mAuthStateListener!!)
    }

    override fun onPause() {
        super.onPause()
        if (mAuthStateListener != null) {
            mFirebaseAuth!!.removeAuthStateListener(mAuthStateListener!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems){
                dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.sign_out_menu -> {
                // sign out
                AuthUI.getInstance().signOut(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            if (resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Signed in cancelled!", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
        else if(requestCode == GALLERY) {

            Log.d("갤러리", GALLERY.toString())
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    val path = saveImage(bitmap)
                    // 음식 종류 추론
                    foodInference(bitmap)

                    Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageview!!.setImageBitmap(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (requestCode == CAMERA) {
            Log.d("가나다라", CAMERA.toString())

            val thumbnail = data!!.extras!!.get("data") as Bitmap
            imageview!!.setImageBitmap(thumbnail)
            // 음식 종류 추론
            foodInference(thumbnail)

            saveImage(thumbnail)
            Toast.makeText(this@MainActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }

        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            Log.d("파일이름은", f.toString())
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    private fun initKoreanFoodClassifier(){
        // 1) FIrebase 호스팅 모델 소스 구성
        conditionsBuilder = FirebaseModelDownloadConditions.Builder().requireWifi()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            // Enable advanced conditions on Android Nougat and newer.
            conditionsBuilder = conditionsBuilder.requireCharging().requireDeviceIdle()
        }
        val conditions = conditionsBuilder.build()

        // Build a FirebaseCloudModelSource object by specifying the name you assigned the model
        // when you uploaded it in the Firebase console.

        val cloudSource = FirebaseCloudModelSource.Builder("korean_food_classifier")
            .enableModelUpdates(true)
            .setInitialDownloadConditions(conditions)
            .build()

        Log.d("클라우드", cloudSource.toString())

        FirebaseModelManager.getInstance().registerCloudModelSource(cloudSource)

        // 2) 로컬 모델 소스 구성
        val localSource = FirebaseLocalModelSource.Builder("local_classifier")
            .setAssetFilePath("190209_MobileNetV2.tflite").build()

        Log.d("로컬", localSource.toString())

        FirebaseModelManager.getInstance().registerLocalModelSource(localSource)

        // 3) 모델 소스에서 인터프리터 만들기
        val options = FirebaseModelOptions.Builder()
//            .setCloudModelName("korean_food_classifier")
            .setLocalModelName("local_classifier").build()

        interpreter = FirebaseModelInterpreter.getInstance(options)

        // 4) 모델의 입 출력 지정
        // ---> input, output 지정
        inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 150)).build()

        Log.d("rrrrr", interpreter.toString())

    }

    private fun foodInference(img: Bitmap){

        // 1) 입력 데이터에 대한 추론 수행
        val bitmap = Bitmap.createScaledBitmap(img, 224, 224, true)
        val batchNum = 0
        val input = Array(1) {Array(224) { Array(224) {FloatArray(3) }}}

        for (x in 0..223) {
            for (y in 0..223) {
                val pixel = bitmap.getPixel(x, y)
                // Normalize channel values to [0.0, 1.0]
                input[batchNum][x][y][0] = Color.red(pixel) / 255.0f
                input[batchNum][x][y][1] = Color.green(pixel) / 255.0f
                input[batchNum][x][y][2] = Color.blue(pixel) / 255.0f
            }
        }

        // 2) 입력 데이터로 FirebaseModelInputs 객체를 만들고, 객체의 입출력 사양을
        // 모델 인터프리터의 run 메서드에 전달
        val inputs = FirebaseModelInputs.Builder()
            .add(input).build()

        interpreter!!.run(inputs, inputOutputOptions)?.addOnSuccessListener{ result ->

            val foodMap = FoodMap().foodMap
            val output = result.getOutput<Array<FloatArray>>(0)
            val probabilities = output[0]
            // 최댓값의 index, 확률
            val argmax = probabilities.indexOf(probabilities.max()!!).toString()
            val max_prob = probabilities.max().toString()

            Log.d("결과 ====", output.size.toString())
            resultText!!.text = "음식 이름 : ${foodMap[argmax]}, 확률 : ${max_prob}"

            }?.addOnFailureListener(
            object : OnFailureListener {
                override fun onFailure(e: Exception) {
                    // Task failed with an exception
                    // ...
                    Toast.makeText(this@MainActivity, "Classification Failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun onSignedInInitialize(username: String?){
        mUsername = username
    }

    private fun onSignedOutCleanUp() {
        mUsername = ANONYMOUS
    }



    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        val ANONYMOUS = "anonymous"

    }




}
