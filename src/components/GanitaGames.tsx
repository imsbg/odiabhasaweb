import React, { useState, useRef, useEffect } from 'react';
import {
  ArrowLeft, Edit3, Trash2, CheckCircle2, XCircle, RotateCcw,
  Lightbulb, Sparkles, Trophy
} from 'lucide-react';
import {
  toOdiaNum,
  fromOdiaNum,
  generateWordProblem,
  WordProblemData
} from '../utils/odiaUtils';

// Shared Digital Rough Slate Modal/Drawer for Math Games
const DigitalRoughSlate: React.FC<{ onClose: () => void }> = ({ onClose }) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [drawing, setDrawing] = useState(false);

  const clear = () => {
    const c = canvasRef.current;
    if (!c) return;
    const ctx = c.getContext('2d');
    ctx?.clearRect(0, 0, c.width, c.height);
  };

  return (
    <div className="bg-[#263238] border-3 border-black rounded-3xl p-4 text-white space-y-2 comic-shadow">
      <div className="flex items-center justify-between">
        <span className="text-xs font-black text-[#FFEB3B]">✏️ ରଫ୍ ସ୍ଲେଟ୍ (Digital Rough Slate)</span>
        <div className="flex gap-2">
          <button
            onClick={clear}
            className="px-2.5 py-1 rounded-lg bg-red-600 text-white text-xs font-bold cursor-pointer"
          >
            ସଫା
          </button>
          <button
            onClick={onClose}
            className="px-2.5 py-1 rounded-lg bg-white/20 text-white text-xs font-bold cursor-pointer"
          >
            ବନ୍ଦ ✕
          </button>
        </div>
      </div>
      <canvas
        ref={canvasRef}
        width={500}
        height={200}
        onPointerDown={e => {
          const c = canvasRef.current;
          if (!c) return;
          const rect = c.getBoundingClientRect();
          const ctx = c.getContext('2d');
          if (!ctx) return;
          ctx.strokeStyle = '#FFFFFF';
          ctx.lineWidth = 4;
          ctx.lineCap = 'round';
          ctx.beginPath();
          ctx.moveTo(
            ((e.clientX - rect.left) * c.width) / rect.width,
            ((e.clientY - rect.top) * c.height) / rect.height
          );
          setDrawing(true);
        }}
        onPointerMove={e => {
          if (!drawing) return;
          const c = canvasRef.current;
          if (!c) return;
          const rect = c.getBoundingClientRect();
          const ctx = c.getContext('2d');
          if (!ctx) return;
          ctx.lineTo(
            ((e.clientX - rect.left) * c.width) / rect.width,
            ((e.clientY - rect.top) * c.height) / rect.height
          );
          ctx.stroke();
        }}
        onPointerUp={() => setDrawing(false)}
        onPointerLeave={() => setDrawing(false)}
        className="w-full h-44 rounded-2xl bg-[#1C262B] border border-white/20 touch-none cursor-crosshair"
      />
    </div>
  );
};

