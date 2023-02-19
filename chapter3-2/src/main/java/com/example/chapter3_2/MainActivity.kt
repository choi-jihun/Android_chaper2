package com.example.chapter3_2

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.chapter3_2.databinding.ActivityMainBinding
import android.Manifest
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import java.io.IOException

class MainActivity : AppCompatActivity(), OnTimerTickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var timer: Timer
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var fileName: String = ""
    private var state = State.RELEASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = "${externalCacheDir?.absolutePath}/audioRecordTest"
        timer = Timer(this)

        binding.play.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    onPlay(true)
                }
                else -> {}
            }
        }

        binding.record.setOnClickListener {
            when (state) {
                State.RELEASE -> {
                    record()
                }
                State.RECORDING -> {
                    onRecord(false)
                }
                State.PLAYING -> {

                }
            }
        }

        binding.stop.setOnClickListener {
            when (state) {
                State.PLAYING -> {
                    onPlay(false)
                }
                else -> {}
            }
        }

    }

    private fun record() {
        when {
            //오디오 녹음 권한이 허용 되어 있다면
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                onRecord(true)
            }
            //다시 한 번 권한을 습득할 것인지 요청
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                showPermissionDialog()
            }
            //권한을 요청
            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
        }
    }

    private fun onRecord(start: Boolean) = if (start) startRecording() else stopRecording()


    private fun startRecording() {
        state = State.RECORDING

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //마이크 사용하겠다
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)  //파일 형식
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //3GPP를 지원하는 인코더
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("App", "prepare() failed $e")
            }
            start()
        }

        binding.waveFormView.clearData()
        timer.start()

        binding.record.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.baseline_stop_24)
        )
        binding.record.imageTintList = ColorStateList.valueOf(Color.BLACK)
        binding.play.isEnabled = false
        binding.play.alpha = 0.3f //약간 흐릿하게
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        timer.stop()

        state = State.RELEASE

        binding.record.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.baseline_fiber_manual_record_24
            )
        )
        binding.record.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
        binding.play.isEnabled = true
        binding.play.alpha = 1.0f
    }

    private fun onPlay(start: Boolean) = if (start) startPlaying() else stopPlaying()

    private fun startPlaying() {
        state = State.PLAYING
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
            } catch (e: IOException) {
                Log.e("APP", "media player prepare $e")
            }
            start()
        }

        binding.waveFormView.clearWave()
        timer.start()

        //player의 파일재생이 종료되었을때 호출
        player?.setOnCompletionListener {
            stopPlaying()
        }

        binding.record.isEnabled = false
        binding.record.alpha = 0.3f
    }

    private fun stopPlaying() {
        state = State.RELEASE
        player?.release()
        player = null

        timer.stop()

        binding.record.isEnabled = true
        binding.record.alpha = 1.0f
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).setMessage("녹음 권한을 허용합니다.")
            .setPositiveButton("권한 허용") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_AUDIO_CODE
                )
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun showPermissionSettingDialog() {
        AlertDialog.Builder(this).setMessage("녹음 권한이 허용되어야 앱 사용이 가능합니다")
            .setPositiveButton("권한 변경") { _, _ ->
                navigateToSetting()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun navigateToSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null) //현재 패키지 name인 chapter3-2의 설정으로 보내겠다
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermission =
            requestCode == REQUEST_RECORD_AUDIO_CODE && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if (audioRecordPermission) {
            onRecord(true)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO
                )
            )
                showPermissionDialog()
            else {
                showPermissionSettingDialog()
            }
        }
    }

    private enum class State {
        RELEASE, RECORDING, PLAYING
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_CODE = 100
    }

    override fun onTick(duration: Long) {
        val millisec = duration % 1000
        val second = (duration / 1000) % 60
        val minute = (duration / 1000) / 60

        binding.timerTextView.text = String.format("%02d:%02d.%02d", minute, second, millisec / 10)

        if(state == State.PLAYING)
            binding.waveFormView.replayAmplitude()
        else if(state == State.RECORDING)
            binding.waveFormView.addAmplitude(recorder?.maxAmplitude?.toFloat() ?: 0f)
    }
}