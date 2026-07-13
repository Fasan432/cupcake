package com.example.cupcakeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PickupScreen(
    subtotal: String,
    options: List<String>,
    onNextButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedDate by rememberSaveable { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        options.forEach { item ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = selectedDate == item,
                        onClick = {
                            selectedDate = item
                            onSelectionChanged(item)
                        }
                    )
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedDate == item,
                    onClick = {
                        selectedDate = item
                        onSelectionChanged(item)
                    }
                )
                Text(item)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Subtotal: $subtotal", style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(onClick = onCancelButtonClicked, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = onNextButtonClicked,
                modifier = Modifier.weight(1f),
                enabled = selectedDate.isNotEmpty()
            ) {
                Text("Next")
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PickupPreview() {
    PickupScreen(
        subtotal = "$10.00",
        options = listOf("Today", "Tomorrow", "Next Day"),
        onNextButtonClicked = {},
        onCancelButtonClicked = {},
        onSelectionChanged = {}
    )
}
