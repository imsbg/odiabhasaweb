import { Screen, characterAudioMap } from '../data/odiaData';

export function toOdiaNum(value: number | string): string {
  const str = String(value);
  let out = '';
  for (let i = 0; i < str.length; i++) {
    const ch = str[i];
    if (ch >= '0' && ch <= '9') {
      out += String.fromCharCode(0x0B66 + (ch.charCodeAt(0) - 48));
    } else {
      out += ch;
    }
  }
  return out;
}

export function fromOdiaNum(value: string): string {
  let out = '';
  for (let i = 0; i < value.length; i++) {
    const code = value.charCodeAt(i);
    if (code >= 0x0B66 && code <= 0x0B6F) {
      out += String.fromCharCode(48 + (code - 0x0B66));
    } else {
      out += value[i];
    }
  }
  return out;
}

export const ODIA_NUMBER_WORDS: string[] = [
  "ଶୂନ", "ଏକ", "ଦୁଇ", "ତିନି", "ଚାରି", "ପାଞ୍ଚ", "ଛଅ", "ସାତ", "ଆଠ", "ନଅ", "ଦଶ",
  "ଏଗାର", "ବାର", "ତେର", "ଚଉଦ", "ପନ୍ଦର", "ଷୋହଳ", "ସତର", "ଅଠର", "ଊଣେଇଶ", "କୋଡ଼ିଏ",
  "ଏକୋଇଶ", "ବାଇଶ", "ତେଇଶ", "ଚବିଶ", "ପଚିଶ", "ଛବିଶ", "ସତେଇଶ", "ଅଠେଇଶ", "ଅଣତିରିଶ", "ତିରିଶ",
  "ଏକତିରିଶ", "ବତିଶ", "ତେତିଶ", "ଚଉତିରିଶ", "ପଇଁତିରିଶ", "ଛତିଶ", "ସଇଁତିରିଶ", "ଅଠତିରିଶ", "ଅଣଚାଳିଶ", "ଚାଳିଶ",
  "ଏକଚାଳିଶ", "ବୟାଳିଶ", "ତେୟାଳିଶ", "ଚଉରାଳିଶ", "ପଞ୍ଚାଳିଶ", "ଛୟାଳିଶ", "ସତଚାଳିଶ", "ଅଠଚାଳିଶ", "ଅଣପଚାଶ", "ପଚାଶ",
  "ଏକାବନ", "ବାବନ", "ତେପନ", "ଚଉବନ", "ପଞ୍ଚାବନ", "ଛପନ", "ସତାବନ", "ଅଠାବନ", "ଅଣଷଠି", "ଷାଠିଏ",
  "ଏକଷଠି", "ବାଷଠି", "ତେଷଠି", "ଚଉଷଠି", "ପଞ୍ଚଷଠି", "ଛଅଷଠି", "ସତଷଠି", "ଅଠଷଠି", "ଅଣସତୁରି", "ସତୁରି",
  "ଏକସ୍ତରି", "ବାସ୍ତରି", "ତେସ୍ତରି", "ଚଉସ୍ତରି", "ପଞ୍ଚସ୍ତରି", "ଛଅସ୍ତରି", "ସତସ୍ତରି", "ଅଠସ୍ତରି", "ଅଣାଅଶୀ", "ଅଶୀ",
  "ଏକାଅଶୀ", "ବୟାଅଶୀ", "ତେୟାଅଶୀ", "ଚଉରାଅଶୀ", "ପଞ୍ଚାଅଶୀ", "ଛୟାଅଶୀ", "ସତାଅଶୀ", "ଅଠାଅଶୀ", "ଅଣାନବେ", "ନବେ",
  "ଏକାନବେ", "ବୟାନବେ", "ତେୟାନବେ", "ଚଉରାନବେ", "ପଞ୍ଚାନବେ", "ଛୟାନବେ", "ସତାନବେ", "ଅଠାନବେ", "ଅଣେଶତ", "ଶହେ"
];

export function convertToOdiaWords(n: number): string {
  if (!Number.isFinite(n) || n < 0) return "";
  const intVal = Math.floor(n);
  if (intVal === 0) return "ଶୂନ";

  function convertLessThanThousand(num: number): string {
    if (num === 0) return "";
    if (num <= 100) return ODIA_NUMBER_WORDS[num] || "";
    const hundreds = Math.floor(num / 100);
    const remainder = num % 100;
    const hundredStr = hundreds === 1 ? "ଶହେ" : `${ODIA_NUMBER_WORDS[hundreds]} ଶହ`;
    return remainder === 0 ? hundredStr : `${hundredStr} ${ODIA_NUMBER_WORDS[remainder]}`;
  }

  let rem = intVal;
  const parts: string[] = [];

  if (rem >= 10000000) {
    const crores = Math.floor(rem / 10000000);
    parts.push(`${convertToOdiaWords(crores)} କୋଟି`);
    rem %= 10000000;
  }
  if (rem >= 100000) {
    const lakhs = Math.floor(rem / 100000);
    parts.push(`${convertLessThanThousand(lakhs)} ଲକ୍ଷ`);
    rem %= 100000;
  }
  if (rem >= 1000) {
    const thousands = Math.floor(rem / 1000);
    parts.push(`${convertLessThanThousand(thousands)} ହଜାର`);
    rem %= 1000;
  }
  if (rem > 0) {
    parts.push(convertLessThanThousand(rem));
  }
  return parts.join(" ").trim();
}

const ENG_UNITS = [
  "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
  "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
];
const ENG_TENS = [
  "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
];

