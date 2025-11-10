package com.example.listperformance

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listperformance.ui.theme.ListPerformanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListPerformanceTheme {
                ListPerformanceDemoApp()
            }
        }
    }
}

/**
 * @Immutable: A crucial annotation for Compose performance.
 * It tells the Compose compiler that once an instance of this class is created,
 * none of its public properties will ever change.
 * This allows Compose to skip recomposition checks for items using this class
 * when its parent or siblings change, significantly improving speed.
 *
 * Note: All fields must be 'val' for a data class to be truly immutable.
 */
@Immutable
data class Product(
    val id: Int,
    val name: String,
    val price: Double
)

// Dummy data for list demos
private fun getSampleProducts() = (1..50).map {
    Product(it, "Product $it", 9.99 + it)
}

@Composable
fun ProductListScreen(products: List<Product>, onMove: (Int, Boolean) -> Unit) {
    Column {
        Text(
            "LazyColumn with 'key'",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        // Control buttons to demonstrate item movement
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { onMove(0, false) }) {
                Text("Move First Item Down", fontSize = 12.sp)
            }
            Button(
                onClick = { onMove(products.lastIndex, true) }) {
                Text("Move Last Item Up", fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(16.dp))

        // IMPORTANT: The 'key' lambda function ensures Compose correctly tracks each item.
        // Without it, moving an item might cause all subsequent items to be recreated.
        LazyColumn(Modifier.fillMaxSize()) {
            items(
                items = products,
                key = { it.id } // VITAL: Using the stable ID for tracking
            ) { product ->
                ProductItem(product = product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    Log.d("ComposeDebug", "Recomposing item with ID: ${product.id}")
    // This item will only recompose if the 'product' object itself changes (thanks to @Immutable)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.id.toString(),
            fontSize = 20.sp,
            color = Color(0xFF4285F4),
            modifier = Modifier.width(30.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.bodyLarge)
            Text("Price: \$${product.price}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ListPerformanceDemoApp() {
    var listKey by remember { mutableStateOf(0) }

    // Manage a mutable list for the LazyColumn demo
    var products by remember { mutableStateOf(getSampleProducts()) }

    // Helper function to simulate list item movement
    val onMove: (Int, Boolean) -> Unit = { index, isUp ->
        products = products.toMutableList().apply {
            if (isUp) {
                // Move Item at index 0 up (i.e., swap with the element before it, or move to end if at start)
                if (index > 0) {
                    val item = removeAt(index)
                    add(index - 1, item)
                }
            } else {
                // Move Item at index X down (i.e., swap with the element after it)
                if (index < size - 1) {
                    val item = removeAt(index)
                    add(index + 1, item)
                }
            }
        }.toList()
        // Force a state update
        listKey++
    }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4F8)).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("List Performance Demo", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(bottom = 16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            // -- List Performance Demo --
            Text("LazyList Performance (key required)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 16.dp))
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                ProductListScreen(products, onMove)
            }
        }
    }
}

