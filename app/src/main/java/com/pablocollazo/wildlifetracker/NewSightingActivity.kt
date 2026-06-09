package com.pablocollazo.wildlifetracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NewSightingActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private var photoUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var database: SightingDatabase

    //Launcher to get camera permission
    private val requestPermissionCameraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted){
            takePhoto()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    //Launcher to get location permission
    private val requestPermissionLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted){
            getLocation()
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
        }
    }



    //Launcher which gets camera result
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageView.setImageURI(photoUri)
            checkLocationPermissionAndGetLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_sighting)

        imageView = findViewById(R.id.imageViewPhoto)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val buttonTakePhoto = findViewById<Button>(R.id.buttonTakePhoto)
        buttonTakePhoto.setOnClickListener { checkCameraPermissionAndTakePhoto()}

        database = Room.databaseBuilder(this, SightingDatabase::class.java, "sighting_db").build()

        val buttonSave = findViewById<Button>(R.id.buttonSave)
        buttonSave.setOnClickListener { saveSighting()}
    }

    private fun checkCameraPermissionAndTakePhoto() {
        when {
            //If has permission, open camera
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> takePhoto()
            //If not, ask permission
            else -> requestPermissionCameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun checkLocationPermissionAndGetLocation() {
        when {
            //If has permission, get location
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> getLocation()
            //If not, ask permission
            else -> requestPermissionLocationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun takePhoto() {
        //Create file to save photo
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File.createTempFile("SIGHTING_${timestamp}_", ".jpg", storageDir)

        //Turn file into URI with FileProvider
        photoUri = FileProvider.getUriForFile(
            this,
            "com.pablocollazo.wildlifetracker.fileprovider",
            photoFile
        )

        //Launch camera
        cameraLauncher.launch(photoUri!!)
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener { location ->
            if (location != null){
                latitude = location.latitude
                longitude = location.longitude
                findViewById<TextView>(R.id.textViewLocation).text = "Location: $latitude, $longitude"
            }

        }
    }

    private fun saveSighting(){
        val species = findViewById<EditText>(R.id.editTextSpecies).text.toString()
        val notes = findViewById<EditText>(R.id.editTextNotes).text.toString()
        val date = System.currentTimeMillis()

        val sighting = Sighting(0,photoUri.toString(),species,notes,latitude,longitude,date,false)

        lifecycleScope.launch(Dispatchers.IO){
            database.sightingDao().insertSighting(sighting)
            runOnUiThread {
                finish()
            }
        }
    }
}