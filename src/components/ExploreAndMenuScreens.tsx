import React, { useState, useRef, useEffect, useMemo } from 'react';
import {
  ArrowLeft, Volume2, Share2, Mail, Smartphone, X,
  Info, Star, Send, Gamepad2, Globe, ChevronRight, Search,
  ChevronLeft, Calendar as CalendarIcon, ExternalLink, Code2, Copy, Check
} from 'lucide-react';
import {
  Screen,
  lipyantaraGroups,
  deepLinkCategories
} from '../data/odiaData';
import { toOdiaNum, playOdiaAudio } from '../utils/odiaUtils';

// --- REUSABLE APP-REQUIRED SCREEN / MODAL (Open App or Download App) ---
export const AppOnlyFeatureModal: React.FC<{
  featureTitle: string;
  description?: string;
  deepLinkParam: string;
  onClose: () => void;
}> = ({ featureTitle, description, deepLinkParam, onClose }) => {
  const openUrl = `https://odiabhasa.pages.dev/open?${deepLinkParam}`;
  const playStoreUrl = 'https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa&hl=or';

  return (
    <div
      onClick={onClose}
      className="fixed inset-0 z-50 bg-black/85 backdrop-blur-sm flex items-center justify-center p-4"
    >
      <div
        onClick={e => e.stopPropagation()}
        className="bg-white rounded-[24px] p-7 text-center w-full max-w-[400px] shadow-[0_10px_30px_rgba(0,0,0,0.5)]"
      >
        <h2 className="text-[22px] font-bold text-[#2C241B] mb-3 leading-snug">
          📱 ଆପ୍ ବ୍ୟବହାର କରନ୍ତୁ
        </h2>
        <div className="inline-block px-3.5 py-1 rounded-full bg-[#6D5240]/10 text-[#6D5240] font-bold text-sm mb-3">
          {featureTitle}
        </div>
        <p className="text-[#7A7063] text-[16px] leading-relaxed mb-6">
          {description ||
            `ଏହି '${featureTitle}' ବିଭାଗଟି ବ୍ୟବହାର କରିବା ପାଇଁ ଆମର 'ଓଡ଼ିଆ ଭାଷା' ଆପ୍ ଖୋଲନ୍ତୁ କିମ୍ବା ଗୁଗଲ୍ ପ୍ଲେ-ଷ୍ଟୋରରୁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ।`}
        </p>

        <div className="space-y-3">
          <a
            href={openUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="block w-full py-4 rounded-full bg-[#6D5240] text-white text-[16px] font-bold active:scale-96 transition"
          >
            ଆପ୍ ଖୋଲନ୍ତୁ
          </a>
          <a
            href={playStoreUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="block w-full py-4 rounded-full bg-[#E65100] text-white text-[16px] font-bold active:scale-96 transition"
          >
            ଆପ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ
          </a>
          <button
            onClick={onClose}
            className="w-full py-3 rounded-full bg-slate-100 text-[#2C241B] text-[15px] font-bold cursor-pointer hover:bg-slate-200 transition"
          >
            ପଛକୁ ଯାଆନ୍ତୁ
          </button>
        </div>
      </div>
    </div>
  );
};

// --- 1. ODIA CALENDAR SCREEN (Exact match with OdiaCalendar_2e719e70.kt + Live parba.json) ---
interface FestivalEntry {
  name?: string | null;
  color?: string | null;
  ct?: string | null;
}

const ODIA_MONTH_NAMES = [
  'ଜାନୁଆରୀ', 'ଫେବୃଆରୀ', 'ମାର୍ଚ୍ଚ', 'ଏପ୍ରିଲ', 'ମଇ', 'ଜୁନ',
  'ଜୁଲାଇ', 'ଅଗଷ୍ଟ', 'ସେପ୍ଟେମ୍ବର', 'ଅକ୍ଟୋବର', 'ନଭେମ୍ବର', 'ଡିସେମ୍ବର'
];

const ODIA_SOLAR_MONTH_MAP = [
  'ପୌଷ - ମାଘ',
  'ମାଘ - ଫାଲ୍ଗୁନ',
  'ଫାଲ୍ଗୁନ - ଚୈତ୍ର',
  'ଚୈତ୍ର - ବୈଶାଖ',
  'ବୈଶାଖ - ଜ୍ୟେଷ୍ଠ',
  'ଜ୍ୟେଷ୍ଠ - ଆଷାଢ଼',
  'ଆଷାଢ଼ - ଶ୍ରାବଣ',
  'ଶ୍ରାବଣ - ଭାଦ୍ରବ',
  'ଭାଦ୍ରବ - ଆଶ୍ୱିନ',
  'ଆଶ୍ୱିନ - କାର୍ତ୍ତିକ',
  'କାର୍ତ୍ତିକ - ମାର୍ଗଶିର',
  'ମାର୍ଗଶିର - ପୌଷ'
];

function getSeasonInOdia(monthOneBased: number): string {
  switch (monthOneBased) {
    case 3:
    case 4:
      return '🌸 ବସନ୍ତ';
    case 5:
    case 6:
      return '☀️ ଗ୍ରୀଷ୍ମ';
    case 7:
    case 8:
      return '🌧️ ବର୍ଷା';
    case 9:
    case 10:
      return '☁️ ଶରତ';
    case 11:
    case 12:
      return '🍂 ହେମନ୍ତ';
    default:
      return '❄️ ଶୀତ';
  }
}

export const OdiaCalendarScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth()); // 0..11
  const [selectedDay, setSelectedDay] = useState<number>(now.getDate());
  const [festivalMap, setFestivalMap] = useState<Record<string, FestivalEntry>>({});
  const [showAllFestivals, setShowAllFestivals] = useState(false);
  const [festSearch, setFestSearch] = useState('');
  const [timeStr, setTimeStr] = useState('');

  useEffect(() => {
    const updateClock = () => {
      const d = new Date();
      const h = d.getHours();
      const period =
        h >= 4 && h < 12
          ? 'ପୂର୍ବାହ୍ନ'
          : h >= 12 && h < 16
          ? 'ମଧ୍ୟାହ୍ନ'
          : h >= 16 && h < 20
          ? 'ଅପରାହ୍ନ'
          : 'ରାତ୍ରି';
      const h12 = h % 12 === 0 ? 12 : h % 12;
      const m = String(d.getMinutes()).padStart(2, '0');
      const s = String(d.getSeconds()).padStart(2, '0');
      setTimeStr(toOdiaNum(`${period} ${String(h12).padStart(2, '0')}:${m}:${s}`));
    };
    updateClock();
    const timer = setInterval(updateClock, 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    fetch('https://imsbg.github.io/odiabhasa/calendar/parba.json')
      .then(r => r.json())
      .then(json => {
        if (json && json.data && typeof json.data === 'object') {
          setFestivalMap(json.data);
        }
      })
      .catch(() => {});
  }, []);

  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const firstDayOffset = new Date(year, month, 1).getDay();
  const weekDays = ['ରବି', 'ସୋମ', 'ମଙ୍ଗଳ', 'ବୁଧ', 'ଗୁରୁ', 'ଶୁକ୍ର', 'ଶନି'];

  const formatKey = (y: number, mZero: number, d: number) =>
    `${y}-${String(mZero + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;

  const selectedKey = formatKey(year, month, selectedDay);
  const selectedFestival = festivalMap[selectedKey];
  const selectedDow = new Date(year, month, selectedDay).getDay();
  const fullDayNames = ['ରବିବାର', 'ସୋମବାର', 'ମଙ୍ଗଳବାର', 'ବୁଧବାର', 'ଗୁରୁବାର', 'ଶୁକ୍ରବାର', 'ଶନିବାର'];

  const monthFestivals = useMemo(() => {
    const prefix = `${year}-${String(month + 1).padStart(2, '0')}-`;
    return Object.entries(festivalMap)
      .filter(([k, v]) => k.startsWith(prefix) && v?.name)
      .sort(([a], [b]) => a.localeCompare(b));
  }, [festivalMap, year, month]);

  const allYearFestivals = useMemo(() => {
    const prefix = `${year}-`;
    return Object.entries(festivalMap)
      .filter(([k, v]) => k.startsWith(prefix) && v?.name)
      .filter(([k, v]) => {
        if (!festSearch.trim()) return true;
        return (
          v.name?.toLowerCase().includes(festSearch.toLowerCase()) ||
          toOdiaNum(k).includes(festSearch)
        );
      })
      .sort(([a], [b]) => a.localeCompare(b));
  }, [festivalMap, year, festSearch]);

  if (showAllFestivals) {
    return (
      <div className="min-h-screen bg-[#FAF8F5] pb-20">
        <header className="sticky top-0 z-20 bg-[#FAF8F5] border-b border-slate-200 px-4 py-3">
          <div className="max-w-3xl mx-auto flex items-center gap-3">
            <button
              onClick={() => setShowAllFestivals(false)}
              className="w-10 h-10 rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5 text-[#1A1A2E]" />
            </button>
            <h1 className="text-[20px] font-black text-[#1A1A2E]">
              ସମସ୍ତ ପର୍ବପର୍ବାଣି ({toOdiaNum(year)})
            </h1>
          </div>
        </header>

        <main className="max-w-3xl mx-auto p-4 space-y-4">
          <div className="bg-white rounded-full border border-slate-200 px-4 py-2.5 flex items-center gap-2 shadow-xs">
            <Search className="w-4 h-4 text-[#D32F2F]" />
            <input
              type="text"
              value={festSearch}
              onChange={e => setFestSearch(e.target.value)}
              placeholder="ପର୍ବ କିମ୍ବା ତାରିଖ ଖୋଜନ୍ତୁ..."
              className="w-full text-sm font-bold focus:outline-none"
            />
            {festSearch && (
              <button onClick={() => setFestSearch('')} className="cursor-pointer">
                <X className="w-4 h-4 text-slate-400" />
              </button>
            )}
          </div>

          <div className="space-y-2.5">
            {allYearFestivals.map(([dateStr, info]) => {
              const parts = dateStr.split('-');
              const mIdx = parseInt(parts[1], 10) - 1;
              const dNum = parseInt(parts[2], 10);
              return (
                <div
                  key={dateStr}
                  onClick={() => {
                    setMonth(mIdx);
                    setSelectedDay(dNum);
                    setShowAllFestivals(false);
                  }}
                  className="bg-white rounded-[16px] p-4 shadow-xs border border-slate-100 flex items-center justify-between cursor-pointer hover:bg-red-50/40"
                >
                  <div>
                    <h3 className="text-[16px] font-black text-[#1A1A2E]">{info.name}</h3>
                    {info.ct && <p className="text-xs font-bold text-slate-500 mt-0.5">{info.ct}</p>}
                  </div>
                  <span className="px-3 py-1 rounded-xl bg-[#FFEBEE] text-[#D32F2F] font-black text-xs shrink-0">
                    {toOdiaNum(dNum)} {ODIA_MONTH_NAMES[mIdx]}
                  </span>
                </div>
              );
            })}
          </div>
        </main>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#FAF8F5] pb-20">
      <header className="sticky top-0 z-20 bg-[#FAF8F5] px-4 py-3 border-b border-slate-200/80">
        <div className="max-w-3xl mx-auto flex items-center justify-between">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5 text-[#1A1A2E]" />
          </button>

          <div className="text-center">
            <h1 className="text-[20px] font-black text-[#1A1A2E]">ଓଡ଼ିଆ କ୍ୟାଲେଣ୍ଡର</h1>
            <div className="inline-block mt-0.5 px-3 py-0.5 rounded-lg bg-[#D32F2F]/10 text-[#D32F2F] text-[12px] font-bold">
              {timeStr}
            </div>
          </div>

          <button
            onClick={() => {
              const t = new Date();
              setYear(t.getFullYear());
              setMonth(t.getMonth());
              setSelectedDay(t.getDate());
            }}
            className="px-3 py-1.5 rounded-full bg-[#D32F2F]/10 border border-[#D32F2F]/30 text-[#D32F2F] font-black text-xs cursor-pointer"
          >
            ଆଜି
          </button>
        </div>
      </header>

      <main className="max-w-3xl mx-auto p-4 space-y-4">
        <div className="bg-white rounded-[20px] p-4 shadow-xs border border-slate-100 flex items-center justify-between">
          <button
            onClick={() => {
              if (month === 0) {
                setMonth(11);
                setYear(y => y - 1);
              } else {
                setMonth(m => m - 1);
              }
              setSelectedDay(1);
            }}
            aria-label="ପୂର୍ବ ମାସ"
            className="w-9 h-9 rounded-full bg-[#D32F2F]/10 text-[#D32F2F] flex items-center justify-center cursor-pointer"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>

          <div className="text-center">
            <div className="flex items-center justify-center gap-2">
              <select
                value={month}
                onChange={e => {
                  setMonth(Number(e.target.value));
                  setSelectedDay(1);
                }}
                className="text-[20px] font-black text-[#1A1A2E] bg-transparent cursor-pointer focus:outline-none"
              >
                {ODIA_MONTH_NAMES.map((m, idx) => (
                  <option key={m} value={idx}>
                    {m}
                  </option>
                ))}
              </select>
              <select
                value={year}
                onChange={e => {
                  setYear(Number(e.target.value));
                  setSelectedDay(1);
                }}
                className="text-[20px] font-black text-[#D32F2F] bg-transparent cursor-pointer focus:outline-none"
              >
                {[2025, 2026, 2027].map(y => (
                  <option key={y} value={y}>
                    {toOdiaNum(y)}
                  </option>
                ))}
              </select>
            </div>
            <p className="text-[12px] font-bold text-slate-500">
              {ODIA_SOLAR_MONTH_MAP[month]} • {getSeasonInOdia(month + 1)}
            </p>
          </div>

          <button
            onClick={() => {
              if (month === 11) {
                setMonth(0);
                setYear(y => y + 1);
              } else {
                setMonth(m => m + 1);
              }
              setSelectedDay(1);
            }}
            aria-label="ପରବର୍ତ୍ତୀ ମାସ"
            className="w-9 h-9 rounded-full bg-[#D32F2F]/10 text-[#D32F2F] flex items-center justify-center cursor-pointer"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>

        {/* Calendar Grid Card */}
        <div className="bg-white rounded-[24px] p-4 shadow-xs border border-slate-200/80 space-y-2">
          <div className="grid grid-cols-7 gap-1 text-center pb-1">
            {weekDays.map((d, i) => (
              <div
                key={d}
                className={`py-1.5 rounded-lg font-black text-xs sm:text-sm ${
                  i === 0 ? 'bg-[#FFEBEE] text-[#D32F2F]' : 'text-slate-600'
                }`}
              >
                {d}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-7 gap-1.5">
            {Array.from({ length: firstDayOffset }).map((_, idx) => (
              <div key={`empty-${idx}`} className="aspect-square" />
            ))}

            {Array.from({ length: daysInMonth }, (_, i) => i + 1).map(day => {
              const key = formatKey(year, month, day);
              const fest = festivalMap[key];
              const isSunday = (firstDayOffset + day - 1) % 7 === 0;
              const isSelected = selectedDay === day;
              const isToday =
                now.getFullYear() === year &&
                now.getMonth() === month &&
                now.getDate() === day;

              return (
                <button
                  key={day}
                  onClick={() => setSelectedDay(day)}
                  style={{
                    backgroundColor: isSelected
                      ? '#1A1A2E'
                      : fest?.color
                      ? fest.color
                      : isToday
                      ? '#FFEBEE'
                      : isSunday
                      ? '#FFF5F5'
                      : '#FFFFFF'
                  }}
                  className={`aspect-square rounded-[12px] border flex flex-col items-center justify-center p-1 relative cursor-pointer transition ${
                    isSelected
                      ? 'border-[#1A1A2E] text-white scale-105 shadow-sm'
                      : isToday
                      ? 'border-2 border-[#D32F2F] text-[#D32F2F]'
                      : 'border-slate-200 text-[#1A1A2E]'
                  }`}
                >
                  <span
                    className={`text-base sm:text-lg font-black ${
                      !isSelected && isSunday ? 'text-[#D32F2F]' : ''
                    }`}
                  >
                    {toOdiaNum(day)}
                  </span>
                  {fest?.name && (
                    <span
                      className={`text-[8px] font-bold truncate max-w-full px-0.5 ${
                        isSelected ? 'text-amber-300' : 'text-[#D32F2F]'
                      }`}
                    >
                      {fest.name.split(' ')[0]}
                    </span>
                  )}
                </button>
              );
            })}
          </div>
        </div>

        {/* Selected Day Details Card */}
        <div className="bg-white rounded-[20px] p-5 shadow-xs border border-slate-100 space-y-3">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-[20px] font-black text-[#1A1A2E]">
                {toOdiaNum(selectedDay)} {ODIA_MONTH_NAMES[month]} {toOdiaNum(year)}
              </h2>
              <p className="text-sm font-bold text-slate-500">{fullDayNames[selectedDow]}</p>
            </div>
            <button
              onClick={() => setShowAllFestivals(true)}
              className="px-3.5 py-2 rounded-xl bg-[#D32F2F] text-white font-black text-xs flex items-center gap-1.5 cursor-pointer"
            >
              <CalendarIcon className="w-4 h-4" /> ସମସ୍ତ ପର୍ବପର୍ବାଣି
            </button>
          </div>

          {selectedFestival?.name ? (
            <div className="p-3.5 rounded-2xl bg-[#FFEBEE] border border-[#FFCDD2] flex items-center gap-3">
              <span className="text-xl">🎉</span>
              <div>
                <div className="text-xs font-bold text-[#D32F2F]">ଆଜିର ପର୍ବ</div>
                <div className="text-base font-black text-[#B71C1C]">{selectedFestival.name}</div>
              </div>
            </div>
          ) : (
            <p className="text-xs font-bold text-slate-400">ଏହି ଦିନ କୌଣସି ବିଶେଷ ପର୍ବ ନାହିଁ ।</p>
          )}

          {selectedFestival?.ct && (
            <div className="p-3.5 rounded-2xl bg-[#FFF8E1] border border-[#FFE082] text-sm font-bold text-[#5D4037]">
              ✨ {selectedFestival.ct}
            </div>
          )}
        </div>

        {monthFestivals.length > 0 && (
          <div className="bg-white rounded-[20px] p-5 shadow-xs border border-slate-100 space-y-3">
            <h3 className="text-[16px] font-black text-[#1A1A2E]">
              {ODIA_MONTH_NAMES[month]} ମାସର ପର୍ବପର୍ବାଣି ({toOdiaNum(monthFestivals.length)})
            </h3>
            <div className="space-y-2">
              {monthFestivals.map(([k, v]) => {
                const d = parseInt(k.split('-')[2], 10);
                return (
                  <div
                    key={k}
                    onClick={() => setSelectedDay(d)}
                    className="p-3 rounded-xl bg-[#FAF8F5] border border-slate-200/80 flex items-center gap-3 cursor-pointer hover:bg-red-50/40"
                  >
                    <span className="w-9 h-9 rounded-xl bg-[#D32F2F] text-white font-black text-sm flex items-center justify-center shrink-0">
                      {toOdiaNum(d)}
                    </span>
                    <span className="font-bold text-sm text-[#1A1A2E]">{v.name}</span>
                  </div>
                );
              })}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

// --- 2. HARMONIUM SCREEN (Prompts to Open App or Download App per user request) ---
export const HarmoniumScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-[#2C1810] text-white flex flex-col">
      <AppOnlyFeatureModal
        featureTitle="ହାରମୋନିୟମ୍ (Harmonium)"
        description="ହାରମୋନିୟମ୍ ବଜାଇବା ଏବଂ ସୁର ଅଭ୍ୟାସ କରିବା ପାଇଁ ଆମର 'ଓଡ଼ିଆ ଭାଷା' ଆପ୍ ଖୋଲନ୍ତୁ କିମ୍ବା ଗୁଗଲ୍ ପ୍ଲେ-ଷ୍ଟୋରରୁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ।"
        deepLinkParam="HARMONIUM"
        onClose={onBack}
      />
    </div>
  );
};

// --- 3. HISTORY SCREENS (Exact match with HistoryOdisha.kt, HistoryLang.kt & CommonHistoryUi.kt) ---
interface HistoryContentBlock {
  type: 'sectionTitle' | 'subHeading' | 'minorHeading' | 'bodyText' | 'spacer' | 'table';
  text?: string;
  height?: number;
  data?: {
    headers: string[];
    columnWeights?: number[];
    rows: string[][];
  };
}

interface GenericHistoryData {
  version: number;
  title: string;
  imageUrl?: string;
  wikipediaUrl?: string;
  disclaimer?: string;
  content: HistoryContentBlock[];
}

export const HistoryScreen: React.FC<{
  type: 'ODISHA' | 'LANG';
  onBack: () => void;
}> = ({ type, onBack }) => {
  const isOdisha = type === 'ODISHA';
  const [data, setData] = useState<GenericHistoryData | null>(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [showSearch, setShowSearch] = useState(false);
  const [fontSize, setFontSize] = useState(16);

  useEffect(() => {
    setLoading(true);
    const url = isOdisha
      ? 'https://imsbg.github.io/odiabhasa/bahi/more/odisharaitihasa.json'
      : 'https://imsbg.github.io/odiabhasa/bahi/more/odiabhasa.json';

    fetch(url)
      .then(r => r.json())
      .then(json => {
        if (json && Array.isArray(json.content)) {
          setData(json);
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [isOdisha]);

  const filteredBlocks = useMemo(() => {
    if (!data) return [];
    if (!searchQuery.trim()) return data.content;
    const q = searchQuery.toLowerCase();
    return data.content.filter(b => {
      if (b.text && b.text.toLowerCase().includes(q)) return true;
      if (b.data) {
        if (b.data.headers.some(h => h.toLowerCase().includes(q))) return true;
        if (b.data.rows.some(r => r.some(c => c.toLowerCase().includes(q)))) return true;
      }
      return false;
    });
  }, [data, searchQuery]);

  return (
    <div className="min-h-screen bg-[#FAF8F5] pb-20">
      <header className="sticky top-0 z-20 bg-[#6200EE] text-white px-4 py-3 shadow-sm">
        <div className="max-w-3xl mx-auto flex items-center justify-between gap-2">
          <div className="flex items-center gap-3 min-w-0">
            <button
              onClick={onBack}
              aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
              className="w-10 h-10 rounded-full bg-white/15 flex items-center justify-center cursor-pointer shrink-0"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-lg sm:text-xl font-black truncate">
              {data?.title || (isOdisha ? 'ଓଡ଼ିଶାର ଇତିହାସ' : 'ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ')}
            </h1>
          </div>

          <div className="flex items-center gap-1.5 shrink-0">
            <button
              onClick={() => setShowSearch(s => !s)}
              aria-label="ଖୋଜନ୍ତୁ"
              className="w-9 h-9 rounded-full bg-white/15 flex items-center justify-center cursor-pointer"
            >
              <Search className="w-4 h-4" />
            </button>
            <button
              onClick={() => setFontSize(f => (f >= 22 ? 14 : f + 2))}
              title="ଅକ୍ଷର ଆକାର"
              className="px-2.5 h-9 rounded-full bg-white/15 font-black text-xs cursor-pointer"
            >
              ଅ+
            </button>
            {data?.wikipediaUrl && (
              <a
                href={data.wikipediaUrl}
                target="_blank"
                rel="noopener noreferrer"
                title="ଉଇକିପିଡ଼ିଆ"
                className="w-9 h-9 rounded-full bg-white/15 flex items-center justify-center"
              >
                <ExternalLink className="w-4 h-4" />
              </a>
            )}
          </div>
        </div>

        {showSearch && (
          <div className="max-w-3xl mx-auto mt-2">
            <input
              type="text"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              placeholder="ଇତିହାସ ବିଷୟ ଖୋଜନ୍ତୁ..."
              className="w-full px-4 py-2 rounded-xl bg-white text-slate-900 font-bold text-sm focus:outline-none"
            />
          </div>
        )}
      </header>

      <main className="max-w-3xl mx-auto p-4 space-y-4">
        {loading ? (
          <div className="py-16 text-center font-bold text-slate-500">ତଥ୍ୟ ଲୋଡ୍ ହେଉଛି...</div>
        ) : data ? (
          <div className="bg-white rounded-3xl p-5 sm:p-7 shadow-xs border border-slate-200/80 space-y-4">
            {data.imageUrl && !searchQuery && (
              <img
                src={data.imageUrl}
                alt={data.title}
                className="w-full max-h-64 object-cover rounded-2xl border border-slate-200"
              />
            )}

            {filteredBlocks.map((block, idx) => {
              if (block.type === 'sectionTitle') {
                return (
                  <div key={idx} className="pt-4">
                    <h2
                      style={{ fontSize: `${fontSize + 6}px` }}
                      className="font-black text-[#6200EE] border-b-2 border-[#6200EE]/30 pb-1"
                    >
                      {block.text}
                    </h2>
                  </div>
                );
              }
              if (block.type === 'subHeading') {
                return (
                  <h3
                    key={idx}
                    style={{ fontSize: `${fontSize + 3}px` }}
                    className="font-black text-slate-900 pt-2"
                  >
                    {block.text}
                  </h3>
                );
              }
              if (block.type === 'minorHeading') {
                return (
                  <h4
                    key={idx}
                    style={{ fontSize: `${fontSize + 1}px` }}
                    className="font-bold text-[#6200EE] pt-1"
                  >
                    {block.text}
                  </h4>
                );
              }
              if (block.type === 'bodyText') {
                return (
                  <p
                    key={idx}
                    style={{ fontSize: `${fontSize}px` }}
                    className="text-slate-800 leading-relaxed font-medium"
                  >
                    {block.text}
                  </p>
                );
              }
              if (block.type === 'table' && block.data) {
                return (
                  <div key={idx} className="overflow-x-auto my-3 border border-slate-300 rounded-xl">
                    <table className="w-full text-left border-collapse">
                      <thead>
                        <tr className="bg-[#6200EE]/15 text-slate-900">
                          {block.data.headers.map((h, hIdx) => (
                            <th key={hIdx} className="p-2.5 text-xs sm:text-sm font-black border-b border-slate-300">
                              {h}
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-slate-200">
                        {block.data.rows.map((row, rIdx) => (
                          <tr key={rIdx} className={rIdx % 2 === 0 ? 'bg-white' : 'bg-slate-50'}>
                            {row.map((cell, cIdx) => (
                              <td key={cIdx} className="p-2.5 text-xs sm:text-sm font-semibold text-slate-800">
                                {cell}
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                );
              }
              return null;
            })}

            {data.disclaimer && (
              <div className="mt-6 p-3.5 rounded-2xl bg-slate-100 text-xs font-semibold text-slate-500 text-center">
                {data.disclaimer}
              </div>
            )}
          </div>
        ) : (
          <div className="bg-white rounded-3xl p-8 text-center font-bold text-slate-600">
            ଇତିହାସ ତଥ୍ୟ ଲୋଡ୍ ହୋଇପାରିଲା ନାହିଁ ।
          </div>
        )}
      </main>
    </div>
  );
};

// --- 4. LIPYANTARA SCREEN ---
export const LipyantaraScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-20">
      <header className="sticky top-0 z-20 bg-[#F8F9FA] px-4 py-3">
        <div className="max-w-4xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-11 h-11 rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <div>
            <h1 className="text-2xl font-black text-slate-900">ଲିପ୍ୟନ୍ତର</h1>
            <p className="text-xs font-bold text-slate-500">ଓଡ଼ିଆ ବର୍ଣ୍ଣମାଳାର ଉଚ୍ଚାରଣ ଓ ଧ୍ୱନି ସାରଣୀ</p>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto p-4 space-y-6">
        {lipyantaraGroups.map(group => (
          <div key={group.title} className="bg-white border border-slate-200 rounded-3xl p-5 shadow-xs space-y-4">
            <div>
              <h2 className="text-xl font-black text-[#00695C]">{group.title}</h2>
              <p className="text-xs font-bold text-slate-500">{group.subtitle}</p>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              {group.items.map(it => (
                <div
                  key={it.odiaChar}
                  onClick={() => playOdiaAudio(it.odiaChar, true)}
                  className="p-3 rounded-2xl bg-[#E0F2F1] border border-teal-200 flex items-center justify-between cursor-pointer hover:bg-[#B2DFDB]"
                >
                  <span className="text-3xl font-black text-[#004D40]">{it.odiaChar}</span>
                  <div className="text-right">
                    <div className="text-sm font-black text-slate-900">{it.transliteration}</div>
                    <div className="text-xs font-mono text-slate-600">{it.ipa}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </main>
    </div>
  );
};

// --- 5. ASA JANIBA SCREEN (100% Exact Port of User's HTML/CSS/JS with ZERO fake data) ---
const FALLBACK_IMG_SRC =
  "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%237A7063'%3E%3Cpath d='M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z'/%3E%3C/svg%3E";

interface MasterCategory {
  id: 'animals' | 'fruits' | 'vegetables';
  title: string;
  url: string;
  icon: string;
}

const MASTER_CATEGORIES: MasterCategory[] = [
  { id: 'animals', title: 'ପଶୁ ଓ ପକ୍ଷୀ', url: 'https://imsbg.github.io/animals/odia-bhasa/data.json', icon: '🦁' },
  { id: 'fruits', title: 'ଫଳ', url: 'https://imsbg.github.io/fruits-and-vegetables/data/fruits.json', icon: '🥭' },
  { id: 'vegetables', title: 'ପନିପରିବା', url: 'https://imsbg.github.io/fruits-and-vegetables/data/vegetables.json', icon: '🥔' }
];

interface AsaJanibaItem {
  odiaName: string;
  englishName: string;
  scientificName: string;
  image: string;
  category: string;
  descriptionOdia: string;
  facts: string[];
  extraDetails: Record<string, string>;
}

function parseAsaJanibaItems(jsonData: any): AsaJanibaItem[] {
  const arr = Array.isArray(jsonData)
    ? jsonData
    : jsonData?.data || jsonData?.animals || jsonData?.fruits || jsonData?.vegetables || jsonData?.items || [];
  if (!Array.isArray(arr)) return [];

  return arr.map((obj: any) => {
    let facts: string[] = [];
    const extraDetails: Record<string, string> = {};
    if (obj?.details && typeof obj.details === 'object') {
      for (const key in obj.details) {
        if (key === 'ବିଶେଷ_ତଥ୍ୟ' && Array.isArray(obj.details[key])) {
          facts = obj.details[key].map(String);
        } else if (obj.details[key] !== null && obj.details[key] !== undefined) {
          const val = obj.details[key];
          extraDetails[key.replace(/_/g, ' ')] = Array.isArray(val)
            ? val.join(', ')
            : typeof val === 'object'
            ? Object.entries(val)
                .map(([k, v]) => `${k}: ${v}`)
                .join(', ')
            : String(val);
        }
      }
    }
    return {
      odiaName: obj?.odiaName || 'Unknown',
      englishName: obj?.name || 'Unknown',
      scientificName: obj?.scientificName || '',
      image: obj?.image || '',
      category: obj?.category || '',
      descriptionOdia: obj?.descriptionOdia || '',
      facts,
      extraDetails
    };
  });
}

export const AsaJanibaScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [appConfig, setAppConfig] = useState<{
    maintenanceMode: boolean;
    maintenanceMessage: string;
    hiddenTabs: string[];
    hasNotification: boolean;
    notifTitle: string;
    notifMessage: string;
    dataVersion: number;
  }>({
    maintenanceMode: false,
    maintenanceMessage: '',
    hiddenTabs: [],
    hasNotification: false,
    notifTitle: '',
    notifMessage: '',
    dataVersion: 0
  });

  const activeCategories = useMemo(
    () => MASTER_CATEGORIES.filter(c => !appConfig.hiddenTabs.includes(c.id)),
    [appConfig.hiddenTabs]
  );

  const [selectedCategoryId, setSelectedCategoryId] = useState<'animals' | 'fruits' | 'vegetables'>('animals');
  const [currentItems, setCurrentItems] = useState<AsaJanibaItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [freshNetworkData, setFreshNetworkData] = useState<any[] | null>(null);
  const [searchOpen, setSearchOpen] = useState(false);
  const [searchString, setSearchString] = useState('');
  const [notifDotVisible, setNotifDotVisible] = useState(false);

  // Offline cache state
  const [isDownloading, setIsDownloading] = useState(false);
  const [dlProgressPct, setDlProgressPct] = useState(0);
  const [isCategoryDownloaded, setIsCategoryDownloaded] = useState(false);

  // Selection & Sheet state
  const [selectedItem, setSelectedItem] = useState<AsaJanibaItem | null>(null);
  const [mobileSheetOpen, setMobileSheetOpen] = useState(false);
  const [showScrollDownBtn, setShowScrollDownBtn] = useState(false);
  const [fullscreenImg, setFullscreenImg] = useState<string | null>(null);
  const [forceAppModal, setForceAppModal] = useState(false);

  // Custom dialog state
  const [dialogState, setDialogState] = useState<{
    title: string;
    message: string;
    danger?: boolean;
    buttons: { text: string; danger?: boolean; action: () => void }[];
  } | null>(null);

  const sheetScrollRef = useRef<HTMLDivElement | null>(null);
  const searchInputRef = useRef<HTMLInputElement | null>(null);

  // 1. Fetch global config on mount
  useEffect(() => {
    fetch('https://imsbg.github.io/animals/notification.json')
      .then(r => r.json())
      .then(data => {
        const hasNew = Boolean(data?.notification?.hasNew);
        setAppConfig({
          maintenanceMode: Boolean(data?.maintenanceMode),
          maintenanceMessage: data?.maintenanceMessage || '',
          hiddenTabs: Array.isArray(data?.hiddenTabs) ? data.hiddenTabs : [],
          hasNotification: hasNew,
          notifTitle: data?.notification?.title || '',
          notifMessage: data?.notification?.message || '',
          dataVersion: data?.dataVersion || 0
        });
        if (hasNew) setNotifDotVisible(true);
      })
      .catch(() => {});
  }, []);

  // 2. Load category data (Cache + Live Network)
  useEffect(() => {
    const category = activeCategories.find(c => c.id === selectedCategoryId) || MASTER_CATEGORIES[0];
    if (!category) return;

    let active = true;
    setLoading(true);
    setFreshNetworkData(null);

    const cacheKey = `wildlife_cache_${category.id}`;
    const cachedString = localStorage.getItem(cacheKey);
    let cachedVersion = 0;

    if (cachedString) {
      try {
        const parsed = JSON.parse(cachedString);
        cachedVersion = parsed[0]?.version || 0;
        const items = parseAsaJanibaItems(parsed);
        if (items.length > 0 && active) {
          setCurrentItems(items);
          setLoading(false);
        }
      } catch {
        // ignore invalid cache
      }
    }

    if ('caches' in window) {
      caches
        .has(`app_images_${category.id}`)
        .then(has => {
          if (active) setIsCategoryDownloaded(has);
        })
        .catch(() => {});
    }

    fetch(category.url)
      .then(res => res.json())
      .then(rawData => {
        if (!active) return;
        const networkVersion = Array.isArray(rawData) ? rawData[0]?.version || 0 : 0;
        const parsedItems = parseAsaJanibaItems(rawData);

        if (!cachedString || currentItems.length === 0) {
          setCurrentItems(parsedItems);
          try {
            localStorage.setItem(cacheKey, JSON.stringify(rawData));
          } catch {
            // ignore quota errors
          }
          setLoading(false);
        } else if (networkVersion > cachedVersion) {
          setFreshNetworkData(rawData);
        }
      })
      .catch(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [selectedCategoryId, activeCategories]);

  const filteredItems = useMemo(() => {
    if (!searchString.trim()) return currentItems;
    const q = searchString.toLowerCase();
    return currentItems.filter(
      i =>
        i.odiaName.toLowerCase().includes(q) ||
        i.englishName.toLowerCase().includes(q)
    );
  }, [currentItems, searchString]);

  const applyUpdate = () => {
    if (!freshNetworkData) return;
    const cacheKey = `wildlife_cache_${selectedCategoryId}`;
    try {
      localStorage.setItem(cacheKey, JSON.stringify(freshNetworkData));
    } catch {
      // ignore
    }
    setCurrentItems(parseAsaJanibaItems(freshNetworkData));
    setFreshNetworkData(null);
  };

  const executeDownload = async () => {
    if (!('caches' in window)) return;
    setIsDownloading(true);
    setDlProgressPct(0);
    try {
      const cacheName = `app_images_${selectedCategoryId}`;
      const cache = await caches.open(cacheName);
      let count = 0;
      for (const item of currentItems) {
        if (item.image) {
          try {
            await cache.add(item.image);
          } catch {
            // ignore single image failure
          }
        }
        count++;
        setDlProgressPct(Math.floor((count / currentItems.length) * 100));
      }
      setIsCategoryDownloaded(true);
    } catch {
      // ignore
    }
    setIsDownloading(false);
  };

  const triggerDownloadProcess = () => {
    const estMb = toOdiaNum((currentItems.length * 0.15).toFixed(1));
    setDialogState({
      title: 'ଅଫଲାଇନ୍ ଡାଉନଲୋଡ୍',
      message: `ବିନା ଇଣ୍ଟରନେଟରେ ଦେଖିବା ପାଇଁ ଆପଣ ଏହି ବିଭାଗର ସମସ୍ତ ଫଟୋ ଡାଉନଲୋଡ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି? (ପାଖାପାଖି ${estMb} ଏମବି ଡାଟା ଖର୍ଚ୍ଚ ହେବ)`,
      buttons: [
        { text: 'ବାତିଲ୍', action: () => setDialogState(null) },
        {
          text: 'ହଁ, ଡାଉନଲୋଡ୍ କରନ୍ତୁ',
          action: () => {
            setDialogState(null);
            executeDownload();
          }
        }
      ]
    });
  };

  const triggerDeleteProcess = () => {
    setDialogState({
      title: 'ଡିଲିଟ୍ କରନ୍ତୁ',
      message:
        'ଆପଣ ଏହି ବିଭାଗର ସମସ୍ତ ଅଫଲାଇନ୍ ଫଟୋ ଡିଲିଟ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି? ଏହାଦ୍ୱାରା ମୋବାଇଲର ଷ୍ଟୋରେଜ୍ ଖାଲି ହେବ।',
      danger: true,
      buttons: [
        { text: 'ବାତିଲ୍', action: () => setDialogState(null) },
        {
          text: 'ହଁ, ଡିଲିଟ୍ କରନ୍ତୁ',
          danger: true,
          action: async () => {
            setDialogState(null);
            if ('caches' in window) {
              await caches.delete(`app_images_${selectedCategoryId}`);
              setIsCategoryDownloaded(false);
            }
          }
        }
      ]
    });
  };

  const handleOpenItem = (item: AsaJanibaItem) => {
    if (window.innerWidth < 900) {
      const readCount = parseInt(localStorage.getItem('odiaAppReadCount') || '0', 10);
      if (readCount >= 5) {
        setForceAppModal(true);
        return;
      }
      localStorage.setItem('odiaAppReadCount', String(readCount + 1));
    }

    setSelectedItem(item);
    setMobileSheetOpen(true);
    setShowScrollDownBtn(item.facts && item.facts.length > 0);
    setTimeout(() => {
      if (sheetScrollRef.current) {
        sheetScrollRef.current.scrollTop = 0;
      }
    }, 50);
  };

  const checkScroll = () => {
    const area = sheetScrollRef.current;
    if (!area || !selectedItem || selectedItem.facts.length === 0) {
      setShowScrollDownBtn(false);
      return;
    }
    if (area.scrollTop + area.clientHeight >= area.scrollHeight - 50) {
      setShowScrollDownBtn(false);
    } else {
      setShowScrollDownBtn(true);
    }
  };

  return (
    <div className="h-screen flex flex-col overflow-hidden bg-[#F4F1EB] text-[#2C241B] select-none">
      {/* Force App Download Popup (Mobile 5-read limit) */}
      {forceAppModal && (
        <AppOnlyFeatureModal
          featureTitle="ଆସ ଜାଣିବା"
          description="ଆପଣ ମାଗଣାରେ ୫ଟି ତଥ୍ୟ ପଢିସାରିଛନ୍ତି। ଅଧିକ ଜାଣିବା ପାଇଁ ଆମର ଆପ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ କିମ୍ବା ଖୋଲନ୍ତୁ।"
          deepLinkParam="ASA_JANIBA"
          onClose={() => setForceAppModal(false)}
        />
      )}

      {/* Floating Update Button */}
      {freshNetworkData && (
        <button
          onClick={applyUpdate}
          className="fixed top-4 left-1/2 -translate-x-1/2 z-50 bg-[#E65100] text-white px-6 py-3 rounded-full font-bold text-sm flex items-center gap-2 shadow-[0_4px_12px_rgba(230,81,0,0.4)] cursor-pointer"
        >
          <svg className="w-5 h-5 fill-white" viewBox="0 0 24 24">
            <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z" />
          </svg>
          ନୂଆ ତଥ୍ୟ ଆସିଛି - ଅପଡେଟ୍ କରନ୍ତୁ
        </button>
      )}

      {/* Header matching user HTML */}
      <header className="flex items-center justify-between px-4 pt-4 pb-3 bg-[#F4F1EB] h-[70px] shrink-0 z-10">
        {!searchOpen ? (
          <>
            <button
              onClick={onBack}
              aria-label="ମୁଖ୍ୟ ପୃଷ୍ଠା"
              className="p-2.5 mr-1 rounded-full hover:bg-black/5 cursor-pointer flex items-center justify-center"
            >
              <svg className="w-[24px] h-[24px] fill-[#2C241B]" viewBox="0 0 24 24">
                <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
              </svg>
            </button>
            <div className="text-[24px] font-bold text-[#2C241B] flex-1 pl-1">ଆସ ଜାଣିବା</div>
            <button
              onClick={() => {
                setSearchOpen(true);
                setTimeout(() => searchInputRef.current?.focus(), 50);
              }}
              aria-label="ଖୋଜନ୍ତୁ"
              className="p-3 rounded-full hover:bg-black/5 cursor-pointer flex items-center justify-center"
            >
              <svg className="w-6 h-6 fill-[#2C241B]" viewBox="0 0 24 24">
                <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z" />
              </svg>
            </button>
            <div
              onClick={() => {
                setNotifDotVisible(false);
                setDialogState({
                  title: appConfig.notifTitle || 'ସୂଚନା',
                  message: appConfig.notifMessage || 'କୌଣସି ନୂଆ ସୂଚନା ନାହିଁ।',
                  buttons: [{ text: 'ଠିକ୍ ଅଛି', action: () => setDialogState(null) }]
                });
              }}
              className="relative"
            >
              <button
                aria-label="ସୂଚନା"
                className="p-3 rounded-full hover:bg-black/5 cursor-pointer flex items-center justify-center"
              >
                <svg className="w-6 h-6 fill-[#2C241B]" viewBox="0 0 24 24">
                  <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.9 2 2 2zm6-6v-5c0-3.07-1.63-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.64 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2zm-2 1H8v-6c0-2.48 1.51-4.5 4-4.5s4 2.02 4 4.5v6z" />
                </svg>
              </button>
              {notifDotVisible && (
                <span className="absolute top-3 right-3 w-2.5 h-2.5 rounded-full bg-[#E65100]" />
              )}
            </div>
          </>
        ) : (
          <>
            <button
              onClick={() => {
                setSearchOpen(false);
                setSearchString('');
              }}
              aria-label="ସନ୍ଧାନ ବନ୍ଦ କରନ୍ତୁ"
              className="p-3 rounded-full hover:bg-black/5 cursor-pointer flex items-center justify-center"
            >
              <svg className="w-6 h-6 fill-[#2C241B]" viewBox="0 0 24 24">
                <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z" />
              </svg>
            </button>
            <div className="flex-1 flex items-center bg-black/5 rounded-[25px] px-4 h-[50px] ml-2">
              <input
                ref={searchInputRef}
                type="text"
                value={searchString}
                onChange={e => setSearchString(e.target.value)}
                placeholder="ଖୋଜନ୍ତୁ..."
                className="flex-1 text-[16px] text-[#2C241B] placeholder:text-[#7A7063] bg-transparent focus:outline-none"
              />
              {searchString && (
                <button onClick={() => setSearchString('')} className="cursor-pointer">
                  <X className="w-5 h-5 text-[#7A7063]" />
                </button>
              )}
            </div>
          </>
        )}
      </header>

      {/* Maintenance Mode */}
      {appConfig.maintenanceMode ? (
        <div className="flex-1 flex flex-col items-center justify-center p-6 text-center">
          <div className="text-[48px]">⚠️</div>
          <div className="mt-4 text-[18px] font-bold">
            {appConfig.maintenanceMessage || 'ସର୍ଭର ମେଣ୍ଟେନାନ୍ସ ଚାଲିଛି...'}
          </div>
        </div>
      ) : (
        /* App Body: Split Screen on Desktop (>=900px), Single + BottomSheet on Mobile */
        <div className="flex-1 flex flex-col min-[900px]:flex-row overflow-hidden relative min-[900px]:px-6 min-[900px]:pb-6 min-[900px]:gap-6 max-w-[1400px] w-full mx-auto">
          {/* Left Side: List & Categories */}
          <div className="flex-1 overflow-y-auto pb-6 min-[900px]:pb-0 scroll-smooth">
            {/* Categories */}
            <div className="flex gap-3 px-5 pt-4 pb-2 min-[900px]:px-0 min-[900px]:pt-0 min-[900px]:pb-4 overflow-x-auto no-scrollbar">
              {activeCategories.map(cat => {
                const isActive = cat.id === selectedCategoryId;
                return (
                  <button
                    key={cat.id}
                    onClick={() => {
                      if (selectedCategoryId === cat.id) return;
                      setSelectedCategoryId(cat.id);
                      setSearchOpen(false);
                      setSearchString('');
                      setMobileSheetOpen(false);
                      setSelectedItem(null);
                    }}
                    className={`flex items-center gap-2 px-5 h-[48px] rounded-full whitespace-nowrap font-bold text-[16px] cursor-pointer transition ${
                      isActive
                        ? 'bg-[#6D5240] text-white shadow-[0_4px_8px_rgba(109,82,64,0.4)]'
                        : 'bg-white text-[#2C241B] shadow-[0_1px_3px_rgba(0,0,0,0.1)] hover:shadow-md'
                    }`}
                  >
                    <span>{cat.icon}</span>
                    <span>{cat.title}</span>
                  </button>
                );
              })}
            </div>

            {/* Controls Row (Total count and Offline buttons) */}
            {!loading && currentItems.length > 0 && searchString.trim() === '' && (
              <div className="flex justify-between items-center px-5 min-[900px]:px-0 py-2 mb-2">
                <div className="text-[14px] text-[#7A7063] font-medium">
                  ମୋଟ: {toOdiaNum(currentItems.length)}
                </div>

                {isDownloading ? (
                  <div className="text-[#E65100] font-bold text-[13px]">
                    ଡାଉନଲୋଡ୍... {toOdiaNum(dlProgressPct)}%
                  </div>
                ) : isCategoryDownloaded ? (
                  <div className="flex items-center">
                    <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[13px] font-bold bg-[#2E7D32]/10 text-[#2E7D32]">
                      <svg className="w-4 h-4 fill-[#2E7D32]" viewBox="0 0 24 24">
                        <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" />
                      </svg>
                      ସେଭ୍ ହୋଇଛି
                    </div>
                    <button
                      onClick={triggerDeleteProcess}
                      className="p-2 ml-2 rounded-full bg-[#D32F2F]/10 cursor-pointer"
                    >
                      <svg className="w-[18px] h-[18px] fill-[#D32F2F]" viewBox="0 0 24 24">
                        <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
                      </svg>
                    </button>
                  </div>
                ) : (
                  <button
                    onClick={triggerDownloadProcess}
                    className="flex items-center gap-1.5 px-3 py-1.5 rounded-full text-[13px] font-bold bg-[#E65100]/10 text-[#E65100] cursor-pointer active:scale-95 transition"
                  >
                    <svg className="w-4 h-4 fill-[#E65100]" viewBox="0 0 24 24">
                      <path d="M19 9h-4V3H9v6H5l7 7 7-7zM5 18v2h14v-2H5z" />
                    </svg>
                    ଅଫଲାଇନ୍ ସେଭ୍ (~{toOdiaNum((currentItems.length * 0.15).toFixed(1))} ଏମବି)
                  </button>
                )}
              </div>
            )}

            {/* Grid Container */}
            <div className="grid grid-cols-2 min-[900px]:grid-cols-[repeat(auto-fill,minmax(220px,1fr))] gap-4 px-5 min-[900px]:px-0">
              {loading ? (
                Array.from({ length: 6 }).map((_, idx) => (
                  <div
                    key={idx}
                    className="bg-white rounded-[20px] aspect-[0.8] overflow-hidden flex flex-col"
                  >
                    <div className="h-[60%] w-full bg-slate-200 animate-pulse" />
                    <div className="p-3 flex flex-col items-center justify-center gap-2 h-[40%]">
                      <div className="h-4 w-4/5 bg-slate-200 rounded animate-pulse" />
                      <div className="h-3 w-1/2 bg-slate-200 rounded animate-pulse" />
                    </div>
                  </div>
                ))
              ) : filteredItems.length === 0 ? (
                <div className="col-span-full text-center text-[#7A7063] text-[18px] p-10">
                  କିଛି ମିଳିଲା ନାହିଁ 😕
                </div>
              ) : (
                filteredItems.map((item, idx) => (
                  <div
                    key={`${item.odiaName}-${idx}`}
                    onClick={() => handleOpenItem(item)}
                    className="bg-white rounded-[20px] shadow-[0_2px_4px_rgba(0,0,0,0.08)] hover:-translate-y-1 hover:shadow-[0_8px_16px_rgba(0,0,0,0.12)] active:scale-96 transition cursor-pointer flex flex-col aspect-[0.8] overflow-hidden"
                  >
                    <img
                      src={item.image || FALLBACK_IMG_SRC}
                      loading="lazy"
                      onError={e => {
                        const target = e.currentTarget;
                        if (target.src !== FALLBACK_IMG_SRC) {
                          target.src = FALLBACK_IMG_SRC;
                        }
                      }}
                      alt={item.odiaName}
                      className="w-full h-[60%] object-cover bg-[#f0f0f0]"
                    />
                    <div className="p-3 text-center flex flex-col justify-center h-[40%]">
                      <div className="text-[18px] font-bold text-[#2C241B] truncate">
                        {item.odiaName}
                      </div>
                      <div className="text-[12px] text-[#7A7063] truncate mt-1">
                        {item.englishName}
                      </div>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Mobile Overlay */}
          {mobileSheetOpen && selectedItem && (
            <div
              onClick={() => setMobileSheetOpen(false)}
              className="fixed inset-0 bg-black/50 z-40 min-[900px]:hidden"
            />
          )}

          {/* Right Side Details Panel (Desktop) / Bottom Sheet (Mobile) */}
          <div
            className={`fixed bottom-0 left-1/2 -translate-x-1/2 w-full max-w-[600px] h-[85vh] bg-[#F4F1EB] rounded-t-[24px] z-50 transition-transform duration-300 flex flex-col overflow-hidden min-[900px]:static min-[900px]:translate-x-0 min-[900px]:w-[400px] min-[900px]:max-w-[400px] min-[900px]:h-full min-[900px]:rounded-[24px] min-[900px]:bg-white min-[900px]:shadow-[0_4px_20px_rgba(0,0,0,0.06)] min-[900px]:z-10 ${
              mobileSheetOpen && selectedItem
                ? 'translate-y-0'
                : 'translate-y-full min-[900px]:translate-y-0'
            }`}
          >
            <div
              onClick={() => setMobileSheetOpen(false)}
              className="w-10 h-1 bg-[#ccc] rounded my-3 mx-auto shrink-0 min-[900px]:hidden cursor-pointer"
            />

            {!selectedItem ? (
              <div className="hidden min-[900px]:flex flex-col items-center justify-center h-full text-center text-[#7A7063] opacity-70 p-6">
                <span className="text-[64px] mb-4 block">👈</span>
                <h2 className="text-xl font-bold">ବାମ ପାଖରୁ ଏକ ବିଷୟ ବାଛନ୍ତୁ</h2>
                <p className="mt-2 text-[14px]">(Select an item from the left)</p>
              </div>
            ) : (
              <div
                ref={sheetScrollRef}
                onScroll={checkScroll}
                className="flex-1 overflow-y-auto pb-20 relative scroll-smooth"
              >
                {/* Banner Image */}
                <div
                  onClick={() => setFullscreenImg(selectedItem.image || FALLBACK_IMG_SRC)}
                  className="relative w-full h-[250px] cursor-pointer"
                >
                  <img
                    src={selectedItem.image || FALLBACK_IMG_SRC}
                    onError={e => {
                      const target = e.currentTarget;
                      if (target.src !== FALLBACK_IMG_SRC) target.src = FALLBACK_IMG_SRC;
                    }}
                    alt={selectedItem.odiaName}
                    className="w-full h-full object-cover bg-[#eee]"
                  />
                  <div className="absolute bottom-3 right-3 bg-black/60 text-white px-2.5 py-1 rounded-[12px] text-[12px]">
                    🔍 ଟ୍ୟାପ୍ କରନ୍ତୁ
                  </div>
                </div>

                {/* Details Pad */}
                <div className="p-6">
                  {selectedItem.category && (
                    <div className="bg-[#6D5240]/10 text-[#6D5240] px-2.5 py-1 rounded-[8px] text-[12px] inline-block mb-2 font-bold">
                      {selectedItem.category}
                    </div>
                  )}
                  <div className="text-[32px] font-bold mb-1 text-[#2C241B]">
                    {selectedItem.odiaName}
                  </div>
                  <div className="text-[18px] text-[#7A7063] mb-1">
                    {selectedItem.englishName}
                  </div>
                  {selectedItem.scientificName && selectedItem.scientificName !== 'Unknown' && (
                    <div className="text-[14px] italic text-[#7A7063]/80 mb-5">
                      Sc: {selectedItem.scientificName}
                    </div>
                  )}

                  <div className="h-[1px] bg-slate-300/50 mb-4" />

                  <div className="text-[18px] leading-[1.6] mb-6 text-[#2C241B]">
                    {selectedItem.descriptionOdia}
                  </div>

                  {/* Extra Details Box */}
                  {Object.keys(selectedItem.extraDetails).length > 0 && (
                    <div className="bg-[#6D5240]/5 rounded-[16px] p-4 mb-6">
                      <h4 className="text-[18px] font-bold text-[#6D5240] mb-3">ଅଧିକ ତଥ୍ୟ</h4>
                      {Object.entries(selectedItem.extraDetails).map(([k, v]) => (
                        <div key={k} className="flex mb-2 text-[15px]">
                          <div className="w-2/5 font-bold text-[#2C241B]">{k} :</div>
                          <div className="w-3/5 text-[#7A7063]">{toOdiaNum(v)}</div>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* Special Facts */}
                  {selectedItem.facts && selectedItem.facts.length > 0 && (
                    <div>
                      <div className="text-[20px] font-bold text-[#6D5240] mb-3">ବିଶେଷ ତଥ୍ୟ</div>
                      <div>
                        {selectedItem.facts.map((f, i) => (
                          <div
                            key={i}
                            className="flex items-start mb-3 text-[16px] leading-[1.6]"
                          >
                            <span className="mr-2 mt-0.5">🐾</span>
                            <div>{toOdiaNum(f)}</div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}
                </div>

                {/* Scroll Down Button */}
                {showScrollDownBtn && (
                  <button
                    onClick={() => {
                      sheetScrollRef.current?.scrollTo({
                        top: sheetScrollRef.current.scrollHeight,
                        behavior: 'smooth'
                      });
                    }}
                    className="sticky bottom-6 left-1/2 -translate-x-1/2 bg-[#6D5240] text-white px-5 py-3 rounded-full flex items-center gap-2 font-bold shadow-[0_4px_12px_rgba(109,82,64,0.3)] cursor-pointer w-max mx-auto"
                  >
                    ବିଶେଷ ତଥ୍ୟ ପାଇଁ ତଳକୁ ଯାଆନ୍ତୁ
                    <svg className="w-6 h-6 fill-white" viewBox="0 0 24 24">
                      <path d="M7.41 8.59L12 13.17l4.59-4.58L18 10l-6 6-6-6 1.41-1.41z" />
                    </svg>
                  </button>
                )}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Fullscreen Image Overlay */}
      {fullscreenImg && (
        <div
          onClick={() => setFullscreenImg(null)}
          className="fixed inset-0 bg-black z-50 flex items-center justify-center p-4"
        >
          <button
            onClick={() => setFullscreenImg(null)}
            aria-label="ବନ୍ଦ କରନ୍ତୁ"
            className="absolute top-4 right-4 bg-white/20 hover:bg-white/40 rounded-full p-2 cursor-pointer"
          >
            <svg className="w-6 h-6 fill-white" viewBox="0 0 24 24">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12 19 6.41z" />
            </svg>
          </button>
          <img
            src={fullscreenImg}
            alt="Fullscreen"
            className="max-w-full max-h-full object-contain"
          />
        </div>
      )}

      {/* Custom Dialog */}
      {dialogState && (
        <div
          onClick={() => setDialogState(null)}
          className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[20px] p-6 w-full max-w-[400px] shadow-2xl"
          >
            <h3
              className={`text-lg font-bold mb-3 ${
                dialogState.danger ? 'text-[#D32F2F]' : 'text-[#2C241B]'
              }`}
            >
              {dialogState.title}
            </h3>
            <p className="text-[#7A7063] mb-6 leading-relaxed">{dialogState.message}</p>
            <div className="flex justify-end gap-2">
              {dialogState.buttons.map((b, idx) => (
                <button
                  key={idx}
                  onClick={b.action}
                  className={`px-4 py-2.5 rounded-lg font-bold text-[15px] cursor-pointer ${
                    b.danger
                      ? 'text-[#D32F2F] hover:bg-[#D32F2F]/5'
                      : 'text-[#6D5240] hover:bg-[#6D5240]/5'
                  }`}
                >
                  {b.text}
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- 6. MENU SCREEN & ABOUT SCREEN (With Open API v1 Explorer) ---
export const MenuScreen: React.FC<{
  onBack: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onBack, onNavigate }) => {
  const [copiedUrl, setCopiedUrl] = useState<string | null>(null);
  const origin = typeof window !== 'undefined' ? window.location.origin : '';

  const apiEndpoints = [
    { label: 'ସମସ୍ତ ବର୍ଣ୍ଣବୋଧ (All API Index)', path: '/api/v1/' },
    { label: 'ସ୍ୱରବର୍ଣ୍ଣ (ଅ - ଔ)', path: '/api/v1/swarabarna' },
    { label: 'କ - ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ', path: '/api/v1/byanjanabarna' },
    { label: 'ବର୍ଣ୍ଣ - ବର୍ଣ୍ଣ ପରିଚୟ', path: '/api/v1/barna-parichaya' },
    { label: 'ା - ମାତ୍ରା', path: '/api/v1/matra' },
    { label: '୍ୟ - ଫଳା', path: '/api/v1/phala' },
    { label: 'ଙ୍କ - ଯୁକ୍ତାକ୍ଷର', path: '/api/v1/yuktakshara' },
    { label: 'ସଂଖ୍ୟା : ୦ - ୯', path: '/api/v1/sankhya' },
    { label: 'ସଂଖ୍ୟାରୁ ଶବ୍ଦ (Number to Word)', path: '/api/v1/number-to-word?num=125' }
  ];

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-24">
      <header className="sticky top-0 z-20 bg-[#F8F9FA] px-5 pt-4 pb-3">
        <div className="max-w-xl mx-auto flex items-center gap-4">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-6 h-6 text-slate-900" />
          </button>
          <h1 className="text-[28px] font-bold text-slate-900">ଅଧିକ</h1>
        </div>
      </header>

      <main className="max-w-xl mx-auto px-5 space-y-5">
        {/* Section 1: ସାଧାରଣ */}
        <div className="space-y-2.5">
          <h2 className="text-[14px] font-bold text-[#6200EE] px-1">ସାଧାରଣ</h2>

          <div
            onClick={() => onNavigate(Screen.ABOUT)}
            className="bg-white rounded-[16px] p-4 shadow-xs flex items-center gap-4 cursor-pointer bouncing-click"
          >
            <div className="w-[45px] h-[45px] rounded-[12px] bg-[#6200EE]/10 flex items-center justify-center shrink-0">
              <Info className="w-6 h-6 text-[#6200EE]" />
            </div>
            <div className="flex-1">
              <h3 className="text-[16px] font-bold text-slate-900">ଆପ୍ ବିଷୟରେ</h3>
              <p className="text-[13px] text-slate-500">ସଂସ୍କରଣ, ଲକ୍ଷ୍ୟ ଏବଂ ଅନ୍ୟାନ୍ୟ ତଥ୍ୟ</p>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </div>

          <a
            href="https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white rounded-[16px] p-4 shadow-xs flex items-center gap-4 cursor-pointer bouncing-click"
          >
            <div className="w-[45px] h-[45px] rounded-[12px] bg-[#6200EE]/10 flex items-center justify-center shrink-0">
              <Star className="w-6 h-6 text-[#6200EE]" />
            </div>
            <div className="flex-1">
              <h3 className="text-[16px] font-bold text-slate-900">ଆପ୍‌କୁ ମୂଲ୍ୟାଙ୍କନ କରନ୍ତୁ</h3>
              <p className="text-[13px] text-slate-500">ପ୍ଲେ-ଷ୍ଟୋରରେ ୫-ଷ୍ଟାର୍ ଦିଅନ୍ତୁ</p>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </a>

          <a
            href="https://t.me/odiabhasa_app"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white rounded-[16px] p-4 shadow-xs flex items-center gap-4 cursor-pointer bouncing-click"
          >
            <div className="w-[45px] h-[45px] rounded-[12px] bg-[#6200EE]/10 flex items-center justify-center shrink-0">
              <Send className="w-6 h-6 text-[#6200EE]" />
            </div>
            <div className="flex-1">
              <h3 className="text-[16px] font-bold text-slate-900">ଯୋଗାଯୋଗ / ସୂଚନା ପାଆନ୍ତୁ</h3>
              <p className="text-[13px] text-slate-500">ଟେଲିଗ୍ରାମ୍ ଚ୍ୟାନେଲ୍‌ରେ ଯୋଡ଼ି ହୁଅନ୍ତୁ</p>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </a>

          <button
            onClick={() => {
              const shareUrl = 'https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa';
              if (navigator.share) {
                navigator
                  .share({
                    title: 'ଓଡ଼ିଆ ଭାଷା',
                    text: 'ଓଡ଼ିଆ ଭାଷା, ସାହିତ୍ୟ ଏବଂ ବର୍ଣ୍ଣବୋଧ ଶିଖିବା ପାଇଁ ଆଜି ହିଁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ "ଓଡ଼ିଆ ଭାଷା" ଆପ୍:',
                    url: shareUrl
                  })
                  .catch(() => {});
              } else {
                navigator.clipboard?.writeText(shareUrl);
              }
            }}
            className="w-full bg-[#2E7D32] text-white rounded-[16px] p-4 shadow-sm flex items-center gap-4 cursor-pointer bouncing-click text-left"
          >
            <div className="w-[45px] h-[45px] rounded-full bg-white flex items-center justify-center shrink-0">
              <Share2 className="w-5 h-5 text-[#2E7D32]" />
            </div>
            <div>
              <h3 className="text-[18px] font-bold">ସାଙ୍ଗମାନଙ୍କୁ ଜଣାନ୍ତୁ</h3>
              <p className="text-[13px] text-white/85">
                ଆପ୍‌ଟିକୁ ସେୟାର୍ କରି ଭାଷା ପ୍ରଚାରରେ ସାହାଯ୍ୟ କରନ୍ତୁ
              </p>
            </div>
          </button>
        </div>

        {/* Open Web API v1 Section */}
        <div className="bg-white rounded-[20px] p-5 shadow-xs space-y-3 border border-purple-100">
          <div className="flex items-center gap-2.5">
            <div className="w-10 h-10 rounded-xl bg-[#6200EE]/10 flex items-center justify-center">
              <Code2 className="w-5 h-5 text-[#6200EE]" />
            </div>
            <div>
              <h2 className="text-[16px] font-black text-[#6200EE]">
                Open Web API (/api/v1/)
              </h2>
              <p className="text-xs font-semibold text-slate-500">
                ବିନା API Key ରେ ତୁରନ୍ତ JSON ରେଜଲ୍ଟ (No Key Required)
              </p>
            </div>
          </div>

          <div className="space-y-2">
            {apiEndpoints.map(ep => {
              const fullUrl = `${origin}${ep.path}`;
              return (
                <div
                  key={ep.path}
                  className="p-2.5 rounded-xl bg-[#F8F9FA] border border-slate-200 flex items-center justify-between gap-2"
                >
                  <div className="min-w-0">
                    <div className="text-xs font-bold text-slate-900">{ep.label}</div>
                    <a
                      href={ep.path}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-[11px] font-mono text-[#6200EE] hover:underline truncate block"
                    >
                      {ep.path}
                    </a>
                  </div>
                  <div className="flex items-center gap-1.5 shrink-0">
                    <button
                      onClick={() => {
                        navigator.clipboard?.writeText(fullUrl);
                        setCopiedUrl(ep.path);
                        setTimeout(() => setCopiedUrl(null), 1500);
                      }}
                      className="p-1.5 rounded-lg bg-white border border-slate-200 cursor-pointer"
                      title="URL କପି କରନ୍ତୁ"
                    >
                      {copiedUrl === ep.path ? (
                        <Check className="w-3.5 h-3.5 text-green-600" />
                      ) : (
                        <Copy className="w-3.5 h-3.5 text-slate-600" />
                      )}
                    </button>
                    <a
                      href={ep.path}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="px-2.5 py-1 rounded-lg bg-[#6200EE] text-white text-[11px] font-bold"
                    >
                      JSON
                    </a>
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Section 2: ସନ୍ଦୀପ୍ ବିଶ୍ୱାଳ ଜି'ଙ୍କ ଅନ୍ୟ ଆପ୍ */}
        <div className="space-y-2.5">
          <h2 className="text-[14px] font-bold text-[#6200EE] px-1">
            ସନ୍ଦୀପ୍ ବିଶ୍ୱାଳ ଜି&apos;ଙ୍କ ଅନ୍ୟ ଆପ୍
          </h2>

          <a
            href="https://play.google.com/store/apps/details?id=in.sandeep.atomicguru"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white rounded-[16px] p-4 shadow-xs flex items-center gap-4 cursor-pointer bouncing-click"
          >
            <div className="w-[45px] h-[45px] rounded-[12px] bg-[#6200EE]/10 flex items-center justify-center shrink-0">
              <Globe className="w-6 h-6 text-[#6200EE]" />
            </div>
            <div className="flex-1">
              <h3 className="text-[16px] font-bold text-slate-900">ଆଟମିକ୍ ଗୁରୁ</h3>
              <p className="text-[13px] text-slate-500">ବିଜ୍ଞାନ ଓ ପର୍ଯ୍ୟାୟ ସାରଣୀ ଶିଖନ୍ତୁ</p>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </a>

          <a
            href="https://play.google.com/store/apps/details?id=in.sandeep.plixogames"
            target="_blank"
            rel="noopener noreferrer"
            className="bg-white rounded-[16px] p-4 shadow-xs flex items-center gap-4 cursor-pointer bouncing-click"
          >
            <div className="w-[45px] h-[45px] rounded-[12px] bg-[#6200EE]/10 flex items-center justify-center shrink-0">
              <Gamepad2 className="w-6 h-6 text-[#6200EE]" />
            </div>
            <div className="flex-1">
              <h3 className="text-[16px] font-bold text-slate-900">ପ୍ଲିକ୍ସୋ ଗେମ୍ସ୍</h3>
              <p className="text-[13px] text-slate-500">ମଜାଦାର ଖେଳର ସମାହାର</p>
            </div>
            <ChevronRight className="w-4 h-4 text-slate-400" />
          </a>
        </div>

        {/* Section 3: ମୋତେ ଫଲୋ କରନ୍ତୁ */}
        <div className="space-y-2.5">
          <h2 className="text-[14px] font-bold text-[#6200EE] px-1">ମୋତେ ଫଲୋ କରନ୍ତୁ</h2>
          <div className="grid grid-cols-3 gap-2.5">
            <a
              href="https://instagram.com/sandeepbiswalg"
              target="_blank"
              rel="noopener noreferrer"
              className="bg-white rounded-[14px] py-3.5 px-2 shadow-xs flex flex-col items-center justify-center gap-1.5 bouncing-click"
            >
              <span className="text-[14px] font-semibold text-[#E1306C]">ଇନ୍‌ଷ୍ଟାଗ୍ରାମ୍</span>
            </a>
            <a
              href="https://x.com/sandeepbiswalg"
              target="_blank"
              rel="noopener noreferrer"
              className="bg-white rounded-[14px] py-3.5 px-2 shadow-xs flex flex-col items-center justify-center gap-1.5 bouncing-click"
            >
              <span className="text-[14px] font-semibold text-black">ଏକ୍ସ</span>
            </a>
            <a
              href="https://sbgb.pages.dev/sandeep"
              target="_blank"
              rel="noopener noreferrer"
              className="bg-white rounded-[14px] py-3.5 px-2 shadow-xs flex flex-col items-center justify-center gap-1.5 bouncing-click"
            >
              <span className="text-[14px] font-semibold text-[#6200EE]">ସବୁ ପ୍ରୋଫାଇଲ୍</span>
            </a>
          </div>
        </div>

        {/* Deep Link Categories */}
        <div className="space-y-4 pt-2">
          {deepLinkCategories.map(cat => (
            <div key={cat.title} className="bg-white rounded-[20px] p-5 shadow-xs space-y-2.5">
              <h3 className="text-[16px] font-bold text-[#6200EE]">{cat.title}</h3>
              {cat.description && <p className="text-xs text-slate-500">{cat.description}</p>}
              <div className="chip-wrapper flex flex-wrap gap-2">
                {cat.chips.map(chip => (
                  <a
                    key={chip.url}
                    href={chip.url}
                    onClick={e => {
                      e.preventDefault();
                      onNavigate(chip.screen);
                    }}
                    className="chip px-3.5 py-2 rounded-full bg-[#F8F9FA] border border-slate-200 text-xs font-bold text-slate-800 hover:bg-[#6200EE]/10 hover:text-[#6200EE] transition"
                  >
                    {chip.label}
                  </a>
                ))}
              </div>
            </div>
          ))}
        </div>

        <p className="text-center text-[13px] text-slate-400 pt-2">
          ଓଡ଼ିଆ ଭାଷା ଆପ୍ ବ୍ୟବହାର କରୁଥିବାରୁ ଧନ୍ୟବାଦ 🙏
        </p>
      </main>
    </div>
  );
};

export const AboutScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-20">
      <header className="sticky top-0 z-20 bg-[#F8F9FA] px-5 pt-4 pb-3">
        <div className="max-w-2xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-2xl font-black text-slate-900">ଆମ ବିଷୟରେ</h1>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-5 space-y-5">
        <div className="bg-white rounded-3xl p-6 shadow-xs text-center space-y-4">
          <img
            src="/assets/odiabhasa/ic_launcher-playstore.png"
            alt="ଓଡ଼ିଆ ଭାଷା"
            className="w-24 h-24 rounded-3xl mx-auto shadow-md"
          />
          <div>
            <h2 className="text-3xl font-black text-[#6200EE]">ଓଡ଼ିଆ ଭାଷା</h2>
            <p className="text-sm font-bold text-slate-600 mt-1">
              ଓଡ଼ିଆ ଭାଷା, ସାହିତ୍ୟ, ଗଣିତ ଓ ସଂସ୍କୃତିର ସମ୍ପୂର୍ଣ୍ଣ ଡିଜିଟାଲ୍ ପ୍ଲାଟଫର୍ମ
            </p>
          </div>
          <p className="text-sm font-semibold text-slate-700 max-w-xl mx-auto leading-relaxed">
            ପିଲାମାନଙ୍କଠାରୁ ଆରମ୍ଭ କରି ବଡ଼ମାନଙ୍କ ପର୍ଯ୍ୟନ୍ତ ସମସ୍ତଙ୍କ ପାଇଁ ଓଡ଼ିଆ ବର୍ଣ୍ଣବୋଧ, ବିଦ୍ୟାଳୟ ପାଠ୍ୟପୁସ୍ତକ (୧ମ ରୁ ୧୦ମ ଶ୍ରେଣୀ), କୁନି ଗଣିତ ଖେଳ, ପଣିକିଆ, ଡିଜିଟାଲ୍ ବଜାର, ଓଡ଼ିଆ କ୍ୟାଲେଣ୍ଡର ଓ ହାରମୋନିୟମ୍ ଗୋଟିଏ ସ୍ଥାନରେ ଉପଲବ୍ଧ ।
          </p>

          <div className="flex flex-wrap justify-center gap-3 pt-2">
            <a
              href="mailto:message.sbg@gmail.com"
              className="px-4 py-2.5 rounded-2xl bg-[#6200EE]/10 font-bold text-xs text-[#6200EE] flex items-center gap-1.5"
            >
              <Mail className="w-4 h-4" /> ଯୋଗାଯୋଗ: message.sbg@gmail.com
            </a>
            <a
              href="https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa"
              target="_blank"
              rel="noopener noreferrer"
              className="px-4 py-2.5 rounded-2xl bg-[#2E7D32] text-white font-bold text-xs flex items-center gap-1.5"
            >
              <Smartphone className="w-4 h-4" /> ଗୁଗଲ୍ ପ୍ଲେ ଷ୍ଟୋର୍
            </a>
          </div>
        </div>

        <div className="bg-white rounded-3xl p-5 shadow-xs space-y-3">
          <h3 className="text-lg font-black text-slate-900">ବିଶେଷ କୃତଜ୍ଞତା</h3>
          <ul className="space-y-2 text-sm font-bold text-slate-700">
            <li>• ସନ୍ଦୀପ ବିଶ୍ୱାଳ ଜି — ନିର୍ମାତା ଓ ବିକାଶକାରୀ (Sandeep Biswal G)</li>
            <li>• ଓଡ଼ିଆ ବିଭବ ଓ ସୃଜନିକା — ପୁରୁଣା ଓଡ଼ିଆ ପୁସ୍ତକ ଓ ଅଭିଧାନ ସଂଗ୍ରହ</li>
            <li>• ବିଦ୍ୟାଳୟ ଓ ଗଣଶିକ୍ଷା ବିଭାଗ, ଓଡ଼ିଶା ସରକାର — ପାଠ୍ୟପୁସ୍ତକ ଉତ୍ସ</li>
          </ul>
        </div>
      </main>
    </div>
  );
};

// --- 7. DEEP LINKS MODAL & APP BANNER POPUP ---
export const DeepLinksModal: React.FC<{
  onClose: () => void;
  onNavigate: (screen: Screen) => void;
}> = ({ onClose, onNavigate }) => {
  return (
    <div
      onClick={onClose}
      className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4"
    >
      <div
        onClick={e => e.stopPropagation()}
        className="bg-white rounded-3xl max-w-2xl w-full max-h-[85vh] overflow-y-auto p-6 shadow-2xl space-y-4"
      >
        <div className="flex items-center justify-between border-b border-slate-100 pb-3">
          <h2 className="text-xl font-black text-[#6200EE]">ସମସ୍ତ ବିଭାଗ ଓ ଡିପ୍ ଲିଙ୍କ୍</h2>
          <button
            onClick={onClose}
            aria-label="ବନ୍ଦ କରନ୍ତୁ"
            className="w-9 h-9 rounded-full bg-slate-100 flex items-center justify-center cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="space-y-4">
          {deepLinkCategories.map(cat => (
            <div key={cat.title} className="bg-[#F8F9FA] rounded-2xl p-4 space-y-2">
              <h3 className="text-base font-black text-slate-900">{cat.title}</h3>
              {cat.description && <p className="text-xs text-slate-500">{cat.description}</p>}
              <div className="flex flex-wrap gap-2">
                {cat.chips.map(chip => (
                  <a
                    key={chip.url}
                    href={chip.url}
                    onClick={e => {
                      e.preventDefault();
                      onNavigate(chip.screen);
                      onClose();
                    }}
                    className="px-3.5 py-1.5 rounded-full bg-white border border-slate-200 text-xs font-bold text-slate-800 hover:bg-[#6200EE] hover:text-white transition"
                  >
                    {chip.label}
                  </a>
                ))}
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export const AppBannerPopup: React.FC<{
  currentScreen: Screen;
  onDismiss: () => void;
}> = ({ currentScreen, onDismiss }) => {
  const deepLinkUrl = `https://odiabhasa.pages.dev/open?${currentScreen}`;
  return (
    <div className="fixed bottom-16 left-0 right-0 z-30 px-4 pointer-events-none">
      <div className="max-w-md mx-auto bg-white/95 backdrop-blur-md border border-slate-200 rounded-2xl p-3 shadow-lg flex items-center justify-between gap-3 pointer-events-auto">
        <div className="flex items-center gap-2.5 min-w-0">
          <img
            src="/assets/odiabhasa/ic_launcher-playstore.png"
            alt="ଓଡ଼ିଆ ଭାଷା"
            className="w-10 h-10 rounded-xl shrink-0"
          />
          <div className="min-w-0">
            <div className="text-xs font-black text-slate-900 truncate">ଓଡ଼ିଆ ଭାଷା ଆପ୍</div>
            <div className="text-[11px] font-semibold text-slate-500 truncate">
              ଆଣ୍ଡ୍ରଏଡ୍ ଆପ୍‌ରେ ଖୋଲନ୍ତୁ
            </div>
          </div>
        </div>

        <div className="flex items-center gap-1.5 shrink-0">
          <a
            href={deepLinkUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="px-3 py-1.5 rounded-xl bg-[#6200EE] text-white font-bold text-xs"
          >
            ଆପ୍ ଖୋଲନ୍ତୁ
          </a>
          <button
            onClick={onDismiss}
            aria-label="ବ୍ୟାନର୍ ବନ୍ଦ କରନ୍ତୁ"
            className="w-7 h-7 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 cursor-pointer"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};
