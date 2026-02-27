package com.example.stamptourapp.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.stamptourapp.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
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
    Place("food_1", "란타이", MapCategory.FOOD, "대구 수성구 대흥동 831", "맛집 더미 설명"),
    Place("exp_1", "아진홀", MapCategory.EXPERIENCE, "대구 수성구 대흥동 858-4", "체험 더미 설명"),
    Place("event_1", "알파시티공원", MapCategory.EVENT, "대구 수성구 대흥동 855-5", "이벤트 더미 설명"),
    Place("cafe_1", "커피", MapCategory.CAFE, "대구 수성구 대흥동 848", "카페 더미 설명"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val context = LocalContext.current

    var selectedCategory by rememberSaveable { mutableStateOf(MapCategory.ALL) }
    var hasLocationPermission by remember { mutableStateOf(context.hasAnyLocationPermission()) }

    var myLatLng by remember { mutableStateOf<LatLng?>(null) }
    var selectedPlace by remember { mutableStateOf<Place?>(null) }

    val placeLatLngMap = remember { mutableStateMapOf<String, LatLng>() }

    val fallback = remember { LatLng(35.8714, 128.6014) } // 대구
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallback, 14f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        hasLocationPermission = granted
    }

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

    @SuppressLint("MissingPermission")
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            myLatLng = null
            return@LaunchedEffect
        }

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
            if (loc != null) myLatLng = LatLng(loc.latitude, loc.longitude)
        }
    }

    LaunchedEffect(myLatLng) {
        val here = myLatLng ?: return@LaunchedEffect
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(here, 16f),
            durationMs = 700
        )
    }

    LaunchedEffect(Unit) {
        samplePlaces.forEach { place ->
            if (placeLatLngMap.containsKey(place.id)) return@forEach
            val latLng = geocodeToLatLng(context, place.address)
            if (latLng != null) placeLatLngMap[place.id] = latLng
        }
    }

    val filteredPlaces = remember(selectedCategory) {
        when (selectedCategory) {
            MapCategory.ALL -> samplePlaces
            else -> samplePlaces.filter { it.category == selectedCategory }
        }
    }

    selectedPlace?.let { place ->
        ModalBottomSheet(onDismissRequest = { selectedPlace = null }) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    place.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text("카테고리: ${place.category.label}")
                Text("주소: ${place.address}")
                Spacer(Modifier.height(10.dp))
                Text(place.description)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("상세 보기(임시)") }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
            context = context,
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
                label = {
                    Text(
                        text = if (selected == category) "✓ ${category.label}" else category.label,
                        color = if (selected == category) Color.White else Color(0xFF1E3A8A)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (selected == category) Color(0xFF2563EB) else Color(0xFFEAF3FF),
                    labelColor = if (selected == category) Color.White else Color(0xFF1E3A8A)
                )
            )
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun GoogleMapBox(
    context: Context,
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    hasLocationPermission: Boolean,
    myLatLng: LatLng?,
    places: List<Place>,
    placeLatLngMap: Map<String, LatLng>,
    onPlaceClick: (Place) -> Unit
) {
    // ✅ 스타일 로드 실패해도 앱 죽지 않게
    val mapStyle: MapStyleOptions? = remember {
        runCatching {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.sky)
        }.getOrNull()
    }

    val properties = remember(hasLocationPermission, mapStyle) {
        MapProperties(
            isMyLocationEnabled = hasLocationPermission,
            mapStyleOptions = mapStyle
        )
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
        myLatLng?.let { here ->
            Marker(
                state = MarkerState(position = here),
                title = "현재 위치"
            )
        }

        places.forEach { place ->
            val latLng = placeLatLngMap[place.id] ?: return@forEach
            Marker(
                state = MarkerState(position = latLng),
                title = place.name,
                snippet = place.category.label,

                // ✅ 마커 탭하면 기본 InfoWindow가 뜨게만 함
                onClick = {
                    false
                },

                // ✅ 말풍선(이름) 탭했을 때 바텀시트 열기
                onInfoWindowClick = {
                    onPlaceClick(place)
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