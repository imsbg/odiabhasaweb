package `in`.sandeep.odiabhasa.ganita

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import `in`.sandeep.odiabhasa.ganita.utils.toLocaleNumerals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class WordProblem(
    val key: String,
    val questionTemplate: String,
    val solutionTemplate: String,
    val answer: Int,
    val numbers: List<Int>,
    val subTotal: String = ""
)

data class ProblemTemplate(
    val key: String,
    val questionTemplate: String,
    val solutionTemplate: String,
    val generator: () -> Triple<List<Int>, Int, String>
)

data class HistoryItem(
    val timestamp: String,
    val question: String,
    val userAnswer: String,
    val result: String
)

data class WordProblemUiState(
    val currentProblem: WordProblem? = null,
    val userAnswer: String = "",
    val isAnswerCorrect: Boolean? = null,
    val showSolution: Boolean = false,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0,
    val hintUsed: Boolean = false,
    val showHistory: Boolean = false,
    val historyList: List<HistoryItem> = emptyList()
)

class WordProblemViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(WordProblemUiState())
    val uiState: StateFlow<WordProblemUiState> = _uiState.asStateFlow()
    private var questionTemplates: MutableList<ProblemTemplate> = mutableListOf()

    init {
        loadTemplates()
        serveNextProblem(null)
    }

    private fun saveHistory(isCorrect: Boolean, context: Context, resolvedQuestionText: String, isSkipped: Boolean = false) {
        viewModelScope.launch {
            try {
                val problem = _uiState.value.currentProblem ?: return@launch
                val ganitaBigyanDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "GanitaBigyan").apply { mkdirs() }
                val file = File(ganitaBigyanDir, "word_problem_history.gba")

                val resultValue = if (isSkipped) "ଛାଡ଼ିଦିଆଯାଇଛି" else if (isCorrect) "ସଠିକ୍" else "ଭୁଲ୍"

                val userAnswerRaw = _uiState.value.userAnswer
                // Convert userAnswer to Odia using context (now works regardless of system locale)
                val userAnswerFormatted = if (userAnswerRaw.isEmpty()) "ଦିଆଯାଇନାହିଁ" else userAnswerRaw.toLocaleNumerals(context)

                val logEntry = buildString {
                    appendLine("[${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())}]")
                    appendLine("ପ୍ରଶ୍ନ: $resolvedQuestionText")
                    appendLine("ଆପଣଙ୍କର ଉତ୍ତର: $userAnswerFormatted")
                    appendLine("ସଠିକ୍ ଉତ୍ତର: ${problem.answer}")
                    appendLine("ଫଳାଫଳ: $resultValue")
                    appendLine("ସଙ୍କେତ ବ୍ୟବହୃତ: ${_uiState.value.hintUsed}")
                    appendLine("----END----")
                    appendLine()
                }
                file.appendText(logEntry)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val ganitaBigyanDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "GanitaBigyan")
                val file = File(ganitaBigyanDir, "word_problem_history.gba")
                if (file.exists()) {
                    file.delete()
                }
                _uiState.update { it.copy(historyList = emptyList()) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                val context = getApplication<Application>().applicationContext
                val ganitaBigyanDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "GanitaBigyan")
                val file = File(ganitaBigyanDir, "word_problem_history.gba")

                if (file.exists()) {
                    val content = file.readText()
                    val items = mutableListOf<HistoryItem>()
                    val blocks = content.split("----END----")

                    for (block in blocks) {
                        if (block.isBlank()) continue
                        val lines = block.lines().filter { it.isNotBlank() }
                        var time = ""
                        var ques = ""
                        var ans = ""
                        var res = ""

                        for (line in lines) {
                            if (line.startsWith("[")) time = line.trim()
                            else if (line.startsWith("ପ୍ରଶ୍ନ:")) ques = line.substringAfter("ପ୍ରଶ୍ନ:").trim()
                            else if (line.startsWith("ଆପଣଙ୍କର ଉତ୍ତର:")) ans = line.substringAfter("ଆପଣଙ୍କର ଉତ୍ତର:").trim()
                            else if (line.startsWith("ଫଳାଫଳ:")) res = line.substringAfter("ଫଳାଫଳ:").trim()
                        }
                        if (time.isNotEmpty() && ques.isNotEmpty()) {
                            items.add(HistoryItem(time, ques, ans, res))
                        }
                    }
                    _uiState.update { it.copy(historyList = items) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleHistory() {
        if (!_uiState.value.showHistory) {
            loadHistory()
        }
        _uiState.update { it.copy(showHistory = !it.showHistory) }
    }

    fun checkAnswer(context: Context, resolvedQuestionText: String) {
        if (_uiState.value.isAnswerCorrect != null) return

        val problem = _uiState.value.currentProblem ?: return
        val userAnswerInt = _uiState.value.userAnswer.toIntOrNull()
        val isCorrect = userAnswerInt == problem.answer
        if (isCorrect) {
            _uiState.update { it.copy(isAnswerCorrect = true, correctAnswers = it.correctAnswers + 1) }
            updateScore(1, 0)
        } else {
            _uiState.update { it.copy(isAnswerCorrect = false, wrongAnswers = it.wrongAnswers + 1) }
            updateScore(0, 1)
        }
        saveHistory(isCorrect, context, resolvedQuestionText, isSkipped = false)
    }

    fun serveNextProblem(context: Context?) {
        if (context != null && _uiState.value.currentProblem != null && _uiState.value.isAnswerCorrect == null) {
            val resolvedQuestion = _uiState.value.currentProblem!!.resolve(context).first
            saveHistory(isCorrect = false, context = context, resolvedQuestionText = resolvedQuestion, isSkipped = true)
        }

        if (questionTemplates.isEmpty()) loadTemplates()
        val template = questionTemplates.removeAt(0)
        val (numbers, answer, subTotal) = template.generator()
        _uiState.update {
            it.copy(
                currentProblem = WordProblem(template.key, template.questionTemplate, template.solutionTemplate, answer, numbers, subTotal),
                userAnswer = "",
                isAnswerCorrect = null,
                showSolution = false,
                hintUsed = false
            )
        }
    }

    private fun loadTemplates() {
        val allTemplates = listOf(
            // --- ADDITION ---
            ProblemTemplate(
                "add_balloons",
                "ତୁମେ {1} ଟି ଲାଲ୍ ବେଲୁନ୍ ଏବଂ {2} ଟି ନୀଳ ବେଲୁନ୍ କିଣିଲ । ତେବେ ତୁମ ପାଖରେ ମୋଟ କେତେଟି ବେଲୁନ୍ ହେଲା?",
                "ମୋଟ ବେଲୁନ୍ = {1} + {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(5, 15); val n2 = Random.nextInt(5, 15); Triple(listOf(n1, n2), n1 + n2, "") },

            ProblemTemplate(
                "add_birds",
                "ଗୋଟିଏ ଗଛରେ {1} ଟି ପାରା ଓ {2} ଟି ଶୁଆ ବସିଛନ୍ତି, ତାହେଲେ ମୋଟ କେତୋଟି ପକ୍ଷୀ ବସିଛନ୍ତି?",
                "ମୋଟ ପକ୍ଷୀ = {1} + {2} = {ans} ଟି । ବୁଝିପାରିଲ ତ"
            ) { val n1 = Random.nextInt(10, 25); val n2 = Random.nextInt(10, 25); Triple(listOf(n1, n2), n1 + n2, "") },

            ProblemTemplate(
                "add_fish",
                "ଜଣେ ମାଛ ଧରାଳି ପ୍ରଥମେ {1} ଟି ମାଛ ଓ ପରେ {2} ଟି ମାଛ ଧରିଲା । ତାହେଲେ ସେ ମୋଟ କେତେଟି ମାଛ ଧରିଲା?",
                "ମୋଟ ମାଛ = {1} + {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(15, 30); val n2 = Random.nextInt(15, 30); Triple(listOf(n1, n2), n1 + n2, "") },

            ProblemTemplate(
                "add_money",
                "ତୁମ ପାଖରେ {1} ଟଙ୍କା ଥିଲା । ତୁମ ଜେଜେବାପା ତୁମକୁ ଆଉ {2} ଟଙ୍କା ଦେଲେ । ତାହେଲେ କୁହ ତୁମ ପାଖରେ ଏବେ ମୋଟ କେତେ ଟଙ୍କା ହେଲା?",
                "ମୋଟ ଟଙ୍କା = {1} + {2} = {ans} ଟଙ୍କା ।"
            ) { val n1 = Random.nextInt(20, 70); val n2 = Random.nextInt(10, 30); Triple(listOf(n1, n2), n1 + n2, "") },

            ProblemTemplate(
                "add_children",
                "ଗୋଟିଏ ଖେଳ ପଡ଼ିଆରେ {1} ଜଣ ପୁଅ ଓ {2} ଜଣ ଝିଅ ଖେଳୁଛନ୍ତି । ତେବେ ପଡ଼ିଆରେ ମୋଟ କେତେ ଜଣ ପିଲା ଅଛନ୍ତି?",
                "ମୋଟ ପିଲା = {1} + {2} = {ans} ଜଣ ।"
            ) { val n1 = Random.nextInt(15, 40); val n2 = Random.nextInt(15, 40); Triple(listOf(n1, n2), n1 + n2, "") },

            // --- SUBTRACTION ---
            ProblemTemplate(
                "sub_flowers",
                "ଗୋଟିଏ ବଗିଚାରେ {1} ଟି ଫୁଲ ଫୁଟିଥିଲା । ସେଥିରୁ {2} ଟି ଫୁଲ ଛିଣ୍ଡି ଗଲା। ତେବେ ବର୍ତମାନ କେତେଟି ଫୁଲ ବାକି ଅଛି?",
                "ବାକି ଫୁଲ = {1} - {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(30, 60); val n2 = Random.nextInt(5, n1 - 5); Triple(listOf(n1, n2), n1 - n2, "") },

            ProblemTemplate(
                "sub_pencils",
                "ରମା ପାଖରେ {1} ଟି ପେନସିଲ୍ ଥିଲା । ସେ {2} ଟି ପେନସିଲ୍ ତା' ସାଙ୍ଗକୁ ଦେଇଦେଲା । ତାହେଲେ ତା' ପାଖରେ ଆଉ କେତେଟି ପେନସିଲ୍ ରହିଲା?",
                "ବାକି ପେନସିଲ୍ = {1} - {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(20, 50); val n2 = Random.nextInt(5, n1 - 5); Triple(listOf(n1, n2), n1 - n2, "") },

            ProblemTemplate(
                "sub_birds",
                "ଆକାଶରେ {1} ଟି ଚଢ଼େଇ ଉଡୁଥିଲେ । ସେମାନଙ୍କ ମଧ୍ୟରୁ {2} ଟି ଚଢ଼େଇ ଆସି ଗଛରେ ବସିଗଲେ । ତେବେ ଆଉ କେତେଟି ଚଢ଼େଇ ଉଡୁଛନ୍ତି?",
                "ଉଡୁଥିବା ଚଢ଼େଇ = {1} - {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(25, 60); val n2 = Random.nextInt(5, n1 - 10); Triple(listOf(n1, n2), n1 - n2, "") },

            ProblemTemplate(
                "sub_money",
                "ତୁମ ମା' ତୁମକୁ {1} ଟଙ୍କା ଦେଇଥିଲେ । ତୁମେ {2} ଟଙ୍କା ରେ ବହିଟିଏ କିଣିଲ । ତେବେ ତୁମ ପାଖରେ ଆଉ କେତେ ଟଙ୍କା ରହିଲା?",
                "ବାକି ଟଙ୍କା = {1} - {2} = {ans} ଟଙ୍କା ।"
            ) { val n1 = Random.nextInt(50, 100); val n2 = Random.nextInt(10, n1 - 10); Triple(listOf(n1, n2), n1 - n2, "") },

            ProblemTemplate(
                "sub_pages",
                "ଗୋଟିଏ ବହିରେ {1} ପୃଷ୍ଠା ଅଛି, ଏବଂ ମୁଁ {2} ପୃଷ୍ଠା ପଢ଼ି ସାରିଛି । ତେବେ ଆଉ କେତେ ପୃଷ୍ଠା ପଢ଼ିବାକୁ ବାକି ଅଛି?",
                "ବାକି ପୃଷ୍ଠା = {1} - {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(80, 200); val n2 = Random.nextInt(20, n1 - 20); Triple(listOf(n1, n2), n1 - n2, "") },

            // --- MULTIPLICATION ---
            ProblemTemplate(
                "mul_dozen",
                "ଯଦି ଗୋଟିଏ ଡଜନରେ {1} ଟି କଦଳୀ ଥାଏ, ତାହେଲେ {2} ଡଜନରେ କେତୋଟି କଦଳୀ ହେବ?",
                "ମୋଟ କଦଳୀ = {1} x {2} = {ans} ଟି ।"
            ) { val n1 = 12; val n2 = Random.nextInt(2, 8); Triple(listOf(n1, n2), n1 * n2, "") },

            ProblemTemplate(
                "mul_pencils",
                "ଯଦି ଗୋଟିଏ ବାକ୍ସରେ {1} ଟି ଲେଖାଏଁ ପେନସିଲ୍ ଥାଏ, ତେବେ ଏହିପରି {2} ଟି ବାକ୍ସରେ ମୋଟ କେତେଟି ପେନସିଲ୍ ଥିବ?",
                "ମୋଟ ପେନସିଲ୍ = {1} x {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(5, 12); val n2 = Random.nextInt(3, 9); Triple(listOf(n1, n2), n1 * n2, "") },

            ProblemTemplate(
                "mul_pages",
                "ତୁମେ ପ୍ରତ୍ୟେକ ଦିନ {1} ପୃଷ୍ଠା ଲେଖାଏଁ ବହି ପଢ଼ୁଛ । ତେବେ {2} ଦିନରେ ତୁମେ ମୋଟ କେତେ ପୃଷ୍ଠା ବହି ପଢ଼ିବ?",
                "ମୋଟ ପୃଷ୍ଠା = {1} x {2} = {ans} ଟି ।"
            ) { val n1 = Random.nextInt(4, 10); val n2 = Random.nextInt(3, 10); Triple(listOf(n1, n2), n1 * n2, "") },

            ProblemTemplate(
                "mul_wheels",
                "ଯଦି ଗୋଟିଏ କାର୍ ରେ {1} ଟି ଚକ ଅଛି, ତେବେ ଏହିପରି {2} ଟି କାର୍ ରେ ମୋଟ କେତୋଟି ଚକ ଥିବ?",
                "ମୋଟ ଚକ = {1} x {2} = {ans} ଟି ।"
            ) { val n1 = 4; val n2 = Random.nextInt(3, 15); Triple(listOf(n1, n2), n1 * n2, "") },

            ProblemTemplate(
                "mul_price",
                "ଯଦି ଗୋଟିଏ କଲମର ଦାମ୍ {1} ଟଙ୍କା ହୁଏ, ତାହେଲେ {2} ଟି କଲମର ଦାମ୍ କେତେ ହେବ?",
                "ମୋଟ ଦାମ୍ = {1} x {2} = {ans} ଟଙ୍କା ।"
            ) { val n1 = Random.nextInt(5, 20); val n2 = Random.nextInt(3, 10); Triple(listOf(n1, n2), n1 * n2, "") },

            // --- DIVISION ---
            ProblemTemplate(
                "div_mangoes",
                "ମୋଟ {1} ଟି ଆମ୍ବକୁ {2} ଜଣ ପିଲାଙ୍କୁ ସମାନ ଭାବରେ ବାଣ୍ଟିଲେ, ପ୍ରତ୍ୟେକ ପିଲା କେତୋଟି ଲେଖାଏଁ ଆମ୍ବ ପାଇବେ?",
                "ସମସ୍ତେ = {1} ÷ {2} = {ans} ଟି ଆମ୍ବ ପାଇବେ ।"
            ) { val n2 = Random.nextInt(2, 8); val ans = Random.nextInt(3, 12); val n1 = n2 * ans; Triple(listOf(n1, n2), ans, "") },

            ProblemTemplate(
                "div_toffee",
                "{1} ଟି ଚକୋଲେଟ କୁ {2} ଜଣ ସାଙ୍ଗଙ୍କ ମଧ୍ୟରେ ସମାନ ଭାବରେ ବାଣ୍ଟିଲେ, ପ୍ରତ୍ୟେକ ସାଙ୍ଗ କେତୋଟି ଲେଖାଏଁ ଚକୋଲେଟ ପାଇବେ?",
                "ପ୍ରତ୍ୟେକ ପାଇବେ = {1} ÷ {2} = {ans} ଟି ଚକୋଲେଟ ।"
            ) { val n2 = Random.nextInt(2, 6); val ans = Random.nextInt(2, 10); val n1 = n2 * ans; Triple(listOf(n1, n2), ans, "") },

            ProblemTemplate(
                "div_bananas",
                "{1} ଟି କଦଳୀକୁ {2} ଟି ମାଙ୍କଡ଼ ମଧ୍ୟରେ ସମାନ ଭାବରେ ବାଣ୍ଟିଲେ, ପ୍ରତ୍ୟେକ ମାଙ୍କଡ଼ କେତୋଟି ଲେଖାଏଁ କଦଳୀ ପାଇବେ?",
                "ପ୍ରତ୍ୟେକ ମାଙ୍କଡ଼ = {1} ÷ {2} = {ans} ଟି କଦଳୀ ପାଇବେ ।"
            ) { val n2 = Random.nextInt(2, 5); val ans = Random.nextInt(2, 6); val n1 = n2 * ans; Triple(listOf(n1, n2), ans, "") },

            ProblemTemplate(
                "div_weeks",
                "{1} ଦିନରେ କେତୋଟି ସପ୍ତାହ ହେବ? (୧ ସପ୍ତାହ = {2} ଦିନ)",
                "ଯଦି ୧ ସପ୍ତାହ ୭ ଦିନ ହୁଏ ତେବେ\nମୋଟ = {1} ÷ {2} = {ans} ସପ୍ତାହ ହେବ ।"
            ) { val n2 = 7; val ans = Random.nextInt(2, 8); val n1 = n2 * ans; Triple(listOf(n1, n2), ans, "") },

            // --- MIXED ---
            ProblemTemplate(
                "mix_mul_add",
                "ତୁମେ {1} ଟି ଥାଳିରେ {2} ଟି ଲେଖାଏଁ ମିଠା ରଖିଲ । ତୁମ ପାଖରେ ଆଉ {3} ଟି ମିଠା ବଳକା ରହିଲା । ତେବେ ତୁମ ପାଖରେ ପ୍ରଥମେ ମୋଟ କେତେଟି ମିଠା ଥିଲା?",
                "ପ୍ରଥମେ ଥାଳିରେ ଥିବା ମିଠା: {1} x {2} = {sub_total} ।\nତାହେଲେ ମୋଟ ମିଠା = {sub_total} + {3} = {ans} ଟି ହେବ ।"
            ) { val n1 = Random.nextInt(3, 8); val n2 = Random.nextInt(3, 8); val n3 = Random.nextInt(2, 10); val s = n1 * n2; Triple(listOf(n1, n2, n3), s + n3, s.toString()) },

            ProblemTemplate(
                "mix_mul_sub",
                "ରୋହିତ ପାଖରେ {1} ଟଙ୍କା ଥିଲା। ସେ {2} ଟଙ୍କା ରେ {3} ଟି ପେନ୍ କିଣିଲା। ତାହେଲେ ତାଙ୍କ ପାଖରେ ଆଉ କେତେ ଟଙ୍କା ବାକି ରହିଲା?",
                "ପେନ୍ ର ମୋଟ ଦାମ୍ = {2} x {3} = {sub_total} ।\nବାକି ଟଙ୍କା = {1} - {sub_total} = {ans} ଟଙ୍କା ବାକି ରହିଲା ।"
            ) {
                val price = Random.nextInt(5, 12) // {2}
                val qty = Random.nextInt(2, 6)    // {3}
                val cost = price * qty
                val totalMoney = cost + Random.nextInt(10, 50) // {1}
                Triple(listOf(totalMoney, price, qty), totalMoney - cost, cost.toString())
            },

            ProblemTemplate(
                "mix_add_sub",
                "ମୁଁ ଦୋକାନରୁ {1} ଟଙ୍କାର ଗୋଟିଏ ବହି ଏବଂ {2} ଟଙ୍କାର ଗୋଟିଏ ଖାତା କିଣିଲି । ଯଦି ମୁଁ ଦୋକାନୀକୁ {3} ଟଙ୍କା ଦିଏ, ତେବେ ସେ ମୋତେ କେତେ ଟଙ୍କା ଫେରାଇବ?",
                "ମୋଟ ଖର୍ଚ୍ଚ = {1} + {2} = {sub_total} ଟଙ୍କା ହେଲେ ।\nସେ ମୋତେ = {3} - {sub_total} = {ans} ଟଙ୍କା ଫେରାଇବ ।"
            ) {
                val n1 = Random.nextInt(10, 40); // Book cost
                val n2 = Random.nextInt(10, 30); // Notebook cost
                val totalCost = n1 + n2
                // Ensure note given {3} is greater than cost (e.g. 50 or 100)
                val noteGiven = if (totalCost < 50) 50 else if (totalCost < 100) 100 else 200
                Triple(listOf(n1, n2, noteGiven), noteGiven - totalCost, totalCost.toString())
            }
        )
        questionTemplates = allTemplates.shuffled().toMutableList()
    }

    fun toggleSolution() { _uiState.update { it.copy(showSolution = !it.showSolution, hintUsed = true) } }
    fun onNumberInput(digit: String) { if (_uiState.value.userAnswer.length < 6) { _uiState.update { it.copy(userAnswer = it.userAnswer + digit) } } }
    fun onBackspace() { if (_uiState.value.userAnswer.isNotEmpty()) { _uiState.update { it.copy(userAnswer = it.userAnswer.dropLast(1)) } } }
    private fun updateScore(correctIncrement: Int, incorrectIncrement: Int) { viewModelScope.launch { try { val context = getApplication<Application>().applicationContext; val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS); val ganitaBigyanDir = File(documentsDir, "GanitaBigyan"); if (!ganitaBigyanDir.exists()) ganitaBigyanDir.mkdirs(); val file = File(ganitaBigyanDir, "word_problem_score.gba"); var currentCorrect = 0; var currentIncorrect = 0; if (file.exists()) { val parts = file.readText().split(","); if (parts.size == 2) { currentCorrect = parts[0].toIntOrNull() ?: 0; currentIncorrect = parts[1].toIntOrNull() ?: 0 } }; file.writeText("${currentCorrect + correctIncrement},${incorrectIncrement + incorrectIncrement}") } catch (e: Exception) { e.printStackTrace() } } }
}

fun WordProblem.resolve(context: Context): Pair<String, String> {
    var resolvedQuestion = this.questionTemplate
    var resolvedSolution = this.solutionTemplate
    this.numbers.forEachIndexed { index, num ->
        val placeholder = "{${index + 1}}"
        val localizedNum = num.toLocaleNumerals(context)
        resolvedQuestion = resolvedQuestion.replace(placeholder, localizedNum)
        resolvedSolution = resolvedSolution.replace(placeholder, localizedNum)
    }
    if (this.subTotal.isNotBlank()) {
        resolvedSolution = resolvedSolution.replace("{sub_total}", this.subTotal.toLocaleNumerals(context))
    }
    resolvedSolution = resolvedSolution.replace("{ans}", this.answer.toLocaleNumerals(context))
    return Pair(resolvedQuestion, resolvedSolution)
}