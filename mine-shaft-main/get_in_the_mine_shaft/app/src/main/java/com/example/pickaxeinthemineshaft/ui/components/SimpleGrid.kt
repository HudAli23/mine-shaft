package com.example.pickaxeinthemineshaft.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A simple grid implementation using LazyColumn and Row for Compose 1.0.5
 * which doesn't have LazyVerticalGrid
 */
@Composable
fun <T> SimpleGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(16.dp),
    content: @Composable (T) -> Unit
) {
    val rows = items.chunked(columns)
    
    LazyColumn(
        verticalArrangement = verticalArrangement,
        modifier = modifier
    ) {
        items(rows) { rowItems ->
            Row(
                horizontalArrangement = horizontalArrangement,
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        content(item)
                    }
                }
                
                // If we have an incomplete row, add empty spaces
                if (rowItems.size < columns) {
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
