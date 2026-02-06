package com.example.stamptourapp.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp

@Composable
fun HomeScreen(
    onGoProgramAll: () -> Unit,
    onGoEventAll: () -> Unit,
    onGoStampList: () -> Unit,
    onGoMap: () -> Unit,
    onGoCoupons: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(14.dp))

        // 1) 메인 이벤트 배너 (단일) 자동으로 넘기
        MainEventBanner(
            title = "메인 이벤트",
            subtitle = "놓치면 아쉬운 메인 이벤트를 확인하세요",
            onClick = { /* TODO: 메인 이벤트 상세 */ }
        )

        Spacer(Modifier.height(18.dp))

        // 2) 오늘의 프로그램 섹션 (슬라이드 3개 + 전체보기)
        SectionHeader(
            title = "오늘의 프로그램",
            actionText = "전체 둘러보기",
            onAction = onGoProgramAll
        )
        Spacer(Modifier.height(10.dp))
        HorizontalCardSlider(
            itemsCount = 3,
            cardHeight = 140.dp,
            cardWidth = 240.dp,
            labelPrefix = "프로그램"
        )

        Spacer(Modifier.height(22.dp))

        // 3) 하이라이트 이벤트 섹션 (슬라이드 3개 + 전체보기)
        SectionHeader(
            title = "하이라이트 이벤트",
            actionText = "전체 둘러보기",
            onAction = onGoEventAll
        )
        Spacer(Modifier.height(10.dp))
        HorizontalCardSlider(
            itemsCount = 3,
            cardHeight = 140.dp,
            cardWidth = 240.dp,
            labelPrefix = "이벤트"
        )

        Spacer(Modifier.height(22.dp))

        // 4) 스탬프 진행률 섹션 (퍼센트 + 목록보기 버튼)
        StampProgressCard(
            percent = 30,
            done = 3,
            total = 10,
            onGoStampList = onGoStampList
        )

        Spacer(Modifier.height(22.dp))

        // 5) 빠른 보기 섹션 (지도 / 쿠폰함)
        Text(
            text = "빠른 보기",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(Modifier.height(10.dp))
        QuickActionRow(
            onGoMap = onGoMap,
            onGoCoupons = onGoCoupons
        )

        Spacer(Modifier.height(24.dp))
    }
}

// ---------------------------
// 1) 메인 이벤트 배너 (1개)
// ---------------------------
@Composable
private fun MainEventBanner(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF3B82F6), Color(0xFF6366F1))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
        }
    }
}

// ---------------------------
// 공통: 섹션 헤더 (제목 + 전체보기)
// ---------------------------
@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = actionText + " >",
            fontSize = 12.sp,
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { onAction() }
        )
    }
}

// ---------------------------
// 2/3) 슬라이드(가로 스크롤) 카드 3개
// ---------------------------
@Composable
private fun HorizontalCardSlider(
    itemsCount: Int,
    cardHeight: Dp,
    cardWidth: Dp,
    labelPrefix: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemsCount) { idx ->
            PlaceholderSlideCard(
                title = "$labelPrefix ${idx + 1}",
                subtitle = "기간/장소 등 간단 설명",
                width = cardWidth,
                height = cardHeight,
                onClick = { /* TODO: 카드 클릭 */ }
            )
        }
    }
}

@Composable
private fun PlaceholderSlideCard(
    title: String,
    subtitle: String,
    width: Dp,
    height: Dp,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        // 이미지 자리(플레이스홀더)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE5E7EB))
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = subtitle,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

// ---------------------------
// 4) 스탬프 진행률 카드
// ---------------------------
@Composable
private fun StampProgressCard(
    percent: Int,
    done: Int,
    total: Int,
    onGoStampList: () -> Unit
) {
    val bg = Brush.linearGradient(listOf(Color(0xFFD9F99D), Color(0xFFBBF7D0)))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "나의 스탬프 진행률",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "${percent}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = "$done / $total",
            fontSize = 12.sp,
            color = Color(0xFF334155)
        )

        Spacer(Modifier.height(12.dp))

        // 간단 진행바 (박스 2개로 구현)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.White.copy(alpha = 0.7f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((percent.coerceIn(0, 100) / 100f))
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color(0xFF1D4ED8))
            )
        }

        Spacer(Modifier.height(14.dp))

        Button(
            onClick = onGoStampList,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.65f)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "스탬프 목록 보기",
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------------
// 5) 빠른보기 (지도 / 쿠폰함)
// ---------------------------
@Composable
private fun QuickActionRow(
    onGoMap: () -> Unit,
    onGoCoupons: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "지도보기",
            subtitle = "부스/행사장 위치",
            modifier = Modifier.weight(1f),
            onClick = onGoMap
        )
        QuickActionCard(
            title = "쿠폰함",
            subtitle = "받은 쿠폰 확인",
            modifier = Modifier.weight(1f),
            onClick = onGoCoupons
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        // 아이콘 자리(플레이스홀더)
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE5E7EB))
        )
    }
}

