package com.example.stamptourapp.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

private enum class MapCategory(val label: String) {
    ALL("전체"),
    FOOD("맛집"),
    EXPERIENCE("체험"),
    EVENT("이벤트"),
    CAFE("카페"),
}

private data class Place(
    val id: String,
    val name: String,
    val category: MapCategory,
    val address: String,
    val description: String,
)

private val samplePlaces = listOf(
    Place(
        id = "food_1",
        name = "란타이",
        category = MapCategory.FOOD,
        address = "대구 수성구 대흥동 831",
        description = "맛집 더미 설명"
    ),
    Place(
        id = "exp_1",
        name = "아진홀",
        category = MapCategory.EXPERIENCE,
        address = "대구 수성구 대흥동 858-4",
        description = "체험 더미 설명"
    ),
    Place(
        id = "event_1",
        name = "알파시티공원",
        category = MapCategory.EVENT,
        address = "대구 수성구 대흥동 855-5",
        description = "이벤트 더미 설명"
    ),
    Place(
        id = "cafe_1",
        name = "커피",
        category = MapCategory.CAFE,
        address = "대구 수성구 대흥동 848",
        description = "카페 더미 설명"
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    var selectedCategory by rememberSaveable { mutableStateOf(MapCategory.ALL) }
    var hasLocationPermission by remember { mutableStateOf(context.hasAnyLocationPermission()) }

    // 현재 위치 좌표
    var myLatLng by remember { mutableStateOf<LatLng?>(null) }

    // 마커 클릭 시 선택된 장소 (바텀시트용)
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    // 주소 -> 좌표 캐시 (id -> LatLng)
    val placeLatLngMap = remember { mutableStateMapOf<String, LatLng>() }

    // 기본 카메라 위치(권한 전/위치 못 가져올 때)
    val fallback = remember { LatLng(35.8714, 128.6014) } // 대구
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallback, 14f)
    }

    // 권한 요청(복수)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        hasLocationPermission = granted
    }

    // 첫 진입 시 권한 없으면 요청
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // 권한 상태 바뀌면 마지막 위치 가져오기
    @SuppressLint("MissingPermission")
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            myLatLng = null
            return@LaunchedEffect
        }

        // 실제 권한 재확인
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            myLatLng = null
            return@LaunchedEffect
        }

        val fused = LocationServices.getFusedLocationProviderClient(context)
        fused.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                myLatLng = LatLng(loc.latitude, loc.longitude)
            }
        }
    }

    // 위치가 잡히면 카메라 이동
    LaunchedEffect(myLatLng) {
        val here = myLatLng ?: return@LaunchedEffect
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(here, 16f),
            durationMs = 700
        )
    }

    // 더미 주소들을 좌표로 변환해서 마커 찍기 (한 번만/없으면만)
    LaunchedEffect(Unit) {
        samplePlaces.forEach { place ->
            if (placeLatLngMap.containsKey(place.id)) return@forEach
            val latLng = geocodeToLatLng(context, place.address)
            if (latLng != null) {
                placeLatLngMap[place.id] = latLng
            }
        }
    }

    // 선택된 카테고리에 따라 마커 대상 필터링
    val filteredPlaces = remember(selectedCategory) {
        when (selectedCategory) {
            MapCategory.ALL -> samplePlaces
            else -> samplePlaces.filter { it.category == selectedCategory }
        }
    }

    // 바텀시트
    selectedPlace?.let { place ->
        ModalBottomSheet(
            onDismissRequest = { selectedPlace = null }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(place.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("카테고리: ${place.category.label}")
                Text("주소: ${place.address}")
                Spacer(Modifier.height(10.dp))
                Text(place.description)
                Spacer(Modifier.height(16.dp))

                // 나중에 여기서 NavController로 상세 페이지 이동 연결하면 됨
                // Button(onClick = { navController.navigate("place/${place.id}") }) { Text("상세 보기") }

                Button(
                    onClick = { /* TODO: 상세페이지 이동 연결 */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("상세 보기(임시)")
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        Text(
            text = "지도",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        CategoryBar(
            selected = selectedCategory,
            onSelect = { selectedCategory = it }
        )

        Spacer(Modifier.height(12.dp))

        GoogleMapBox(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = cameraPositionState,
            hasLocationPermission = hasLocationPermission,
            myLatLng = myLatLng,
            places = filteredPlaces,
            placeLatLngMap = placeLatLngMap,
            onPlaceClick = { selectedPlace = it }
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun CategoryBar(
    selected: MapCategory,
    onSelect: (MapCategory) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MapCategory.entries.forEach { category ->
            AssistChip(
                onClick = { onSelect(category) },
                label = { Text(if (selected == category) "✓ ${category.label}" else category.label) }
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun GoogleMapBox(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    myLatLng: LatLng?,
    places: List<Place>,
    placeLatLngMap: Map<String, LatLng>,
    onPlaceClick: (Place) -> Unit
) {
    val properties = remember(hasLocationPermission) {
        MapProperties(isMyLocationEnabled = hasLocationPermission)
    }

    val uiSettings = remember {
        MapUiSettings(
            myLocationButtonEnabled = true,
            zoomControlsEnabled = false
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        // 현재 위치 마커
        myLatLng?.let { here ->
            Marker(
                state = MarkerState(position = here),
                title = "현재 위치"
            )
        }

        // 장소 마커들 (필터된 것만)
        places.forEach { place ->
            val latLng = placeLatLngMap[place.id] ?: return@forEach
            Marker(
                state = MarkerState(position = latLng),
                title = place.name,
                snippet = place.category.label,
                onClick = {
                    onPlaceClick(place)
                    true // 기본 인포윈도우 동작 막고 우리가 처리
                }
            )
        }
    }
}

private fun Context.hasAnyLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarse = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fine || coarse
}

/**
 * 주소 문자열 -> LatLng 변환 (실패하면 null)
 * ※ 일부 기기/환경에서 네트워크 필요할 수 있음
 */
private suspend fun geocodeToLatLng(context: Context, address: String): LatLng? {
    return withContext(Dispatchers.IO) {
        runCatching {
            val geocoder = Geocoder(context, Locale.KOREA)
            @Suppress("DEPRECATION")
            val results = geocoder.getFromLocationName(address, 1)
            val first = results?.firstOrNull() ?: return@runCatching null
            LatLng(first.latitude, first.longitude)
        }.getOrNull()
    }
}
