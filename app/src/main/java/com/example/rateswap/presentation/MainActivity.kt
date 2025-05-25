@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rateswap.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rateswap.R
import com.example.rateswap.domain.model.AccountBalance
import com.example.rateswap.domain.model.ExchangeRate

import com.example.rateswap.presentation.dialog.ExchangeMessageDialog
import com.example.rateswap.presentation.dialog.ExchangeRateDialog

import com.example.rateswap.ui.theme.RateSwapTheme
import com.example.rateswap.utils.RateDialogSource
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
                    val viewModel: MainViewModel = hiltViewModel()
                    var amount by remember { mutableStateOf("") }
                    LaunchedEffect(key1 = amount) {
                        viewModel.updateAmount(amount)
                    }
                    ExchangeScreen(
                        uiState = viewModel.state,
                        sellingCurrency = viewModel.sellingCurrency,
                        updateSellingCurrency = { viewModel.updateSellingCurrency(it) },
                        buyingCurrency = viewModel.buyingCurrency,
                        updateBuyingCurrency = { viewModel.updateBuyingCurrency(it) },
                        accountBalances = viewModel.state.accountBalances,
                        amountValidation = viewModel.state.validationError,
                        amount = amount,
                        onAmountChange = {
                            amount = it
                            viewModel.clearValidationError()
                        },
                        updateSellingAmount = {
                            amount = it
                            viewModel.clearValidationError()
                        },
                        updateBuyingAmount = {
                            viewModel.updateBuyingAmount()
                        },
                        submitExchange = {
                            viewModel.submitExchange()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeScreen(
    uiState: MainScreenState,
    sellingCurrency: String,
    updateSellingCurrency: (String) -> Unit,
    buyingCurrency: String,
    updateBuyingCurrency: (String) -> Unit,
    accountBalances: List<AccountBalance>,
    amountValidation: String,
    amount: String,
    onAmountChange: (String) -> Unit,
    updateSellingAmount: (String) -> Unit,
    updateBuyingAmount: () -> Unit,
    submitExchange: () -> Unit
) {
    var openRateDialog by remember { mutableStateOf(false) }
    var openSubmitDialog by remember { mutableStateOf(false) }
    var dialogSource by remember { mutableStateOf<RateDialogSource?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Spacer(modifier = Modifier.padding(10.dp))
        Text(
            text = stringResource(R.string.account_balances),
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight(300),
        )
        Spacer(modifier = Modifier.padding(5.dp))
        LazyRow(
            modifier = Modifier.padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
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
        Spacer(modifier = Modifier.padding(10.dp))
        Card(
            modifier = Modifier
                .padding()
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = Color.White,

                ),
            border = BorderStroke(1.dp, Color.Black)
        ) {
            Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 15.dp)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.sell),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600),
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = amount,
                        onValueChange = {
                            onAmountChange(it)
                        },
                        label = {
                            Text(
                                text = stringResource(R.string.enter_amount),
                                fontSize = 15.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
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
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            openRateDialog = true
                            dialogSource = RateDialogSource.SELL
                        }
                    ) {
                        Text(
                            text = sellingCurrency,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(550)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                }
            }
            HorizontalDivider(
                color = Color.Black,
                thickness = 1.dp
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 15.dp)
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.receive),
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight(600)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = uiState.buyingAmount.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable {
                            openRateDialog = true
                            dialogSource = RateDialogSource.RECEIVE
                        }
                    ) {
                        Text(
                            text = buyingCurrency,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(550)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black,
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.commission_fee),
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight(500)
            )
            Row {
                Text(
                    text = sellingCurrency,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight(550)
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = uiState.commissionFee.toString(),
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight(550)
                )
            }
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.amount_to_be_deducted),
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight(500)
            )
            Row {
                Text(
                    text = sellingCurrency,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight(550)
                )
                Spacer(modifier = Modifier.padding(2.dp))
                Text(
                    text = uiState.totalAmountDeducted.toString(),
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight(550)
                )
            }

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            onClick = {
                if (amount.isNotBlank() && amountValidation.isBlank()) {
                    submitExchange()
                    openSubmitDialog = true
                }
            },
        ) {
            Text(
                text = stringResource(R.string.submit),
                color = Color.White,
                fontSize = 20.sp
            )
        }

    }
    if (openRateDialog && dialogSource != null) {
        ExchangeRateDialog(
            onDismissRequest = { openRateDialog = false },
            rateResponse = uiState.exchangeRates,
            onSelectedCurrency = { currency ->
                if (dialogSource == RateDialogSource.SELL) {
                    updateSellingCurrency(currency)
                } else {
                    updateBuyingCurrency(currency)
                }
                updateSellingAmount("")
                updateBuyingAmount()
            },
            errorMessage = uiState.exchangeRateError
        )
    }

    if (openSubmitDialog) {
        ExchangeMessageDialog(
            onDismissRequest = { openSubmitDialog = false },
            exchangeAlert = stringResource(
                R.string.exchange_message,
                amount.toDouble(),
                sellingCurrency,
                uiState.buyingAmount,
                buyingCurrency,
                uiState.commissionFee,
                sellingCurrency
            ),
            updateAmountToReceive = {
                updateBuyingAmount()
            },
            onCurrencyChange = {
                updateSellingAmount(it)
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
        color = Color.Black,
        fontSize = 16.sp,
        fontWeight = FontWeight(600),
    )
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
            // ErrorScreen(onDismissRequest = {}, errorMessage = "")
            //SubmitDialog(onDismissRequest = {}) { }
//            AccountBalanceItem(
//                amount = "100.00",
//                currency = "EUR"
//            )
            ExchangeScreen(
                uiState = MainScreenState(
                    ExchangeRate(
                        mapOf(
                            "USD" to 1.0,
                            "GBP" to 0.85
                        )
                    )
                ),
                sellingCurrency = "EUR",
                updateSellingCurrency = {},
                buyingCurrency = "USD",
                updateBuyingCurrency = {},
                accountBalances = listOf(
                    AccountBalance(currency = "EUR", balance = 100.0),
                    AccountBalance(currency = "USD", balance = 50.0),
                    AccountBalance(currency = "USD", balance = 50.00),
                    AccountBalance(currency = "USD", balance = 80000.0),
                    AccountBalance(currency = "USD", balance = 50.0)
                ),
                amountValidation = "Amount is too lowjjjjj",
                amount = "100.00",
                onAmountChange = {},
                updateSellingAmount = {},
                updateBuyingAmount = {},
                submitExchange = {})
        }
    }
}