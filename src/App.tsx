import React, { useState, useEffect, useCallback } from 'react';
import { Screen, swarabarnaList } from './data/odiaData';
import { parseRouteFromLocation, getOdiaUrlPath } from './utils/odiaUtils';
import { HomeScreen } from './components/HomeScreen';
import { CommonGridPage, DetailScreen } from './components/CommonGridAndDetail';
import { WritingPracticeScreen } from './components/WritingPracticeScreen';
import {
  MadhuBarnabodhaScreen,
  SchoolBooksScreen,
  ClassBooksScreen,
  BahiScreen,
  PurunaBahiScreen
} from './components/BooksScreens';
import {
  GanitaHomeScreen,
  GamesCategoryScreen,
  LearningCategoryScreen,
  UtilityCategoryScreen,
  PanikiaScreen,
  OrdinalsScreen,
  CountingScreen,
  NumberToTextScreen,
  UnitConverterScreen,
  DigitalBajaraScreen,
  CalculatorScreen
} from './components/GanitaScreens';
import {
  MathGameScreen,
  LogicPuzzleScreen,
  WordProblemScreen,
  SudokuScreen,
  FTMNScreen,
  SlidingPuzzleScreen
} from './components/GanitaGames';
import {
  OdiaCalendarScreen,
  HarmoniumScreen,
  HistoryScreen,
  LipyantaraScreen,
  AsaJanibaScreen,
  MenuScreen,
  AboutScreen,
  DeepLinksModal
} from './components/ExploreAndMenuScreens';

