package com.risyan.quickshutdownphone.screen_content_guard.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.risyan.quickshutdownphone.R


@Composable
fun MenuGrid(
    onHowTo : () -> Unit,
    onNoLearn : () -> Unit
){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Spacer(Modifier)

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.clickable {
                onNoLearn()
            }
        ){
            Image(
                painterResource(id = R.drawable.no_learn),
                contentDescription = "app_icon",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier)

        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.clickable {
                onHowTo()
            }
        ){
            Image(
                painterResource(id = R.drawable.how_to),
                contentDescription = "app_icon",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(Modifier)
    }
}