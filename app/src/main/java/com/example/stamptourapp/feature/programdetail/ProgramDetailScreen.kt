package com.example.stamptourapp.feature.programdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stamptourapp.R

/**
 * ProgramDetailScreen.kt
 * - HomeScreen에서 넘긴 programId로 상세 정보를 찾아 화면에 표시
 *
 * AppNavGraph에서 연결 예시:
 * composable(Routes.PROGRAM_DETAIL) { backStackEntry ->
 *   val programId = backStackEntry.arguments?.getString("programId") ?: ""
 *   ProgramDetailScreen(programId = programId, onBack = { navController.popBackStack() })
 * }
 */
@Composable
fun ProgramDetailScreen(
    programId: String,
    onBack: () -> Unit
) {
    // 나중에 서버/DB/ViewModel로 바꾸기ㅣ
    val programs = listOf(
        // 오늘의 프로그램
        ProgramDetailUi(
            id = "cafe",
            imageRes = R.drawable.cafe,
            title = "카페",
            subtitle = "#카페 #쿠폰사용",
            description = "달콤한 디저트와 커피를 즐길 수 있는 프로그램이에요.",
            location = "1층 A구역",
            time = "10:00 ~ 18:00"
        ),
        ProgramDetailUi(
            id = "vr",
            imageRes = R.drawable.vr,
            title = "VR",
            subtitle = "#vr #게임",
            description = "몰입감 있는 VR 체험과 미션을 즐겨보세요!",
            location = "2층 체험존",
            time = "11:00 ~ 19:00"
        ),
        ProgramDetailUi(
            id = "food",
            imageRes = R.drawable.food,
            title = "맛집",
            subtitle = "#맛집 #쿠폰사용",
            description = "인기 메뉴를 쿠폰으로 할인받아 즐길 수 있어요.",
            location = "야외 푸드존",
            time = "10:30 ~ 20:00"
        ),

        // 하이라이트 이벤트
        ProgramDetailUi(
            id = "event_vr",
            imageRes = R.drawable.vr,
            title = "VR 이벤트",
            subtitle = "#vr #게임",
            description = "하이라이트 VR 이벤트! 한정 미션/보상에 참여해보세요.",
            location = "2층 이벤트존",
            time = "13:00 ~ 17:00"
        ),
        ProgramDetailUi(
            id = "event_cafe",
            imageRes = R.drawable.cafe,
            title = "카페 이벤트",
            subtitle = "#카페 #쿠폰사용",
            description = "카페 이벤트 기간에 쿠폰 사용 시 추가 혜택이 있어요.",
            location = "1층 카페 부스",
            time = "12:00 ~ 16:00"
        ),
        ProgramDetailUi(
            id = "event_food",
            imageRes = R.drawable.food,
            title = "맛집 이벤트",
            subtitle = "#맛집 #쿠폰사용",
            description = "맛집 이벤트! 지정 메뉴 할인/스탬프 추가 적립을 진행해요.",
            location = "야외 푸드 이벤트존",
            time = "14:00 ~ 18:00"
        )
    )

    val item = programs.find { it.id == programId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 상단 바 (뒤로가기)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "< 뒤로",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB),
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "프로그램 상세",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Spacer(Modifier.weight(1f))
            // 오른쪽 균형 맞추기용
            Spacer(Modifier.width(40.dp))
        }

        Spacer(Modifier.height(14.dp))

        if (item == null) {
            Text(
                text = "프로그램 정보를 찾을 수 없어요: $programId",
                color = Color(0xFF0F172A),
                fontSize = 14.sp
            )
            return
        }

        // 메인 이미지
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(18.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = item.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = item.subtitle,
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(Modifier.height(14.dp))

        InfoCard(title = "설명", body = item.description)
        Spacer(Modifier.height(10.dp))
        InfoCard(title = "위치", body = item.location)
        Spacer(Modifier.height(10.dp))
        InfoCard(title = "운영시간", body = item.time)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: 쿠폰/예약/지도 등 */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111827)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "길찾기",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

private data class ProgramDetailUi(
    val id: String,
    val imageRes: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val location: String,
    val time: String
)

@Composable
private fun InfoCard(
    title: String,
    body: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            color = Color(0xFF334155)
        )
    }
}