package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLinkTopBar(
    userRole: UserRole,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF090014),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo Icon Badge
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    color = Color.White
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_homelink_logo),
                        contentDescription = "HomeLink Logo Badge",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "HOME LINK",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                // User Role Badge
                val (roleLabel, roleColor, roleIcon) = when (userRole) {
                    UserRole.ADMIN -> Triple("ADMINISTRATOR", Color(0xFFFFD54F), Icons.Default.AdminPanelSettings)
                    UserRole.TECHNICIAN -> Triple("TECHNICIAN", Color(0xFF00E676), Icons.Default.Engineering)
                    else -> Triple("GUEST", Color.LightGray, Icons.Default.Engineering)
                }

                Row(
                    modifier = Modifier
                        .background(
                            color = roleColor.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, roleColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = roleLabel,
                        tint = roleColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = roleLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = roleColor
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onLogoutClick,
                modifier = Modifier.testTag("logout_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout Portal",
                    tint = Color.White
                )
            }
        }
    )
}
