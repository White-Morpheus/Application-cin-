package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MediaViewModel
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MediaViewModel = viewModel(
        factory = MediaViewModel.Factory(application)
      )
      val isDarkMode by viewModel.isDarkMode.collectAsState()

      MyApplicationTheme(darkTheme = isDarkMode) {
        var selectedTab by remember { mutableStateOf(0) } // 0 = Library, 1 = Search

        Scaffold(
          modifier = Modifier.fillMaxSize(),
          bottomBar = {
            NavigationBar {
              NavigationBarItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                icon = {
                  Icon(
                    imageVector = if (selectedTab == 0) Icons.Filled.ListAlt else Icons.Outlined.ListAlt,
                    contentDescription = "My Library"
                  )
                },
                label = { Text("Ma collection") },
                modifier = Modifier.testTag("tab_library")
              )
              NavigationBarItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                icon = {
                  Icon(
                    imageVector = if (selectedTab == 1) Icons.Filled.Add else Icons.Outlined.Add,
                    contentDescription = "Ajouter nouvel elément"
                  )
                },
                label = { Text("Ajouter nouvel elément") },
                modifier = Modifier.testTag("tab_search")
              )
            }
          }
        ) { innerPadding ->
          when (selectedTab) {
            0 -> LibraryScreen(
              viewModel = viewModel,
              modifier = Modifier.padding(innerPadding)
            )
            1 -> SearchScreen(
              viewModel = viewModel,
              modifier = Modifier.padding(innerPadding)
            )
          }
        }
      }
    }
  }
}

