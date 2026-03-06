package com.example.stamptourapp.feature.coupons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Coupon(
    val id: String,
    val title: String,
    val description: String,
    val isUsed: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponScreen(
    onBack: () -> Unit
) {
    // 임시 데이터 (나중에 API로 교체)
    val couponList = listOf(
        Coupon("1", "아메리카노 1잔 무료", "카페 디지넛", false),
        Coupon("2", "5,000원 할인 쿠폰", "지역 상점 제휴", false),
        Coupon("3", "베이커리 10% 할인", "사용 완료", true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("보유 쿠폰") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (couponList.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("보유한 쿠폰이 없습니다.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(couponList) { coupon ->
                    CouponCard(coupon = coupon)
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun CouponCard(
    coupon: Coupon
) {
    val backgroundColor = if (coupon.isUsed) Color(0xFFE0E0E0) else Color(0xFFEDF4FF)
    val titleColor = if (coupon.isUsed) Color.Gray else Color(0xFF1E1E1E)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = null,
                tint = if (coupon.isUsed) Color.Gray else Color(0xFF2C2FD6),
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coupon.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = titleColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = coupon.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (coupon.isUsed) Color.Gray else Color.DarkGray
                )
            }

            if (coupon.isUsed) {
                Text(
                    text = "사용완료",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}