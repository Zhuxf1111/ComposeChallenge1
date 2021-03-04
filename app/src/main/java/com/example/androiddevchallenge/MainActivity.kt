/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.shapes
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                var totalTime: Long by rememberSaveable { mutableStateOf(60000) }
                var remainTime: Long by rememberSaveable { mutableStateOf(60000) }
                var countDownTimer: CountDownTimer? by rememberSaveable { mutableStateOf(null)}
                Surface(color = MaterialTheme.colors.background) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        ClockCompose(modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp), totalTime, remainTime)
                        Button(onClick = {
                            if (countDownTimer == null){
                                countDownTimer = object : CountDownTimer(remainTime, 100){
                                    override fun onTick(millisUntilFinished: Long) {
                                        remainTime = millisUntilFinished
                                    }

                                    override fun onFinish() {
                                        remainTime = 0
                                        countDownTimer = null
                                    }
                                }
                                countDownTimer?.start()
                            } else {
                                countDownTimer?.cancel()
                                countDownTimer = null
                            }
                        },modifier = Modifier
                            .width(100.dp)
                            .wrapContentHeight()){
                            Text(text = if (countDownTimer == null && totalTime == remainTime) {"Start"}
                            else if (countDownTimer == null && totalTime != remainTime){"Resume"}
                            else {"Stop"})
                        }
                        Button(onClick = {
                            if (countDownTimer != null) {
                                countDownTimer?.cancel()
                            }
                            countDownTimer = null
                            totalTime = 60000
                            remainTime = 60000
                        }, modifier = Modifier
                            .width(100.dp)
                            .wrapContentHeight()
                            .padding(top = 12.dp)){
                            Text(text = "Reset")
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun ClockOutline(modifier: Modifier, fraction: Float){
    val brush = Brush.horizontalGradient(colorStops = arrayOf(Pair(0f, Color.Red), Pair(1f, Color.Green)))
    val path = Path()
    Canvas(modifier = modifier,
        onDraw = {
        path.reset()
        path.moveTo(size.width / 2, size.height / 2)
            val offset = if (size.width > size.height ){
                Offset((size.width - size.height) / 2 + 10.dp.value, 10.dp.value)
            } else {
                Offset(10.dp.value, (size.height - size.width) / 2  + 10.dp.value)
            }
        path.addArc(Rect(offset, Size(minOf(size.width, size.height) - 20.dp.value, minOf(size.width, size.height) - 20.dp.value)),
            -90f , -360f * fraction)
        drawCircle(color = Color.Gray, alpha = 0.5f, radius = minOf(size.width, size.height) / 2 - 10.dp.value, style = Stroke(width = 10.dp.value))
        drawPath(path = path, brush = brush, style = Stroke(10.dp.value))
    })
}

@Composable
fun ClockCompose(modifier: Modifier, totalTime: Long, remainTime: Long){
    Box(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        contentAlignment = Alignment.Center) {
        ClockOutline(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f, false)
            .clip(shape = GenericShape { size: Size, layoutDirection: LayoutDirection ->
                if (size.width > size.height) {
                    val offset = (size.width - size.height) / 2f
                    addRect(Rect(offset, 0f, size.width - offset, size.height))
                } else {
                    val offset = (size.height - size.width) / 2f
                    addRect(Rect(0f, offset, size.width, size.height - offset))
                }
            }), fraction = remainTime * 1f / totalTime)
        ClockTimeLabel(modifier = modifier.wrapContentSize(align = Alignment.Center), remainTime)
    }
}

private val df = DecimalFormat("00")

@Composable
fun ClockTimeLabel(modifier: Modifier, time: Long){
    val fontSize = 48.sp
    val hour = time / TimeUnit.HOURS.toMillis(1)
    val min = time  / TimeUnit.MINUTES.toMillis(1) % 60
    val second = time % TimeUnit.MINUTES.toMillis(1) / 1000
    Row(modifier) {
        Text(text = "${df.format(hour)}", fontSize = fontSize, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = ":", fontSize = fontSize, modifier = Modifier.padding(horizontal = 10.dp), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = "${df.format(min)}", fontSize = fontSize, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = ":", fontSize = fontSize, modifier = Modifier.padding(horizontal = 10.dp), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Text(text = "${df.format(second)}", fontSize = fontSize, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
