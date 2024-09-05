package team.co2.medical_records.service.audio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import team.co2.medical_records.MainActivity
import team.co2.medical_records.R
import team.co2.medical_records.service.bluetooth.BluetoothResponse
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AudioUpload(
    val startAt: String,
    val filePath: String,
    val fileMD5: String,
    val nearbyBluetooth: List<String>
)


class AudioRecordingService : Service() {
    private lateinit var medicalRecordAPI: MedicalRecordAPI

    private val esp32Handler: Handler = Handler(Looper.getMainLooper())
    private var isRecording = false

    private var esp32Runnable: Runnable? = null
    private lateinit var esp32Communicator: ESP32Communicator

    private lateinit var audioRecord: AudioRecord
    private lateinit var recordingThread: Thread
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var recordStartTime = ""

    private val recordBufferSize = AudioRecord.getMinBufferSize(RECORD_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_FORMAT)

    private var audioUploadRunnable: Runnable? = null
    private var nearbyBluetooth: MutableList<String> = mutableListOf()
    private val audioUploadHandler: Handler = Handler(Looper.getMainLooper())
    private var previousAudioUid: String? = null
    private val uploadTask: MutableList<AudioUpload> = mutableListOf()

    companion object {
        const val RECORD_SAMPLE_RATE = 16000
        const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC
        const val AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val NEARBY_BLUETOOTH_DB_MIN = -65
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        medicalRecordAPI = MedicalRecordAPI(baseContext.applicationContext)

        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        esp32Communicator = ESP32Communicator(usbManager)

        deleteProjectRecordData()

        startForegroundService()
        startReadingNearbyBluetooth()
        startRecording()
        startUploading()
    }

    private fun startForegroundService() {
        val notificationChannelId = "RecordingServiceChannel"
        val channelName = "Medical Record Audio Channel"

        val channel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("醫療紀錄整合輔助系統")
            .setContentText("背景錄音程序執行中...")
            .setSmallIcon(R.drawable.baseline_medical_information_24) // Replace with a valid icon resource
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun deleteProjectRecordData() {
        val dir = getExternalFilesDir(null)  // Replace with your specific directory if needed

        if (dir != null && dir.exists()) {
            val files = dir.listFiles()
            if (files != null) {
                for (file in files) {
                    deleteFileOrDirectory(file)
                }
            }
        }
    }

    private fun deleteFileOrDirectory(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            val children = fileOrDirectory.listFiles()
            if (children != null) {
                for (child in children) {
                    deleteFileOrDirectory(child)
                }
            }
        }
        fileOrDirectory.delete()
    }

    private fun startReadingNearbyBluetooth() {
        esp32Runnable = Runnable {
            val devices = esp32Communicator.getAllUsbDevices()
            val index = esp32Communicator.findDevice(baseContext.applicationContext, devices)
            if (index != -1) {
                // Connect to the found device and start reading
                esp32Communicator.setDeviceConnection(devices[index]).startReading { data ->
                    for (device in data.nearby_devices) {
                        if (!nearbyBluetooth.contains(device.MAC) && device.rssi > NEARBY_BLUETOOTH_DB_MIN) {
                            nearbyBluetooth.add(device.MAC)
                        }
                    }
                }
            } else {
                // If no device is found, retry after 1 second
                esp32Handler.postDelayed(esp32Runnable!!, 1000)
            }
        }
        // Start the first check immediately
        esp32Handler.post(esp32Runnable!!)
    }

