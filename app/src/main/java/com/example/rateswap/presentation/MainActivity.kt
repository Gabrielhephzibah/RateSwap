package com.example.rateswap.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rateswap.domain.model.ExchangeRate

import com.example.rateswap.ui.theme.RateSwapTheme
import com.example.rateswap.utils.SelectionSource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RateSwapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: MainScreenViewModel = hiltViewModel()
                    //val state = viewModel.state
                   // val sellingCurrency = viewModel.sellingCurrency
                    //var amountToSell by remember { mutableStateOf("") }
//
//                   LaunchedEffect(key1 = amountToSell) {
//                       viewModel.amountToSell(amountToSell)
//                   }

                    RateSwapScreen(
                        screenState = viewModel.state,
                        sellingCurrency = viewModel.sellingCurrency,
                        updateSellingCurrency = {viewModel.updateSellingCurrency(it)},
                        updateAmount = {  viewModel.amountToSell(it) },
                        buyingCurrency = viewModel.buyingCurrency,
                        updateBuyingCurrency = {viewModel.updateBuyingCurrency(it) }
                    )


                }
            }
        }
    }
}

@Composable
fun RateSwapScreen(
    screenState: MainScreenState,
    sellingCurrency: String,
    updateSellingCurrency: (String) ->Unit,
    updateAmount: (String)->Unit,
    buyingCurrency: String,
    updateBuyingCurrency: (String) -> Unit)
{
    var openDialog by remember { mutableStateOf(false) }
    var amountToSell by remember { mutableStateOf("") }
    val amountToReceive by remember { mutableStateOf("0.00") }
    //var currencyToSell by remember { mutableStateOf("EUR") }
    //var currencyToReceive by remember { mutableStateOf("USD") }
    var dialogSource by remember { mutableStateOf<SelectionSource?>(null) }
    LaunchedEffect(key1 = amountToSell) {
        updateAmount(amountToSell)
        //viewModel.amountToSell(amountToSell)
    }
    Column(
        modifier = Modifier.fillMaxSize().
        padding(15.dp)) {
        Text(
            text = "My Balances",
            color = Color.Black
        )
        Text(
            text = "EUR 100.00",
            modifier = Modifier.padding(16.dp),
            color = Color.Black
        )
        Card(
            modifier = Modifier.padding()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Sell",
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = amountToSell,
                        onValueChange = {
                            amountToSell = it
                        },
                        label = { Text("Enter amount to sell") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        singleLine = true
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            openDialog = true
                            dialogSource = SelectionSource.SELL
                        }
                    ) {
                        Text(
                            text = sellingCurrency,
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }
                }
            }
            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp
            )
            Column(modifier = Modifier.padding(horizontal = 15.dp
                , vertical = 15.dp).padding(bottom = 10.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Receive",
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = screenState.amountToReceive.toString(),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable {
                            openDialog = true
                            dialogSource = SelectionSource.RECEIVE
                        }
                    ) {
                        Text(
                            text = buyingCurrency,
                            color = Color.Black
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Commission Fee:",
                color = Color.Black
            )
            Row {
                Text(
                    text = sellingCurrency ,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = "1.00",
                    color = Color.Black
                )
            }

        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.padding(5.dp))
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Total Amount:",
                color = Color.Black
            )
            Row {
                Text(
                    text = sellingCurrency,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = "99.00",
                    color = Color.Black
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            onClick = { /*TODO*/ },
        ) {
            Text(
                text = "Swap",
                color = Color.White,
                fontSize = 20.sp
            )
        }

    }
    if (openDialog && dialogSource != null) {
        RateDialog(
            onDismissRequest = { openDialog = false },
            rateList = screenState.exchangeRates,
            onSelectedCurrency = { currency ->
                if (dialogSource == SelectionSource.SELL) {
                    updateSellingCurrency(currency)
                    //currencyToSell = currency
                }else{
                    updateBuyingCurrency(currency)
                }
            }
        )
    }

}
@Composable
fun RateDialog(onDismissRequest: () -> Unit, rateList: ExchangeRate, onSelectedCurrency: (String) -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth().height(400.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Start)
                        .clickable { onDismissRequest() }
                )
                Text(
                    text = "Select Currency",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )

                LazyColumn {
                    items(rateList.rates.size){
                        val item = rateList.rates.entries.elementAt(it)
                        RateItem(item.key, item.value){ currency ->
                            onSelectedCurrency(currency)
                            onDismissRequest()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RateItem(currency: String, rate: Double, onSelectedItem: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                onSelectedItem(currency)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = currency,
            color = Color.Black
        )
        Text(
            text = rate.toString(),
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RateSwapTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RateSwapScreen(screenState = MainScreenState(ExchangeRate(mapOf("USD" to 1.0, "GBP" to 0.85))), sellingCurrency = "EUR", updateSellingCurrency = {}, updateAmount = {}, buyingCurrency = "USD", updateBuyingCurrency = {})
        }
    }
}