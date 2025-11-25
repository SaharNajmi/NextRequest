package com.example.postman.core.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.postman.core.presentation.icons.Arrow_back_ios

@Composable
fun CustomToolbar(title: String, navController: NavController) {
    Box(Modifier.fillMaxWidth()) {
        IconButton(onClick = {
            navController.popBackStack()
        }) {
            Icon(
                imageVector = Arrow_back_ios,
                contentDescription = "backToHome",
                modifier = Modifier
                    .padding(start = 4.dp),
            )
        }
        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}