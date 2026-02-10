package com.example.stamptourapp.core.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.stamptourapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onLogoClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLogoClick() }
            ) {
                //  로고 자리: drawable 로고로 바꿔도 됨
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "logo",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Filled.Person, contentDescription = "profile")
            }
        }
    )
}
