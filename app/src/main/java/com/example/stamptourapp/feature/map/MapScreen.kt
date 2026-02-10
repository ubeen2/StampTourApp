package com.example.stamptourapp.feature.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

private enum class MapCategory(val label: String) {
    ALL("전체"),
    FOOD("맛집"),
    EXPERIENCE("체험"),
    EVENT("이벤트"),
    CAFE("카페"),
}

@Composable
fun MapScreen() {
    val context = LocalContext.current

    var selectedCategory by rememberSaveable { mutableStateOf(MapCategory.ALL) }
    var hasLocationPermission by remember { mutableStateOf(context.hasAnyLocationPermission()) }

    // ✅ 현재 위치 좌표
    var myLatLng by remember { mutableStateOf<LatLng?>(null) }

    // ✅ 기본 카메라 위치(권한 전/위치 못 가져올 때)
    val fallback = remember { LatLng(35.8714, 128.6014) } // 대구
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(fallback, 14f)
    }

    // ✅ 권한 요청(복수)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) ||
                (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        hasLocationPermission = granted
    }

    // ✅ 첫 진입 시 권한 없으면 요청
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

    // ✅ 권한 상태 바뀌면 마지막 위치 가져오기
    @SuppressLint("MissingPermission")
    LaunchedEffect(hasLocationPermission) {
        if (!hasLocationPermission) {
            myLatLng = null
            return@LaunchedEffect
        }

        // 🔒 Lint/실행 모두 안전하게: 실제 권한 재확인
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
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

    // ✅ 위치가 잡히면 카메라 이동
    LaunchedEffect(myLatLng) {
        val here = myLatLng ?: return@LaunchedEffect
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(here, 16f),
            durationMs = 700
        )
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
            myLatLng = myLatLng
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
    myLatLng: LatLng?
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
        // ✅ 현재 위치 마커
        myLatLng?.let { here ->
            Marker(
                state = MarkerState(position = here),
                title = "현재 위치"
            )
        }
    }
}

private fun Context.hasAnyLocationPermission(): Boolean {
    val fine = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarse = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fine || coarse
}
