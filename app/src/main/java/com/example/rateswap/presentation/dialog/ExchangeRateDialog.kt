package com.example.rateswap.presentation.dialog

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rateswap.R
import com.example.rateswap.domain.model.ExchangeRate


@Composable
fun ExchangeRateDialog(
    onDismissRequest: () -> Unit,
    rateResponse: ExchangeRate,
    onSelectedCurrency: (String) -> Unit,
    errorMessage:String) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        if (rateResponse.rates.isEmpty() && errorMessage.isNotBlank()) {
            ErrorScreen(onDismissRequest = onDismissRequest, errorMessage = errorMessage)
            return@Dialog
        }
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
                    text = stringResource(R.string.select_currency),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Black
                )

                LazyColumn {
                    items(rateResponse.rates.size){
                        val item = rateResponse.rates.entries.elementAt(it)
                        ExchangeRateItem(item.key, item.value){ currency ->
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
fun ExchangeRateItem(currency: String, rate: Double, onSelectedItem: (String) -> Unit) {
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
fun ErrorScreen(
    onDismissRequest: () -> Unit,
    errorMessage: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)

    ) {
        Text(
            text = errorMessage,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(15.dp))
        TextButton(
            onClick = {
                onDismissRequest()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.ok),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}