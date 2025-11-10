# Step 1. Define Data Stability (`@Immutable`)

1.  Define the **`Product`** data class, annotating it with `@Immutable` and ensuring all fields are `val`:
    ```kotlin
    @Immutable
    data class Product(
        val id: Int,
        val name: String,
        val price: Double
    )
    ```
2.  Create the **`getSampleProducts`** private utility function to generate dummy list data:
    ```kotlin
    private fun getSampleProducts() = (1..50).map {
        Product(it, "Product $it", 9.99 + it)
    }
    ```

# Step 2. Implement List Performance Components

1.  Create the **`ProductItem`** Composable accepting a `product: Product`.
2.  Implement a `Row` layout inside this Composable to display the product ID, name, and price. Use a specific blue color for the ID text and set background/padding/shapes.
3.  Create the **`ProductListScreen`** Composable accepting a list of `products` and an **`onMove`** lambda: `(Int, Boolean) -> Unit` to move items in the list.
4.  Add **`Text`** and **`Row`** containing two **`Button`** components to the screen, which call the `onMove` lambda to demonstrate item movement.
5.  Implement the **`LazyColumn`** that displays the list of products.
6.  Crucially, use the overloaded `items` function and provide the **`key`** lambda: **`key = { it.id }`**.
7.  Inside the `items` block, call the **`ProductItem`** Composable.
8.  Uncomment the relevant section in `ListPerformanceDemoApp` to test the functionality