// --- 1. MATH QUIZ GAME (ହିସାବ ଖେଳ) ---
export const MathGameScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [opMode, setOpMode] = useState<'ADD' | 'SUB' | 'MUL' | 'DIV'>('ADD');
  const [score, setScore] = useState(0);
  const [showSlate, setShowSlate] = useState(false);
  const [selectedAns, setSelectedAns] = useState<number | null>(null);
  const [seed, setSeed] = useState(0);

  const problem = React.useMemo(() => {
    const r = (min: number, max: number) => Math.floor(Math.random() * (max - min + 1)) + min;
    let a = r(5, 25);
    let b = r(2, 15);
    let sym = '+';
    let ans = a + b;

    if (opMode === 'SUB') {
      a = r(15, 40);
      b = r(3, a - 1);
      sym = '−';
      ans = a - b;
    } else if (opMode === 'MUL') {
      a = r(2, 12);
      b = r(2, 10);
      sym = '×';
      ans = a * b;
    } else if (opMode === 'DIV') {
      b = r(2, 9);
      ans = r(2, 10);
      a = b * ans;
      sym = '÷';
    }

    const opts = new Set<number>([ans]);
    while (opts.size < 4) {
      const candidate = Math.max(1, ans + r(-6, 6));
      opts.add(candidate);
    }
    return {
      a,
      b,
      sym,
      ans,
      options: Array.from(opts).sort((x, y) => x - y)
    };
  }, [opMode, seed]);

  return (
    <div className="min-h-screen bg-[#FFFDF7] pb-20">
      <header className="sticky top-0 z-20 bg-white border-b-2 border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-xl bg-slate-100 border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-xl sm:text-2xl font-black text-slate-900">ହିସାବ ଖେଳ</h1>
          </div>
          <div className="flex items-center gap-2">
            <span className="px-3 py-1.5 rounded-xl bg-[#FFF9C4] border-2 border-black font-black text-sm">
              🏆 ସ୍କୋର: {toOdiaNum(score)}
            </span>
            <button
              onClick={() => setShowSlate(s => !s)}
              className="p-2 rounded-xl bg-[#E3F2FD] border-2 border-black cursor-pointer"
              title="ରଫ୍ ସ୍ଲେଟ୍"
            >
              <Edit3 className="w-5 h-5 text-[#0D47A1]" />
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-4 space-y-4">
        <div className="grid grid-cols-4 gap-2">
          {[
            { id: 'ADD', label: 'ମିଶାଣ (+)' },
            { id: 'SUB', label: 'ଫେଡ଼ାଣ (−)' },
            { id: 'MUL', label: 'ଗୁଣନ (×)' },
            { id: 'DIV', label: 'ହରଣ (÷)' }
          ].map(t => (
            <button
              key={t.id}
              onClick={() => {
                setOpMode(t.id as any);
                setSelectedAns(null);
              }}
              className={`py-2 rounded-2xl border-2 border-black font-black text-xs sm:text-sm cursor-pointer ${
                opMode === t.id ? 'bg-[#1565C0] text-white' : 'bg-white text-slate-800'
              }`}
            >
              {t.label}
            </button>
          ))}
        </div>

        {showSlate && <DigitalRoughSlate onClose={() => setShowSlate(false)} />}

        <div className="bg-white border-3 border-black rounded-3xl p-8 comic-shadow text-center space-y-6">
          <div className="text-5xl sm:text-6xl font-black text-slate-900">
            {toOdiaNum(problem.a)} {problem.sym} {toOdiaNum(problem.b)} = ?
          </div>

          <div className="grid grid-cols-2 gap-3">
            {problem.options.map(opt => {
              const isSelected = selectedAns === opt;
              const isCorrect = opt === problem.ans;
              let cls = 'bg-[#F5F7FA] text-slate-900 hover:bg-blue-50';
              if (selectedAns !== null) {
                if (isCorrect) cls = 'bg-[#C8E6C9] text-[#1B5E20]';
                else if (isSelected) cls = 'bg-[#FFCDD2] text-[#B71C1C]';
              }
              return (
                <button
                  key={opt}
                  disabled={selectedAns !== null}
                  onClick={() => {
                    setSelectedAns(opt);
                    if (opt === problem.ans) setScore(s => s + 10);
                  }}
                  className={`py-4 rounded-2xl border-2 border-black font-black text-3xl comic-shadow-sm cursor-pointer transition ${cls}`}
                >
                  {toOdiaNum(opt)}
                </button>
              );
            })}
          </div>

          {selectedAns !== null && (
            <div className="pt-2 flex items-center justify-between">
              <span className="font-black text-base">
                {selectedAns === problem.ans ? '🎉 ସଠିକ୍ ଉତ୍ତର!' : `❌ ଭୁଲ୍! ସଠିକ୍ ଉତ୍ତର ହେଉଛି ${toOdiaNum(problem.ans)}`}
              </span>
              <button
                onClick={() => {
                  setSelectedAns(null);
                  setSeed(s => s + 1);
                }}
                className="px-5 py-2.5 rounded-2xl bg-[#FFEB3B] border-2 border-black font-black text-sm cursor-pointer comic-shadow-sm"
              >
                ପରବର୍ତ୍ତୀ ପ୍ରଶ୍ନ →
              </button>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

// --- 2. BALLOON POP LOGIC GAME (ବେଲୁନ୍ ଖେଳ - Prompts to Open App or Download App) ---
export const LogicPuzzleScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  return (
    <div className="min-h-screen bg-gradient-to-b from-[#BBDEFB] to-[#E3F2FD] flex items-center justify-center p-4">
      <div
        onClick={onBack}
        className="fixed inset-0 z-50 bg-black/85 backdrop-blur-sm flex items-center justify-center p-4"
      >
        <div
          onClick={e => e.stopPropagation()}
          className="bg-white rounded-[24px] p-7 text-center w-full max-w-[400px] shadow-[0_10px_30px_rgba(0,0,0,0.5)]"
        >
          <h2 className="text-[22px] font-bold text-[#2C241B] mb-2 leading-snug">
            📱 ଆପ୍ ବ୍ୟବହାର କରନ୍ତୁ
          </h2>
          <div className="inline-block px-3.5 py-1 rounded-full bg-[#0288D1]/10 text-[#0288D1] font-bold text-sm mb-3">
            🎈 ବେଲୁନ୍ ଖେଳ (ବୁଦ୍ଧି ଖେଳ)
          </div>
          <p className="text-[#7A7063] text-[16px] leading-relaxed mb-6">
            ବେଲୁନ୍ ଖେଳ ଖେଳିବା ପାଇଁ ଆମର &apos;ଓଡ଼ିଆ ଭାଷା&apos; ଆପ୍ ଖୋଲନ୍ତୁ କିମ୍ବା ଗୁଗଲ୍ ପ୍ଲେ-ଷ୍ଟୋରରୁ ଡାଉନଲୋଡ୍ କରନ୍ତୁ।
          </p>

          <div className="space-y-3">
            <a
              href="https://odiabhasa.pages.dev/open?GAME_LOGIC"
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
              onClick={onBack}
              className="w-full py-3 rounded-full bg-slate-100 text-[#2C241B] text-[15px] font-bold cursor-pointer hover:bg-slate-200 transition"
            >
              ପଛକୁ ଯାଆନ୍ତୁ
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

// --- 3. WORD PROBLEM GAME (ଗାଣିତିକ କାହାଣୀ - 22 Odia Templates) ---
export const WordProblemScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [problem, setProblem] = useState<WordProblemData>(() => generateWordProblem(1));
  const [userAns, setUserAns] = useState('');
  const [checked, setChecked] = useState<boolean | null>(null);
  const [showSolution, setShowSolution] = useState(false);
  const [showSlate, setShowSlate] = useState(false);

  const handleCheck = () => {
    const clean = parseInt(fromOdiaNum(userAns).trim(), 10);
    setChecked(clean === problem.answer);
  };

  const handleNext = () => {
    const nextIdx = (problem.templateIndex % 22) + 1;
    setProblem(generateWordProblem(nextIdx));
    setUserAns('');
    setChecked(null);
    setShowSolution(false);
  };

  return (
    <div className="min-h-screen bg-[#FFFDF7] pb-20">
      <header className="sticky top-0 z-20 bg-white border-b-2 border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-xl bg-slate-100 border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <div>
              <h1 className="text-xl font-black text-slate-900">ଗାଣିତିକ କାହାଣୀ</h1>
              <p className="text-xs font-bold text-slate-600">
                ପ୍ରଶ୍ନ {toOdiaNum(problem.templateIndex)} / ୨୨
              </p>
            </div>
          </div>

          <button
            onClick={() => setShowSlate(s => !s)}
            className="px-3 py-2 rounded-xl bg-[#E3F2FD] border-2 border-black font-black text-xs flex items-center gap-1 cursor-pointer"
          >
            <Edit3 className="w-4 h-4" /> ରଫ୍ ସ୍ଲେଟ୍
          </button>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-4 space-y-4">
        {showSlate && <DigitalRoughSlate onClose={() => setShowSlate(false)} />}

        <div className="bg-white border-3 border-black rounded-3xl p-6 comic-shadow space-y-4">
          <div className="p-4 rounded-2xl bg-[#FFF8E1] border-2 border-black text-lg sm:text-xl font-bold text-slate-900 leading-relaxed">
            {problem.question}
          </div>

          <div className="flex gap-2">
            <input
              type="text"
              value={userAns}
              onChange={e => {
                setUserAns(e.target.value);
                setChecked(null);
              }}
              placeholder="ଉତ୍ତର ଲେଖନ୍ତୁ..."
              className="flex-1 px-4 py-3 rounded-2xl border-2 border-black font-black text-xl"
            />
            <button
              onClick={handleCheck}
              className="px-6 py-3 rounded-2xl bg-[#1565C0] text-white border-2 border-black font-black text-sm cursor-pointer comic-shadow-sm"
            >
              ଯାଞ୍ଚ କରନ୍ତୁ
            </button>
          </div>

          {checked !== null && (
            <div
              className={`p-3.5 rounded-2xl border-2 border-black font-black flex items-center justify-between ${
                checked ? 'bg-[#C8E6C9] text-[#1B5E20]' : 'bg-[#FFCDD2] text-[#B71C1C]'
              }`}
            >
              <span>
                {checked
                  ? '🎉 ସଠିକ୍ ଉତ୍ତର! ବହୁତ ବଢ଼ିଆ!'
                  : `❌ ଭୁଲ୍ ଉତ୍ତର! ସଠିକ୍ ଉତ୍ତର: ${toOdiaNum(problem.answer)}`}
              </span>
            </div>
          )}

          <div className="flex items-center justify-between pt-2">
            <button
              onClick={() => setShowSolution(s => !s)}
              className="px-4 py-2 rounded-xl bg-[#FFF9C4] border-2 border-black font-black text-xs flex items-center gap-1 cursor-pointer"
            >
              <Lightbulb className="w-4 h-4 text-[#F57F17]" /> ସମାଧାନ ଦେଖନ୍ତୁ
            </button>

            <button
              onClick={handleNext}
              className="px-5 py-2.5 rounded-xl bg-[#43A047] text-white border-2 border-black font-black text-xs cursor-pointer"
            >
              ପରବର୍ତ୍ତୀ କାହାଣୀ →
            </button>
          </div>

          {showSolution && (
            <div className="p-4 rounded-2xl bg-[#E8F5E9] border-2 border-black whitespace-pre-line font-bold text-sm text-[#1B5E20]">
              {problem.solution}
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

// --- 4. ODIA SUDOKU GAME (ସୁଡୋକୁ ଖେଳ) ---
const BASE_SUDOKU_SOLUTION = [
  [5, 3, 4, 6, 7, 8, 9, 1, 2],
  [6, 7, 2, 1, 9, 5, 3, 4, 8],
  [1, 9, 8, 3, 4, 2, 5, 6, 7],
  [8, 5, 9, 7, 6, 1, 4, 2, 3],
  [4, 2, 6, 8, 5, 3, 7, 9, 1],
  [7, 1, 3, 9, 2, 4, 8, 5, 6],
  [9, 6, 1, 5, 3, 7, 2, 8, 4],
  [2, 8, 7, 4, 1, 9, 6, 3, 5],
  [3, 4, 5, 2, 8, 6, 1, 7, 9]
];

export const SudokuScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [difficulty, setDifficulty] = useState<'EASY' | 'MEDIUM' | 'HARD'>('EASY');
  const [selectedCell, setSelectedCell] = useState<[number, number] | null>([0, 2]);

  const createBoard = (diff: 'EASY' | 'MEDIUM' | 'HARD') => {
    const dropChance = diff === 'EASY' ? 0.35 : diff === 'MEDIUM' ? 0.5 : 0.62;
    return BASE_SUDOKU_SOLUTION.map((row, r) =>
      row.map((val, c) => (((r * 7 + c * 13) % 10) / 10 < dropChance ? 0 : val))
    );
  };

  const [initialBoard, setInitialBoard] = useState<number[][]>(() => createBoard('EASY'));
  const [board, setBoard] = useState<number[][]>(() => createBoard('EASY'));

  const resetSudoku = (diff: 'EASY' | 'MEDIUM' | 'HARD') => {
    const b = createBoard(diff);
    setInitialBoard(b.map(r => [...r]));
    setBoard(b.map(r => [...r]));
    setSelectedCell(null);
  };

  const handleNumberInput = (n: number) => {
    if (!selectedCell) return;
    const [r, c] = selectedCell;
    if (initialBoard[r][c] !== 0) return;
    setBoard(prev => {
      const next = prev.map(row => [...row]);
      next[r][c] = n;
      return next;
    });
  };

  return (
    <div className="min-h-screen bg-[#FFFDF7] pb-20">
      <header className="sticky top-0 z-20 bg-white border-b-2 border-black px-4 py-3">
        <div className="max-w-xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-xl bg-slate-100 border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-xl font-black text-slate-900">ଓଡ଼ିଆ ସୁଡୋକୁ</h1>
          </div>

          <div className="flex gap-1.5">
            {(['EASY', 'MEDIUM', 'HARD'] as const).map(d => (
              <button
                key={d}
                onClick={() => {
                  setDifficulty(d);
                  resetSudoku(d);
                }}
                className={`px-3 py-1 rounded-xl border-2 border-black font-black text-xs cursor-pointer ${
                  difficulty === d ? 'bg-[#1565C0] text-white' : 'bg-white text-slate-800'
                }`}
              >
                {d === 'EASY' ? 'ସହଜ' : d === 'MEDIUM' ? 'ମଧ୍ୟମ' : 'କଠିନ'}
              </button>
            ))}
          </div>
        </div>
      </header>

      <main className="max-w-md mx-auto p-4 space-y-4">
        {/* 9x9 Grid */}
        <div className="bg-black border-3 border-black rounded-2xl overflow-hidden comic-shadow grid grid-cols-9 gap-[1px]">
          {board.map((row, r) =>
            row.map((val, c) => {
              const isFixed = initialBoard[r][c] !== 0;
              const isSelected = selectedCell?.[0] === r && selectedCell?.[1] === c;
              const isWrong = val !== 0 && val !== BASE_SUDOKU_SOLUTION[r][c];
              const subGridShade = (Math.floor(r / 3) + Math.floor(c / 3)) % 2 === 0;

              let bg = subGridShade ? 'bg-white' : 'bg-[#F5F7FA]';
              if (isSelected) bg = 'bg-[#FFEB3B]';
              else if (isWrong) bg = 'bg-[#FFCDD2]';

              return (
                <button
                  key={`${r}-${c}`}
                  onClick={() => setSelectedCell([r, c])}
                  className={`aspect-square flex items-center justify-center text-lg sm:text-xl font-black cursor-pointer ${bg} ${
                    isFixed ? 'text-slate-900' : isWrong ? 'text-red-700' : 'text-[#1565C0]'
                  }`}
                >
                  {val === 0 ? '' : toOdiaNum(val)}
                </button>
              );
            })
          )}
        </div>

        {/* Odia Keypad 1..9 */}
        <div className="grid grid-cols-5 gap-2">
          {[1, 2, 3, 4, 5, 6, 7, 8, 9].map(n => (
            <button
              key={n}
              onClick={() => handleNumberInput(n)}
              className="py-3 rounded-2xl bg-white border-2 border-black font-black text-xl comic-shadow-sm hover:bg-blue-50 cursor-pointer"
            >
              {toOdiaNum(n)}
            </button>
          ))}
          <button
            onClick={() => handleNumberInput(0)}
            className="py-3 rounded-2xl bg-red-100 border-2 border-black font-black text-xs text-red-800 cursor-pointer"
          >
            ଲିଭାନ୍ତୁ
          </button>
        </div>
      </main>
    </div>
  );
};

// --- 5. FIND THE MISSING NUMBER (ନିଖୋଜ ସଂଖ୍ୟା - FTMN) ---
export const FTMNScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [seed, setSeed] = useState(0);
  const [selectedOpt, setSelectedOpt] = useState<number | null>(null);

  const puzzle = React.useMemo(() => {
    const start = Math.floor(Math.random() * 15) + 2;
    const step = Math.floor(Math.random() * 5) + 2;
    const hideIdx = Math.floor(Math.random() * 5);
    const seq = Array.from({ length: 5 }, (_, i) => start + i * step);
    const ans = seq[hideIdx];
    const opts = new Set<number>([ans, ans + step, Math.max(1, ans - step), ans + 2]);
    return {
      seq,
      hideIdx,
      ans,
      step,
      options: Array.from(opts).sort((a, b) => a - b)
    };
  }, [seed]);

  return (
    <div className="min-h-screen bg-[#FFFDF7] pb-20">
      <header className="sticky top-0 z-20 bg-white border-b-2 border-black px-4 py-3">
        <div className="max-w-2xl mx-auto flex items-center gap-3">
          <button
            onClick={onBack}
            className="w-10 h-10 rounded-xl bg-slate-100 border-2 border-black flex items-center justify-center cursor-pointer"
          >
            <ArrowLeft className="w-5 h-5" />
          </button>
          <h1 className="text-xl font-black text-slate-900">ନିଖୋଜ ସଂଖ୍ୟା ଖୋଜନ୍ତୁ</h1>
        </div>
      </header>

      <main className="max-w-2xl mx-auto p-4 space-y-5">
        <div className="bg-white border-3 border-black rounded-3xl p-6 comic-shadow space-y-6 text-center">
          <p className="text-sm font-bold text-slate-600">
            ସଂଖ୍ୟା ଶୃଙ୍ଖଳାରେ ‘?’ ସ୍ଥାନରେ କେଉଁ ସଂଖ୍ୟା ରହିବ?
          </p>

          <div className="flex flex-wrap items-center justify-center gap-3">
            {puzzle.seq.map((num, idx) => (
              <div
                key={idx}
                className={`w-16 h-16 rounded-2xl border-2 border-black flex items-center justify-center text-2xl font-black ${
                  idx === puzzle.hideIdx
                    ? 'bg-[#FFEB3B] text-black animate-pulse'
                    : 'bg-[#F3E5F5] text-[#4A148C]'
                }`}
              >
                {idx === puzzle.hideIdx
                  ? selectedOpt === puzzle.ans
                    ? toOdiaNum(puzzle.ans)
                    : '?'
                  : toOdiaNum(num)}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-2 gap-3">
            {puzzle.options.map(opt => (
              <button
                key={opt}
                onClick={() => setSelectedOpt(opt)}
                className={`py-4 rounded-2xl border-2 border-black font-black text-2xl comic-shadow-sm cursor-pointer ${
                  selectedOpt === opt
                    ? opt === puzzle.ans
                      ? 'bg-[#C8E6C9] text-[#1B5E20]'
                      : 'bg-[#FFCDD2] text-[#B71C1C]'
                    : 'bg-slate-50 text-slate-900'
                }`}
              >
                {toOdiaNum(opt)}
              </button>
            ))}
          </div>

          {selectedOpt !== null && (
            <div className="flex items-center justify-between pt-2">
              <span className="font-black text-sm">
                {selectedOpt === puzzle.ans
                  ? `🎉 ଠିକ୍! ପ୍ରତି ସଂଖ୍ୟାରେ +${toOdiaNum(puzzle.step)} ବଢ଼ୁଛି ।`
                  : '❌ ପୁଣି ଚେଷ୍ଟା କରନ୍ତୁ!'}
              </span>
              <button
                onClick={() => {
                  setSelectedOpt(null);
                  setSeed(s => s + 1);
                }}
                className="px-5 py-2 rounded-xl bg-[#1565C0] text-white border-2 border-black font-black text-xs cursor-pointer"
              >
                ନୂଆ ପ୍ରଶ୍ନ →
              </button>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

// --- 6. SLIDING PUZZLE GAME (ସଂଖ୍ୟା ଖସଡ଼ା) ---
export const SlidingPuzzleScreen: React.FC<{ onBack: () => void }> = ({ onBack }) => {
  const [size, setSize] = useState<3 | 4>(3);
  const [moves, setMoves] = useState(0);

  const createSolved = (n: number) => {
    const arr = Array.from({ length: n * n - 1 }, (_, i) => i + 1);
    arr.push(0);
    return arr;
  };

  const shuffleTiles = (n: number) => {
    const tiles = createSolved(n);
    let emptyIdx = tiles.length - 1;
    for (let step = 0; step < 60; step++) {
      const r = Math.floor(emptyIdx / n);
      const c = emptyIdx % n;
      const neighbors: number[] = [];
      if (r > 0) neighbors.push((r - 1) * n + c);
      if (r < n - 1) neighbors.push((r + 1) * n + c);
      if (c > 0) neighbors.push(r * n + (c - 1));
      if (c < n - 1) neighbors.push(r * n + (c + 1));
      const pick = neighbors[Math.floor(Math.random() * neighbors.length)];
      [tiles[emptyIdx], tiles[pick]] = [tiles[pick], tiles[emptyIdx]];
      emptyIdx = pick;
    }
    return tiles;
  };

  const [tiles, setTiles] = useState<number[]>(() => shuffleTiles(3));

  const handleTileClick = (idx: number) => {
    const emptyIdx = tiles.indexOf(0);
    const r1 = Math.floor(idx / size);
    const c1 = idx % size;
    const r2 = Math.floor(emptyIdx / size);
    const c2 = emptyIdx % size;
    if (Math.abs(r1 - r2) + Math.abs(c1 - c2) === 1) {
      const next = [...tiles];
      [next[idx], next[emptyIdx]] = [next[emptyIdx], next[idx]];
      setTiles(next);
      setMoves(m => m + 1);
    }
  };

  const isSolved = tiles.every((val, idx) =>
    idx === tiles.length - 1 ? val === 0 : val === idx + 1
  );

  return (
    <div className="min-h-screen bg-[#FFFDF7] pb-20">
      <header className="sticky top-0 z-20 bg-white border-b-2 border-black px-4 py-3">
        <div className="max-w-md mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <button
              onClick={onBack}
              className="w-10 h-10 rounded-xl bg-slate-100 border-2 border-black flex items-center justify-center cursor-pointer"
            >
              <ArrowLeft className="w-5 h-5" />
            </button>
            <h1 className="text-xl font-black text-slate-900">ସଂଖ୍ୟା ଖସଡ଼ା</h1>
          </div>

          <div className="flex gap-1.5">
            {[3, 4].map(n => (
              <button
                key={n}
                onClick={() => {
                  setSize(n as 3 | 4);
                  setTiles(shuffleTiles(n));
                  setMoves(0);
                }}
                className={`px-3 py-1 rounded-xl border-2 border-black font-black text-xs cursor-pointer ${
                  size === n ? 'bg-[#1565C0] text-white' : 'bg-white'
                }`}
              >
                {toOdiaNum(n)}×{toOdiaNum(n)}
              </button>
            ))}
          </div>
        </div>
      </header>

      <main className="max-w-md mx-auto p-4 space-y-4">
        <div className="flex items-center justify-between bg-white border-2 border-black rounded-2xl px-4 py-2.5">
          <span className="font-black text-sm">ଚାଲ୍ ସଂଖ୍ୟା: {toOdiaNum(moves)}</span>
          <button
            onClick={() => {
              setTiles(shuffleTiles(size));
              setMoves(0);
            }}
            className="px-3 py-1 rounded-xl bg-[#FFEB3B] border-2 border-black font-black text-xs cursor-pointer"
          >
            ସଜାଡ଼ନ୍ତୁ 🔄
          </button>
        </div>

        <div
          className={`bg-slate-800 border-3 border-black rounded-3xl p-3 comic-shadow grid ${
            size === 3 ? 'grid-cols-3' : 'grid-cols-4'
          } gap-2.5`}
        >
          {tiles.map((val, idx) => (
            <button
              key={idx}
              disabled={val === 0}
              onClick={() => handleTileClick(idx)}
              className={`aspect-square rounded-2xl font-black text-3xl flex items-center justify-center transition ${
                val === 0
                  ? 'bg-slate-700/50 cursor-default'
                  : 'bg-[#E0F7FA] text-[#006064] border-2 border-black comic-shadow-sm cursor-pointer active:scale-95'
              }`}
            >
              {val === 0 ? '' : toOdiaNum(val)}
            </button>
          ))}
        </div>

        {isSolved && moves > 0 && (
          <div className="p-4 rounded-2xl bg-[#C8E6C9] border-2 border-black text-center font-black text-[#1B5E20]">
            🎉 ଅଭିନନ୍ଦନ! ଆପଣ {toOdiaNum(moves)} ଚାଲ୍ ରେ ପଜଲ୍ ସମାଧାନ କରିଛନ୍ତି!
          </div>
        )}
      </main>
    </div>
  );
};
