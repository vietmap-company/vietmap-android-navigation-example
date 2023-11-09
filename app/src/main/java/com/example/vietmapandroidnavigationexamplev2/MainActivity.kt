package com.example.vietmapandroidnavigationexamplev2

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.vietmap.vietmapsdk.Vietmap
import vn.vietmap.vietmapsdk.location.permissions.PermissionsListener
import vn.vietmap.vietmapsdk.location.permissions.PermissionsManager
import java.util.*


class MainActivity : AppCompatActivity(), PermissionsListener {

    private var permissionsManager: PermissionsManager? = null

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Vietmap.getInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.pushToNavigationScreen)
        val ttsButton: Button = findViewById(R.id.testSpeech)
        val speechAgain: Button = findViewById(R.id.speechAgain)
        val intent = Intent(this, VietMapNavigationExampleV2::class.java)
        button.setOnClickListener {
            startActivity(intent)
            speechAgain.visibility = GONE
        }
        speechAgain.setOnClickListener { speakOut("Ngôn ngữ: Tiếng Việt") }
        ttsButton.setOnClickListener {
            startActivity(Intent("com.android.settings.TTS_SETTINGS"))
            speechAgain.visibility = VISIBLE
        }
        permissionsManager = PermissionsManager(this)
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager!!.requestLocationPermissions(this)
        }

        textToSpeech = TextToSpeech(
            applicationContext
        ) { setTextToSpeechLanguage() }
    }


    private fun setTextToSpeechLanguage() {
        val language = Locale("vi", "VN")
        when (textToSpeech!!.setLanguage(language)) {
            TextToSpeech.LANG_MISSING_DATA -> {
                Toast.makeText(this, "Không có dữ liệu ngôn ngữ", Toast.LENGTH_LONG).show()
                return
            }
            TextToSpeech.LANG_NOT_SUPPORTED -> {
                Toast.makeText(
                    this,
                    "Chưa hỗ trợ ngôn ngữ " + language.language,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            else -> {
                Toast.makeText(this, "Ngôn ngữ: Tiếng Việt", Toast.LENGTH_LONG).show()
                speakOut("Ngôn ngữ: Tiếng Việt")
            }
        }
    }

    private fun speakOut(speechContent: String) {
        val utteranceId: String = UUID.randomUUID().toString()
        textToSpeech!!.speak(speechContent, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
        Toast.makeText(
            this, "This app needs location permissions in order to show its functionality.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
        } else {
            Toast.makeText(
                this, "You didn't grant location permissions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}