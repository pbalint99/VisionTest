package hu.bme.mogi.android.visiontest

import android.graphics.Bitmap
import android.os.Environment
import android.view.InputDevice
import java.io.File
import java.io.FileOutputStream
object File {
    var smallestVisibleInDegrees = 0f
    var fileName = "VisionTestNamingError"
    lateinit var dir: File
    var fileText = ""

//    fun writeFileOnInternalStorage(sBody: String?) {
//        try {
////            val gpxfile = File(dir, fileName+".txt")
////            val writer = PrintWriter(gpxfile,true)
////            writer.append(sBody)
////            writer.flush()
////            writer.close()
////            File(dir,"$fileName.txt").printWriter().use { out ->
////                out.println(sBody)
////            }
//            val dir: File =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
//            File(dir,"$fileName.txt").appendText(sBody!!)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw IllegalArgumentException("Couldn't write to file")
//        }
//    }
    fun saveImage(finalBitmap: Bitmap) {
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val fname = "Image.png"
        val file = File(dir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getGameControllerIds(): List<Int> {
        val gameControllerDeviceIds = mutableListOf<Int>()
        val deviceIds = InputDevice.getDeviceIds()
        deviceIds.forEach { deviceId ->
            InputDevice.getDevice(deviceId).apply {

                // Verify that the device has gamepad buttons, control sticks, or both.
                if (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
                    || sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
                    // This device is a game controller. Store its device ID.
                    gameControllerDeviceIds
                        .takeIf { !it.contains(deviceId) }
                        ?.add(deviceId)
                }
            }
        }
        return gameControllerDeviceIds
    }
}