export default function App() {
  const initialRoute = parseRouteFromLocation(window.location.pathname, window.location.search);

  const [currentScreen, setCurrentScreen] = useState<Screen>(initialRoute.screen);
  const [bahiIdParam, setBahiIdParam] = useState<string | undefined>(initialRoute.bahiId);
  const [previousGridScreen, setPreviousGridScreen] = useState<Screen>(Screen.SWARA);
  const [selectedChar, setSelectedChar] = useState<string>('ଅ');
  const [selectedCharList, setSelectedCharList] = useState<string[]>(swarabarnaList);
  const [showDeepLinksModal, setShowDeepLinksModal] = useState(false);

  const navigateTo = useCallback((screen: Screen) => {
    setCurrentScreen(screen);
    window.scrollTo({ top: 0, behavior: 'smooth' });
    const newUrl = getOdiaUrlPath(screen);
    window.history.pushState({ screen }, '', newUrl);
  }, []);

  useEffect(() => {
    // Normalize initial URL to Odia path if opened via query param (unless ?Bahi=...)
    if (window.location.search && !initialRoute.bahiId && initialRoute.screen !== Screen.HOME) {
      window.history.replaceState({ screen: initialRoute.screen }, '', getOdiaUrlPath(initialRoute.screen));
    }

    const handlePopState = () => {
      const parsed = parseRouteFromLocation(window.location.pathname, window.location.search);
      setCurrentScreen(parsed.screen);
      setBahiIdParam(parsed.bahiId);
    };
    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [initialRoute.bahiId, initialRoute.screen]);

  const renderScreen = () => {
    switch (currentScreen) {
      case Screen.HOME:
      case Screen.SPLASH:
        return (
          <HomeScreen
            onNavigate={navigateTo}
            onOpenDeepLinksModal={() => setShowDeepLinksModal(true)}
          />
        );

      case Screen.SWARA:
      case Screen.BYANJANA:
      case Screen.SANKHYA:
      case Screen.YUKTA:
      case Screen.MATRA:
      case Screen.PHALA:
      case Screen.BARNA_PARICHAYA:
        return (
          <CommonGridPage
            screen={currentScreen}
            onBack={() => navigateTo(Screen.HOME)}
            onSelectChar={(ch, list) => {
              setPreviousGridScreen(currentScreen);
              setSelectedChar(ch);
              setSelectedCharList(list);
              setCurrentScreen(Screen.DETAIL);
            }}
          />
        );

      case Screen.DETAIL:
        return (
          <DetailScreen
            char={selectedChar}
            charList={selectedCharList}
            onBack={() => navigateTo(previousGridScreen)}
            onChangeChar={ch => setSelectedChar(ch)}
          />
        );

      case Screen.WRITING:
        return <WritingPracticeScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.MADHU_BARNABODHA:
        return <MadhuBarnabodhaScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.SCHOOL_BOOKS:
        return (
          <SchoolBooksScreen
            onBack={() => navigateTo(Screen.HOME)}
            onSelectClass={clsScreen => navigateTo(clsScreen)}
          />
        );

      case Screen.CLASS_1:
        return <ClassBooksScreen classNum={1} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_2:
        return <ClassBooksScreen classNum={2} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_3:
        return <ClassBooksScreen classNum={3} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_4:
        return <ClassBooksScreen classNum={4} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_5:
        return <ClassBooksScreen classNum={5} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_6:
        return <ClassBooksScreen classNum={6} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_7:
        return <ClassBooksScreen classNum={7} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_8:
        return <ClassBooksScreen classNum={8} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_9:
        return <ClassBooksScreen classNum={9} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;
      case Screen.CLASS_10:
        return <ClassBooksScreen classNum={10} onBack={() => navigateTo(Screen.SCHOOL_BOOKS)} />;

      case Screen.BAHI:
      case Screen.BOOK_READER:
        return <BahiScreen initialBahiId={bahiIdParam} onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.PURUNA_BAHI:
        return <PurunaBahiScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.GANITA:
        return (
          <GanitaHomeScreen
            onBack={() => navigateTo(Screen.HOME)}
            onNavigate={navigateTo}
          />
        );

      case Screen.GAMES_CATEGORY:
        return (
          <GamesCategoryScreen
            onBack={() => navigateTo(Screen.GANITA)}
            onNavigate={navigateTo}
          />
        );

      case Screen.LEARNING_CATEGORY:
        return (
          <LearningCategoryScreen
            onBack={() => navigateTo(Screen.GANITA)}
            onNavigate={navigateTo}
          />
        );

      case Screen.UTILITY_CATEGORY:
        return (
          <UtilityCategoryScreen
            onBack={() => navigateTo(Screen.GANITA)}
            onNavigate={navigateTo}
          />
        );

      case Screen.LEARNING_PANIKIA:
        return <PanikiaScreen onBack={() => navigateTo(Screen.LEARNING_CATEGORY)} />;

      case Screen.LEARNING_ORDINALS:
        return <OrdinalsScreen onBack={() => navigateTo(Screen.LEARNING_CATEGORY)} />;

      case Screen.LEARNING_COUNTING:
        return <CountingScreen onBack={() => navigateTo(Screen.LEARNING_CATEGORY)} />;

      case Screen.UTILITY_NUM_TO_TEXT:
        return <NumberToTextScreen onBack={() => navigateTo(Screen.UTILITY_CATEGORY)} />;

      case Screen.UTILITY_CONVERTER:
        return <UnitConverterScreen onBack={() => navigateTo(Screen.UTILITY_CATEGORY)} />;

      case Screen.UTILITY_DIGITAL_BAJARA:
        return <DigitalBajaraScreen onBack={() => navigateTo(Screen.UTILITY_CATEGORY)} />;

      case Screen.UTILITY_CALCULATOR:
        return <CalculatorScreen onBack={() => navigateTo(Screen.UTILITY_CATEGORY)} />;

      case Screen.GAME_MATH_QUIZ:
        return <MathGameScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.GAME_LOGIC:
        return <LogicPuzzleScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.GAME_WORD_PROBLEM:
        return <WordProblemScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.GAME_SUDOKU:
        return <SudokuScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.GAME_FTMN:
        return <FTMNScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.GAME_SLIDING_PUZZLE:
        return <SlidingPuzzleScreen onBack={() => navigateTo(Screen.GAMES_CATEGORY)} />;

      case Screen.ODIA_CALENDAR:
        return <OdiaCalendarScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.HARMONIUM:
        return <HarmoniumScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.HISTORY_ODISHA:
        return <HistoryScreen type="ODISHA" onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.HISTORY_LANG:
        return <HistoryScreen type="LANG" onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.LIPYANTARA:
        return <LipyantaraScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.ASA_JANIBA:
        return <AsaJanibaScreen onBack={() => navigateTo(Screen.HOME)} />;

      case Screen.MENU:
        return (
          <MenuScreen
            onBack={() => navigateTo(Screen.HOME)}
            onNavigate={navigateTo}
          />
        );

      case Screen.ABOUT:
        return <AboutScreen onBack={() => navigateTo(Screen.MENU)} />;

      default:
        return (
          <HomeScreen
            onNavigate={navigateTo}
            onOpenDeepLinksModal={() => setShowDeepLinksModal(true)}
          />
        );
    }
  };

  return (
    <div className="min-h-screen bg-[#F5F7FA] text-slate-900">
      {renderScreen()}

      {showDeepLinksModal && (
        <DeepLinksModal
          onClose={() => setShowDeepLinksModal(false)}
          onNavigate={navigateTo}
        />
      )}
    </div>
  );
}
