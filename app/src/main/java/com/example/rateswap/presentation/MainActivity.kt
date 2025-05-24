@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rateswap.domain.model.AccountBalance
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
                    var amount by remember { mutableStateOf("") }
                    LaunchedEffect(key1 = amount) {
                        viewModel.amountToSell(amount)
                    }


                    RateSwapScreen(
                        screenState = viewModel.state,
                        sellingCurrency = viewModel.sellingCurrency,
                        updateSellingCurrency = { viewModel.updateSellingCurrency(it) },
                        buyingCurrency = viewModel.buyingCurrency,
                        updateBuyingCurrency = { viewModel.updateBuyingCurrency(it) },
                        accountBalances = viewModel.state.accountBalances,
                        amountValidation = viewModel.validationError,
                        amountToSell = amount,
                        onAmountChange = {
                            amount = it
                            viewModel.clearValidationError()
                        },
                        onCurrencyChange = {
                            amount = it
                            viewModel.clearValidationError()
                        },
                        updateAmountToReceive = {
                            viewModel.updateAmountToReceive()
                        },
                        submitExchangeRequest = {
                            viewModel.saveAccountBalance()
                        }
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
    buyingCurrency: String,
    updateBuyingCurrency: (String) -> Unit,
    accountBalances : List<AccountBalance>,
    amountValidation : String,
    amountToSell : String,
    onAmountChange: (String) -> Unit,
    onCurrencyChange : (String) -> Unit,
    updateAmountToReceive: () -> Unit,
    submitExchangeRequest:() -> Unit
  )
{
    var openDialog by remember { mutableStateOf(false) }
    var openSubmitDialog by remember { mutableStateOf(false) }
    //val amountToReceive by remember { mutableStateOf("0.00") }
    var isError by rememberSaveable { mutableStateOf(false) }
    var dialogSource by remember { mutableStateOf<SelectionSource?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)) {
        Text(
            text = "My Balances",
            color = Color.Black
        )
        LazyRow(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(accountBalances.size) {
                val item = accountBalances[it]
                AccountBalanceItem(
                    amount = item.balance.toString(),
                    currency = item.currency
                )
            }
        }
        Card(
            modifier = Modifier
                .padding()
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
                            onAmountChange(it)
                            //amountToSell = it
                           // amountValidation = ""
                           // isError = (amountValidation.isNotBlank() || amountValidation.isNotEmpty())
                        },
                        label = { Text("Enter amount to sell") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        singleLine = true,
                        supportingText = {
                            if (amountValidation.isNotBlank()) {
                                Text(
                                    text = amountValidation,
                                    color = Color.Red
                                )
                            }
                        },
                        isError = amountValidation.isNotBlank(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        //keyboardActions = KeyboardActions { isError = (amountValidation.isNotBlank() || amountValidation.isNotEmpty()) },
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
            Column(modifier = Modifier
                .padding(
                    horizontal = 15.dp, vertical = 15.dp
                )
                .padding(bottom = 10.dp)) {
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
                    text = screenState.commissionFee.toString(),
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
                    text = screenState.totalAmountDeducted.toString(),
                    color = Color.Black
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            onClick = {
                if (amountToSell.isNotBlank() && amountValidation.isBlank()) {
                    submitExchangeRequest()
                    openSubmitDialog = true
                }
            },
        ) {
            Text(
                text = "Submit",
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
                onCurrencyChange("")
                updateAmountToReceive()
                if (dialogSource == SelectionSource.SELL) {
                    updateSellingCurrency(currency)
                }else{
                    updateBuyingCurrency(currency)
                }
            }
        )
    }

    if (openSubmitDialog) {
        SubmitDialog(
            onDismissRequest = { openSubmitDialog = false },
            exchangeAlert = "You have successfully converted ${amountToSell.toDouble()} $sellingCurrency to ${screenState.amountToReceive} $buyingCurrency",
            updateAmountToReceive = {
                updateAmountToReceive()
            },
            onCurrencyChange = {
                onCurrencyChange(it)
            }
        )
    }

}

@Composable
fun AccountBalanceItem(
    amount: String,
    currency: String
) {
    Text(
        modifier = Modifier.padding(end = 12.dp),
        text = "$currency $amount",
        color = Color.Black
    )
}
@Composable
fun RateDialog(onDismissRequest: () -> Unit, rateList: ExchangeRate, onSelectedCurrency: (String) -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
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

@Composable
fun SubmitDialog(
    onDismissRequest: () -> Unit,
    exchangeAlert: String,
    updateAmountToReceive: () -> Unit,
    onCurrencyChange: (String) -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = {},
    ) {
        Surface(
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Currency Converted",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally),
                    fontSize = 20.sp
                )
                Text(
                    text = exchangeAlert,
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextButton(
                    onClick = {
                        onDismissRequest()
                        updateAmountToReceive()
                        onCurrencyChange("")
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Done",
                        fontSize = 20.sp
                    )
                }
            }
        }
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
            //SubmitDialog(onDismissRequest = {}) { }
//            AccountBalanceItem(
//                amount = "100.00",
//                currency = "EUR"
//            )
            RateSwapScreen(screenState = MainScreenState(ExchangeRate(mapOf("USD" to 1.0, "GBP" to 0.85))), sellingCurrency = "EUR", updateSellingCurrency = {}, buyingCurrency = "USD", updateBuyingCurrency = {}, accountBalances = listOf(AccountBalance(currency = "EUR", balance = 100.0), AccountBalance(currency = "USD", balance = 50.0), AccountBalance(currency = "USD", balance = 50.00), AccountBalance(currency = "USD", balance = 80000.0), AccountBalance(currency = "USD", balance = 50.0)), amountValidation = "Amount is too lowjjjjj", amountToSell = "100.00", onAmountChange = {}, onCurrencyChange = {}, updateAmountToReceive = {}, submitExchangeRequest = {})
        }
    }
}