export function convertToEnglishWords(n: number): string {
  if (!Number.isFinite(n) || n < 0) return "";
  const intVal = Math.floor(n);
  if (intVal === 0) return "Zero";

  function convertLessThanThousand(num: number): string {
    if (num === 0) return "";
    if (num < 20) return ENG_UNITS[num];
    if (num < 100) {
      const u = num % 10;
      return `${ENG_TENS[Math.floor(num / 10)]}${u !== 0 ? " " + ENG_UNITS[u] : ""}`;
    }
    const rem = num % 100;
    return `${ENG_UNITS[Math.floor(num / 100)]} Hundred${rem !== 0 ? " " + convertLessThanThousand(rem) : ""}`;
  }

  let rem = intVal;
  const parts: string[] = [];

  if (rem >= 10000000) {
    const crores = Math.floor(rem / 10000000);
    parts.push(`${convertToEnglishWords(crores)} Crore`);
    rem %= 10000000;
  }
  if (rem >= 100000) {
    const lakhs = Math.floor(rem / 100000);
    parts.push(`${convertLessThanThousand(lakhs)} Lakh`);
    rem %= 100000;
  }
  if (rem >= 1000) {
    const thousands = Math.floor(rem / 1000);
    parts.push(`${convertLessThanThousand(thousands)} Thousand`);
    rem %= 1000;
  }
  if (rem > 0) {
    parts.push(convertLessThanThousand(rem));
  }
  return parts.join(" ").trim();
}

export const panikiaTerms = [
  "କେ", "ଦୁଗୁଣେ", "ତିରି", "ଚଉ", "ପଞ୍ଚା", "ଷୋ", "ସତା", "ଅଷ୍ଟା", "ନୂଆଁ", "ଦଶା"
];

export function getOdiaNumberWord(n: number): string {
  if (n >= 0 && n <= 100) {
    if (n === 20) return "କୋଡ଼ିଏ";
    return ODIA_NUMBER_WORDS[n] || toOdiaNum(n);
  }
  return convertToOdiaWords(n) || toOdiaNum(n);
}

export interface PanikiaRow {
  multiplier: number;
  product: number;
  numericText: string;
  scriptText: string;
}

export function getPanikiaTableData(number: number): PanikiaRow[] {
  const numWord = getOdiaNumberWord(number);
  return Array.from({ length: 10 }, (_, idx) => {
    const i = idx + 1;
    const product = number * i;
    const term = panikiaTerms[idx];
    const prodWord = getOdiaNumberWord(product);
    return {
      multiplier: i,
      product,
      numericText: `${toOdiaNum(number)} × ${toOdiaNum(i)} = ${toOdiaNum(product)}`,
      scriptText: `${numWord} ${term} ${prodWord}`
    };
  });
}

// --- Digital Bajara Utilities ---
export interface ShopUnit {
  name: string;
  factor: number;
  category: string;
  baseUnitName: string;
}

export const shopUnits: ShopUnit[] = [
  { name: "କି.ଗ୍ରା (Kg)", factor: 1.0, category: "Weight", baseUnitName: "କି.ଗ୍ରା" },
  { name: "ଗ୍ରାମ୍ (gm)", factor: 0.001, category: "Weight", baseUnitName: "କି.ଗ୍ରା" },
  { name: "କ୍ୱିଣ୍ଟାଲ୍ (Quintal)", factor: 100.0, category: "Weight", baseUnitName: "କି.ଗ୍ରା" },
  { name: "ଲିଟର (Ltr)", factor: 1.0, category: "Volume", baseUnitName: "ଲିଟର" },
  { name: "ମି.ଲି (ml)", factor: 0.001, category: "Volume", baseUnitName: "ଲିଟର" },
  { name: "ଗୋଟା (Pc)", factor: 1.0, category: "Count", baseUnitName: "ଗୋଟା" },
  { name: "ଡଜନ (Dozen)", factor: 12.0, category: "Count", baseUnitName: "ଗୋଟା" },
];

export function formatNumberClean(d: number): string {
  if (Number.isInteger(d)) return String(d);
  return d.toFixed(2).replace(/\.?0+$/, '');
}

export function getCalculationIdea(rateStr: string, qtyStr: string, unit: ShopUnit, totalPrice: number): string {
  const rate = parseFloat(rateStr) || 0;
  const qty = parseFloat(qtyStr) || 0;
  const rOdia = toOdiaNum(formatNumberClean(rate));
  const qOdia = toOdiaNum(formatNumberClean(qty));
  const totalOdia = toOdiaNum(totalPrice.toFixed(2));
  const uName = unit.name.split(' ')[0];

  if (unit.factor === 1.0) {
    return `• ୧ ${unit.baseUnitName} ର ଦାମ୍ = ₹${rOdia}\n• ଆପଣ ନେଉଛନ୍ତି = ${qOdia} ${uName}\n• ମୋଟ ଦାମ୍ = ${rOdia} × ${qOdia} = ₹${totalOdia}`;
  } else if (unit.factor === 0.001) {
    const ratePerSmall = rate / 1000;
    const rSmallOdia = toOdiaNum(formatNumberClean(ratePerSmall));
    return `• ୧ ${unit.baseUnitName} (୧୦୦୦ ${uName}) ର ଦାମ୍ = ₹${rOdia}\n• ୧ ${uName} ର ଦାମ୍ = ${rOdia} ÷ ୧୦୦୦ = ₹${rSmallOdia}\n• ତେଣୁ ${qOdia} ${uName} ର ଦାମ୍ = ${rSmallOdia} × ${qOdia} = ₹${totalOdia}`;
  } else {
    const fOdia = toOdiaNum(formatNumberClean(unit.factor));
    const ratePerBig = rate * unit.factor;
    const rBigOdia = toOdiaNum(formatNumberClean(ratePerBig));
    return `• ୧ ${unit.baseUnitName} ର ଦାମ୍ = ₹${rOdia}\n• ୧ ${uName} (${fOdia} ${unit.baseUnitName}) ର ଦାମ୍ = ${rOdia} × ${fOdia} = ₹${rBigOdia}\n• ତେଣୁ ${qOdia} ${uName} ର ଦାମ୍ = ${rBigOdia} × ${qOdia} = ₹${totalOdia}`;
  }
}