    private fun startRecording() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("AudioRecordingService", "RECORD_AUDIO permission not granted")
            return
        }
        audioRecord = AudioRecord(AUDIO_SOURCE, RECORD_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_FORMAT, recordBufferSize)
        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("AudioRecordingService", "error initializing AudioRecord");
            return
        }

        recordingThread = Thread {
            var totalFramesRecorded = 0
            val audioData = ByteArray(recordBufferSize)
            var wavFile = createNewWavFile()
            var wavOutputStream = RandomAccessFile(wavFile, "rw")
            writeWavHeader(wavOutputStream)

            while (isRecording) {
                val read = audioRecord.read(audioData, 0, audioData.size)
                if (read > 0) {
                    try {
                        wavOutputStream.write(audioData, 0, read)
                        totalFramesRecorded += read / 2
                        // Check if we've reached 30 seconds worth of frames
                        if (totalFramesRecorded >= RECORD_SAMPLE_RATE * 30) {
                            val newStartTime = sdf.format(Date(System.currentTimeMillis()))
                            totalFramesRecorded = 0

                            updateWavHeader(wavOutputStream)
                            wavOutputStream.close()
                            addRecordingToMediaLibrary(wavFile)
                            val uploadPath = wavFile.absolutePath
                            val fileMd5 = calculateMD5(uploadPath)

                            wavFile = createNewWavFile()
                            wavOutputStream = RandomAccessFile(wavFile, "rw")
                            writeWavHeader(wavOutputStream)

                            uploadTask.add(AudioUpload(recordStartTime, uploadPath, fileMd5 ?: "", nearbyBluetooth))
                            nearbyBluetooth = mutableListOf()
                            recordStartTime = newStartTime
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        isRecording = true
        recordStartTime = sdf.format(Date(System.currentTimeMillis()))
        audioRecord.startRecording()
        recordingThread.start()
    }

    private fun startUploading() {
        audioUploadRunnable = Runnable {
            while (uploadTask.isNotEmpty()) {
                val task = uploadTask[0]
                medicalRecordAPI.transcriptBedAudioUpload(task.filePath, task.fileMD5, task.startAt, previousAudioUid, task.nearbyBluetooth, {
                    val file = File(task.filePath)
                    if (file.exists()) {
                        file.delete()
                        previousAudioUid = it
                    }
                    Log.d("AudioRecordingService", "transcriptBedAudioUpload success: $it")
                }, {
                    Log.e("AudioRecordingService", "transcriptBedAudioUpload error: $it")
                })
                uploadTask.removeAt(0)
            }
            audioUploadHandler.postDelayed(audioUploadRunnable!!, 3000)
        }
        audioUploadHandler.post(audioUploadRunnable!!)
    }

    private fun createNewWavFile(): File {
        return File(baseContext.applicationContext.getExternalFilesDir(null), "recording_${System.currentTimeMillis()}.wav")
    }

    private fun writeWavHeader(out: RandomAccessFile) {
        val channels = 1
        val byteRate = 16 * RECORD_SAMPLE_RATE * channels / 8

        val header = ByteArray(44)
        ByteBuffer.wrap(header).apply {
            order(ByteOrder.LITTLE_ENDIAN)
            put("RIFF".toByteArray())
            putInt(36)  // Chunk size
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16)  // Subchunk1Size (16 for PCM)
            putShort(1.toShort())  // AudioFormat (1 for PCM)
            putShort(channels.toShort())
            putInt(RECORD_SAMPLE_RATE)
            putInt(byteRate)
            putShort((2 * 16 / 8).toShort())
            putShort(16.toShort())
            put("data".toByteArray())
            putInt(0)
        }

        out.write(header)
    }

    private fun updateWavHeader(out: RandomAccessFile) {
        val fileSize = out.length().toInt()
        out.seek(4)
        out.writeInt(fileSize - 8)  // Chunk size
        out.seek(40)
        out.writeInt(fileSize - 44)  // Subchunk2Size
    }

    private fun addRecordingToMediaLibrary(wavFile: File) {
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, wavFile.name)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/wav")
            put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            resolver.openOutputStream(uri).use { outputStream ->
                wavFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream!!)
                }
            }

            values.clear()
            values.put(MediaStore.Audio.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }

        Log.d("AudioRecordingService", "Save Success, filePath: ${wavFile.absolutePath}")
        MediaScannerConnection.scanFile(
            applicationContext,
            arrayOf(wavFile.absolutePath),
            arrayOf("audio/wav"),
            null
        )
    }

    private fun calculateMD5(filePath: String): String? {
        try {
            val file = File(filePath)
            val digest = MessageDigest.getInstance("MD5")
            val inputStream = FileInputStream(file)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
            inputStream.close()

            val md5 = digest.digest().joinToString("") { "%02x".format(it) }
            Log.d("AudioRecordingService", "MD5: $md5")
            return md5
        } catch (e: Exception) {
            Log.e("AudioRecordingService", "Error calculating MD5: ${e.message}")
            return null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        if (esp32Runnable != null){
            esp32Handler.removeCallbacks(esp32Runnable!!)
        }
        if (audioUploadRunnable != null){
            audioUploadHandler.removeCallbacks(audioUploadRunnable!!)
        }
        recordingThread.join()
        audioRecord.stop()
        audioRecord.release()
    }
}
