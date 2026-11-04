import React, { useState, useEffect, useMemo, useRef } from 'react';
import {
  ArrowLeft, ZoomIn, ZoomOut, Grid, ChevronLeft, ChevronRight,
  Download, BookOpen, Search, Volume2,
  Pin, Info, CheckCircle2, Sparkles, RefreshCw, Eye, EyeOff,
  Share2, Copy, X, Play, Pause, Sliders, ListFilter, ArrowUpDown,
  Smartphone, ExternalLink, MoreVertical, AlignLeft, AlignCenter, AlignRight, AlignJustify
} from 'lucide-react';
import {
  Screen,
  schoolBooksByClass,
  SchoolBook
} from '../data/odiaData';
import { toOdiaNum, fromOdiaNum, playOdiaAudio } from '../utils/odiaUtils';

// --- 1. CHHABILA MADHU BARNABODHA SCREEN (44 pages) ---
export const MadhuBarnabodhaScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const totalPages = 44;
  const [currentPage, setCurrentPage] = useState(1);
  const [zoom, setZoom] = useState(1);
  const [showGrid, setShowGrid] = useState(false);

  const pageUrl = (p: number) => `https://imsbg.github.io/odiabhasa/bahi/barnabodha/${p}.jpg`;

  return (
    <div className="min-h-screen bg-[#1E1E2C] text-white flex flex-col">
      <header className="sticky top-0 z-20 bg-[#2A2A3C] border-b border-white/10 px-4 py-3">
        <div className="max-w-4xl mx-auto flex items-center justify-between gap-2">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-full bg-white/10 border border-white/20 flex items-center justify-center hover:bg-white/20 cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div>
              <h1 className="text-lg sm:text-xl font-black text-[#FFD54F]">ଛବିଳ ମଧୁ ବର୍ଣ୍ଣବୋଧ</h1>
              <p className="text-xs font-bold text-slate-300">
                ପୃଷ୍ଠା {toOdiaNum(currentPage)} / {toOdiaNum(totalPages)}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setZoom(z => Math.max(1, +(z - 0.25).toFixed(2)))}
              className="p-2 rounded-xl bg-white/10 hover:bg-white/20 cursor-pointer"
              title="ଛୋଟ କରନ୍ତୁ"
            >
              <ZoomOut className="w-4 h-4" />
            </button>
            <span className="text-xs font-bold px-2">{toOdiaNum(Math.round(zoom * 100))}%</span>
            <button
              onClick={() => setZoom(z => Math.min(2.5, +(z + 0.25).toFixed(2)))}
              className="p-2 rounded-xl bg-white/10 hover:bg-white/20 cursor-pointer"
              title="ବଡ଼ କରନ୍ତୁ"
            >
              <ZoomIn className="w-4 h-4" />
            </button>
            <button
              onClick={() => setShowGrid(g => !g)}
              className={`p-2 rounded-xl border cursor-pointer ${
                showGrid ? 'bg-[#FFD54F] text-black border-black' : 'bg-white/10 border-white/20'
              }`}
              title="ସମସ୍ତ ପୃଷ୍ଠା"
            >
              <Grid className="w-4 h-4" />
            </button>
          </div>
        </div>
      </header>

      <main className="flex-1 max-w-4xl w-full mx-auto p-4 flex flex-col items-center justify-center">
        {showGrid ? (
          <div className="w-full grid grid-cols-3 sm:grid-cols-5 gap-3">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map(p => (
              <button
                key={p}
                onClick={() => {
                  setCurrentPage(p);
                  setShowGrid(false);
                }}
                className={`rounded-2xl overflow-hidden border-2 transition cursor-pointer bg-white p-1 ${
                  currentPage === p ? 'border-[#FFD54F] ring-2 ring-[#FFD54F]' : 'border-black'
                }`}
              >
                <img
                  src={pageUrl(p)}
                  alt={`ପୃଷ୍ଠା ${toOdiaNum(p)}`}
                  loading="lazy"
                  className="w-full aspect-[3/4] object-cover rounded-xl"
                />
                <span className="block text-center text-xs font-black text-black mt-1">
                  ପୃଷ୍ଠା {toOdiaNum(p)}
                </span>
              </button>
            ))}
          </div>
        ) : (
          <div className="w-full flex flex-col items-center gap-4">
            <div className="w-full overflow-auto rounded-2xl bg-white border-2 border-black shadow-lg flex justify-center p-2 max-h-[72vh]">
              <img
                src={pageUrl(currentPage)}
                alt={`ମଧୁ ବର୍ଣ୍ଣବୋଧ ପୃଷ୍ଠା ${toOdiaNum(currentPage)}`}
                style={{ transform: `scale(${zoom})`, transformOrigin: 'top center' }}
                className="max-w-full h-auto object-contain transition-transform duration-200"
              />
            </div>

            <div className="w-full bg-[#2A2A3C] border border-white/15 rounded-2xl p-3 flex flex-col sm:flex-row items-center gap-3">
              <div className="flex items-center gap-2">
                <button
                  disabled={currentPage <= 1}
                  onClick={() => {
                    setZoom(1);
                    setCurrentPage(p => Math.max(1, p - 1));
                  }}
                  className="px-4 py-2 rounded-xl bg-white text-black font-black text-xs disabled:opacity-40 flex items-center gap-1 cursor-pointer"
                >
                  <ChevronLeft className="w-4 h-4" /> ପୂର୍ବ ପୃଷ୍ଠା
                </button>
                <button
                  disabled={currentPage >= totalPages}
                  onClick={() => {
                    setZoom(1);
                    setCurrentPage(p => Math.min(totalPages, p + 1));
                  }}
                  className="px-4 py-2 rounded-xl bg-[#FFD54F] text-black font-black text-xs disabled:opacity-40 flex items-center gap-1 cursor-pointer"
                >
                  ପରବର୍ତ୍ତୀ ପୃଷ୍ଠା <ChevronRight className="w-4 h-4" />
                </button>
              </div>
              <input
                type="range"
                min={1}
                max={totalPages}
                value={currentPage}
                onChange={e => setCurrentPage(Number(e.target.value))}
                className="flex-1 w-full accent-[#FFD54F]"
              />
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

// --- 2. SCHOOL BOOKS MAIN SCREEN & CLASS BOOKS SCREEN (Exact match with SchoolBooks.kt & ClassOne.kt) ---
const CLASS_LIST: { screen: Screen; num: number; title: string }[] = [
  { screen: Screen.CLASS_1, num: 1, title: 'ପ୍ରଥମ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_2, num: 2, title: 'ଦ୍ୱିତୀୟ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_3, num: 3, title: 'ତୃତୀୟ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_4, num: 4, title: 'ଚତୁର୍ଥ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_5, num: 5, title: 'ପଞ୍ଚମ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_6, num: 6, title: 'ଷଷ୍ଠ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_7, num: 7, title: 'ସପ୍ତମ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_8, num: 8, title: 'ଅଷ୍ଟମ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_9, num: 9, title: 'ନବମ ଶ୍ରେଣୀ' },
  { screen: Screen.CLASS_10, num: 10, title: 'ଦଶମ ଶ୍ରେଣୀ' },
];

const COMIC_COLORS = [
  '#00E5FF', // Bright Cyan
  '#FFE135', // Comic Yellow
  '#FF66CC', // Pop Pink
  '#B2FF59', // Lime Green
  '#FF9D00', // Vibrant Orange
];

export const SchoolBooksScreen: React.FC<{
  onBack: () => void;
  onSelectClass: (screen: Screen) => void;
}> = ({ onBack, onSelectClass }) => {
  const [showInfoDialog, setShowInfoDialog] = useState(false);

  return (
    <div className="min-h-screen bg-[#FAF8F5] pb-20">
      <header className="sticky top-0 z-20 bg-[#FAF8F5] px-5 pt-4 pb-3">
        <div className="max-w-xl mx-auto flex items-center justify-between">
          <button
            onClick={onBack}
            className="w-[42px] h-[42px] rounded-full bg-white border-2 border-black shadow-sm flex items-center justify-center cursor-pointer bouncing-click"
          >
            <ArrowLeft className="w-5 h-5 text-black" />
          </button>

          <h1 className="text-[28px] font-black text-black">ବିଦ୍ୟାଳୟ ବହି</h1>

          <button
            onClick={() => setShowInfoDialog(true)}
            className="w-[42px] h-[42px] rounded-full bg-[#FFD54F] border-2 border-black shadow-sm flex items-center justify-center cursor-pointer bouncing-click"
          >
            <Info className="w-5 h-5 text-black" />
          </button>
        </div>
      </header>

      <main className="max-w-xl mx-auto px-5 pt-2 space-y-[18px]">
        {CLASS_LIST.map((cls, idx) => {
          const cardColor = COMIC_COLORS[idx % COMIC_COLORS.length];
          return (
            <div
              key={cls.screen}
              onClick={() => onSelectClass(cls.screen)}
              className="relative h-[88px] cursor-pointer bouncing-click"
            >
              <div className="absolute inset-0 translate-x-[6px] translate-y-[6px] bg-black rounded-[18px] border-2 border-black" />
              <div
                style={{ backgroundColor: cardColor }}
                className="relative h-full w-full rounded-[18px] border-[3px] border-black px-5 flex items-center justify-between"
              >
                <div className="flex items-center gap-4">
                  <div className="w-[52px] h-[52px] rounded-full bg-white border-[2.5px] border-black -rotate-10 flex items-center justify-center shrink-0">
                    <BookOpen className="w-7 h-7 text-black" />
                  </div>
                  <span className="text-[24px] font-extrabold text-black tracking-wide">
                    {cls.title}
                  </span>
                </div>

                <div className="w-9 h-9 rounded-full bg-white border-2 border-black flex items-center justify-center shrink-0">
                  <ChevronRight className="w-5 h-5 text-black" />
                </div>
              </div>
            </div>
          );
        })}
      </main>

      {showInfoDialog && (
        <div
          onClick={() => setShowInfoDialog(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[20px] max-w-sm w-full p-6 shadow-xl space-y-4"
          >
            <h3 className="text-[22px] font-bold text-[#6200EE]">ସୂଚନା</h3>
            <div className="space-y-2 text-[16px] text-slate-800 leading-[22px]">
              <p>
                ଏହି ବିଭାଗରେ ଥିବା ସମସ୍ତ ପାଠ୍ୟପୁସ୍ତକ &apos;ଓଡ଼ିଶା ବିଦ୍ୟାଳୟ ଶିକ୍ଷା କାର୍ଯ୍ୟକ୍ରମ ପ୍ରାଧିକରଣ&apos; (OSEPA) ର ୱେବସାଇଟରୁ ସଂଗୃହୀତ।
              </p>
              <p className="text-[14px] text-slate-500">
                ଛାତ୍ରଛାତ୍ରୀମାନଙ୍କ ସୁବିଧା ପାଇଁ ଏହାକୁ ଆପ୍ ମାଧ୍ୟମରେ ଉପଲବ୍ଧ କରାଯାଇଛି।
              </p>
            </div>
            <div className="flex justify-end pt-2">
              <button
                onClick={() => setShowInfoDialog(false)}
                className="px-5 py-2.5 rounded-xl bg-[#6200EE] text-white font-bold text-sm cursor-pointer"
              >
                ବୁଝିଗଲି
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export const ClassBooksScreen: React.FC<{
  classNum: number;
  onBack: () => void;
}> = ({ classNum, onBack }) => {
  const screenKey = `CLASS_${classNum}` as Screen;
  const books = schoolBooksByClass[screenKey] || (schoolBooksByClass as any)[classNum] || [];
  const classMeta = CLASS_LIST.find(c => c.num === classNum);
  const [sheetBook, setSheetBook] = useState<SchoolBook | null>(null);

  return (
    <div className="min-h-screen bg-[#FAF8F5] pb-20">
      <header className="sticky top-0 z-20 bg-[#FAF8F5] px-4 py-3">
        <div className="max-w-xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            aria-label="ପଛକୁ ଯାଆନ୍ତୁ"
            className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-6 h-6 text-slate-900" />
          </button>
          <h1 className="text-[24px] font-bold text-slate-900">
            {classMeta?.title || `${toOdiaNum(classNum)}ମ ଶ୍ରେଣୀ`}
          </h1>
        </div>
      </header>

      <main className="max-w-xl mx-auto px-4 pt-2 space-y-3">
        {books.map((book, idx) => {
          const isNew = book.title.includes('(ନୂତନ)');
          const isRevised = book.title.includes('(ସଂଶୋଧିତ)');

          let containerStyle = 'bg-white border border-slate-200 shadow-xs';
          let textColor = 'text-slate-900 font-medium';
          let iconNode = <BookOpen className="w-6 h-6 text-[#6200EE] shrink-0" />;

          if (isNew) {
            containerStyle = 'bg-[#6200EE]/12 border border-[#6200EE]';
            textColor = 'text-[#6200EE] font-bold';
            iconNode = <Sparkles className="w-6 h-6 text-[#6200EE] shrink-0" />;
          } else if (isRevised) {
            containerStyle = 'bg-[#4CAF50]/15 border border-[#4CAF50]';
            textColor = 'text-[#2E7D32] font-bold';
            iconNode = <CheckCircle2 className="w-6 h-6 text-[#2E7D32] shrink-0" />;
          }

          return (
            <div
              key={idx}
              onClick={() => setSheetBook(book)}
              className={`min-h-[70px] rounded-[15px] px-4 py-3 flex items-center justify-between gap-3 cursor-pointer bouncing-click ${containerStyle}`}
            >
              <div className="flex items-center gap-4">
                {iconNode}
                <span className={`text-[18px] leading-snug ${textColor}`}>{book.title}</span>
              </div>
              <ChevronRight className="w-5 h-5 text-slate-500 shrink-0" />
            </div>
          );
        })}
      </main>

      {sheetBook && (
        <div
          onClick={() => setSheetBook(null)}
          className="fixed inset-0 z-50 bg-black/85 backdrop-blur-sm flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white rounded-[24px] p-7 text-center w-full max-w-[400px] shadow-[0_10px_30px_rgba(0,0,0,0.5)]"
          >
            <h2 className="text-[22px] font-bold text-[#2C241B] mb-2 leading-snug">
              📱 ଆପ୍ ବ୍ୟବହାର କରନ୍ତୁ
            </h2>
            <div className="inline-block px-3.5 py-1 rounded-full bg-[#6200EE]/10 text-[#6200EE] font-bold text-sm mb-2">
              {classMeta?.title} • {sheetBook.title}
            </div>
            <p className="text-[#7A7063] text-[15px] leading-relaxed mb-6">
              ଏହି ବିଦ୍ୟାଳୟ ପାଠ୍ୟପୁସ୍ତକଟି ପଢ଼ିବା ପାଇଁ ଆମର &apos;ଓଡ଼ିଆ ଭାଷା&apos; ଆପ୍ ଖୋଲନ୍ତୁ କିମ୍ବା ଗୁଗଲ୍ ପ୍ଲେ-ଷ୍ଟୋରରୁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ।
            </p>

            <div className="space-y-3">
              <a
                href={`https://odiabhasa.pages.dev/open?CLASS_${classNum}`}
                target="_blank"
                rel="noopener noreferrer"
                className="block w-full py-4 rounded-full bg-[#6D5240] text-white text-[16px] font-bold active:scale-96 transition"
              >
                ଆପ୍ ଖୋଲନ୍ତୁ
              </a>
              <a
                href="https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa&hl=or"
                target="_blank"
                rel="noopener noreferrer"
                className="block w-full py-4 rounded-full bg-[#E65100] text-white text-[16px] font-bold active:scale-96 transition"
              >
                ଆପ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ
              </a>
              <button
                onClick={() => setSheetBook(null)}
                className="w-full py-3 rounded-full bg-slate-100 text-[#2C241B] text-[15px] font-bold cursor-pointer hover:bg-slate-200 transition"
              >
                ବନ୍ଦ କରନ୍ତୁ
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- 3. PATHAGARA (BAHI) & BOOK READER SCREEN (Exact match with Bahi.kt & BookReaderScreen.kt) ---
export interface TextSpan {
  text: string;
  style: string;
}

export interface ContentBlock {
  type: string;
  text?: string;
  spans?: TextSpan[];
}

export interface BookPage {
  pageNumber: number;
  content: ContentBlock[];
  audio?: string | null;
  lyrics?: string | null;
}

export interface BookDetails {
  title: string;
  author: string;
  source: string;
  coverpage: string;
  pages: BookPage[];
}

export interface BookMetadata {
  destination: string;
  version: number;
  new?: string | null;
}

export interface BookUiModel {
  id: string;
  details: BookDetails;
  metadata: BookMetadata;
  isPinned: boolean;
  isHidden: boolean;
}

interface LrcWord {
  text: string;
  startTime: number;
}

interface LrcLine {
  startTime: number;
  content: string;
  words: LrcWord[];
}

function parseTimeTag(minutes: string, seconds: string, millisStr: string): number {
  const millisPart = millisStr.length === 2 ? Number(millisStr) * 10 : Number(millisStr);
  return Number(minutes) * 60 * 1000 + Number(seconds) * 1000 + millisPart;
}

async function fetchAndParseLrc(urlString: string): Promise<LrcLine[]> {
  try {
    const res = await fetch(urlString);
    const content = await res.text();
    const lines: LrcLine[] = [];
    const lineRegex = /\[(\d{2}):(\d{2})\.(\d{1,3})\](.*)/;
    const wordRegex = /<(\d{2}):(\d{2})\.(\d{1,3})>([^<]*)/g;

    content.split(/\r?\n/).forEach(line => {
      const m = lineRegex.exec(line);
      if (m) {
        const [, min, sec, mil, rawText] = m;
        const lineStartTime = parseTimeTag(min, sec, mil);
        const trimmedText = rawText.trim();
        const words: LrcWord[] = [];
        const firstTagIndex = trimmedText.indexOf('<');

        if (firstTagIndex === -1) {
          if (trimmedText) words.push({ text: trimmedText, startTime: lineStartTime });
        } else {
          const firstWordText = trimmedText.substring(0, firstTagIndex).trim();
          if (firstWordText) words.push({ text: firstWordText, startTime: lineStartTime });
          let wm: RegExpExecArray | null;
          wordRegex.lastIndex = 0;
          while ((wm = wordRegex.exec(trimmedText)) !== null) {
            const [, wmMin, wmSec, wmMil, wordText] = wm;
            const wordTime = parseTimeTag(wmMin, wmSec, wmMil);
            if (wordText.trim()) words.push({ text: wordText.trim(), startTime: wordTime });
          }
        }

        if (words.length > 0) {
          lines.push({
            startTime: lineStartTime,
            content: words.map(w => w.text).join(' '),
            words
          });
        } else if (trimmedText) {
          lines.push({ startTime: lineStartTime, content: trimmedText, words: [] });
        }
      }
    });

    return lines.sort((a, b) => a.startTime - b.startTime);
  } catch {
    return [];
  }
}

function formatMillisToOdia(ms: number): string {
  if (!ms || ms <= 0) return '୦୦:୦୦';
  const totalSec = Math.floor(ms / 1000);
  const m = String(Math.floor(totalSec / 60)).padStart(2, '0');
  const s = String(totalSec % 60).padStart(2, '0');
  return toOdiaNum(`${m}:${s}`);
}

export const BahiScreen: React.FC<{
  initialBahiId?: string;
  onBack: () => void;
}> = ({ initialBahiId, onBack }) => {
  const [allBooks, setAllBooks] = useState<BookUiModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [showHidden, setShowHidden] = useState(false);

  const [pinnedUrls, setPinnedUrls] = useState<string[]>(() => {
    try {
      return JSON.parse(localStorage.getItem('bahi_pinned_urls') || '[]');
    } catch {
      return [];
    }
  });
  const [hiddenUrls, setHiddenUrls] = useState<string[]>(() => {
    try {
      return JSON.parse(localStorage.getItem('bahi_hidden_urls') || '[]');
    } catch {
      return [];
    }
  });

  const [selectedBook, setSelectedBook] = useState<BookUiModel | null>(null);
  const [bookForOptions, setBookForOptions] = useState<BookUiModel | null>(null);
  const [toastMsg, setToastMsg] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMsg(msg);
    setTimeout(() => setToastMsg(null), 2200);
  };

  const loadBooks = async (forceRefresh = false) => {
    setLoading(true);
    setErrorMsg(null);
    try {
      const cacheKey = 'bahi_resolved_books_v2';
      if (!forceRefresh) {
        const cached = localStorage.getItem(cacheKey);
        if (cached) {
          const parsed = JSON.parse(cached);
          if (Array.isArray(parsed) && parsed.length > 0) {
            setAllBooks(parsed);
            setLoading(false);
          }
        }
      }

      const masterRes = await fetch(`https://sbgb.pages.dev/ବହି/ସବୁ.json?t=${Date.now()}`);
      if (!masterRes.ok) throw new Error('Network error');
      const masterList: BookMetadata[] = await masterRes.json();

      const resolved = await Promise.allSettled(
        masterList.map(async (meta, index) => {
          const r = await fetch(meta.destination);
          if (!r.ok) throw new Error('Failed book fetch');
          const details: BookDetails = await r.json();
          const identifier = meta.destination.split('/').pop()?.replace('.json', '') || `book_${index}`;
          return {
            id: identifier,
            details,
            metadata: meta,
            isPinned: false,
            isHidden: false,
          } as BookUiModel;
        })
      );

      const validBooks = resolved
        .filter((r): r is PromiseFulfilledResult<BookUiModel> => r.status === 'fulfilled' && Boolean(r.value?.details?.title))
        .map(r => r.value);

      if (validBooks.length > 0) {
        setAllBooks(validBooks);
        try {
          localStorage.setItem(cacheKey, JSON.stringify(validBooks));
        } catch {
          // Ignore storage quota errors
        }
      } else if (allBooks.length === 0) {
        setErrorMsg('ବହି ତାଲିକା ପାଇବା ପାଇଁ ଦୟାକରି ଇଣ୍ଟରନେଟ୍ ସଂଯୋଗ କରନ୍ତୁ');
      }
    } catch {
      if (allBooks.length === 0) {
        setErrorMsg('ବହି ତାଲିକା ପାଇବା ପାଇଁ ଦୟାକରି ଇଣ୍ଟରନେଟ୍ ସଂଯୋଗ କରନ୍ତୁ');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBooks(false);
  }, []);

  useEffect(() => {
    if (initialBahiId && allBooks.length > 0) {
      const byId = allBooks.find(
        b => b.id.toLowerCase() === initialBahiId.toLowerCase()
      );
      if (byId) {
        setSelectedBook(byId);
      } else {
        const asIdx = parseInt(initialBahiId, 10);
        if (!isNaN(asIdx) && allBooks[asIdx]) {
          setSelectedBook(allBooks[asIdx]);
        }
      }
    }
  }, [initialBahiId, allBooks]);

  const decoratedBooks = useMemo(() => {
    return allBooks.map(b => ({
      ...b,
      isPinned: pinnedUrls.includes(b.metadata.destination),
      isHidden: hiddenUrls.includes(b.metadata.destination),
    }));
  }, [allBooks, pinnedUrls, hiddenUrls]);

  const hiddenBooksList = useMemo(
    () => decoratedBooks.filter(b => b.isHidden),
    [decoratedBooks]
  );

  const visibleBooksList = useMemo(
    () =>
      decoratedBooks
        .filter(b => !b.isHidden)
        .sort((a, b) => (b.isPinned ? 1 : 0) - (a.isPinned ? 1 : 0)),
    [decoratedBooks]
  );

  const booksToShow = useMemo(() => {
    const source = showHidden ? hiddenBooksList : visibleBooksList;
    if (!searchQuery.trim()) return source;
    const q = searchQuery.toLowerCase();
    return source.filter(
      b =>
        b.details.title.toLowerCase().includes(q) ||
        b.details.author.toLowerCase().includes(q)
    );
  }, [showHidden, hiddenBooksList, visibleBooksList, searchQuery]);

  const togglePin = (dest: string) => {
    setPinnedUrls(prev => {
      const next = prev.includes(dest) ? prev.filter(x => x !== dest) : [...prev, dest];
      localStorage.setItem('bahi_pinned_urls', JSON.stringify(next));
      return next;
    });
  };

  const toggleHide = (dest: string) => {
    setHiddenUrls(prev => {
      const next = prev.includes(dest) ? prev.filter(x => x !== dest) : [...prev, dest];
      localStorage.setItem('bahi_hidden_urls', JSON.stringify(next));
      return next;
    });
    if (pinnedUrls.includes(dest)) {
      togglePin(dest);
    }
  };

  if (selectedBook) {
    return (
      <BookReaderView
        bookDetails={selectedBook.details}
        onBack={() => setSelectedBook(null)}
      />
    );
  }

  return (
    <div className="min-h-screen bg-[#FFFDE7] pb-20">
      {/* TopAppBar matching Bahi.kt */}
      <header className="sticky top-0 z-20 bg-[#FFEB3B] border-b-2 border-black px-4 py-3">
        <div className="max-w-5xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={() => {
                if (showHidden) {
                  setShowHidden(false);
                } else {
                  onBack();
                }
              }}
              className="w-10 h-10 rounded-full hover:bg-black/10 flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-7 h-7 text-black" />
            </button>
            <h1 className="text-[24px] font-black text-black">
              {showHidden ? 'ଲୁଚିଥିବା ବହି' : 'ଓଡ଼ିଆ ପାଠାଗାର'}
            </h1>
          </div>

          <div className="flex items-center gap-2">
            {hiddenBooksList.length > 0 && (
              <button
                onClick={() => setShowHidden(h => !h)}
                title="ଲୁଚିଥିବା ବହି ଦେଖାନ୍ତୁ"
                className="w-10 h-10 rounded-full hover:bg-black/10 flex items-center justify-center cursor-pointer"
              >
                {showHidden ? (
                  <Eye className="w-6 h-6 text-black" />
                ) : (
                  <EyeOff className="w-6 h-6 text-black" />
                )}
              </button>
            )}
            <button
              onClick={() => loadBooks(true)}
              title="Refresh Books"
              className="w-10 h-10 rounded-full hover:bg-black/10 flex items-center justify-center cursor-pointer"
            >
              <RefreshCw className={`w-6 h-6 text-black ${loading ? 'animate-spin' : ''}`} />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-5xl mx-auto p-4 space-y-5">
        {/* Comic Search Bar matching Bahi.kt */}
        {!errorMsg && (
          <div className="relative">
            <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-full" />
            <div className="relative flex items-center bg-white border-2 border-black rounded-full px-4 py-3">
              <Search className="w-5 h-5 text-black mr-3 shrink-0" />
              <input
                type="text"
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                placeholder="ବହି ଖୋଜନ୍ତୁ..."
                className="w-full bg-transparent outline-none font-bold text-black placeholder:text-gray-500 text-base"
              />
              {searchQuery && (
                <button
                  onClick={() => setSearchQuery('')}
                  className="p-1 hover:bg-black/5 rounded-full cursor-pointer"
                >
                  <X className="w-5 h-5 text-black" />
                </button>
              )}
            </div>
          </div>
        )}

        {/* Books Grid / Shimmer / Error */}
        {loading && allBooks.length === 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-5">
            {Array.from({ length: 8 }).map((_, idx) => (
              <div key={idx} className="relative h-[320px] p-1">
                <div className="absolute inset-1 translate-x-1.5 translate-y-1.5 bg-black/25 rounded-[16px]" />
                <div className="relative h-full bg-white border-[3px] border-gray-400 rounded-[16px] overflow-hidden flex flex-col animate-pulse">
                  <div className="w-full aspect-[3/4] bg-amber-100" />
                  <div className="p-3 space-y-2 flex-1">
                    <div className="h-4 bg-gray-200 rounded w-4/5" />
                    <div className="h-3 bg-gray-200 rounded w-1/2" />
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : errorMsg ? (
          <div className="py-16 text-center space-y-4">
            <p className="text-lg font-bold text-red-600 whitespace-pre-line">{errorMsg}</p>
            <button
              onClick={() => loadBooks(true)}
              className="px-5 py-2.5 rounded-xl bg-[#FFEB3B] border-2 border-black font-black text-black cursor-pointer"
            >
              ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ
            </button>
          </div>
        ) : booksToShow.length === 0 ? (
          <div className="py-20 text-center">
            <p className="text-xl font-bold text-black">
              {showHidden ? 'ଆପଣ କୌଣସି ବହି ଲୁଚାଇ ନାହାଁନ୍ତି' : 'କୌଣସି ବହି ମିଳିଲା ନାହିଁ'}
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-5">
            {booksToShow.map(book => (
              <div
                key={book.metadata.destination}
                onClick={() => setSelectedBook(book)}
                onContextMenu={e => {
                  e.preventDefault();
                  setBookForOptions(book);
                }}
                className="relative h-[320px] p-1 cursor-pointer group"
              >
                {/* 1. Hard Shadow (Static at 6,6) */}
                <div className="absolute inset-1 translate-x-[6px] translate-y-[6px] bg-black rounded-[16px]" />

                {/* 2. Main Card Content */}
                <div className="relative h-full bg-white border-[3px] border-black rounded-[16px] overflow-hidden flex flex-col active:translate-x-[6px] active:translate-y-[6px] transition-transform">
                  {/* Cover Image Area (3:4) */}
                  <div className="relative w-full aspect-[3/4] border-b-[3px] border-black bg-[#FFFDE7] overflow-hidden">
                    {book.details.coverpage ? (
                      <img
                        src={book.details.coverpage}
                        alt={book.details.title}
                        loading="lazy"
                        className="w-full h-full object-cover"
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center">
                        <BookOpen className="w-12 h-12 text-black/30" />
                      </div>
                    )}

                    {/* "New" Badge */}
                    {book.metadata.new === 'yes' && (
                      <div className="absolute top-1.5 right-1.5 bg-[#FF4081] border-2 border-black rounded-[8px] px-2 py-0.5">
                        <span className="text-[12px] font-bold text-black">ନୂତନ </span>
                      </div>
                    )}

                    {/* Pinned Badge */}
                    {book.isPinned && (
                      <div className="absolute top-1.5 left-1.5 bg-[#FFEB3B] border-2 border-black rounded-full p-1">
                        <Pin className="w-4 h-4 text-black" />
                      </div>
                    )}

                    {/* Options Button (for easy web access alongside right-click/long-press) */}
                    <button
                      onClick={e => {
                        e.stopPropagation();
                        setBookForOptions(book);
                      }}
                      title="ବିକଳ୍ପ"
                      className="absolute bottom-1.5 right-1.5 w-8 h-8 rounded-full bg-white/90 border-2 border-black flex items-center justify-center hover:bg-[#FFEB3B] cursor-pointer"
                    >
                      <MoreVertical className="w-4 h-4 text-black" />
                    </button>
                  </div>

                  {/* Text Area */}
                  <div className="flex-1 bg-white p-3 flex flex-col justify-between">
                    <h2 className="text-[14px] font-black text-black leading-[20px] line-clamp-2">
                      {book.details.title}
                    </h2>
                    <p className="text-[12px] font-bold text-[#FF4081] truncate">
                      {book.details.author}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Eye-Catchy Bottom Sheet Menu matching BookOptionsSheetContent in Bahi.kt */}
      {bookForOptions && (
        <div
          onClick={() => setBookForOptions(null)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-lg rounded-t-[24px] overflow-hidden shadow-2xl animate-in slide-in-from-bottom"
          >
            <div className="w-full h-3 bg-[#FFEB3B]" />
            <div className="p-5 space-y-3">
              {/* Header with Big Cover */}
              <div className="flex items-center gap-4 pb-3">
                <div className="relative w-[80px] h-[100px] shrink-0">
                  <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-[8px]" />
                  <img
                    src={bookForOptions.details.coverpage}
                    alt={bookForOptions.details.title}
                    className="relative w-full h-full object-cover rounded-[8px] border-2 border-black bg-white"
                  />
                </div>
                <div className="flex-1 min-w-0">
                  <h3 className="text-xl font-black text-black leading-snug">
                    {bookForOptions.details.title}
                  </h3>
                  <p className="text-sm font-bold text-gray-500 mt-1">
                    {bookForOptions.details.author}
                  </p>
                </div>
              </div>

              {/* Share (Cyan) */}
              <button
                onClick={() => {
                  const link = `https://odiabhasa.pages.dev/open?Bahi=${bookForOptions.id}`;
                  const text = `ଓଡ଼ିଆ ଭାଷା ଆପ୍ ରେ *${bookForOptions.details.title}* ବହିଟି ଦେଖନ୍ତୁ \n ${bookForOptions.details.author}'ଙ୍କ ଦ୍ବାରା\n${link}`;
                  if (navigator.share) {
                    navigator.share({ title: bookForOptions.details.title, text, url: link }).catch(() => {});
                  } else {
                    navigator.clipboard?.writeText(text);
                    showToast('ସେୟାର ଲିଙ୍କ୍ କପି ହେଲା');
                  }
                  setBookForOptions(null);
                }}
                className="relative w-full h-[70px] block cursor-pointer text-left"
              >
                <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-[12px]" />
                <div className="relative h-full bg-[#00E5FF] border-2 border-black rounded-[12px] px-4 flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center">
                      <Share2 className="w-5 h-5 text-black" />
                    </div>
                    <div>
                      <div className="text-[16px] font-black text-black">ସେୟାର କରନ୍ତୁ</div>
                      <div className="text-[12px] font-bold text-black/70">(କେବଳ ପିତାମାତା)</div>
                    </div>
                  </div>
                  <ChevronRight className="w-5 h-5 text-black" />
                </div>
              </button>

              {/* Copy Link (Yellow) */}
              <button
                onClick={() => {
                  const link = `https://odiabhasa.pages.dev/open?Bahi=${bookForOptions.id}`;
                  navigator.clipboard?.writeText(link);
                  showToast('ଲିଙ୍କ୍ କପି ହେଲା');
                  setBookForOptions(null);
                }}
                className="relative w-full h-[70px] block cursor-pointer text-left"
              >
                <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-[12px]" />
                <div className="relative h-full bg-[#FFEB3B] border-2 border-black rounded-[12px] px-4 flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center">
                      <Copy className="w-5 h-5 text-black" />
                    </div>
                    <div>
                      <div className="text-[16px] font-black text-black">ଲିଙ୍କ୍ କପି କରନ୍ତୁ</div>
                      <div className="text-[12px] font-bold text-black/70">(କେବଳ ପିତାମାତା)</div>
                    </div>
                  </div>
                  <ChevronRight className="w-5 h-5 text-black" />
                </div>
              </button>

              {/* Pin (Pink) */}
              {!showHidden && (
                <button
                  onClick={() => {
                    togglePin(bookForOptions.metadata.destination);
                    setBookForOptions(null);
                  }}
                  className="relative w-full h-[70px] block cursor-pointer text-left"
                >
                  <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-[12px]" />
                  <div className="relative h-full bg-[#FF4081] border-2 border-black rounded-[12px] px-4 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                      <div className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center">
                        <Pin className="w-5 h-5 text-black" />
                      </div>
                      <div className="text-[16px] font-black text-black">
                        {bookForOptions.isPinned ? 'ପିନ୍ ହଟାନ୍ତୁ' : 'ପିନ୍ କରନ୍ତୁ'}
                      </div>
                    </div>
                    <ChevronRight className="w-5 h-5 text-black" />
                  </div>
                </button>
              )}

              {/* Hide (Green) */}
              <button
                onClick={() => {
                  toggleHide(bookForOptions.metadata.destination);
                  setBookForOptions(null);
                }}
                className="relative w-full h-[70px] block cursor-pointer text-left"
              >
                <div className="absolute inset-0 translate-x-1 translate-y-1 bg-black rounded-[12px]" />
                <div className="relative h-full bg-[#C6FF00] border-2 border-black rounded-[12px] px-4 flex items-center justify-between">
                  <div className="flex items-center gap-4">
                    <div className="w-10 h-10 rounded-full bg-white border-2 border-black flex items-center justify-center">
                      {showHidden ? <Eye className="w-5 h-5 text-black" /> : <EyeOff className="w-5 h-5 text-black" />}
                    </div>
                    <div className="text-[16px] font-black text-black">
                      {showHidden ? 'ପୁଣି ଦେଖାନ୍ତୁ' : 'ଲୁଚାନ୍ତୁ'}
                    </div>
                  </div>
                  <ChevronRight className="w-5 h-5 text-black" />
                </div>
              </button>
            </div>
          </div>
        </div>
      )}

      {toastMsg && (
        <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 bg-black text-white px-4 py-2 rounded-full font-bold text-sm shadow-lg">
          {toastMsg}
        </div>
      )}
    </div>
  );
};

// --- BOOK READER VIEW (Exact match with BookReaderScreen.kt) ---
const BookReaderView: React.FC<{
  bookDetails: BookDetails;
  onBack: () => void;
}> = ({ bookDetails, onBack }) => {
  const totalPages = bookDetails.pages?.length || 1;
  const [pageIdx, setPageIdx] = useState(0);
  const [theme, setTheme] = useState<'LIGHT' | 'DARK' | 'SEPIA'>('LIGHT');
  const [fontSize, setFontSize] = useState<number>(19);
  const [textAlign, setTextAlign] = useState<'left' | 'center' | 'right' | 'justify'>('left');
  const [showSettings, setShowSettings] = useState(false);

  // Search inside book
  const [isSearchActive, setIsSearchActive] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  // Audio & LRC state
  const audioRef = useRef<HTMLAudioElement | null>(null);
  const [isAudioPlaying, setIsAudioPlaying] = useState(false);
  const [isPlayerVisible, setIsPlayerVisible] = useState(false);
  const [playbackSpeed, setPlaybackSpeed] = useState(1.0);
  const [audioPosMs, setAudioPosMs] = useState(0);
  const [audioDurMs, setAudioDurMs] = useState(0);
  const [lrcLines, setLrcLines] = useState<LrcLine[]>([]);
  const [showLyrics, setShowLyrics] = useState(false);

  const currentPage = bookDetails.pages?.[pageIdx];
  const hasAudio = Boolean(currentPage?.audio);

  useEffect(() => {
    // Stop audio on page change
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    setIsAudioPlaying(false);
    setIsPlayerVisible(false);
    setAudioPosMs(0);
    setAudioDurMs(0);
    setLrcLines([]);
    setShowLyrics(false);
  }, [pageIdx]);

  const togglePlayPageAudio = async () => {
    if (!currentPage?.audio) return;
    if (!audioRef.current) {
      const audio = new Audio(currentPage.audio);
      audio.playbackRate = playbackSpeed;
      audio.addEventListener('timeupdate', () => {
        setAudioPosMs(audio.currentTime * 1000);
      });
      audio.addEventListener('loadedmetadata', () => {
        setAudioDurMs((audio.duration || 0) * 1000);
      });
      audio.addEventListener('ended', () => {
        setIsAudioPlaying(false);
      });
      audioRef.current = audio;
      if (currentPage.lyrics) {
        fetchAndParseLrc(currentPage.lyrics).then(setLrcLines);
      }
    }

    if (isAudioPlaying) {
      audioRef.current.pause();
      setIsAudioPlaying(false);
    } else {
      try {
        await audioRef.current.play();
        setIsAudioPlaying(true);
      } catch {
        setIsAudioPlaying(false);
      }
    }
  };

  const themeColors = {
    LIGHT: { bg: 'bg-white', text: 'text-black', primary: '#6200EE', card: 'bg-[#6200EE] text-white' },
    DARK: { bg: 'bg-[#121212]', text: 'text-white', primary: '#BB86FC', card: 'bg-[#BB86FC] text-black' },
    SEPIA: { bg: 'bg-[#FBF0D9]', text: 'text-[#5B4636]', primary: '#795548', card: 'bg-[#795548] text-white' },
  }[theme];

  const currentLrcIdx = useMemo(() => {
    if (lrcLines.length === 0) return -1;
    let idx = -1;
    for (let i = 0; i < lrcLines.length; i++) {
      if (lrcLines[i].startTime <= audioPosMs) idx = i;
    }
    return idx;
  }, [lrcLines, audioPosMs]);

  const pagePlainText = useMemo(() => {
    if (!currentPage?.content) return '';
    return currentPage.content
      .map(b => b.text || b.spans?.map(s => s.text).join('') || '')
      .filter(Boolean)
      .join(' । ');
  }, [currentPage]);

  return (
    <div className={`min-h-screen flex flex-col ${themeColors.bg} ${themeColors.text}`}>
      {/* Top Bar matching BookReaderScreen.kt */}
      <header className="sticky top-0 z-20 border-b border-current/10 px-4 py-3 backdrop-blur-md">
        <div className="max-w-3xl mx-auto flex items-center justify-between gap-2">
          {isSearchActive ? (
            <div className="flex items-center gap-2 w-full">
              <button
                onClick={() => {
                  setIsSearchActive(false);
                  setSearchQuery('');
                }}
                className="p-2 rounded-full hover:bg-current/10 cursor-pointer"
              >
                <ArrowLeft className="w-5 h-5" />
              </button>
              <input
                type="text"
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                placeholder="ବହି ଭିତରେ ଖୋଜନ୍ତୁ..."
                autoFocus
                className="flex-1 bg-transparent border-b border-current/30 py-1 outline-none font-bold"
              />
              {searchQuery && (
                <button onClick={() => setSearchQuery('')} className="p-1 cursor-pointer">
                  <X className="w-4 h-4" />
                </button>
              )}
            </div>
          ) : (
            <>
              <div className="flex items-center gap-3 min-w-0">
                <button
                  onClick={onBack}
                  className="w-10 h-10 rounded-full hover:bg-current/10 flex items-center justify-center shrink-0 cursor-pointer"
                >
                  <ArrowLeft className="w-5 h-5" />
                </button>
                <div className="min-w-0">
                  <h1 className="text-base sm:text-lg font-bold truncate">{bookDetails.title}</h1>
                  <p className="text-xs opacity-70 truncate">{bookDetails.author}</p>
                </div>
              </div>

              <div className="flex items-center gap-1 shrink-0">
                {hasAudio && (
                  <button
                    onClick={() => {
                      const nextVis = !isPlayerVisible;
                      setIsPlayerVisible(nextVis);
                      if (nextVis && !isAudioPlaying) {
                        togglePlayPageAudio();
                      }
                    }}
                    title="ଶୁଣନ୍ତୁ"
                    className="p-2.5 rounded-full hover:bg-current/10 cursor-pointer"
                  >
                    <Play className="w-5 h-5" />
                  </button>
                )}
                <button
                  onClick={() => playOdiaAudio(pagePlainText || bookDetails.title, true)}
                  title="ପୃଷ୍ଠା ପଢ଼ନ୍ତୁ"
                  className="p-2.5 rounded-full hover:bg-current/10 cursor-pointer"
                >
                  <Volume2 className="w-5 h-5" />
                </button>
                <button
                  onClick={() => setIsSearchActive(true)}
                  title="ଖୋଜନ୍ତୁ"
                  className="p-2.5 rounded-full hover:bg-current/10 cursor-pointer"
                >
                  <Search className="w-5 h-5" />
                </button>
                <button
                  onClick={() => setShowSettings(true)}
                  title="ସେଟିଂସ୍"
                  className="p-2.5 rounded-full hover:bg-current/10 cursor-pointer"
                >
                  <Sliders className="w-5 h-5" />
                </button>
              </div>
            </>
          )}
        </div>
      </header>

      {/* Audio & Karaoke Lyrics Player Card */}
      {isPlayerVisible && hasAudio && (
        <div className="max-w-3xl w-full mx-auto px-4 pt-3">
          <div className={`rounded-3xl p-5 shadow-xl ${themeColors.card} space-y-4`}>
            <div className="flex items-center gap-4">
              <button
                onClick={togglePlayPageAudio}
                className="w-14 h-14 rounded-full bg-white text-black flex items-center justify-center shrink-0 cursor-pointer shadow"
              >
                {isAudioPlaying ? <Pause className="w-7 h-7" /> : <Play className="w-7 h-7" />}
              </button>
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between">
                  <h3 className="font-bold truncate">{bookDetails.title}</h3>
                  <button onClick={() => setIsPlayerVisible(false)} className="cursor-pointer">
                    <X className="w-5 h-5 opacity-80" />
                  </button>
                </div>
                <p className="text-xs opacity-80">ଓଡ଼ିଆ ଭାଷା • ଅଡିଓ</p>
                <div className="flex items-center gap-2 mt-2">
                  <button
                    onClick={() => {
                      const nextSpeed = playbackSpeed >= 2 ? 0.75 : +(playbackSpeed + 0.25).toFixed(2);
                      setPlaybackSpeed(nextSpeed);
                      if (audioRef.current) audioRef.current.playbackRate = nextSpeed;
                    }}
                    className="px-3 py-1 rounded-full border border-current/30 text-xs font-bold cursor-pointer"
                  >
                    {toOdiaNum(playbackSpeed.toFixed(2))} ଗୁଣା
                  </button>
                  {lrcLines.length > 0 && (
                    <button
                      onClick={() => setShowLyrics(l => !l)}
                      className="px-3 py-1 rounded-full border border-current/30 text-xs font-bold cursor-pointer"
                    >
                      ପଦାବଳୀ
                    </button>
                  )}
                </div>
              </div>
            </div>

            <div>
              <input
                type="range"
                min={0}
                max={Math.max(audioDurMs, 1)}
                value={audioPosMs}
                onChange={e => {
                  const val = Number(e.target.value);
                  setAudioPosMs(val);
                  if (audioRef.current) audioRef.current.currentTime = val / 1000;
                }}
                className="w-full accent-white"
              />
              <div className="flex justify-between text-xs font-bold opacity-80">
                <span>{formatMillisToOdia(audioPosMs)}</span>
                <span>{formatMillisToOdia(audioDurMs)}</span>
              </div>
            </div>

            {showLyrics && lrcLines.length > 0 && (
              <div className="max-h-56 overflow-y-auto space-y-2 border-t border-current/20 pt-3">
                {lrcLines.map((line, i) => (
                  <div
                    key={i}
                    onClick={() => {
                      if (audioRef.current) audioRef.current.currentTime = line.startTime / 1000;
                    }}
                    className={`p-2 rounded-xl cursor-pointer transition ${
                      i === currentLrcIdx ? 'bg-white/20 font-black text-lg' : 'opacity-65 text-sm'
                    }`}
                  >
                    {line.content}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Main Page Content matching PageContent in BookReaderScreen.kt */}
      <main className="flex-1 max-w-3xl w-full mx-auto px-6 py-6 flex flex-col justify-between">
        {currentPage?.pageNumber === 1 ? (
          /* Page 1: Cover / Title Page */
          <div className="my-auto text-center space-y-4 py-12">
            {bookDetails.coverpage && (
              <img
                src={bookDetails.coverpage}
                alt={bookDetails.title}
                className="w-48 aspect-[3/4] object-cover mx-auto rounded-2xl border-2 border-black shadow-lg mb-6"
              />
            )}
            <h2 className="text-3xl sm:text-4xl font-black leading-tight">{bookDetails.title}</h2>
            <p className="text-xl font-bold opacity-75">{bookDetails.author}</p>
            {bookDetails.source && (
              <p className="text-sm opacity-60 pt-8">ଉତ୍ସ: {bookDetails.source}</p>
            )}
          </div>
        ) : (
          <div className="space-y-4">
            {currentPage?.content?.map((block, idx) => {
              const rawText =
                block.text || block.spans?.map(s => s.text).join('') || '';
              if (!rawText.trim()) return null;

              // Check if TOC line on page 2 ("title ... pageNum")
              const isTocPage = currentPage.pageNumber === 2;
              const tocParts = isTocPage ? rawText.split('...') : [];

              if (block.type === 'header') {
                return (
                  <h2
                    key={idx}
                    style={{ fontSize: `${fontSize + 6}px`, textAlign }}
                    className="font-black leading-snug pt-2 pb-1"
                  >
                    {rawText}
                  </h2>
                );
              }

              if (isTocPage && tocParts.length === 2) {
                const targetPageNum = parseInt(fromOdiaNum(tocParts[1].trim()), 10);
                return (
                  <div
                    key={idx}
                    onClick={() => {
                      if (!isNaN(targetPageNum) && targetPageNum >= 1 && targetPageNum <= totalPages) {
                        setPageIdx(targetPageNum - 1);
                      }
                    }}
                    className="flex items-center justify-between py-2.5 px-3 rounded-xl border border-current/15 hover:bg-current/5 cursor-pointer font-bold"
                    style={{ fontSize: `${fontSize}px` }}
                  >
                    <span>{tocParts[0].trim()}</span>
                    <span className="px-2.5 py-0.5 rounded-lg bg-current/10 text-sm font-black">
                      ପୃଷ୍ଠା {tocParts[1].trim()}
                    </span>
                  </div>
                );
              }

              return (
                <p
                  key={idx}
                  style={{
                    fontSize: `${fontSize}px`,
                    lineHeight: `${Math.round(fontSize * 1.65)}px`,
                    textAlign
                  }}
                  className="whitespace-pre-line font-medium"
                >
                  {block.spans && block.spans.length > 0
                    ? block.spans.map((sp, sIdx) => (
                        <span
                          key={sIdx}
                          className={
                            sp.style === 'bold'
                              ? 'font-black'
                              : sp.style === 'italic'
                              ? 'italic'
                              : ''
                          }
                        >
                          {sp.text}
                        </span>
                      ))
                    : rawText}
                </p>
              );
            })}
          </div>
        )}

        {/* Bottom Page Navigation Bar */}
        <div className="pt-8 mt-8 border-t border-current/15 flex items-center justify-between gap-4">
          <button
            disabled={pageIdx <= 0}
            onClick={() => {
              setPageIdx(p => Math.max(0, p - 1));
              window.scrollTo({ top: 0, behavior: 'smooth' });
            }}
            className="px-4 py-2.5 rounded-xl border border-current/30 font-bold text-sm disabled:opacity-35 flex items-center gap-1.5 cursor-pointer"
          >
            <ChevronLeft className="w-4 h-4" /> ପୂର୍ବ ପୃଷ୍ଠା
          </button>

          <span className="font-black text-sm">
            {toOdiaNum(pageIdx + 1)} / {toOdiaNum(totalPages)}
          </span>

          <button
            disabled={pageIdx >= totalPages - 1}
            onClick={() => {
              setPageIdx(p => Math.min(totalPages - 1, p + 1));
              window.scrollTo({ top: 0, behavior: 'smooth' });
            }}
            className="px-4 py-2.5 rounded-xl border border-current/30 font-bold text-sm disabled:opacity-35 flex items-center gap-1.5 cursor-pointer"
          >
            ପରବର୍ତ୍ତୀ ପୃଷ୍ଠା <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </main>

      {/* Settings Bottom Sheet matching SettingsPanel in BookReaderScreen.kt */}
      {showSettings && (
        <div
          onClick={() => setShowSettings(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className={`w-full max-w-lg rounded-t-3xl p-6 space-y-5 shadow-2xl ${themeColors.bg} ${themeColors.text}`}
          >
            <h3 className="text-xl font-black text-center">ନିର୍ଦ୍ଧାରଣ</h3>

            {/* Font Size */}
            <div className="space-y-2">
              <div className="text-sm font-bold">অକ୍ଷର ଆକାର ({toOdiaNum(fontSize)})</div>
              <div className="flex items-center gap-3">
                <button
                  onClick={() => setFontSize(s => Math.max(12, s - 1))}
                  className="w-10 h-10 rounded-full border border-current/30 font-black cursor-pointer"
                >
                  -
                </button>
                <input
                  type="range"
                  min={12}
                  max={32}
                  value={fontSize}
                  onChange={e => setFontSize(Number(e.target.value))}
                  className="flex-1"
                />
                <button
                  onClick={() => setFontSize(s => Math.min(32, s + 1))}
                  className="w-10 h-10 rounded-full border border-current/30 font-black cursor-pointer"
                >
                  +
                </button>
              </div>
            </div>

            {/* Text Alignment */}
            <div className="space-y-2">
              <div className="text-sm font-bold">ଲେଖା ସଜ୍ଜିକରଣ</div>
              <div className="grid grid-cols-4 gap-2">
                {[
                  { id: 'left', label: 'ବାମ', icon: AlignLeft },
                  { id: 'center', label: 'ମଧ୍ୟ', icon: AlignCenter },
                  { id: 'right', label: 'ଡାହାଣ', icon: AlignRight },
                  { id: 'justify', label: 'ଉଭୟ', icon: AlignJustify },
                ].map(a => {
                  const Icon = a.icon;
                  return (
                    <button
                      key={a.id}
                      onClick={() => setTextAlign(a.id as any)}
                      className={`py-2.5 rounded-xl border font-bold text-xs flex flex-col items-center gap-1 cursor-pointer ${
                        textAlign === a.id
                          ? 'bg-[#6200EE] text-white border-[#6200EE]'
                          : 'border-current/25'
                      }`}
                    >
                      <Icon className="w-4 h-4" />
                      {a.label}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Theme */}
            <div className="space-y-2">
              <div className="text-sm font-bold">ଥିମ୍</div>
              <div className="grid grid-cols-3 gap-2">
                {[
                  { id: 'LIGHT', label: 'ଆଲୋକିତ' },
                  { id: 'DARK', label: 'ଅନ୍ଧାରିଆ' },
                  { id: 'SEPIA', label: 'ଆଖି ସୁରକ୍ଷା' },
                ].map(t => (
                  <button
                    key={t.id}
                    onClick={() => setTheme(t.id as any)}
                    className={`py-2.5 rounded-xl border font-bold text-xs cursor-pointer ${
                      theme === t.id
                        ? 'bg-[#6200EE] text-white border-[#6200EE]'
                        : 'border-current/25'
                    }`}
                  >
                    {t.label}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// --- 4. PURUNA BAHI ARCHIVE SCREEN (All Books + Pagination + Sort + Filter + App-Redirect Popup) ---
export interface PurunaBahiData {
  id: string;
  title: string;
  author: string;
  year: string;
  category: string;
  source: string;
  publisher: string;
  url: string;
  version: number;
}

const FALLBACK_PURUNA_BAHI: PurunaBahiData[] = [
  {
    id: 'puruna_17454',
    title: 'ପୂର୍ଣ୍ଣଚନ୍ଦ୍ର ଓଡ଼ିଆ ଭାଷାକୋଷ - ଭାଗ ୧ ସମ୍ପୂର୍ଣ୍ଣ',
    author: 'ଗୋପାଳ ଚନ୍ଦ୍ର ପ୍ରହରାଜ',
    year: '୧୯୩୦',
    category: 'ଭାଷାକୋଷ, ଅଭିଧାନ',
    source: 'ଓଡ଼ିଆ ବିଭବ',
    publisher: 'ସୃଜନିକା',
    url: 'https://odiabhasa.pages.dev/open?PURUNA_BAHI',
    version: 1
  },
  {
    id: 'puruna_102',
    title: 'ମଧୁସୂଦନ ଗ୍ରନ୍ଥାବଳୀ',
    author: 'ଭକ୍ତକବି ମଧୁସୂଦନ ରାଓ',
    year: '୧୯୧୫',
    category: 'କବିତା, ପ୍ରବନ୍ଧ',
    source: 'ଓଡ଼ିଆ ବିଭବ',
    publisher: 'ସୃଜନିକା',
    url: 'https://odiabhasa.pages.dev/open?PURUNA_BAHI',
    version: 1
  },
  {
    id: 'puruna_103',
    title: 'ଫକୀରମୋହନ ଗ୍ରନ୍ଥାବଳୀ',
    author: 'ବ୍ୟାସକବି ଫକୀର ମୋହନ ସେନାପତି',
    year: '୧୯୦୨',
    category: 'ଉପନ୍ୟାସ, ଗଳ୍ପ',
    source: 'ଓଡ଼ିଆ ବିଭବ',
    publisher: 'ସୃଜନିକା',
    url: 'https://odiabhasa.pages.dev/open?PURUNA_BAHI',
    version: 1
  },
  {
    id: 'puruna_104',
    title: 'ରାଧାନାଥ ଗ୍ରନ୍ଥାବଳୀ',
    author: 'କବିବର ରାଧାନାଥ ରାୟ',
    year: '୧୮୯୬',
    category: 'କାବ୍ୟ',
    source: 'ଓଡ଼ିଆ ବିଭବ',
    publisher: 'ସୃଜନିକା',
    url: 'https://odiabhasa.pages.dev/open?PURUNA_BAHI',
    version: 1
  }
];

function extractYearAsInt(yearStr: string): number {
  const cleanStr = yearStr.split(',')[0]?.trim() || '';
  const engStr = fromOdiaNum(cleanStr).replace(/[^0-9]/g, '');
  const parsed = parseInt(engStr, 10);
  return isNaN(parsed) ? 9999 : parsed;
}

const SORT_OPTIONS = ['କୌଣସିଟି ନୁହେଁ', 'ଅକ୍ଷର ଅନୁଯାୟୀ', 'ପୁରୁଣା ରୁ ନୂଆ', 'ନୂଆ ରୁ ପୁରୁଣା'];
const BOOKS_PER_PAGE = 24;

export const PurunaBahiScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [allBooks, setAllBooks] = useState<PurunaBahiData[]>(FALLBACK_PURUNA_BAHI);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [sortOption, setSortOption] = useState('କୌଣସିଟି ନୁହେଁ');
  const [showSortSheet, setShowSortSheet] = useState(false);
  const [showFilterSheet, setShowFilterSheet] = useState(false);
  const [activeFilterTab, setActiveFilterTab] = useState<'ବିଭାଗ' | 'ଲେଖକ' | 'ପ୍ରକାଶକ' | 'ବର୍ଷ'>('ବିଭାଗ');
  const [sheetSearchQuery, setSheetSearchQuery] = useState('');

  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [selectedAuthors, setSelectedAuthors] = useState<string[]>([]);
  const [selectedPublishers, setSelectedPublishers] = useState<string[]>([]);
  const [selectedYears, setSelectedYears] = useState<string[]>([]);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);

  // Selected book for Odia App-Only Modal
  const [selectedBook, setSelectedBook] = useState<PurunaBahiData | null>(null);
  const [copiedField, setCopiedField] = useState<string | null>(null);

  const fetchBooks = async () => {
    setIsLoading(true);
    try {
      const res = await fetch('https://odiabhasa.pages.dev/puruna-bahi/bahi.json');
      const data = await res.json();
      if (Array.isArray(data) && data.length > 0) {
        const parsed: PurunaBahiData[] = data.map((obj: any, idx: number) => ({
          id: String(obj.id || `puruna_${idx + 1}`),
          title: obj.title || 'ପୁରୁଣା ଓଡ଼ିଆ ପୁସ୍ତକ',
          author: obj.author || 'ଅଜ୍ଞାତ',
          year: String(obj.year || 'ଅଜ୍ଞାତ'),
          category: obj.category || 'ଅନ୍ୟାନ୍ୟ',
          source: obj.source || 'ଅଜ୍ଞାତ',
          publisher: obj.publisher || 'ଅଜ୍ଞାତ',
          url: obj.url || '',
          version: Number(obj.version || 1),
        }));
        setAllBooks(parsed);
      }
    } catch {
      // Keep fallback if offline
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchBooks();
  }, []);

  // Reset to page 1 when filters/search/sort change
  useEffect(() => {
    setCurrentPage(1);
  }, [searchQuery, sortOption, selectedCategories, selectedAuthors, selectedPublishers, selectedYears]);

  const availableCategories = useMemo(
    () =>
      Array.from(
        new Set(
          allBooks
            .flatMap(b => b.category.split(','))
            .map(s => s.trim())
            .filter(Boolean)
        )
      ).sort(),
    [allBooks]
  );

  const availableAuthors = useMemo(
    () =>
      Array.from(
        new Set(
          allBooks
            .flatMap(b => b.author.split(','))
            .map(s => s.trim())
            .filter(Boolean)
        )
      ).sort(),
    [allBooks]
  );

  const availablePublishers = useMemo(
    () =>
      Array.from(
        new Set(
          allBooks
            .flatMap(b => b.publisher.split(','))
            .map(s => s.trim())
            .filter(Boolean)
        )
      ).sort(),
    [allBooks]
  );

  const availableYears = useMemo(
    () =>
      Array.from(
        new Set(
          allBooks
            .flatMap(b => b.year.split(','))
            .map(s => s.trim())
            .filter(Boolean)
        )
      ).sort(),
    [allBooks]
  );

  const filteredBooks = useMemo(() => {
    const queryTrim = searchQuery.trim();
    const queryLower = queryTrim.toLowerCase();
    const queryEngDigits = fromOdiaNum(queryTrim).replace(/[^0-9]/g, '');
    const isJustPrefix = ['puruna', 'puruna_', 'ପୁରୁଣା', 'ପୁରୁଣା_'].includes(queryLower);

    let list = allBooks.filter(book => {
      const numId = book.id.replace(/[^0-9]/g, '');
      const matchesSearch = !queryTrim
        ? true
        : isJustPrefix
        ? false
        : book.title.toLowerCase().includes(queryLower) ||
          book.author.toLowerCase().includes(queryLower) ||
          book.publisher.toLowerCase().includes(queryLower) ||
          book.category.toLowerCase().includes(queryLower) ||
          book.id.toLowerCase().includes(queryLower) ||
          (queryEngDigits.length > 0 && numId === queryEngDigits);

      if (!matchesSearch) return false;

      if (selectedCategories.length > 0) {
        const cats = book.category.split(',').map(s => s.trim());
        if (!cats.some(c => selectedCategories.includes(c))) return false;
      }
      if (selectedAuthors.length > 0) {
        const auths = book.author.split(',').map(s => s.trim());
        if (!auths.some(a => selectedAuthors.includes(a))) return false;
      }
      if (selectedPublishers.length > 0) {
        const pubs = book.publisher.split(',').map(s => s.trim());
        if (!pubs.some(p => selectedPublishers.includes(p))) return false;
      }
      if (selectedYears.length > 0) {
        const yrs = book.year.split(',').map(s => s.trim());
        if (!yrs.some(y => selectedYears.includes(y))) return false;
      }

      return true;
    });

    if (sortOption !== 'କୌଣସିଟି ନୁହେଁ') {
      list = [...list].sort((b1, b2) => {
        if (sortOption === 'ଅକ୍ଷର ଅନୁଯାୟୀ') return b1.title.localeCompare(b2.title);
        if (sortOption === 'ପୁରୁଣା ରୁ ନୂଆ') return extractYearAsInt(b1.year) - extractYearAsInt(b2.year);
        if (sortOption === 'ନୂଆ ରୁ ପୁରୁଣା') return extractYearAsInt(b2.year) - extractYearAsInt(b1.year);
        return 0;
      });
    }

    return list;
  }, [allBooks, searchQuery, sortOption, selectedCategories, selectedAuthors, selectedPublishers, selectedYears]);

  const totalPages = Math.max(1, Math.ceil(filteredBooks.length / BOOKS_PER_PAGE));
  const safeCurrentPage = Math.min(currentPage, totalPages);
  const paginatedBooks = useMemo(() => {
    const start = (safeCurrentPage - 1) * BOOKS_PER_PAGE;
    return filteredBooks.slice(start, start + BOOKS_PER_PAGE);
  }, [filteredBooks, safeCurrentPage]);

  const activeFilters = [
    ...selectedCategories,
    ...selectedAuthors,
    ...selectedPublishers,
    ...selectedYears,
  ];

  const handleCopy = (text: string, label: string) => {
    navigator.clipboard?.writeText(text);
    setCopiedField(label);
    setTimeout(() => setCopiedField(null), 2000);
  };

  // Visible page numbers for pagination bar
  const visiblePageNumbers = useMemo(() => {
    const pages: number[] = [];
    const maxButtons = 5;
    let start = Math.max(1, safeCurrentPage - 2);
    const end = Math.min(totalPages, start + maxButtons - 1);
    if (end - start + 1 < maxButtons) {
      start = Math.max(1, end - maxButtons + 1);
    }
    for (let p = start; p <= end; p++) pages.push(p);
    return pages;
  }, [safeCurrentPage, totalPages]);

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-24">
      {/* TopAppBar matching PurunaBahi.kt */}
      <header className="sticky top-0 z-20 bg-[#F8F9FA] border-b border-slate-200 px-4 py-3">
        <div className="max-w-4xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-6 h-6 text-slate-900" />
            </button>
            <h1 className="text-[22px] font-bold text-slate-900">ପୁରୁଣା ବହି</h1>
          </div>

          <div className="flex items-center gap-1">
            <button
              onClick={() => setShowFilterSheet(true)}
              title="ସନ୍ଧାନ ବିକଳ୍ପ"
              className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer relative"
            >
              <ListFilter className="w-5 h-5 text-slate-800" />
              {activeFilters.length > 0 && (
                <span className="w-2.5 h-2.5 rounded-full bg-[#D84315] absolute top-2 right-2" />
              )}
            </button>
            <button
              onClick={fetchBooks}
              title="Refresh"
              className="w-10 h-10 rounded-full hover:bg-black/5 flex items-center justify-center cursor-pointer"
            >
              <RefreshCw className={`w-5 h-5 text-slate-800 ${isLoading ? 'animate-spin' : ''}`} />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 pt-3 space-y-3">
        {/* Search Bar matching PurunaBahi.kt */}
        <div className="relative">
          <Search className="w-5 h-5 text-gray-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="ସନ୍ଧାନ କରନ୍ତୁ ବା ବହି ଆଇଡି ଦିଅନ୍ତୁ..."
            className="w-full pl-11 pr-10 py-3.5 rounded-[14px] bg-white border border-gray-300 focus:border-[#6200EE] outline-none font-semibold text-sm text-slate-900 shadow-2xs"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery('')}
              className="absolute right-3 top-1/2 -translate-y-1/2 p-1 text-gray-500 hover:text-black cursor-pointer"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>

        {/* Info and Sort Row matching PurunaBahi.kt */}
        <div className="flex items-center justify-between px-1">
          <span className="text-[14px] font-medium text-gray-600">
            ସମୁଦାୟ {toOdiaNum(filteredBooks.length)}ଟି ବହି (ପୃଷ୍ଠା {toOdiaNum(safeCurrentPage)} / {toOdiaNum(totalPages)})
          </span>

          <button
            onClick={() => setShowSortSheet(true)}
            className="flex items-center gap-1.5 text-[#6200EE] font-bold text-sm py-1 px-2 rounded-lg hover:bg-[#6200EE]/10 cursor-pointer"
          >
            <ArrowUpDown className="w-4 h-4" />
            <span>{sortOption === 'କୌଣସିଟି ନୁହେଁ' ? 'ସଜାଡନ୍ତୁ' : sortOption}</span>
          </button>
        </div>

        {/* Active Filter Chips */}
        {activeFilters.length > 0 && (
          <div className="flex gap-2 overflow-x-auto pb-1">
            {activeFilters.map(f => (
              <button
                key={f}
                onClick={() => {
                  setSelectedCategories(prev => prev.filter(x => x !== f));
                  setSelectedAuthors(prev => prev.filter(x => x !== f));
                  setSelectedPublishers(prev => prev.filter(x => x !== f));
                  setSelectedYears(prev => prev.filter(x => x !== f));
                }}
                className="px-3 py-1.5 rounded-[8px] bg-[#6200EE]/12 text-[#6200EE] text-xs font-bold flex items-center gap-1.5 shrink-0 cursor-pointer"
              >
                <span>{f}</span>
                <X className="w-3.5 h-3.5" />
              </button>
            ))}
          </div>
        )}

        {/* Book List matching PurunaBahi.kt Card items */}
        {isLoading && allBooks.length <= 4 ? (
          <div className="space-y-3">
            {Array.from({ length: 6 }).map((_, idx) => (
              <div
                key={idx}
                className="bg-white rounded-[16px] p-5 shadow-xs flex items-center gap-4 animate-pulse"
              >
                <div className="w-[45px] h-[45px] rounded-full bg-gray-200 shrink-0" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-3/4" />
                  <div className="h-3 bg-gray-200 rounded w-1/2" />
                  <div className="h-3 bg-gray-200 rounded w-1/3" />
                </div>
              </div>
            ))}
          </div>
        ) : filteredBooks.length === 0 ? (
          <div className="py-20 text-center text-gray-500 font-bold text-lg">
            କୌଣସି ବହି ମିଳିଲା ନାହିଁ ।
          </div>
        ) : (
          <div className="space-y-3">
            {paginatedBooks.map(book => (
              <div
                key={book.id}
                onClick={() => setSelectedBook(book)}
                className="bg-white rounded-[16px] p-4 sm:p-5 shadow-xs hover:shadow-md transition flex items-center justify-between gap-4 cursor-pointer border border-slate-100"
              >
                <div className="flex items-center gap-4 min-w-0 flex-1">
                  <div className="w-[45px] h-[45px] rounded-full bg-[#FFF8E1] flex items-center justify-center shrink-0">
                    <BookOpen className="w-6 h-6 text-[#D4AF37]" />
                  </div>
                  <div className="min-w-0 flex-1">
                    <h3 className="text-[18px] font-bold text-slate-900 leading-snug line-clamp-2">
                      {book.title}
                    </h3>
                    <p className="text-[14px] text-slate-700 mt-1 truncate">
                      ଲେଖକ: {book.author}
                    </p>
                    <p className="text-[13px] text-gray-500 mt-0.5 truncate">
                      ପ୍ରକାଶକ: {book.publisher || 'ଅଜ୍ଞାତ'} • ବର୍ଷ: {book.year}
                    </p>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  <button
                    onClick={e => {
                      e.stopPropagation();
                      setSelectedBook(book);
                    }}
                    className="px-3.5 py-2 rounded-xl bg-[#6200EE] text-white font-bold text-xs flex items-center gap-1.5 cursor-pointer shadow-xs"
                  >
                    <BookOpen className="w-4 h-4" /> ବହି ଖୋଲନ୍ତୁ
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Pagination Bar ("Add Pages" requested by user) */}
        {totalPages > 1 && (
          <div className="pt-4 pb-6 flex flex-col items-center gap-3">
            <div className="flex flex-wrap items-center justify-center gap-1.5">
              <button
                disabled={safeCurrentPage <= 1}
                onClick={() => {
                  setCurrentPage(p => Math.max(1, p - 1));
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                className="px-3 py-2 rounded-xl bg-white border border-slate-300 font-bold text-xs disabled:opacity-40 flex items-center gap-1 cursor-pointer"
              >
                <ChevronLeft className="w-4 h-4" /> ପୂର୍ବ
              </button>

              {visiblePageNumbers[0] > 1 && (
                <>
                  <button
                    onClick={() => {
                      setCurrentPage(1);
                      window.scrollTo({ top: 0, behavior: 'smooth' });
                    }}
                    className="w-9 h-9 rounded-xl bg-white border border-slate-300 font-bold text-sm cursor-pointer"
                  >
                    {toOdiaNum(1)}
                  </button>
                  {visiblePageNumbers[0] > 2 && <span className="px-1 text-gray-400">...</span>}
                </>
              )}

              {visiblePageNumbers.map(pageNum => (
                <button
                  key={pageNum}
                  onClick={() => {
                    setCurrentPage(pageNum);
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                  }}
                  className={`w-9 h-9 rounded-xl font-bold text-sm cursor-pointer transition ${
                    safeCurrentPage === pageNum
                      ? 'bg-[#6200EE] text-white shadow-sm'
                      : 'bg-white border border-slate-300 text-slate-800 hover:bg-slate-100'
                  }`}
                >
                  {toOdiaNum(pageNum)}
                </button>
              ))}

              {visiblePageNumbers[visiblePageNumbers.length - 1] < totalPages && (
                <>
                  {visiblePageNumbers[visiblePageNumbers.length - 1] < totalPages - 1 && (
                    <span className="px-1 text-gray-400">...</span>
                  )}
                  <button
                    onClick={() => {
                      setCurrentPage(totalPages);
                      window.scrollTo({ top: 0, behavior: 'smooth' });
                    }}
                    className="px-2.5 h-9 rounded-xl bg-white border border-slate-300 font-bold text-sm cursor-pointer"
                  >
                    {toOdiaNum(totalPages)}
                  </button>
                </>
              )}

              <button
                disabled={safeCurrentPage >= totalPages}
                onClick={() => {
                  setCurrentPage(p => Math.min(totalPages, p + 1));
                  window.scrollTo({ top: 0, behavior: 'smooth' });
                }}
                className="px-3 py-2 rounded-xl bg-white border border-slate-300 font-bold text-xs disabled:opacity-40 flex items-center gap-1 cursor-pointer"
              >
                ପରବର୍ତ୍ତୀ <ChevronRight className="w-4 h-4" />
              </button>
            </div>

            <div className="text-xs font-bold text-slate-500">
              ପୃଷ୍ଠା {toOdiaNum(safeCurrentPage)} / {toOdiaNum(totalPages)} • ମୋଟ {toOdiaNum(filteredBooks.length)}ଟି ବହି
            </div>
          </div>
        )}
      </main>

      {/* SORT BOTTOM SHEET matching PurunaBahi.kt */}
      {showSortSheet && (
        <div
          onClick={() => setShowSortSheet(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-lg rounded-t-[28px] p-6 pb-8 space-y-3 shadow-2xl"
          >
            <div className="w-10 h-1 bg-gray-300 rounded-full mx-auto mb-2" />
            <h3 className="text-[20px] font-bold text-[#6200EE] mb-3">କିପରି ସଜାଡିବେ?</h3>
            {SORT_OPTIONS.map(option => {
              const isSelected = sortOption === option;
              return (
                <div
                  key={option}
                  onClick={() => {
                    setSortOption(option);
                    setShowSortSheet(false);
                  }}
                  className={`flex items-center gap-4 px-4 py-3.5 rounded-[12px] cursor-pointer ${
                    isSelected ? 'bg-[#6200EE]/12 text-[#6200EE] font-bold' : 'text-slate-800'
                  }`}
                >
                  <div
                    className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                      isSelected ? 'border-[#6200EE]' : 'border-gray-400'
                    }`}
                  >
                    {isSelected && <div className="w-2.5 h-2.5 rounded-full bg-[#6200EE]" />}
                  </div>
                  <span className="text-[16px]">{option}</span>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* FILTER BOTTOM SHEET matching PurunaBahi.kt */}
      {showFilterSheet && (
        <div
          onClick={() => setShowFilterSheet(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-xl rounded-t-[28px] h-[540px] flex flex-col overflow-hidden shadow-2xl"
          >
            <div className="w-10 h-1 bg-gray-300 rounded-full mx-auto mt-3 mb-1" />
            <div className="px-5 py-3 flex items-center justify-between border-b border-gray-200">
              <h3 className="text-[20px] font-bold text-[#6200EE]">ସନ୍ଧାନ ବିକଳ୍ପ</h3>
              {activeFilters.length > 0 && (
                <span className="text-sm font-bold text-[#D84315]">
                  {toOdiaNum(activeFilters.length)}ଟି ବଛା ଯାଇଛି
                </span>
              )}
            </div>

            <div className="flex-1 flex min-h-0">
              {/* Left Tabs */}
              <div className="w-[35%] bg-[#F5F5F5] border-r border-gray-200">
                {(['ବିଭାଗ', 'ଲେଖକ', 'ପ୍ରକାଶକ', 'ବର୍ଷ'] as const).map(tab => {
                  const isSelected = activeFilterTab === tab;
                  return (
                    <button
                      key={tab}
                      onClick={() => {
                        setActiveFilterTab(tab);
                        setSheetSearchQuery('');
                      }}
                      className={`w-full text-left py-4 px-3 text-[15px] cursor-pointer ${
                        isSelected
                          ? 'bg-white text-[#6200EE] font-bold'
                          : 'text-gray-700 font-normal'
                      }`}
                    >
                      {tab}
                    </button>
                  );
                })}
              </div>

              {/* Right Options List */}
              <div className="w-[65%] flex flex-col min-h-0">
                <div className="p-3">
                  <div className="relative">
                    <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
                    <input
                      type="text"
                      value={sheetSearchQuery}
                      onChange={e => setSheetSearchQuery(e.target.value)}
                      placeholder="ଖୋଜନ୍ତୁ..."
                      className="w-full pl-9 pr-3 py-2 rounded-[10px] border border-gray-300 text-sm outline-none focus:border-[#6200EE]"
                    />
                  </div>
                </div>

                <div className="flex-1 overflow-y-auto px-3 pb-3 space-y-1">
                  {(() => {
                    const rawList =
                      activeFilterTab === 'ବିଭାଗ'
                        ? availableCategories
                        : activeFilterTab === 'ଲେଖକ'
                        ? availableAuthors
                        : activeFilterTab === 'ପ୍ରକାଶକ'
                        ? availablePublishers
                        : availableYears;

                    const filteredList = !sheetSearchQuery.trim()
                      ? rawList
                      : rawList.filter(item =>
                          item.toLowerCase().includes(sheetSearchQuery.toLowerCase())
                        );

                    const currentSet =
                      activeFilterTab === 'ବିଭାଗ'
                        ? selectedCategories
                        : activeFilterTab === 'ଲେଖକ'
                        ? selectedAuthors
                        : activeFilterTab === 'ପ୍ରକାଶକ'
                        ? selectedPublishers
                        : selectedYears;

                    if (filteredList.length === 0) {
                      return (
                        <div className="py-12 text-center text-sm text-gray-400">ମିଳିଲା ନାହିଁ</div>
                      );
                    }

                    return filteredList.map(item => {
                      const isChecked = currentSet.includes(item);
                      return (
                        <label
                          key={item}
                          onClick={() => {
                            const toggle = (prev: string[]) =>
                              prev.includes(item) ? prev.filter(x => x !== item) : [...prev, item];
                            if (activeFilterTab === 'ବିଭାଗ') setSelectedCategories(toggle);
                            else if (activeFilterTab === 'ଲେଖକ') setSelectedAuthors(toggle);
                            else if (activeFilterTab === 'ପ୍ରକାଶକ') setSelectedPublishers(toggle);
                            else setSelectedYears(toggle);
                          }}
                          className="flex items-center gap-3 px-2 py-2.5 rounded-lg hover:bg-slate-50 cursor-pointer"
                        >
                          <input
                            type="checkbox"
                            checked={isChecked}
                            readOnly
                            className="w-4 h-4 accent-[#6200EE]"
                          />
                          <span className="text-sm text-slate-800 line-clamp-2">{item}</span>
                        </label>
                      );
                    });
                  })()}
                </div>
              </div>
            </div>

            <div className="p-4 border-t border-gray-200 flex gap-4">
              <button
                onClick={() => {
                  setSelectedCategories([]);
                  setSelectedAuthors([]);
                  setSelectedPublishers([]);
                  setSelectedYears([]);
                }}
                className="flex-1 py-3 rounded-[12px] border border-gray-300 font-bold text-sm text-slate-700 cursor-pointer"
              >
                ସଫା କରନ୍ତୁ
              </button>
              <button
                onClick={() => setShowFilterSheet(false)}
                className="flex-1 py-3 rounded-[12px] bg-[#6200EE] text-white font-bold text-sm cursor-pointer"
              >
                ପ୍ରୟୋଗ କରନ୍ତୁ
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ODIA APP-ONLY POPUP MODAL WHEN USER CLICKS A BOOK OR "ବହି ଖୋଲନ୍ତୁ" */}
      {selectedBook && (
        <div
          onClick={() => setSelectedBook(null)}
          className="fixed inset-0 z-50 bg-black/60 backdrop-blur-xs flex items-center justify-center p-4"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-lg rounded-[24px] p-6 shadow-2xl space-y-4 max-h-[92vh] overflow-y-auto border-2 border-[#6200EE]"
          >
            {/* Header */}
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-[#6200EE]/12 flex items-center justify-center shrink-0">
                  <BookOpen className="w-6 h-6 text-[#6200EE]" />
                </div>
                <div>
                  <span className="inline-block px-2.5 py-0.5 rounded-full bg-amber-100 text-amber-900 font-black text-xs mb-1">
                    📱 କେବଳ ଓଡ଼ିଆ ଭାଷା ଆପ୍ ରେ ଉପଲବ୍ଧ
                  </span>
                  <h2 className="text-[20px] font-black text-slate-900 leading-snug">
                    {selectedBook.title}
                  </h2>
                </div>
              </div>
              <button
                onClick={() => setSelectedBook(null)}
                className="p-1.5 rounded-full bg-slate-100 hover:bg-slate-200 cursor-pointer shrink-0"
              >
                <X className="w-5 h-5 text-slate-700" />
              </button>
            </div>

            {/* Odia Notice Banner */}
            <div className="p-3.5 rounded-2xl bg-[#FFF8E1] border border-amber-300 text-amber-950 text-sm font-bold leading-relaxed">
              ଆପଣ ଏହି ବହିଟିକୁ କେବଳ <strong>&ldquo;ଓଡ଼ିଆ ଭାଷା&rdquo;</strong> ମୋବାଇଲ୍ ଆପ୍ ଭିତରେ ପଢ଼ିପାରିବେ । ତଳେ ଦିଆଯାଇଥିବା <strong>ବହି ଆଇଡି (Book ID)</strong> କିମ୍ବା <strong>ବହିର ନାମ</strong> ଓଡ଼ିଆ ଭାଷା ଆପ୍ ର <strong>&lsquo;ପୁରୁଣା ବହି&rsquo;</strong> ସ୍କ୍ରିନ୍ ରେ ସର୍ଚ୍ଚ କରନ୍ତୁ:
            </div>

            {/* Full Book Details Table matching user's exact specification */}
            <div className="bg-slate-50 rounded-2xl p-4 border border-slate-200 space-y-2.5 text-[15px]">
              <div className="flex items-center justify-between gap-2 pb-2 border-b border-slate-200">
                <span className="text-gray-500 font-semibold">ବହି ଆଇଡି (Book ID) :</span>
                <div className="flex items-center gap-2">
                  <code className="px-2.5 py-1 rounded-lg bg-[#6200EE]/10 text-[#6200EE] font-black text-sm">
                    {selectedBook.id}
                  </code>
                  <span className="text-xs font-bold text-slate-500">
                    ({toOdiaNum(selectedBook.id.replace(/[^0-9]/g, ''))})
                  </span>
                  <button
                    onClick={() => handleCopy(selectedBook.id.replace(/[^0-9]/g, '') || selectedBook.id, 'id')}
                    className="p-1.5 rounded-lg bg-white border border-slate-300 hover:bg-slate-100 cursor-pointer"
                    title="ଆଇଡି କପି କରନ୍ତୁ"
                  >
                    <Copy className="w-3.5 h-3.5 text-slate-700" />
                  </button>
                </div>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ବହିର ନାମ :</span>
                <div className="flex-1 text-right font-bold text-slate-900 flex items-start justify-end gap-2">
                  <span>{selectedBook.title}</span>
                  <button
                    onClick={() => handleCopy(selectedBook.title, 'title')}
                    className="p-1.5 rounded-lg bg-white border border-slate-300 hover:bg-slate-100 cursor-pointer shrink-0"
                    title="ନାମ କପି କରନ୍ତୁ"
                  >
                    <Copy className="w-3.5 h-3.5 text-slate-700" />
                  </button>
                </div>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ଲେଖକ/ସଂପାଦକ :</span>
                <span className="font-bold text-slate-800 text-right">{selectedBook.author}</span>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ପ୍ରକାଶନ ବର୍ଷ :</span>
                <span className="font-bold text-slate-800 text-right">{selectedBook.year}</span>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ବିଭାଗ :</span>
                <span className="font-bold text-slate-800 text-right">{selectedBook.category}</span>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ଉତ୍ସ :</span>
                <span className="font-bold text-slate-800 text-right">{selectedBook.source}</span>
              </div>

              <div className="flex items-start justify-between gap-2 py-1">
                <span className="text-gray-500 font-semibold shrink-0 w-28">ପ୍ରକାଶକ :</span>
                <span className="font-bold text-[#D84315] text-right">{selectedBook.publisher || 'ଅଜ୍ଞାତ'}</span>
              </div>
            </div>

            {copiedField && (
              <div className="text-center text-xs font-black text-green-700 bg-green-50 py-1.5 rounded-xl border border-green-200">
                ✓ କପି ହୋଇଗଲା! ଏବେ ଆପ୍ ଖୋଲି ସର୍ଚ୍ଚ କରନ୍ତୁ ।
              </div>
            )}

            {/* Search Instruction Box */}
            <div className="p-3.5 rounded-2xl bg-[#F3E5F5] border border-[#6200EE]/25 text-center space-y-1">
              <p className="text-xs font-bold text-[#4A148C]">
                🔍 ଓଡ଼ିଆ ଭାଷା ଆପ୍ ର &lsquo;ପୁରୁଣା ବହି&rsquo; ସ୍କ୍ରିନ୍ ରେ ସର୍ଚ୍ଚ କରନ୍ତୁ:
              </p>
              <p className="text-sm font-black text-black">
                &ldquo;{selectedBook.id.replace(/[^0-9]/g, '') || selectedBook.id}&rdquo; କିମ୍ବା &ldquo;{selectedBook.title}&rdquo;
              </p>
            </div>

            {/* Action Buttons to Open the App */}
            <div className="space-y-2.5 pt-1">
              <a
                href="https://odiabhasa.pages.dev/open?PURUNA_BAHI"
                target="_blank"
                rel="noopener noreferrer"
                className="w-full py-3.5 px-4 rounded-2xl bg-[#6200EE] hover:bg-[#5000CA] text-white font-black text-base flex items-center justify-center gap-2 shadow-md transition"
              >
                <Smartphone className="w-5 h-5" />
                ଆପ୍ ଖୋଲନ୍ତୁ (ପୁରୁଣା ବହି ବିଭାଗ)
                <ExternalLink className="w-4 h-4" />
              </a>

              <div className="flex items-center gap-2">
                <a
                  href="https://play.google.com/store/apps/details?id=in.sandeep.odiabhasa"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex-1 py-2.5 px-3 rounded-xl bg-[#2E7D32] text-white font-bold text-xs flex items-center justify-center gap-1.5"
                >
                  ଗୁଗଲ୍ ପ୍ଲେ-ଷ୍ଟୋରରୁ ଆପ୍ ଡାଉନଲୋଡ୍ କରନ୍ତୁ
                </a>
                <button
                  onClick={() => setSelectedBook(null)}
                  className="px-4 py-2.5 rounded-xl border border-slate-300 font-bold text-xs text-slate-700 cursor-pointer"
                >
                  ବନ୍ଦ କରନ୍ତୁ
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
