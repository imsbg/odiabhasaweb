import React, { useState, useRef, useEffect } from 'react';
import {
  ArrowLeft, SlidersHorizontal, Volume2, Edit3, Trash2,
  ChevronLeft, ChevronRight
} from 'lucide-react';
import {
  Screen,
  swarabarnaList,
  byanjanabarnaList,
  sankhyaList,
  yuktakshyaraList,
  matraList,
  phalaList,
  barnaParichayaList,
  deprecatedList,
  getCharDetails
} from '../data/odiaData';
import { playOdiaAudio, toOdiaNum } from '../utils/odiaUtils';

interface CommonGridPageProps {
  screen: Screen;
  onBack: () => void;
  onSelectChar: (char: string, list: string[]) => void;
}

export const CommonGridPage: React.FC<CommonGridPageProps> = ({
  screen,
  onBack,
  onSelectChar
}) => {
  const [columns, setColumns] = useState<number>(() => {
    const saved = localStorage.getItem('grid_columns');
    return saved ? parseInt(saved, 10) : 3;
  });
  const [showDeprecated, setShowDeprecated] = useState<boolean>(() => {
    return localStorage.getItem('show_deprecated') === 'true';
  });
  const [playAudioOnTap, setPlayAudioOnTap] = useState<boolean>(() => {
    return localStorage.getItem('play_audio_tap') !== 'false';
  });
  const [showSettings, setShowSettings] = useState(false);

  useEffect(() => {
    localStorage.setItem('grid_columns', String(columns));
  }, [columns]);

  useEffect(() => {
    localStorage.setItem('show_deprecated', String(showDeprecated));
  }, [showDeprecated]);

  useEffect(() => {
    localStorage.setItem('play_audio_tap', String(playAudioOnTap));
  }, [playAudioOnTap]);

  const { title, items } = (() => {
    switch (screen) {
      case Screen.SWARA:
        return { title: 'ସ୍ୱରବର୍ଣ୍ଣ', items: swarabarnaList };
      case Screen.BYANJANA:
        return { title: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ', items: byanjanabarnaList };
      case Screen.SANKHYA:
        return { title: 'ସଂଖ୍ୟା', items: sankhyaList };
      case Screen.YUKTA:
        return { title: 'ଯୁକ୍ତାକ୍ଷର', items: yuktakshyaraList };
      case Screen.MATRA:
        return { title: 'ମାତ୍ରା', items: matraList };
      case Screen.PHALA:
        return { title: 'ଫଳା', items: phalaList };
      case Screen.BARNA_PARICHAYA:
        return { title: 'ବର୍ଣ୍ଣ ପରିଚୟ', items: barnaParichayaList };
      default:
        return { title: 'ସ୍ୱରବର୍ଣ୍ଣ', items: swarabarnaList };
    }
  })();

  const filteredItems = showDeprecated
    ? items
    : items.filter(ch => !deprecatedList.includes(ch));

  const gridColsClass: Record<number, string> = {
    2: 'grid-cols-2',
    3: 'grid-cols-3 sm:grid-cols-4',
    4: 'grid-cols-4 sm:grid-cols-5',
    5: 'grid-cols-5 sm:grid-cols-6',
    6: 'grid-cols-6 sm:grid-cols-8'
  };

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-20">
      {/* Top Bar matching CommonGridPage.kt */}
      <header className="sticky top-0 z-20 bg-[#F8F9FA] px-5 pt-4 pb-3">
        <div className="max-w-3xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-4">
            <button
              onClick={onBack}
              className="w-[45px] h-[45px] rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer bouncing-click"
            >
              <ArrowLeft className="w-5 h-5 text-slate-900" />
            </button>
            <h1 className="text-[26px] font-bold text-slate-900">{title}</h1>
          </div>

          <button
            onClick={() => setShowSettings(true)}
            title="ସେଟିଂସ୍"
            className="w-[45px] h-[45px] rounded-full bg-[#6200EE]/10 flex items-center justify-center cursor-pointer bouncing-click"
          >
            <SlidersHorizontal className="w-5 h-5 text-[#6200EE]" />
          </button>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-5 pt-2">
        <div className={`grid ${gridColsClass[columns] || 'grid-cols-3'} gap-4`}>
          {filteredItems.map(ch => {
            const displayChar = ch.includes(' (') ? ch.split(' (')[0] : ch;
            const subLabel = ch.includes(' (') ? ch.split(' (')[1].replace(')', '') : '';
            return (
              <button
                key={ch}
                onClick={() => {
                  playOdiaAudio(displayChar, playAudioOnTap);
                  onSelectChar(ch, filteredItems);
                }}
                className="aspect-square rounded-[22px] bg-white shadow-xs border border-slate-100 flex flex-col items-center justify-center p-2 cursor-pointer bouncing-click"
              >
                <span className="text-3xl sm:text-4xl font-bold text-[#6200EE] leading-none">
                  {displayChar}
                </span>
                {subLabel && (
                  <span className="text-xs font-bold mt-1.5 text-slate-500">
                    ({subLabel})
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </main>

      {/* Grid Settings Bottom Sheet matching CommonGridPage.kt */}
      {showSettings && (
        <div
          onClick={() => setShowSettings(false)}
          className="fixed inset-0 z-50 bg-black/50 flex items-end justify-center"
        >
          <div
            onClick={e => e.stopPropagation()}
            className="bg-white w-full max-w-xl rounded-t-[28px] p-6 pb-8 shadow-2xl space-y-5"
          >
            <div className="w-10 h-1 bg-slate-300 rounded-full mx-auto" />
            <h3 className="text-[22px] font-bold text-[#6200EE]">ସେଟିଂସ୍</h3>

            <div className="space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-base font-medium text-slate-800">ଧାଡ଼ି ସଂଖ୍ୟା</span>
                <span className="text-lg font-bold text-[#6200EE]">{toOdiaNum(columns)}</span>
              </div>
              <div className="flex gap-2">
                {[2, 3, 4, 5, 6].map(n => (
                  <button
                    key={n}
                    onClick={() => setColumns(n)}
                    className={`flex-1 py-2.5 rounded-xl font-bold cursor-pointer ${
                      columns === n
                        ? 'bg-[#6200EE] text-white'
                        : 'bg-slate-100 text-slate-700'
                    }`}
                  >
                    {toOdiaNum(n)}
                  </button>
                ))}
              </div>
            </div>

            <label className="flex items-center justify-between py-2 cursor-pointer">
              <div>
                <div className="text-base font-medium text-slate-900">ଅବ୍ୟବହୃତ ଅକ୍ଷର</div>
                <div className="text-xs text-slate-500">ୠ, ଌ, ୡ ଆଦି ଅକ୍ଷର ଦେଖାନ୍ତୁ</div>
              </div>
              <input
                type="checkbox"
                checked={showDeprecated}
                onChange={e => setShowDeprecated(e.target.checked)}
                className="w-5 h-5 accent-[#6200EE]"
              />
            </label>

            <label className="flex items-center justify-between py-2 cursor-pointer">
              <div>
                <div className="text-base font-medium text-slate-900">ଅକ୍ଷର ଧ୍ୱନି</div>
                <div className="text-xs text-slate-500">ଅକ୍ଷର ଉପରେ କ୍ଲିକ୍ କଲେ ଉଚ୍ଚାରଣ ଶୁଣନ୍ତୁ</div>
              </div>
              <input
                type="checkbox"
                checked={playAudioOnTap}
                onChange={e => setPlayAudioOnTap(e.target.checked)}
                className="w-5 h-5 accent-[#6200EE]"
              />
            </label>

            <button
              onClick={() => setShowSettings(false)}
              className="w-full py-3 rounded-2xl bg-[#6200EE] text-white font-bold cursor-pointer"
            >
              ବନ୍ଦ କରନ୍ତୁ
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

interface DetailScreenProps {
  char: string;
  charList: string[];
  onBack: () => void;
  onChangeChar: (newChar: string) => void;
}

export const DetailScreen: React.FC<DetailScreenProps> = ({
  char,
  charList,
  onBack,
  onChangeChar
}) => {
  const details = getCharDetails(char);
  const currentIdx = charList.indexOf(char);
  const [showDrawingPad, setShowDrawingPad] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [isDrawing, setIsDrawing] = useState(false);
  const [penColor, setPenColor] = useState('#EC407A');

  const clearCanvas = () => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
    }
  };

  useEffect(() => {
    clearCanvas();
  }, [char, showDrawingPad]);

  const handlePointerDown = (e: React.PointerEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.strokeStyle = penColor;
    ctx.lineWidth = 8;
    ctx.lineCap = 'round';
    ctx.lineJoin = 'round';
    ctx.beginPath();
    ctx.moveTo(e.clientX - rect.left, e.clientY - rect.top);
    setIsDrawing(true);
  };

  const handlePointerMove = (e: React.PointerEvent<HTMLCanvasElement>) => {
    if (!isDrawing) return;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.lineTo(e.clientX - rect.left, e.clientY - rect.top);
    ctx.stroke();
  };

  const handlePointerUp = () => {
    setIsDrawing(false);
  };

  const displayChar = char.includes(' (') ? char.split(' (')[0] : char;

  return (
    <div className="min-h-screen bg-[#F8F9FA] pb-24">
      <header className="sticky top-0 z-20 bg-[#F8F9FA] px-5 pt-4 pb-3">
        <div className="max-w-2xl mx-auto flex items-center justify-between">
          <button
            onClick={onBack}
            className="w-[45px] h-[45px] rounded-full bg-white shadow-xs flex items-center justify-center cursor-pointer bouncing-click"
          >
            <ArrowLeft className="w-5 h-5 text-slate-900" />
          </button>

          <div className="flex items-center gap-2">
            <button
              onClick={() => playOdiaAudio(displayChar, true)}
              className="w-[45px] h-[45px] rounded-full bg-[#EC407A]/12 text-[#EC407A] flex items-center justify-center cursor-pointer bouncing-click"
              title="ଶୁଣନ୍ତୁ"
            >
              <Volume2 className="w-5 h-5" />
            </button>
            <button
              onClick={() => setShowDrawingPad(p => !p)}
              className={`px-4 py-2 rounded-full font-bold text-xs flex items-center gap-1.5 cursor-pointer bouncing-click ${
                showDrawingPad
                  ? 'bg-[#EC407A] text-white'
                  : 'bg-white text-slate-800 shadow-xs'
              }`}
            >
              <Edit3 className="w-4 h-4" /> ଲେଖନ୍ତୁ
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-2xl mx-auto px-5 space-y-5">
        {/* Hero Pink Gradient Card matching DetailScreen in CommonGridPage.kt */}
        {!showDrawingPad ? (
          <div
            onClick={() => playOdiaAudio(displayChar, true)}
            className="h-[220px] rounded-[32px] bg-gradient-to-br from-[#F48FB1] to-[#EC407A] shadow-lg flex flex-col items-center justify-center text-white cursor-pointer bouncing-click"
          >
            <span className="text-[96px] font-black leading-none">{displayChar}</span>
            {char.includes(' (') && (
              <span className="text-lg font-bold mt-2 opacity-90">{char}</span>
            )}
          </div>
        ) : (
          <div className="bg-white rounded-[28px] p-5 shadow-sm space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-sm font-bold text-slate-700">ଅକ୍ଷର ଉପରେ ଆଙ୍ଗୁଠି ବୁଲାଇ ଲେଖନ୍ତୁ:</span>
              <div className="flex items-center gap-2">
                {['#EC407A', '#6200EE', '#2E7D32', '#000000'].map(c => (
                  <button
                    key={c}
                    onClick={() => setPenColor(c)}
                    style={{ backgroundColor: c }}
                    className={`w-6 h-6 rounded-full border-2 ${
                      penColor === c ? 'border-black scale-125' : 'border-transparent'
                    } cursor-pointer`}
                  />
                ))}
                <button
                  onClick={clearCanvas}
                  className="p-1.5 rounded-lg bg-red-50 text-red-600 cursor-pointer"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
            <div className="relative w-full h-60 rounded-2xl bg-[#FAFAFA] border-2 border-dashed border-slate-300 flex items-center justify-center overflow-hidden">
              <span className="text-9xl font-black text-slate-200 select-none pointer-events-none">
                {displayChar}
              </span>
              <canvas
                ref={canvasRef}
                width={500}
                height={240}
                onPointerDown={handlePointerDown}
                onPointerMove={handlePointerMove}
                onPointerUp={handlePointerUp}
                onPointerLeave={handlePointerUp}
                className="absolute inset-0 w-full h-full touch-none cursor-crosshair"
              />
            </div>
          </div>
        )}

        {/* Prev / Next Navigation */}
        {charList.length > 1 && (
          <div className="flex items-center justify-between">
            <button
              disabled={currentIdx <= 0}
              onClick={() => currentIdx > 0 && onChangeChar(charList[currentIdx - 1])}
              className="flex items-center gap-1 px-4 py-2 rounded-full bg-white shadow-xs font-bold text-xs text-slate-700 disabled:opacity-40 cursor-pointer"
            >
              <ChevronLeft className="w-4 h-4" /> ପୂର୍ବ ଅକ୍ଷର
            </button>
            <span className="text-xs font-bold text-slate-500">
              {toOdiaNum(currentIdx + 1)} / {toOdiaNum(charList.length)}
            </span>
            <button
              disabled={currentIdx >= charList.length - 1}
              onClick={() => currentIdx < charList.length - 1 && onChangeChar(charList[currentIdx + 1])}
              className="flex items-center gap-1 px-4 py-2 rounded-full bg-white shadow-xs font-bold text-xs text-slate-700 disabled:opacity-40 cursor-pointer"
            >
              ପରବର୍ତ୍ତୀ ଅକ୍ଷର <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        )}

        {/* Description Card */}
        {details.description && (
          <div className="bg-white rounded-[20px] p-4 shadow-xs text-center font-bold text-slate-700">
            {details.description}
          </div>
        )}

        {/* Words List matching CommonGridPage.kt */}
        {details.words.length > 0 && (
          <div className="space-y-3">
            <h2 className="text-[20px] font-bold text-[#EC407A]">ଶବ୍ଦ ଗଠନ :</h2>
            <div className="space-y-2.5">
              {details.words.map(word => (
                <div
                  key={word}
                  onClick={() => playOdiaAudio(word, true)}
                  className="bg-white rounded-[16px] p-4 shadow-xs flex items-center justify-between cursor-pointer bouncing-click"
                >
                  <div className="flex items-center gap-3">
                    <span className="w-2.5 h-2.5 rounded-full bg-[#EC407A]" />
                    <span className="text-[20px] font-medium text-slate-900">{word}</span>
                  </div>
                  <Volume2 className="w-5 h-5 text-slate-400" />
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Sentences List */}
        {details.sentences && details.sentences.length > 0 && (
          <div className="space-y-3">
            <h2 className="text-[20px] font-bold text-[#6200EE]">ବାକ୍ୟ ଗଠନ :</h2>
            <div className="space-y-2.5">
              {details.sentences.map((sentence, idx) => (
                <div
                  key={idx}
                  onClick={() => playOdiaAudio(sentence, true)}
                  className="bg-white rounded-[16px] p-4 shadow-xs flex items-center justify-between gap-3 cursor-pointer bouncing-click"
                >
                  <p className="font-medium text-slate-900 text-base">{sentence}</p>
                  <Volume2 className="w-5 h-5 text-[#6200EE] shrink-0" />
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};
