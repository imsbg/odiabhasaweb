import React, { useState, useMemo, useEffect } from 'react';
import {
  ArrowLeft, Gamepad2, BookOpen, Wrench, Volume2, Lightbulb,
  Plus, Trash2, Calculator, ArrowRightLeft, FileText, ShoppingBag,
  Copy, Check, ArrowUpDown, Delete, History, X, Share2, Sparkles
} from 'lucide-react';
import {
  Screen,
  ordinalData,
  gananaData
} from '../data/odiaData';
import {
  toOdiaNum,
  fromOdiaNum,
  getPanikiaTableData,
  convertToOdiaWords,
  convertToEnglishWords,
  shopUnits,
  ShopUnit,
  getCalculationIdea,
  getSmartIdea1,
  getSmartIdea2,
  playOdiaAudio,
  formatNumberClean
} from '../utils/odiaUtils';

// --- 1. GANITA HOME SCREEN (Exact match with GanitaHomeScreen_4de66e69.kt) ---
export const GanitaHomeScreen: React.FC<{
  onBack: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onBack, onNavigate }) => {
  const greeting = useMemo(() => {
    const hour = new Date().getHours();
    if (hour >= 5 && hour < 12) return 'ସୁପ୍ରଭାତ';
    if (hour >= 12 && hour < 17) return 'ଶୁଭ ଅପରାହ୍ଣ';
    if (hour >= 17 && hour < 21) return 'ଶୁଭ ସନ୍ଧ୍ୟା';
    return 'ଶୁଭ ରାତ୍ରି';
  }, []);

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20 flex flex-col">
      <div className="max-w-lg w-full mx-auto px-5 pt-4 pb-10 flex-1 flex flex-col justify-between">
        <div>
          {/* Back button row */}
          <div className="flex items-center justify-between mb-3">
            <button
              onClick={onBack}
              aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
              className="w-11 h-11 rounded-full bg-white border-[2.5px] border-black shadow-[0_3px_0_#000] flex items-center justify-center cursor-pointer active:translate-y-0.5"
            >
              <ArrowLeft className="w-5 h-5 text-black" />
            </button>
          </div>

          {/* 1. TOP HEADER CARD */}
          <div className="w-full rounded-[24px] bg-[#E0F7FA] border-[3px] border-black shadow-[0_6px_0_#000] p-5 flex items-center justify-between">
            <div>
              <p className="text-[18px] font-bold text-[#00695C]">👋 {greeting}</p>
              <h1 className="text-[36px] leading-tight font-black text-black mt-0.5">କୁନି ଗଣିତ</h1>
            </div>
            <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center text-[32px] shrink-0">
              🧮
            </div>
          </div>

          {/* 2. MAIN MENU GRID */}
          <div className="mt-6 space-y-5">
            <h2 className="text-[20px] font-black text-[#37474F] ml-1">ତୁମ ପସନ୍ଦ ବାଛ:</h2>

            {/* Row 1: Two Square Cards */}
            <div className="grid grid-cols-2 gap-4">
              <button
                onClick={() => onNavigate(Screen.GAMES_CATEGORY)}
                className="h-[175px] rounded-[24px] bg-[#FFD54F] border-[3px] border-black shadow-[0_8px_0_#000] p-4 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click"
              >
                <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center mb-3">
                  <Gamepad2 className="w-9 h-9 text-black" />
                </div>
                <span className="text-[20px] font-black text-black leading-tight">ଗଣିତ ଖେଳ</span>
                <span className="text-[13px] font-bold text-black/75 mt-0.5">ମଜାଦାର ଖେଳ</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.LEARNING_CATEGORY)}
                className="h-[175px] rounded-[24px] bg-[#4FC3F7] border-[3px] border-black shadow-[0_8px_0_#000] p-4 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click"
              >
                <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center mb-3">
                  <BookOpen className="w-9 h-9 text-black" />
                </div>
                <span className="text-[20px] font-black text-black leading-tight">ଶିକ୍ଷା</span>
                <span className="text-[13px] font-bold text-black/75 mt-0.5">ପଣିକିଆ ଓ ଗଣନା</span>
              </button>
            </div>

            {/* Row 2: One Wide Card */}
            <button
              onClick={() => onNavigate(Screen.UTILITY_CATEGORY)}
              className="w-full h-[135px] rounded-[24px] bg-[#AED581] border-[3px] border-black shadow-[0_8px_0_#000] px-6 py-4 flex items-center justify-start gap-6 text-left cursor-pointer bouncing-click"
            >
              <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center shrink-0">
                <Wrench className="w-9 h-9 text-black" />
              </div>
              <div>
                <div className="text-[22px] font-black text-black">ଉପଯୋଗିତା</div>
                <div className="text-[14px] font-bold text-black/75 mt-0.5">କାଲକୁଲେଟର ଓ ମାପ</div>
              </div>
            </button>
          </div>
        </div>

        {/* 3. FOOTER */}
        <div className="pt-10 pb-2">
          <p className="text-[32px] font-black text-black/25 leading-[38px] whitespace-pre-line">
            {"ଗଣିତ ଶିଖିବା ଏବେ\nଆହୁରି ସହଜ..."}
          </p>
        </div>
      </div>
    </div>
  );
};

// --- 2. GAMES CATEGORY SCREEN (Exact match with GamesCategoryScreen_c925a8f6.kt) ---
export const GamesCategoryScreen: React.FC<{
  onBack: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onBack, onNavigate }) => {
  const games = [
    {
      screen: Screen.GAME_MATH_QUIZ,
      title: 'ଗଣିତ କୁଇଜ୍',
      subtitle: 'ମିଶାଣ ଓ ଫେଡ଼ାଣ',
      bg: 'bg-[#FF8A80]',
      badge: '? + ?'
    },
    {
      screen: Screen.GAME_LOGIC,
      title: 'ବୁଦ୍ଧି ଖେଳ',
      subtitle: 'ବେଲୁନ୍ ଫୁଟାଅ',
      bg: 'bg-[#80D8FF]',
      badge: '🎈'
    },
    {
      screen: Screen.GAME_WORD_PROBLEM,
      title: 'ଗାଣିତିକ ପ୍ରଶ୍ନ',
      subtitle: 'ବୁଝି କରି ଉତ୍ତର ଦିଅ',
      bg: 'bg-[#FFD180]',
      badge: '📖'
    },
    {
      screen: Screen.GAME_SUDOKU,
      title: 'ସୁଡୋକୁ',
      subtitle: 'ସଂଖ୍ୟା ସଜାଅ',
      bg: 'bg-[#CCFF90]',
      badge: '🧩'
    },
    {
      screen: Screen.GAME_FTMN,
      title: 'ସଂଖ୍ୟା ଖୋଜ',
      subtitle: 'ହଜିଯାଇଥିବା ସଂଖ୍ୟା',
      bg: 'bg-[#CF94DA]',
      badge: '🔍'
    },
    {
      screen: Screen.GAME_SLIDING_PUZZLE,
      title: 'ସଂଖ୍ୟା ଖସଡ଼ା',
      subtitle: 'ସଂଖ୍ୟା ସଜାଅ',
      bg: 'bg-[#80CBC4]',
      badge: '🔢'
    }
  ];

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      <header className="sticky top-0 z-20 bg-[#FFEB3B] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[24px] font-black text-black">ଗଣିତ ଖେଳ</h1>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-5">
        <h2 className="text-[20px] font-black text-black mb-5">ଆସ ଖେଳିବା! (Choose a Game)</h2>

        <div className="grid grid-cols-2 gap-4">
          {games.map(g => (
            <button
              key={g.screen}
              onClick={() => onNavigate(g.screen)}
              className={`${g.bg} h-[180px] rounded-[24px] border-[3px] border-black shadow-[0_8px_0_#000] p-3 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click`}
            >
              <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center text-2xl font-black text-black mb-3">
                {g.badge}
              </div>
              <span className="text-[18px] font-black text-black leading-tight">{g.title}</span>
              <span className="text-[12px] font-bold text-black/70 mt-1 leading-tight">{g.subtitle}</span>
            </button>
          ))}
        </div>
      </main>
    </div>
  );
};

