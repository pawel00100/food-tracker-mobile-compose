package org.acme.food_tracker_mobile_compose.screens.misccomponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

interface NameKcalInputViewModel {
    var nameTextFieldState: String
    var kcalTextFieldState: String

    fun kcalFieldContainsPotentialExpression(): Boolean
    fun evaluateKcal(): Int?
}

@Composable
fun KcalField(
    viewModel: NameKcalInputViewModel,
    onDone: () -> Unit,
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
                val hasExpression = viewModel.kcalFieldContainsPotentialExpression()
                Row() {
                    if (hasExpression) {
                        Text(text = viewModel.evaluateKcal()?.toString() ?: "NAN")
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    ClearTextFieldIcon(viewModel.kcalTextFieldState) { viewModel.kcalTextFieldState = "" }
                    if (hasExpression) {
                        Spacer(modifier = Modifier.width(12.dp)) // when trailingIcon becomes a rectangle, right margin gets eaten so this restores it
                    }
                }
            },
            onValueChange = { viewModel.kcalTextFieldState = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
