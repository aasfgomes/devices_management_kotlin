package com.computacaomovel.devicemanagement.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.computacaomovel.devicemanagement.R

@Composable
fun Ecra01() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra01),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

@Composable
fun Ecra02() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra02),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

@Composable
fun Ecra03() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra03),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

@Composable
fun Ecra04() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra04),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

@Composable
fun Ecra05() {
    Column(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
        Text(text = stringResource(id = R.string.ecra05),
            fontWeight = FontWeight.Bold, color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center, fontSize = 18.sp
        )
    }
}

