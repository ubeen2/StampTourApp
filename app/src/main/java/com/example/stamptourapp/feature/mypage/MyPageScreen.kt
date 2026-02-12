package com.example.stamptourapp.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.RowScope


@Composable
fun MyPageScreen(
    // ✅ 스탬프 카드 클릭 시: Stampbook으로 이동 연결용
    onGoStampbook: () -> Unit,
    // ✅ 아직 상세페이지가 없어서 일단 Unit으로 두는 클릭들
    onGoCoupons: () -> Unit = {},
    onGoActivity: () -> Unit = {},
    onGoSettings: () -> Unit = {},
    onGoEditProfile: () -> Unit = {},
    onGoStoreRegister: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ---------------------------
        // 상단 헤더 영역 (연두 톤)
        // ---------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB6FF8B),
                            Color(0xFF8DFF6A)
                        )
                    )
                )
        ) {
            // 프로필 + 이름/이메일
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 36.dp, end = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF35A100),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "홍길동님",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E1E1E)
                    )
                    Text(
                        text = "testlive2026@email.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2B2B2B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // ---------------------------
        // 상단 카드(프로필/스탬프/쿠폰) 영역
        // ---------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-28).dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "획득 스탬프",
                        value = "3",
                        isPrimary = true,
                        onClick = onGoStampbook
                    )
                    StatCard(
                        title = "보유 쿠폰",
                        value = "2",
                        isPrimary = false,
                        onClick = onGoCoupons
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 메뉴 리스트 (클릭은 되지만 아직 상세 없음 -> Unit)
                MenuRow(
                    iconTint = Color(0xFFFF6B6B),
                    icon = Icons.Filled.History,
                    title = "활동이력",
                    onClick = onGoActivity
                )
                Divider()
                MenuRow(
                    iconTint = Color(0xFF4D7CFE),
                    icon = Icons.Filled.Settings,
                    title = "설정",
                    onClick = onGoSettings
                )
                Divider()
                MenuRow(
                    iconTint = Color(0xFF15B7A3),
                    icon = Icons.Filled.Edit,
                    title = "개인정보 수정",
                    onClick = onGoEditProfile
                )
                Divider()
                MenuRow(
                    iconTint = Color(0xFFFFB000),
                    icon = Icons.Filled.Storefront,
                    title = "상점 등록",
                    onClick = onGoStoreRegister
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 로그아웃 버튼
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("로그아웃")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "디지넛 비즈페일 앱 v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8A8A8A),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // 카드가 위로 떠있어서 아래 여백 정리
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun RowScope.StatCard(
    title: String,
    value: String,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isPrimary) Color(0xFF2C2FD6) else Color(0xFFF3F4F6)
    val titleColor = if (isPrimary) Color.White else Color(0xFF2B2B2B)
    val valueColor = if (isPrimary) Color.White else Color(0xFF2B2B2B)

    Card(
        modifier = Modifier
            .weight(1f)
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = titleColor
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = valueColor
            )
        }
    }
}

@Composable
private fun MenuRow(
    iconTint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.weight(1f),
            color = Color(0xFF1E1E1E)
        )

        Icon(
            imageVector = Icons.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFFB0B0B0),
            modifier = Modifier.size(14.dp)
        )
    }
}
