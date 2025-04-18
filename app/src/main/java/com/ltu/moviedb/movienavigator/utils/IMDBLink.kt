package com.ltu.moviedb.movienavigator.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun IMDBLink(imdbId: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    if (!imdbId.isNullOrBlank()) {
        Text(
            text = "View on IMDB",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline,
            modifier = modifier.clickable {
                try {
                    // Try to open in IMDB app first
                    val appIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("imdb:///title/$imdbId")
                        `package` = "com.imdb.mobile"
                    }

                    // Fallback to web browser
                    val webIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.imdb.com/title/$imdbId")
                    }

                    try {
                        context.startActivity(appIntent)
                    } catch (e: ActivityNotFoundException) {
                        context.startActivity(webIntent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Couldn't open IMDB", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}