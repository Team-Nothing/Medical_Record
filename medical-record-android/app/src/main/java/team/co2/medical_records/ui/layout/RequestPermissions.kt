package team.co2.medical_records.ui.layout

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissions(
    context: Context,
    result: (isGranted: Boolean) -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }

    val permissions = mutableListOf(
        Manifest.permission.RECORD_AUDIO
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult: Map<String, Boolean> ->
        val allGranted = permissionsResult.all { it.value }
        if (!allGranted) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        } else {
            showDialog.value = false
            result(true)
        }
    }

    val areAllPermissionsGranted = permissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
    if (!areAllPermissionsGranted) {
        showDialog.value = true
    } else {
        result(true)
    }

    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> "錄音"
            Manifest.permission.READ_MEDIA_AUDIO -> "讀取媒體檔案"
            Manifest.permission.POST_NOTIFICATIONS -> "發送通知"
            else -> "未知權限"
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                result(false)
            },
            title = {
                Text(text = "Android 權限要求")
            },
            text = {
                Column {
                    Text("為確保 APP 能夠正常運行，請授權以下這些權限:")
                    Spacer(modifier = Modifier.height(8.dp))
                    permissions.forEach { permission ->
                        Text(text = "- ${getPermissionDescription(permission)}")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        permissionsLauncher.launch(permissions.toTypedArray())
                    }
                ) {
                    Text("授權")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        (context as? Activity)?.finish()
                    }
                ) {
                    Text("取消")
                }
            }
        )
    }
}