// --- 3. LEARNING CATEGORY SCREEN (Exact match with LearningCategoryScreen_82b2468e.kt) ---
export const LearningCategoryScreen: React.FC<{
  onBack: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onBack, onNavigate }) => {
  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      <header className="sticky top-0 z-20 bg-[#29B6F6] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[24px] font-black text-white">ଗଣିତ ଶିକ୍ଷା</h1>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-5">
        <h2 className="text-[20px] font-black text-black mb-5">ଆସ ପଢିବା ଓ ଶିଖିବା!</h2>

        <div className="space-y-4">
          {/* Row 1: Two square cards */}
          <div className="grid grid-cols-2 gap-4">
            <button
              onClick={() => onNavigate(Screen.LEARNING_PANIKIA)}
              className="h-[180px] rounded-[24px] bg-[#AB47BC] border-[3px] border-black shadow-[0_8px_0_#000] p-3 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click"
            >
              <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center mb-3 text-2xl">
                🔢
              </div>
              <span className="text-[20px] font-black text-white">ପଣିକିଆ</span>
              <span className="text-[12px] font-bold text-white/90 mt-1">୨ ରୁ ୨୫ ପଣିକିଆ</span>
            </button>

            <button
              onClick={() => onNavigate(Screen.LEARNING_ORDINALS)}
              className="h-[180px] rounded-[24px] bg-[#FFA726] border-[3px] border-black shadow-[0_8px_0_#000] p-3 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click"
            >
              <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center mb-3 text-[22px] font-black text-black">
                ୧ମ
              </div>
              <span className="text-[20px] font-black text-white">କ୍ରମିକ ସଂଖ୍ୟା</span>
              <span className="text-[12px] font-bold text-white/90 mt-1">ପ୍ରଥମ, ଦ୍ୱିତୀୟ...</span>
            </button>
          </div>

          {/* Row 2: Full width card */}
          <button
            onClick={() => onNavigate(Screen.LEARNING_COUNTING)}
            className="w-full h-[180px] rounded-[24px] bg-[#66BB6A] border-[3px] border-black shadow-[0_8px_0_#000] p-4 flex flex-col items-center justify-center text-center cursor-pointer bouncing-click"
          >
            <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center mb-3 text-[22px] font-black text-black">
              ଏକ
            </div>
            <span className="text-[22px] font-black text-white">ଗଣନା ପ୍ରଣାଳୀ</span>
            <span className="text-[13px] font-bold text-white/90 mt-1">ଏକ, ଦଶ, ଶହ, ହଜାର, ଲକ୍ଷ...</span>
          </button>
        </div>
      </main>
    </div>
  );
};

// --- 4. UTILITY CATEGORY SCREEN (Exact match with UtilityCategory_7f540e76.kt) ---
export const UtilityCategoryScreen: React.FC<{
  onBack: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onBack, onNavigate }) => {
  const items = [
    {
      screen: Screen.UTILITY_DIGITAL_BAJARA,
      title: 'ଡିଜିଟାଲ୍ ବଜାର',
      subtitle: 'ଦୋକାନ ହିସାବ ଓ ବଜାର ଫର୍ଦ୍ଦ',
      bg: 'bg-[#FFCA28]',
      icon: ShoppingBag
    },
    {
      screen: Screen.UTILITY_CONVERTER,
      title: 'ଏକକ ପରିବର୍ତ୍ତନ',
      subtitle: 'ଲମ୍ବ, ଓଜନ ଓ ସମୟ ମାପ',
      bg: 'bg-[#26A69A]',
      icon: ArrowRightLeft
    },
    {
      screen: Screen.UTILITY_NUM_TO_TEXT,
      title: 'ସଂଖ୍ୟାରୁ ଶବ୍ଦ',
      subtitle: 'ସଂଖ୍ୟାକୁ ଅକ୍ଷରରେ ଲେଖ (୧ -> ଏକ)',
      bg: 'bg-[#7E57C2]',
      icon: FileText
    },
    {
      screen: Screen.UTILITY_CALCULATOR,
      title: 'ଓଡ଼ିଆ କାଲକୁଲେଟର',
      subtitle: 'ମିଶାଣ, ଫେଡ଼ାଣ ଓ ହିସାବ ଯନ୍ତ୍ର',
      bg: 'bg-[#EF5350]',
      icon: Calculator
    }
  ];

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      <header className="sticky top-0 z-20 bg-[#9CCC65] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[24px] font-black text-black">ଉପଯୋଗିତା</h1>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-5 space-y-5">
        <h2 className="text-[20px] font-black text-black">ଦରକାରୀ ଯନ୍ତ୍ରପାତି (Useful Tools)</h2>

        {items.map(it => {
          const Icon = it.icon;
          return (
            <button
              key={it.screen}
              onClick={() => onNavigate(it.screen)}
              className={`w-full h-[125px] ${it.bg} rounded-[24px] border-[3px] border-black shadow-[0_8px_0_#000] px-6 flex items-center gap-5 text-left cursor-pointer bouncing-click`}
            >
              <div className="w-16 h-16 rounded-full bg-white border-2 border-black flex items-center justify-center shrink-0">
                <Icon className="w-8 h-8 text-black" />
              </div>
              <div>
                <h3 className="text-[22px] font-black text-white drop-shadow-[0_1px_1px_rgba(0,0,0,0.4)]">{it.title}</h3>
                <p className="text-[14px] font-bold text-white/95 mt-1">{it.subtitle}</p>
              </div>
            </button>
          );
        })}
      </main>
    </div>
  );
};

