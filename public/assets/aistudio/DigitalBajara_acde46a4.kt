package `in`.sandeep.odiabhasa.ganita

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.sandeep.odiabhasa.OdiaFont
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

enum class BajaraMode { NORMAL, LIST, HISTORY, SMART }
enum class UnitCategory { WEIGHT, VOLUME, COUNT }
enum class ActiveInput { PRICE, QUANTITY, DISCOUNT, CASH, SMART_KNOWN_QTY, SMART_KNOWN_PRICE, SMART_DISCOUNT, SMART_TARGET_QTY, SMART_TARGET_PRICE }

data class ShopUnit(val name: String, val category: UnitCategory, val factorToBase: Double)

data class BillItem(
    val name: String,
    val price: Double,
    val baseUnit: ShopUnit,
    val quantity: Double,
    val targetUnit: ShopUnit,
    val discountPercent: Double,
    val total: Double
)

data class SavedBill(val date: String, val total: Double, val itemsDesc: List<String>)

val shopUnits = listOf(
    ShopUnit("କିଲୋଗ୍ରାମ୍ (କେଜି)", UnitCategory.WEIGHT, 1000.0),
    ShopUnit("ଗ୍ରାମ୍", UnitCategory.WEIGHT, 1.0),
    ShopUnit("କ୍ୱିଣ୍ଟାଲ୍", UnitCategory.WEIGHT, 100000.0),
    ShopUnit("ଲିଟର", UnitCategory.VOLUME, 1000.0),
    ShopUnit("ମିଲିଲିଟର", UnitCategory.VOLUME, 1.0),
    ShopUnit("ପିସ୍ / ଟା", UnitCategory.COUNT, 1.0),
    ShopUnit("ଦର୍ଜନ", UnitCategory.COUNT, 12.0)
)

fun formatStringToOdia(value: String): String {
    return value.map { char ->
        when (char) {
            '0' -> '୦'; '1' -> '୧'; '2' -> '୨'; '3' -> '୩'; '4' -> '୪'
            '5' -> '୫'; '6' -> '୬'; '7' -> '୭'; '8' -> '୮'; '9' -> '୯'
            else -> char
        }
    }.joinToString("")
}

fun formatToOdiaNumbers(value: Double): String {
    val df = DecimalFormat("#.####")
    return formatStringToOdia(df.format(value))
}

fun getCurrentOdiaDate(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault())
    return formatStringToOdia(sdf.format(Date()))
}

fun cleanOdiaUnit(name: String): String {
    return name.substringBefore(" (").substringBefore(" /").trim()
}

// ଡିଜିଟାଲ ବଜାର ଆଇଡିଆ
fun getCalculationIdea(
    p: Double, q: Double, baseUnit: ShopUnit, targetUnit: ShopUnit,
    d: Double, baseCost: Double, discAmt: Double, normalTotalPrice: Double
): String {
    val bu = cleanOdiaUnit(baseUnit.name)
    val tu = cleanOdiaUnit(targetUnit.name)
    val ratio = baseUnit.factorToBase / targetUnit.factorToBase

    val calcStep = if (bu == tu) {
        if (q == 1.0) {
            "୧ $bu ର ଦାମ୍ ହେଉଛି ₹${formatToOdiaNumbers(p)} ।\nତୁମେ ନେଲ ୧ $tu, ତେଣୁ ଦାମ୍ ହେବ ₹${formatToOdiaNumbers(baseCost)} ।"
        } else {
            "୧ $bu ର ଦାମ୍ ହେଉଛି ₹${formatToOdiaNumbers(p)} ।\n" +
                    "ତୁମେ ନେଲ ${formatToOdiaNumbers(q)} $tu ।\n\n" +
                    "ତେଣୁ ମୋଟ ଦାମ୍ ହେବ:\n${formatToOdiaNumbers(q)} × ₹${formatToOdiaNumbers(p)} = ₹${formatToOdiaNumbers(baseCost)} ।"
        }
    } else if (ratio > 1) {
        val unitPrice = p / ratio
        "୧ $bu ବା ${formatToOdiaNumbers(ratio)} $tu ର ଦାମ୍ ହେଉଛି ₹${formatToOdiaNumbers(p)} ।\n" +
                "ସେହି ଅନୁସାରେ ୧ $tu ର ଦାମ୍ ହେବ: ₹${formatToOdiaNumbers(p)} ÷ ${formatToOdiaNumbers(ratio)} = ₹${formatToOdiaNumbers(unitPrice)} ।\n\n" +
                "ତୁମେ ନେଲ ${formatToOdiaNumbers(q)} $tu ।\n" +
                "ତେଣୁ ମୋଟ ଦାମ୍ ହେବ:\n${formatToOdiaNumbers(q)} × ₹${formatToOdiaNumbers(unitPrice)} = ₹${formatToOdiaNumbers(baseCost)} ।"
    } else {
        val unitPrice = p * (targetUnit.factorToBase / baseUnit.factorToBase)
        "୧ $bu ର ଦାମ୍ ହେଉଛି ₹${formatToOdiaNumbers(p)} ।\n" +
                "ସେହି ଅନୁସାରେ ୧ $tu ର ଦାମ୍ ହେବ: ₹${formatToOdiaNumbers(unitPrice)} ।\n\n" +
                "ତୁମେ ନେଲ ${formatToOdiaNumbers(q)} $tu ।\n" +
                "ତେଣୁ ମୋଟ ଦାମ୍ ହେବ:\n${formatToOdiaNumbers(q)} × ₹${formatToOdiaNumbers(unitPrice)} = ₹${formatToOdiaNumbers(baseCost)} ।"
    }

    val discStep = if (d > 0) {
        "\n\nଏହା ଉପରେ ଦୋକାନୀ ଦେଲେ ${formatToOdiaNumbers(d)}% ରିହାତି (ଶତକଡା ଛାଡ଼) ।\n" +
                "${formatToOdiaNumbers(d)}% ମାନେ ହେଉଛି ପ୍ରତି ୧୦୦ ଟଙ୍କାରେ ${formatToOdiaNumbers(d)} ଟଙ୍କା ଛାଡ଼ ।\n" +
                "ଶତକଡ଼ା ଛାଡ଼ କେମିତି ବାହାର କରିବା? \n(ମୋଟ ଦାମ୍ × ରିହାତି) ÷ ୧୦୦\n\n" +
                "ତେଣୁ ମୋଟ ₹${formatToOdiaNumbers(baseCost)} ଉପରେ ଛାଡ଼ ହେବ:\n" +
                "(₹${formatToOdiaNumbers(baseCost)} × ${formatToOdiaNumbers(d)}) ÷ ୧୦୦ = ₹${formatToOdiaNumbers(discAmt)} ।\n\n" +
                "ଶେଷରେ ତୁମକୁ ଦେବାକୁ ପଡିବ:\n₹${formatToOdiaNumbers(baseCost)} - ₹${formatToOdiaNumbers(discAmt)} = ₹${formatToOdiaNumbers(normalTotalPrice)} ।"
    } else {
        "\n\nଏଥିରେ କୌଣସି ରିହାତି ବା ଛାଡ଼ ନାହିଁ ।\nତେଣୁ ତୁମକୁ ମୋଟ ଦେବାକୁ ପଡିବ ₹${formatToOdiaNumbers(normalTotalPrice)} ।"
    }

    return "ଦେଖ ବାବୁ/ମା...\n$calcStep$discStep"
}

