import React, { useState, useRef, useEffect, useCallback } from 'react';
import {
  ArrowLeft,
  ArrowRight,
  Volume2,
  RotateCcw,
  Trash2,
  Eye,
  EyeOff,
  BarChart2,
  Edit3,
  Undo2,
  Redo2,
  Sliders,
  ZoomIn
} from 'lucide-react';
import {
  swarabarnaList,
  byanjanabarnaList,
  yuktakshyaraList,
  deprecatedList,
  characterAudioMap
} from '../data/odiaData';
import { playOdiaAudio, toOdiaNum } from '../utils/odiaUtils';

interface WritingPracticeScreenProps {
  onBack: () => void;
}

type CategoryKey = 'Swara' | 'Byanjana' | 'Yukta';

export interface StrokePoint {
  x: number;
  y: number;
}

export interface StrokeData {
  color: string;
  strokeWidth: number;
  points: StrokePoint[];
}

export interface CharStats {
  undoCount: number;
  redoCount: number;
  clearCount: number;
}

export interface PracticeStats {
  totalUndo: number;
  totalRedo: number;
  totalClears: number;
  perCharStats: Record<string, CharStats>;
}

const EXCLUDED_PRACTICE_CHARS = ['ଡ଼', 'ଢ଼'];

const DISTINCT_COLORS = [
  '#673AB7',
  '#E91E63',
  '#F44336',
  '#009688',
  '#3F51B5',
  '#FF9800',
  '#4CAF50',
  '#9C27B0',
  '#2196F3',
  '#795548'
];

function getPracticeListForCategory(category: CategoryKey): string[] {
  const raw =
    category === 'Swara'
      ? swarabarnaList
      : category === 'Byanjana'
      ? byanjanabarnaList
      : yuktakshyaraList;
  return raw.filter(ch => !deprecatedList.includes(ch) && !EXCLUDED_PRACTICE_CHARS.includes(ch));
}

function getCharColor(ch: string): string {
  if (!ch) return '#000000';
  let hash = 0;
  for (let i = 0; i < ch.length; i++) {
    hash = (hash * 31 + ch.charCodeAt(i)) | 0;
  }
  return DISTINCT_COLORS[Math.abs(hash) % DISTINCT_COLORS.length];
}

const STORAGE_KEY_DATA = 'OdiaWritingPrefs_v2';
const STORAGE_KEY_UI = 'odia_practice_ui_settings_v2';

interface PersistedPracticeData {
  lastCategory: CategoryKey;
  lastIndex: number;
  lastIndexByCategory?: Record<CategoryKey, number>;
  drawings: Record<string, StrokeData[]>;
  stats: PracticeStats;
}

function loadPersistedPracticeData(): PersistedPracticeData {
  const fallback: PersistedPracticeData = {
    lastCategory: 'Swara',
    lastIndex: 0,
    lastIndexByCategory: { Swara: 0, Byanjana: 0, Yukta: 0 },
    drawings: {},
    stats: {
      totalUndo: 0,
      totalRedo: 0,
      totalClears: 0,
      perCharStats: {}
    }
  };
  try {
    const raw = localStorage.getItem(STORAGE_KEY_DATA);
    if (!raw) return fallback;
    const parsed = JSON.parse(raw);
    const validCat: CategoryKey =
      parsed.lastCategory === 'Swara' ||
      parsed.lastCategory === 'Byanjana' ||
      parsed.lastCategory === 'Yukta'
        ? parsed.lastCategory
        : 'Swara';
    return {
      lastCategory: validCat,
      lastIndex: typeof parsed.lastIndex === 'number' ? parsed.lastIndex : 0,
      lastIndexByCategory: parsed.lastIndexByCategory || {
        Swara: validCat === 'Swara' ? parsed.lastIndex || 0 : 0,
        Byanjana: validCat === 'Byanjana' ? parsed.lastIndex || 0 : 0,
        Yukta: validCat === 'Yukta' ? parsed.lastIndex || 0 : 0
      },
      drawings: parsed.drawings && typeof parsed.drawings === 'object' ? parsed.drawings : {},
      stats: parsed.stats || fallback.stats
    };
  } catch {
    return fallback;
  }
}

