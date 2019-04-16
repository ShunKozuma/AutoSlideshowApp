package kozuma.shun.techacademy.autoslideshowapp

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100

    lateinit var cursor: Cursor

    var cnt: Int = 0

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0

    private var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画像の情報を取得する
        var resolver = contentResolver

        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

       // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        gobtn.setOnClickListener {

            if(cursor.isLast()){
                cursor.moveToFirst()
            }else{
                cursor.moveToNext()
            }

            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            Log.d("ANDROID", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)

            //cursor.close()
        }



        backbtn.setOnClickListener {

            if(cursor.isFirst()){
                cursor.moveToLast()
            }else{
                cursor.moveToPrevious()
            }

            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            Log.d("ANDROID", "URI : " + imageUri.toString())
            imageView.setImageURI(imageUri)

            //cursor.close()

        }



        movebtn.setOnClickListener {
            if(cnt%2 == 0){
                movebtn.text = "停止"

                gobtn.isEnabled = false
                backbtn.isEnabled = false

                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mTimerSec += 0.1*10
                            mHandler.post {
                                //timer.text = String.format("%.1f", mTimerSec)



                                if(mTimerSec%2 == 0.0){
                                    if(cursor.isLast()){
                                        cursor.moveToFirst()
                                    }else{
                                        cursor.moveToNext()
                                    }

                                    val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                                    val id = cursor.getLong(fieldIndex)
                                    val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                                    Log.d("ANDROID", "URI : " + imageUri.toString())
                                    imageView.setImageURI(imageUri)
                                }
                            }
                        }

                    }, 2000, 1000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定

                }


            }else{
                movebtn.text = "再生"

                gobtn.isEnabled = true
                backbtn.isEnabled = true
                if (mTimer != null){
                    mTimer!!.cancel()
                    mTimer = null
                }
            }
            cnt++

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                    Log.d("ANDROID", "許可された")
                }else{
                    Log.d("ANDROID", "許可されなかった")
                }
        }
    }

    private fun getContentsInfo() {

        cursor.moveToFirst()
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        //cursor.close()
    }

}