package com.example.incometracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.incometracker.ui.theme.IncomeTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent { App() }
  }
}

@Composable
private fun App() {
  IncomeTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Build pipeline is working.", style = MaterialTheme.typography.titleLarge)
          Spacer(Modifier.height(8.dp))
          Text("Next: we’ll add the full finance app screens.")
        }
      }
    }
  }
}
