package com.example.myhappyplaces.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myhappyplaces.R
import com.example.myhappyplaces.database.DatabaseHandler
import com.example.myhappyplaces.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var cal= Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_happy_place)

        val toolBarAddActivity = findViewById<Toolbar>(R.id.toolbar_add_activity)

        setSupportActionBar(toolBarAddActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBarAddActivity.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{
            view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        val editTextDate = findViewById<EditText>(R.id.et_date)
        editTextDate.setOnClickListener(this)

        val  addImageBtn= findViewById<TextView>(R.id.tv_add_image)
        addImageBtn.setOnClickListener(this)

        val saveBtn = findViewById<Button>(R.id.btn_save)
        saveBtn.setOnClickListener(this)

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY){

                if(data!=null){
                    val contentURI = data.data
                    try{
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        val placeImage = findViewById<ImageView>(R.id.iv_place_image)

                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)

                        Log.e("save image: ", "Path :: $saveImageToInternalStorage")

                        placeImage.setImageBitmap(selectedImageBitmap)

                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity,
                            "Failed to load the image from gallery",
                            Toast.LENGTH_LONG).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap

                val captureImage = findViewById<ImageView>(R.id.iv_place_image)

                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.e("save image: ", "Path :: $saveImageToInternalStorage")


                captureImage.setImageBitmap(thumbnail)
            }
        }
    }

    private fun takePhotoFromCamera(){
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent,CAMERA)

                }else{
                    Toast.makeText(this@AddHappyPlaceActivity,
                        "Permissions failed",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest?>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()

    }

    private fun choosePhotoFromGallery(){
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_MEDIA_IMAGES,
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?)
            {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent,GALLERY)

                }else{
                    Toast.makeText(this@AddHappyPlaceActivity,
                        "Permissions failed",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest?>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }
    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("it looks like you have turned off permission required for this feature. It can be enabled under the application settings")
            .setPositiveButton("GO TO SETTINGS")
            { _,_ ->
                try{
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onClick(v: View?){

        when(v!!.id){
            R.id.et_date ->{
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")

                val pictureDialogItems = arrayOf("Select photo from gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems)
                { _, which->
                    when(which){
                        0->choosePhotoFromGallery()
                        1-> takePhotoFromCamera()
                        }
                    }
                    pictureDialog.show()
                }
            R.id.btn_save ->{ //TODO make the saved place appear on the list when you press save
                val etTitle = findViewById<EditText>(R.id.et_title)
                val etDescription = findViewById<EditText>(R.id.et_description)
                val etLocation = findViewById<EditText>(R.id.et_location)
                val etDate = findViewById<EditText>(R.id.et_date)
                when{
                    etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this@AddHappyPlaceActivity,
                            "Please enter a title",
                            Toast.LENGTH_LONG).show()
                    }
                    etDescription.text.isNullOrEmpty()->{
                        Toast.makeText(this@AddHappyPlaceActivity,
                            "Please enter a description",
                            Toast.LENGTH_LONG).show()
                    }
                    etLocation.text.isNullOrEmpty()->{
                        Toast.makeText(this@AddHappyPlaceActivity,
                            "Please enter a location",
                            Toast.LENGTH_LONG).show()
                    }
                    saveImageToInternalStorage == null ->{
                        Toast.makeText(this@AddHappyPlaceActivity,
                            "Please select image",
                            Toast.LENGTH_LONG).show()
                    }else->{

                        val happyPlaceModel = HappyPlaceModel(
                            0,
                            etTitle.text.toString(),
                            saveImageToInternalStorage.toString(),
                            etDescription.text.toString(),
                            etDate.text.toString(),
                            etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )
                        val dbHandler = DatabaseHandler(this)

                        val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)

                        if(addHappyPlace > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun updateDateInView(){
        val myFormat = "dd.MMM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        val editTextDate = findViewById<EditText>(R.id.et_date)
        editTextDate.setText(sdf.format(cal.time).toString())
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")//store image as jpg with random id

        try{
            val stream : OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object{
        private const val GALLERY =1
        private const val CAMERA =2
        private const val IMAGE_DIRECTORY = "HappyPlacesImage"
    }
}