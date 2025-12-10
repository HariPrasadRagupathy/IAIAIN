package com.hp.iaiain.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.hp.iaiain.design.system.CornerRadius
import com.hp.iaiain.design.system.IconSize
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.SurfaceLight
import com.hp.iaiain.design.system.TextGray

@Composable
fun Card(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceLight,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(Spacing.lg)
    ) {
        content()
    }
}

@Composable
fun FeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceLight,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.lg)
                .background(
                    color = Color(0xFF1B3A6B),
                    shape = RoundedCornerShape(CornerRadius.md)
                ),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            modifier = Modifier.padding(top = Spacing.md)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            modifier = Modifier.padding(top = Spacing.xs)
        )
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = SurfaceLight,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = TextGray
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(top = Spacing.sm)
        )
    }
}

