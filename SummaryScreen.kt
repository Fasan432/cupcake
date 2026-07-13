package com.example.cupcakeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cupcakeapp.data.OrderUiState

@Composable
fun SummaryScreen(
    orderUiState: OrderUiState,
    onSendButtonClicked: (String, String) -> Unit,
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Quantity: ${orderUiState.quantity}")
        Text(text = "Flavor: ${orderUiState.flavor}")
        Text(text = "Pickup Date: ${orderUiState.date}")
        Text(text = "Total Price: ${orderUiState.price}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val summary = "Order Summary:\n" +
                        "Quantity: ${orderUiState.quantity}\n" +
                        "Flavor: ${orderUiState.flavor}\n" +
                        "Pickup Date: ${orderUiState.date}\n" +
                        "Total Price: ${orderUiState.price}"
                onSendButtonClicked("New Cupcake Order", summary)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Order")
        }
        OutlinedButton(onClick = onCancelButtonClicked, modifier = Modifier.fillMaxWidth()) {
            Text("Cancel")
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SummaryPreview() {
    SummaryScreen(
        orderUiState = OrderUiState(
            quantity = 6,
            flavor = "Chocolate",
            date = "Today",
            price = "$12.00"
        ),
        onSendButtonClicked = { _, _ -> },
        onCancelButtonClicked = {}
    )
}
