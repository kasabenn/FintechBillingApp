package com.industri.fintechbilling

import com.google.gson.annotations.SerializedName

// 1. Root DTO Faktur Transaksi
data class InvoiceResponse(
    @SerializedName("invoice_number")
    val invoiceNumber: String = "",

    @SerializedName("transaction_date")
    val transactionDate: String = "",

    @SerializedName("payment_status")
    val paymentStatus: String = "",

    @SerializedName("payment_method")
    val paymentMethod: String = "",

    @SerializedName("merchant")
    val merchant: Merchant? = null,

    @SerializedName("customer")
    val customer: Customer? = null,

    @SerializedName("items")
    val items: List<InvoiceItem>? = null,

    @SerializedName("summary")
    val summary: BillingSummary? = null
)

// 2. DTO Customer Pembayar
data class Customer(
    @SerializedName("customer_id")
    val customerId: String = "",

    @SerializedName("full_name")
    val fullName: String = "",

    @SerializedName("phone")
    val phone: String = ""
)

// 3. DTO Item Produk Belanja (JSON Array Element)
data class InvoiceItem(
    @SerializedName("item_id")
    val itemId: String = "",

    @SerializedName("item_name")
    val itemName: String = "",

    @SerializedName("qty")
    val qty: Int = 0,

    @SerializedName("unit_price")
    val unitPrice: Double = 0.0,

    @SerializedName("subtotal")
    val subtotal: Double = 0.0
)

// 4. DTO Rangkuman Biaya & Pajak (Tugas Mandiri Nullable Voucher)
data class BillingSummary(
    @SerializedName("subtotal_amount")
    val subtotalAmount: Double = 0.0,

    @SerializedName("discount_amount")
    val discountAmount: Double = 0.0,

    @SerializedName("tax_ppn_11")
    val taxPpn11: Double = 0.0,

    @SerializedName("service_fee")
    val serviceFee: Double = 0.0,

    @SerializedName("total_paid")
    val totalPaid: Double = 0.0,

    // TUGAS MANDIRI 1: Defensive Nullable Handling
    @SerializedName("voucher_code")
    val voucherCode: String? = null,

    @SerializedName("voucher_discount_percent")
    val voucherDiscountPercent: Int? = null
)
