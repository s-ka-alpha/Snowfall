package com.idapgroup.snowfall

import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.core.content.getSystemService
import com.idapgroup.snowfall.Constants.snowflakeDensity
import com.idapgroup.snowfall.types.AnimType
import com.idapgroup.snowfall.types.FlakeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Creates flakes falling animation base on `FlakeType` param.
 * @param type type of flake.
 * @param colors list of Colors for Flakes
 * @param density density of Flakes must be between 0.0 (no Flakes) or 1.0 (a lot of Flakes). Default is 0.05
 */
public fun Modifier.snowfall(
    type: FlakeType = FlakeType.Snowflakes,
    colors: List<Color>,
    density: Double = snowflakeDensity,
): Modifier =
    letItSnow(type, AnimType.Falling, colors = colors, density)

/**
 * Creates flakes falling animation base on `FlakeType` param.
 * @param type type of flake.
 * @param color single Color for Flakes
 * @param density density of Flakes must be between 0.0 (no Flakes) or 1.0 (a lot of Flakes). Default is 0.05
 */
public fun Modifier.snowfall(
    type: FlakeType = FlakeType.Snowflakes,
    color: Color = Color.White,
    density: Double = snowflakeDensity,
): Modifier =
    letItSnow(type, AnimType.Falling, colors = listOf(color), density)

/**
 * Creates flakes melting animation base on `FlakeType` param.
 * @param type - type of flake.
 * @param colors - list of Colors for Flakes
 * @param density - density of Flakes must be between 0.0 (no Flakes) or 1.0 (a lot of Flakes). Default is 0.05
 *
 */
public fun Modifier.snowmelt(
    type: FlakeType = FlakeType.Snowflakes,
    colors: List<Color>,
    density: Double = snowflakeDensity
): Modifier =
    letItSnow(type, AnimType.Melting, colors = colors, density = density)

/**
 * Creates flakes melting animation base on `FlakeType` param.
 * @param type - type of flake.
 * @param color - single Color for Flakes
 * @param density - density of Flakes must be between 0.0 (no Flakes) or 1.0 (a lot of Flakes). Default is 0.05
 *
 */
public fun Modifier.snowmelt(
    type: FlakeType = FlakeType.Snowflakes,
    color: Color = Color.White,
    density: Double = snowflakeDensity
): Modifier =
    letItSnow(type, AnimType.Melting, colors = listOf(color), density = density)


private fun Modifier.letItSnow(
    flakeType: FlakeType,
    animType: AnimType,
    colors: List<Color>,
    density: Double
) = composed {
    val flakes = when (flakeType) {
        is FlakeType.Custom -> flakeType.data
        is FlakeType.Snowflakes -> getDefaultFlakes()
    }
    var snowAnimState by remember {
        mutableStateOf(
            SnowAnimState(
                tick = -1,
                canvasSize = IntSize(0, 0),
                painters = flakes,
                animType = animType,
                colors = colors,
                density = density
            )
        )
    }
    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos { frameTimeNanos ->
                val elapsedMillis =
                    (frameTimeNanos - snowAnimState.tickNanos).nanoseconds.inWholeMilliseconds
                val isFirstRun = snowAnimState.tickNanos < 0
                snowAnimState.tickNanos = frameTimeNanos

                if (isFirstRun) return@withFrameNanos 0
                snowAnimState.snowflakes.forEach {
                    it.update(elapsedMillis)
                }
                return@withFrameNanos frameTimeNanos
            }
            delay(8L)
        }
    }
    onSizeChanged { newSize -> snowAnimState = snowAnimState.resize(newSize) }
        .clipToBounds()
        .drawWithContent {
            drawContent()
            snowAnimState.draw(this)
        }
}

@Composable
private fun getDefaultFlakes(): List<Painter> = listOf(
    painterResource(id = R.drawable.ic_flake_1),
    painterResource(id = R.drawable.ic_flake_2),
    painterResource(id = R.drawable.ic_flake_3),
    painterResource(id = R.drawable.ic_flake_4),
    painterResource(id = R.drawable.ic_flake_5),
    painterResource(id = R.drawable.ic_flake_6),
    painterResource(id = R.drawable.ic_flake_7),
    painterResource(id = R.drawable.ic_flake_8),
    painterResource(id = R.drawable.ic_flake_9),
    painterResource(id = R.drawable.ic_flake_10),
)