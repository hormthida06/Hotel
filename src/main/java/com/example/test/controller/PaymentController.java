package com.example.test.controller;

import com.example.test.entity.Payment;
import com.example.test.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/list")
    public String listPayments(
            @RequestParam(required = false) String bookingId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Payment> paymentPage;

        if (bookingId != null && !bookingId.isBlank()) {
            paymentPage = paymentRepository.searchByBookingId(bookingId, pageable);
        } else {
            paymentPage = paymentRepository.findAll(pageable);
        }

        model.addAttribute("paymentPage", paymentPage);
        model.addAttribute("pageSize", size);
        model.addAttribute("searchBookingId", bookingId);

        return "table/payments/view";
    }

    @PostMapping("/delete/{id}")
    public String deletePayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        if (!paymentRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Payment not found!");
            return "redirect:/payment/list";
        }

        paymentRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Payment deleted successfully!");

        return "redirect:/payment/list";
    }


}