export function getSmartIdea1(baseQty: string, basePrice: string, targetPrice: string, unit: ShopUnit, resQty: number): string {
  const bQ = parseFloat(baseQty) || 0;
  const bP = parseFloat(basePrice) || 0;
  const tP = parseFloat(targetPrice) || 0;
  const uName = unit.name.split(' ')[0];

  const bQOdia = toOdiaNum(formatNumberClean(bQ));
  const bPOdia = toOdiaNum(formatNumberClean(bP));
  const tPOdia = toOdiaNum(formatNumberClean(tP));
  const resOdia = toOdiaNum(formatNumberClean(resQty));

  if (bP === 0) return "ଦୟାକରି ସଠିକ୍ ମୂଲ୍ୟ ଦିଅନ୍ତୁ ।";
  const perRupee = bQ / bP;
  const perRupeeOdia = toOdiaNum(formatNumberClean(perRupee));

  return `• ₹${bPOdia} ରେ ମିଳୁଛି = ${bQOdia} ${uName}\n• ₹୧ ରେ ମିଳିବ = ${bQOdia} ÷ ${bPOdia} = ${perRupeeOdia} ${uName}\n• ତେଣୁ ₹${tPOdia} ରେ ମିଳିବ = ${perRupeeOdia} × ${tPOdia} = ${resOdia} ${uName}`;
}

export function getSmartIdea2(baseQty: string, basePrice: string, targetQty: string, unit: ShopUnit, resPrice: number): string {
  const bQ = parseFloat(baseQty) || 0;
  const bP = parseFloat(basePrice) || 0;
  const tQ = parseFloat(targetQty) || 0;
  const uName = unit.name.split(' ')[0];

  const bQOdia = toOdiaNum(formatNumberClean(bQ));
  const bPOdia = toOdiaNum(formatNumberClean(bP));
  const tQOdia = toOdiaNum(formatNumberClean(tQ));
  const resOdia = toOdiaNum(resPrice.toFixed(2));

  if (bQ === 0) return "ଦୟାକରି ସଠିକ୍ ପରିମାଣ ଦିଅନ୍ତୁ ।";
  const perUnit = bP / bQ;
  const perUnitOdia = toOdiaNum(formatNumberClean(perUnit));

  return `• ${bQOdia} ${uName} ର ଦାମ୍ = ₹${bPOdia}\n• ୧ ${uName} ର ଦାମ୍ = ${bPOdia} ÷ ${bQOdia} = ₹${perUnitOdia}\n• ତେଣୁ ${tQOdia} ${uName} ର ଦାମ୍ = ${perUnitOdia} × ${tQOdia} = ₹${resOdia}`;
}

// --- Word Problem Templates (Exact 22 templates from WordProblemViewModel.kt) ---
export interface WordProblemData {
  question: string;
  answer: number;
  solution: string;
  templateIndex: number;
}

