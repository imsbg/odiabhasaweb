import React, { useState, useEffect, useMemo } from 'react';
import {
  Lightbulb, Menu, Home, BookOpen, Compass,
  ChevronRight, Edit, PawPrint, Book, GraduationCap,
  Library, Wrench, Calendar, Music
} from 'lucide-react';
import { Screen } from '../data/odiaData';
import { toOdiaNum } from '../utils/odiaUtils';

const BANNER_JSON_URL = "https://odiabhasa.pages.dev/banners/7.json";
const QUIZ_JSON_URL = "https://sbgb.pages.dev/quiz/quiz.json";
const MARQUEE_JSON_URL = "https://odiabhasa.pages.dev/marquee/2.json";
const ALLOWED_HOSTS = ["imsbg.github.io", "play.google.com"];

interface BannerData {
  imageUrl: string;
  caption: string;
  paragraph: string;
  link: string;
  isLink: boolean;
  start: string | null;
  end: string | null;
}

interface QuizMainConfig {
  version: number;
  showQuestions: boolean;
  expirationTime: string | null;
  dailyLinks: Record<number, string>;
}

interface QuizQuestion {
  question: string;
  options: string[];
  answerIndex: number;
}

function parseDdMmYyyyHhMm(dateStr: string): Date | null {
  try {
    // Format: "dd-MM-yyyy HH:mm"
    const [datePart, timePart] = dateStr.trim().split(' ');
    if (!datePart) return null;
    const [dd, mm, yyyy] = datePart.split('-').map(Number);
    const [hh, min] = (timePart || '00:00').split(':').map(Number);
    if (!dd || !mm || !yyyy) return null;
    return new Date(yyyy, mm - 1, dd, hh || 0, min || 0);
  } catch {
    return null;
  }
}

function isBannerCurrentlyValid(startStr: string | null, endStr: string | null): boolean {
  if (!startStr && !endStr) return true;
  const now = new Date();
  if (startStr) {
    const start = parseDdMmYyyyHhMm(startStr);
    if (start && now < start) return false;
  }
  if (endStr) {
    const end = parseDdMmYyyyHhMm(endStr);
    if (end && now > end) return false;
  }
  return true;
}

function isQuizValid(expireStr: string | null | undefined): boolean {
  if (!expireStr || !expireStr.trim()) return true;
  const exp = parseDdMmYyyyHhMm(expireStr);
  if (!exp) return true;
  return new Date() < exp;
}

function isUrlAllowed(url: string): boolean {
  try {
    const u = new URL(url);
    return ALLOWED_HOSTS.some(h => u.hostname.toLowerCase().includes(h));
  } catch {
    return false;
  }
}

