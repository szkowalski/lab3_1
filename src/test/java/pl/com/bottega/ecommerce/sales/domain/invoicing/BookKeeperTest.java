package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;
import java.util.List;

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

    @Captor
    ArgumentCaptor<ProductType> productTypeArgumentCaptor;

    @Captor
    ArgumentCaptor<Money> moneyArgumentCaptor;

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

    @Test
    void twoInvokesOfTaxMethodForRequestWithTwoItems() {

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
        Money money = new Money(5, Money.DEFAULT_CURRENCY);
        RequestItem item2 = new RequestItem(productData, 1, money);
        request.add(item);
        request.add(item2);

        keeper.issuance(request, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(productTypeArgumentCaptor.capture(), moneyArgumentCaptor.capture());
        List<ProductType> productTypeList = productTypeArgumentCaptor.getAllValues();
        List<Money> moneyList = moneyArgumentCaptor.getAllValues();

        assertEquals(ProductType.FOOD, productTypeList.get(0));
        assertEquals(ProductType.FOOD, productTypeList.get(1));

        assertEquals(SAMPLE_MONEY, moneyList.get(0));
        assertEquals(money, moneyList.get(1));
    }

}