export function generateWordProblem(forcedTemplateIndex?: number): WordProblemData {
  const idx = forcedTemplateIndex !== undefined
    ? forcedTemplateIndex
    : Math.floor(Math.random() * 22) + 1;

  const rand = (min: number, max: number) => Math.floor(Math.random() * (max - min + 1)) + min;

  switch (idx) {
    case 1: {
      const a = rand(15, 50);
      const b = rand(10, 40);
      const ans = a + b;
      return {
        templateIndex: 1,
        question: `ଗୋଟିଏ ବଗିଚାରେ ${toOdiaNum(a)} ଟି ଆମ୍ବ ଗଛ ଓ ${toOdiaNum(b)} ଟି ପଣସ ଗଛ ଅଛି । ବଗିଚାରେ ମୋଟ କେତୋଟି ଗଛ ଅଛି?`,
        answer: ans,
        solution: `ଆମ୍ବ ଗଛ = ${toOdiaNum(a)} ଟି\nପଣସ ଗଛ = ${toOdiaNum(b)} ଟି\nମୋଟ ଗଛ = ${toOdiaNum(a)} + ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 2: {
      const a = rand(10, 30);
      const b = rand(5, 25);
      const ans = a + b;
      return {
        templateIndex: 2,
        question: `ରମେଶ ପାଖରେ ${toOdiaNum(a)} ଟଙ୍କା ଥିଲା । ତା' ବାପା ତାକୁ ଆଉ ${toOdiaNum(b)} ଟଙ୍କା ଦେଲେ । ଏବେ ରମେଶ ପାଖରେ ମୋଟ କେତେ ଟଙ୍କା ହେଲା?`,
        answer: ans,
        solution: `ରମେଶ ପାଖରେ ଥିଲା = ${toOdiaNum(a)} ଟଙ୍କା\nବାପା ଦେଲେ = ${toOdiaNum(b)} ଟଙ୍କା\nମୋଟ ଟଙ୍କା = ${toOdiaNum(a)} + ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 3: {
      const a = rand(30, 60);
      const b = rand(10, 25);
      const ans = a - b;
      return {
        templateIndex: 3,
        question: `ଗୋଟିଏ ଗଛରେ ${toOdiaNum(a)} ଟି ଚଢ଼େଇ ବସିଥିଲେ । ସେଥିରୁ ${toOdiaNum(b)} ଟି ଚଢ଼େଇ ଉଡ଼ିଗଲେ । ଗଛରେ ଆଉ କେତୋଟି ଚଢ଼େଇ ରହିଲେ?`,
        answer: ans,
        solution: `ମୋଟ ଚଢ଼େଇ = ${toOdiaNum(a)} ଟି\nଉଡ଼ିଗଲେ = ${toOdiaNum(b)} ଟି\nବାକି ରହିଲେ = ${toOdiaNum(a)} - ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 4: {
      const a = rand(50, 100);
      const b = rand(15, 45);
      const ans = a - b;
      return {
        templateIndex: 4,
        question: `ସୀତା ପାଖରେ ${toOdiaNum(a)} ଟଙ୍କା ଥିଲା । ସେ ଦୋକାନରୁ ${toOdiaNum(b)} ଟଙ୍କାର ଖାତା କିଣିଲା । ତା' ପାଖରେ ଆଉ କେତେ ଟଙ୍କା ବଳିଲା?`,
        answer: ans,
        solution: `ସୀତା ପାଖରେ ଥିଲା = ${toOdiaNum(a)} ଟଙ୍କା\nଖାତା କିଣିଲା = ${toOdiaNum(b)} ଟଙ୍କା\nବଳିଲା = ${toOdiaNum(a)} - ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 5: {
      const a = rand(5, 15);
      const b = rand(3, 12);
      const ans = a * b;
      return {
        templateIndex: 5,
        question: `ଗୋଟିଏ କଲମର ଦାମ୍ ${toOdiaNum(a)} ଟଙ୍କା ହେଲେ, ${toOdiaNum(b)} ଟି କଲମର ଦାମ୍ କେତେ ହେବ?`,
        answer: ans,
        solution: `୧ ଟି କଲମର ଦାମ୍ = ${toOdiaNum(a)} ଟଙ୍କା\n${toOdiaNum(b)} ଟି କଲମର ଦାମ୍ = ${toOdiaNum(a)} × ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 6: {
      const a = rand(4, 10);
      const b = rand(4, 10);
      const ans = a * b;
      return {
        templateIndex: 6,
        question: `ଗୋଟିଏ ବାକ୍ସରେ ${toOdiaNum(a)} ଟି ବହି ଅଛି । ଏପରି ${toOdiaNum(b)} ଟି ବାକ୍ସରେ ମୋଟ କେତୋଟି ବହି ରହିବ?`,
        answer: ans,
        solution: `୧ ଟି ବାକ୍ସରେ ବହି = ${toOdiaNum(a)} ଟି\n${toOdiaNum(b)} ଟି ବାକ୍ସରେ ବହି = ${toOdiaNum(a)} × ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 7: {
      const b = rand(2, 9);
      const ans = rand(3, 10);
      const a = b * ans;
      return {
        templateIndex: 7,
        question: `${toOdiaNum(a)} ଟି ଚକୋଲେଟ୍ କୁ ${toOdiaNum(b)} ଜଣ ପିଲାଙ୍କ ମଧ୍ୟରେ ସମାନ ଭାବରେ ବାଣ୍ଟିଲେ ଜଣେ ପିଲା କେତୋଟି ଚକୋଲେଟ୍ ପାଇବ?`,
        answer: ans,
        solution: `ମୋଟ ଚକୋଲେଟ୍ = ${toOdiaNum(a)} ଟି\nମୋଟ ପିଲା = ${toOdiaNum(b)} ଜଣ\nଜଣେ ପାଇବ = ${toOdiaNum(a)} ÷ ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 8: {
      const b = rand(3, 8);
      const ans = rand(5, 15);
      const a = b * ans;
      return {
        templateIndex: 8,
        question: `${toOdiaNum(b)} ଟି ସପୁରୀର ମୂଲ୍ୟ ${toOdiaNum(a)} ଟଙ୍କା ହେଲେ, ୧ ଟି ସପୁରୀର ମୂଲ୍ୟ କେତେ ଟଙ୍କା ହେବ?`,
        answer: ans,
        solution: `${toOdiaNum(b)} ଟି ସପୁରୀର ମୂଲ୍ୟ = ${toOdiaNum(a)} ଟଙ୍କା\n୧ ଟି ସପୁରୀର ମୂଲ୍ୟ = ${toOdiaNum(a)} ÷ ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 9: {
      const a = rand(10, 25);
      const b = rand(10, 20);
      const c = rand(5, 15);
      const ans = a + b - c;
      return {
        templateIndex: 9,
        question: `ଗୋଟିଏ ବସ୍ ରେ ${toOdiaNum(a)} ଜଣ ଯାତ୍ରୀ ଥିଲେ । ବାଟରେ ଆଉ ${toOdiaNum(b)} ଜଣ ଚଢ଼ିଲେ ଏବଂ ${toOdiaNum(c)} ଜଣ ଓହ୍ଲାଇ ପଡ଼ିଲେ । ଏବେ ବସ୍ ରେ କେତେ ଜଣ ଯାତ୍ରୀ ଅଛନ୍ତି?`,
        answer: ans,
        solution: `ପ୍ରଥମେ ଥିଲେ = ${toOdiaNum(a)} ଜଣ\nଚଢ଼ିଲେ = ${toOdiaNum(b)} ଜଣ (ମୋଟ = ${toOdiaNum(a)} + ${toOdiaNum(b)} = ${toOdiaNum(a + b)})\nଓହ୍ଲାଇଲେ = ${toOdiaNum(c)} ଜଣ\nଏବେ ଅଛନ୍ତି = ${toOdiaNum(a + b)} - ${toOdiaNum(c)} = ${toOdiaNum(ans)} ଜଣ`
      };
    }
    case 10: {
      const a = rand(100, 200);
      const b = rand(20, 40);
      const c = rand(15, 35);
      const ans = a - (b + c);
      return {
        templateIndex: 10,
        question: `ହରି ପାଖରେ ${toOdiaNum(a)} ଟଙ୍କା ଥିଲା । ସେ ବଜାରରୁ ${toOdiaNum(b)} ଟଙ୍କାର ଆଳୁ ଓ ${toOdiaNum(c)} ଟଙ୍କାର ପିଆଜ କିଣିଲା । ତା' ପାଖରେ ଆଉ କେତେ ଟଙ୍କା ରହିଲା?`,
        answer: ans,
        solution: `ମୋଟ ଟଙ୍କା = ${toOdiaNum(a)} ଟଙ୍କା\nମୋଟ ଖର୍ଚ୍ଚ = ${toOdiaNum(b)} + ${toOdiaNum(c)} = ${toOdiaNum(b + c)} ଟଙ୍କା\nବଳିଲା = ${toOdiaNum(a)} - ${toOdiaNum(b + c)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 11: {
      const a = rand(20, 45);
      const b = rand(15, 40);
      const ans = a + b;
      return {
        templateIndex: 11,
        question: `ଗୋଟିଏ ଶ୍ରେଣୀରେ ${toOdiaNum(a)} ଜଣ ପୁଅ ଓ ${toOdiaNum(b)} ଜଣ ଝିଅ ପଢ଼ନ୍ତି । ସେହି ଶ୍ରେଣୀରେ ମୋଟ କେତେ ଜଣ ପିଲା ପଢ଼ନ୍ତି?`,
        answer: ans,
        solution: `ପୁଅ ସଂଖ୍ୟା = ${toOdiaNum(a)} ଜଣ\nଝିଅ ସଂଖ୍ୟା = ${toOdiaNum(b)} ଜଣ\nମୋଟ ପିଲା = ${toOdiaNum(a)} + ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଜଣ`
      };
    }
    case 12: {
      const a = rand(60, 120);
      const b = rand(20, 50);
      const ans = a - b;
      return {
        templateIndex: 12,
        question: `ଗୋଟିଏ ଗଳ୍ପ ବହିରେ ${toOdiaNum(a)} ଟି ପୃଷ୍ଠା ଅଛି । ମଧୁ ସେଥିରୁ ${toOdiaNum(b)} ଟି ପୃଷ୍ଠା ପଢ଼ିସାରିଛି । ଆଉ କେତୋଟି ପୃଷ୍ଠା ପଢ଼ିବାକୁ ବାକି ଅଛି?`,
        answer: ans,
        solution: `ମୋଟ ପୃଷ୍ଠା = ${toOdiaNum(a)} ଟି\nପଢ଼ା ସରିଛି = ${toOdiaNum(b)} ଟି\nବାକି ଅଛି = ${toOdiaNum(a)} - ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 13: {
      const a = rand(3, 9);
      const ans = a * 7;
      return {
        templateIndex: 13,
        question: `ଗୋପାଳ ପ୍ରତିଦିନ ${toOdiaNum(a)} ଗ୍ଲାସ୍ କ୍ଷୀର ପିଏ । ସେ ଗୋଟିଏ ସପ୍ତାହରେ (୭ ଦିନରେ) ମୋଟ କେତେ ଗ୍ଲାସ୍ କ୍ଷୀର ପିଇବ?`,
        answer: ans,
        solution: `୧ ଦିନରେ ପିଏ = ${toOdiaNum(a)} ଗ୍ଲାସ୍\n୭ ଦିନରେ ପିଇବ = ${toOdiaNum(a)} × ୭ = ${toOdiaNum(ans)} ଗ୍ଲାସ୍`
      };
    }
    case 14: {
      const a = rand(5, 15);
      const ans = a * 4;
      return {
        templateIndex: 14,
        question: `ଗୋଟିଏ ଗାଡ଼ିର ୪ ଟି ଚକ ଅଛି । ତେବେ ଏପରି ${toOdiaNum(a)} ଟି ଗାଡ଼ିର ମୋଟ କେତୋଟି ଚକ ଥିବ?`,
        answer: ans,
        solution: `୧ ଟି ଗାଡ଼ିର ଚକ = ୪ ଟି\n${toOdiaNum(a)} ଟି ଗାଡ଼ିର ଚକ = ${toOdiaNum(a)} × ୪ = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 15: {
      const b = rand(4, 9);
      const ans = rand(4, 12);
      const a = b * ans;
      return {
        templateIndex: 15,
        question: `${toOdiaNum(a)} ଟି ଲଡ଼ୁକୁ ଗୋଟିଏ ଥାଳିରେ ${toOdiaNum(b)} ଟି ଲେଖାଏଁ ରଖିଲେ, ମୋଟ କେତୋଟି ଥାଳି ଦରକାର ହେବ?`,
        answer: ans,
        solution: `ମୋଟ ଲଡ଼ୁ = ${toOdiaNum(a)} ଟି\n୧ ଥାଳିରେ ରୁହେ = ${toOdiaNum(b)} ଟି\nମୋଟ ଥାଳି ଦରକାର = ${toOdiaNum(a)} ÷ ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 16: {
      const a = rand(2, 6);
      const p1 = rand(10, 20);
      const b = rand(2, 5);
      const p2 = rand(5, 10);
      const ans = (a * p1) + (b * p2);
      return {
        templateIndex: 16,
        question: `ରାଜୁ ଦୋକାନରୁ ${toOdiaNum(a)} ଟି ଖାତା (ଗୋଟାକୁ ${toOdiaNum(p1)} ଟଙ୍କା) ଏବଂ ${toOdiaNum(b)} ଟି ପେନସିଲ୍ (ଗୋଟାକୁ ${toOdiaNum(p2)} ଟଙ୍କା) କିଣିଲା । ସେ ଦୋକାନୀକୁ ମୋଟ କେତେ ଟଙ୍କା ଦେବ?`,
        answer: ans,
        solution: `ଖାତା ଦାମ୍ = ${toOdiaNum(a)} × ${toOdiaNum(p1)} = ${toOdiaNum(a * p1)} ଟଙ୍କା\nପେନସିଲ୍ ଦାମ୍ = ${toOdiaNum(b)} × ${toOdiaNum(p2)} = ${toOdiaNum(b * p2)} ଟଙ୍କା\nମୋଟ ଦେବ = ${toOdiaNum(a * p1)} + ${toOdiaNum(b * p2)} = ${toOdiaNum(ans)} ଟଙ୍କା`
      };
    }
    case 17: {
      const a = rand(3, 8);
      const b = rand(10, 20);
      const total = a * b;
      const c = rand(5, 15);
      const ans = total - c;
      return {
        templateIndex: 17,
        question: `ବାପା ବଜାରରୁ ${toOdiaNum(a)} ପ୍ୟାକେଟ୍ ବିସ୍କୁଟ୍ ଆଣିଲେ । ପ୍ରତି ପ୍ୟାକେଟ୍ ରେ ${toOdiaNum(b)} ଟି ବିସ୍କୁଟ୍ ଥିଲା । ପିଲାମାନେ ସେଥିରୁ ${toOdiaNum(c)} ଟି ବିସ୍କୁଟ୍ ଖାଇଦେଲେ । ଆଉ କେତୋଟି ବିସ୍କୁଟ୍ ରହିଲା?`,
        answer: ans,
        solution: `ମୋଟ ବିସ୍କୁଟ୍ = ${toOdiaNum(a)} × ${toOdiaNum(b)} = ${toOdiaNum(total)} ଟି\nପିଲା ଖାଇଲେ = ${toOdiaNum(c)} ଟି\nବାକି ରହିଲା = ${toOdiaNum(total)} - ${toOdiaNum(c)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 18: {
      const a = rand(15, 45);
      const b = rand(12, 35);
      const ans = a + b;
      return {
        templateIndex: 18,
        question: `ଗୋଟିଏ କ୍ରିକେଟ୍ ମ୍ୟାଚ୍ ରେ ରୋହିତ ${toOdiaNum(a)} ରନ୍ ଓ ବିରାଟ ${toOdiaNum(b)} ରନ୍ କଲେ । ଦୁହେଁ ମିଶି ମୋଟ କେତେ ରନ୍ କଲେ?`,
        answer: ans,
        solution: `ରୋହିତ କଲେ = ${toOdiaNum(a)} ରନ୍\nବିରାଟ କଲେ = ${toOdiaNum(b)} ରନ୍\nମୋଟ ରନ୍ = ${toOdiaNum(a)} + ${toOdiaNum(b)} = ${toOdiaNum(ans)} ରନ୍`
      };
    }
    case 19: {
      const a = rand(40, 80);
      const b = rand(10, 30);
      const ans = a - b;
      return {
        templateIndex: 19,
        question: `ଗୋଟିଏ ଝୁଡ଼ିରେ ${toOdiaNum(a)} ଟି କମଳା ଥିଲା । ସେଥିରୁ ${toOdiaNum(b)} ଟି କମଳା ପଚିଗଲା । ଝୁଡ଼ିରେ ଆଉ କେତୋଟି ଭଲ କମଳା ଅଛି?`,
        answer: ans,
        solution: `ମୋଟ କମଳା = ${toOdiaNum(a)} ଟି\nପଚିଗଲା = ${toOdiaNum(b)} ଟି\nଭଲ କମଳା = ${toOdiaNum(a)} - ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 20: {
      const a = rand(4, 12);
      const b = rand(5, 10);
      const ans = a * b;
      return {
        templateIndex: 20,
        question: `ଗୋଟିଏ ଧାଡ଼ିରେ ${toOdiaNum(a)} ଟି ଗଛ ଅଛି । ଏପରି ${toOdiaNum(b)} ଟି ଧାଡ଼ିରେ ମୋଟ କେତୋଟି ଗଛ ଅଛି?`,
        answer: ans,
        solution: `୧ ଧାଡ଼ିରେ ଗଛ = ${toOdiaNum(a)} ଟି\n${toOdiaNum(b)} ଟି ଧାଡ଼ିରେ ଗଛ = ${toOdiaNum(a)} × ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    case 21: {
      const b = rand(3, 8);
      const ans = rand(4, 12);
      const a = b * ans;
      return {
        templateIndex: 21,
        question: `ଗୋଟିଏ ବେଞ୍ଚରେ ${toOdiaNum(b)} ଜଣ ପିଲା ବସିପାରିବେ । ତେବେ ${toOdiaNum(a)} ଜଣ ପିଲା ବସିବା ପାଇଁ କେତୋଟି ବେଞ୍ଚ ଦରକାର ହେବ?`,
        answer: ans,
        solution: `ମୋଟ ପିଲା = ${toOdiaNum(a)} ଜଣ\n୧ ବେଞ୍ଚରେ ବସନ୍ତି = ${toOdiaNum(b)} ଜଣ\nବେଞ୍ଚ ଦରକାର = ${toOdiaNum(a)} ÷ ${toOdiaNum(b)} = ${toOdiaNum(ans)} ଟି`
      };
    }
    default: {
      const a = rand(2, 5);
      const ans = a * 12;
      return {
        templateIndex: 22,
        question: `୧ ଡଜନ କଦଳୀ ମାନେ ୧୨ ଟି କଦଳୀ । ତେବେ ${toOdiaNum(a)} ଡଜନ କଦଳୀରେ ମୋଟ କେତୋଟି କଦଳୀ ରହିବ?`,
        answer: ans,
        solution: `୧ ଡଜନ = ୧୨ ଟି କଦଳୀ\n${toOdiaNum(a)} ଡଜନ = ୧୨ × ${toOdiaNum(a)} = ${toOdiaNum(ans)} ଟି କଦଳୀ`
      };
    }
  }
}

// --- Odia URL Slug Mapping & Deep Link Parsing ---
export const SCREEN_TO_ODIA_SLUG: Record<Screen, string> = {
  [Screen.SPLASH]: '',
  [Screen.HOME]: '',
  [Screen.SWARA]: 'ସ୍ୱରବର୍ଣ୍ଣ',
  [Screen.BYANJANA]: 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ',
  [Screen.SANKHYA]: 'ଓଡ଼ିଆ-ସଂଖ୍ୟା',
  [Screen.YUKTA]: 'ଯୁକ୍ତାକ୍ଷର',
  [Screen.MATRA]: 'ମାତ୍ରା',
  [Screen.PHALA]: 'ଫଳା',
  [Screen.BARNA_PARICHAYA]: 'ବର୍ଣ୍ଣ-ପରିଚୟ',
  [Screen.DETAIL]: 'ଅକ୍ଷର-ପରିଚୟ',
  [Screen.WRITING]: 'ଲିଖନ-ଅଭ୍ୟାସ',
  [Screen.MADHU_BARNABODHA]: 'ମଧୁ-ବର୍ଣ୍ଣବୋଧ',
  [Screen.SCHOOL_BOOKS]: 'ବିଦ୍ୟାଳୟ-ବହି',
  [Screen.CLASS_1]: 'ପ୍ରଥମ-ଶ୍ରେଣୀ',
  [Screen.CLASS_2]: 'ଦ୍ୱିତୀୟ-ଶ୍ରେଣୀ',
  [Screen.CLASS_3]: 'ତୃତୀୟ-ଶ୍ରେଣୀ',
  [Screen.CLASS_4]: 'ଚତୁର୍ଥ-ଶ୍ରେଣୀ',
  [Screen.CLASS_5]: 'ପଞ୍ଚମ-ଶ୍ରେଣୀ',
  [Screen.CLASS_6]: 'ଷଷ୍ଠ-ଶ୍ରେଣୀ',
  [Screen.CLASS_7]: 'ସପ୍ତମ-ଶ୍ରେଣୀ',
  [Screen.CLASS_8]: 'ଅଷ୍ଟମ-ଶ୍ରେଣୀ',
  [Screen.CLASS_9]: 'ନବମ-ଶ୍ରେଣୀ',
  [Screen.CLASS_10]: 'ଦଶମ-ଶ୍ରେଣୀ',
  [Screen.BAHI]: 'ପାଠାଗାର',
  [Screen.BOOK_READER]: 'ବହି-ପଠନ',
  [Screen.PURUNA_BAHI]: 'ପୁରୁଣା-ବହି',
  [Screen.GANITA]: 'କୁନି-ଗଣିତ',
  [Screen.GAMES_CATEGORY]: 'ଗଣିତ-ଖେଳ',
  [Screen.LEARNING_CATEGORY]: 'ଗଣିତ-ଶିକ୍ଷା',
  [Screen.UTILITY_CATEGORY]: 'ଉପଯୋଗିତା',
  [Screen.LEARNING_PANIKIA]: 'ପଣିକିଆ',
  [Screen.LEARNING_ORDINALS]: 'କ୍ରମିକ-ସଂଖ୍ୟା',
  [Screen.LEARNING_COUNTING]: 'ଗଣନା-ପ୍ରଣାଳୀ',
  [Screen.UTILITY_NUM_TO_TEXT]: 'ସଂଖ୍ୟାରୁ-ଶବ୍ଦ',
  [Screen.UTILITY_CONVERTER]: 'ଏକକ-ପରିବର୍ତ୍ତନ',
  [Screen.UTILITY_DIGITAL_BAJARA]: 'ଡିଜିଟାଲ୍-ବଜାର',
  [Screen.UTILITY_CALCULATOR]: 'ଓଡ଼ିଆ-କାଲକୁଲେଟର',
  [Screen.GAME_MATH_QUIZ]: 'ଗଣିତ-କୁଇଜ୍',
  [Screen.GAME_LOGIC]: 'ବେଲୁନ୍-ଖେଳ',
  [Screen.GAME_WORD_PROBLEM]: 'ଗାଣିତିକ-ପ୍ରଶ୍ନ',
  [Screen.GAME_SUDOKU]: 'ସୁଡୋକୁ',
  [Screen.GAME_FTMN]: 'ସଂଖ୍ୟା-ଖୋଜ',
  [Screen.GAME_SLIDING_PUZZLE]: 'ସଂଖ୍ୟା-ଖସଡ଼ା',
  [Screen.ODIA_CALENDAR]: 'ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର',
  [Screen.HARMONIUM]: 'ହାରମୋନିୟମ୍',
  [Screen.HISTORY_ODISHA]: 'ଓଡ଼ିଶାର-ଇତିହାସ',
  [Screen.HISTORY_LANG]: 'ଓଡ଼ିଆ-ଭାଷାର-ଇତିହାସ',
  [Screen.LIPYANTARA]: 'ଲିପ୍ୟନ୍ତର',
  [Screen.ASA_JANIBA]: 'ଆସ-ଜାଣିବା',
  [Screen.MENU]: 'ମେନୁ',
  [Screen.ABOUT]: 'ଆମ-ବିଷୟରେ'
};

export function getOdiaUrlPath(screen: Screen): string {
  const slug = SCREEN_TO_ODIA_SLUG[screen];
  if (!slug) return '/';
  return `/${slug}/`;
}

export function normalizeOdiaSlug(str: string): string {
  return str
    .normalize('NFD')
    .replace(/\u0B5C/g, '\u0B21\u0B3C')
    .replace(/\u0B5D/g, '\u0B22\u0B3C')
    .replace(/^\/+|\/+$/g, '')
    .trim();
}

export function parseDeepLinkFromSearch(
  search: string
): { screen: Screen; bahiId?: string; isValidSearch: boolean } {
  const clean = search.replace(/^\?/, '').trim();
  if (!clean) return { screen: Screen.HOME, isValidSearch: false };

  const params = new URLSearchParams(clean);
  const bahiId = params.get('Bahi') || params.get('bahi');
  if (bahiId) {
    return { screen: Screen.BAHI, bahiId, isValidSearch: true };
  }

  const openParam = params.get('open') || params.get('screen');
  const rawCandidate = (openParam || clean.split('&')[0].split('=')[0]).trim();
  const candidate = rawCandidate.toUpperCase();

  if (candidate in Screen) {
    return { screen: Screen[candidate as keyof typeof Screen], isValidSearch: true };
  }

  // Also check if query param is an Odia slug (e.g. ?ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର)
  let decodedQuery = rawCandidate;
  try {
    decodedQuery = decodeURIComponent(rawCandidate);
  } catch {
    decodedQuery = rawCandidate;
  }
  const normalizedQuery = normalizeOdiaSlug(decodedQuery);
  for (const [screenKey, slug] of Object.entries(SCREEN_TO_ODIA_SLUG)) {
    if (slug && normalizeOdiaSlug(slug) === normalizedQuery) {
      return { screen: screenKey as Screen, isValidSearch: true };
    }
  }

  return { screen: Screen.HOME, isValidSearch: false };
}

export function parseRouteFromLocation(
  pathname: string,
  search: string
): { screen: Screen; bahiId?: string; isNotFound?: boolean } {
  // Check query string first if present
  if (search && search.replace(/^\?/, '').trim() !== '') {
    const fromSearch = parseDeepLinkFromSearch(search);
    if (fromSearch.isValidSearch) {
      return {
        screen: fromSearch.screen,
        bahiId: fromSearch.bahiId,
        isNotFound: false
      };
    }
  }

  let decodedPath = pathname;
  try {
    decodedPath = decodeURIComponent(pathname);
  } catch {
    decodedPath = pathname;
  }

  const cleanSlug = decodedPath.replace(/^\/+|\/+$/g, '').trim();
  if (!cleanSlug || cleanSlug.toLowerCase() === 'index.html' || cleanSlug.toLowerCase() === 'open') {
    return { screen: Screen.HOME, isNotFound: false };
  }

  const normalizedInput = normalizeOdiaSlug(cleanSlug);

  // Match against Odia slugs (with canonical Unicode normalization)
  for (const [screenKey, slug] of Object.entries(SCREEN_TO_ODIA_SLUG)) {
    if (slug && normalizeOdiaSlug(slug) === normalizedInput) {
      return { screen: screenKey as Screen, isNotFound: false };
    }
  }

  // Match against Screen enum keys case-insensitively
  const upper = cleanSlug.replace(/-/g, '_').toUpperCase();
  if (upper in Screen) {
    return { screen: Screen[upper as keyof typeof Screen], isNotFound: false };
  }

  // Any other path is a 404 / Not Found route -> redirect to HOME
  return { screen: Screen.HOME, isNotFound: true };
}

// --- Audio Playback Helper ---
let currentAudio: HTMLAudioElement | null = null;

export function playOdiaAudio(charOrText: string, enabled = true): void {
  if (!enabled || typeof window === 'undefined') return;

  try {
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
    }

    const mappedUrl = characterAudioMap[charOrText.trim()];
    if (mappedUrl) {
      currentAudio = new Audio(mappedUrl);
      currentAudio.play().catch(() => {
        speakFallback(charOrText);
      });
      return;
    }

    speakFallback(charOrText);
  } catch {
    // Ignore audio errors on restricted browsers
  }
}

function speakFallback(text: string): void {
  if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
    try {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.lang = 'or-IN';
      utterance.rate = 0.9;
      window.speechSynthesis.speak(utterance);
    } catch {
      // Ignore speech synthesis errors
    }
  }
}

// --- App Download Footer Popup 24-Hour Cooldown Helper ---
export const APP_POPUP_DISMISS_KEY = 'odia_app_popup_dismissed_at';
export const ONE_DAY_MS = 24 * 60 * 60 * 1000;

export function shouldShowAppPopup(dismissedAtRaw: string | null, now = Date.now()): boolean {
  if (!dismissedAtRaw) return true;
  const dismissedAt = Number(dismissedAtRaw);
  if (!Number.isFinite(dismissedAt) || dismissedAt <= 0) return true;
  return now - dismissedAt >= ONE_DAY_MS;
}


