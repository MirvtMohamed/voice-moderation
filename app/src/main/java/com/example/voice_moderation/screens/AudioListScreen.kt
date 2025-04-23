package com.example.voice_moderation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voice_moderation.data.viewmodel.AudioViewModel


@Composable
fun AudioListScreen(viewModel: AudioViewModel = viewModel()) {
    val audioFiles by viewModel.audioFiles.collectAsState()

    LazyColumn {
        items(audioFiles) { audio ->
            Text(
                text = audio.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
