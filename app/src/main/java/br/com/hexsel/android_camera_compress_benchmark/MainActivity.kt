package br.com.hexsel.android_camera_compress_benchmark

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.MediaStore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.widget.SeekBar
import java.io.ByteArrayOutputStream
import android.media.ExifInterface
import android.text.format.Formatter
import android.os.AsyncTask.execute
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.net.Uri


class MainActivity : AppCompatActivity() {

    val REQUEST_RAW_IMAGE_CAPTURE = 1
    val REQUEST_BITMAP_COMPRESS_IMAGE_CAPTURE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDefaultValues()

        verifyStoragePermissions(this)

        btnRaw.setOnClickListener {
            takeRawPicture()
        }

        btnBitmapCompress.setOnClickListener {
            takeBitMapCompressPicture()
        }

        compressionRatio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                txtCompressionRatio.text = i.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    fun setDefaultValues(){
        title = "Android Camera Compress Tests"
        compressionRatio.progress = 80
    }

    fun takeRawPicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val filename = Environment.getExternalStorageDirectory().getPath() + "/raw.jpg";
            val imageUri = Uri.fromFile(File(filename));
            takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                    imageUri);
            startActivityForResult(takePictureIntent, REQUEST_RAW_IMAGE_CAPTURE)
        }
    }

    fun takeBitMapCompressPicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_BITMAP_COMPRESS_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_RAW_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            //val extras = data.extras
            //val imageBitmap = extras!!.get("data") as Bitmap
            //val rawJPEGData = compressJPEG(imageBitmap, 100)
            //saveCompressedJPEG(imageBitmap, 100, "raw.jpg")
            //txtImageSize.text = "Image Size: " + Formatter.formatShortFileSize(baseContext, rawJPEGData.size().toLong())
        } else if (requestCode == REQUEST_BITMAP_COMPRESS_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!.get("data") as Bitmap
            val compressedJPEGData = compressJPEG(imageBitmap, compressionRatio.progress)
            saveCompressedJPEG(imageBitmap, compressionRatio.progress, "compressed.jpg")
            txtImageSize.text = "Image Size: " + Formatter.formatShortFileSize(baseContext, compressedJPEGData.size().toLong())
        }
    }

    fun saveCompressedJPEG(bitMap: Bitmap, compression: Int, name: String){
        val path = Environment.getExternalStorageDirectory().toString()
        var fOut: OutputStream? = null
        val counter = 0
        val file = File(path, name) // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = FileOutputStream(file)

        bitMap.compress(Bitmap.CompressFormat.JPEG, compression, fOut) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        fOut!!.flush() // Not really required
        fOut!!.close() // do not forget to close the stream

        MediaStore.Images.Media.insertImage(contentResolver, file.getAbsolutePath(), file.getName(), file.getName())
    }

    fun compressJPEG(bitMap: Bitmap, compression: Int): ByteArrayOutputStream {
        val out = ByteArrayOutputStream()
        bitMap.compress(Bitmap.CompressFormat.JPEG, compression, out)
        return out
    }


    fun formatBytes(bytes: Long): String{
        val unit = 1024.0;
        if (bytes < unit)
            return bytes.toString() + " B";
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit));
        val pre = ("KMGTPE").toCharArray()[(exp-1).toInt()];
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    //persmission method.
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have read or write permission
        val writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}
