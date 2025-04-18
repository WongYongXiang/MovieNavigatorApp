package com.ltu.moviedb.movienavigator.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

@Composable
fun HomepageLink(homepage: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    if (!homepage.isNullOrBlank()) {
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )) {
                append("Official Website")
            }
        }

        Text(
            text = annotatedString,
            modifier = modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = homepage.toUri()
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }

                val chooserIntent = Intent.createChooser(intent, "Open with")
                startActivity(context, chooserIntent, null)
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}