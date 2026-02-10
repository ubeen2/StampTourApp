package com.example.stamptourapp.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stamptourapp.R
import kotlinx.coroutines.delay

// 카드 공통 아이템
private data class SlideCardItem(
    @field:DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String
)

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

        // 1) 메인 이벤트 배너 (3초,텍스트X)
        MainEventBannerSlider(
            imageResList = listOf(
                R.drawable.main,
                R.drawable.main2,
                R.drawable.main3
            ),
            onClick = { /* TODO: 메인 이벤트 상세 */ }
        )

        Spacer(Modifier.height(18.dp))

        // 2) 오늘의 프로그램
        val programItems = listOf(
            SlideCardItem(R.drawable.cafe, "카페", "#카페 #쿠폰사용"),
            SlideCardItem(R.drawable.vr, "VR", "#vr #게임"),
            SlideCardItem(R.drawable.food, "맛집", "#맛집 #쿠폰사용"),
        )

        SectionHeader(
            title = "오늘의 프로그램:)",
            actionText = "보러가기",
            onAction = onGoProgramAll
        )
        Spacer(Modifier.height(10.dp))
        HorizontalCardSlider(
            items = programItems,
            cardHeight = 140.dp,
            cardWidth = 240.dp,
            onItemClick = { /* TODO: 프로그램 카드 클릭 */ }
        )

        Spacer(Modifier.height(22.dp))

        // 3) 하이라이트 이벤트
        val eventItems = listOf(
            SlideCardItem(R.drawable.vr, "VR 이벤트", "#vr #게임"),
            SlideCardItem(R.drawable.cafe, "카페 이벤트", "#카페 #쿠폰사용"),
            SlideCardItem(R.drawable.food, "맛집 이벤트", "#맛집 #쿠폰사용"),
        )

        SectionHeader(
            title = "하이라이트 이벤트><",
            actionText = "보러가기",
            onAction = onGoEventAll
        )
        Spacer(Modifier.height(10.dp))
        HorizontalCardSlider(
            items = eventItems,
            cardHeight = 140.dp,
            cardWidth = 240.dp,
            onItemClick = { /* TODO: 이벤트 카드 클릭 */ }
        )

        Spacer(Modifier.height(22.dp))

        // 4) 스탬프 진행률 섹션
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
// 1) 메인 이벤트 배너
// ---------------------------
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainEventBannerSlider(
    @DrawableRes imageResList: List<Int>,
    intervalMillis: Long = 3000L,
    onClick: () -> Unit
) {
    // imageResList.size가 0일 가능성 방어
    if (imageResList.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { imageResList.size }
    )

    // 2초마다 자동 슬라이드
    LaunchedEffect(imageResList) {
        while (true) {
            delay(intervalMillis)
            val next = (pagerState.currentPage + 1) % imageResList.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = imageResList[page]),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // (선택) 인디케이터 점
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(imageResList.size) { index ->
                val selected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(if (selected) 8.dp else 6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (selected) Color.White
                            else Color.White.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

// ---------------------------
// 공통: 섹션 헤더
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
            text = "$actionText >", // = actionText + " >",
            fontSize = 12.sp,
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { onAction() }
        )
    }
}

// ---------------------------
// 2/3) 슬라이드(가로)
// ---------------------------
@Composable
private fun HorizontalCardSlider(
    items: List<SlideCardItem>,
    cardHeight: Dp,
    cardWidth: Dp,
    onItemClick: (SlideCardItem) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            SlideCard(
                item = item,
                width = cardWidth,
                height = cardHeight,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun SlideCard(
    item: SlideCardItem,
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
        // 이미지
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE5E7EB))
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        //  "#카페 #쿠폰사용"
        Text(
            text = item.subtitle,
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

        // 간단 진입 바
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
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF121212).copy(alpha = 0.65f)
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "나의 스탬프 보러가기",
                color = Color.White,
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

        // 아이콘 자리
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE5E7EB))
        )
    }
}
