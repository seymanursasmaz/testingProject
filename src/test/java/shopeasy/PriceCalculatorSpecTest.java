package shopeasy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Task 1 – Specification-Based Testing (Chapter 2)
 *
 * <p>Target class: {@link PriceCalculator}
 *
 * <p>Your goal is to test {@code PriceCalculator.calculate(basePrice, discountRate, taxRate)}
 * using the domain testing technique from Chapter 2:
 * <ol>
 *   <li>Identify equivalence partitions for each input dimension.</li>
 *   <li>Identify boundary values between partitions (on-point / off-point).</li>
 *   <li>Write at least 10 meaningful test cases that cover both partitions and boundaries.</li>
 *   <li>Use {@code @ParameterizedTest} with {@code @CsvSource} for tests that share structure.</li>
 *   <li>Add a comment above each test method explaining which partition or boundary it covers.</li>
 * </ol>
 *
 * <h3>Input dimensions to consider</h3>
 * <ul>
 *   <li><b>basePrice</b>  – zero, positive, very large</li>
 *   <li><b>discountRate</b> – 0 (no discount), (0,100) typical, 100 (full discount)</li>
 *   <li><b>taxRate</b>    – 0 (no tax), (0,100) typical, 100 (100% tax)</li>
 * </ul>
 */
class PriceCalculatorSpecTest {

    private PriceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PriceCalculator();
    }

    // -----------------------------------------------------------------------
    // TODO: Write your tests below.
    // -----------------------------------------------------------------------
    
    @Test
    void returnsZeroWhenBasePriceIsZero() {
        double result = calculator.calculate(0, 20, 10);
        assertThat(result).isZero();
    }
    /** Partition: if there is no discount, result should be equal to base price */
    @Test
    void returnsBasePriceWhenNoDiscount() {
        double result = calculator.calculate(100, 0, 0);
        assertThat(result).isEqualTo(100.0);
    }
    /** Partition: no discount, tax is applied */
    @Test
    void appliesTaxWhenNoDiscount() {
        double result = calculator.calculate(100, 0, 10);
        assertThat(result).isEqualTo(110.0);
    }

    /** Invalid Boundary: negative base price should throw an error */
    @Test
    void throwsWhenBasePriceIsNegative() {
        Throwable thrown = org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(-10, 20, 10));
        assertThat(thrown).isInstanceOf(AssertionError.class);
    }

    /** Invalid Boundary: discount rate is negative or greater than 100 should throw an error */
    @Test
    void throwsWhenDiscountRateInvalid() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, -10, 10));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 110, 10));
    }

    /** Invalid Boundary: tax rate is negative or greater than 100 should throw an error */
    @Test
    void throwsWhenTaxRateInvalid() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 20, -5));
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> calculator.calculate(100, 20, 150));
    }

    /** Boundary: if discount rate is 100%, price should be zero */
    @Test
    void fullDiscountResultsInZero() {
        double result = calculator.calculate(100, 100, 0);
        assertThat(result).isZero();
    }

    /** Boundary: if tax rate is 0%, no tax should be applied */
    @Test
    void noTaxAppliedWhenZeroTaxRate() {
        double result = calculator.calculate(100, 20, 0);
        assertThat(result).isEqualTo(80.0);
    }

    /** Boundary: if tax rate is 100%, full tax should be applied */
    @Test
    void fullTaxAppliedWhenTaxRateHundred() {
        double result = calculator.calculate(100, 20, 100);
        assertThat(result).isEqualTo(160.0);
    }

    /** Partition: typical values — check formula correctness */
    @ParameterizedTest(name = "base={0}, discount={1}%, tax={2}% => expected={3}")
    @CsvSource({
        "100.0, 10.0, 20.0, 108.0",
        "200.0,  0.0, 10.0, 220.0",
        "50.0, 50.0, 50.0, 37.5",
    })
    void calculatesCorrectlyForTypicalValues(double base, double discount, double tax, double expected) {
        double result = calculator.calculate(base, discount, tax);
        assertThat(result).isCloseTo(expected, within(0.001));
    }
}
