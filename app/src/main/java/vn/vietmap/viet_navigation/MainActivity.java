package vn.vietmap.viet_navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import vn.vietmap.viet_navigation.R;
import vn.vietmap.vietmapsdk.location.permissions.PermissionsListener;
import vn.vietmap.vietmapsdk.location.permissions.PermissionsManager;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PermissionsListener {

    private PermissionsManager permissionsManager;

    private TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button =  findViewById(R.id.pushToNavigationScreen);
        Button ttsButton = findViewById(R.id.testSpeech);
        Button speechAgain = findViewById(R.id.speechAgain);
        Intent it = new Intent(this, VietMapNavigationActivity.class);
        button.setOnClickListener(view -> {
            startActivity(it);
            speechAgain.setVisibility(View.GONE);
        });
        speechAgain.setOnClickListener(view -> speakOut("Ngôn ngữ: Tiếng Việt"));
        ttsButton.setOnClickListener(view-> {
            startActivity(new Intent("com.android.settings.TTS_SETTINGS"));
            speechAgain.setVisibility(View.VISIBLE);
        });
        permissionsManager = new PermissionsManager(this);
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), status -> setTextToSpeechLanguage());
    }

    private void setTextToSpeechLanguage() {
        Locale language =new Locale("vi","VN");
        int result = textToSpeech.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Toast.makeText(this, "Không có dữ liệu ngôn ngữ", Toast.LENGTH_LONG).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(this, "Chưa hỗ trợ ngôn ngữ "+language.getLanguage() , Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "Ngôn ngữ: Tiếng Việt", Toast.LENGTH_LONG).show();
            speakOut("Ngôn ngữ: Tiếng Việt");
        }
    }
    private void speakOut(String speechContent) {
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(speechContent, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "This app needs location permissions in order to show its functionality.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
        } else {
            Toast.makeText(this, "You didn't grant location permissions.",
                    Toast.LENGTH_LONG).show();
        }
    }
}