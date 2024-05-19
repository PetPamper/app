package com.android.PetPamper.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.android.PetPamper.CAViewModel
import com.android.PetPamper.CommonProgressSpinner
import com.android.PetPamper.CommonRow
import com.android.PetPamper.navigateTo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersScreen(navController: NavController, vm: CAViewModel, email: String) {
    val inProgress = vm.inProgressChats.value
    if (inProgress)
        CommonProgressSpinner()
    else {
        val users = vm.users.value
        val userData = vm.userData.value

        val showDialog = remember { mutableStateOf(false) }
        val onFabClick: () -> Unit = { showDialog.value = true }
        val onDismiss: () -> Unit = { showDialog.value = false }
        val onAddChat: (String) -> Unit = {
            //vm.onAddChat(it, email)
            showDialog.value = false
        }

        Scaffold(

            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(brush = Brush.verticalGradient(listOf(Color(0xFFECEFF1), Color(0xFFE0E0E0)))),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Users",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F51B5)
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    if (users.isEmpty()) {
                        Text(
                            text = "No users available",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 16.sp,
                                color = Color(0xFF757575)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .wrapContentHeight(Alignment.CenterVertically)
                                .padding(horizontal = 16.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                CommonRow(
                                    imageUrl ="",
                                    name = user.name,
                                    onItemClick = {
                                        user.phoneNumber?.let { phoneNumber ->
                                            vm.onAddChat(phoneNumber, email)
//                                            navigateTo(
//                                                navController, "ChatListScreen"
//                                            )
                                            navController.navigateUp()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}
