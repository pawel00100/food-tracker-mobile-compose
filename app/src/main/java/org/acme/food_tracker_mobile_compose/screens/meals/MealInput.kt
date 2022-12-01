package org.acme.food_tracker_mobile_compose.screens.meals

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.acme.food_tracker_mobile_compose.httpclient.Dish
import org.acme.food_tracker_mobile_compose.ui.theme.BlankedText
import org.acme.food_tracker_mobile_compose.viewmodel.MainScreenViewModel

@Composable
fun MealInput(
    viewModel: MainScreenViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    create: Boolean = true,
    additionalActionAfterSubmit: () -> Unit = {},
) {
    NameField(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    KcalField(viewModel, scope, scaffoldState, additionalActionAfterSubmit)
    Spacer(modifier = Modifier.height(16.dp))
    TimeOfDayInput(viewModel)
    Spacer(modifier = Modifier.height(16.dp))
    Buttons(scope, viewModel, scaffoldState, create, additionalActionAfterSubmit)
}

@Composable
private fun NameField(viewModel: MainScreenViewModel) {
    val localFocusManager = LocalFocusManager.current
    val isKeyboardOpen by keyboardAsState()

    Box {
        OutlinedTextField(
            value = viewModel.nameTextFieldState,
            label = { Text("Name") },
            onValueChange = {
                viewModel.nameTextFieldState = it
                viewModel.dropdownMenuOpened = viewModel.nameTextFieldState.isNotEmpty()
                println("Keyboard opened: $isKeyboardOpen")
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            keyboardActions = KeyboardActions(onDone = { localFocusManager.moveFocus(FocusDirection.Down) }),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    println("Focus: $focusState, ${focusState.isFocused}")
                    viewModel.dropdownMenuOpened = focusState.hasFocus && viewModel.nameTextFieldState.isNotEmpty()
                    println("Keyboard opened: $isKeyboardOpen")
                },
        )

        val dishes = viewModel.dishViewModel.getFilteredDishes(viewModel.nameTextFieldState)
        DropdownMenu(
            expanded = viewModel.dropdownMenuOpened && isKeyboardOpen,
            onDismissRequest = {},
            properties = PopupProperties(focusable = false)
        ) {
            val columnModifier = if (dishes.size <= 3) Modifier else Modifier
                .height(300.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxHeight()
            Column(modifier = columnModifier) {
                for (dish in dishes) {
                    DishDropdownItem(dish, viewModel)
                }
            }
        }
    }
}

@Composable
private fun DishDropdownItem(dish: Dish, viewModel: MainScreenViewModel) {
    val dishName = if (dish.name.length < 22) dish.name else "${dish.name.subSequence(0, 18)}..."
    DropdownMenuItem(onClick = {
        viewModel.nameTextFieldState = dish.name
        viewModel.kcalTextFieldState = dish.kcalExpression ?: ""
    }) {
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.weight(1f)) {
            Text(dishName)
            Text("${dish.kcal} kcal", color = MaterialTheme.colors.BlankedText)
        }
    }
}

@Composable
private fun KcalField(
    viewModel: MainScreenViewModel,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    additionalActionAfterSubmit: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = viewModel.kcalTextFieldState,
            label = { Text("Kcal") },
            trailingIcon = {
                if (viewModel.kcalFieldContainsPotentialExpression()) {
                    Text(text = viewModel.evaluateKcal()?.toString() ?: "NAN")
                }
            },
            onValueChange = { viewModel.kcalTextFieldState = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { sendMeal(scope, viewModel, scaffoldState, additionalActionAfterSubmit) }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TimeOfDayInput(viewModel: MainScreenViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            TimeOfDaySlider(viewModel)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight(),
        ) {
            Text(text = viewModel.sliderTime().toString())
        }
    }
}

@Composable
private fun TimeOfDaySlider(viewModel: MainScreenViewModel) {
    Slider(
        value = viewModel.sliderPosition,
        onValueChange = {
            viewModel.sliderPosition = it //0..1
        },
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
    create: Boolean,
    additionalActionAfterSubmit: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Button(
            onClick = { sendMeal(scope, viewModel, scaffoldState, additionalActionAfterSubmit) },
            enabled = viewModel.validated()
        ) {
            Text(if (create) "Add" else "Save")
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

@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = keypadHeight > screenHeight * 0.15
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}
