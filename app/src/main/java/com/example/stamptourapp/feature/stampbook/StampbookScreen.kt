package com.example.stamptourapp.feature.stampbook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private data class StampSpot(
    val id: String,
    val title: String,
    val subtitle: String,
    val isCollected: Boolean,
)

@Composable
fun StampbookScreen(
    onGoMap: () -> Unit,                 // ✅ 지도 이동은 콜백으로
    onCouponClick: () -> Unit = {}        // 아직 쿠폰함 없으니 기본 Unit
) {
    val stamps = remember {
        listOf(
            StampSpot("meta", "메타버스 라운지", "획득 완료", true),
            StampSpot("vr", "VR 스튜디오", "획득 완료", true),
            StampSpot("gallery", "디지털 갤러리", "획득 완료", true),

            StampSpot("stage", "버추얼 스테이지", "미획득", false),
            StampSpot("nft", "NFT 마켓플레이스", "미획득", false),
            StampSpot("cafe", "메타 카페", "미획득", false),
            StampSpot("arcade", "게임 아케이드", "미획득", false),
            StampSpot("creator", "크리에이터 스튜디오", "미획득", false),
            StampSpot("cinema", "디지털 시네마", "미획득", false),
        )
    }

    val collected = stamps.filter { it.isCollected }
    val remaining = stamps.filter { !it.isCollected }

    val total = stamps.size.coerceAtLeast(1)
    val progress = collected.size.toFloat() / total.toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 12.dp)
    ) {
        item {
            Text(
                text = "스탬프 컬렉션",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "축제 장소를 방문하고 스탬프를 모아보세요",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            ProgressCard(
                progress = progress,
                collectedCount = collected.size,
                totalCount = total
            )
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            CouponCard(onClick = onCouponClick)
        }

        item { Spacer(Modifier.height(18.dp)) }

        item {
            SectionTitle(text = "획득한 스탬프")
            Spacer(Modifier.height(10.dp))

            StampGrid(
                items = collected,
                onItemClick = null
            )
        }

        item { Spacer(Modifier.height(18.dp)) }

        item {
            SectionTitle(text = "남은 스탬프")
            Spacer(Modifier.height(10.dp))

            // ✅ 미획득 클릭 시에만 지도 이동
            StampGrid(
                items = remaining,
                onItemClick = onGoMap
            )
        }
    }
}

@Composable
private fun ProgressCard(
    progress: Float,
    collectedCount: Int,
    totalCount: Int
) {
    val pct = (progress * 100).toInt().coerceIn(0, 100)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "전체 진행률",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "계속해서 스탬프를 모아보세요!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                }
                Text(
                    text = "${pct}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(999.dp))
            )

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "$collectedCount/$totalCount 획득",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
                Text(
                    text = "${totalCount - collectedCount}개 남음",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun CouponCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = "쿠폰 받기",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "스탬프로 쿠폰을 교환해보세요",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                )
            }

            Icon(
                imageVector = Icons.Default.ReceiptLong,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun StampGrid(
    items: List<StampSpot>,
    onItemClick: (() -> Unit)?
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 520.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = false
    ) {
        items(items) { spot ->
            StampCard(
                spot = spot,
                onClick = if (!spot.isCollected) onItemClick else null
            )
        }
    }
}

@Composable
private fun StampCard(
    spot: StampSpot,
    onClick: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(Modifier.fillMaxSize().padding(12.dp)) {
            Icon(
                imageVector = if (spot.isCollected) Icons.Default.CheckCircle else Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (spot.isCollected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )

            if (spot.isCollected) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "✓",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = spot.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = spot.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
