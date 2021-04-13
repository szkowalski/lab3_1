package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

@ExtendWith(MockitoExtension.class)
class BookKeeperTest {

    @Mock
    private InvoiceFactory factory;
    @Mock
    private TaxPolicy taxPolicy;

    private static final Id SAMPLE_CLIENT_ID = Id.generate();
    private static final String SAMPLE_CLIENT_NAME = "Kowalski";
    private static final ClientData SAMPLE_CLIENT_DATA = new ClientData(SAMPLE_CLIENT_ID, SAMPLE_CLIENT_NAME);
    private static final String SAMPLE_PRODUCT_DATA_NAME = "Product data name";
    private static final String SAMPLE_TAX_DESCRIPTION = "Tax description";
    private static final Money SAMPLE_MONEY = Money.ZERO;

    private BookKeeper keeper;

    @BeforeEach
    void setUp() throws Exception {
        keeper = new BookKeeper(factory);
    }

    @Test
    void returnOneItemWhenOneItemInInvoice() {

        InvoiceRequest request = new InvoiceRequest(SAMPLE_CLIENT_DATA);
        ProductData productData = new ProductDataBuilder()
                .productId(Id.generate())
                .name(SAMPLE_PRODUCT_DATA_NAME)
                .price(SAMPLE_MONEY)
                .snapshotDate(new Date())
                .type(ProductType.FOOD)
                .build();

        when(factory.create(SAMPLE_CLIENT_DATA)).thenReturn(new Invoice(Id.generate(), SAMPLE_CLIENT_DATA));
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(
                SAMPLE_MONEY,
                SAMPLE_TAX_DESCRIPTION
        ));
        RequestItem item = new RequestItem(productData, 1, SAMPLE_MONEY);
        request.add(item);


        Invoice actualInvoice = keeper.issuance(request, taxPolicy);

        int expectedCount = 1;
        int actualInvoiceCount = actualInvoice.getItems().size();
        assertEquals(expectedCount, actualInvoiceCount);
    }

}
