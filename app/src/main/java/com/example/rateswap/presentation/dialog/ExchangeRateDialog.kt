package com.example.rateswap.presentation.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val filteredRates = rateResponse.rates
        .filter { it.key.contains(searchQuery.trim(), ignoreCase = true) }

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
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,

            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 20.dp, start = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(40.dp)

                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onDismissRequest() },
                        tint = Color.Black
                    )

                    Text(
                        text = stringResource(R.string.select_currency),
                        modifier = Modifier
                            .fillMaxWidth(),
                        fontSize = 14.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                )

                LazyColumn {
                    items(count = filteredRates.size){
                        val item = filteredRates.entries.elementAt(it)
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
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Box(modifier = Modifier
        .fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier
                .align(Alignment.Center).fillMaxWidth().padding(horizontal = 16.dp),
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.search_currency),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,

            )
        )
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
