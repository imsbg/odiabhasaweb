import {
  swarabarnaList,
  byanjanabarnaList,
  barnaParichayaList,
  matraList,
  phalaList,
  yuktakshyaraList,
  sankhyaList,
  getCharDetails,
  characterAudioMap
} from '../data/odiaData';
import {
  toOdiaNum,
  fromOdiaNum,
  convertToOdiaWords,
  convertToEnglishWords
} from './odiaUtils';

export interface BarnabodhaItemPayload {
  symbol: string;
  words: string[];
  sentences: string[];
  audioUrl: string | null;
}

export function buildCharacterItem(symbol: string): BarnabodhaItemPayload {
  const details = getCharDetails(symbol);
  const audioUrl = characterAudioMap[symbol] || null;
  return {
    symbol,
    words: details.words,
    sentences: details.sentences,
    audioUrl
  };
}

export function getSwarabarnaApiData() {
  return {
    status: 'success',
    section: 'ସ୍ୱରବର୍ଣ୍ଣ',
    titleOdia: 'ସ୍ୱରବର୍ଣ୍ଣ',
    key: 'swarabarna',
    symbol: 'ଅ',
    total: swarabarnaList.length,
    count: swarabarnaList.length,
    items: swarabarnaList.map(buildCharacterItem)
  };
}

export function getByanjanabarnaApiData() {
  return {
    status: 'success',
    section: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ',
    titleOdia: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ',
    key: 'byanjanabarna',
    symbol: 'କ',
    total: byanjanabarnaList.length,
    count: byanjanabarnaList.length,
    items: byanjanabarnaList.map(buildCharacterItem)
  };
}

export function getBarnaParichayaApiData() {
  return {
    status: 'success',
    section: 'ବର୍ଣ୍ଣ ପରିଚୟ',
    titleOdia: 'ବର୍ଣ୍ଣ ପରିଚୟ',
    key: 'barna_parichaya',
    symbol: 'ବର୍ଣ୍ଣ',
    total: barnaParichayaList.length,
    count: barnaParichayaList.length,
    items: barnaParichayaList.map(buildCharacterItem)
  };
}

export function getMatraApiData() {
  return {
    status: 'success',
    section: 'ମାତ୍ରା',
    titleOdia: 'ମାତ୍ରା',
    key: 'matra',
    symbol: 'ା',
    total: matraList.length,
    count: matraList.length,
    items: matraList.map(buildCharacterItem)
  };
}

export function getPhalaApiData() {
  return {
    status: 'success',
    section: 'ଫଳା',
    titleOdia: 'ଫଳା',
    key: 'phala',
    symbol: '୍ୟ',
    total: phalaList.length,
    count: phalaList.length,
    items: phalaList.map(buildCharacterItem)
  };
}

export function getYuktaksharaApiData() {
  return {
    status: 'success',
    section: 'ଯୁକ୍ତାକ୍ଷର',
    titleOdia: 'ଯୁକ୍ତାକ୍ଷର',
    key: 'yuktakshara',
    symbol: 'ଙ୍କ',
    total: yuktakshyaraList.length,
    count: yuktakshyaraList.length,
    items: yuktakshyaraList.map(buildCharacterItem)
  };
}

export function getSankhyaApiData() {
  return {
    status: 'success',
    section: 'ସଂଖ୍ୟା',
    titleOdia: 'ସଂଖ୍ୟା : ୦',
    key: 'sankhya',
    symbol: '୦',
    total: sankhyaList.length,
    count: sankhyaList.length,
    items: sankhyaList.map(sym => {
      const numVal = parseInt(fromOdiaNum(sym), 10);
      return {
        ...buildCharacterItem(sym),
        numericValue: Number.isNaN(numVal) ? null : numVal,
        odiaWord: Number.isNaN(numVal) ? '' : convertToOdiaWords(numVal),
        englishWord: Number.isNaN(numVal) ? '' : convertToEnglishWords(numVal)
      };
    })
  };
}

export function getNumberToWordApiData(rawInput: string | number) {
  const str = String(rawInput ?? '').trim();
  const cleanDigits = fromOdiaNum(str).replace(/\D/g, '');
  if (!cleanDigits) {
    return {
      status: 'error',
      message: 'Please provide a valid number via ?num=125 or /api/v1/number-to-word/125',
      example: '/api/v1/number-to-word?num=125'
    };
  }
  const clipped = cleanDigits.slice(0, 10);
  const numVal = parseInt(clipped, 10);
  const odiaDigits = toOdiaNum(clipped);
  const odiaWords = convertToOdiaWords(numVal);
  const englishWords = convertToEnglishWords(numVal);

  return {
    status: 'success',
    input: str,
    number: numVal,
    odiaDigits,
    odiaWord: odiaWords,
    englishWord: englishWords,
    odiaWords,
    englishWords,
    chequeFormat: {
      amountOdia: `₹ ${odiaDigits}/-`,
      amountEnglish: `₹ ${clipped}/-`,
      wordsOdia: `${odiaWords} ଟଙ୍କା ମାତ୍ର`,
      wordsEnglish: `${englishWords} Rupees Only`
    }
  };
}

