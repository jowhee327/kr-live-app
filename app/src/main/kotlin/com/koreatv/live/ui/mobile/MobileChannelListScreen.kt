package com.koreatv.live.ui.mobile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.koreatv.live.data.model.Channel
import com.koreatv.live.ui.player.PlayerViewModel
import com.koreatv.live.ui.theme.AccentCyan
import com.koreatv.live.ui.theme.AccentPurple
import com.koreatv.live.ui.theme.CardBorder
import com.koreatv.live.ui.theme.FavoriteGold
import com.koreatv.live.ui.theme.TextSecondary
import com.koreatv.live.ui.theme.TextTertiary

@Composable
fun MobileChannelListScreen(
    viewModel: PlayerViewModel,
    onOpenSettings: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val currentChannel by viewModel.currentChannel.collectAsState()

    val channels = viewModel.filteredChannels()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "韩流直播",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row {
                    IconButton(onClick = { viewModel.toggleFavoritesFilter() }) {
                        Text(
                            text = if (showFavoritesOnly) "★" else "☆",
                            fontSize = 24.sp,
                            color = if (showFavoritesOnly) FavoriteGold else TextSecondary
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Text(
                            text = "⚙",
                            fontSize = 22.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Category chips
            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) },
                            label = {
                                Text(
                                    "全部",
                                    fontSize = 14.sp,
                                    color = if (selectedCategory == null) Color.White else TextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple.copy(alpha = 0.3f),
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = CardBorder,
                                selectedBorderColor = AccentPurple,
                                enabled = true,
                                selected = selectedCategory == null
                            )
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    category,
                                    fontSize = 14.sp,
                                    color = if (selectedCategory == category) Color.White else TextSecondary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple.copy(alpha = 0.3f),
                                containerColor = Color(0xFF1A1A1A)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = CardBorder,
                                selectedBorderColor = AccentPurple,
                                enabled = true,
                                selected = selectedCategory == category
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Content
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPurple)
                }
            } else if (channels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无频道", color = TextTertiary, fontSize = 16.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(channels, key = { it.id }) { channel ->
                        ChannelCard(
                            channel = channel,
                            isFavorite = favorites.contains(channel.id),
                            isPlaying = currentChannel?.id == channel.id,
                            onPlay = { viewModel.playChannel(channel) },
                            onToggleFavorite = { viewModel.toggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }

        // Top gradient mask for scroll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun ChannelCard(
    channel: Channel,
    isFavorite: Boolean,
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1.0f,
        animationSpec = spring(stiffness = 800f, dampingRatio = 0.6f),
        label = "cardScale"
    )

    val glowBorderModifier = if (isPlaying) {
        Modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = AccentPurple.copy(alpha = 0.6f),
                spotColor = AccentPurple.copy(alpha = 0.6f)
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(AccentPurple, AccentCyan)),
                shape = RoundedCornerShape(16.dp)
            )
    } else {
        Modifier.border(
            width = 1.dp,
            color = CardBorder,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(glowBorderModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onPlay
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPlaying) 8.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logo.isNotEmpty()) {
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier.size(64.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = channel.name.take(2),
                        color = AccentCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = channel.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.category,
                    fontSize = 12.sp,
                    color = TextTertiary
                )
                Text(
                    text = if (isFavorite) "★" else "☆",
                    fontSize = 18.sp,
                    color = if (isFavorite) FavoriteGold else TextTertiary,
                    modifier = Modifier.clickable(onClick = onToggleFavorite)
                )
            }
        }
    }
}
