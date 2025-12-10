package com.hp.iaiain.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.hp.iaiain.design.system.CornerRadius
import com.hp.iaiain.design.system.InputSize
import com.hp.iaiain.design.system.Spacing
import com.hp.iaiain.design.system.SurfaceLight
import com.hp.iaiain.design.system.TextDarkGray
import com.hp.iaiain.design.system.TextGray

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextGray,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(InputSize.height)
                .background(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(CornerRadius.md)
                )
                .border(
                    width = if (error != null) Spacing.xs else Spacing.xs,
                    color = if (error != null) Color.Red else SurfaceLight,
                    shape = RoundedCornerShape(CornerRadius.md)
                )
                .padding(Spacing.lg),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDarkGray
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

@Composable
fun TextAreaField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextGray,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(InputSize.minHeight)
                .background(
                    color = SurfaceLight,
                    shape = RoundedCornerShape(CornerRadius.md)
                )
                .border(
                    width = if (error != null) Spacing.xs else Spacing.xs,
                    color = if (error != null) Color.Red else SurfaceLight,
                    shape = RoundedCornerShape(CornerRadius.md)
                )
                .padding(Spacing.lg),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDarkGray
                        )
                    }
                    innerTextField()
                }
            }
        )

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}