// --- 5. PANIKIA SCREEN (Exact match with PanikiaScreen_9c69ad4c.kt: Grid 2..25 -> Table Detail) ---
export const PanikiaScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [selectedNumber, setSelectedNumber] = useState<number | null>(null);
  const numbers = useMemo(() => Array.from({ length: 24 }, (_, i) => i + 2), []);

  const rows = useMemo(
    () => (selectedNumber !== null ? getPanikiaTableData(selectedNumber) : []),
    [selectedNumber]
  );

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      <header className="sticky top-0 z-20 bg-[#FFEB3B] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-3xl mx-auto flex items-center gap-3">
          <button
            onClick={() => {
              if (selectedNumber !== null) {
                setSelectedNumber(null);
              } else {
                onBack();
              }
            }}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[22px] font-black text-black">
            {selectedNumber === null ? 'ପଣିକିଆ (୨ - ୨୫)' : `${toOdiaNum(selectedNumber)} ପଣିକିଆ`}
          </h1>
        </div>
      </header>

      <main className="max-w-3xl mx-auto p-4">
        {selectedNumber === null ? (
          <div>
            <p className="text-[18px] font-black text-black mb-4 ml-1">ଗୋଟିଏ ସଂଖ୍ୟା ବାଛନ୍ତୁ:</p>
            <div className="grid grid-cols-4 gap-3">
              {numbers.map(num => {
                const bg =
                  num % 3 === 0
                    ? 'bg-[#00E676]'
                    : num % 2 === 0
                    ? 'bg-[#29B6F6]'
                    : 'bg-[#AB47BC]';
                return (
                  <button
                    key={num}
                    onClick={() => setSelectedNumber(num)}
                    className={`aspect-square ${bg} rounded-[18px] border-[2.5px] border-black shadow-[0_6px_0_#000] flex items-center justify-center text-[32px] font-black text-white cursor-pointer bouncing-click`}
                  >
                    {toOdiaNum(num)}
                  </button>
                );
              })}
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Section 1: ସଂଖ୍ୟା ତାଲିକା (Numerical Table View) */}
            <div className="space-y-2">
              <span className="inline-block bg-[#29B6F6] text-white border-2 border-black rounded-lg px-3 py-1 text-[14px] font-black">
                ସଂଖ୍ୟା ତାଲିକା
              </span>
              <div className="bg-white rounded-[20px] border-[3px] border-black shadow-[0_8px_0_#000] p-4 divide-y divide-slate-200">
                {rows.map(row => (
                  <div
                    key={row.multiplier}
                    onClick={() => playOdiaAudio(row.scriptText, true)}
                    className="py-2.5 flex items-center justify-between text-[22px] font-bold cursor-pointer hover:bg-amber-50 px-2 rounded-lg"
                  >
                    <div className="flex items-center gap-3">
                      <span className="font-black text-black">{toOdiaNum(selectedNumber)}</span>
                      <span className="text-slate-500">×</span>
                      <span className="font-black text-black">{toOdiaNum(row.multiplier)}</span>
                      <span className="text-slate-500">=</span>
                    </div>
                    <span className="text-[24px] font-black text-[#AB47BC]">
                      {toOdiaNum(row.product)}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Section 2: ଶବ୍ଦ ତାଲିକା (Script Table View) */}
            <div className="space-y-2">
              <span className="inline-block bg-[#00E676] text-black border-2 border-black rounded-lg px-3 py-1 text-[14px] font-black">
                ଶବ୍ଦ ତାଲିକା (ଉଚ୍ଚାରଣ)
              </span>
              <div className="bg-[#FFF9C4] rounded-[20px] border-[3px] border-black shadow-[0_8px_0_#000] p-4 divide-y divide-black/10">
                {rows.map(row => (
                  <div
                    key={row.multiplier}
                    onClick={() => playOdiaAudio(row.scriptText, true)}
                    className="py-2.5 flex items-center justify-between gap-2 cursor-pointer hover:bg-amber-100/70 px-2 rounded-lg"
                  >
                    <span className="text-[19px] font-bold text-black leading-snug">
                      {row.scriptText}
                    </span>
                    <Volume2 className="w-5 h-5 text-black/60 shrink-0" />
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

// --- 6. ORDINALS SCREEN (Exact match with OrdinalsScreen_dd15b0d8.kt) ---
export const OrdinalsScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-[#F3E5F5] pb-20 flex flex-col">
      <header className="sticky top-0 z-20 bg-[#AB47BC] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-3xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[22px] font-black text-white">କ୍ରମିକ ସଂଖ୍ୟା (୧ମ - ୧୦୦ତମ)</h1>
        </div>
      </header>

      <div className="max-w-3xl w-full mx-auto px-4 pt-3 flex-1">
        {/* Sticky Table Header */}
        <div className="sticky top-[67px] z-10 h-[50px] rounded-[12px] bg-[#FFCA28] border-[2.5px] border-black shadow-[0_4px_0_#000] px-4 flex items-center font-black text-[18px] text-black mb-3">
          <span className="w-1/4 text-center">ସଂଖ୍ୟା</span>
          <span className="w-2/4 text-center">ଶବ୍ଦ</span>
          <span className="w-1/4 text-center">ଚିହ୍ନ</span>
        </div>

        {/* Rows */}
        <div className="space-y-2.5">
          {ordinalData.map(item => (
            <div
              key={item.num}
              onClick={() => playOdiaAudio(item.word, true)}
              className="min-h-[60px] rounded-[16px] bg-white border-2 border-black shadow-[0_4px_0_#000] px-3 py-2 flex items-center justify-between cursor-pointer hover:bg-purple-50 transition"
            >
              {/* 1. Number Badge */}
              <div className="w-1/4 flex justify-center">
                <div className="w-10 h-10 rounded-full bg-[#29B6F6] border-2 border-black flex items-center justify-center font-black text-[18px] text-white">
                  {item.num}
                </div>
              </div>

              {/* 2. Word */}
              <div className="w-2/4 text-center font-bold text-[19px] text-black px-2">
                {item.word}
              </div>

              {/* 3. Short Form Badge */}
              <div className="w-1/4 flex justify-center">
                <div className="px-3 py-1 rounded-[8px] bg-[#E1BEE7] border-[1.5px] border-black font-black text-[16px] text-[#4A148C]">
                  {item.shortForm}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

// --- 7. COUNTING SYSTEM SCREEN (Exact match with CountingScreen_6c0d7151.kt) ---
export const CountingScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-[#E8EAF6] pb-20">
      <header className="sticky top-0 z-20 bg-[#3949AB] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-3xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[22px] font-black text-white">ଓଡ଼ିଆ ଗଣନା ପ୍ରଣାଳୀ</h1>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 pt-3 space-y-3">
        {/* Table Header */}
        <div className="sticky top-[67px] z-10 h-[50px] rounded-[12px] bg-[#00E5FF] border-[2.5px] border-black shadow-[0_4px_0_#000] px-4 flex items-center justify-between font-black text-[18px] text-black">
          <span>ସଂଖ୍ୟା (Number)</span>
          <span>ନାମ (Name)</span>
        </div>

        {/* Rows */}
        <div className="space-y-3">
          {gananaData.map(item => (
            <div
              key={item.name}
              onClick={() => playOdiaAudio(item.name, true)}
              className="rounded-[16px] bg-white border-[2.5px] border-black shadow-[0_5px_0_#000] p-4 flex items-center justify-between gap-3 cursor-pointer hover:bg-indigo-50/50 transition"
            >
              {/* Left Side: Number + Power Badge */}
              <div className="flex-1 min-w-0 space-y-1.5">
                <div className="text-[16px] sm:text-[18px] font-bold text-[#37474F] break-all leading-snug">
                  {item.num}
                </div>
                <div className="flex items-center gap-2">
                  <span className="px-2 py-0.5 rounded-[6px] bg-[#FFECB3] border border-black font-black text-[12px] text-black">
                    {item.power}
                  </span>
                  <span className="text-[12px] font-bold text-slate-500">({item.zeros})</span>
                </div>
              </div>

              {/* Right Side: Name */}
              <div className="text-right shrink-0 pl-2">
                <span className="text-[24px] font-black text-[#3949AB]">{item.name}</span>
              </div>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

// --- 8. NUMBER TO TEXT SCREEN (Exact 1:1 with NumberToTextScreen_e090c7e5.kt) ---
export const NumberToTextScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [inputNumber, setInputNumber] = useState('');
  const [isChequeOdia, setIsChequeOdia] = useState(true);
  const [copiedKey, setCopiedKey] = useState<string | null>(null);

  const cleanInput = fromOdiaNum(inputNumber).replace(/\D/g, '').slice(0, 10);
  const numberVal = cleanInput ? parseInt(cleanInput, 10) : null;

  const odiaText = useMemo(() => {
    if (!cleanInput) return '';
    if (numberVal === null || Number.isNaN(numberVal)) return 'ସଂଖ୍ୟାଟି ବହୁତ ବଡ଼ ଅଟେ';
    return convertToOdiaWords(numberVal);
  }, [cleanInput, numberVal]);

  const englishText = useMemo(() => {
    if (!cleanInput) return '';
    if (numberVal === null || Number.isNaN(numberVal)) return 'Number too large';
    return convertToEnglishWords(numberVal);
  }, [cleanInput, numberVal]);

  const handleCopy = (text: string, key: string) => {
    navigator.clipboard?.writeText(text);
    setCopiedKey(key);
    setTimeout(() => setCopiedKey(null), 1600);
  };

  return (
    <div className="min-h-screen bg-[#E8F5E9] pb-20">
      {/* Top Bar */}
      <header className="sticky top-0 z-20 bg-[#43A047] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[22px] font-black text-white">ସଂଖ୍ୟାରୁ ଶବ୍ଦ (Number to Text)</h1>
        </div>
      </header>

      <main className="max-w-xl mx-auto p-5 space-y-6">
        {/* 1. INPUT SECTION */}
        <div>
          <label className="block text-[18px] font-black text-black mb-2 ml-1">
            ସଂଖ୍ୟା ଲେଖନ୍ତୁ (Enter Number):
          </label>
          <div className="h-[75px] rounded-[20px] bg-white border-[3px] border-black shadow-[0_6px_0_#000] px-5 flex items-center justify-between">
            <input
              type="text"
              inputMode="numeric"
              value={toOdiaNum(cleanInput)}
              onChange={e => {
                const digits = fromOdiaNum(e.target.value).replace(/\D/g, '');
                if (digits.length <= 10) {
                  setInputNumber(digits);
                }
              }}
              placeholder="ଉଦାହରଣ: ୧୨୫ (Ex: 125)"
              className="w-full text-[30px] font-black text-black placeholder:text-slate-400 placeholder:text-[20px] placeholder:font-bold focus:outline-none bg-transparent"
            />
            {cleanInput.length > 0 && (
              <button
                onClick={() => setInputNumber('')}
                aria-label="ସଫା କରନ୍ତୁ"
                className="w-9 h-9 rounded-full bg-[#FFCDD2] flex items-center justify-center cursor-pointer shrink-0 ml-2"
              >
                <X className="w-5 h-5 text-[#D32F2F]" />
              </button>
            )}
          </div>
        </div>

        {/* 2. RESULTS SECTION */}
        {cleanInput.length > 0 && (
          <div className="space-y-5">
            {/* ODIA RESULT CARD (Yellow) */}
            <div className="w-full rounded-[24px] bg-[#FFF176] border-[3px] border-black shadow-[0_8px_0_#000] p-5">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-1 rounded-[8px] bg-black text-white font-bold text-[14px]">
                    {toOdiaNum(cleanInput)}
                  </span>
                  <span className="text-[14px] font-black text-black/60">ଓଡ଼ିଆ</span>
                </div>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => playOdiaAudio(odiaText, true)}
                    aria-label="ଓଡ଼ିଆ ଉଚ୍ଚାରଣ ଶୁଣନ୍ତୁ"
                    className="w-9 h-9 rounded-full bg-white border-[1.5px] border-black flex items-center justify-center cursor-pointer"
                  >
                    <Volume2 className="w-4 h-4 text-black" />
                  </button>
                  <button
                    onClick={() => handleCopy(odiaText, 'odia')}
                    aria-label="ଓଡ଼ିଆ ଶବ୍ଦ କପି କରନ୍ତୁ"
                    className="w-9 h-9 rounded-full bg-white border-[1.5px] border-black flex items-center justify-center cursor-pointer"
                  >
                    {copiedKey === 'odia' ? (
                      <Check className="w-4 h-4 text-green-700" />
                    ) : (
                      <Copy className="w-4 h-4 text-black" />
                    )}
                  </button>
                </div>
              </div>
              <hr className="my-3 border-black/15" />
              <p className="text-[24px] font-black text-black leading-[32px]">{odiaText}</p>
            </div>

            {/* ENGLISH RESULT CARD (Light Blue) */}
            <div className="w-full rounded-[24px] bg-[#81D4FA] border-[3px] border-black shadow-[0_8px_0_#000] p-5">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="px-2.5 py-1 rounded-[8px] bg-black text-white font-bold text-[14px]">
                    {cleanInput}
                  </span>
                  <span className="text-[14px] font-black text-black/60">ଇଂରାଜୀ (English)</span>
                </div>
                <button
                  onClick={() => handleCopy(englishText, 'eng')}
                  aria-label="ଇଂରାଜୀ ଶବ୍ଦ କପି କରନ୍ତୁ"
                  className="w-9 h-9 rounded-full bg-white border-[1.5px] border-black flex items-center justify-center cursor-pointer"
                >
                  {copiedKey === 'eng' ? (
                    <Check className="w-4 h-4 text-green-700" />
                  ) : (
                    <Copy className="w-4 h-4 text-black" />
                  )}
                </button>
              </div>
              <hr className="my-3 border-black/15" />
              <p className="text-[24px] font-black text-black leading-[32px]">{englishText}</p>
            </div>

            {/* 3. CHEQUE PREVIEW CARD (White with Toggle) */}
            <div className="w-full rounded-[20px] bg-white border-[2.5px] border-black shadow-[0_6px_0_#000] p-4 space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-1.5">
                  <span className="text-lg">🏦</span>
                  <span className="text-[14px] font-black text-slate-600">
                    ବ୍ୟାଙ୍କ ଫର୍ମାଟ୍ (Bank Format)
                  </span>
                </div>

                {/* Language Switch Button */}
                <button
                  onClick={() => setIsChequeOdia(prev => !prev)}
                  className={`h-[30px] px-2.5 rounded-full border-[1.5px] border-black flex items-center gap-1.5 cursor-pointer transition ${
                    isChequeOdia ? 'bg-[#C8E6C9]' : 'bg-[#BBDEFB]'
                  }`}
                >
                  <span className="text-[11px] font-black text-black">
                    {isChequeOdia ? 'ଓଡ଼ିଆ' : 'Eng'}
                  </span>
                  <span className="w-3 h-3 rounded-full bg-black inline-block" />
                </button>
              </div>

              {/* Grey Amount Box */}
              <div className="w-full rounded-[8px] bg-[#EEEEEE] border border-slate-400 p-2.5">
                <span className="text-[18px] font-black text-black">
                  ₹ {isChequeOdia ? toOdiaNum(cleanInput) : cleanInput}/-
                </span>
              </div>

              <div>
                <p className="text-[16px] font-bold text-black">
                  {isChequeOdia ? `${odiaText} ଟଙ୍କା ମାତ୍ର` : `${englishText} Rupees Only`}
                </p>
                <div className="w-full h-[2px] bg-black/20 mt-1" />
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

// --- 9. UNIT CONVERTER SCREEN (Exact 1:1 with UnitConverterScreen_a73c7281.kt) ---
interface UnitItem {
  id: string;
  odiaName: string;
  englishName: string;
  symbol: string;
  factorToBase: number;
}

interface UnitCategoryDef {
  key: 'Length' | 'Weight' | 'Volume' | 'Temperature' | 'Time' | 'Digital';
  odiaTitle: string;
  emoji: string;
  gradientFrom: string;
  gradientTo: string;
}

const CONVERTER_CATEGORIES: UnitCategoryDef[] = [
  { key: 'Length', odiaTitle: 'ଲମ୍ବ', emoji: '📏', gradientFrom: '#FF9A9E', gradientTo: '#FECFEF' },
  { key: 'Weight', odiaTitle: 'ଓଜନ', emoji: '⚖️', gradientFrom: '#a18cd1', gradientTo: '#fbc2eb' },
  { key: 'Volume', odiaTitle: 'ଧାରଣ କ୍ଷମତା', emoji: '🥛', gradientFrom: '#84fab0', gradientTo: '#8fd3f4' },
  { key: 'Temperature', odiaTitle: 'ତାପମାତ୍ରା', emoji: '🌡️', gradientFrom: '#ff9a9e', gradientTo: '#fecfef' },
  { key: 'Time', odiaTitle: 'ସମୟ', emoji: '⏰', gradientFrom: '#fccb90', gradientTo: '#d57eeb' },
  { key: 'Digital', odiaTitle: 'ଷ୍ଟୋରେଜ୍', emoji: '💾', gradientFrom: '#e0c3fc', gradientTo: '#8ec5fc' }
];

const UNITS_BY_CATEGORY: Record<UnitCategoryDef['key'], UnitItem[]> = {
  Length: [
    { id: 'm', odiaName: 'ମିଟର', englishName: 'Meter', symbol: 'ମି.', factorToBase: 1.0 },
    { id: 'km', odiaName: 'କିଲୋମିଟର', englishName: 'Kilometer', symbol: 'କି.ମି.', factorToBase: 1000.0 },
    { id: 'cm', odiaName: 'ସେଣ୍ଟିମିଟର', englishName: 'Centimeter', symbol: 'ସେ.ମି.', factorToBase: 0.01 },
    { id: 'mm', odiaName: 'ମିଲିମିଟର', englishName: 'Millimeter', symbol: 'ମି.ମି.', factorToBase: 0.001 },
    { id: 'ft', odiaName: 'ଫୁଟ', englishName: 'Feet', symbol: 'ଫୁଟ', factorToBase: 0.3048 },
    { id: 'in', odiaName: 'ଇଞ୍ଚ', englishName: 'Inch', symbol: 'ଇଞ୍ଚ', factorToBase: 0.0254 },
    { id: 'yd', odiaName: 'ଗଜ', englishName: 'Yard', symbol: 'ଗଜ', factorToBase: 0.9144 },
    { id: 'mi', odiaName: 'ମାଇଲ୍', englishName: 'Mile', symbol: 'ମାଇଲ୍', factorToBase: 1609.34 }
  ],
  Weight: [
    { id: 'kg', odiaName: 'କିଲୋଗ୍ରାମ', englishName: 'Kilogram', symbol: 'କି.ଗ୍ରା.', factorToBase: 1000.0 },
    { id: 'g', odiaName: 'ଗ୍ରାମ', englishName: 'Gram', symbol: 'ଗ୍ରା.', factorToBase: 1.0 },
    { id: 'mg', odiaName: 'ମିଲିଗ୍ରାମ', englishName: 'Milligram', symbol: 'ମି.ଗ୍ରା.', factorToBase: 0.001 },
    { id: 'q', odiaName: 'କ୍ୱିଣ୍ଟାଲ', englishName: 'Quintal', symbol: 'କ୍ୱିଣ୍ଟାଲ', factorToBase: 100000.0 },
    { id: 't', odiaName: 'ଟନ୍', englishName: 'Metric Ton', symbol: 'ଟନ୍', factorToBase: 1000000.0 },
    { id: 'lb', odiaName: 'ପାଉଣ୍ଡ', englishName: 'Pound', symbol: 'ପାଉଣ୍ଡ', factorToBase: 453.592 }
  ],
  Volume: [
    { id: 'l', odiaName: 'ଲିଟର', englishName: 'Liter', symbol: 'ଲି.', factorToBase: 1.0 },
    { id: 'ml', odiaName: 'ମିଲିଲିଟର', englishName: 'Milliliter', symbol: 'ମି.ଲି.', factorToBase: 0.001 },
    { id: 'gal', odiaName: 'ଗ୍ୟାଲନ୍', englishName: 'Gallon', symbol: 'ଗ୍ୟାଲନ୍', factorToBase: 3.78541 }
  ],
  Temperature: [
    { id: 'c', odiaName: 'ସେଲସିୟସ୍', englishName: 'Celsius', symbol: '°C', factorToBase: 1.0 },
    { id: 'f', odiaName: 'ଫାରେନହାଇଟ୍', englishName: 'Fahrenheit', symbol: '°F', factorToBase: 1.0 },
    { id: 'k', odiaName: 'କେଲଭିନ୍', englishName: 'Kelvin', symbol: 'K', factorToBase: 1.0 }
  ],
  Time: [
    { id: 's', odiaName: 'ସେକେଣ୍ଡ', englishName: 'Second', symbol: 'ସେକେଣ୍ଡ', factorToBase: 1.0 },
    { id: 'min', odiaName: 'ମିନିଟ୍', englishName: 'Minute', symbol: 'ମିନିଟ୍', factorToBase: 60.0 },
    { id: 'hr', odiaName: 'ଘଣ୍ଟା', englishName: 'Hour', symbol: 'ଘଣ୍ଟା', factorToBase: 3600.0 },
    { id: 'd', odiaName: 'ଦିନ', englishName: 'Day', symbol: 'ଦିନ', factorToBase: 86400.0 },
    { id: 'wk', odiaName: 'ସପ୍ତାହ', englishName: 'Week', symbol: 'ସପ୍ତାହ', factorToBase: 604800.0 },
    { id: 'mo', odiaName: 'ମାସ (୩୦ ଦିନ)', englishName: 'Month', symbol: 'ମାସ', factorToBase: 2592000.0 },
    { id: 'yr', odiaName: 'ବର୍ଷ (୩୬୫ ଦିନ)', englishName: 'Year', symbol: 'ବର୍ଷ', factorToBase: 31536000.0 }
  ],
  Digital: [
    { id: 'b', odiaName: 'ବାଇଟ୍', englishName: 'Byte', symbol: 'B', factorToBase: 1.0 },
    { id: 'kb', odiaName: 'କିଲୋବାଇଟ୍', englishName: 'Kilobyte', symbol: 'KB', factorToBase: 1024.0 },
    { id: 'mb', odiaName: 'ମେଗାବାଇଟ୍', englishName: 'Megabyte', symbol: 'MB', factorToBase: Math.pow(1024.0, 2) },
    { id: 'gb', odiaName: 'ଗିଗାବାଇଟ୍', englishName: 'Gigabyte', symbol: 'GB', factorToBase: Math.pow(1024.0, 3) },
    { id: 'tb', odiaName: 'ଟେରାବାଇଟ୍', englishName: 'Terabyte', symbol: 'TB', factorToBase: Math.pow(1024.0, 4) }
  ]
};

function convertUnitValue(
  input: string,
  from: UnitItem,
  to: UnitItem,
  cat: UnitCategoryDef['key']
): string {
  if (!input || input === '-' || input === '.') return '୦';
  const val = parseFloat(input);
  if (Number.isNaN(val)) return '୦';

  let result = 0;
  if (cat === 'Temperature') {
    const inC =
      from.id === 'c'
        ? val
        : from.id === 'f'
        ? ((val - 32) * 5) / 9
        : val - 273.15;
    result =
      to.id === 'c'
        ? inC
        : to.id === 'f'
        ? (inC * 9) / 5 + 32
        : inC + 273.15;
  } else {
    const inBase = val * from.factorToBase;
    result = inBase / to.factorToBase;
  }

  let formatted = '';
  if (result === 0) {
    formatted = '0';
  } else if (Math.abs(result) < 0.0001 || Math.abs(result) > 1e9) {
    formatted = result.toExponential(3);
  } else if (result % 1 === 0) {
    formatted = String(Math.trunc(result));
  } else {
    formatted = result.toFixed(4).replace(/0+$/, '').replace(/\.$/, '');
  }
  return toOdiaNum(formatted);
}

export const UnitConverterScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [selectedCategory, setSelectedCategory] = useState<UnitCategoryDef['key']>('Length');
  const [fromUnit, setFromUnit] = useState<UnitItem>(UNITS_BY_CATEGORY.Length[0]);
  const [toUnit, setToUnit] = useState<UnitItem>(UNITS_BY_CATEGORY.Length[1]);
  const [inputValue, setInputValue] = useState('1');
  const [sheetSide, setSheetSide] = useState<'FROM' | 'TO' | null>(null);

  useEffect(() => {
    const list = UNITS_BY_CATEGORY[selectedCategory];
    setFromUnit(list[0]);
    setToUnit(list[1] || list[0]);
    setInputValue('1');
  }, [selectedCategory]);

  const resultValue = useMemo(
    () => convertUnitValue(inputValue, fromUnit, toUnit, selectedCategory),
    [inputValue, fromUnit, toUnit, selectedCategory]
  );

  const onKeyPress = (key: string) => {
    if (key === 'C') {
      setInputValue('');
      return;
    }
    if (key === 'BS') {
      setInputValue(prev => (prev.length > 0 ? prev.slice(0, -1) : ''));
      return;
    }
    if (key === '±') {
      if (selectedCategory === 'Temperature') {
        setInputValue(prev => (prev.startsWith('-') ? prev.slice(1) : '-' + prev));
      }
      return;
    }
    if (key === '.') {
      if (!inputValue.includes('.')) {
        setInputValue(prev => (prev === '' ? '0.' : prev + '.'));
      }
      return;
    }
    if (inputValue.replace('-', '').replace('.', '').length < 12) {
      if (inputValue === '0') {
        setInputValue(key);
      } else {
        setInputValue(prev => prev + key);
      }
    }
  };

  return (
    <div className="min-h-screen bg-[#FFFDE7] flex flex-col justify-between pb-4">
      <div>
        {/* Top Bar */}
        <header className="sticky top-0 z-20 bg-[#26A69A] border-b-[3px] border-black px-4 py-3">
          <div className="max-w-xl mx-auto flex items-center gap-3">
            <button
              onClick={onBack}
              aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
              className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5 text-black" />
            </button>
            <h1 className="text-[22px] font-black text-white">ଏକକ ପରିବର୍ତ୍ତନ (Converter)</h1>
          </div>
        </header>

        <main className="max-w-xl mx-auto px-4 pt-3 space-y-4">
          {/* 1. Category Chips Row */}
          <div className="flex gap-3 overflow-x-auto py-1">
            {CONVERTER_CATEGORIES.map(cat => {
              const isSelected = selectedCategory === cat.key;
              return (
                <button
                  key={cat.key}
                  onClick={() => setSelectedCategory(cat.key)}
                  className="flex flex-col items-center shrink-0 cursor-pointer group"
                >
                  <div
                    style={{
                      background: isSelected
                        ? `linear-gradient(135deg, ${cat.gradientFrom}, ${cat.gradientTo})`
                        : '#FFFFFF'
                    }}
                    className={`w-[58px] h-[58px] rounded-full flex items-center justify-center text-2xl transition ${
                      isSelected
                        ? 'border-[2.5px] border-black shadow-[0_4px_0_#000] scale-105'
                        : 'border-[1.5px] border-slate-400 shadow-xs'
                    }`}
                  >
                    {cat.emoji}
                  </div>
                  <span
                    className={`mt-1 text-[12px] ${
                      isSelected ? 'font-black text-black' : 'font-semibold text-slate-600'
                    }`}
                  >
                    {cat.odiaTitle}
                  </span>
                </button>
              );
            })}
          </div>

          {/* 2. FROM & TO DISPLAY CARDS WITH SWAP BUTTON */}
          <div className="relative space-y-2.5">
            {/* FROM CARD */}
            <div className="w-full h-[108px] rounded-[20px] bg-white border-[2.5px] border-black shadow-[0_6px_0_#000] px-5 py-3 flex items-center justify-between">
              <div>
                <span className="text-[11px] font-bold text-slate-500">ଏଠାରୁ (From)</span>
                <button
                  onClick={() => setSheetSide('FROM')}
                  className="flex items-center gap-1.5 mt-0.5 text-left cursor-pointer"
                >
                  <span className="text-[20px] font-black text-black">{fromUnit.odiaName}</span>
                  <span className="text-xs font-black text-black">▼</span>
                </button>
                <span className="text-[12px] font-bold text-slate-500">{fromUnit.englishName}</span>
              </div>

              <div className="text-right">
                <div className="text-[32px] font-black text-black leading-none">
                  {inputValue === '' ? '୦' : toOdiaNum(inputValue)}
                </div>
                <span className="text-[14px] font-bold text-[#26A69A]">{fromUnit.symbol}</span>
              </div>
            </div>

            {/* SWAP BUTTON */}
            <div className="flex justify-center -my-5 relative z-10">
              <button
                onClick={() => {
                  const temp = fromUnit;
                  setFromUnit(toUnit);
                  setToUnit(temp);
                }}
                aria-label="ଏକକ ଅଦଳବଦଳ କରନ୍ତୁ"
                className="w-11 h-11 rounded-full bg-[#FFCA28] border-2 border-black shadow-[0_4px_0_#000] flex items-center justify-center cursor-pointer active:translate-y-0.5"
              >
                <ArrowUpDown className="w-5 h-5 text-black" />
              </button>
            </div>

            {/* TO CARD */}
            <div className="w-full h-[108px] rounded-[20px] bg-[#E0F2F1] border-[2.5px] border-black shadow-[0_6px_0_#000] px-5 py-3 flex items-center justify-between">
              <div>
                <span className="text-[11px] font-bold text-slate-500">ଏହାକୁ (To)</span>
                <button
                  onClick={() => setSheetSide('TO')}
                  className="flex items-center gap-1.5 mt-0.5 text-left cursor-pointer"
                >
                  <span className="text-[20px] font-black text-black">{toUnit.odiaName}</span>
                  <span className="text-xs font-black text-black">▼</span>
                </button>
                <span className="text-[12px] font-bold text-slate-500">{toUnit.englishName}</span>
              </div>

              <div className="text-right">
                <div className="text-[32px] font-black text-[#004D40] leading-none">
                  {resultValue}
                </div>
                <span className="text-[14px] font-bold text-[#26A69A]">{toUnit.symbol}</span>
              </div>
            </div>
          </div>
        </main>
      </div>

      {/* 3. CUSTOM ODIA COMIC KEYBOARD */}
      <div className="max-w-xl w-full mx-auto px-4 pt-3">
        <div className="bg-[#ECEFF1] rounded-t-[24px] rounded-b-[16px] border-[2.5px] border-black p-3 space-y-2">
          {[
            ['1', '2', '3', 'C'],
            ['4', '5', '6', 'BS'],
            ['7', '8', '9', '±'],
            ['.', '0', '00']
          ].map((row, rIdx) => (
            <div key={rIdx} className="flex gap-2 h-[54px]">
              {row.map(key => {
                if (key === '±' && selectedCategory !== 'Temperature') {
                  return <div key={key} className="flex-1" />;
                }
                const isAction = key === 'C' || key === 'BS' || key === '±';
                const bg =
                  key === 'C'
                    ? 'bg-[#FF8A80]'
                    : key === 'BS'
                    ? 'bg-[#CFD8DC]'
                    : key === '±'
                    ? 'bg-[#FFF59D]'
                    : 'bg-white';
                const weightClass = key === '00' ? 'flex-[2]' : 'flex-1';

                return (
                  <button
                    key={key}
                    onClick={() => onKeyPress(key === '00' ? '00' : key)}
                    className={`${weightClass} ${bg} h-full rounded-[12px] border-2 border-black shadow-[0_3px_0_#000] flex items-center justify-center font-black text-[22px] text-black cursor-pointer active:translate-y-0.5`}
                  >
                    {key === 'BS' ? (
                      <Delete className="w-6 h-6 text-black" />
                    ) : isAction || key === '.' ? (
                      key
                    ) : (
                      toOdiaNum(key)
                    )}
                  </button>
                );
              })}
            </div>
          ))}
        </div>
      </div>

      {/* 4. UNIT SELECTION BOTTOM SHEET MODAL */}
      {sheetSide && (
        <div
          onClick={() => setSheetSide(null)}
          className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-end sm:items-center justify-center p-0 sm:p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-[#FFFDE7] w-full max-w-lg rounded-t-[28px] sm:rounded-[28px] border-t-[3px] sm:border-[3px] border-black p-5 max-h-[80vh] flex flex-col"
          >
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-[22px] font-black text-black">ଏକକ ବାଛନ୍ତୁ (Select Unit)</h3>
              <button
                onClick={() => setSheetSide(null)}
                className="w-9 h-9 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-2.5 overflow-y-auto pr-1">
              {UNITS_BY_CATEGORY[selectedCategory].map(unit => {
                const isSelected =
                  sheetSide === 'FROM' ? unit.id === fromUnit.id : unit.id === toUnit.id;
                return (
                  <button
                    key={unit.id}
                    onClick={() => {
                      if (sheetSide === 'FROM') setFromUnit(unit);
                      else setToUnit(unit);
                      setSheetSide(null);
                    }}
                    className={`w-full rounded-[12px] p-3.5 border-2 border-black flex items-center justify-between text-left cursor-pointer ${
                      isSelected ? 'bg-[#FFF59D]' : 'bg-white'
                    }`}
                  >
                    <div>
                      <div className="text-[18px] font-bold text-black">{unit.odiaName}</div>
                      <div className="text-[14px] font-semibold text-slate-500">
                        {unit.englishName}
                      </div>
                    </div>
                    <span className="text-[16px] font-black text-[#26A69A]">{unit.symbol}</span>
                  </button>
                );
              })}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- 10. DIGITAL BAJARA SCREEN (Exact match with DigitalBajara_acde46a4.kt) ---
interface BillItem {
  id: string;
  name: string;
  rate: string;
  qty: string;
  unit: ShopUnit;
  price: number;
}

interface SavedBill {
  id: number;
  date: string;
  items: BillItem[];
  subTotal: number;
  discountPercent: number;
  finalTotal: number;
}

export const DigitalBajaraScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [showAppPrompt, setShowAppPrompt] = useState(true);
  const [mode, setMode] = useState<'NORMAL' | 'LIST' | 'SMART' | 'HISTORY'>('NORMAL');

  // Normal & List states
  const [itemName, setItemName] = useState('');
  const [rate, setRate] = useState('');
  const [qty, setQty] = useState('');
  const [selectedUnit, setSelectedUnit] = useState<ShopUnit>(shopUnits[0]);
  const [billItems, setBillItems] = useState<BillItem[]>([]);
  const [discountInput, setDiscountInput] = useState('');
  const [cashGivenInput, setCashGivenInput] = useState('');
  const [ideaText, setIdeaText] = useState<string | null>(null);
  const [savedBills, setSavedBills] = useState<SavedBill[]>(() => {
    try {
      const raw = localStorage.getItem('odia_bajara_history');
      return raw ? JSON.parse(raw) : [];
    } catch {
      return [];
    }
  });

  // Smart Hisab states
  const [smartBaseQty, setSmartBaseQty] = useState('1');
  const [smartBasePrice, setSmartBasePrice] = useState('');
  const [smartTargetPrice, setSmartTargetPrice] = useState('');
  const [smartTargetQty, setSmartTargetQty] = useState('');
  const [smartUnit, setSmartUnit] = useState<ShopUnit>(shopUnits[0]);

  const currentPrice =
    (parseFloat(fromOdiaNum(rate)) || 0) *
    (parseFloat(fromOdiaNum(qty)) || 0) *
    selectedUnit.factor;

  const subTotalBill = billItems.reduce((acc, i) => acc + i.price, 0);
  const discountPercent = parseFloat(fromOdiaNum(discountInput)) || 0;
  const discountAmount = (subTotalBill * discountPercent) / 100.0;
  const finalTotalBill = subTotalBill - discountAmount;
  const cashGiven = parseFloat(fromOdiaNum(cashGivenInput)) || 0;

  const bQ = parseFloat(fromOdiaNum(smartBaseQty)) || 0;
  const bP = parseFloat(fromOdiaNum(smartBasePrice)) || 0;
  const tP = parseFloat(fromOdiaNum(smartTargetPrice)) || 0;
  const tQ = parseFloat(fromOdiaNum(smartTargetQty)) || 0;
  const smartResQty = bP > 0 ? (bQ / bP) * tP : 0;
  const smartResPrice = bQ > 0 ? (bP / bQ) * tQ : 0;

  const saveBillToHistory = () => {
    if (billItems.length === 0) return;
    const now = new Date();
    const dateStr = now.toLocaleDateString('or-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
    const newBill: SavedBill = {
      id: Date.now(),
      date: dateStr,
      items: billItems,
      subTotal: subTotalBill,
      discountPercent,
      finalTotal: finalTotalBill
    };
    const updated = [newBill, ...savedBills];
    setSavedBills(updated);
    try {
      localStorage.setItem('odia_bajara_history', JSON.stringify(updated));
    } catch {
      // ignore
    }
  };

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      <header className="sticky top-0 z-20 bg-[#FFCA28] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-3xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => {
                if (mode === 'HISTORY' || mode === 'SMART') setMode('NORMAL');
                else onBack();
              }}
              aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
              className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5 text-black" />
            </button>
            <h1 className="text-[22px] font-black text-black">
              {mode === 'HISTORY'
                ? 'ପୁରୁଣା ହିସାବ (History)'
                : mode === 'SMART'
                ? 'ସ୍ମାର୍ଟ ହିସାବ'
                : 'ଡିଜିଟାଲ୍ ବଜାର'}
            </h1>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setShowAppPrompt(true)}
              className="px-3 py-1.5 rounded-full bg-[#6200EE] text-white border-2 border-black font-black text-xs cursor-pointer"
            >
              ଆପ୍ ଖୋଲନ୍ତୁ
            </button>
            <button
              onClick={() => setMode(mode === 'SMART' ? 'NORMAL' : 'SMART')}
              title="ସ୍ମାର୍ଟ ହିସାବ"
              className={`w-10 h-10 rounded-full border-2 border-black flex items-center justify-center cursor-pointer ${
                mode === 'SMART' ? 'bg-[#00E5FF]' : 'bg-white'
              }`}
            >
              <Sparkles className="w-5 h-5 text-black" />
            </button>
            <button
              onClick={() => setMode(mode === 'HISTORY' ? 'NORMAL' : 'HISTORY')}
              title="ପୁରୁଣା ହିସାବ"
              className={`w-10 h-10 rounded-full border-2 border-black flex items-center justify-center cursor-pointer ${
                mode === 'HISTORY' ? 'bg-[#FF8A80]' : 'bg-white'
              }`}
            >
              <History className="w-5 h-5 text-black" />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-3xl mx-auto p-4 space-y-4">
        {mode !== 'HISTORY' && mode !== 'SMART' && (
          <div className="bg-white rounded-[16px] border-[2.5px] border-black p-1 grid grid-cols-2 gap-1">
            <button
              onClick={() => setShowAppPrompt(true)}
              className={`py-2.5 rounded-[12px] font-black text-[15px] cursor-pointer transition ${
                mode === 'NORMAL'
                  ? 'bg-[#29B6F6] text-white border-2 border-black'
                  : 'text-black'
              }`}
            >
              ସାଧାରଣ ହିସାବ
            </button>
            <button
              onClick={() => setShowAppPrompt(true)}
              className={`py-2.5 rounded-[12px] font-black text-[15px] cursor-pointer transition ${
                mode === 'LIST'
                  ? 'bg-[#AB47BC] text-white border-2 border-black'
                  : 'text-black'
              }`}
            >
              ବଜାର ଫର୍ଦ୍ଦ (List)
            </button>
          </div>
        )}

        {(mode === 'NORMAL' || mode === 'LIST') && (
          <div
            onClick={() => setShowAppPrompt(true)}
            className="bg-white border-[3px] border-black rounded-[20px] p-4 shadow-[0_6px_0_#000] space-y-3 cursor-pointer"
          >
            {mode === 'LIST' && (
              <div>
                <label className="text-xs font-black text-slate-700 block mb-1">
                  ଜିନିଷର ନାମ (ଯଥା: ଆଳୁ, ଚିନି)
                </label>
                <input
                  type="text"
                  value={itemName}
                  onChange={e => setItemName(e.target.value)}
                  placeholder="ଜିନିଷର ନାମ..."
                  className="w-full p-2.5 rounded-xl border-2 border-black font-bold"
                />
              </div>
            )}

            {/* Unit Chips */}
            <div>
              <span className="text-[13px] font-bold text-slate-600 block mb-1.5">ଏକକ ବାଛନ୍ତୁ:</span>
              <div className="flex gap-2 overflow-x-auto pb-1">
                {shopUnits.map(u => {
                  const isSel = selectedUnit.name === u.name;
                  return (
                    <button
                      key={u.name}
                      onClick={() => {
                        setSelectedUnit(u);
                        setShowAppPrompt(true);
                      }}
                      className={`px-3 py-1.5 rounded-[10px] border-[1.5px] border-black font-bold text-xs shrink-0 cursor-pointer ${
                        isSel ? 'bg-[#FFF59D] text-black' : 'bg-[#F5F5F5] text-slate-700'
                      }`}
                    >
                      {u.name}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Rate & Qty Row */}
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-black text-slate-700 block mb-1">
                  ଦର (୧ {selectedUnit.baseUnitName} ପିଛା) ₹
                </label>
                <input
                  type="number"
                  value={rate}
                  onChange={e => setRate(e.target.value)}
                  placeholder="୦"
                  className="w-full p-2.5 rounded-xl border-2 border-black font-black text-lg"
                />
              </div>
              <div>
                <label className="text-xs font-black text-slate-700 block mb-1">
                  ପରିମାଣ ({selectedUnit.name.split(' ')[0]})
                </label>
                <input
                  type="number"
                  value={qty}
                  onChange={e => setQty(e.target.value)}
                  placeholder="୦"
                  className="w-full p-2.5 rounded-xl border-2 border-black font-black text-lg"
                />
              </div>
            </div>

            <div className="p-4 rounded-[16px] bg-[#E0F2F1] border-[2.5px] border-black flex items-center justify-between">
              <div>
                <span className="text-[14px] font-bold text-slate-600">ମୋଟ ଟଙ୍କା (Total):</span>
                <div className="text-[32px] font-black text-[#2E7D32]">
                  ₹ {toOdiaNum(currentPrice.toFixed(2))}
                </div>
              </div>
              <button
                onClick={e => {
                  e.stopPropagation();
                  setShowAppPrompt(true);
                }}
                className="px-4 py-2 rounded-xl bg-[#6200EE] text-white border-2 border-black font-black text-sm cursor-pointer"
              >
                ଆପ୍ ରେ ହିସାବ କରନ୍ତୁ
              </button>
            </div>
          </div>
        )}
      </main>

      {/* Open App / Download App Popup */}
      {showAppPrompt && (
        <div
          onClick={onBack}
          className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[28px] max-w-md w-full p-6 shadow-2xl border-[3px] border-black text-center space-y-4 animate-in zoom-in-95 duration-200"
          >
            <div className="w-16 h-16 rounded-2xl bg-[#FFF8E1] border-2 border-black text-[#FF8F00] flex items-center justify-center mx-auto text-3xl shadow-[0_4px_0_#000]">
              🛒
            </div>
            <h3 className="text-[22px] font-black text-slate-900">📱 ଆପ୍ ବ୍ୟବହାର କରନ୍ତୁ</h3>
            <div className="p-3.5 rounded-2xl bg-[#FFFDE7] border-2 border-black">
              <div className="text-lg font-black text-[#E65100]">ଡିଜିଟାଲ୍ ବଜାର (Digital Bajara)</div>
              <div className="text-xs font-bold text-slate-600 mt-0.5">
                ସାଧାରଣ ହିସାବ, ବଜାର ଫର୍ଦ୍ଦ ଓ ସ୍ମାର୍ଟ ହିସାବ
              </div>
            </div>
            <p className="text-sm font-bold text-slate-700 leading-relaxed">
              <strong>ଡିଜିଟାଲ୍ ବଜାର</strong> କେବଳ{' '}
              <strong className="text-[#6200EE]">ଓଡ଼ିଆ ଭାଷା (Odia Bhasa)</strong> ଆପ୍ ଭିତରେ ଉପଲବ୍ଧ
              ଅଟେ। ଏହାକୁ ବ୍ୟବହାର କରିବା ପାଇଁ ଆପ୍ ଖୋଲନ୍ତୁ କିମ୍ବା ଡାଉନଲୋଡ୍ କରନ୍ତୁ।
            </p>
            <div className="flex flex-col gap-2.5 pt-1">
              <a
                href="https://odiabhasa.pages.dev/open?UTILITY_DIGITAL_BAJARA"
                target="_blank"
                rel="noopener noreferrer"
                className="w-full py-3.5 rounded-2xl bg-[#6200EE] text-white font-black text-base shadow-[0_4px_0_#000] border-2 border-black flex items-center justify-center gap-2 hover:bg-[#5000CA] transition"
              >
                🚀 ଆପ୍ ଖୋଲନ୍ତୁ (Open App)
              </a>
              <a
                href="https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa&hl=or"
                target="_blank"
                rel="noopener noreferrer"
                className="w-full py-3.5 rounded-2xl bg-[#00C853] text-white font-black text-base shadow-[0_4px_0_#000] border-2 border-black flex items-center justify-center gap-2 hover:bg-[#00A844] transition"
              >
                📥 ଆପ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ (Download App)
              </a>
              <button
                onClick={onBack}
                className="w-full py-2.5 rounded-xl bg-slate-100 text-slate-700 font-bold text-sm hover:bg-slate-200 transition cursor-pointer"
              >
                ପଛକୁ ଫେରନ୍ତୁ (Go Back)
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Explanation Lightbulb Modal */}
      {ideaText && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-[#FFFDE7] border-[3px] border-black rounded-[20px] max-w-md w-full p-6 shadow-[0_8px_0_#000] space-y-4">
            <div className="flex items-center gap-2">
              <Lightbulb className="w-7 h-7 text-[#FBC02D] fill-[#FBC02D]" />
              <h3 className="text-[20px] font-black text-black">ହିସାବ ବୁଝନ୍ତୁ (Idea)</h3>
            </div>
            <div className="p-4 rounded-2xl bg-white border-2 border-black whitespace-pre-line font-bold text-black leading-relaxed text-[16px]">
              {ideaText}
            </div>
            <button
              onClick={() => setIdeaText(null)}
              className="w-full py-2.5 rounded-2xl bg-[#29B6F6] text-white border-2 border-black font-black cursor-pointer"
            >
              ବୁଝିଗଲି (Got It)
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

// --- 11. ODIA CALCULATOR SCREEN (Fixed 00 under + icon & = col-span-2) ---
export const CalculatorScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [expr, setExpr] = useState('');
  const [result, setResult] = useState('୦');

  const handlePress = (key: string) => {
    if (key === 'C') {
      setExpr('');
      setResult('୦');
      return;
    }
    if (key === '⌫') {
      setExpr(prev => prev.slice(0, -1));
      return;
    }
    if (key === '=') {
      try {
        const sanitized = expr.replace(/×/g, '*').replace(/÷/g, '/');
        // eslint-disable-next-line no-new-func
        const val = Function(`"use strict"; return (${sanitized || 0})`)();
        setResult(toOdiaNum(Number(Number(val).toFixed(4))));
      } catch {
        setResult('ତ୍ରୁଟି');
      }
      return;
    }
    setExpr(prev => prev + key);
  };

  // Row 4 has ['1', '2', '3', '00'] so '00' sits directly under '+'!
  // Row 5 has ['0', '.', '='] where '=' spans 2 columns.
  const rows = [
    ['C', '÷', '×', '⌫'],
    ['7', '8', '9', '-'],
    ['4', '5', '6', '+'],
    ['1', '2', '3', '00'],
    ['0', '.', '=']
  ];

  return (
    <div className="min-h-screen bg-[#E0F7FA] pb-20">
      <header className="sticky top-0 z-20 bg-[#00BCD4] border-b-[3px] border-black px-4 py-3">
        <div className="max-w-md mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>
          <h1 className="text-[22px] font-black text-white">ଓଡ଼ିଆ କାଲକୁଲେଟର</h1>
        </div>
      </header>

      <main className="max-w-md mx-auto p-4 space-y-4">
        {/* Display Screen */}
        <div className="bg-[#C8E6C9] border-[3px] border-black rounded-[24px] p-5 shadow-[0_8px_0_#000] text-right space-y-1">
          <div className="text-xl font-bold text-slate-700 h-8 overflow-x-auto">
            {toOdiaNum(expr) || '୦'}
          </div>
          <div className="text-4xl font-black text-[#1B5E20] overflow-x-auto">
            = {result}
          </div>
        </div>

        {/* Keypad Grid (4 columns, 00 directly under +, = spans 2 columns) */}
        <div className="grid grid-cols-4 gap-3">
          {rows.flat().map(btn => {
            const isOp = ['÷', '×', '-', '+'].includes(btn);
            const isEq = btn === '=';
            const isClear = btn === 'C' || btn === '⌫';
            return (
              <button
                key={btn}
                onClick={() => handlePress(btn)}
                className={`h-16 rounded-[18px] border-[2.5px] border-black font-black text-2xl shadow-[0_5px_0_#000] active:translate-y-0.5 transition cursor-pointer ${
                  isEq
                    ? 'col-span-2 bg-[#00E676] text-black'
                    : isOp
                    ? 'bg-[#FFCA28] text-black'
                    : isClear
                    ? 'bg-[#FF8A80] text-black'
                    : 'bg-white text-slate-900'
                }`}
              >
                {toOdiaNum(btn)}
              </button>
            );
          })}
        </div>
      </main>
    </div>
  );
};
