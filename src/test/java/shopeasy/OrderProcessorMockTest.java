package shopeasy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Task 5 – Mocks &amp; Stubs (Chapter 6)
 *
 * <p>Target class: {@link OrderProcessor}
 *
 * <p>Use Mockito to mock {@link InventoryService} and {@link PaymentGateway},
 * then test {@link OrderProcessor#process(String, ShoppingCart)} in isolation.
 *
 * <h3>Required scenarios (at least 4)</h3>
 * <ol>
 *   <li><b>Happy path</b> — inventory available, payment succeeds → non-null {@link Order} returned.</li>
 *   <li><b>Inventory failure</b> — {@code isAvailable()} returns {@code false} for at least one item
 *       → method returns {@code null} AND {@code charge()} is <em>never</em> called.</li>
 *   <li><b>Payment failure</b> — inventory OK, {@code charge()} returns {@code false}
 *       → method returns {@code null}.</li>
 *   <li><b>Partial quantity</b> — define the expected behaviour when only some items
 *       pass the inventory check, and write a test for it.</li>
 * </ol>
 *
 * <h3>Verification</h3>
 * Use {@code verify(paymentGateway, never()).charge(...)} to assert that
 * payment is never attempted when inventory is insufficient.
 *
 * <h3>Reflection (add to your report)</h3>
 * Answer: What does mocking allow you to test that you could not test otherwise?
 * What does it prevent you from testing? When is mocking a bad idea?
 */
@ExtendWith(MockitoExtension.class)
class OrderProcessorMockTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private OrderProcessor orderProcessor;

    private ShoppingCart cart;
    private Product widget;

    @BeforeEach
    void setUp() {
        cart   = new ShoppingCart();
        widget = new Product("P001", "Widget", 25.0, 100);
    }

    // -----------------------------------------------------------------------
    // TODO: Write your mock-based tests below.
    //
    // EXAMPLE STRUCTURE — happy path:
    //
    // @Test
    // void process_inventoryOkAndPaymentOk_returnsOrder() {
    //     cart.addItem(widget, 2);
    //
    //     when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
    //     when(paymentGateway.charge("customer-1", 50.0)).thenReturn(true);
    //
    //     Order order = orderProcessor.process("customer-1", cart);
    //
    //     assertThat(order).isNotNull();
    //     assertThat(order.getCustomerId()).isEqualTo("customer-1");
    //     assertThat(order.getTotal()).isEqualTo(50.0);
    //     verify(paymentGateway).charge("customer-1", 50.0);
    // }

    // Standard scenario: inventory and payment both succeed
    @Test
    void process_returnsOrderWhenInventoryAndPaymentSucceed() {
        cart.addItem(widget, 2);
        // Arrange mocks for available inventory and successful payment
        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(paymentGateway.charge("customer-1", 50.0)).thenReturn(true);
        // Act
        Order result = orderProcessor.process("customer-1", cart);
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo("customer-1");
        assertThat(result.getTotal()).isEqualTo(50.0);
        verify(paymentGateway).charge("customer-1", 50.0);
    }

    // Should return null and never charge if inventory is not available
    @Test
    void process_returnsNullIfInventoryUnavailable() {
        cart.addItem(widget, 2);
        // Arrange mock for unavailable inventory
        when(inventoryService.isAvailable(widget, 2)).thenReturn(false);
        // Act
        Order result = orderProcessor.process("customer-1", cart);
        // Assert
        assertThat(result).isNull();
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }

    // Should return null if payment fails, even if inventory is available
    @Test
    void process_returnsNullIfPaymentFails() {
        cart.addItem(widget, 2);
        // Arrange mocks for available inventory and failed payment
        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(paymentGateway.charge("customer-1", 50.0)).thenReturn(false);
        // Act
        Order result = orderProcessor.process("customer-1", cart);
        // Assert
        assertThat(result).isNull();
        verify(paymentGateway).charge("customer-1", 50.0);
    }

    // If only some items are available in inventory, should return null and never charge
    @Test
    void process_returnsNullIfPartialInventoryAvailable() {
        Product gadget = new Product("P002", "Gadget", 25.0, 1);
        cart.addItem(widget, 2);
        cart.addItem(gadget, 5);
        // Arrange mocks: widget available, gadget unavailable
        when(inventoryService.isAvailable(widget, 2)).thenReturn(true);
        when(inventoryService.isAvailable(gadget, 5)).thenReturn(false);
        // Act
        Order result = orderProcessor.process("customer-1", cart);
        // Assert
        assertThat(result).isNull();
        verify(paymentGateway, never()).charge(anyString(), anyDouble());
    }
    // -----------------------------------------------------------------------

}