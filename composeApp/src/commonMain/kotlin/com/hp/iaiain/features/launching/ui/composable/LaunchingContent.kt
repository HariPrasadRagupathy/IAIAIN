package com.hp.iaiain.features.launching.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import iaiain.composeapp.generated.resources.Res
import iaiain.composeapp.generated.resources.logo
import com.hp.iaiain.design.components.FeatureCard
import com.hp.iaiain.design.system.AccentGreen
import com.hp.iaiain.design.system.AccentOrange
import com.hp.iaiain.design.system.BackgroundMedium
import com.hp.iaiain.design.system.CornerRadius
import com.hp.iaiain.design.system.IconSize
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.SurfaceLight
import com.hp.iaiain.design.system.TextGray
import com.hp.iaiain.design.system.TextWhite
import iaiain.composeapp.generated.resources.campushubicon
import iaiain.composeapp.generated.resources.glocalhubicon
import iaiain.composeapp.generated.resources.juniorhubIcon

@Composable
fun LaunchingHeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo and Brand Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = Spacing.xxl)
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.xl)
                    .background(
                        color = BackgroundMedium,
                        shape = RoundedCornerShape(CornerRadius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.logo),
                    contentDescription = "IAIAIN Logo",
                    modifier = Modifier.size(28.sp.value.dp)
                )
            }

            Spacer(modifier = Modifier.size(Spacing.md))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "IAIAIN",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    ),
                    color = TextWhite
                )
                Text(
                    text = "INCUBATION NETWORK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        letterSpacing = 1.sp
                    ),
                    color = TextGray
                )
            }
        }

        // Title with inline styling
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = TextWhite, fontWeight = FontWeight.Normal)) {
                    append("Something ")
                }
                withStyle(style = SpanStyle(color = AccentOrange, fontWeight = FontWeight.Bold)) {
                    append("Amazing")
                }
            },
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 36.sp,
                lineHeight = 42.sp
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "is Coming Soon",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 36.sp,
                fontWeight = FontWeight.Normal
            ),
            color = TextWhite,
            textAlign = TextAlign.Center
        )

        // Subtitle
        Text(
            text = "We're building the future of academic innovation and incubation across India. Get ready to be part of a revolutionary network connecting students, colleges, and industry experts.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            color = TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.lg)
        )
    }
}

@Composable
fun FeaturesSection() {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(Spacing.lg, Alignment.CenterHorizontally)
        ) {
            FeatureCard(
                //modifier = Modifier.weight(1f),
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.juniorhubIcon),
                        contentDescription = "IAIAIN Logo",
                        modifier = Modifier.size(28.sp.value.dp)
                    )
                },
                title = "Junior Hub",
                description = "Nurturing young minds in schools with STEM programs"
            )

            FeatureCard(
                //modifier = Modifier.weight(1f),
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.campushubicon),
                        contentDescription = "IAIAIN Logo",
                        modifier = Modifier.size(28.sp.value.dp)
                    )
                },
                title = "Campus Hub",
                description = "College incubation centers for real-world innovation"
            )

            FeatureCard(
                //modifier = Modifier.weight(1f),
                icon = {
                    Image(
                        painter = painterResource(Res.drawable.glocalhubicon),
                        contentDescription = "IAIAIN Logo",
                        modifier = Modifier.size(28.sp.value.dp)
                    )
                },
                title = "Global Network",
                description = "Connecting with international innovation ecosystems"
            )
        }

}

@Composable
fun CountdownSection(
    days: Int,
    hours: Int,
    minutes: Int,
    seconds: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Launch Countdown",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            modifier = Modifier.padding(bottom = Spacing.lg)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxxl, Alignment.CenterHorizontally)
        ) {
            CountdownItem(value = days, label = "Days")
            CountdownItem(value = hours, label = "Hours")
            CountdownItem(value = minutes, label = "Minutes")
            CountdownItem(value = seconds, label = "Seconds")
        }
    }
}

@Composable
fun CountdownItem(value: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = SurfaceLight,
                shape = RoundedCornerShape(CornerRadius.lg)
            ).width(80.dp)
            .padding(Spacing.md)
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.headlineSmall,
            color = AccentOrange,
            textAlign = TextAlign.Center
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray,
            modifier = Modifier.padding(top = Spacing.xs)
        )
    }
}
