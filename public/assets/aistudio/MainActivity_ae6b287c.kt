// --- START OF FILE MainActivity.kt ---
package `in`.sandeep.odiabhasa

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import `in`.sandeep.odiabhasa.ganita.*

enum class Screen {
    SPLASH, HOME, SWARA, BYANJANA, SANKHYA, YUKTA, MATRA, PHALA, BARNA_PARICHAYA,
    DETAIL, MENU, ABOUT, HISTORY_ODISHA, HISTORY_LANG, LIPYANTARA, BAHI, BOOK_READER,
    ODIA_CALENDAR, MADHU_BARNABODHA, WRITING,
    SCHOOL_BOOKS, SCHOOL_BOOK_READER,
    CLASS_1, CLASS_2, CLASS_3, CLASS_4, CLASS_5,
    CLASS_6, CLASS_7, CLASS_8, CLASS_9, CLASS_10, PURUNA_BAHI,
    ASA_JANIBA,
    HARMONIUM,
    GANITA, GAMES_CATEGORY, LEARNING_CATEGORY, UTILITY_CATEGORY,
    GAME_MATH_QUIZ, GAME_LOGIC, GAME_WORD_PROBLEM, GAME_SUDOKU, GAME_FTMN, GAME_SLIDING_PUZZLE,
    LEARNING_PANIKIA, LEARNING_ORDINALS, LEARNING_COUNTING,
    UTILITY_CALCULATOR, UTILITY_CONVERTER, UTILITY_NUM_TO_TEXT, UTILITY_DIGITAL_BAJARA
}

class MainActivity : ComponentActivity() {

