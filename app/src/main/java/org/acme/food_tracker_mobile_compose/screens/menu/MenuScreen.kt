package org.acme.food_tracker_mobile_compose.screens.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.acme.food_tracker_mobile_compose.viewmodel.MenuScreenViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    viewModel: MenuScreenViewModel = MenuScreenViewModel(),
    padding: PaddingValues,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding), color = MaterialTheme.colors.background
    ) {
        Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)) {
            BackButton(navController)
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = viewModel.serverAddressFieldState,
                label = { Text("Server") },
                onValueChange = { viewModel.serverAddressFieldState = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = viewModel.kcalTargetFieldState,
                label = { Text("Kcal target") },
                onValueChange = { viewModel.kcalTargetFieldState = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BackButton(navController: NavController) {
    Box() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
        }
    }
}