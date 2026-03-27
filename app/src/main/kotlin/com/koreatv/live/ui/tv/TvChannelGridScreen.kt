package com.koreatv.live.ui.tv

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
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
fun TvChannelGridScreen(
    viewModel: PlayerViewModel,
    onOpenSettings: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val channels = viewModel.filteredChannels()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.Menu) {
                        onOpenSettings()
                        true
                    } else false
                }
        ) {
            // Title bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "韩流直播",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TvFocusableButton(
                        text = if (showFavoritesOnly) "★ 收藏" else "☆ 收藏",
                        onClick = { viewModel.toggleFavoritesFilter() }
                    )
                    TvFocusableButton(
                        text = "⚙ 设置",
                        onClick = onOpenSettings
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category row
            if (categories.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        TvFocusableChip(
                            text = "全部",
                            selected = selectedCategory == null,
                            onClick = { viewModel.selectCategory(null) }
                        )
                    }
                    items(categories) { category ->
                        TvFocusableChip(
                            text = category,
                            selected = selectedCategory == category,
                            onClick = { viewModel.selectCategory(category) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Channel grid
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentPurple)
                }
            } else if (channels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无频道", color = TextTertiary, fontSize = 24.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 200.dp),
                    contentPadding = PaddingValues(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(channels, key = { it.id }) { channel ->
                        TvChannelCard(
                            channel = channel,
                            isFavorite = favorites.contains(channel.id),
                            onSelect = { viewModel.playChannel(channel) },
                            onToggleFavorite = { viewModel.toggleFavorite(channel.id) }
                        )
                    }
                }
            }
        }

        // Top scroll gradient mask
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun TvChannelCard(
    channel: Channel,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val cardScale by animateFloatAsState(
        targetValue = if (isFocused) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 300),
        label = "tvCardScale"
    )

    val cardElevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 4.dp,
        animationSpec = tween(durationMillis = 300),
        label = "tvCardElevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .scale(cardScale)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionCenter, Key.Enter -> {
                            onSelect()
                            true
                        }
                        Key.Bookmark -> {
                            onToggleFavorite()
                            true
                        }
                        else -> false
                    }
                } else false
            }
            .then(
                if (isFocused) Modifier.border(
                    2.dp,
                    Brush.linearGradient(listOf(AccentPurple, AccentCyan)),
                    RoundedCornerShape(16.dp)
                )
                else Modifier.border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused)
                Color(0xFF252525)
            else
                Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                if (channel.logo.isNotEmpty()) {
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier.size(80.dp).clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = channel.name.take(2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AccentCyan
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.category,
                    fontSize = 12.sp,
                    color = TextTertiary
                )
                if (isFavorite) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("★", fontSize = 14.sp, color = FavoriteGold)
                }
            }
        }
    }
}

@Composable
private fun TvFocusableButton(text: String, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    val buttonScale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "tvBtnScale"
    )

    Box(
        modifier = Modifier
            .scale(buttonScale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isFocused) Brush.horizontalGradient(listOf(AccentPurple, AccentCyan))
                else Brush.horizontalGradient(listOf(Color(0xFF1A1A1A), Color(0xFF1A1A1A)))
            )
            .then(
                if (!isFocused) Modifier.border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                else Modifier
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.DirectionCenter || event.key == Key.Enter)) {
                    onClick()
                    true
                } else false
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isFocused) Color.White else TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TvFocusableChip(text: String, selected: Boolean, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    val chipScale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "tvChipScale"
    )

    Box(
        modifier = Modifier
            .scale(chipScale)
            .clip(RoundedCornerShape(20.dp))
            .background(
                when {
                    isFocused -> Brush.horizontalGradient(listOf(AccentPurple, AccentCyan))
                    selected -> Brush.horizontalGradient(
                        listOf(AccentPurple.copy(alpha = 0.3f), AccentCyan.copy(alpha = 0.3f))
                    )
                    else -> Brush.horizontalGradient(
                        listOf(Color(0xFF1A1A1A), Color(0xFF1A1A1A))
                    )
                }
            )
            .then(
                when {
                    isFocused -> Modifier
                    selected -> Modifier.border(1.dp, AccentPurple.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    else -> Modifier.border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.DirectionCenter || event.key == Key.Enter)) {
                    onClick()
                    true
                } else false
            }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = when {
                isFocused -> Color.White
                selected -> Color.White
                else -> TextSecondary
            },
            fontWeight = if (selected || isFocused) FontWeight.Bold else FontWeight.Normal
        )
    }
}