// ସ୍ମାର୍ଟ ହିସାବ ଆଇଡିଆ ୧ (ପରିମାଣ ଦେଇ ଟଙ୍କା ଖୋଜିବା)
fun getSmartIdea1(p1: Double, q1: Double, knownUnit: ShopUnit, tQ: Double, targetUnit: ShopUnit, d: Double, basePrice: Double, discAmt: Double, finalPrice: Double): String {
    val u1 = cleanOdiaUnit(knownUnit.name)
    val u2 = cleanOdiaUnit(targetUnit.name)

    val ratio = knownUnit.factorToBase / targetUnit.factorToBase
    val q1InU2 = q1 * ratio
    val unitPrice = p1 / q1InU2

    var explanation = "ଦେଖ ବାବୁ/ମା...\nଗଣିତରେ ଏହାକୁ 'ଐକିକ ନିୟମ' କୁହାଯାଏ ।\n\n"

    if (u1 != u2) {
        explanation += "ଆମେ ଜାଣୁ ଯେ ୧ $u1 ସମାନ ${formatToOdiaNumbers(ratio)} $u2 ।\n" +
                "ତେଣୁ ${formatToOdiaNumbers(q1)} $u1 ହେବ ${formatToOdiaNumbers(q1InU2)} $u2 ।\n\n" +
                "ଯଦି ${formatToOdiaNumbers(q1InU2)} $u2 ର ଦାମ୍ ହେଲା ₹${formatToOdiaNumbers(p1)}...\n"
    } else {
        explanation += "ଯଦି ${formatToOdiaNumbers(q1)} $u2 ର ଦାମ୍ ହେଲା ₹${formatToOdiaNumbers(p1)}...\n"
    }

    if (q1InU2 != 1.0) {
        explanation += "ତେବେ ୧ $u2 ର ଦାମ୍ ହେବ: ₹${formatToOdiaNumbers(p1)} ÷ ${formatToOdiaNumbers(q1InU2)} = ₹${formatToOdiaNumbers(unitPrice)} ।\n\n"
    } else {
        explanation += "ଏଠାରେ ୧ $u2 ର ଦାମ୍ ହେଉଛି ₹${formatToOdiaNumbers(unitPrice)} ।\n\n"
    }

    explanation += "ସେହି ଅନୁପାତରେ, ତୁମେ ମାଗିଥିବା ${formatToOdiaNumbers(tQ)} $u2 ର ପ୍ରକୃତ ଦାମ୍ ହେବ:\n" +
            "${formatToOdiaNumbers(tQ)} × ₹${formatToOdiaNumbers(unitPrice)} = ₹${formatToOdiaNumbers(basePrice)} ।\n"

    if (d > 0) {
        explanation += "\nଏହା ଉପରେ ${formatToOdiaNumbers(d)}% ରିହାତି ଅଛି ।\n" +
                "ଛାଡ଼ ଟଙ୍କା = (ମୋଟ ଦାମ୍ × ରିହାତି) ÷ ୧୦୦\n" +
                "ମାନେ (₹${formatToOdiaNumbers(basePrice)} × ${formatToOdiaNumbers(d)}) ÷ ୧୦୦ = ₹${formatToOdiaNumbers(discAmt)} ।\n\n" +
                "ତେଣୁ ଶେଷରେ ତୁମକୁ ଦେବାକୁ ପଡିବ:\n₹${formatToOdiaNumbers(basePrice)} - ₹${formatToOdiaNumbers(discAmt)} = ₹${formatToOdiaNumbers(finalPrice)} ।"
    } else {
        explanation += "\nଏଥିରେ ରିହାତି ନାହିଁ, ତେଣୁ ମୋଟ ଦାମ୍ ₹${formatToOdiaNumbers(finalPrice)} ଦେବାକୁ ହେବ ।"
    }

    return explanation
}

