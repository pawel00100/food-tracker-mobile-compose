package org.acme.food_tracker_mobile_compose.screens.meals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.R
import org.acme.food_tracker_mobile_compose.screens.Screen
import org.acme.food_tracker_mobile_compose.screens.misccomponents.ClearTextFieldIcon
import org.acme.food_tracker_mobile_compose.screens.misccomponents.KcalField
import org.acme.food_tracker_mobile_compose.viewmodel.DishCreateViewModel

@Composable
fun DishInput(
    navController: NavController,
    viewModel: DishCreateViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    create: Boolean = true,
    additionalActionAfterSubmit: () -> Unit = {},
) {
    NameField(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    KcalField(viewModel) { sendDish(scope, viewModel, scaffoldState, additionalActionAfterSubmit) }
    Spacer(modifier = Modifier.height(16.dp))
    if (viewModel.barcode != null) {
        BarcodeRow(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
    }
    Buttons(navController, scope, viewModel, scaffoldState, create, additionalActionAfterSubmit)
}

@Composable
private fun NameField(viewModel: DishCreateViewModel) {
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        value = viewModel.nameTextFieldState,
        label = { Text("Name") },
        trailingIcon = { ClearTextFieldIcon(viewModel.nameTextFieldState) { viewModel.nameTextFieldState = "" } },
        onValueChange = { viewModel.nameTextFieldState = it },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Down) }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun Buttons(
    navController: NavController,
    scope: CoroutineScope,
    viewModel: DishCreateViewModel,
    scaffoldState: ScaffoldState,
    create: Boolean,
    additionalActionAfterSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { sendDish(scope, viewModel, scaffoldState, additionalActionAfterSubmit) },
            enabled = viewModel.validated()
        ) {
            Text(if (create) "Add" else "Save")
        }

        IconButton(onClick = {
            navController.navigate(Screen.DishBarcodeScanner.withArgs(if (create) "create" else "edit"))
        }) {
            Icon(painterResource(R.drawable.barcode_scanner_24px), contentDescription = "Open barcode scanner")
        }
    }
}

@Composable
private fun BarcodeRow(viewModel: DishCreateViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(R.drawable.barcode_24px), contentDescription = "Barcode number")
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = viewModel.barcode?.toString() ?: "", modifier = Modifier.weight(1f))
        IconButton(onClick = {
            viewModel.barcode = null
        }) {
            Icon(Icons.Filled.Clear, contentDescription = "Remove barcode")
        }
    }
}

private fun sendDish(
    scope: CoroutineScope,
    viewModel: DishCreateViewModel,
    scaffoldState: ScaffoldState,
    additionalActionAfterSubmit: () -> Unit,
) {
    if (!viewModel.validated()) {
        return
    }

    scope.launch {
        val postSuccessful = viewModel.submitDish()
        additionalActionAfterSubmit()
        scaffoldState.snackbarHostState.showSnackbar(if (postSuccessful) "ok" else "something went wrong")
    }
}
