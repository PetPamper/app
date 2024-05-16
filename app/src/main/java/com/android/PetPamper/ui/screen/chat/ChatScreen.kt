package com.android.PetPamper.ui.screen.chat

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.WindowCompat

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Preview(showSystemUi = true)
@Composable
fun ChatScreenPreview() {
  ChatzTheme {
    ChatScreen(
        model =
            ChatUiModel(
                messages =
                    listOf(
                        ChatUiModel.Message(
                            "Hi Tree, How you doing?", ChatUiModel.Author("0", "Branch")),
                        ChatUiModel.Message(
                            "Hi Branch, good. You?", ChatUiModel.Author("-1", "Tree"))),
                addressee = ChatUiModel.Author("0", "Branch")),
        onSendChatClickListener = {},
        modifier = Modifier)
  }
}

@Composable
fun ChatScreen(model: ChatUiModel, onSendChatClickListener: (String) -> Unit, modifier: Modifier) {
  ConstraintLayout(modifier = modifier.fillMaxSize()) {
    val (messages, chatBox) = createRefs()

    val listState = rememberLazyListState()
    LaunchedEffect(model.messages.size) { listState.animateScrollToItem(model.messages.size) }
    LazyColumn(
        state = listState,
        modifier =
            Modifier.fillMaxWidth().constrainAs(messages) {
              top.linkTo(parent.top)
              bottom.linkTo(chatBox.top)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
              height = Dimension.fillToConstraints
            },
        contentPadding = PaddingValues(16.dp)) {
          items(model.messages) { item -> ChatItem(item) }
        }
    ChatBox(
        onSendChatClickListener,
        modifier =
            Modifier.fillMaxWidth().constrainAs(chatBox) {
              bottom.linkTo(parent.bottom)
              start.linkTo(parent.start)
              end.linkTo(parent.end)
            })
  }
}

@Composable
fun ChatItem(message: ChatUiModel.Message) {
  Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
    Box(
        modifier =
            Modifier.align(if (message.isFromMe) Alignment.End else Alignment.Start)
                .clip(
                    RoundedCornerShape(
                        topStart = 48f,
                        topEnd = 48f,
                        bottomStart = if (message.isFromMe) 48f else 0f,
                        bottomEnd = if (message.isFromMe) 0f else 48f))
                .background(PurpleGrey80)
                .padding(16.dp)) {
          Text(text = message.text)
        }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBox(onSendChatClickListener: (String) -> Unit, modifier: Modifier) {
  var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
  Row(modifier = modifier.padding(16.dp)) {
    TextField(
        value = chatBoxValue,
        onValueChange = { newText -> chatBoxValue = newText },
        modifier = Modifier.weight(1f).padding(4.dp),
        shape = RoundedCornerShape(24.dp),
        colors =
            TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent),
        placeholder = { Text(text = "Type something") })
    IconButton(
        onClick = {
          val msg = chatBoxValue.text
          if (msg.isBlank()) return@IconButton
          onSendChatClickListener(chatBoxValue.text)
          chatBoxValue = TextFieldValue("")
        },
        modifier =
            Modifier.clip(CircleShape)
                .background(color = PurpleGrey80)
                .align(Alignment.CenterVertically)) {
          Icon(
              imageVector = Icons.Filled.Send,
              contentDescription = "Send",
              modifier = Modifier.fillMaxSize().padding(8.dp))
        }
  }
}

private val DarkColorScheme =
    darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
    lightColorScheme(
        primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40

        /* Other default colors to override
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
        */
        )

@Composable
fun ChatzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
  val colorScheme =
      when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
          val context = LocalContext.current
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
      }
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = colorScheme.primary.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
    }
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

// Set of Material typography styles to start with
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp))

data class ChatUiModel(
    val messages: List<Message>,
    val addressee: Author,
) {
  data class Message(
      val text: String,
      val author: Author,
  ) {
    val isFromMe: Boolean
      get() = author.id == MY_ID

    companion object {
      val initConv = Message(text = "Hi there, how you doing?", author = Author.bot)
    }
  }

  data class Author(val id: String, val name: String) {
    companion object {
      val bot = Author("1", "Bot")
      val me = Author(MY_ID, "Me")
    }
  }

  companion object {
    const val MY_ID = "-1"
  }
}
