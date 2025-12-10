package com.hp.iaiain.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.hp.iaiain.design.system.AccentOrange
import com.hp.iaiain.design.system.ButtonSize
import com.hp.iaiain.design.system.CornerRadius
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.TextWhite

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonSize.height),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = AccentOrange,
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        if (isLoading) {
            LoadingIndicator(size = Spacing.md, color = Color.Black)
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonSize.height),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = TextWhite,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        shape = RoundedCornerShape(CornerRadius.md)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun LoadingIndicator(
    size: androidx.compose.ui.unit.Dp = Spacing.lg,
    color: Color = AccentOrange
) {
    Box(
        modifier = Modifier
            .padding(Spacing.sm)
            .background(color = Color.Transparent)
    ) {
        Text(
            text = "⊙",
            fontSize = androidx.compose.ui.unit.TextUnit(size.value, androidx.compose.ui.unit.TextUnitType.Sp),
            color = color,
            textAlign = TextAlign.Center
        )
    }
}