    private var deepLinkUri by mutableStateOf<Uri?>(null)

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data != null) {
            deepLinkUri = intent.data
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            val LightColors = lightColorScheme(
                background = Color(0xFFF5F7FA),
                surface = Color.White,
                onSurface = Color(0xFF1D1D1D),
                onBackground = Color(0xFF1D1D1D),
                primaryContainer = Color(0xFFF5F7FA)
            )

            MaterialTheme(colorScheme = LightColors) {
                var currentScreen by rememberSaveable { mutableStateOf(Screen.SPLASH) }
                var previousScreen by rememberSaveable { mutableStateOf(Screen.HOME) }
                var openedUtilityFromHome by rememberSaveable { mutableStateOf(false) }

                var currentBookTitle by rememberSaveable { mutableStateOf("") }
                var currentPdfPath by rememberSaveable { mutableStateOf("") }
                var classScreenToReturn by rememberSaveable { mutableStateOf(Screen.CLASS_1) }

                var selectedDetailTitle by rememberSaveable { mutableStateOf("") }
                var selectedDetailChar by rememberSaveable { mutableStateOf("") }
                var currentDetailList by remember { mutableStateOf(listOf<String>()) }

                var selectedBook by remember { mutableStateOf<BookDetails?>(null) }
                var selectedBookDestination by rememberSaveable { mutableStateOf("") }
                var selectedBookVersion by rememberSaveable { mutableIntStateOf(0) }

                val homeState = rememberLazyListState()
                val swaraState = rememberLazyGridState()
                val byanjanaState = rememberLazyGridState()
                val sankhyaState = rememberLazyGridState()
                val yuktaState = rememberLazyGridState()
                val barnaState = rememberLazyGridState()
                val matraState = rememberLazyGridState()
                val phalaState = rememberLazyGridState()

                val bahiViewModel: BahiViewModel = viewModel(factory = BahiViewModelFactory(applicationContext as Application))
                val bahiState by bahiViewModel.booksState

                LaunchedEffect(deepLinkUri, bahiState) {
                    val uri = deepLinkUri ?: return@LaunchedEffect

                    fun navigateToBook(bookIdentifier: String) {
                        if (bahiState !is BookScreenState.Success) return

                        val book = bookIdentifier.toIntOrNull()?.let { index ->
                            bahiViewModel.findBookByIndex(index)
                        } ?: bahiViewModel.findBookByIdentifier(bookIdentifier)

                        if (book != null) {
                            bahiViewModel.getBookForReading(book) { details ->
                                selectedBook = details
                                selectedBookDestination = book.metadata.destination
                                selectedBookVersion = book.metadata.version
                                currentScreen = Screen.BOOK_READER
                            }
                        } else {
                            currentScreen = Screen.BAHI
                        }
                        deepLinkUri = null
                    }

                    val bookId = uri.getQueryParameter("Bahi")
                    if (bookId != null) {
                        navigateToBook(bookId)
                        return@LaunchedEffect
                    }

                    val screenName = uri.queryParameterNames.firstOrNull()
                    if (screenName != null) {
                        val targetScreen = Screen.values().find {
                            it.name.equals(screenName, ignoreCase = true)
                        }
                        if (targetScreen != null) {
                            if (targetScreen == Screen.BAHI || targetScreen == Screen.BOOK_READER) {
                                currentScreen = Screen.BAHI
                            } else {
                                currentScreen = targetScreen
                            }
                        } else if (currentScreen != Screen.SPLASH) {
                            currentScreen = Screen.HOME
                        }
                        deepLinkUri = null
                        return@LaunchedEffect
                    }

                    if (currentScreen != Screen.SPLASH) {
                        currentScreen = Screen.HOME
                    }
                    deepLinkUri = null
                }

                fun navigateToDetail(title: String, char: String, list: List<String>, sourceScreen: Screen) {
                    selectedDetailTitle = title
                    selectedDetailChar = char
                    currentDetailList = list
                    previousScreen = sourceScreen
                    currentScreen = Screen.DETAIL
                }

                BackHandler(enabled = true) {
                    when (currentScreen) {
                        Screen.SPLASH -> { }
                        Screen.HOME -> finish()
                        Screen.DETAIL -> currentScreen = previousScreen
                        Screen.MENU -> currentScreen = Screen.HOME
                        Screen.ABOUT -> currentScreen = Screen.MENU
                        Screen.BOOK_READER -> currentScreen = Screen.BAHI

                        Screen.SCHOOL_BOOK_READER -> currentScreen = classScreenToReturn

                        Screen.GAMES_CATEGORY, Screen.LEARNING_CATEGORY -> currentScreen = Screen.GANITA
                        Screen.UTILITY_CATEGORY -> currentScreen = if (openedUtilityFromHome) Screen.HOME else Screen.GANITA

                        Screen.GAME_MATH_QUIZ, Screen.GAME_LOGIC, Screen.GAME_WORD_PROBLEM, Screen.GAME_SUDOKU, Screen.GAME_FTMN, Screen.GAME_SLIDING_PUZZLE -> currentScreen = Screen.GAMES_CATEGORY
                        Screen.LEARNING_PANIKIA, Screen.LEARNING_ORDINALS, Screen.LEARNING_COUNTING -> currentScreen = Screen.LEARNING_CATEGORY
                        Screen.UTILITY_CALCULATOR, Screen.UTILITY_CONVERTER, Screen.UTILITY_NUM_TO_TEXT, Screen.UTILITY_DIGITAL_BAJARA -> currentScreen = Screen.UTILITY_CATEGORY

                        Screen.CLASS_1, Screen.CLASS_2, Screen.CLASS_3, Screen.CLASS_4, Screen.CLASS_5,
                        Screen.CLASS_6, Screen.CLASS_7, Screen.CLASS_8, Screen.CLASS_9, Screen.CLASS_10 -> currentScreen = Screen.SCHOOL_BOOKS

                        Screen.BAHI, Screen.HISTORY_ODISHA, Screen.HISTORY_LANG, Screen.LIPYANTARA,
                        Screen.ODIA_CALENDAR, Screen.PHALA, Screen.WRITING, Screen.GANITA, Screen.HARMONIUM,
                        Screen.SCHOOL_BOOKS, Screen.PURUNA_BAHI, Screen.ASA_JANIBA,
                        Screen.MADHU_BARNABODHA -> currentScreen = Screen.HOME
                        else -> currentScreen = Screen.HOME
                    }
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    SharedTransitionLayout(modifier = Modifier.weight(1f)) {
                        AnimatedContent(
                            targetState = currentScreen,
                            label = "ScreenTransition",
                            modifier = Modifier.fillMaxSize(),
                            transitionSpec = {
                                val mainFeatureScreens = setOf(
                                    Screen.MENU, Screen.ODIA_CALENDAR, Screen.WRITING, Screen.GANITA, Screen.HARMONIUM,
                                    Screen.HISTORY_ODISHA, Screen.HISTORY_LANG, Screen.LIPYANTARA,
                                    Screen.BAHI, Screen.PURUNA_BAHI, Screen.MADHU_BARNABODHA, Screen.SCHOOL_BOOKS,
                                    Screen.SWARA, Screen.BYANJANA, Screen.SANKHYA, Screen.YUKTA,
                                    Screen.MATRA, Screen.PHALA, Screen.BARNA_PARICHAYA, Screen.ASA_JANIBA
                                )

                                val gridScreens = setOf(
                                    Screen.SWARA, Screen.BYANJANA, Screen.SANKHYA, Screen.YUKTA,
                                    Screen.MATRA, Screen.PHALA, Screen.BARNA_PARICHAYA
                                )

                                val ganitaCategoryScreens = setOf(Screen.GAMES_CATEGORY, Screen.LEARNING_CATEGORY, Screen.UTILITY_CATEGORY)
                                val gameScreens = setOf(Screen.GAME_MATH_QUIZ, Screen.GAME_LOGIC, Screen.GAME_WORD_PROBLEM, Screen.GAME_SUDOKU, Screen.GAME_FTMN, Screen.GAME_SLIDING_PUZZLE)
                                val learningScreens = setOf(Screen.LEARNING_PANIKIA, Screen.LEARNING_ORDINALS, Screen.LEARNING_COUNTING)
                                val utilityScreens = setOf(Screen.UTILITY_CALCULATOR, Screen.UTILITY_CONVERTER, Screen.UTILITY_NUM_TO_TEXT, Screen.UTILITY_DIGITAL_BAJARA)
                                val schoolBookScreens = setOf(Screen.CLASS_1, Screen.CLASS_2, Screen.CLASS_3, Screen.CLASS_4, Screen.CLASS_5, Screen.CLASS_6, Screen.CLASS_7, Screen.CLASS_8, Screen.CLASS_9, Screen.CLASS_10)

                                val isForwardTransition =
                                    (initialState == Screen.HOME && targetState in mainFeatureScreens) ||
                                            (initialState in gridScreens && targetState == Screen.DETAIL) ||
                                            (initialState == Screen.GANITA && targetState in ganitaCategoryScreens) ||
                                            (initialState == Screen.GAMES_CATEGORY && targetState in gameScreens) ||
                                            (initialState == Screen.LEARNING_CATEGORY && targetState in learningScreens) ||
                                            (initialState == Screen.UTILITY_CATEGORY && targetState in utilityScreens) ||
                                            (initialState == Screen.BAHI && targetState == Screen.BOOK_READER) ||
                                            (initialState == Screen.MENU && targetState == Screen.ABOUT) ||
                                            (initialState == Screen.SCHOOL_BOOKS && targetState in schoolBookScreens) ||
                                            (initialState in schoolBookScreens && targetState == Screen.SCHOOL_BOOK_READER) ||
                                            (initialState == Screen.PURUNA_BAHI && targetState == Screen.SCHOOL_BOOK_READER)

                                val isBackwardTransition =
                                    (targetState == Screen.HOME && initialState in mainFeatureScreens) ||
                                            (targetState in gridScreens && initialState == Screen.DETAIL) ||
                                            (targetState == Screen.GANITA && initialState in ganitaCategoryScreens) ||
                                            (targetState == Screen.GAMES_CATEGORY && initialState in gameScreens) ||
                                            (targetState == Screen.LEARNING_CATEGORY && initialState in learningScreens) ||
                                            (targetState == Screen.UTILITY_CATEGORY && initialState in utilityScreens) ||
                                            (targetState == Screen.BAHI && initialState == Screen.BOOK_READER) ||
                                            (targetState == Screen.MENU && initialState == Screen.ABOUT) ||
                                            (targetState == Screen.SCHOOL_BOOKS && initialState in schoolBookScreens) ||
                                            (targetState in schoolBookScreens && initialState == Screen.SCHOOL_BOOK_READER) ||
                                            (targetState == Screen.PURUNA_BAHI && initialState == Screen.SCHOOL_BOOK_READER)

                                if (isForwardTransition) {
                                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> -width / 2 } + scaleOut(targetScale = 0.95f) + fadeOut()
                                    )
                                } else if (isBackwardTransition) {
                                    (slideInHorizontally { width -> -width / 2 } + scaleIn(initialScale = 0.95f) + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> width } + fadeOut()
                                    ).apply {
                                        targetContentZIndex = 1f
                                    }
                                } else {
                                    fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) togetherWith
                                            fadeOut(animationSpec = tween(90))
                                }
                            }
                        ) { targetScreen ->

                            when (targetScreen) {
                                Screen.SPLASH -> SplashScreen( onNavigateToHome = { if (deepLinkUri == null) { currentScreen = Screen.HOME } } )

                                Screen.HOME -> HomeScreen(
                                    onNavToSwara = { currentScreen = Screen.SWARA },
                                    onNavToByanjana = { currentScreen = Screen.BYANJANA },
                                    onNavToSankhya = { currentScreen = Screen.SANKHYA },
                                    onNavToYukta = { currentScreen = Screen.YUKTA },
                                    onNavToBarna = { currentScreen = Screen.BARNA_PARICHAYA },
                                    onNavToMatra = { currentScreen = Screen.MATRA },
                                    onNavToPhala = { currentScreen = Screen.PHALA },
                                    onNavToMenu = { currentScreen = Screen.MENU },
                                    onNavToHistoryOdisha = { currentScreen = Screen.HISTORY_ODISHA },
                                    onNavToHistoryLang = { currentScreen = Screen.HISTORY_LANG },
                                    onNavToLipyantara = { currentScreen = Screen.LIPYANTARA },
                                    onNavToBahi = { currentScreen = Screen.BAHI },
                                    onNavToPurunaBahi = { currentScreen = Screen.PURUNA_BAHI },
                                    onNavToOdiaCalendar = { currentScreen = Screen.ODIA_CALENDAR },
                                    onNavToMadhuBarnabodha = { currentScreen = Screen.MADHU_BARNABODHA },
                                    onNavToSchoolBooks = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onNavToWriting = { currentScreen = Screen.WRITING },
                                    onNavToGanita = { currentScreen = Screen.GANITA },
                                    onNavToHarmonium = { currentScreen = Screen.HARMONIUM },
                                    onNavToUtility = { openedUtilityFromHome = true; currentScreen = Screen.UTILITY_CATEGORY },
                                    onNavToAsaJaniba = { currentScreen = Screen.ASA_JANIBA },
                                    homeState = homeState
                                )

                                Screen.MENU -> MenuScreen( onNavBack = { currentScreen = Screen.HOME }, onNavToAbout = { currentScreen = Screen.ABOUT })
                                Screen.ABOUT -> AboutScreen( onNavBack = { currentScreen = Screen.MENU })
                                Screen.HISTORY_ODISHA -> HistoryOdishaScreen( onBack = { currentScreen = Screen.HOME })
                                Screen.HISTORY_LANG -> HistoryLangScreen( onBack = { currentScreen = Screen.HOME })
                                Screen.LIPYANTARA -> LipyantaraScreen( onBack = { currentScreen = Screen.HOME })
                                Screen.HARMONIUM -> HarmoniumScreen( onBack = { currentScreen = Screen.HOME } )
                                Screen.ASA_JANIBA -> AsaJanibaScreen( onBack = { currentScreen = Screen.HOME } )

                                Screen.SWARA -> CommonGridPage("ସ୍ୱରବର୍ଣ୍ଣ", swarabarnaList, swaraState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ସ୍ୱରବର୍ଣ୍ଣ", char, swarabarnaList, Screen.SWARA) }, this@AnimatedContent)
                                Screen.BYANJANA -> CommonGridPage("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ", byanjanabarnaList, byanjanaState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ", char, byanjanabarnaList, Screen.BYANJANA) }, this@AnimatedContent)
                                Screen.SANKHYA -> CommonGridPage("ଓଡ଼ିଆ ସଂଖ୍ୟା", sankhyaList, sankhyaState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ଓଡ଼ିଆ ସଂଖ୍ୟା", char, sankhyaList, Screen.SANKHYA) }, this@AnimatedContent)
                                Screen.YUKTA -> CommonGridPage("ଯୁକ୍ତାକ୍ଷର", yuktakshyaraList, yuktaState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ଯୁକ୍ତାକ୍ଷର", char, yuktakshyaraList, Screen.YUKTA) }, this@AnimatedContent)
                                Screen.BARNA_PARICHAYA -> CommonGridPage("ବର୍ଣ୍ଣ ପରିଚୟ", barnaParichayaList, barnaState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ବର୍ଣ୍ଣ ପରିଚୟ", char, barnaParichayaList, Screen.BARNA_PARICHAYA) }, this@AnimatedContent)
                                Screen.MATRA -> CommonGridPage("ମାତ୍ରା", matraList, matraState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ମାତ୍ରା", char, matraList, Screen.MATRA) }, this@AnimatedContent)
                                Screen.PHALA -> CommonGridPage("ଫଳା", phalaList, phalaState, { currentScreen = Screen.HOME }, { char -> navigateToDetail("ଫଳା", char, phalaList, Screen.PHALA) }, this@AnimatedContent)

                                Screen.DETAIL -> DetailScreen(selectedDetailTitle, selectedDetailChar, currentDetailList, { currentScreen = previousScreen }, { newChar -> selectedDetailChar = newChar }, this@AnimatedContent)

                                Screen.BAHI -> BahiScreen( onBack = { currentScreen = Screen.HOME }, onBookClick = { book, dest, ver -> selectedBook = book; selectedBookDestination = dest; selectedBookVersion = ver; currentScreen = Screen.BOOK_READER } )
                                Screen.BOOK_READER -> { selectedBook?.let { book -> BookReaderScreen( bookDetails = book, onBack = { currentScreen = Screen.BAHI } ) } }

                                Screen.ODIA_CALENDAR -> OdiaCalendarScreen(onBack = { currentScreen = Screen.HOME })
                                Screen.MADHU_BARNABODHA -> MadhuBarnabodhaScreen(onBack = { currentScreen = Screen.HOME })
                                Screen.WRITING -> WritingPracticeScreen(onBack = { currentScreen = Screen.HOME })

                                Screen.SCHOOL_BOOKS -> SchoolBooksScreen(
                                    onBack = { currentScreen = Screen.HOME },
                                    onClassClick = { screen -> currentScreen = screen },
                                    animatedVisibilityScope = this@AnimatedContent
                                )

                                Screen.SCHOOL_BOOK_READER -> SchoolBookPdfViewerScreen(
                                    title = currentBookTitle,
                                    filePath = currentPdfPath,
                                    onBack = { currentScreen = classScreenToReturn }
                                )

                                Screen.CLASS_1 -> ClassOneScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_1; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_2 -> ClassTwoScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_2; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_3 -> ClassThreeScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_3; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_4 -> ClassFourScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_4; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_5 -> ClassFiveScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_5; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_6 -> ClassSixScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_6; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_7 -> ClassSevenScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_7; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_8 -> ClassEightScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_8; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_9 -> ClassNineScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_9; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )
                                Screen.CLASS_10 -> ClassTenScreen(
                                    onBack = { currentScreen = Screen.SCHOOL_BOOKS },
                                    onReadBook = { title, path -> currentBookTitle = title; currentPdfPath = path; classScreenToReturn = Screen.CLASS_10; currentScreen = Screen.SCHOOL_BOOK_READER },
                                    animatedVisibilityScope = this@AnimatedContent
                                )

                                Screen.PURUNA_BAHI -> PurunaBahiScreen(
                                    onBack = { currentScreen = Screen.HOME },
                                    onReadBook = { title, path ->
                                        currentBookTitle = title
                                        currentPdfPath = path
                                        classScreenToReturn = Screen.PURUNA_BAHI
                                        currentScreen = Screen.SCHOOL_BOOK_READER
                                    }
                                )

                                Screen.GANITA -> GanitaHomeScreen( onNavigateBack = { currentScreen = Screen.HOME }, onNavToGames = { currentScreen = Screen.GAMES_CATEGORY }, onNavToLearning = { currentScreen = Screen.LEARNING_CATEGORY }, onNavToUtility = { openedUtilityFromHome = false; currentScreen = Screen.UTILITY_CATEGORY })
                                Screen.GAMES_CATEGORY -> GamesCategoryScreen( onNavigateBack = { currentScreen = Screen.GANITA }, onNavigateToRoute = { route -> currentScreen = when (route) { "game_route" -> Screen.GAME_MATH_QUIZ; "logic_game_route" -> Screen.GAME_LOGIC; "word_problem_route" -> Screen.GAME_SUDOKU; "sudoku_route" -> Screen.GAME_SUDOKU; "ftmn_game_route" -> Screen.GAME_FTMN; "sliding_puzzle_route" -> Screen.GAME_SLIDING_PUZZLE; else -> Screen.GAMES_CATEGORY } } )
                                Screen.LEARNING_CATEGORY -> LearningCategoryScreen( onNavigateBack = { currentScreen = Screen.GANITA }, onNavigateToRoute = { route -> currentScreen = when (route) { "learning_panikia" -> Screen.LEARNING_PANIKIA; "learning_ordinals" -> Screen.LEARNING_ORDINALS; "learning_counting" -> Screen.LEARNING_COUNTING; else -> Screen.LEARNING_CATEGORY } } )
                                Screen.UTILITY_CATEGORY -> UtilityCategoryScreen( onNavigateBack = { currentScreen = if (openedUtilityFromHome) Screen.HOME else Screen.GANITA }, onNavigateToRoute = { route -> currentScreen = when (route) { "utility_calculator" -> Screen.UTILITY_CALCULATOR; "utility_converter" -> Screen.UTILITY_CONVERTER; "utility_num_to_text" -> Screen.UTILITY_NUM_TO_TEXT; "utility_digital_bajara" -> Screen.UTILITY_DIGITAL_BAJARA; else -> Screen.UTILITY_CATEGORY } } )

                                Screen.GAME_MATH_QUIZ -> MathGameScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.GAME_LOGIC -> LogicPuzzleScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.GAME_WORD_PROBLEM -> WordProblemScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.GAME_SUDOKU -> SudokuScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.GAME_FTMN -> FTMNScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.GAME_SLIDING_PUZZLE -> SlidingPuzzleScreen(onBack = { currentScreen = Screen.GAMES_CATEGORY })
                                Screen.LEARNING_PANIKIA -> PanikiaScreen(onBack = { currentScreen = Screen.LEARNING_CATEGORY })
                                Screen.LEARNING_ORDINALS -> OrdinalsScreen(onBack = { currentScreen = Screen.LEARNING_CATEGORY })
                                Screen.LEARNING_COUNTING -> CountingScreen(onBack = { currentScreen = Screen.LEARNING_CATEGORY })
                                Screen.UTILITY_CALCULATOR -> CalculatorScreen(onBack = { currentScreen = Screen.UTILITY_CATEGORY })
                                Screen.UTILITY_CONVERTER -> UnitConverterScreen(onBack = { currentScreen = Screen.UTILITY_CATEGORY })
                                Screen.UTILITY_NUM_TO_TEXT -> NumberToTextScreen(onBack = { currentScreen = Screen.UTILITY_CATEGORY })
                                Screen.UTILITY_DIGITAL_BAJARA -> DigitalBajaraScreen(onBack = { currentScreen = Screen.UTILITY_CATEGORY })
                            }
                        }
                    }
                }
            }
        }
    }
}
// --- END OF FILE MainActivity.kt ---

