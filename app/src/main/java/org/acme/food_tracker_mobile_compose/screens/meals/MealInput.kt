package org.acme.food_tracker_mobile_compose.screens.meals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel

@Composable
fun MealInput(
    viewModel: MainScreenViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    addNameField: Boolean = true,
    additionalActionAfterSubmit: () -> Unit = {}
) {
    if (addNameField) {
        NameField(viewModel)
        Spacer(modifier = Modifier.height(16.dp))
    }
    KcalField(viewModel, scope, scaffoldState, additionalActionAfterSubmit)
    Spacer(modifier = Modifier.height(16.dp))
    TimeOfDaySlider(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    Buttons(scope, viewModel, scaffoldState, additionalActionAfterSubmit)
}

@Composable
private fun NameField(viewModel: MainScreenViewModel) {
    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        value = viewModel.nameTextFieldState,
        label = { Text("Name") },
        onValueChange = { viewModel.nameTextFieldState = it },
        keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Down) }),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun KcalField(
    viewModel: MainScreenViewModel,
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { sendMeal(scope, viewModel, scaffoldState, additionalActionAfterSubmit) }),
            singleLine = true,
            modifier = if (!kcalFieldContainsPotentialExpression) Modifier.fillMaxWidth() else Modifier
        )
        if (viewModel.kcalFieldContainsPotentialExpression()) {
            Text(text = "${viewModel.evaluateKcal()?.toString() ?: "NAN"} kcal")
        }
    }
}

@Composable
private fun TimeOfDaySlider(viewModel: MainScreenViewModel) {
    Slider(
        value = viewModel.sliderPosition,
        onValueChange = {
            viewModel.sliderPosition = it
        },
        steps = 2 //does not count leftmost and rightmost
    )
    Row {
        Text(
            text = "Breakfast",
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = "Lunch",
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = "Snack",
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
        Text(
            text = "Dinner",
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.8f)
        )
    }
}

@Composable
private fun Buttons(
    scope: CoroutineScope,
    viewModel: MainScreenViewModel,
    scaffoldState: ScaffoldState,
    additionalActionAfterSubmit: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { sendMeal(scope, viewModel, scaffoldState, additionalActionAfterSubmit) },
            enabled = viewModel.validated()
        ) {
            Text("Add")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Exercise")
            Switch(
                checked = viewModel.exerciseSwitchState,
                onCheckedChange = { viewModel.exerciseSwitchState = !viewModel.exerciseSwitchState }
            )
        }
    }
}


private fun sendMeal(
    scope: CoroutineScope,
    viewModel: MainScreenViewModel,
    scaffoldState: ScaffoldState,
    additionalActionAfterSubmit: () -> Unit,
) {
    if (!viewModel.validated()) {
        return
    }

    scope.launch {
        val postSuccessful = viewModel.submitMeal()
        additionalActionAfterSubmit()
        scaffoldState.snackbarHostState.showSnackbar(if (postSuccessful) "ok" else "something went wrong")
    }
}
