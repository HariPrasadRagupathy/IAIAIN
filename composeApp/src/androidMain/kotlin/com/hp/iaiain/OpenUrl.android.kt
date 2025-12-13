package com.hp.iaiain

import android.content.Context
import android.content.Intent
import android.net.Uri

actual fun openUrl(url: String) {
    // This will need context, we'll use a workaround for now
    // In a real app, you'd pass context or use a more sophisticated approach
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Note: This won't work without context. Will need to refactor if Android is needed
        // For now, just a placeholder
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