export function getAllHomeBarnabodhaApiData(baseUrl = '') {
  const sections = [
    { id: 'swarabarna', title: 'ସ୍ୱରବର୍ଣ୍ଣ', symbol: 'ଅ', count: swarabarnaList.length, endpoint: `${baseUrl}/api/v1/swarabarna` },
    { id: 'byanjanabarna', title: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ', symbol: 'କ', count: byanjanabarnaList.length, endpoint: `${baseUrl}/api/v1/byanjanabarna` },
    { id: 'barna_parichaya', title: 'ବର୍ଣ୍ଣ ପରିଚୟ', symbol: 'ବର୍ଣ୍ଣ', count: barnaParichayaList.length, endpoint: `${baseUrl}/api/v1/barna-parichaya` },
    { id: 'matra', title: 'ମାତ୍ରା', symbol: 'ା', count: matraList.length, endpoint: `${baseUrl}/api/v1/matra` },
    { id: 'phala', title: 'ଫଳା', symbol: '୍ୟ', count: phalaList.length, endpoint: `${baseUrl}/api/v1/phala` },
    { id: 'yuktakshara', title: 'ଯୁକ୍ତାକ୍ଷର', symbol: 'ଙ୍କ', count: yuktakshyaraList.length, endpoint: `${baseUrl}/api/v1/yuktakshara` },
    { id: 'sankhya', title: 'ସଂଖ୍ୟା : ୦', symbol: '୦', count: sankhyaList.length, endpoint: `${baseUrl}/api/v1/sankhya` }
  ];

  return {
    status: 'success',
    name: 'ଓଡ଼ିଆ ଭାଷା Open API v1',
    version: '1.0.0',
    authRequired: false,
    endpoints: {
      index: `${baseUrl}/api/v1/`,
      allBarnabodha: `${baseUrl}/api/v1/barnabodha`,
      swarabarna: `${baseUrl}/api/v1/swarabarna`,
      byanjanabarna: `${baseUrl}/api/v1/byanjanabarna`,
      barnaParichaya: `${baseUrl}/api/v1/barna-parichaya`,
      matra: `${baseUrl}/api/v1/matra`,
      phala: `${baseUrl}/api/v1/phala`,
      yuktakshara: `${baseUrl}/api/v1/yuktakshara`,
      sankhya: `${baseUrl}/api/v1/sankhya`,
      numberToWord: `${baseUrl}/api/v1/number-to-word?num=125`
    },
    sections,
    homeBarnabodhaCards: sections,
    data: {
      swarabarna: getSwarabarnaApiData(),
      byanjanabarna: getByanjanabarnaApiData(),
      barnaParichaya: getBarnaParichayaApiData(),
      matra: getMatraApiData(),
      phala: getPhalaApiData(),
      yuktakshara: getYuktaksharaApiData(),
      sankhya: getSankhyaApiData()
    }
  };
}

function normalizeApiToken(str: string): string {
  return str
    .normalize('NFD')
    .replace(/\u0B5C/g, '\u0B21\u0B3C')
    .replace(/\u0B5D/g, '\u0B22\u0B3C')
    .trim()
    .toLowerCase();
}

export function isOpenApiPath(pathname: string): boolean {
  let decoded = pathname || '';
  try {
    decoded = decodeURIComponent(decoded);
  } catch {
    // keep raw
  }
  const clean = decoded.trim();
  return clean === '/api' || clean.startsWith('/api/');
}

export function resolveOpenApiV1Request(
  pathname: string,
  search = '',
  baseUrl = ''
): { status: number; data: unknown } {
  let decodedPath = pathname || '';
  try {
    decodedPath = decodeURIComponent(decodedPath);
  } catch {
    // keep raw
  }

  // Strip leading /api/v1 or /api, trailing slashes, and optional .json suffix
  const subPathRaw = decodedPath
    .replace(/^\/api(?:\/v1)?\/?/i, '')
    .replace(/\.json$/i, '')
    .replace(/^\/+|\/+$/g, '')
    .trim();

  const cleanSearch = (search || '').replace(/^\?/, '').trim();
  const params = new URLSearchParams(cleanSearch);

  const matchSectionByType = (rawType: string) => {
    const t = normalizeApiToken(rawType);
    if (t === 'swara' || t === 'swarabarna' || t === normalizeApiToken('ସ୍ୱରବର୍ଣ୍ଣ') || t === normalizeApiToken('ଅ')) {
      return getSwarabarnaApiData();
    }
    if (t === 'byanjana' || t === 'byanjanabarna' || t === normalizeApiToken('ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ') || t === normalizeApiToken('କ')) {
      return getByanjanabarnaApiData();
    }
    if (
      t === 'barna_parichaya' ||
      t === 'barna-parichaya' ||
      t === 'barnaparichaya' ||
      t === normalizeApiToken('ବର୍ଣ୍ଣ ପରିଚୟ') ||
      t === normalizeApiToken('ବର୍ଣ୍ଣ-ପରିଚୟ') ||
      t === normalizeApiToken('ବର୍ଣ୍ଣ')
    ) {
      return getBarnaParichayaApiData();
    }
    if (t === 'matra' || t === normalizeApiToken('ମାତ୍ରା') || t === normalizeApiToken('ା')) {
      return getMatraApiData();
    }
    if (t === 'phala' || t === normalizeApiToken('ଫଳା') || t === normalizeApiToken('୍ୟ')) {
      return getPhalaApiData();
    }
    if (t === 'yukta' || t === 'yuktakshara' || t === normalizeApiToken('ଯୁକ୍ତାକ୍ଷର') || t === normalizeApiToken('ଙ୍କ')) {
      return getYuktaksharaApiData();
    }
    if (
      t === 'sankhya' ||
      t === normalizeApiToken('ସଂଖ୍ୟା') ||
      t === normalizeApiToken('ଓଡ଼ିଆ-ସଂଖ୍ୟା') ||
      t === normalizeApiToken('୦')
    ) {
      return getSankhyaApiData();
    }
    return null;
  };

  // 1. Root /api/v1 or /api/v1/barnabodha
  if (!subPathRaw || subPathRaw.toLowerCase() === 'barnabodha') {
    const numQuery = params.get('num') ?? params.get('number') ?? params.get('n');
    if (numQuery !== null && subPathRaw.toLowerCase() !== 'barnabodha') {
      return { status: 200, data: getNumberToWordApiData(numQuery) };
    }

    const typeQuery = params.get('type') ?? params.get('section') ?? '';
    if (typeQuery) {
      const matched = matchSectionByType(typeQuery);
      if (matched) {
        return { status: 200, data: matched };
      }
    }

    return { status: 200, data: getAllHomeBarnabodhaApiData(baseUrl) };
  }

  // 2. Check if subPath matches one of the 7 Home Barnabodha sections directly
  const directSection = matchSectionByType(subPathRaw);
  if (directSection) {
    return { status: 200, data: directSection };
  }

  // 3. Number to word: /api/v1/number-to-word?num=125 or /api/v1/number-to-word/125
  const normSub = normalizeApiToken(subPathRaw);
  if (
    normSub === 'number-to-word' ||
    normSub === 'number_to_word' ||
    normSub === 'numbertoword' ||
    normSub === normalizeApiToken('ସଂଖ୍ୟାରୁ-ଶବ୍ଦ') ||
    normSub === normalizeApiToken('ସଂଖ୍ୟାରୁ-ଅକ୍ଷର')
  ) {
    let numVal =
      params.get('num') ??
      params.get('number') ??
      params.get('n') ??
      params.get('q') ??
      params.get('val');
    if (numVal === null && cleanSearch && !cleanSearch.includes('=')) {
      numVal = decodeURIComponent(cleanSearch);
    }
    return { status: 200, data: getNumberToWordApiData(numVal ?? '0') };
  }

  if (
    normSub.startsWith('number-to-word/') ||
    normSub.startsWith('number_to_word/') ||
    normSub.startsWith('numbertoword/')
  ) {
    const parts = subPathRaw.split('/');
    const numSegment = parts.slice(1).join('/');
    return { status: 200, data: getNumberToWordApiData(numSegment || '0') };
  }

  // 4. Single character lookup: /api/v1/char/:symbol or /api/v1/char?symbol=ଅ
  if (normSub === 'char' || normSub.startsWith('char/')) {
    const sym = normSub.startsWith('char/')
      ? subPathRaw.slice(subPathRaw.indexOf('/') + 1)
      : params.get('symbol') ?? params.get('char') ?? 'ଅ';
    return {
      status: 200,
      data: {
        status: 'success',
        ...buildCharacterItem(sym)
      }
    };
  }

  // 5. Fallback if ?num= is present on any /api/v1 path
  const fallbackNum = params.get('num') ?? params.get('number') ?? params.get('n');
  if (fallbackNum !== null) {
    return { status: 200, data: getNumberToWordApiData(fallbackNum) };
  }

  return { status: 200, data: getAllHomeBarnabodhaApiData(baseUrl) };
}

