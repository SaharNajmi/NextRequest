package com.example.nextrequest.home.presentation.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sahar.nextrequest.R

@Composable
fun TextVisibilityTextField(value: String, onTextChange: (String) -> Unit) {
    val state = remember { TextFieldState(value) }
    var showText by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        snapshotFlow { state.text }
            .collect {
                Log.e("DDD", it.toString())
                onTextChange(it.toString())
            }
    }
    BasicSecureTextField(
        state = state,
        textObfuscationMode =
            if (showText) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.RevealLastTyped
            },
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
            .padding(6.dp),
        decorator = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    innerTextField()
                }
                Icon(
                    if (showText)
                        painterResource(R.drawable.visibility_off)
                    else
                        painterResource(R.drawable.visibility),
                    contentDescription = "Toggle text visibility",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { showText = !showText }
                )
            }
        }
    )
}
