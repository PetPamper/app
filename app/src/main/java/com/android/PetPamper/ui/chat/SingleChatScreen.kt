package com.android.PetPamper.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.PetPamper.CAViewModel
import com.android.PetPamper.CommonImage
import com.android.PetPamper.data.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChatScreen(navController: NavController, vm: CAViewModel, chatId: String) {
  LaunchedEffect(key1 = Unit) { vm.populateChat(chatId) }
  BackHandler { vm.depopulateChat() }

  val currentChat = vm.chats.value.first { it.chatId == chatId }
  val myUser = vm.userData.value
  val chatUser =
      if (myUser?.userId == currentChat.user1.userId) currentChat.user2 else currentChat.user1
  var reply by rememberSaveable { mutableStateOf("") }
  val onSendReply = {
    vm.onSendReply(chatId, reply)
    reply = ""
  }
  val chatMessages = vm.chatMessages

  Scaffold(
      topBar = {
        TopAppBar(
            title = { Text(chatUser.name ?: "") },
            navigationIcon = {
              IconButton(
                  onClick = {
                    navController.popBackStack()
                    vm.depopulateChat()
                  }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                  }
            },
            actions = {
              CommonImage(
                  data = chatUser.imageUrl ?: "", modifier = Modifier.size(40.dp).clip(CircleShape))
            })
      },
      bottomBar = {
        ReplyBox(reply = reply, onReplyChange = { reply = it }, onSendReply = onSendReply)
      }) { paddingValues ->
        Messages(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            chatMessages = chatMessages.value,
            currentUserId = myUser?.userId ?: "")
      }
}

@Composable
fun Messages(modifier: Modifier, chatMessages: List<Message>, currentUserId: String) {
  val sortedMessages = chatMessages.sortedByDescending { it.timestamp }

  LazyColumn(modifier = modifier, reverseLayout = true, contentPadding = PaddingValues(16.dp)) {
    items(sortedMessages) { msg ->
      val isCurrentUser = msg.sentBy == currentUserId
      val backgroundColor = if (isCurrentUser) Color(0xFF3F51B5) else Color(0xFFF5F5F5)
      val textColor = if (isCurrentUser) Color.White else Color.Black

      Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
          horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start) {
            Text(
                text = msg.message ?: "",
                modifier =
                    Modifier.background(color = backgroundColor, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                color = textColor,
                style = TextStyle(fontSize = 16.sp))
          }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply: String, onReplyChange: (String) -> Unit, onSendReply: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = reply,
            onValueChange = onReplyChange,
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            placeholder = { Text("Type your message...") },
            colors =
                TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color(0xFFBDBDBD)),
            shape = RoundedCornerShape(16.dp))
        IconButton(onClick = onSendReply, modifier = Modifier.size(48.dp)) {
          Icon(
              imageVector = Icons.Rounded.Send,
              contentDescription = "Send",
              tint = Color(0xFF3F51B5))
        }
      }
}
