package org.acme.food_tracker_mobile_compose.screens.meals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.viewmodel.DishCreateViewModel

@Composable
fun DishInput(
    viewModel: DishCreateViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    create: Boolean = true,
    additionalActionAfterSubmit: () -> Unit = {},
) {
    NameField(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    KcalField(viewModel, scope, scaffoldState, additionalActionAfterSubmit)
    Spacer(modifier = Modifier.height(16.dp))
    Buttons(scope, viewModel, scaffoldState, create, additionalActionAfterSubmit)
}

@Composable
private fun NameField(viewModel: DishCreateViewModel) {
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        value = viewModel.nameTextFieldState,
        label = { Text("Name") },
        onValueChange = { viewModel.nameTextFieldState = it },
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Down) }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun KcalField(
    viewModel: DishCreateViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    additionalActionAfterSubmit: () -> Unit,
) {
    val kcalFieldContainsPotentialExpression = viewModel.kcalFieldContainsPotentialExpression()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = viewModel.kcalTextFieldState,
            label = { Text("Kcal") },
            onValueChange = { viewModel.kcalTextFieldState = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { sendDish(scope, viewModel, scaffoldState, additionalActionAfterSubmit) }),
            singleLine = true,
            modifier = if (!kcalFieldContainsPotentialExpression) Modifier.fillMaxWidth() else Modifier
        )
        if (viewModel.kcalFieldContainsPotentialExpression()) {
            Text(text = "${viewModel.evaluateKcal()?.toString() ?: "NAN"} kcal")
        }
    }
}


@Composable
private fun Buttons(
    scope: CoroutineScope,
    viewModel: DishCreateViewModel,
    scaffoldState: ScaffoldState,
    create: Boolean,
    additionalActionAfterSubmit: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { sendDish(scope, viewModel, scaffoldState, additionalActionAfterSubmit) },
            enabled = viewModel.validated()
        ) {
            Text(if (create) "Add" else "Save")
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
