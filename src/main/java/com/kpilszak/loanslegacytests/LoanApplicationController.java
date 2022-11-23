package com.kpilszak.loanslegacytests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;

@Controller
public class LoanApplicationController {

    @Autowired
    private LoanApplicationRepository repository;

    @Autowired
    private JavaMailSender mailSender;

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping(value = "/")
    public ModelAndView renderNewLoanApplicationForm() {
        LoanApplication loan = new LoanApplication();
        return new ModelAndView("newLoanApplication", "form", loan);
    }

    @PostMapping(value = "/")
    public ModelAndView processNewLoanApplicationForm(LoanApplication loanApplication) {
        repository.save(loanApplication);

        URI location = restTemplate.postForLocation("http://localhost/loanApplication", loanApplication);

        BigDecimal applicableRate = loanApplication.getInterestRate().divide(new BigDecimal(100));
        applicableRate = applicableRate.add(new BigDecimal(1));

        BigDecimal totalRepayable = new BigDecimal(loanApplication.getPrincipal()
                * Double.parseDouble(applicableRate.toString()) * loanApplication.getTermInMonths() / 12);
        BigDecimal repayment = totalRepayable.divide(new BigDecimal("" + loanApplication.getTermInMonths()),
                RoundingMode.UP);
        loanApplication.setRepayment(repayment);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(loanApplication.getName());
        message.setSubject("Thank you for your loan application.");
        message.setText("We're currently processing your request and will send you a further email when we have a decision.");
        mailSender.send(message);

        return new ModelAndView("requestAccepted");
    }

    // Set methods used for tests only
    public void setRepository(LoanApplicationRepository repository) {
        this.repository = repository;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
