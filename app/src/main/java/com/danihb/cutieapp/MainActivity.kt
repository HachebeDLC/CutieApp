package com.danihb.cutieapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.constraint.ConstraintLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ShareCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageButton
import android.widget.Toast
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import com.otaliastudios.cameraview.CameraView
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {
    private lateinit var bmp: Bitmap
    private lateinit var camera: CameraView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        camera = findViewById(R.id.camera)
        camera.setLifecycleOwner(this)
        val botonCambioCamara = findViewById<ImageButton>(R.id.botonCambioCamara)
        val botonFoto = findViewById<ImageButton>(R.id.botonFoto)


        camera.addCameraListener(object : CameraListener() {
            @SuppressLint("WrongThread")
            override fun onPictureTaken(picture: ByteArray) {
                // Create a bitmap or a file...
                // CameraUtils will read EXIF orientation for you, in a worker thread.
                bmp = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                screenShot()

            }
        })

        botonFoto.setOnClickListener { camera.captureSnapshot() }
        botonCambioCamara.setOnClickListener { camera.toggleFacing() }
    }

    private fun screenShot() {


        val bitmap = BitmapFactory.decodeResource(
            this.resources,
            R.drawable.cutiepie
        )
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val mutableBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width-100, bmp.height-100)


        canvas.drawBitmap(mutableBmp, 520F + mutableBmp.width/2, 450F + mutableBmp.height/2, null)

        bmp = mutableBitmap
        compartirImagen(this)
    }

    private fun compartirImagen(context: Context) {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {

            val intent = ShareCompat.IntentBuilder.from(this)
                .setType("image/png")
                .setStream(getImageUri(context, bmp))
                .setChooserTitle("There goes a cutie pie")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivityForResult(intent, 1)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"),
                1
            )

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (!((grantResults.isNotEmpty()
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))
                ) {
                    Toast.makeText(this, getString(R.string.permissionsNotGranted), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, getString(R.string.permissionsGranted), Toast.LENGTH_LONG).show()

                }
            }
        }
    }


    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}
