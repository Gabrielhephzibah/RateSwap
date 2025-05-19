package com.example.rateswap.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rateswap.ui.theme.RateSwapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RateSwapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
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
                        value = "",
                        onValueChange = {},
                        label = { Text("Enter amount to sell") },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Gray
                       ),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GBP",
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
                      text = "100.00",
                  )
                   Row(
                       verticalAlignment = Alignment.CenterVertically
                   ) {
                       Text(
                           text = "GBP",
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
                    text = "EUR"
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
                    text = "GBP"
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
            Greeting()
        }
    }
}