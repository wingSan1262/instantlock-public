package com.risyan.quickshutdownphone.screen_content_guard.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.ui.theme.DateStyle
import com.risyan.quickshutdownphone.base.ui.theme.Header3Style
import com.risyan.quickshutdownphone.base.ui.theme.HeaderStyle
import com.risyan.quickshutdownphone.base.currentDatetoFormattedString
import com.risyan.quickshutdownphone.screen_content_guard.navigator.OnboardNavigator
import com.risyan.quickshutdownphone.screen_content_guard.navigator.WELCOME_SCREEN
import com.risyan.quickshutdownphone.screen_content_guard.widget.MenuGrid


fun NavGraphBuilder.WelcomeScreen() {
    composable(route = WELCOME_SCREEN) {
        WelcomScreenContent()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun WelcomScreenContent(){
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ){
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            Image(
                painterResource(id = R.mipmap.ic_launcher_monochrome),
                contentDescription = "app_icon",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = Header3Style
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
//        ImageBitmap.imageResource(id = R.mipmap.ic_launcher_round)
        Text(
            text = currentDatetoFormattedString(),
            style = DateStyle
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.welcome_back_great_people),
            style = HeaderStyle
        )

        Spacer(modifier = Modifier.height(32.dp))


        MenuGrid(onHowTo = {
            OnboardNavigator.Current?.navigateToEdu()
        }) {
            OnboardNavigator.Current?.navigateToSetup()
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.please_choose_menu_above),
            style = DateStyle.copy(
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
