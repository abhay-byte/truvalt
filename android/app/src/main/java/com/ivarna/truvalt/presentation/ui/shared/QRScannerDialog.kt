package com.ivarna.truvalt.presentation.ui.shared

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.common.GlobalHistogramBinarizer
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun QRScannerDialog(
    onDismiss: () -> Unit,
    onQRCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val barcodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val qrText = result.contents
            val secret = if (qrText.startsWith("otpauth://")) {
                Uri.parse(qrText).getQueryParameter("secret") ?: qrText
            } else {
                qrText
            }
            onQRCodeScanned(secret)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap == null) {
                    errorMessage = "Could not decode image"
                    return@let
                }

                val result = decodeQRFromBitmap(bitmap)
                
                if (result != null) {
                    val secret = if (result.startsWith("otpauth://")) {
                        Uri.parse(result).getQueryParameter("secret") ?: result
                    } else {
                        result
                    }
                    onQRCodeScanned(secret)
                } else {
                    errorMessage = "No QR code found in image. Try a clearer image or use camera."
                }
            } catch (e: Exception) {
                errorMessage = "Failed to read image: ${e.message}"
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Scan QR Code") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Choose an option to scan the 2FA QR code:")

                Button(
                    onClick = {
                        val options = ScanOptions()
                        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        options.setPrompt("Scan 2FA QR Code")
                        options.setBeepEnabled(false)
                        options.setOrientationLocked(true)  // Lock to portrait
                        barcodeLauncher.launch(options)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan with Camera")
                }

                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Select from Gallery")
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun decodeQRFromBitmap(bitmap: Bitmap): String? {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val reader = MultiFormatReader()
    
    // Try 1: Normal with HybridBinarizer
    try {
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        return reader.decode(binaryBitmap)?.text
    } catch (e: Exception) { }
    
    // Try 2: Normal with GlobalHistogramBinarizer (better for low contrast)
    try {
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
        return reader.decode(binaryBitmap)?.text
    } catch (e: Exception) { }
    
    // Try 3: Inverted colors with HybridBinarizer
    try {
        val invertedPixels = IntArray(pixels.size) { i ->
            val pixel = pixels[i]
            Color.argb(
                Color.alpha(pixel),
                255 - Color.red(pixel),
                255 - Color.green(pixel),
                255 - Color.blue(pixel)
            )
        }
        val source = RGBLuminanceSource(width, height, invertedPixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        return reader.decode(binaryBitmap)?.text
    } catch (e: Exception) { }
    
    // Try 4: Inverted with GlobalHistogramBinarizer
    try {
        val invertedPixels = IntArray(pixels.size) { i ->
            val pixel = pixels[i]
            Color.argb(
                Color.alpha(pixel),
                255 - Color.red(pixel),
                255 - Color.green(pixel),
                255 - Color.blue(pixel)
            )
        }
        val source = RGBLuminanceSource(width, height, invertedPixels)
        val binaryBitmap = BinaryBitmap(GlobalHistogramBinarizer(source))
        return reader.decode(binaryBitmap)?.text
    } catch (e: Exception) { }
    
    return null
}
