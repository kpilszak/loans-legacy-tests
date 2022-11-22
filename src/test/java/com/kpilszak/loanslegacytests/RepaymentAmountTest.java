package com.kpilszak.loanslegacytests;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class RepaymentAmountTest {

    @Spy
    LoanApplication loanApplication;

    @Test
    public void test1YearLoanWholePounds() {
        loanApplication = spy (new LoanApplication());
        loanApplication.setPrincipal(1200);
        loanApplication.setTermInMonths(12);
        doReturn(new BigDecimal(10)).when(loanApplication).getInterestRate();

        LoanApplicationController controller = new LoanApplicationController();
        controller.processNewLoanApplicationForm(loanApplication);

        assertEquals(new BigDecimal(110), loanApplication.getRepayment());
    }

    @Test
    public void test2YearLoanWholePounds() {}

    @Test
    public void test5YearLoanWithRounding() {}

}
