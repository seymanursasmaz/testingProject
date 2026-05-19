package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Task 3 – Design by Contract (Chapter 4)
 *
 * <p>This task has two parts:
 *
 * <h3>Part A – Add contracts to production code</h3>
 * Open {@link ShoppingCart} and {@link PriceCalculator} and add {@code assert}
 * statements for the pre-conditions and post-conditions described in their Javadoc.
 * Note: assertions are enabled via {@code -ea} in Maven Surefire (already configured
 * in {@code pom.xml}).
 *
 * <p>Contracts to implement:
 * <ul>
 *   <li><b>ShoppingCart.addItem</b>: pre — {@code product != null}, {@code quantity > 0};
 *       post — {@code itemCount()} increased or product quantity updated.</li>
 *   <li><b>ShoppingCart.applyDiscount</b>: pre — {@code 0 <= discountRate <= 100};
 *       post — result &lt;= {@code total()} when {@code discountRate > 0}.</li>
 *   <li><b>PriceCalculator.calculate</b>: pre — {@code basePrice >= 0},
 *       {@code 0 <= discountRate <= 100}, {@code 0 <= taxRate <= 100};
 *       post — result {@code >= 0}.</li>
 *   <li><b>ShoppingCart invariant</b>: {@code total() >= 0} after any operation.</li>
 * </ul>
 *
 * <h3>Part B – Write contract tests</h3>
 * Write tests below that:
 * <ol>
 *   <li>Verify contracts hold for valid inputs (positive tests).</li>
 *   <li>Verify contracts are violated ({@code AssertionError}) for invalid inputs (negative tests).</li>
 * </ol>
 *
 * <p>Use {@code assertThatThrownBy(...).isInstanceOf(AssertionError.class)} to test violations.
 */
class ContractTest {

    private ShoppingCart cart;
    private PriceCalculator calculator;
    private Product product;

    @BeforeEach
    void setUp() {
        cart       = new ShoppingCart();
        calculator = new PriceCalculator();
        product    = new Product("P001", "Widget", 10.0, 50);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your contract tests below.
    //
    // EXAMPLE — pre-condition violation (fill in the correct assertion):
    //
    // @Test
    // void addItem_nullProduct_shouldViolatePreCondition() {
    //     assertThatThrownBy(() -> cart.addItem(null, 1))
    //             .isInstanceOf(AssertionError.class);
    // }
    //
    // EXAMPLE — pre-condition holds (valid input):
    //
    // @Test
    // void addItem_validInput_shouldNotThrow() {
    //     assertThatCode(() -> cart.addItem(product, 3)).doesNotThrowAnyException();
    // }
    // -----------------------------------------------------------------------

     // Pre-Condition violations:

     // Should throw AssertionError if basePrice is negative
    @Test
    void calculateThrowsWhenBasePriceIsNegative() {
        Throwable thrown = org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(-1, 10, 5));
        assertThat(thrown).isInstanceOf(AssertionError.class);
    }

    // Negative discountRate should violate precondition
    @Test
    void calculateThrowsForNegativeDiscountRate() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, -5, 5));
    }
    // Discount rate above 100 should not be allowed
    @Test
    void calculateThrowsForDiscountRateOver100() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 150, 5));
    }

    // Should throw if taxRate is less than zero
    @Test
    void calculateThrowsForNegativeTaxRate() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 10, -5));
    }
    // Should throw if taxRate is greater than 100
    @Test
    void calculateThrowsForTaxRateAbove100() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 10, 150));
    }


    // Passing null as product should fail precondition
    @Test
    void addItemShouldThrowIfProductIsNull() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> cart.addItem(null, 1));
    }

    // Zero quantity is not allowed for addItem
    @Test
    void addItemShouldThrowIfQuantityIsZero() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> cart.addItem(product, 0));
    }
    // Negative quantity should violate addItem precondition
    @Test
    void addItemShouldThrowIfQuantityIsNegative() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> cart.addItem(product, -5));
    }

    // Discount rate less than zero should throw
    @Test
    void applyDiscountThrowsIfRateIsNegative() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> cart.applyDiscount(-10));
    }

    // Discount rate above 100 should throw
    @Test
    void applyDiscountThrowsIfRateAbove100() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> cart.applyDiscount(150));
    }


    // Post-Condition violations
    @Test
    void addItem_cartSizeIncreasedOrQuantityUpdated() {
        cart.addItem(product, 5);
        assertThat(cart.itemCount()).isEqualTo(1);
        assertThat(cart.total()).isEqualTo(50.0);

        cart.addItem(product, 3);
        assertThat(cart.itemCount()).isEqualTo(1); // same product, quantity updated
        assertThat(cart.total()).isEqualTo(80.0); // total should reflect new quantity
    }

    @Test
    void applyDiscount_resultLessThanTotal() {
        cart.addItem(product, 5); // total = 50.0
        double discounted = cart.applyDiscount(20); // 20% discount
        assertThat(discounted).isLessThan(cart.total());
    }

    @Test
    void applyDiscount_zeroRate_resultEqualsTotal() {
        cart.addItem(product, 5); // total = 50.0
        double discounted = cart.applyDiscount(0); // 0% discount
        assertThat(discounted).isEqualTo(cart.total());
    }

    @Test
    void calculate_resultNonNegative() {
        double finalPrice = calculator.calculate(100, 20, 10);
        assertThat(finalPrice).isGreaterThanOrEqualTo(0);
        double finalPrice2 = calculator.calculate(100, 0, 50);
        assertThat(finalPrice2).isGreaterThanOrEqualTo(0);
        double finalPrice3 = calculator.calculate(100, 50, 0);
        assertThat(finalPrice3).isGreaterThanOrEqualTo(0);
        double finalPrice4 = calculator.calculate(0, 20, 10);
        assertThat(finalPrice4).isGreaterThanOrEqualTo(0);
    }



    // Invariant Tests
    @Test
    void totalShouldNeverBeNegative() {
        cart.addItem(product, 5);
        assertThat(cart.total()).isGreaterThanOrEqualTo(0);
        cart.updateQuantity(product.getId(), 10);
        assertThat(cart.total()).isGreaterThanOrEqualTo(0);
        cart.applyDiscount(20);
        assertThat(cart.total()).isGreaterThanOrEqualTo(0);
        cart.removeItem(product.getId());
        assertThat(cart.total()).isGreaterThanOrEqualTo(0);
    }

}