// ସ୍ମାର୍ଟ ହିସାବ ଆଇଡିଆ ୨ (ଟଙ୍କା ଦେଇ ପରିମାଣ ଖୋଜିବା)
fun getSmartIdea2(p1: Double, q1: Double, knownUnit: ShopUnit, tP: Double, d: Double, effectiveP1: Double, resultStr: String): String {
    val u1 = cleanOdiaUnit(knownUnit.name)
    var explanation = "ଦେଖ ବାବୁ/ମା...\n"

    if (d > 0) {
        val discAmt = p1 * (d / 100.0)
        explanation += "ପ୍ରଥମେ ଆମେ ରିହାତି ପାଇଁ ଦାମ୍ କମାଇବା ।\n" +
                "ଦୋକାନୀ ₹${formatToOdiaNumbers(p1)} ଉପରେ ${formatToOdiaNumbers(d)}% ଛାଡ଼ ଦେଉଛନ୍ତି ।\n" +
                "ଛାଡ଼ ଟଙ୍କା = (₹${formatToOdiaNumbers(p1)} × ${formatToOdiaNumbers(d)}) ÷ ୧୦୦ = ₹${formatToOdiaNumbers(discAmt)} ।\n\n" +
                "ତେଣୁ ${formatToOdiaNumbers(q1)} $u1 ର ନୂଆ ଦାମ୍ ହେଲା:\n" +
                "₹${formatToOdiaNumbers(p1)} - ₹${formatToOdiaNumbers(discAmt)} = ₹${formatToOdiaNumbers(effectiveP1)} ।\n\n"
    } else {
        explanation += "ଯଦି ₹${formatToOdiaNumbers(p1)} ରେ ମିଳୁଛି ${formatToOdiaNumbers(q1)} $u1...\n\n"
    }

    val qtyPerRupee = q1 / effectiveP1
    val totalUnits = tP * qtyPerRupee

    explanation += "ଏବେ 'ଐକିକ ନିୟମ' ପକାଇବା...\n"
    if (effectiveP1 != 1.0) {
        explanation += "୧ ଟଙ୍କାରେ ମିଳିବ: ${formatToOdiaNumbers(q1)} ÷ ${formatToOdiaNumbers(effectiveP1)} = ${formatToOdiaNumbers(qtyPerRupee)} $u1 ।\n\n"
    } else {
        explanation += "୧ ଟଙ୍କାରେ ମିଳୁଛି ${formatToOdiaNumbers(qtyPerRupee)} $u1 ।\n\n"
    }

    explanation += "ଆମ ପାଖରେ ଅଛି ମୋଟ ₹${formatToOdiaNumbers(tP)} ।\n" +
            "ତେଣୁ ଆମକୁ ମିଳିବ: ₹${formatToOdiaNumbers(tP)} × ${formatToOdiaNumbers(qtyPerRupee)} = ${formatToOdiaNumbers(totalUnits)} $u1 ।\n\n"

    if (resultStr.contains("କେଜି") || resultStr.contains("ଲିଟର")) {
        explanation += "ଯାହାକି ଠିକ୍ ଭାବେ ସଜାଇଲେ $resultStr ସହିତ ସମାନ ଅଟେ ।"
    } else {
        explanation += "ସେହି ହିସାବରେ ଆମକୁ ଠିକ୍ $resultStr ମିଳିବ ।"
    }

    return explanation
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalBajaraScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var currentMode by remember { mutableStateOf(BajaraMode.NORMAL) }
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var activeInput by remember { mutableStateOf(ActiveInput.PRICE) }

    // State for Normal/List
    var productName by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("") }
    var discountInput by remember { mutableStateOf("") }
    var cashGivenInput by remember { mutableStateOf("") }
    var baseUnit by remember { mutableStateOf(shopUnits[0]) }
    var targetUnit by remember { mutableStateOf(shopUnits[1]) }
    val billItems = remember { mutableStateListOf<BillItem>() }

    // State for Smart Mode
    var smartKnownQtyInput by remember { mutableStateOf("") }
    var smartKnownPriceInput by remember { mutableStateOf("") }
    var smartDiscountInput by remember { mutableStateOf("") } // ସ୍ମାର୍ଟ ହିସାବ ରିହାତି
    var smartKnownUnit by remember { mutableStateOf(shopUnits[1]) }

    var smartTargetQtyInput by remember { mutableStateOf("") }
    var smartTargetUnit by remember { mutableStateOf(shopUnits[0]) }
    var smartTargetPriceInput by remember { mutableStateOf("") }

    var showBaseUnitSheet by remember { mutableStateOf(false) }
    var showTargetUnitSheet by remember { mutableStateOf(false) }
    var showSmartKnownUnitSheet by remember { mutableStateOf(false) }
    var showSmartTargetUnitSheet by remember { mutableStateOf(false) }

    var showIdeaDialog by remember { mutableStateOf(false) }
    var ideaMessage by remember { mutableStateOf("") }

    val filteredTargetUnits = remember(baseUnit) { shopUnits.filter { it.category == baseUnit.category } }
    LaunchedEffect(baseUnit) { if (targetUnit.category != baseUnit.category) targetUnit = filteredTargetUnits.first() }

    val filteredSmartTargetUnits = remember(smartKnownUnit) { shopUnits.filter { it.category == smartKnownUnit.category } }
    LaunchedEffect(smartKnownUnit) { if (smartTargetUnit.category != smartKnownUnit.category) smartTargetUnit = filteredSmartTargetUnits.first() }

    BackHandler {
        if (isKeyboardVisible) {
            isKeyboardVisible = false
            focusManager.clearFocus()
        } else {
            when (currentMode) {
                BajaraMode.HISTORY -> currentMode = BajaraMode.LIST
                BajaraMode.LIST, BajaraMode.SMART -> currentMode = BajaraMode.NORMAL
                BajaraMode.NORMAL -> onBack()
            }
        }
    }

    val normalTotalPrice = remember(priceInput, quantityInput, discountInput, baseUnit, targetUnit) {
        val p = priceInput.toDoubleOrNull() ?: 0.0
        val q = quantityInput.toDoubleOrNull() ?: 0.0
        val d = discountInput.toDoubleOrNull() ?: 0.0
        if (p > 0 && q > 0) {
            val baseCost = (p / baseUnit.factorToBase) * (q * targetUnit.factorToBase)
            val discountAmount = baseCost * (d / 100.0)
            baseCost - discountAmount
        } else 0.0
    }
    val grandTotal = billItems.sumOf { it.total }
    val cashGiven = cashGivenInput.toDoubleOrNull() ?: 0.0
    val returnAmount = cashGiven - grandTotal

    fun handleKeyPress(key: String) {
        val currentStr = when (activeInput) {
            ActiveInput.PRICE -> priceInput
            ActiveInput.QUANTITY -> quantityInput
            ActiveInput.DISCOUNT -> discountInput
            ActiveInput.CASH -> cashGivenInput
            ActiveInput.SMART_KNOWN_QTY -> smartKnownQtyInput
            ActiveInput.SMART_KNOWN_PRICE -> smartKnownPriceInput
            ActiveInput.SMART_DISCOUNT -> smartDiscountInput
            ActiveInput.SMART_TARGET_QTY -> smartTargetQtyInput
            ActiveInput.SMART_TARGET_PRICE -> smartTargetPriceInput
        }
        val newStr = when (key) {
            "BACK" -> if (currentStr.isNotEmpty()) currentStr.dropLast(1) else ""
            "." -> if (!currentStr.contains(".")) currentStr + "." else currentStr
            else -> currentStr + key
        }
        when (activeInput) {
            ActiveInput.PRICE -> priceInput = newStr
            ActiveInput.QUANTITY -> quantityInput = newStr
            ActiveInput.DISCOUNT -> discountInput = newStr
            ActiveInput.CASH -> cashGivenInput = newStr
            ActiveInput.SMART_KNOWN_QTY -> smartKnownQtyInput = newStr
            ActiveInput.SMART_KNOWN_PRICE -> smartKnownPriceInput = newStr
            ActiveInput.SMART_DISCOUNT -> smartDiscountInput = newStr
            ActiveInput.SMART_TARGET_QTY -> smartTargetQtyInput = newStr
            ActiveInput.SMART_TARGET_PRICE -> smartTargetPriceInput = newStr
        }
    }

    fun saveBill() {
        if (billItems.isEmpty()) return
        val prefs = context.getSharedPreferences("digital_bajara_history", Context.MODE_PRIVATE)
        val existingJson = prefs.getString("history", "[]") ?: "[]"
        val jsonArray = JSONArray(existingJson)

        val newBill = JSONObject()
        newBill.put("date", getCurrentOdiaDate())
        newBill.put("total", grandTotal)

        val itemsArray = JSONArray()
        billItems.forEach { item ->
            val itemName = item.name.ifEmpty { "ଅଜଣା ଦ୍ରବ୍ୟ" }
            val discText = if (item.discountPercent > 0) " (${formatToOdiaNumbers(item.discountPercent)}% ରିହାତି)" else ""
            itemsArray.put("$itemName: ${formatToOdiaNumbers(item.quantity)} ${item.targetUnit.name} = ₹${formatToOdiaNumbers(item.total)}$discText")
        }
        newBill.put("items", itemsArray)
        jsonArray.put(newBill)

        prefs.edit().putString("history", jsonArray.toString()).apply()
        billItems.clear()
        productName = ""
        priceInput = ""
        quantityInput = ""
        discountInput = ""
        cashGivenInput = ""
        isKeyboardVisible = false
        Toast.makeText(context, "ଫର୍ଦ୍ଦ ସେଭ୍ ହୋଇଗଲା!", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        containerColor = Color(0xFFE0F2F1),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentMode) {
                            BajaraMode.NORMAL -> "ଡିଜିଟାଲ ବଜାର"
                            BajaraMode.LIST -> "ଫର୍ଦ୍ଦ ତିଆରି"
                            BajaraMode.HISTORY -> "ପୁରୁଣା ଫର୍ଦ୍ଦ"
                            BajaraMode.SMART -> "ସ୍ମାର୍ଟ ହିସାବ"
                        },
                        fontFamily = OdiaFont, fontWeight = FontWeight.Black, color = Color.Black
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(44.dp)
                            .border(2.dp, Color.Black, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable {
                                isKeyboardVisible = false
                                focusManager.clearFocus()
                                when (currentMode) {
                                    BajaraMode.HISTORY -> currentMode = BajaraMode.LIST
                                    BajaraMode.LIST, BajaraMode.SMART -> currentMode = BajaraMode.NORMAL
                                    BajaraMode.NORMAL -> onBack()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ପଛକୁ ଫେରନ୍ତୁ", tint = Color.Black) }
                },
                actions = {
                    if (currentMode == BajaraMode.NORMAL) {
                        Button(
                            onClick = { currentMode = BajaraMode.LIST; isKeyboardVisible = false; focusManager.clearFocus() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.padding(end = 8.dp)
                        ) { Text("ଫର୍ଦ୍ଦ ତିଆରି", fontFamily = OdiaFont, color = Color.White, fontWeight = FontWeight.Bold) }
                    } else if (currentMode == BajaraMode.LIST) {
                        IconButton(onClick = { currentMode = BajaraMode.HISTORY; isKeyboardVisible = false; focusManager.clearFocus() }) {
                            Icon(Icons.Default.History, contentDescription = "ପୁରୁଣା ଫର୍ଦ୍ଦ", tint = Color.Black)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus(); isKeyboardVisible = false }) },
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (currentMode == BajaraMode.NORMAL || currentMode == BajaraMode.LIST) {
                    item {
                        Button(
                            onClick = { currentMode = BajaraMode.SMART; isKeyboardVisible = false; focusManager.clearFocus() },
                            modifier = Modifier.fillMaxWidth().height(65.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF9C4)),
                            border = BorderStroke(2.dp, Color(0xFFF57F17)),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("ସ୍ମାର୍ଟ ହିସାବ ଖୋଲନ୍ତୁ", fontFamily = OdiaFont, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                        }
                    }
                }

                if (currentMode == BajaraMode.HISTORY) {
                    item { HistorySection(context) }
                } else if (currentMode == BajaraMode.SMART) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("ଯଦି (ଦୋକାନୀଙ୍କ ଦର):", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 16.sp)

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FakeInputField(
                                        value = smartKnownQtyInput, placeholder = "ପରିମାଣ", isActive = activeInput == ActiveInput.SMART_KNOWN_QTY && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.SMART_KNOWN_QTY; isKeyboardVisible = true }, modifier = Modifier.weight(0.4f)
                                    )
                                    UnitSelectorButton(text = smartKnownUnit.name, onClick = { showSmartKnownUnitSheet = true; isKeyboardVisible = false; focusManager.clearFocus() }, modifier = Modifier.weight(0.6f))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("ର ମୂଲ୍ୟ =", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(0.3f))
                                    FakeInputField(
                                        value = smartKnownPriceInput, placeholder = "ଟଙ୍କା (₹)", isActive = activeInput == ActiveInput.SMART_KNOWN_PRICE && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.SMART_KNOWN_PRICE; isKeyboardVisible = true }, modifier = Modifier.weight(0.7f)
                                    )
                                }

                                // ସ୍ମାର୍ଟ ହିସାବରେ ନୂଆ ରିହାତି ଘର
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("ରିହାତି ଛାଡ଼:", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63), fontSize = 16.sp, modifier = Modifier.weight(0.3f))
                                    FakeInputField(
                                        value = smartDiscountInput, placeholder = "ଶତକଡା (%)", isActive = activeInput == ActiveInput.SMART_DISCOUNT && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.SMART_DISCOUNT; isKeyboardVisible = true }, modifier = Modifier.weight(0.7f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("ତେବେ ହିସାବ କରନ୍ତୁ:", fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFF57F17))
                                HorizontalDivider(color = Color.Black, thickness = 1.dp)

                                // Case 1: Value of X quantity
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("୧. କେତେ ପରିମାଣ ନେବେ?", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C), fontSize = 16.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        FakeInputField(
                                            value = smartTargetQtyInput, placeholder = "ପରିମାଣ", isActive = activeInput == ActiveInput.SMART_TARGET_QTY && isKeyboardVisible,
                                            onClick = { focusManager.clearFocus(); activeInput = ActiveInput.SMART_TARGET_QTY; isKeyboardVisible = true }, modifier = Modifier.weight(0.4f)
                                        )
                                        UnitSelectorButton(text = smartTargetUnit.name, onClick = { showSmartTargetUnitSheet = true; isKeyboardVisible = false; focusManager.clearFocus() }, modifier = Modifier.weight(0.6f))
                                    }

                                    val p1 = smartKnownPriceInput.toDoubleOrNull() ?: 0.0
                                    val q1 = smartKnownQtyInput.toDoubleOrNull() ?: 0.0
                                    val tQ = smartTargetQtyInput.toDoubleOrNull() ?: 0.0
                                    val d = smartDiscountInput.toDoubleOrNull() ?: 0.0

                                    var basePrice = 0.0
                                    var discAmt = 0.0
                                    var finalPrice = 0.0

                                    if (p1 > 0 && q1 > 0 && tQ > 0) {
                                        val unitP = p1 / (q1 * smartKnownUnit.factorToBase)
                                        basePrice = unitP * (tQ * smartTargetUnit.factorToBase)
                                        discAmt = basePrice * (d / 100.0)
                                        finalPrice = basePrice - discAmt
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("ମୂଲ୍ୟ ହେବ: ₹${formatToOdiaNumbers(finalPrice)}", fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFFD32F2F))

                                        IconButton(
                                            onClick = {
                                                if (p1 > 0 && q1 > 0 && tQ > 0) {
                                                    ideaMessage = getSmartIdea1(p1, q1, smartKnownUnit, tQ, smartTargetUnit, d, basePrice, discAmt, finalPrice)
                                                    showIdeaDialog = true
                                                }
                                            },
                                            modifier = Modifier.background(Color.White, CircleShape).border(1.dp, Color.Black, CircleShape).size(40.dp)
                                        ) { Icon(Icons.Default.Lightbulb, contentDescription = "ଆଇଡିଆ", tint = Color(0xFFF57F17)) }
                                    }
                                }

                                HorizontalDivider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(horizontal = 8.dp))

                                // Case 2: Quantity for Y amount
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("୨. କେତେ ଟଙ୍କାର ନେବେ?", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 16.sp)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("ମୋଟ ଟଙ୍କା:", fontFamily = OdiaFont, fontSize = 16.sp, modifier = Modifier.weight(0.3f))
                                        FakeInputField(
                                            value = smartTargetPriceInput, placeholder = "ଟଙ୍କା (₹)", isActive = activeInput == ActiveInput.SMART_TARGET_PRICE && isKeyboardVisible,
                                            onClick = { focusManager.clearFocus(); activeInput = ActiveInput.SMART_TARGET_PRICE; isKeyboardVisible = true }, modifier = Modifier.weight(0.7f)
                                        )
                                    }

                                    val tP = smartTargetPriceInput.toDoubleOrNull() ?: 0.0
                                    val p1 = smartKnownPriceInput.toDoubleOrNull() ?: 0.0
                                    val q1 = smartKnownQtyInput.toDoubleOrNull() ?: 0.0
                                    val d = smartDiscountInput.toDoubleOrNull() ?: 0.0

                                    var resultStr = "୦"
                                    var effectiveP1 = p1

                                    if (p1 > 0 && q1 > 0 && tP > 0) {
                                        // ଆଗ ରିହାତି ହିସାବ କରି ନୂଆ ଦାମ୍ ବାହାର କରାଯିବ
                                        effectiveP1 = p1 - (p1 * (d / 100.0))

                                        if (effectiveP1 > 0) {
                                            val basePricePerUnit = effectiveP1 / (q1 * smartKnownUnit.factorToBase)
                                            val totalBaseUnits = tP / basePricePerUnit

                                            if (smartKnownUnit.category == UnitCategory.WEIGHT) {
                                                if (totalBaseUnits >= 1000) {
                                                    val kg = (totalBaseUnits / 1000).toInt()
                                                    val g = totalBaseUnits % 1000
                                                    resultStr = if (g > 0) "${formatToOdiaNumbers(kg.toDouble())} କେଜି ${formatToOdiaNumbers(g)} ଗ୍ରାମ୍" else "${formatToOdiaNumbers(kg.toDouble())} କେଜି"
                                                } else {
                                                    resultStr = "${formatToOdiaNumbers(totalBaseUnits)} ଗ୍ରାମ୍"
                                                }
                                            } else if (smartKnownUnit.category == UnitCategory.VOLUME) {
                                                if (totalBaseUnits >= 1000) {
                                                    val l = (totalBaseUnits / 1000).toInt()
                                                    val ml = totalBaseUnits % 1000
                                                    resultStr = if (ml > 0) "${formatToOdiaNumbers(l.toDouble())} ଲିଟର ${formatToOdiaNumbers(ml)} ମିଲିଲିଟର" else "${formatToOdiaNumbers(l.toDouble())} ଲିଟର"
                                                } else {
                                                    resultStr = "${formatToOdiaNumbers(totalBaseUnits)} ମିଲିଲିଟର"
                                                }
                                            } else {
                                                val cleanName = cleanOdiaUnit(smartKnownUnit.name)
                                                resultStr = "${formatToOdiaNumbers(totalBaseUnits)} $cleanName"
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("ମିଳିବ: $resultStr", fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF388E3C), modifier = Modifier.weight(1f))

                                        IconButton(
                                            onClick = {
                                                if (p1 > 0 && q1 > 0 && tP > 0) {
                                                    ideaMessage = getSmartIdea2(p1, q1, smartKnownUnit, tP, d, effectiveP1, resultStr)
                                                    showIdeaDialog = true
                                                }
                                            },
                                            modifier = Modifier.background(Color.White, CircleShape).border(1.dp, Color.Black, CircleShape).size(40.dp)
                                        ) { Icon(Icons.Default.Lightbulb, contentDescription = "ଆଇଡିଆ", tint = Color(0xFFF57F17)) }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                if (currentMode == BajaraMode.LIST) {
                                    OutlinedTextField(
                                        value = productName,
                                        onValueChange = { productName = it },
                                        label = { Text("ଦ୍ରବ୍ୟର ନାମ (ଉଦାହରଣ - ଆଳୁ)", fontFamily = OdiaFont) },
                                        modifier = Modifier.fillMaxWidth().onFocusChanged { if (it.isFocused) isKeyboardVisible = false },
                                        shape = RoundedCornerShape(12.dp),
                                        textStyle = LocalTextStyle.current.copy(fontFamily = OdiaFont, fontSize = 18.sp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("ଦର:", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C), fontSize = 16.sp, modifier = Modifier.weight(0.3f))
                                    FakeInputField(
                                        value = priceInput, placeholder = "ଟଙ୍କା (₹)", isActive = activeInput == ActiveInput.PRICE && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.PRICE; isKeyboardVisible = true }, modifier = Modifier.weight(0.35f)
                                    )
                                    UnitSelectorButton(text = baseUnit.name, onClick = { showBaseUnitSheet = true; isKeyboardVisible = false; focusManager.clearFocus() }, modifier = Modifier.weight(0.35f))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("ପରିମାଣ:", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFFF57C00), fontSize = 16.sp, modifier = Modifier.weight(0.3f))
                                    FakeInputField(
                                        value = quantityInput, placeholder = "କେତେ", isActive = activeInput == ActiveInput.QUANTITY && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.QUANTITY; isKeyboardVisible = true }, modifier = Modifier.weight(0.35f)
                                    )
                                    UnitSelectorButton(text = targetUnit.name, onClick = { showTargetUnitSheet = true; isKeyboardVisible = false; focusManager.clearFocus() }, modifier = Modifier.weight(0.35f))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("ରିହାତି ଛାଡ଼:", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFFE91E63), fontSize = 16.sp, modifier = Modifier.weight(0.3f))
                                    FakeInputField(
                                        value = discountInput, placeholder = "ଶତକଡା (%)", isActive = activeInput == ActiveInput.DISCOUNT && isKeyboardVisible,
                                        onClick = { focusManager.clearFocus(); activeInput = ActiveInput.DISCOUNT; isKeyboardVisible = true }, modifier = Modifier.weight(0.7f)
                                    )
                                }

                                if (currentMode == BajaraMode.LIST) {
                                    Button(
                                        onClick = {
                                            val p = priceInput.toDoubleOrNull() ?: 0.0
                                            val q = quantityInput.toDoubleOrNull() ?: 0.0
                                            val d = discountInput.toDoubleOrNull() ?: 0.0
                                            if (p > 0 && q > 0) {
                                                val baseCost = (p / baseUnit.factorToBase) * (q * targetUnit.factorToBase)
                                                val finalCost = baseCost - (baseCost * (d / 100.0))
                                                billItems.add(BillItem(productName, p, baseUnit, q, targetUnit, d, finalCost))
                                                priceInput = ""; quantityInput = ""; productName = ""; discountInput = ""
                                                focusManager.clearFocus()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                                        border = BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ଫର୍ଦ୍ଦରେ ଯୋଡ଼ନ୍ତୁ", fontFamily = OdiaFont, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    if (currentMode == BajaraMode.NORMAL) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color(0xFF4CAF50)).border(3.dp, Color.Black, RoundedCornerShape(24.dp)).padding(20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text("ମୋଟ ମୂଲ୍ୟ", fontFamily = OdiaFont, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                        Text("₹ ${formatToOdiaNumbers(normalTotalPrice)}", fontFamily = OdiaFont, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black)
                                    }

                                    IconButton(
                                        onClick = {
                                            val p = priceInput.toDoubleOrNull() ?: 0.0
                                            val q = quantityInput.toDoubleOrNull() ?: 0.0
                                            val d = discountInput.toDoubleOrNull() ?: 0.0
                                            if (p > 0 && q > 0) {
                                                val baseCost = (p / baseUnit.factorToBase) * (q * targetUnit.factorToBase)
                                                val discAmt = baseCost * (d / 100.0)

                                                ideaMessage = getCalculationIdea(p, q, baseUnit, targetUnit, d, baseCost, discAmt, normalTotalPrice)
                                                showIdeaDialog = true
                                            }
                                        },
                                        modifier = Modifier.background(Color.White, CircleShape).size(50.dp)
                                    ) { Icon(Icons.Default.Lightbulb, contentDescription = "ଆଇଡିଆ", tint = Color(0xFFF57F17), modifier = Modifier.size(32.dp)) }
                                }
                            }
                        }
                    }

                    if (currentMode == BajaraMode.LIST && billItems.isNotEmpty()) {
                        item { Text("ଆପଣଙ୍କ ଫର୍ଦ୍ଦ (ବିଲ୍):", fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black) }
                        itemsIndexed(billItems) { index, item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color.Black, RoundedCornerShape(12.dp)).padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    val nameDisp = item.name.ifEmpty { "ଦ୍ରବ୍ୟ ${formatStringToOdia((index + 1).toString())}" }
                                    Text(nameDisp, fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF7E57C2))
                                    Text("${formatToOdiaNumbers(item.quantity)} ${item.targetUnit.name} (ଦର: ₹${formatToOdiaNumbers(item.price)}/${item.baseUnit.name})", fontFamily = OdiaFont, color = Color.Gray, fontSize = 14.sp)
                                    if (item.discountPercent > 0) {
                                        Text("ରିହାତି: ${formatToOdiaNumbers(item.discountPercent)}%", fontFamily = OdiaFont, color = Color(0xFFE91E63), fontSize = 14.sp)
                                    }
                                }
                                Text("₹${formatToOdiaNumbers(item.total)}", fontFamily = OdiaFont, fontWeight = FontWeight.Black, color = Color(0xFFD32F2F), fontSize = 18.sp)
                            }
                        }

                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color(0xFF4CAF50)).border(3.dp, Color.Black, RoundedCornerShape(24.dp)).padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("ସର୍ବମୋଟ ଟଙ୍କା", fontFamily = OdiaFont, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text("₹ ${formatToOdiaNumbers(grandTotal)}", fontFamily = OdiaFont, color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp),
                                border = BorderStroke(2.dp, Color.Black), modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("ହିସାବ କରନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        Text("ଦୋକାନୀଙ୍କୁ ଦେଲେ:", fontFamily = OdiaFont, color = Color(0xFF7E57C2), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        FakeInputField(
                                            value = cashGivenInput, placeholder = "ଟଙ୍କା ଦିଅନ୍ତୁ", isActive = activeInput == ActiveInput.CASH && isKeyboardVisible,
                                            onClick = {
                                                focusManager.clearFocus()
                                                activeInput = ActiveInput.CASH
                                                isKeyboardVisible = true
                                                coroutineScope.launch { delay(200); listState.animateScrollToItem(listState.layoutInfo.totalItemsCount) }
                                            }, modifier = Modifier.width(140.dp)
                                        )
                                    }

                                    HorizontalDivider(color = Color.Black, thickness = 1.dp)

                                    val (changeText, changeColor) = when {
                                        cashGivenInput.isEmpty() || cashGiven == 0.0 -> Pair("ଟଙ୍କା ଦିଅନ୍ତୁ...", Color.Gray)
                                        returnAmount > 0 -> Pair("ଆପଣ ₹${formatToOdiaNumbers(returnAmount)} ପାଇବେ", Color(0xFF388E3C))
                                        returnAmount < 0 -> Pair("ଆପଣ ଆଉ ₹${formatToOdiaNumbers(abs(returnAmount))} ଦେବେ", Color.Red)
                                        else -> Pair("ହିସାବ ବରାବର", Color(0xFF388E3C))
                                    }

                                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(changeText, fontFamily = OdiaFont, color = changeColor, fontWeight = FontWeight.Black, fontSize = 22.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { saveBill() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                border = BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth().height(60.dp)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ଫର୍ଦ୍ଦ ସେଭ୍ କରନ୍ତୁ", fontFamily = OdiaFont, color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = isKeyboardVisible, enter = slideInVertically(initialOffsetY = { it }), exit = slideOutVertically(targetOffsetY = { it })) {
                CustomOdiaKeyboard(onKeyPress = { handleKeyPress(it) }, onHide = { isKeyboardVisible = false })
            }
        }
    }

    if (showIdeaDialog) {
        AlertDialog(
            onDismissRequest = { showIdeaDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, tint = Color(0xFFF57F17), contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("କେମିତି ହେଲା?", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                }
            },
            text = { Text(ideaMessage, fontFamily = OdiaFont, fontSize = 18.sp, lineHeight = 26.sp, color = Color.Black) },
            confirmButton = {
                TextButton(onClick = { showIdeaDialog = false }) {
                    Text("ବୁଝିଗଲି!", fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF388E3C))
                }
            },
            containerColor = Color(0xFFFFF9C4)
        )
    }

    if (showBaseUnitSheet) {
        ModalBottomSheet(onDismissRequest = { showBaseUnitSheet = false }) {
            UnitSelectionList("ଏକକ ବାଛନ୍ତୁ (ଦର)", shopUnits) { baseUnit = it; showBaseUnitSheet = false }
        }
    }
    if (showTargetUnitSheet) {
        ModalBottomSheet(onDismissRequest = { showTargetUnitSheet = false }) {
            UnitSelectionList("ଏକକ ବାଛନ୍ତୁ (ପରିମାଣ)", filteredTargetUnits) { targetUnit = it; showTargetUnitSheet = false }
        }
    }
    if (showSmartKnownUnitSheet) {
        ModalBottomSheet(onDismissRequest = { showSmartKnownUnitSheet = false }) {
            UnitSelectionList("ଏକକ ବାଛନ୍ତୁ", shopUnits) { smartKnownUnit = it; showSmartKnownUnitSheet = false }
        }
    }
    if (showSmartTargetUnitSheet) {
        ModalBottomSheet(onDismissRequest = { showSmartTargetUnitSheet = false }) {
            UnitSelectionList("ଏକକ ବାଛନ୍ତୁ", filteredSmartTargetUnits) { smartTargetUnit = it; showSmartTargetUnitSheet = false }
        }
    }
}