function savePersistedPracticeData(data: PersistedPracticeData) {
  try {
    localStorage.setItem(STORAGE_KEY_DATA, JSON.stringify(data));
  } catch {
    // ignore storage errors
  }
}

export const WritingPracticeScreen: React.FC<WritingPracticeScreenProps> = ({ onBack }) => {
  const initialData = useMemoLazy(loadPersistedPracticeData);

  const [selectedCategory, setSelectedCategory] = useState<CategoryKey>(initialData.lastCategory);
  const [indexByCategory, setIndexByCategory] = useState<Record<CategoryKey, number>>(
    initialData.lastIndexByCategory || { Swara: initialData.lastIndex, Byanjana: 0, Yukta: 0 }
  );

  const practiceList = getPracticeListForCategory(selectedCategory);
  const rawIdx = indexByCategory[selectedCategory] || 0;
  const currentIndex =
    practiceList.length > 0 ? Math.min(Math.max(0, rawIdx), practiceList.length - 1) : 0;
  const currentChar = practiceList[currentIndex] || practiceList[0] || '';

  const setCurrentIndex = (updater: number | ((prev: number) => number)) => {
    setIndexByCategory(prev => {
      const curr = prev[selectedCategory] || 0;
      const next = typeof updater === 'function' ? updater(curr) : updater;
      return { ...prev, [selectedCategory]: next };
    });
  };

  // Saved drawings per character (persisted after write!)
  const [drawings, setDrawings] = useState<Record<string, StrokeData[]>>(initialData.drawings);
  const [undonePaths, setUndonePaths] = useState<StrokeData[]>([]);
  const [stats, setStats] = useState<PracticeStats>(initialData.stats);

  // UI Settings (Preview Text Size & Background Letter Visibility)
  const [showBackgroundLetter, setShowBackgroundLetter] = useState<boolean>(() => {
    try {
      const s = JSON.parse(localStorage.getItem(STORAGE_KEY_UI) || '{}');
      return typeof s.showBackground === 'boolean' ? s.showBackground : true;
    } catch {
      return true;
    }
  });
  const [previewTextSize, setPreviewTextSize] = useState<number>(() => {
    try {
      const s = JSON.parse(localStorage.getItem(STORAGE_KEY_UI) || '{}');
      return typeof s.previewTextSize === 'number' ? s.previewTextSize : 48;
    } catch {
      return 48;
    }
  });
  const [strokeWidth, setStrokeWidth] = useState<number>(15);

  // Dialog & Sheet states matching WritingPracticeScreen.kt
  const [showStatsPage, setShowStatsPage] = useState(false);
  const [showResetDialog, setShowResetDialog] = useState(false);
  const [openStrokeBottomSheet, setOpenStrokeBottomSheet] = useState(false);
  const [showResizeDialog, setShowResizeDialog] = useState(false);

  // Canvas & active stroke refs
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const isDrawingRef = useRef(false);
  const activePointsRef = useRef<StrokePoint[]>([]);

  // Save data synchronously whenever category, index, drawings, or stats change
  useEffect(() => {
    savePersistedPracticeData({
      lastCategory: selectedCategory,
      lastIndex: currentIndex,
      lastIndexByCategory: indexByCategory,
      drawings,
      stats
    });
  }, [selectedCategory, currentIndex, indexByCategory, drawings, stats]);

  // Save UI settings
  useEffect(() => {
    try {
      localStorage.setItem(
        STORAGE_KEY_UI,
        JSON.stringify({
          showBackground: showBackgroundLetter,
          previewTextSize
        })
      );
    } catch {
      // ignore
    }
  }, [showBackgroundLetter, previewTextSize]);

  const drawColor = getCharColor(currentChar);

  // Render background letter & saved strokes + active stroke onto canvas
  const renderCanvas = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const w = canvas.width;
    const h = canvas.height;
    ctx.clearRect(0, 0, w, h);

    // 1. Draw Background Letter (Auto-fitted outline trace in #E0E0E0)
    if (showBackgroundLetter && currentChar) {
      ctx.save();
      const fontSize = currentChar.length > 2 ? Math.min(w, h) * 0.45 : Math.min(w, h) * 0.62;
      ctx.font = `900 ${fontSize}px "Noto Sans Oriya", sans-serif`;
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.strokeStyle = '#E0E0E0';
      ctx.lineWidth = 8;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';
      ctx.fillStyle = '#F5F5F5';
      ctx.fillText(currentChar, w / 2, h / 2 + fontSize * 0.04);
      ctx.strokeText(currentChar, w / 2, h / 2 + fontSize * 0.04);
      ctx.restore();
    }

    // Helper to draw a stroke
    const drawSingleStroke = (stroke: StrokeData) => {
      if (!stroke.points || stroke.points.length === 0) return;
      ctx.save();
      ctx.strokeStyle = stroke.color;
      ctx.fillStyle = stroke.color;
      ctx.lineWidth = stroke.strokeWidth;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';

      if (stroke.points.length === 1) {
        ctx.beginPath();
        ctx.arc(stroke.points[0].x, stroke.points[0].y, stroke.strokeWidth / 2, 0, Math.PI * 2);
        ctx.fill();
      } else {
        ctx.beginPath();
        ctx.moveTo(stroke.points[0].x, stroke.points[0].y);
        for (let i = 1; i < stroke.points.length; i++) {
          ctx.lineTo(stroke.points[i].x, stroke.points[i].y);
        }
        ctx.stroke();
      }
      ctx.restore();
    };

    // 2. Draw Saved Strokes for currentChar
    const savedStrokes = drawings[currentChar] || [];
    for (const s of savedStrokes) {
      drawSingleStroke(s);
    }

    // 3. Draw Current Active Stroke
    if (isDrawingRef.current && activePointsRef.current.length > 0) {
      drawSingleStroke({
        color: drawColor,
        strokeWidth,
        points: activePointsRef.current
      });
    }
  }, [currentChar, drawings, showBackgroundLetter, drawColor, strokeWidth]);

  useEffect(() => {
    renderCanvas();
  }, [renderCanvas]);

  const getCanvasPoint = (e: React.PointerEvent<HTMLCanvasElement>): StrokePoint | null => {
    const canvas = canvasRef.current;
    if (!canvas) return null;
    const rect = canvas.getBoundingClientRect();
    const scaleX = canvas.width / rect.width;
    const scaleY = canvas.height / rect.height;
    return {
      x: Math.round((e.clientX - rect.left) * scaleX * 10) / 10,
      y: Math.round((e.clientY - rect.top) * scaleY * 10) / 10
    };
  };

  const handlePointerDown = (e: React.PointerEvent<HTMLCanvasElement>) => {
    e.currentTarget.setPointerCapture(e.pointerId);
    const pt = getCanvasPoint(e);
    if (!pt) return;
    isDrawingRef.current = true;
    activePointsRef.current = [pt];
    renderCanvas();
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!isDrawingRef.current) return;
    const pt = getCanvasPoint(e);
    if (!pt) return;
    activePointsRef.current.push(pt);
    renderCanvas();
  };

  const finishStroke = () => {
    if (!isDrawingRef.current) return;
    isDrawingRef.current = false;
    const pts = activePointsRef.current;
    activePointsRef.current = [];
    if (pts.length > 0 && currentChar) {
      const newStroke: StrokeData = {
        color: drawColor,
        strokeWidth,
        points: pts
      };
      setDrawings(prev => ({
        ...prev,
        [currentChar]: [...(prev[currentChar] || []), newStroke]
      }));
      setUndonePaths([]);
    } else {
      renderCanvas();
    }
  };

  // Actions matching WritingPracticeScreen.kt
  const onSelectCategory = (category: CategoryKey) => {
    setSelectedCategory(category);
    setUndonePaths([]);
  };

  const onPreviousChar = () => {
    if (currentIndex > 0) {
      setCurrentIndex(i => i - 1);
      setUndonePaths([]);
    }
  };

  const onNextChar = () => {
    if (currentIndex < practiceList.length - 1) {
      setCurrentIndex(i => i + 1);
      setUndonePaths([]);
    }
  };

  const onUndo = () => {
    const currentPaths = drawings[currentChar] || [];
    if (currentPaths.length === 0) return;
    const lastStroke = currentPaths[currentPaths.length - 1];
    const nextPaths = currentPaths.slice(0, -1);
    setDrawings(prev => ({ ...prev, [currentChar]: nextPaths }));
    setUndonePaths(prev => [...prev, lastStroke]);
    setStats(prev => {
      const charStat = prev.perCharStats[currentChar] || { undoCount: 0, redoCount: 0, clearCount: 0 };
      return {
        ...prev,
        totalUndo: prev.totalUndo + 1,
        perCharStats: {
          ...prev.perCharStats,
          [currentChar]: { ...charStat, undoCount: charStat.undoCount + 1 }
        }
      };
    });
  };

  const onRedo = () => {
    if (undonePaths.length === 0) return;
    const restored = undonePaths[undonePaths.length - 1];
    setUndonePaths(prev => prev.slice(0, -1));
    setDrawings(prev => ({
      ...prev,
      [currentChar]: [...(prev[currentChar] || []), restored]
    }));
    setStats(prev => {
      const charStat = prev.perCharStats[currentChar] || { undoCount: 0, redoCount: 0, clearCount: 0 };
      return {
        ...prev,
        totalRedo: prev.totalRedo + 1,
        perCharStats: {
          ...prev.perCharStats,
          [currentChar]: { ...charStat, redoCount: charStat.redoCount + 1 }
        }
      };
    });
  };

  const onClearLetter = () => {
    const currentPaths = drawings[currentChar] || [];
    if (currentPaths.length === 0) return;
    setDrawings(prev => ({ ...prev, [currentChar]: [] }));
    setUndonePaths([]);
    setStats(prev => {
      const charStat = prev.perCharStats[currentChar] || { undoCount: 0, redoCount: 0, clearCount: 0 };
      return {
        ...prev,
        totalClears: prev.totalClears + 1,
        perCharStats: {
          ...prev.perCharStats,
          [currentChar]: { ...charStat, clearCount: charStat.clearCount + 1 }
        }
      };
    });
  };

  const handleClearAllData = () => {
    localStorage.removeItem(STORAGE_KEY_DATA);
    setSelectedCategory('Swara');
    setIndexByCategory({ Swara: 0, Byanjana: 0, Yukta: 0 });
    setDrawings({});
    setUndonePaths([]);
    setStats({
      totalUndo: 0,
      totalRedo: 0,
      totalClears: 0,
      perCharStats: {}
    });
    setShowResetDialog(false);
  };

  const hasAudio = Boolean(characterAudioMap[currentChar]);
  const isPrevEnabled = currentIndex > 0;
  const isNextEnabled = practiceList.length > 0 && currentIndex < practiceList.length - 1;
  const canUndo = (drawings[currentChar] || []).length > 0;
  const canRedo = undonePaths.length > 0;

  return (
    <div className="min-h-screen bg-[#FFF3E0] flex flex-col">
      {/* CenterAlignedTopAppBar matching WritingPracticeScreen.kt */}
      <header className="sticky top-0 z-20 bg-[#FFF3E0] px-4 py-2.5">
        <div className="max-w-xl mx-auto flex items-center justify-between">
          <button
            onClick={() => {
              if (showStatsPage) setShowStatsPage(false);
              else onBack();
            }}
            title="ପଛକୁ ଯାଆନ୍ତୁ"
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-6 h-6 text-slate-900" />
          </button>

          <h1 className="text-[22px] font-normal text-slate-900">
            {showStatsPage ? 'ପରିସଂଖ୍ୟାନ' : 'ଲେଖି ଶିଖନ୍ତୁ'}
          </h1>

          <div className="flex items-center gap-1">
            {!showStatsPage && (
              <button
                onClick={() => setOpenStrokeBottomSheet(true)}
                title="ଗାର ର ଆକାର ବଦଳାନ୍ତୁ"
                className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
              >
                <Sliders className="w-5 h-5 text-slate-800" />
              </button>
            )}
            <button
              onClick={() => setShowStatsPage(s => !s)}
              title={showStatsPage ? 'ଲେଖା ଅଭ୍ୟାସକୁ ଫେରନ୍ତୁ' : 'ପରିସଂଖ୍ୟାନ ଦେଖନ୍ତୁ'}
              className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
            >
              {showStatsPage ? (
                <Edit3 className="w-5 h-5 text-slate-800" />
              ) : (
                <BarChart2 className="w-5 h-5 text-slate-800" />
              )}
            </button>
            <button
              onClick={() => setShowResetDialog(true)}
              title="ସମସ୍ତ ତଥ୍ୟ ଲିଭାନ୍ତୁ"
              className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
            >
              <Trash2 className="w-5 h-5 text-red-600/80" />
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 max-w-xl w-full mx-auto px-4 py-1 flex flex-col items-center">
        {showStatsPage ? (
          <StatsContentView stats={stats} />
        ) : (
          <>
            {/* CATEGORY BUTTONS (Only ସ୍ୱରବର୍ଣ୍ଣ, ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ, ଯୁକ୍ତାକ୍ଷର — ସଂଖ୍ୟା removed!) */}
            <div className="w-full flex items-center gap-2.5 pb-2 overflow-x-auto no-scrollbar">
              {[
                { key: 'Swara' as CategoryKey, label: 'ସ୍ୱରବର୍ଣ୍ଣ' },
                { key: 'Byanjana' as CategoryKey, label: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ' },
                { key: 'Yukta' as CategoryKey, label: 'ଯୁକ୍ତାକ୍ଷର' }
              ].map(cat => {
                const isSelected = selectedCategory === cat.key;
                return (
                  <button
                    key={cat.key}
                    onClick={() => onSelectCategory(cat.key)}
                    className={`px-4 py-2 rounded-full text-[14px] font-normal whitespace-nowrap transition cursor-pointer border ${
                      isSelected
                        ? 'bg-[#673AB7] text-white border-transparent'
                        : 'bg-white text-[#673AB7] border-[#673AB7]'
                    }`}
                  >
                    {cat.label}
                  </button>
                );
              })}
            </div>

            {/* PREVIEW HEADER CARD (RESIZABLE ON CLICK) */}
            <div
              onClick={() => setShowResizeDialog(true)}
              title="ଚାପି ଧରି ଆକାର ବଦଳାନ୍ତୁ"
              className="my-2 bg-white rounded-2xl shadow-sm px-6 py-1 cursor-pointer select-none hover:shadow-md transition flex items-center justify-center"
            >
              <span
                style={{ fontSize: `${previewTextSize}px` }}
                className="text-black leading-tight font-normal"
              >
                {currentChar}
              </span>
            </div>

            {/* MAIN WHITE SLATE AREA */}
            <div className="w-full max-w-[380px] aspect-square rounded-[20px] bg-white p-1 shadow-sm relative overflow-hidden">
              <canvas
                ref={canvasRef}
                width={500}
                height={500}
                onPointerDown={handlePointerDown}
                onPointerMove={handlePointerMove}
                onPointerUp={finishStroke}
                onPointerCancel={finishStroke}
                onPointerLeave={finishStroke}
                className="w-full h-full touch-none cursor-crosshair rounded-[16px]"
              />

              {/* Bottom-Left: Eye (Visibility Toggle) */}
              <div className="absolute bottom-3 left-3">
                <button
                  onClick={() => setShowBackgroundLetter(v => !v)}
                  title={showBackgroundLetter ? 'ପଛ ଅକ୍ଷର ଲୁଚାନ୍ତୁ' : 'ପଛ ଅକ୍ଷର ଦେଖାନ୍ତୁ'}
                  className="w-11 h-11 rounded-full bg-[#E8F5E9] border border-[#C8E6C9] flex items-center justify-center cursor-pointer shadow-xs"
                >
                  {showBackgroundLetter ? (
                    <Eye className="w-5 h-5 text-[#2E7D32]" />
                  ) : (
                    <EyeOff className="w-5 h-5 text-[#2E7D32]" />
                  )}
                </button>
              </div>

              {/* Bottom-Right: Speaker (Audio Pronunciation) */}
              {hasAudio && (
                <div className="absolute bottom-3 right-3">
                  <button
                    onClick={() => playOdiaAudio(currentChar, true)}
                    title="ଉଚ୍ଚାରଣ ଶୁଣନ୍ତୁ"
                    className="w-11 h-11 rounded-full bg-[#E1F5FE] border border-[#B3E5FC] flex items-center justify-center cursor-pointer shadow-xs"
                  >
                    <Volume2 className="w-5 h-5 text-[#0277BD]" />
                  </button>
                </div>
              )}
            </div>

            {/* NAVIGATION & UNDO / CLEAR / REDO CONTROLS */}
            <div className="w-full max-w-[420px] flex items-center justify-between mt-4">
              <button
                onClick={onPreviousChar}
                disabled={!isPrevEnabled}
                title="ପୂର୍ବ ଅକ୍ଷର"
                className={`w-14 h-14 rounded-2xl shadow-md flex items-center justify-center transition cursor-pointer ${
                  isPrevEnabled
                    ? 'bg-[#FFCC80] text-black hover:brightness-95'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
              >
                <ArrowLeft className="w-6 h-6" />
              </button>

              <div className="flex items-center gap-2">
                <button
                  onClick={onUndo}
                  disabled={!canUndo}
                  title="ଗୋଟିଏ ପଦକ୍ଷେପ ପଛକୁ"
                  className="w-11 h-11 rounded-full flex items-center justify-center disabled:opacity-35 cursor-pointer hover:bg-black/5"
                >
                  <Undo2 className="w-7 h-7 text-slate-900" />
                </button>

                <button
                  onClick={onClearLetter}
                  title="ସମ୍ପୂର୍ଣ୍ଣ ଲିଭାନ୍ତୁ"
                  className="px-5 h-14 rounded-2xl bg-[#FFAB91] text-black shadow-md flex items-center gap-2 font-medium text-base cursor-pointer hover:brightness-95"
                >
                  <RotateCcw className="w-5 h-5" />
                  <span>ଲିଭାନ୍ତୁ</span>
                </button>

                <button
                  onClick={onRedo}
                  disabled={!canRedo}
                  title="ପୁଣି ଥରେ ଫେରାଇ ଆଣନ୍ତୁ"
                  className="w-11 h-11 rounded-full flex items-center justify-center disabled:opacity-35 cursor-pointer hover:bg-black/5"
                >
                  <Redo2 className="w-7 h-7 text-slate-900" />
                </button>
              </div>

              <button
                onClick={onNextChar}
                disabled={!isNextEnabled}
                title="ପରବର୍ତ୍ତୀ ଅକ୍ଷର"
                className={`w-14 h-14 rounded-2xl shadow-md flex items-center justify-center transition cursor-pointer ${
                  isNextEnabled
                    ? 'bg-[#A5D6A7] text-black hover:brightness-95'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
              >
                <ArrowRight className="w-6 h-6" />
              </button>
            </div>

            <p className="mt-2.5 text-[16px] text-gray-500">ଅକ୍ଷର ଉପରେ ଲେଖନ୍ତୁ</p>
          </>
        )}
      </main>

      {/* 1. Reset Dialog matching WritingPracticeScreen.kt */}
      {showResetDialog && (
        <div
          onClick={() => setShowResetDialog(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[24px] max-w-sm w-full p-6 shadow-xl space-y-4"
          >
            <h3 className="text-xl font-bold text-slate-900">ସତର୍କ ସୂଚନା</h3>
            <p className="text-base text-slate-700">
              ଆପଣ ସମସ୍ତ ଲେଖା ଏବଂ ପରିସଂଖ୍ୟାନ ଡିଲିଟ୍ କରିବାକୁ ଚାହୁଁଛନ୍ତି କି?
            </p>
            <div className="flex justify-end gap-4 pt-2">
              <button
                onClick={() => setShowResetDialog(false)}
                className="px-4 py-2 font-medium text-black cursor-pointer"
              >
                ନା
              </button>
              <button
                onClick={handleClearAllData}
                className="px-4 py-2 font-bold text-red-600 cursor-pointer"
              >
                ହଁ
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 2. Resize Dialog for Preview Alphabet */}
      {showResizeDialog && (
        <div
          onClick={() => setShowResizeDialog(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[24px] max-w-sm w-full p-6 shadow-xl text-center space-y-4"
          >
            <ZoomIn className="w-7 h-7 mx-auto text-[#673AB7]" />
            <h3 className="text-xl font-bold text-slate-900">ଅକ୍ଷର ଆକାର</h3>
            <p className="text-sm text-slate-600">ଉପର ଅକ୍ଷରର ଆକାର ବଦଳାନ୍ତୁ</p>
            <input
              type="range"
              min={30}
              max={100}
              step={5}
              value={previewTextSize}
              onChange={e => setPreviewTextSize(Number(e.target.value))}
              className="w-full accent-[#673AB7]"
            />
            <div className="text-lg font-bold text-slate-800">
              {toOdiaNum(Math.round(previewTextSize))}
            </div>
            <div className="flex justify-end">
              <button
                onClick={() => setShowResizeDialog(false)}
                className="px-4 py-2 text-[#673AB7] font-bold cursor-pointer"
              >
                ଠିକ୍ ଅଛି
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 3. Stroke Width Bottom Sheet */}
      {openStrokeBottomSheet && (
        <div
          onClick={() => setOpenStrokeBottomSheet(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-lg rounded-t-[24px] p-6 pb-10 flex flex-col items-center space-y-4 shadow-2xl"
          >
            <div className="w-10 h-1 bg-slate-300 rounded-full mb-1" />
            <h3 className="text-[20px] font-bold text-slate-900">ଗାର ର ଆକାର ବାଛନ୍ତୁ</h3>
            <input
              type="range"
              min={10}
              max={40}
              step={4}
              value={strokeWidth}
              onChange={e => setStrokeWidth(Number(e.target.value))}
              className="w-4/5 accent-[#673AB7]"
            />
            <div className="text-[18px] font-medium text-slate-800">
              {toOdiaNum(Math.round(strokeWidth))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- STATS CONTENT VIEW (Exact match with StatsContent in WritingPracticeScreen.kt) ---
const StatsContentView: React.FC<{ stats: PracticeStats }> = ({ stats }) => {
  if (stats.totalUndo === 0 && stats.totalRedo === 0 && stats.totalClears === 0) {
    return (
      <div className="flex-1 w-full flex items-center justify-center py-24">
        <p className="text-[18px] text-gray-500">କୌଣସି ପରିସଂଖ୍ୟାନ ଉପଲବ୍ଧ ନାହିଁ ।</p>
      </div>
    );
  }

  const sortedCharStats = Object.entries(stats.perCharStats)
    .filter(([, cs]) => cs.undoCount > 0 || cs.redoCount > 0 || cs.clearCount > 0)
    .sort(([a], [b]) => a.localeCompare(b));

  return (
    <div className="w-full space-y-4 py-2">
      <div className="bg-white rounded-2xl p-4 shadow-sm space-y-3">
        <h2 className="text-[20px] font-bold text-[#673AB7]">ସାମଗ୍ରିକ ପରିସଂଖ୍ୟାନ</h2>
        <div className="divide-y divide-slate-200">
          <div className="py-2 flex items-center justify-between text-[16px]">
            <span>ମୋଟ ପୂର୍ବ ସ୍ଥିତିକୁ:</span>
            <span className="font-bold">{toOdiaNum(stats.totalUndo)}</span>
          </div>
          <div className="py-2 flex items-center justify-between text-[16px]">
            <span>ମୋଟ ପୁଣି ଥରେ:</span>
            <span className="font-bold">{toOdiaNum(stats.totalRedo)}</span>
          </div>
          <div className="py-2 flex items-center justify-between text-[16px]">
            <span>ମୋଟ ସଫା:</span>
            <span className="font-bold">{toOdiaNum(stats.totalClears)}</span>
          </div>
        </div>
      </div>

      {sortedCharStats.length > 0 && (
        <div className="space-y-3 pt-2">
          <h3 className="text-[18px] font-bold text-slate-900">ବର୍ଣ୍ଣ ଅନୁଯାୟୀ ପରିସଂଖ୍ୟାନ</h3>
          {sortedCharStats.map(([char, cs]) => (
            <div
              key={char}
              className="bg-[#F1E8FF] border border-[#D0BCFF] rounded-2xl px-4 py-3 flex items-center justify-between"
            >
              <span className="text-[28px] font-bold text-slate-900">{char}</span>
              <div className="text-right text-[14px] space-y-0.5 text-slate-800">
                <div>ପୂର୍ବ ସ୍ଥିତିକୁ: {toOdiaNum(cs.undoCount)}</div>
                <div>ପୁଣି ଥରେ: {toOdiaNum(cs.redoCount)}</div>
                <div>ସଫା: {toOdiaNum(cs.clearCount)}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

function useMemoLazy<T>(factory: () => T): T {
  const ref = useRef<{ initialized: boolean; value?: T }>({ initialized: false });
  if (!ref.current.initialized) {
    ref.current.value = factory();
    ref.current.initialized = true;
  }
  return ref.current.value as T;
}
