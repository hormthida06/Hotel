package com.example.test.controller;

import com.example.test.entity.Discount;
import com.example.test.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/discount")
public class DiscountController {

    @Autowired
    private DiscountRepository discountRepository;

    // ================= CREATE =================

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("discount", new Discount());
        model.addAttribute("types", Discount.DiscountType.values());
        model.addAttribute("statuses", Discount.Status.values());
        return "table/discounts/create";
    }

    @PostMapping("/create")
    public String createDiscount(
            @RequestParam String code,
            @RequestParam Discount.DiscountType discountType,
            @RequestParam BigDecimal discountValue,
            @RequestParam BigDecimal minBookingAmount,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            RedirectAttributes redirectAttributes) {

        try {
            Discount discount = new Discount();
            discount.setCode(code);
            discount.setDiscountType(discountType);
            discount.setDiscountValue(discountValue);
            discount.setMinBookingAmount(minBookingAmount);
            discount.setStartDate(startDate);
            discount.setEndDate(endDate);
            discount.setStatus(Discount.Status.ACTIVE);

            discountRepository.save(discount);

            redirectAttributes.addFlashAttribute("success", "Discount created successfully!");
            return "redirect:/discount/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/discount/create";
        }
    }

    // ================= LIST =================

    @GetMapping("/list")
    public String listDiscounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Discount> discountPage =
                discountRepository.findAll(PageRequest.of(page, size));

        model.addAttribute("discounts", discountPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", discountPage.getTotalPages());
        model.addAttribute("totalElements", discountPage.getTotalElements());

        return "table/discounts/list";
    }

    // ================= EDIT =================

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount ID"));

        model.addAttribute("discount", discount);
        model.addAttribute("types", Discount.DiscountType.values());
        model.addAttribute("statuses", Discount.Status.values());

        return "table/discounts/edit";
    }

    // ================= UPDATE =================

    @PostMapping("/update")
    public String updateDiscount(
            @RequestParam Long id,
            @RequestParam String code,
            @RequestParam Discount.DiscountType discountType,
            @RequestParam BigDecimal discountValue,
            @RequestParam BigDecimal minBookingAmount,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam Discount.Status status,
            RedirectAttributes redirectAttributes) {

        try {
            Discount discount = discountRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid discount ID"));

            discount.setCode(code);
            discount.setDiscountType(discountType);
            discount.setDiscountValue(discountValue);
            discount.setMinBookingAmount(minBookingAmount);
            discount.setStartDate(startDate);
            discount.setEndDate(endDate);
            discount.setStatus(status);

            discountRepository.save(discount);

            redirectAttributes.addFlashAttribute("success", "Discount updated successfully!");
            return "redirect:/discount/list";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/discount/edit/" + id;
        }
    }

    // ================= DELETE =================

    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            discountRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Discount deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/discount/list";
    }
}
