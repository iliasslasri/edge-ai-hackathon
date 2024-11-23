package com.example.appappel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.appappel.ui.theme.AppAppelTheme

class MainActivity : ComponentActivity() {

    private var selectedAudioUri: Uri? = null
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var audioManager: AudioManager

    // Permission launcher for CALL_PHONE permission
    private val requestCallPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showToast("Permission to make calls is required.")
            }
        }

    // Permission launcher for READ_PHONE_STATE permission
    private val requestReadPhoneStatePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                registerPhoneStateListener()
            } else {
                showToast("Permission to read phone state is required.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request CALL_PHONE permission
        requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)

        // Request READ_PHONE_STATE permission
        requestReadPhoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)

        // Initialize TelephonyManager and AudioManager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        setContent {
            AppAppelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CallScreen(
                        modifier = Modifier.padding(innerPadding),
                        onPickAudio = { pickAudioLauncher.launch("audio/*") },
                        onMakeCallWithAudio = { phoneNumber ->
                            if (selectedAudioUri != null) {
                                makePhoneCall(phoneNumber)
                            } else {
                                showToast("Please select an audio file first.")
                            }
                        }
                    )
                }
            }
        }
    }

    private val pickAudioLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedAudioUri = uri
                showToast("Audio selected: $uri")
            } else {
                showToast("No audio selected")
            }
        }

    private fun registerPhoneStateListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            try {
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            } catch (e: SecurityException) {
                showToast("Unable to listen to phone state changes due to missing permissions.")
            }
        } else {
            showToast("READ_PHONE_STATE permission is not granted.")
        }
    }

    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)
            when (state) {
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // Call is connected
                    enableSpeakerAndMaxVolume()
                    selectedAudioUri?.let { playPreRecordedAudio(it) }
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    // Call ended
                    disableSpeaker()
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    // Incoming call - do nothing in this case
                }
            }
        }
    }

    private fun makePhoneCall(number: String) {
        if (number.isNotEmpty()) {
            val dial = "tel:$number"
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse(dial)
            }
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent)
                } else {
                    showToast("Permission to make calls is not granted.")
                }
            } catch (e: SecurityException) {
                showToast("Permission denied to make a phone call.")
            } catch (e: Exception) {
                showToast("Failed to make the call.")
            }
        } else {
            showToast("Please enter a valid phone number.")
        }
    }

    private fun playPreRecordedAudio(audioUri: Uri) {
        mediaPlayer?.release() // Release any existing media player instance
        mediaPlayer = MediaPlayer.create(this, audioUri)

        if (mediaPlayer == null) {
            showToast("Failed to play audio. Please try selecting a different audio file.")
            return
        }

        mediaPlayer?.apply {
            setOnCompletionListener {
                it.release()
                mediaPlayer = null
            }
            try {
                start()
            } catch (e: Exception) {
                showToast("Failed to play the audio.")
                release()
                mediaPlayer = null
            }
        }
    }

    private fun enableSpeakerAndMaxVolume() {
        audioManager.apply {
            isSpeakerphoneOn = true
            setStreamVolume(
                AudioManager.STREAM_VOICE_CALL,
                getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                0
            )
        }
    }

    private fun disableSpeaker() {
        audioManager.isSpeakerphoneOn = false
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
    }
}

@Composable
fun CallScreen(
    modifier: Modifier = Modifier,
    onPickAudio: () -> Unit,
    onMakeCallWithAudio: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Enter phone number") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onPickAudio,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pick Audio")
        }
        Button(
            onClick = { onMakeCallWithAudio(phoneNumber) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Call with Selected Audio")
        }
    }
}