@Composable
fun HistorySection(context: Context) {
    val prefs = context.getSharedPreferences("digital_bajara_history", Context.MODE_PRIVATE)
    var historyString by remember { mutableStateOf(prefs.getString("history", "[]") ?: "[]") }
    var showDialog by remember { mutableStateOf(false) }

    val savedBills = remember(historyString) {
        val list = mutableListOf<SavedBill>()
        try {
            val jsonArray = JSONArray(historyString)
            for (i in jsonArray.length() - 1 downTo 0) {
                val obj = jsonArray.getJSONObject(i)
                val itemsArray = obj.getJSONArray("items")
                val itemsList = mutableListOf<String>()
                for (j in 0 until itemsArray.length()) { itemsList.add(itemsArray.getString(j)) }
                list.add(SavedBill(obj.getString("date"), obj.getDouble("total"), itemsList))
            }
        } catch (e: Exception) { e.printStackTrace() }
        list
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("ଫର୍ଦ୍ଦ ଲିଭାନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold) },
            text = { Text("ଆପଣ ସବୁ ପୁରୁଣା ଫର୍ଦ୍ଦ ଲିଭାଇବାକୁ ଚାହୁଁଛନ୍ତି କି?", fontFamily = OdiaFont, fontSize = 18.sp) },
            confirmButton = {
                TextButton(onClick = {
                    prefs.edit().putString("history", "[]").apply()
                    historyString = "[]"
                    showDialog = false
                    Toast.makeText(context, "ସବୁ ଫର୍ଦ୍ଦ ଲିଭାଇ ଦିଆଗଲା!", Toast.LENGTH_SHORT).show()
                }) { Text("ହଁ", fontFamily = OdiaFont, color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("ନା", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            }
        )
    }

    if (savedBills.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("କୌଣସି ପୁରୁଣା ଫର୍ଦ୍ଦ ନାହିଁ ।", fontFamily = OdiaFont, fontSize = 20.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.End) {
            Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("ସବୁ ଲିଭାନ୍ତୁ", fontFamily = OdiaFont, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        savedBills.forEach { bill ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.Black), modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ତାରିଖ: ${bill.date}", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                        Text("ମୋଟ: ₹${formatToOdiaNumbers(bill.total)}", fontFamily = OdiaFont, fontWeight = FontWeight.Black, color = Color(0xFF388E3C), fontSize = 16.sp)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray)
                    bill.itemsDesc.forEach { itemDesc ->
                        Text("• $itemDesc", fontFamily = OdiaFont, fontSize = 16.sp, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FakeInputField(value: String, placeholder: String, isActive: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(52.dp)
            .background(if (isActive) Color(0xFFE3F2FD) else Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .border(if (isActive) 2.dp else 1.dp, if (isActive) Color.Blue else Color.Gray, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) Text(text = placeholder, fontFamily = OdiaFont, color = Color.Gray, fontSize = 14.sp)
        else Text(text = formatStringToOdia(value), fontFamily = OdiaFont, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1)
    }
}

@Composable
fun UnitSelectorButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(52.dp).background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color.Black, RoundedCornerShape(12.dp))
            .clickable { onClick() }.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 1, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun UnitSelectionList(title: String, units: List<ShopUnit>, onSelect: (ShopUnit) -> Unit) {
    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
        Text(title, fontFamily = OdiaFont, fontWeight = FontWeight.Black, fontSize = 22.sp, modifier = Modifier.padding(bottom = 16.dp))
        units.forEach { unit ->
            Text(unit.name, fontFamily = OdiaFont, fontSize = 18.sp, modifier = Modifier.fillMaxWidth().clickable { onSelect(unit) }.padding(vertical = 12.dp))
            HorizontalDivider()
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun CustomOdiaKeyboard(onKeyPress: (String) -> Unit, onHide: () -> Unit) {
    val keys = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf(".", "0", "BACK"))
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFCFD8DC)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Box(
                modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)).border(1.dp, Color.Black, RoundedCornerShape(8.dp)).clickable { onHide() }.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("କିବୋର୍ଡ ଲୁଚାନ୍ତୁ", fontFamily = OdiaFont, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "କିବୋର୍ଡ ଲୁଚାନ୍ତୁ", tint = Color.Black)
                }
            }
        }
        keys.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key -> KeyboardKey(key = key, modifier = Modifier.weight(1f), onClick = { onKeyPress(key) }) }
            }
        }
    }
}

@Composable
fun KeyboardKey(key: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offset by animateDpAsState(targetValue = if (isPressed) 4.dp else 0.dp, label = "press")

    Box(modifier = modifier.height(50.dp).clickable(interactionSource = interactionSource, indication = null, onClick = onClick)) {
        Box(modifier = Modifier.fillMaxSize().padding(top = 4.dp, start = 4.dp).background(Color.Black, RoundedCornerShape(12.dp)))
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 4.dp, end = 4.dp).offset(x = offset, y = offset)
                .border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                .background(if (key == "BACK") Color(0xFFFFCDD2) else Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (key == "BACK") Icon(Icons.Default.Backspace, contentDescription = "ଲିଭାନ୍ତୁ", tint = Color.Black)
            else Text(formatStringToOdia(key), fontFamily = OdiaFont, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
    }
}