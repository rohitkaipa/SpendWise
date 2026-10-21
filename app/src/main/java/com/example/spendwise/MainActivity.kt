package com.example.spendwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spendwise.domain.model.TransactionType
import com.example.spendwise.presentation.viewmodel.TransactionDisplayItem
import com.example.spendwise.presentation.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Date
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val transactionViewModel: TransactionViewModel = hiltViewModel()

                SpendWiseScreen(
                    viewModel = transactionViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpendWiseScreen(
    viewModel: TransactionViewModel
) {
    val displayCurrency by viewModel.displayCurrency.collectAsState()
    val transactionItems by viewModel.transactionDisplayItems.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val income by viewModel.totalIncome.collectAsState()
    val expenses by viewModel.totalExpenses.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var currencyDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Text(
                text = "Made by Rohit Kaipa",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SpendWise",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Default display currency (converts all totals)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = currencyDropdownExpanded,
                    onExpandedChange = { currencyDropdownExpanded = !currencyDropdownExpanded },
                    modifier = Modifier.width(130.dp)
                ) {
                    OutlinedTextField(
                        value = displayCurrency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Display") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyDropdownExpanded)
                        },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = currencyDropdownExpanded,
                        onDismissRequest = { currencyDropdownExpanded = false }
                    ) {
                        listOf("INR", "USD", "EUR", "GBP", "JPY").forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    viewModel.setDisplayCurrency(code)
                                    currencyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("Total Balance ($displayCurrency)")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatAmount(balance, displayCurrency),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryCard(
                    title = "Income ($displayCurrency)",
                    amount = income,
                    currencyCode = displayCurrency,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Expenses ($displayCurrency)",
                    amount = expenses,
                    currencyCode = displayCurrency,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { showForm = !showForm },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (showForm) "Cancel" else "+ Add Transaction"
                )
            }

            if (showForm) {
                AddTransactionForm(
                    defaultCurrency = displayCurrency,
                    onAddTransaction = { title, category, amount, currency, type ->
                        viewModel.addTransaction(
                            title = title,
                            category = category,
                            amount = amount,
                            currencyCode = currency,
                            type = type
                        )
                        showForm = false
                    }
                )
            }

            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (transactionItems.isEmpty()) {
                Text(
                    text = "No transactions yet. Add your first one!",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                transactionItems.forEach { item ->
                    TransactionRow(
                        item = item,
                        onDelete = { id -> viewModel.deleteTransaction(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    currencyCode: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(title)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = formatAmount(amount, currencyCode),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TransactionRow(
    item: TransactionDisplayItem,
    onDelete: (Long) -> Unit
) {
    val isIncome = item.type == TransactionType.INCOME

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${item.category} • ${item.originalCurrency} • ${formatDate(item.timestamp)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "${if (isIncome) "+" else "−"}${
                            formatAmount(item.originalAmount, item.originalCurrency)
                        }",
                        fontWeight = FontWeight.Bold
                    )
                    if (item.originalCurrency != item.displayCurrency) {
                        Text(
                            text = "(${if (isIncome) "+" else "−"}${
                                formatAmount(item.convertedAmount, item.displayCurrency)
                            })",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionForm(
    defaultCurrency: String,
    onAddTransaction: (
        String,
        String,
        Double,
        String,
        TransactionType
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }

    val expenseCategories = listOf(
        "Food & Dining",
        "Travel",
        "Shopping",
        "Bills & Utilities",
        "Entertainment",
        "Health",
        "Miscellaneous",
        "Other"
    )
    val incomeCategories = listOf(
        "Salary",
        "Freelance",
        "Investment",
        "Gift",
        "Rental",
        "Other"
    )

    var selectedCategory by remember(type) {
        mutableStateOf(if (type == TransactionType.EXPENSE) expenseCategories[0] else incomeCategories[0])
    }
    var customCategory by remember { mutableStateOf("") }

    var amountText by remember { mutableStateOf("") }
    var currency by remember(defaultCurrency) { mutableStateOf(defaultCurrency) }

    var currencyMenuExpanded by remember { mutableStateOf(false) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    val amount = amountText
        .replace(",", "")
        .toDoubleOrNull()

    val finalCategory = if (selectedCategory == "Other") customCategory.trim() else selectedCategory

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Add Transaction",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Transaction Type",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = type == TransactionType.EXPENSE,
                    onClick = { type = TransactionType.EXPENSE }
                )
                Text("Expense")

                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = type == TransactionType.INCOME,
                    onClick = { type = TransactionType.INCOME }
                )
                Text("Income")
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title (e.g. Dinner, Salary)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Category")

            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryMenuExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false }
                ) {
                    val categories = if (type == TransactionType.EXPENSE) expenseCategories else incomeCategories
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

            if (selectedCategory == "Other") {
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Custom Category Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Text("Transaction Currency (default: $defaultCurrency)")

            ExposedDropdownMenuBox(
                expanded = currencyMenuExpanded,
                onExpandedChange = {
                    currencyMenuExpanded = !currencyMenuExpanded
                }
            ) {
                OutlinedTextField(
                    value = currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = currencyMenuExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = currencyMenuExpanded,
                    onDismissRequest = {
                        currencyMenuExpanded = false
                    }
                ) {
                    listOf("INR", "USD", "EUR", "GBP", "JPY").forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                currency = code
                                currencyMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        finalCategory.isNotBlank() &&
                        amount != null &&
                        amount > 0.0 &&
                        amount.isFinite()
                    ) {
                        onAddTransaction(
                            title.trim(),
                            finalCategory,
                            amount,
                            currency,
                            type
                        )
                    }
                },
                enabled = title.isNotBlank() &&
                        finalCategory.isNotBlank() &&
                        amount != null &&
                        amount > 0.0 &&
                        amount.isFinite(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Transaction")
            }
        }
    }
}

fun formatAmount(
    amount: Double,
    currencyCode: String = "INR"
): String {
    return try {
        val currency = Currency.getInstance(currencyCode)
        val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            this.currency = currency
        }
        formatter.format(amount)
    } catch (e: Exception) {
        "$currencyCode %.2f".format(amount)
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
