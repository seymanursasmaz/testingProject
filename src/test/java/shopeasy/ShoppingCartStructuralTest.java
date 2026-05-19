package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 2 – Structural Testing &amp; Code Coverage (Chapter 3)
 *
 * <p>Target class: {@link ShoppingCart}
 *
 * <h3>Workflow</h3>
 * <ol>
 *   <li>Write an initial test suite based on the specification (Javadoc of ShoppingCart).</li>
 *   <li>Run {@code mvn test} to generate the JaCoCo report:
 *       <pre>  target/site/jacoco/index.html</pre></li>
 *   <li>Open the report, navigate to {@code ShoppingCart}, and identify uncovered branches.</li>
 *   <li>Add tests specifically to cover those branches until branch coverage &gt;= 80%.</li>
 *   <li>Take a screenshot of the final JaCoCo summary and put it in {@code report/jacoco-screenshot.png}.</li>
 * </ol>
 *
 * <h3>Branches to think about</h3>
 * <ul>
 *   <li>{@code addItem}: product already in cart vs. new product</li>
 *   <li>{@code removeItem}: product found vs. not found in cart</li>
 *   <li>{@code updateQuantity}: product found vs. not found, quantity valid vs. invalid</li>
 *   <li>{@code applyDiscount}: zero discount, positive discount</li>
 *   <li>{@code total}: empty cart vs. non-empty cart</li>
 * </ul>
 *
 * <h3>Bonus (PIT Mutation Testing)</h3>
 * Run: {@code mvn org.pitest:pitest-maven:mutationCoverage}
 * <br>Examine the HTML report in {@code target/pit-reports/}. Find two surviving mutants,
 * explain why each survived, and describe a test that would kill it. Add this analysis
 * to your reflection report.
 */
class ShoppingCartStructuralTest {

    private ShoppingCart cart;
    private Product apple;
    private Product banana;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        apple  = new Product("P001", "Apple",  1.50, 100);
        banana = new Product("P002", "Banana", 0.80, 50);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    //
    // Start with happy-path tests, then add tests that target specific branches.
    //
    // HINT: Run `mvn test` after every few tests to see coverage progress.
    // -----------------------------------------------------------------------

    // Adding a product that is not yet in the cart
    @Test
    void addProductToEmptyCart() {
        cart.addItem(apple, 5);
        assertThat(cart.total()).isEqualTo(7.50);
    }

    // Adding a product that already exists in the cart
    @Test
    void addProductAlreadyInCart() {
        cart.addItem(apple, 5);
        cart.addItem(apple, 3);
        assertThat(cart.total()).isEqualTo(12.00);
    }

    // Adding multiple distinct products to the cart
    @Test
    void addSeveralProductsToCart() {
        cart.addItem(apple, 5);
        cart.addItem(banana, 10);
        assertThat(cart.total()).isEqualTo(15.50);
    }

    // Removing a product that is present in the cart
    @Test
    void removeExistingProductFromCart() {
        cart.addItem(apple, 5);
        cart.removeItem("P001");
        assertThat(cart.total()).isEqualTo(0.00);
    }

    // updateQuantity should throw if quantity is not positive
    @Test
    void updateQuantityWithNonPositiveValue() {
        cart.addItem(apple, 5);
        assertThatThrownBy(() -> cart.updateQuantity("P001", 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity must be > 0");
    }

    // updateQuantity should throw if product is not in the cart
    @Test
    void updateQuantityForProductNotInCart() {
        assertThatThrownBy(() -> cart.updateQuantity("P999", 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product not found in cart: P999");
    }

    // updateQuantity should throw if product code does not match any in the cart
    @Test
    void updateQuantityForWrongProductCode() {
        cart.addItem(apple, 5);
        assertThatThrownBy(() -> cart.updateQuantity("P002", 5))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product not found in cart: P002");
    }

    // updateQuantity should update the quantity for a valid product
    @Test
    void updateQuantityForProductInCart() {
        cart.addItem(apple, 5);
        cart.updateQuantity("P001", 10);
        assertThat(cart.total()).isEqualTo(15.00);
    }

    // applyDiscount should apply a positive discount rate
    @Test
    void applyDiscountWithValidRate() {
        cart.addItem(apple, 5);
        double discountedTotal = cart.applyDiscount(20);
        assertThat(discountedTotal).isEqualTo(6.00);
    }

    // getItems should return the correct number of items in the cart
    @Test
    void getItemsReturnsExpectedItemCount() {
        cart.addItem(apple, 5);
        cart.addItem(banana, 10);
        assertThat(cart.itemCount()).isEqualTo(2);
    }

    @Test
        void updateQuantityForWrongProductInNonEmptyCart() {
            cart.addItem(apple, 5);
            cart.clear();
            assertThatThrownBy(() -> cart.updateQuantity("P002", 10));
        }

    @Test
        void clearRemovesAllItemsFromCart() {

            cart.addItem(apple, 3);
            cart.addItem(banana, 5);
            assertThat(cart.itemCount()).isGreaterThan(0);
            assertThat(cart.total()).isGreaterThan(0.00);

            cart.clear();

            assertThat(cart.itemCount()).isEqualTo(0);
            assertThat(cart.total()).isEqualTo(0.00);
            assertThat(cart.getItems()).isEmpty();
        }

    @Test
        void testToStringFormatting() {
            cart.addItem(apple, 2); 

            String expectedOutput = "ShoppingCart{items=1, total=3,00}";
            assertThat(cart.toString()).isEqualTo(expectedOutput);
        }
    // -----------------------------------------------------------------------

}