interface HomeScreenProps {
  onNavigate: (screen: Screen) => void;
  onOpenDeepLinksModal?: () => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({ onNavigate }) => {
  const [selectedTab, setSelectedTab] = useState<number>(() => {
    const saved = sessionStorage.getItem('home_tab');
    return saved ? parseInt(saved, 10) : 0;
  });

  // Marquee States (Exact match with HomeScreen.kt)
  const [showMarquee, setShowMarquee] = useState<boolean>(() => {
    return localStorage.getItem('marquee_show') === 'true';
  });
  const [marqueeText, setMarqueeText] = useState<string>(() => {
    const dayOfWeek = new Date().getDay() + 1;
    return localStorage.getItem(`marquee_text_${dayOfWeek}`) || '';
  });

  // Banner States (Exact match with HomeScreen.kt)
  const [bannerList, setBannerList] = useState<BannerData[]>([]);
  const [currentBannerIdx, setCurrentBannerIdx] = useState(0);

  // Quiz States (Exact match with HomeScreen.kt)
  const [quizConfig, setQuizConfig] = useState<QuizMainConfig | null>(null);
  const [todayQuestions, setTodayQuestions] = useState<QuizQuestion[]>([]);
  const [showQuizDialog, setShowQuizDialog] = useState(false);
  const [isQuizSolvedToday, setIsQuizSolvedToday] = useState(false);
  const [quizScore, setQuizScore] = useState(0);

  // Parental Gate Dialog for Banner Links
  const [showMathDialog, setShowMathDialog] = useState(false);
  const [selectedLink, setSelectedLink] = useState('');
  const [mathQuestion, setMathQuestion] = useState('');
  const [mathAnswer, setMathAnswer] = useState(0);
  const [userAnswer, setUserAnswer] = useState('');
  const [errorMsg, setErrorMsg] = useState('');

  useEffect(() => {
    sessionStorage.setItem('home_tab', String(selectedTab));
  }, [selectedTab]);

  // Fetch Marquee, Banners, and Daily Quiz from exact Kotlin URLs
  useEffect(() => {
    const todayDayOfWeek = new Date().getDay() + 1; // 1 = Sun ... 7 = Sat

    // 1. Fetch Marquee
    fetch(MARQUEE_JSON_URL)
      .then(r => (r.ok ? r.json() : null))
      .then(obj => {
        if (!obj) return;
        const version = Number(obj.version ?? 1);
        const show = String(obj.show ?? 'no').toLowerCase() === 'yes';
        const quotesObj = obj.quotes || {};
        const dayText = String(quotesObj[String(todayDayOfWeek)] || '');

        setShowMarquee(show);
        setMarqueeText(dayText);
        localStorage.setItem('marquee_show', String(show));
        localStorage.setItem('marquee_version', String(version));
        Object.keys(quotesObj).forEach(k => {
          localStorage.setItem(`marquee_text_${k}`, String(quotesObj[k]));
        });
      })
      .catch(() => {});

    // 2. Fetch Banners
    fetch(BANNER_JSON_URL)
      .then(r => (r.ok ? r.json() : null))
      .then(root => {
        if (!root || !Array.isArray(root.banners)) return;
        const parsed: BannerData[] = root.banners.map((item: any) => ({
          imageUrl: String(item.image || ''),
          caption: String(item.text || ''),
          paragraph: String(item.paragraph || ''),
          link: String(item.link || ''),
          isLink: String(item.promo || 'no').toLowerCase() === 'yes',
          start: item.start ? String(item.start).trim() || null : null,
          end: item.end ? String(item.end).trim() || null : null
        }));
        const valid = parsed.filter(b => isBannerCurrentlyValid(b.start, b.end));
        if (valid.length > 0) {
          setBannerList(valid);
        }
      })
      .catch(() => {});

    // 3. Fetch Quiz Configuration (linked to URL; if disabled, bulb won't show)
    fetch(QUIZ_JSON_URL)
      .then(r => (r.ok ? r.json() : null))
      .then(async obj => {
        if (!obj) return;
        const version = Number(obj.version ?? 1);
        const showQuestions = String(obj.showQuestions ?? 'no').toLowerCase() === 'yes';
        const expirationTime = obj.expirationTime ? String(obj.expirationTime).trim() || null : null;
        const linksObj = obj.dailyLinks || {};
        const dailyLinks: Record<number, string> = {};
        Object.keys(linksObj).forEach(k => {
          dailyLinks[Number(k)] = String(linksObj[k]);
        });

        const cfg: QuizMainConfig = { version, showQuestions, expirationTime, dailyLinks };
        if (cfg.showQuestions && isQuizValid(cfg.expirationTime)) {
          const todayLink = cfg.dailyLinks[todayDayOfWeek];
          if (todayLink && todayLink.trim()) {
            try {
              const qRes = await fetch(todayLink);
              if (qRes.ok) {
                const qJson = await qRes.json();
                const arr = Array.isArray(qJson?.questions) ? qJson.questions : [];
                const questions: QuizQuestion[] = arr.map((qObj: any) => ({
                  question: String(qObj.question || ''),
                  options: Array.isArray(qObj.options) ? qObj.options.map(String) : [],
                  answerIndex: Number(qObj.answerIndex ?? 0)
                }));

                setQuizConfig(cfg);
                setTodayQuestions(questions);

                const todayStr = new Date().toISOString().slice(0, 10);
                const savedDate = localStorage.getItem('quiz_last_solved_date');
                const savedVersion = Number(localStorage.getItem('quiz_last_solved_version') ?? -1);
                if (savedDate === todayStr && savedVersion === cfg.version) {
                  setIsQuizSolvedToday(true);
                  setQuizScore(Number(localStorage.getItem('quiz_last_solved_score') ?? 0));
                } else {
                  setIsQuizSolvedToday(false);
                  setQuizScore(0);
                }
              }
            } catch {
              setQuizConfig(cfg);
            }
          } else {
            setQuizConfig(cfg);
          }
        } else {
          setQuizConfig(cfg);
          setTodayQuestions([]);
        }
      })
      .catch(() => {});
  }, []);

  const carouselData: BannerData[] = useMemo(() => {
    if (bannerList.length > 0) return bannerList;
    return [
      {
        imageUrl: '',
        caption: '',
        paragraph: '',
        link: '',
        isLink: false,
        start: null,
        end: null
      }
    ];
  }, [bannerList]);

  // Auto-scroll carousel matching HomeScreen.kt
  useEffect(() => {
    if (carouselData.length <= 1) return;
    const currentItem = carouselData[currentBannerIdx] || carouselData[0];
    const delayTime = currentItem?.isLink ? 10000 : 6000;
    const timer = setTimeout(() => {
      setCurrentBannerIdx(prev => (prev + 1) % carouselData.length);
    }, delayTime);
    return () => clearTimeout(timer);
  }, [carouselData, currentBannerIdx]);

  const sankhyaNumbers = useMemo(() => {
    return Array.from({ length: 101 }, (_, i) => toOdiaNum(i));
  }, []);

  const generateMathGateQuestion = (link: string) => {
    const n1 = Math.floor(Math.random() * 20) + 1;
    const n2 = Math.floor(Math.random() * 20) + 1;
    const isAdd = Math.random() > 0.5;
    const num1 = Math.max(n1, n2);
    const num2 = Math.min(n1, n2);
    const ans = isAdd ? num1 + num2 : num1 - num2;
    setMathAnswer(ans);
    setMathQuestion(`${toOdiaNum(num1)} ${isAdd ? '+' : '-'} ${toOdiaNum(num2)} = ?`);
    setUserAnswer('');
    setErrorMsg('');
    setSelectedLink(link);
    setShowMathDialog(true);
  };

  const showQuizBulb =
    quizConfig?.showQuestions === true &&
    isQuizValid(quizConfig?.expirationTime) &&
    todayQuestions.length > 0;

  return (
    <div className="min-h-screen flex flex-col bg-[#F8F9FA] text-[#1A1A1A]">
      {/* --- FIXED TOP HEADER --- */}
      <header className="top-header max-w-4xl w-full mx-auto">
        <div className="header-row">
          <h1 className="header-title">ଓଡ଼ିଆ ଭାଷା</h1>

          {showQuizBulb && (
            <button
              onClick={() => setShowQuizDialog(true)}
              aria-label="ଦୈନିକ କୁଇଜ୍"
              className="quiz-bulb"
              style={{
                background: !isQuizSolvedToday ? 'rgba(255, 193, 7, 0.15)' : 'transparent'
              }}
            >
              <Lightbulb
                className={`w-7 h-7 ${
                  !isQuizSolvedToday ? 'pulse-bulb-anim text-[#FFC107] fill-[#FFC107]' : 'text-gray-400'
                }`}
              />
            </button>
          )}
        </div>

        {/* --- MARQUEE TEXT BLOCK --- */}
        {showMarquee && marqueeText.length > 0 && (
          <div className="marquee-container">
            <div className="marquee-text">{marqueeText}</div>
          </div>
        )}
      </header>

      {/* --- FIXED BANNER CAROUSEL --- */}
      <div className="carousel-wrapper max-w-4xl w-full mx-auto">
        <div className="carousel">
          {carouselData.map((item, idx) => {
            if (idx !== currentBannerIdx) return null;
            return (
              <div key={idx} className="banner-card shadow-md">
                {item.imageUrl ? (
                  <img
                    src={item.imageUrl}
                    alt={item.caption || 'ଓଡ଼ିଆ ଭାଷା'}
                    className="banner-img"
                  />
                ) : (
                  <div className="w-full h-full bg-gradient-to-br from-[#6200EE] via-[#7B1FA2] to-[#EC407A] flex items-center justify-between px-8 text-white relative overflow-hidden">
                    <div className="z-10 space-y-1">
                      <span className="inline-block px-3 py-0.5 rounded-full bg-white/20 text-xs font-bold backdrop-blur-xs">
                        ଆମ ଭାଷା, ଆମ ପରିଚୟ
                      </span>
                      <h2 className="text-3xl sm:text-4xl font-black tracking-tight">ଓଡ଼ିଆ ଭାଷା</h2>
                      <p className="text-sm text-white/90 font-medium">
                        ବର୍ଣ୍ଣବୋଧ, ବିଦ୍ୟାଳୟ ବହି, ଗଣିତ ଓ ସାହିତ୍ୟ
                      </p>
                    </div>
                    <img
                      src="/assets/aistudio/ic_launcher-playstore_f1f41a0b.png"
                      alt="ଓଡ଼ିଆ ଭାଷା"
                      className="w-24 h-24 sm:w-28 sm:h-28 rounded-3xl shadow-xl border-2 border-white/30 z-10"
                    />
                  </div>
                )}

                {/* Promo Button */}
                {item.isLink && item.link && (
                  <button
                    onClick={() => {
                      if (isUrlAllowed(item.link)) {
                        if (item.link.startsWith('https://play.google.com/')) {
                          window.open(item.link, '_blank', 'noopener,noreferrer');
                        } else {
                          generateMathGateQuestion(item.link);
                        }
                      }
                    }}
                    className="absolute top-3 right-3 z-20 px-3.5 py-2 rounded-xl bg-black/70 text-white text-xs font-bold cursor-pointer"
                  >
                    ଅଧିକ ଜାଣନ୍ତୁ
                  </button>
                )}

                {/* Caption & Paragraph Overlay */}
                {(item.caption || item.paragraph) && (
                  <div className="banner-overlay">
                    {item.caption && <h3>{item.caption}</h3>}
                    {item.paragraph && <p>{item.paragraph}</p>}
                  </div>
                )}
              </div>
            );
          })}
        </div>

        <div className="carousel-dots">
          {carouselData.map((_, i) => (
            <button
              key={i}
              onClick={() => setCurrentBannerIdx(i)}
              aria-label={`Banner ${i + 1}`}
              className={`dot ${i === currentBannerIdx ? 'active' : ''}`}
            />
          ))}
        </div>
      </div>

      {/* --- MAIN PAGER CONTENT --- */}
      <main className="main-pager max-w-4xl w-full mx-auto">
        {/* TAB 0: ଶିକ୍ଷା (HomeTabContent) */}
        {selectedTab === 0 && (
          <div className="tab-content active">
            <h2 className="section-title">ବିଭାଗ</h2>

            <div className="grid-categories">
              <button
                onClick={() => onNavigate(Screen.SWARA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-swara)' }}>
                  ଅ
                </div>
                <span>ସ୍ୱରବର୍ଣ୍ଣ</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.BYANJANA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-byanjana)' }}>
                  କ
                </div>
                <span>ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.BARNA_PARICHAYA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-barna)', fontSize: '22px' }}>
                  ବର୍ଣ୍ଣ
                </div>
                <span>ବର୍ଣ୍ଣ ପରିଚୟ</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.MATRA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-matra)' }}>
                  ା
                </div>
                <span>ମାତ୍ରା</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.PHALA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-phala)' }}>
                  ୍ୟ
                </div>
                <span>ଫଳା</span>
              </button>

              <button
                onClick={() => onNavigate(Screen.YUKTA)}
                className="category-card bouncing-click"
              >
                <div className="category-icon" style={{ background: 'var(--c-yukta)' }}>
                  ଙ୍କ
                </div>
                <span>ଯୁକ୍ତାକ୍ଷର</span>
              </button>
            </div>

            {/* Sankhya Special Card */}
            <div
              onClick={() => onNavigate(Screen.SANKHYA)}
              className="sankhya-card bouncing-click"
            >
              <h3>ସଂଖ୍ୟା :</h3>
              <div className="sankhya-scroll">
                {sankhyaNumbers.map(n => (
                  <span key={n}>{n}</span>
                ))}
              </div>
              <div
                className="pointer-events-none absolute right-0 top-0 bottom-0 w-24"
                style={{
                  background: 'linear-gradient(to right, transparent, var(--c-sankhya))'
                }}
              />
            </div>

            {/* Ganita Card */}
            <div
              onClick={() => onNavigate(Screen.GANITA)}
              className="ganita-card bouncing-click"
            >
              <h2>କୁନି ଗଣିତ</h2>
              <h3>୨ + ୩ = ?</h3>
            </div>

            {/* Writing Entry Card */}
            <div
              onClick={() => onNavigate(Screen.WRITING)}
              className="wide-card grad-orange bouncing-click"
            >
              <div className="icon-left">
                <Edit className="w-10 h-10 text-white" />
              </div>
              <div className="content">
                <h3>ଅକ୍ଷର ଲେଖି ଶିଖନ୍ତୁ</h3>
                <p>ଅକ୍ଷର ଉପରେ ଲେଖନ୍ତୁ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-white shrink-0" />
            </div>

            {/* Asa Janiba Card */}
            <div
              onClick={() => onNavigate(Screen.ASA_JANIBA)}
              className="wide-card grad-green bouncing-click"
            >
              <div className="icon-left">
                <PawPrint className="w-10 h-10 text-white" />
              </div>
              <div className="content">
                <h3>ଆସ ଜାଣିବା</h3>
                <p>ପଶୁପକ୍ଷୀ, ଫଳ ଓ ପନିପରିବା</p>
              </div>
              <ChevronRight className="w-6 h-6 text-white shrink-0" />
            </div>
          </div>
        )}

        {/* TAB 1: ବହି (BooksTabContent) */}
        {selectedTab === 1 && (
          <div className="tab-content active">
            <h2 className="section-title">ପୁସ୍ତକ ଓ ପାଠାଗାର</h2>

            {/* Madhu Barnabodha Card */}
            <div
              onClick={() => onNavigate(Screen.MADHU_BARNABODHA)}
              className="wide-card grad-purple bouncing-click"
            >
              <div className="icon-left">
                <Book className="w-10 h-10 text-white" />
              </div>
              <div className="content">
                <h3>ଛବିଳ ମଧୁ ବର୍ଣ୍ଣବୋଧ</h3>
                <p>ପଢିବା ପାଇଁ କ୍ଲିକ୍ କରନ୍ତୁ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-white shrink-0" />
            </div>

            {/* School Books Card */}
            <div
              onClick={() => onNavigate(Screen.SCHOOL_BOOKS)}
              className="wide-card grad-blue bouncing-click"
            >
              <div className="icon-left">
                <GraduationCap className="w-11 h-11 text-white" />
              </div>
              <div className="content">
                <h3>ବିଦ୍ୟାଳୟ ବହି</h3>
                <p>ପ୍ରଥମରୁ ଦଶମ ଶ୍ରେଣୀ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-white shrink-0" />
            </div>

            {/* Pathagara (ReadBooksCard) */}
            <div
              onClick={() => onNavigate(Screen.BAHI)}
              className="wide-card card-surface bouncing-click"
            >
              <div className="icon-left">
                <BookOpen className="w-10 h-10 text-[#6200EE]" />
              </div>
              <div className="content">
                <h3>ପାଠାଗାର</h3>
                <p>କାହାଣୀ ଓ କବିତା ପଢନ୍ତୁ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-[#6200EE] shrink-0" />
            </div>

            {/* Puruna Bahi Card */}
            <div
              onClick={() => onNavigate(Screen.PURUNA_BAHI)}
              className="wide-card grad-gold bouncing-click"
            >
              <div className="icon-left">
                <Library className="w-10 h-10 text-white" />
              </div>
              <div className="content">
                <h3>ପୁରୁଣା ବହି</h3>
                <p>ପୁରୁଣା ଶିକ୍ଷାବର୍ଷର ପୁସ୍ତକ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-white shrink-0" />
            </div>
          </div>
        )}

        {/* TAB 2: ଅନ୍ୟାନ୍ୟ (ExploreTabContent) */}
        {selectedTab === 2 && (
          <div className="tab-content active">
            <h2 className="section-title">ଦରକାରୀ ଯନ୍ତ୍ରପାତି</h2>

            <div
              onClick={() => onNavigate(Screen.UTILITY_CATEGORY)}
              className="wide-card card-surface bouncing-click"
            >
              <div className="icon-left">
                <Wrench className="w-10 h-10 text-[#6200EE]" />
              </div>
              <div className="content">
                <h3>ଉପଯୋଗିତା</h3>
                <p>ଏକକ ପରିବର୍ତ୍ତନ ଓ ଅନ୍ୟାନ୍ୟ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-[#6200EE] shrink-0" />
            </div>

            <h2 className="section-title">ଆଉ କିଛି ଉପଯୋଗୀ</h2>

            <div
              onClick={() => onNavigate(Screen.ODIA_CALENDAR)}
              className="wide-card card-surface bouncing-click"
            >
              <div className="icon-left">
                <Calendar className="w-10 h-10 text-[#6200EE]" />
              </div>
              <div className="content">
                <h3>କ୍ୟାଲେଣ୍ଡର</h3>
                <p>ପର୍ବପର୍ବାଣି ଓ ଛୁଟିଦିନ ଦେଖନ୍ତୁ</p>
              </div>
              <ChevronRight className="w-6 h-6 text-[#6200EE] shrink-0" />
            </div>

            <div
              onClick={() => onNavigate(Screen.HARMONIUM)}
              className="wide-card card-surface bouncing-click"
            >
              <div className="icon-left">
                <Music className="w-10 h-10 text-[#6200EE]" />
              </div>
              <div className="content">
                <h3>ହାରମୋନିୟମ</h3>
                <p>ଚାଲ, ହାରମୋନିୟମ ବଜାଇବା!</p>
              </div>
              <ChevronRight className="w-6 h-6 text-[#6200EE] shrink-0" />
            </div>

            <h2 className="section-title mt-2">ଆମ ଇତିହାସ ଓ ଭାଷା</h2>

            <div className="horizontal-list">
              <div
                onClick={() => onNavigate(Screen.HISTORY_ODISHA)}
                className="big-card bouncing-click"
              >
                <div className="img-placeholder" style={{ background: '#FFCC80' }}>
                  <span className="text-6xl select-none">🏛️</span>
                </div>
                <div className="text-area">
                  <h3>ଓଡ଼ିଶାର ଇତିହାସ</h3>
                  <p>ପଢନ୍ତୁ...</p>
                </div>
              </div>

              <div
                onClick={() => onNavigate(Screen.HISTORY_LANG)}
                className="big-card bouncing-click"
              >
                <div className="img-placeholder" style={{ background: '#A5D6A7' }}>
                  <span className="text-6xl font-black text-[#1B5E20] select-none">ଓଡ଼ିଆ</span>
                </div>
                <div className="text-area">
                  <h3>ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ</h3>
                  <p>ପଢନ୍ତୁ...</p>
                </div>
              </div>

              <div
                onClick={() => onNavigate(Screen.LIPYANTARA)}
                className="big-card bouncing-click"
              >
                <div className="img-placeholder" style={{ background: '#90CAF9' }}>
                  <span className="text-5xl font-black text-[#0D47A1] select-none">ଅ → A</span>
                </div>
                <div className="text-area">
                  <h3>ଲିପ୍ୟନ୍ତର</h3>
                  <p>ପଢନ୍ତୁ...</p>
                </div>
              </div>
            </div>

            <div className="footer-text">{"ଆମ ଭାଷା,\nଆମ ପରିଚୟ"}</div>
          </div>
        )}
      </main>

      {/* --- FLOATING BOTTOM NAVIGATION --- */}
      <div className="bottom-nav-container">
        <div className="nav-pill">
          <button
            onClick={() => setSelectedTab(0)}
            className={`nav-item ${selectedTab === 0 ? 'active' : ''}`}
          >
            <Home className="w-6 h-6" />
            <span>ଶିକ୍ଷା</span>
          </button>
          <button
            onClick={() => setSelectedTab(1)}
            className={`nav-item ${selectedTab === 1 ? 'active' : ''}`}
          >
            <BookOpen className="w-6 h-6" />
            <span>ବହି</span>
          </button>
          <button
            onClick={() => setSelectedTab(2)}
            className={`nav-item ${selectedTab === 2 ? 'active' : ''}`}
          >
            <Compass className="w-6 h-6" />
            <span>ଅନ୍ୟାନ୍ୟ</span>
          </button>
        </div>

        <button
          onClick={() => onNavigate(Screen.MENU)}
          aria-label="ଅଧିକ"
          className="menu-btn bouncing-click"
        >
          <Menu className="w-8 h-8" />
        </button>
      </div>

      {/* --- DAILY QUIZ DIALOG (Exact match with DailyQuizDialog in HomeScreen.kt) --- */}
      {showQuizDialog && todayQuestions.length > 0 && (
        <DailyQuizModal
          questions={todayQuestions}
          isInitiallySolved={isQuizSolvedToday}
          initialScore={quizScore}
          onDismiss={() => setShowQuizDialog(false)}
          onQuizFinished={finalScore => {
            const todayStr = new Date().toISOString().slice(0, 10);
            localStorage.setItem('quiz_last_solved_date', todayStr);
            localStorage.setItem('quiz_last_solved_version', String(quizConfig?.version ?? 1));
            localStorage.setItem('quiz_last_solved_score', String(finalScore));
            setIsQuizSolvedToday(true);
            setQuizScore(finalScore);
          }}
        />
      )}

      {/* --- PARENTAL GATE MATH DIALOG --- */}
      {showMathDialog && (
        <div className="fixed inset-0 z-[1100] bg-black/50 flex items-center justify-center p-4">
          <div className="bg-white rounded-[28px] max-w-sm w-full p-6 shadow-2xl space-y-4">
            <h3 className="text-xl font-bold text-[#1A1A1A]">ଅଭିଭାବକଙ୍କ ପାଇଁ ପ୍ରଶ୍ନ</h3>
            <p className="text-sm text-gray-600">
              ଏହି ଲିଙ୍କ୍‌ଟି ଆପଣଙ୍କୁ ଆମ ଆପ୍‌ ବାହାରେ ଏକ ଅନ୍ୟ ୱେବସାଇଟ୍‌କୁ ନେଇଯିବ ।
            </p>
            <div className="text-3xl font-black text-[#6200EE] text-center py-2">
              {mathQuestion}
            </div>
            <input
              type="number"
              value={userAnswer}
              onChange={e => {
                setUserAnswer(e.target.value);
                setErrorMsg('');
              }}
              className="w-full px-4 py-3 rounded-xl border border-gray-300 text-lg font-bold focus:outline-none focus:border-[#6200EE]"
            />
            {errorMsg && <p className="text-xs text-red-600 font-bold">{errorMsg}</p>}
            <div className="flex justify-end gap-3 pt-2">
              <button
                onClick={() => setShowMathDialog(false)}
                className="px-4 py-2 text-sm font-semibold text-gray-600 cursor-pointer"
              >
                ବନ୍ଦ କରନ୍ତୁ
              </button>
              <button
                onClick={() => {
                  if (parseInt(userAnswer, 10) === mathAnswer) {
                    setShowMathDialog(false);
                    window.open(selectedLink, '_blank', 'noopener,noreferrer');
                  } else {
                    setErrorMsg('ଭୁଲ୍ ଉତ୍ତର');
                  }
                }}
                className="px-5 py-2 rounded-xl bg-[#6200EE] text-white text-sm font-bold cursor-pointer"
              >
                ଠିକ୍ ଅଛି
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const DailyQuizModal: React.FC<{
  questions: QuizQuestion[];
  isInitiallySolved: boolean;
  initialScore: number;
  onDismiss: () => void;
  onQuizFinished: (score: number) => void;
}> = ({ questions, isInitiallySolved, initialScore, onDismiss, onQuizFinished }) => {
  const [dialogState, setDialogState] = useState<'QUIZ' | 'RESULT'>(
    isInitiallySolved ? 'RESULT' : 'QUIZ'
  );
  const [currentQIndex, setCurrentQIndex] = useState(0);
  const [score, setScore] = useState(isInitiallySolved ? initialScore : 0);
  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);

  const q = questions[currentQIndex];

  const handleSelectOption = (idx: number) => {
    if (selectedAnswer !== null) return;
    setSelectedAnswer(idx);
    const nextScore = idx === q.answerIndex ? score + 1 : score;
    if (idx === q.answerIndex) setScore(nextScore);

    setTimeout(() => {
      if (currentQIndex < questions.length - 1) {
        setCurrentQIndex(i => i + 1);
        setSelectedAnswer(null);
      } else {
        setDialogState('RESULT');
        onQuizFinished(nextScore);
      }
    }, 1200);
  };

  return (
    <div className="fixed inset-0 z-[1100] bg-black/50 flex items-center justify-center p-4">
      <div className="bg-white rounded-[28px] max-w-md w-full p-6 shadow-2xl flex flex-col items-center text-center">
        <h3 className="text-2xl font-bold text-[#6200EE] mb-4">ଦୈନିକ କୁଇଜ୍</h3>

        {dialogState === 'QUIZ' && q ? (
          <>
            <p className="text-base text-black/60 mb-3">
              ପ୍ରଶ୍ନ {toOdiaNum(currentQIndex + 1)}/{toOdiaNum(questions.length)}
            </p>
            <h4 className="text-xl font-bold text-[#1A1A1A] mb-6">{q.question}</h4>

            <div className="w-full space-y-3">
              {q.options.map((opt, idx) => {
                const isCorrect = idx === q.answerIndex;
                const isSelected = idx === selectedAnswer;
                let bg = 'bg-white text-[#1A1A1A] border border-black/15';
                if (selectedAnswer !== null) {
                  if (isCorrect) bg = 'bg-[#81C784] text-white border-transparent';
                  else if (isSelected) bg = 'bg-[#E57373] text-white border-transparent';
                }
                return (
                  <button
                    key={idx}
                    disabled={selectedAnswer !== null}
                    onClick={() => handleSelectOption(idx)}
                    className={`w-full p-4 rounded-2xl text-lg font-medium transition cursor-pointer ${bg}`}
                  >
                    {opt}
                  </button>
                );
              })}
            </div>
          </>
        ) : (
          <>
            <Lightbulb className="w-18 h-18 text-[#FF9800] my-4" />
            <p className="text-xl text-black/70">ଆପଣଙ୍କ ସ୍କୋର</p>
            <p className="text-4xl font-bold text-[#6200EE] mt-1 mb-6">
              {toOdiaNum(score)}/{toOdiaNum(questions.length)}
            </p>
            <button
              onClick={() => {
                setDialogState('QUIZ');
                setCurrentQIndex(0);
                setScore(0);
                setSelectedAnswer(null);
              }}
              className="w-full h-[50px] rounded-2xl bg-[#6200EE] text-white text-lg font-bold cursor-pointer"
            >
              ପୁଣି ଖେଳନ୍ତୁ
            </button>
            <button
              onClick={onDismiss}
              className="mt-3 py-2 text-base font-semibold text-[#6200EE] cursor-pointer"
            >
              ବନ୍ଦ କରନ୍ତୁ
            </button>
          </>
        )}
      </div>
    </div>
  );
};
