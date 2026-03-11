package com.risyan.quickshutdownphone.screen_content_guard.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.risyan.quickshutdownphone.R
import com.risyan.quickshutdownphone.base.ui.theme.Body2Style
import com.risyan.quickshutdownphone.base.ui.theme.Header2Style
import com.risyan.quickshutdownphone.screen_content_guard.navigator.EDU_SCREEN
import com.risyan.quickshutdownphone.screen_content_guard.navigator.OnboardNavigator

fun NavGraphBuilder.EduScreen() {
    composable(route = EDU_SCREEN) {
        Content()
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
fun Content(
    modifier: Modifier = Modifier,
){
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ){

            ContentBlock(
                headerText = stringResource(R.string.instant_lock_keeps_you_from_provoking_content),
                bodyText = stringResource(R.string.this_app_is_designed_to_prevent),
                imageResId = R.drawable.neuron_buster
            )

            ContentBlock(
                headerText = stringResource(R.string.instant_lock_will_lock_your_phone_instantly),
                bodyText = stringResource(R.string.you_wont_be_able_to_use_your_phone),
                imageResId = R.drawable.lockdown_purpose_illustration
            )
        }
        Button(
            onClick = {
                OnboardNavigator.Current?.pop()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(text = stringResource(R.string.back))
        }
    }

}

@Composable
fun ContentBlock(
    headerText: String,
    bodyText: String,
    imageResId: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = headerText,
            style = Header2Style
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = bodyText,
            style = Body2Style
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            painter = painterResource(id = imageResId),
            contentDescription = "purpose"
        )
    }
}