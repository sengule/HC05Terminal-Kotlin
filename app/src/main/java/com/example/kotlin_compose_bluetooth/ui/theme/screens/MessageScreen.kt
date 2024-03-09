@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.kotlin_compose_bluetooth.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_compose_bluetooth.SERVER_NAME
import com.example.kotlin_compose_bluetooth.bluetooth.AppBluetoothController
import com.example.kotlin_compose_bluetooth.model.MessageModel
import com.example.kotlin_compose_bluetooth.testMessages
import com.example.kotlin_compose_bluetooth.ui.theme.KotlinComposeBluetoothTheme
import com.example.kotlin_compose_bluetooth.viewmodels.BluetoothViewModel


@Composable
fun MessageScreen(
    messageList: List<MessageModel> = testMessages,
    onBackButtonClicked: () -> Unit = {},
    onSendMessage: (String) -> Unit = {}
){
    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val message = rememberSaveable {
            mutableStateOf("")
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.padding(10.dp).weight(1f),
                text = "Messages"
            )

            IconButton(onClick = onBackButtonClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back scan screen"
                )
            }
        }

        MessageList(messageList = messageList, modifier = Modifier.fillMaxSize().weight(1f))

        SendMessageRow(message = message){
            onSendMessage(message.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMessageRow(
    message: MutableState<String>,
    onSendMessage: () -> Unit = {}
){

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        TextField(
            value = message.value,
            onValueChange = {message.value = it},
            modifier = Modifier.weight(1f),
            placeholder = { Text(text = "Enter Message")},
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        IconButton(
            onClick = onSendMessage
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send icon"
            )
        }
    }
}

@Composable
fun MessageList(
    messageList: List<MessageModel> = testMessages,
    modifier: Modifier
){
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        items(messageList){
            Text(
                color = if (it.sender == SERVER_NAME) Color.Blue else Color.Magenta,
                text = "${it.sender}:  ${it.message}"
            )
        }

    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MessageScreenPreview() {
    KotlinComposeBluetoothTheme {
        MessageScreen()
    }
}