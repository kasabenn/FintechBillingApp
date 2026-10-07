package com.industri.fintechbilling

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InvoiceParsingTest {

    private val gson = Gson()

    @Test
    fun testMerchantParsing() {
        val json = """
        {
          "merchant_id": "MCH-99201",
          "merchant_name": "Mega Elektrindo Ritel",
          "city_location": "Bandung",
          "is_verified": true
        }
        """.trimIndent()

        val merchant = gson.fromJson(json, Merchant::class.java)
        assertEquals("MCH-99201", merchant.merchantId)
        assertEquals("Mega Elektrindo Ritel", merchant.merchantName)
        assertEquals("Bandung", merchant.cityLocation)
        assertTrue(merchant.isVerified)
    }

    @Test
    fun testInvoiceResponseParsing() {
        val json = """
        {
          "invoice_number": "INV-2026-FT9012",
          "transaction_date": "2026-09-23 10:15:00",
          "payment_status": "PAID_SETTLED",
          "payment_method": "QRIS_BCA",
          "merchant": {
            "merchant_id": "MCH-99201",
            "merchant_name": "Mega Elektrindo Ritel",
            "city_location": "Bandung",
            "is_verified": true
          },
          "customer": {
            "customer_id": "CUST-4412",
            "full_name": "Ahmad Fauzi",
            "phone": "081298765432"
          },
          "items": [
            {
              "item_id": "ITM-01",
              "item_name": "Kabel Type-C 65W Fast Charging",
              "qty": 2,
              "unit_price": 75000.0,
              "subtotal": 150000.0
            },
            {
              "item_id": "ITM-02",
              "item_name": "Adaptor GaN Charger 3-Port 100W",
              "qty": 1,
              "unit_price": 320000.0,
              "subtotal": 320000.0
            },
            {
              "item_id": "ITM-03",
              "item_name": "Mouse Wireless Silent Click Ergonomis",
              "qty": 1,
              "unit_price": 130000.0,
              "subtotal": 130000.0
            }
          ],
          "summary": {
            "subtotal_amount": 600000.0,
            "discount_amount": 50000.0,
            "tax_ppn_11": 60500.0,
            "service_fee": 2500.0,
            "total_paid": 613000.0,
            "voucher_code": null,
            "voucher_discount_percent": null
          }
        }
        """.trimIndent()

        val invoice = gson.fromJson(json, InvoiceResponse::class.java)

        assertEquals("INV-2026-FT9012", invoice.invoiceNumber)
        assertEquals("2026-09-23 10:15:00", invoice.transactionDate)
        assertEquals("PAID_SETTLED", invoice.paymentStatus)
        assertEquals("QRIS_BCA", invoice.paymentMethod)

        // Merchant checks
        assertEquals("Mega Elektrindo Ritel", invoice.merchant?.merchantName)
        assertEquals("Bandung", invoice.merchant?.cityLocation)
        assertTrue(invoice.merchant?.isVerified == true)

        // Customer checks
        assertEquals("Ahmad Fauzi", invoice.customer?.fullName)
        assertEquals("081298765432", invoice.customer?.phone)

        // Items checks
        val items = invoice.items ?: emptyList()
        assertEquals(3, items.size)
        assertEquals("Kabel Type-C 65W Fast Charging", items[0].itemName)
        assertEquals(2, items[0].qty)
        assertEquals(75000.0, items[0].unitPrice, 0.001)
        assertEquals(150000.0, items[0].subtotal, 0.001)

        // Summary & Voucher checks
        assertNotNull(invoice.summary)
        invoice.summary?.let { summary ->
            assertEquals(600000.0, summary.subtotalAmount, 0.001)
            assertEquals(50000.0, summary.discountAmount, 0.001)
            assertEquals(60500.0, summary.taxPpn11, 0.001)
            assertEquals(2500.0, summary.serviceFee, 0.001)
            assertEquals(613000.0, summary.totalPaid, 0.001)
            assertNull(summary.voucherCode)
            assertNull(summary.voucherDiscountPercent)
        }
    }

    @Test
    fun testCustomDateFormatting() {
        val rawDate = "2026-09-23 10:15:00"
        val formatted = rawDate.toUserFriendlyDate()
        assertEquals("Rabu, 23 September 2026 - Pukul 10:15 WIB", formatted)
    }

    @Test
    fun testVoucherPromoNullFallback() {
        val summaryWithoutPromo = BillingSummary(
            subtotalAmount = 600000.0,
            discountAmount = 0.0,
            taxPpn11 = 60500.0,
            serviceFee = 2500.0,
            totalPaid = 663000.0,
            voucherCode = null,
            voucherDiscountPercent = null
        )

        val promoText = if (summaryWithoutPromo.voucherCode == null) {
            "Tidak menggunakan promo"
        } else {
            "Voucher: ${summaryWithoutPromo.voucherCode} - Diskon ${summaryWithoutPromo.voucherDiscountPercent}%"
        }

        assertEquals("Tidak menggunakan promo", promoText)
    }

    @Test
    fun testVoucherPromoActive() {
        val summaryWithPromo = BillingSummary(
            subtotalAmount = 600000.0,
            discountAmount = 50000.0,
            taxPpn11 = 60500.0,
            serviceFee = 2500.0,
            totalPaid = 613000.0,
            voucherCode = "DISKON50K",
            voucherDiscountPercent = 10
        )

        val promoText = if (summaryWithPromo.voucherCode == null) {
            "Tidak menggunakan promo"
        } else {
            "Voucher: ${summaryWithPromo.voucherCode} - Diskon ${summaryWithPromo.voucherDiscountPercent}%"
        }

        assertEquals("Voucher: DISKON50K - Diskon 10%", promoText)
    }

    @Test
    fun testDefensiveNullSafety() {
        val emptyJson = "{}"
        val parsed = gson.fromJson(emptyJson, InvoiceResponse::class.java)
        // Ensure no crash occurred
        assertNull(parsed.summary)
        assertTrue(parsed.items.isNullOrEmpty())
    }
}
