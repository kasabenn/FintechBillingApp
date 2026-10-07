package com.industri.fintechbilling

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.Locale

class InvoiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        val rupiahFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))

        // 1. Eksekusi Parsing JSON Payload Transaksi
        val jsonPayload = loadMockInvoiceJson()
        val invoice: InvoiceResponse = Gson().fromJson(jsonPayload, InvoiceResponse::class.java)

        // 2. Hubungkan Informasi Header, Waktu & Metode
        findViewById<TextView>(R.id.tvInvoiceNo).text = invoice.invoiceNumber
        findViewById<TextView>(R.id.tvStatusBadge).text = invoice.paymentStatus
        findViewById<TextView>(R.id.tvPaymentMethod).text = "Metode: ${invoice.paymentMethod}"

        // TUGAS MANDIRI 2: Custom Date Formatting
        findViewById<TextView>(R.id.tvTransactionDate).text = invoice.transactionDate.toUserFriendlyDate()

        // 3. Hubungkan Informasi Merchant (Defensive Safe-Calls)
        val merchant = invoice.merchant
        findViewById<TextView>(R.id.tvMerchantName).text = merchant?.merchantName ?: "-"
        findViewById<TextView>(R.id.tvMerchantCity).text = "Kota: ${merchant?.cityLocation ?: "-"}"
        findViewById<TextView>(R.id.tvMerchantVerified).text =
            if (merchant?.isVerified == true) "Terverifikasi: RESMI (Ya)" else "Terverifikasi: Belum"

        // 4. Hubungkan Informasi Customer (Defensive Safe-Calls)
        val customer = invoice.customer
        val customerName = customer?.fullName ?: "-"
        val customerPhone = customer?.phone ?: "-"
        findViewById<TextView>(R.id.tvCustomerName).text = "Penerima: $customerName ($customerPhone)"

        // 5. Pasangkan Daftar Barang Belanja ke RecyclerView
        val rvItems = findViewById<RecyclerView>(R.id.rvInvoiceItems)
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = InvoiceItemAdapter(invoice.items ?: emptyList())

        // 6. Hubungkan Rangkuman Biaya (Pajak PPN 11%, Fee, Diskon & Total) dengan Defensive Null Handling
        val s = invoice.summary
        val subtotal = s?.subtotalAmount ?: 0.0
        val discount = s?.discountAmount ?: 0.0
        val tax = s?.taxPpn11 ?: 0.0
        val fee = s?.serviceFee ?: 0.0
        val totalPaid = s?.totalPaid ?: 0.0

        findViewById<TextView>(R.id.tvSubtotalAmt).text = rupiahFormat.format(subtotal)
        findViewById<TextView>(R.id.tvTaxAmt).text = rupiahFormat.format(tax)
        findViewById<TextView>(R.id.tvFeeAmt).text = rupiahFormat.format(fee)
        findViewById<TextView>(R.id.tvTotalPaid).text = rupiahFormat.format(totalPaid)

        // TUGAS MANDIRI 1: Defensive Nullable Handling untuk Voucher & Diskon
        val tvVoucherInfo = findViewById<TextView>(R.id.tvVoucherInfo)
        val layoutDiscount = findViewById<LinearLayout>(R.id.layoutDiscount)
        val tvDiscountAmt = findViewById<TextView>(R.id.tvDiscountAmt)

        if (s?.voucherCode == null) {
            tvVoucherInfo.text = "Tidak menggunakan promo"
            layoutDiscount.visibility = View.GONE
        } else {
            val percent = s.voucherDiscountPercent ?: 0
            tvVoucherInfo.text = "Voucher: ${s.voucherCode} - Diskon $percent%"
            layoutDiscount.visibility = View.VISIBLE
            tvDiscountAmt.text = "-${rupiahFormat.format(discount)}"
        }
    }

    /**
     * Data mock JSON sesuai spesifikasi modul praktikum Pertemuan 11.
     */
    private fun loadMockInvoiceJson(): String {
        return """
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
    }
}
