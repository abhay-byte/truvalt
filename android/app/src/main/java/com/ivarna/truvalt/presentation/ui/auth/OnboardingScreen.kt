package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

private val OnboardingSurface = Color(0xFFFCF8FE)
private val OnboardingPrimary = Color(0xFF534AB7)
private val OnboardingOnSurface = Color(0xFF33313A)
private val OnboardingOnSurfaceVariant = Color(0xFF605E68)
private val OnboardingSurfaceVariant = Color(0xFFE5E1ED)
private val OnboardingOnPrimary = Color(0xFFFCF7FF)
private val OnboardingOutlineVariant = Color(0xFFB4B0BC)
private val OnboardingSurfaceContainerLow = Color(0xFFF6F2FA)
private val OnboardingSurfaceContainer = Color(0xFFF0ECF6)
private val OnboardingSurfaceContainerHigh = Color(0xFFEBE6F1)
private val OnboardingSurfaceContainerLowest = Color(0xFFFFFFFF)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = OnboardingSurface,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text(
                        text = "Skip",
                        color = OnboardingPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnboardingSlide1()
                    1 -> OnboardingSlide2()
                    2 -> OnboardingSlide3()
                }
            }

            // Bottom Controls Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, top = 24.dp)
                    .padding(horizontal = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Page Indicator Dots
                    Row(
                        modifier = Modifier
                            .height(10.dp)
                            .padding(bottom = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { iteration ->
                            val color = if (pagerState.currentPage == iteration) OnboardingPrimary else OnboardingSurfaceVariant
                            val width = if (pagerState.currentPage == iteration) 32.dp else 10.dp
                            
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .width(width)
                                    .height(10.dp)
                                    .animateContentSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Primary Action Button
                    Button(
                        onClick = {
                            if (pagerState.currentPage < 2) {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            } else {
                                onComplete()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .shadow(8.dp, CircleShape, spotColor = OnboardingPrimary.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.buttonColors(containerColor = OnboardingPrimary),
                        shape = CircleShape
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (pagerState.currentPage == 2) "Get Started" else "Next",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnboardingOnPrimary
                            )
                            if (pagerState.currentPage < 2) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = OnboardingOnPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingSlide1() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Illustration Space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Background Glow
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .blur(60.dp)
                    .alpha(0.1f)
                    .background(OnboardingPrimary, CircleShape)
            )
            
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAwQKcV-QmMlH-EvVsv08eginISDSIqbgJxri4JGT-BlP6BVDctZfQa9Bnr71k5NLTP2gTlT_o5Ka5ksRX0gbZJIx5f4h_QbDj5DPmLvIyvH3MFjaErCx16xNgeXIFHaIpTNLSIjEJIo2NO4yLYTLhmGAqSdSfx8DlsovJX4xBzkR55B6fbNFu6uVKpimw2irsvAib5AIG4rUj9nE4mdvdMdIl_ooENdjaCk0cXb7R7qZ3ShMRuHyZFWn6YMTHnOb-2JSlGZyJ7QrU",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
        }

        // Copy
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Text(
                text = "Your data, only yours",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardingOnSurface,
                letterSpacing = (-1).sp,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Everything is encrypted on your device before it ever leaves.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = OnboardingOnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun OnboardingSlide2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Illustration Space: Recreate the browser/phone sync logic
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Glow
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .blur(80.dp)
                    .alpha(0.05f)
                    .background(OnboardingPrimary, CircleShape)
            )

            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                
                // Connection line (Dashed)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width * 0.3f, size.height * 0.65f)
                        quadraticBezierTo(
                            size.width * 0.5f, size.height * 0.5f,
                            size.width * 0.7f, size.height * 0.35f
                        )
                    }
                    drawPath(
                        path = path,
                        color = OnboardingPrimary.copy(alpha = 0.2f),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                        )
                    )
                }

                // Browser Window (Right side)
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 0.dp, y = 16.dp)
                        .size(width = 192.dp, height = 128.dp),
                    colors = CardDefaults.cardColors(containerColor = OnboardingSurfaceContainerLowest),
                    border = BorderStroke(1.dp, OnboardingOutlineVariant.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(OnboardingSurfaceContainer)
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(3) { i ->
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(
                                            when(i) {
                                                0 -> Color(0xFFA8364B).copy(0.3f)
                                                1 -> Color(0xFF765377).copy(0.3f)
                                                else -> OnboardingPrimary.copy(0.3f)
                                            },
                                            CircleShape
                                        )
                                )
                            }
                        }
                        // Content
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.width(80.dp).height(8.dp).background(OnboardingOutlineVariant.copy(0.1f), RoundedCornerShape(2.dp)))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeat(3) {
                                    Box(modifier = Modifier.weight(1f).height(32.dp).background(OnboardingSurfaceContainerHigh, RoundedCornerShape(4.dp)))
                                }
                            }
                        }
                    }
                }

                // Mobile Phone (Left side)
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = 16.dp, y = 16.dp)
                        .size(width = 96.dp, height = 176.dp),
                    colors = CardDefaults.cardColors(containerColor = OnboardingSurfaceContainerLowest),
                    border = BorderStroke(1.dp, OnboardingOutlineVariant.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(20.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(modifier = Modifier.width(24.dp).height(4.dp).background(OnboardingOutlineVariant.copy(0.2f), CircleShape))
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(OnboardingSurfaceContainerHigh, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth(0.75f).height(8.dp).background(OnboardingSurfaceContainerHigh, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(OnboardingPrimary.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = OnboardingPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.width(32.dp).height(4.dp).background(OnboardingOutlineVariant.copy(0.2f), CircleShape))
                    }
                }
            }
        }

        // Copy
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Text(
                text = "One vault, every device",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardingOnSurface,
                letterSpacing = (-1).sp,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Access your passwords from Android or web. Always in sync.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = OnboardingOnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun OnboardingSlide3() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Illustration Space: Tonal circles and card
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(288.dp)
                    .alpha(0.6f)
                    .background(OnboardingSurfaceContainerLow, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(224.dp)
                    .alpha(0.4f)
                    .background(OnboardingSurfaceContainer, CircleShape)
            )
            
            Card(
                modifier = Modifier
                    .size(256.dp)
                    .shadow(elevation = 48.dp, shape = RoundedCornerShape(12.dp), spotColor = Color(0x1433313A)),
                colors = CardDefaults.cardColors(containerColor = OnboardingSurfaceContainerLowest),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = OnboardingPrimary,
                            modifier = Modifier.size(100.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            repeat(3) {
                                Icon(Icons.Filled.Verified, contentDescription = null, tint = OnboardingPrimary, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }

        // Copy
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 64.dp)
        ) {
            Text(
                text = "Built for security",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardingOnSurface,
                letterSpacing = (-1).sp,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Biometrics, 2FA, and breach alerts keep you protected.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = OnboardingOnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
