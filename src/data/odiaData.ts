export interface ContentData {
  wordString: string;
  sentences: string[];
}

export interface CharDetail {
  words: string[];
  sentences: string[];
}

export enum Screen {
  SPLASH = 'SPLASH',
  HOME = 'HOME',
  SWARA = 'SWARA',
  BYANJANA = 'BYANJANA',
  SANKHYA = 'SANKHYA',
  YUKTA = 'YUKTA',
  MATRA = 'MATRA',
  PHALA = 'PHALA',
  BARNA_PARICHAYA = 'BARNA_PARICHAYA',
  DETAIL = 'DETAIL',
  MENU = 'MENU',
  ABOUT = 'ABOUT',
  HISTORY_ODISHA = 'HISTORY_ODISHA',
  HISTORY_LANG = 'HISTORY_LANG',
  LIPYANTARA = 'LIPYANTARA',
  BAHI = 'BAHI',
  BOOK_READER = 'BOOK_READER',
  ODIA_CALENDAR = 'ODIA_CALENDAR',
  MADHU_BARNABODHA = 'MADHU_BARNABODHA',
  WRITING = 'WRITING',
  SCHOOL_BOOKS = 'SCHOOL_BOOKS',
  SCHOOL_BOOK_READER = 'SCHOOL_BOOK_READER',
  CLASS_1 = 'CLASS_1',
  CLASS_2 = 'CLASS_2',
  CLASS_3 = 'CLASS_3',
  CLASS_4 = 'CLASS_4',
  CLASS_5 = 'CLASS_5',
  CLASS_6 = 'CLASS_6',
  CLASS_7 = 'CLASS_7',
  CLASS_8 = 'CLASS_8',
  CLASS_9 = 'CLASS_9',
  CLASS_10 = 'CLASS_10',
  PURUNA_BAHI = 'PURUNA_BAHI',
  ASA_JANIBA = 'ASA_JANIBA',
  HARMONIUM = 'HARMONIUM',
  GANITA = 'GANITA',
  GAMES_CATEGORY = 'GAMES_CATEGORY',
  LEARNING_CATEGORY = 'LEARNING_CATEGORY',
  UTILITY_CATEGORY = 'UTILITY_CATEGORY',
  GAME_MATH_QUIZ = 'GAME_MATH_QUIZ',
  GAME_LOGIC = 'GAME_LOGIC',
  GAME_WORD_PROBLEM = 'GAME_WORD_PROBLEM',
  GAME_SUDOKU = 'GAME_SUDOKU',
  GAME_FTMN = 'GAME_FTMN',
  GAME_SLIDING_PUZZLE = 'GAME_SLIDING_PUZZLE',
  LEARNING_PANIKIA = 'LEARNING_PANIKIA',
  LEARNING_ORDINALS = 'LEARNING_ORDINALS',
  LEARNING_COUNTING = 'LEARNING_COUNTING',
  UTILITY_CALCULATOR = 'UTILITY_CALCULATOR',
  UTILITY_CONVERTER = 'UTILITY_CONVERTER',
  UTILITY_NUM_TO_TEXT = 'UTILITY_NUM_TO_TEXT',
  UTILITY_DIGITAL_BAJARA = 'UTILITY_DIGITAL_BAJARA'
}

// --- ବ୍ୟବହାର ହେଉନଥିବା ---
export const deprecatedList: string[] = [
  "ୠ", "ଌ", "ୡ", "ଵ", "ୖ", "ୗ", "ୄ", "ୢ", "ୣ"
];

export const swarabarnaList: string[] = [
  "ଅ", "ଆ", "ଇ", "ଈ", "ଉ", "ଊ", "ଋ", "ୠ", "ଏ", "ଐ", "ଓ", "ଔ", "ଌ", "ୡ"
];

export const matraList: string[] = [
  "ା", "ି", "ୀ", "ୁ", "ୂ", "ୃ", "େ", "ୈ", "ୋ", "ୌ", "ୖ", "ୗ", "୍", "଼", "ୄ", "ୢ", "ୣ"
];

export const byanjanabarnaList: string[] = [
  "କ", "ଖ", "ଗ", "ଘ", "ଙ", "ଚ", "ଛ", "ଜ", "ଝ", "ଞ",
  "ଟ", "ଠ", "ଡ", "ଢ", "ଣ", "ତ", "ଥ", "ଦ", "ଧ", "ନ",
  "ପ", "ଫ", "ବ", "ଭ", "ମ", "ଯ", "ର", "ଳ", "ୱ", "ଶ",
  "ଷ", "ସ", "ହ", "କ୍ଷ", "ୟ", "ଲ", "ଡ଼", "ଢ଼", "ଵ"
];

export const sankhyaList: string[] = [
  // 0 to 10
  "୦", "୧", "୨", "୩", "୪", "୫", "୬", "୭", "୮", "୯", "୧୦",
  // 11 to 20
  "୧୧", "୧୨", "୧୩", "୧୪", "୧୫", "୧୬", "୧୭", "୧୮", "୧୯", "୨୦",
  // 21 to 30
  "୨୧", "୨୨", "୨୩", "୨୪", "୨୫", "୨୬", "୨୭", "୨୮", "୨୯", "୩୦",
  // 31 to 40
  "୩୧", "୩୨", "୩୩", "୩୪", "୩୫", "୩୬", "୩୭", "୩୮", "୩୯", "୪୦",
  // 41 to 50
  "୪୧", "୪୨", "୪୩", "୪୪", "୪୫", "୪୬", "୪୭", "୪୮", "୪୯", "୫୦",
  // 51 to 60
  "୫୧", "୫୨", "୫୩", "୫୪", "୫୫", "୫୬", "୫୭", "୫୮", "୫୯", "୬୦",
  // 61 to 70
  "୬୧", "୬୨", "୬୩", "୬୪", "୬୫", "୬୬", "୬୭", "୬୮", "୬୯", "୭୦",
  // 71 to 80
  "୭୧", "୭୨", "୭୩", "୭୪", "୭୫", "୭୬", "୭୭", "୭୮", "୭୯", "୮୦",
  // 81 to 90
  "୮୧", "୮୨", "୮୩", "୮୪", "୮୫", "୮୬", "୮୭", "୮୮", "୮୯", "୯୦",
  // 91 to 100
  "୯୧", "୯୨", "୯୩", "୯୪", "୯୫", "୯୬", "୯୭", "୯୮", "୯୯", "୧୦୦",
  "୧୫୦", "୨୦୦", "୨୫୦", "୩୦୦", "୩୫୦", "୪୦୦", "୫୦୦", "୧୦୦୦", "୧୧୦୦",
  "୧୫୦୦", "୨୦୦୦", "୨୫୦୦", "୩୦୦୦", "୩୫୦୦", "୪୦୦୦"
];

export const yuktakshyaraList: string[] = [
  "ଙ୍କ", "ଙ୍ଖ", "ଙ୍ଗ", "ଙ୍ଘ", "ଞ୍ଚ", "ଞ୍ଛ", "ଞ୍ଜ", "ଞ୍ଝ", "ଣ୍ଟ", "ଣ୍ଠ", "ଣ୍ଡ", "ଣ୍ଢ",
  "ନ୍ତ", "ନ୍ଥ", "ନ୍ଦ", "ନ୍ଧ", "ମ୍ପ", "ମ୍ଫ", "ମ୍ବ", "ମ୍ଭ", "କ୍କ", "କ୍ତ", "ତ୍କ", "ଚ୍ଚ", "ଚ୍ଛ",
  "ଜ୍ଜ", "ଜ୍ଞ", "ଟ୍ଟ", "ଡ୍ଗ", "ଡ୍ଡ", "ତ୍ତ", "ତ୍ଥ", "ଦ୍ୱ", "ଗ୍ଧ", "ଦ୍ଘ", "ଦ୍ଦ",
  "ଦ୍ଧ", "ବ୍ଦ", "ବ୍ବ", "ଦ୍ଭ", "ପ୍ପ", "ପ୍ତ", "ତ୍ମ", "ଳ୍କ", "ଳ୍ପ",
  "ଶ୍ଚ", "ଶ୍ନ", "ଷ୍କ", "ଷ୍ଟ", "ଷ୍ଠ", "ଷ୍ଣ", "ଷ୍ମ", "ସ୍କ", "ସ୍ତ", "ସ୍ଥ", "ସ୍ନ", "ସ୍ପ",
  "ସ୍ଫ", "ସ୍ତ୍ର", "ତ୍ସ", "କ୍ଷ୍ଣ", "କ୍ଷ୍ମ", "କ୍ଷ୍ୟ", "ଙ୍\u200Cକ୍ତ", "ଙ୍କ୍ଷ", "ଜ୍ଜ୍ଵ", "ତ୍ତ୍ବ", "ତ୍ତ୍ର", "ତ୍ମ୍ୟ", "ଣ୍ଡ୍ର",
  "ତ୍ସ୍ନ", "ତ୍ସ୍ୟ", "ନ୍ତ୍ୟ", "ନ୍ତ୍ର", "ନ୍ତ୍ଵ", "ନ୍ଦ୍ର", "ନ୍ଦ୍ବ", "ନ୍ଧ୍ର", "ନ୍ଧ୍ୟ", "ନ୍ନ୍ୟ", "ମ୍ପ୍ର", "ମ୍ଭ୍ର",
  "ର୍ଚ୍ଚ", "ର୍ଚ୍ଛ", "ର୍ଣ୍ଣ", "ର୍ତ୍ତ", "ର୍ଦ୍ଧ", "ର୍ଦ୍ଦ", "ର୍ତ୍ସ", "ର୍ଦ୍ର", "ର୍ଯ୍ୟ", "ର୍ଶ୍ଵ", "ଷ୍କ୍ର", "ଷ୍ଟ୍ର", "ଷ୍ପ୍ର",
  "ସ୍ଥ୍ୟ", "ନ୍ତ୍ର୍ୟ", "ର୍ତ୍ତ୍ୟ", "ର୍ତ୍ତ୍ର", "ର୍ଦ୍ଧ୍ୱ"
];

export const barnaParichayaList: string[] = [
  "ଇ", "ଈ", "ଉ", "ଊ", "ଶ", "ଷ", "ସ", "ଂ", "ଃ", "ଁ", "୍", ",", "।", "?", "<", "ଽ", "୰"
];

export const phalaList: string[] = [
  "୍ୟ", "୍ର", "୍ୱ", "୍ମ", "୍ନ", "୍ଳ", "୍ଲ"
];

// Exact 47 audio files mapped to /assets/odiabhasa/<filename>.mp3
export const characterAudioMap: Record<string, string> = {
  "ଅ": "/assets/odiabhasa/ଅ.mp3",
  "ଆ": "/assets/odiabhasa/ଆ.mp3",
  "ଇ": "/assets/odiabhasa/ଇ.mp3",
  "ଈ": "/assets/odiabhasa/ଈ.mp3",
  "ଉ": "/assets/odiabhasa/ଉ.mp3",
  "ଊ": "/assets/odiabhasa/ଊ.mp3",
  "ଋ": "/assets/odiabhasa/ଋ.mp3",
  "ଏ": "/assets/odiabhasa/ଏ.mp3",
  "ଐ": "/assets/odiabhasa/ଐ.mp3",
  "ଓ": "/assets/odiabhasa/ଓ.mp3",
  "ଔ": "/assets/odiabhasa/ଔ.mp3",
  "କ": "/assets/odiabhasa/କ.mp3",
  "ଖ": "/assets/odiabhasa/ଖ.mp3",
  "ଗ": "/assets/odiabhasa/ଗ.mp3",
  "ଘ": "/assets/odiabhasa/ଘ.mp3",
  "ଙ": "/assets/odiabhasa/ଙ.mp3",
  "ଚ": "/assets/odiabhasa/ଚ.mp3",
  "ଛ": "/assets/odiabhasa/ଛ.mp3",
  "ଜ": "/assets/odiabhasa/ଜ.mp3",
  "ଝ": "/assets/odiabhasa/ଝ.mp3",
  "ଞ": "/assets/odiabhasa/ଞ.mp3",
  "ଟ": "/assets/odiabhasa/ଟ.mp3",
  "ଠ": "/assets/odiabhasa/ଠ.mp3",
  "ଡ": "/assets/odiabhasa/ଡ.mp3",
  "ଡ଼": "/assets/odiabhasa/ଡ଼.mp3",
  "ଢ": "/assets/odiabhasa/ଢ.mp3",
  "ଢ଼": "/assets/odiabhasa/ଢ଼.mp3",
  "ଣ": "/assets/odiabhasa/ଣ.mp3",
  "ତ": "/assets/odiabhasa/ତ.mp3",
  "ଥ": "/assets/odiabhasa/ଥ.mp3",
  "ଦ": "/assets/odiabhasa/ଦ.mp3",
  "ଧ": "/assets/odiabhasa/ଧ.mp3",
  "ନ": "/assets/odiabhasa/ନ.mp3",
  "ପ": "/assets/odiabhasa/ପ.mp3",
  "ଫ": "/assets/odiabhasa/ଫ.mp3",
  "ବ": "/assets/odiabhasa/ବ.mp3",
  "ଭ": "/assets/odiabhasa/ଭ.mp3",
  "ମ": "/assets/odiabhasa/ମ.mp3",
  "ଯ": "/assets/odiabhasa/ଯ.mp3",
  "ୟ": "/assets/odiabhasa/ୟ.mp3",
  "ର": "/assets/odiabhasa/ର.mp3",
  "ଲ": "/assets/odiabhasa/ଲ.mp3",
  "ଳ": "/assets/odiabhasa/ଳ.mp3",
  "ଶ": "/assets/odiabhasa/ଶ.mp3",
  "ଷ": "/assets/odiabhasa/ଷ.mp3",
  "ସ": "/assets/odiabhasa/ସ.mp3",
  "ହ": "/assets/odiabhasa/ହ.mp3",
};

// Note: In Kotlin mapOf, duplicate keys ("ଶ", "ଷ", "ସ", "୍") are overwritten by the later BARNA PARICHAYA entries,
// while we also keep a separate lookup for Byanjana vs Barna Parichaya if needed, and keep exact parity with OdiaData.kt!
export const odiaDictionary: Record<string, ContentData> = {
  // === SWARABARNNA (Vowels) ===
  "ଅ": { wordString: "ଅରଟ, ଅଜା", sentences: ["ଅରଟ ରେ ସୂତା ବୁଣା ଯାଏ ।", "ମୋ ଅଜା ମୋତେ ବହୁତ ଭଲ ପାଆନ୍ତି ।"] },
  "ଆ": { wordString: "ଆକାଶ, ଆମ", sentences: ["ଆକାଶର ରଙ୍ଗ ନୀଳ ।", "ଆମ ଦେଶର ନାମ ଭାରତ ।"] },
  "ଇ": { wordString: "ନାମ: ହର୍ଷ ଇ, ଇଟା, ଇଛା", sentences: ["ଇଟାରେ ଘର ତିଆରି ହୁଏ ।", "ମୋର ପଢିବାକୁ ଇଛା ଅଛି ।"] },
  "ଈ": { wordString: "ନାମ: ଦୀର୍ଘ ଈ, ଈଶ୍ୱର, ଈର୍ଷା", sentences: ["ଈଶ୍ୱର ସବୁଠାରେ ଅଛନ୍ତି ।", "କାହା ପ୍ରତି ଈର୍ଷା କରିବା ଉଚିତ୍ ନୁହେଁ ।"] },
  "ଉ": { wordString: " ନାମ: ହର୍ଷ ଉ, ଉପର, ଉଠ", sentences: ["ଟେବୁଲ ଉପରେ ବହି ଅଛି ।", "ସକାଳୁ ଜଲଦି ଉଠ ।"] },
  "ଊ": { wordString: " ନାମ: ଦୀର୍ଘ ଊ, ଊଣା", sentences: ["ତରକାରୀରେ ଲୁଣ ଊଣା ହେଲା ।"] },
  "ଋ": { wordString: "ନାମ: ହର୍ଷ ଋ, ଋଷି, ଋତୁ", sentences: ["ବନରେ ଋଷି ତପସ୍ୟା କରନ୍ତି ।", " ଶୀତ ଋତୁ ରେ ଧାନ ଅମଳ ହୁଏ ।"] },
  "ଏ": { wordString: "ଏକ, ଏବେ", sentences: ["ରବି ଏକ ଭଲ ପିଲା ।", "ମୁଁ ଏବେ ପାଠ ପଢୁଛି ।"] },
  "ଐ": { wordString: "ଐକ୍ୟ, ଐରାବତ", sentences: ["ଜାତୀୟ ଐକ୍ୟ ବଜାୟ ରଖ ।", "ଇନ୍ଦ୍ରଙ୍କ ବାହନର ନାମ ଐରାବତ ।"] },
  "ଓ": { wordString: "ଓଟ, ଓଜନ", sentences: ["ଓଟ ହେଉଛି ମରୁଭୂମିର ଜାହାଜ ।", "ଦୋକାନୀ ଜିନିଷ ଗୁଡିକ ଓଜନ କରେ ।"] },
  "ଔ": { wordString: "ଔଷଧ", sentences: ["ଦେହ ଖରାପ ହେଲେ ଔଷଧ ଖାଅ ।", "ଠିକ୍ ସମୟରେ ଔଷଧ ସେବନ କର ।"] },

  // Deprecated Vowels
  "ୠ": { wordString: "ନାମ: ଦୀର୍ଘ ୠ, ପୁରାତନ ବର୍ଣ୍ଣ", sentences: ["ଏହା ଏକ ପୁରାତନ ବର୍ଣ୍ଣ ।"] },
  "ଌ": { wordString: "ପୁରାତନ ବର୍ଣ୍ଣ", sentences: ["ଏହା ଏକ ପୁରାତନ ବର୍ଣ୍ଣ ।"] },
  "ୡ": { wordString: "ପୁରାତନ ବର୍ଣ୍ଣ", sentences: ["ଏହା ଏକ ପୁରାତନ ବର୍ଣ୍ଣ ।"] },

  // === Matra ===
  "ା": { wordString: "ବାପା, ମା, ରାଜା, ଭାଇ", sentences: ["ବାପା ମୋତେ ପାଠ ପଢ଼ାଉଛନ୍ତି ।", "ମା ଘରେ ରୋଷେଇ କରୁଛନ୍ତି ।", "ସେ ଜଣେ ଦୟାଳୁ ରାଜା ଥିଲେ ।", "ମୋ ଭାଇ ବହୁତ ଭଲ ଖେଳେ ।"] },
  "ି": { wordString: "ଦିନ, ଚିଲ", sentences: ["ଆଜି ଏକ ସୁନ୍ଦର ଦିନ ।", "ଆକାଶରେ ଏକ ଚିଲ ଉଡୁଛି ।"] },
  "ୀ": { wordString: "ନଦୀ, ଗୀତ", sentences: ["ନଦୀ କୂଳରେ ଗାଁ'ଟିଏ ଅଛି ।", "ସେ ସୁନ୍ଦର ଗୀତ ଗାଏ ।"] },
  "ୁ": { wordString: "ଫୁଲ, ପୁଅ", sentences: ["ବଗିଚାରେ ଏକ ଗୋଲାପ ଫୁଲ ଫୁଟିଛି ।", "ତାଙ୍କ ପୁଅ ଭଲ କବାଡି ଖେଳେ ।"] },
  "ୂ": { wordString: "ମୂଳ, ଦୂର", sentences: ["ଗଛ ମୂଳରେ ପ୍ରତିଦିନ ପାଣି ଦିଅ ।", "ସେହି ସହରଟି ଏଠାରୁ ବହୁତ ଦୂରରେ ଅଛି ।"] },
  "ୃ": { wordString: "କୃଷକ, ଗୃହ", sentences: ["କୃଷକ ବିଲରେ କାମ କରେ ।", "ଆମେ ଏକ ସୁନ୍ଦର ଗୃହରେ ରହୁ ।"] },
  "େ": { wordString: "ତେଲ, ଦେଶ", sentences: ["ବଜାରରେ ତେଲର ଦାମ୍ ବଢ଼ିଯାଇଛି ।", "ଆମ ଦେଶର ନାମ ଭାରତ ।"] },
  "ୈ": { wordString: "ଦୈନିକ", sentences: ["ସ୍ୱାସ୍ଥ୍ୟ ପାଇଁ ଦୈନିକ ବ୍ୟାୟାମ ଆବଶ୍ୟକ ।"] },
  "ୋ": { wordString: "ଲୋକ, ଭୋକ", sentences: ["ବାଟରେ ବହୁତ ଲୋକ ଯାଉଛନ୍ତି ।", "ମୋତେ ଭୋକ ଲାଗୁଛି କ'ଣ ଖାଇବି?"] },
  "ୌ": { wordString: "ମୌନ, ଚୌକି", sentences: ["ଶ୍ରେଣୀରେ ମୌନ ରୁହ ।", "ଆମ ଘରେ ଏକ କାଠର ଚୌକି ଅଛି ।"] },
  "଼": { wordString: "ଓଡ଼ିଆ, ପଡ଼ିଲା", sentences: ["ଏହା ଡ ଓ ଢ ଧ୍ୱନି ପାଇଁ ବ୍ୟବହୃତ ହୁଏ ।", "ଗଛରୁ ଫଳଟି ପଡ଼ିଲା ।"] },

  // === BYANJANABARNA (Consonants) ===
  "କ": { wordString: "କମଳା, କଦଳୀ", sentences: ["ମୋତେ କମଳା ଖାଇବାକୁ ଭଲ ଲାଗେ ।", "ମୁଁ କଦଳୀ ଖାଇବାକୁ ଭଲପାଏ ।"] },
  "ଖ": { wordString: "ଖବର, ଖରା", sentences: ["ଖବର କାଗଜ ପଢିବା ଭଲ ।", "ଆଜି ବହୁତ ଖରା ହେଉଛି ।"] },
  "ଗ": { wordString: "ଗାଈ, ଗଛ", sentences: ["ଗାଈ ଆମକୁ କ୍ଷୀର ଦିଏ ।", "ଗଛ ଆମକୁ ଛାଇ ଦିଏ ।"] },
  "ଘ": { wordString: "ଘର, ଘଣ୍ଟା", sentences: ["ଆମ ଘର ଆଗରେ ଆମ୍ବ ଗଛଟିଏ ଅଛି ।", "ଆମ ବିଦ୍ୟାଳୟ ରେ ଏକ କାନ୍ଥ ଘଣ୍ଟା ଅଛି ।"] },
  "ଙ": { wordString: "ରଙ୍ଗ", sentences: ["ଆମ ଜାତୀୟ ପତାକାରେ ତିନୋଟି ରଙ୍ଗ ଅଛି ।", "ଙ ବର୍ଣ୍ଣର ସ୍ୱତନ୍ତ୍ର ବ୍ୟବହାର ନାହିଁ ।"] },
  "ଚ": { wordString: "ଚକ, ଚଷମା, ଚଢ଼େଇ", sentences: ["ଗାଡିର ଚକ ଗୋଲ ଅଟେ ।", "ବାପା ଚଷମା ପିନ୍ଧନ୍ତି ।", "ଚଢ଼େଇ ଆକାଶରେ ଉଡ଼େ ।"] },
  "ଛ": { wordString: "ଛତା, ଛବି", sentences: ["ବର୍ଷା ହେଲେ ଛତା ଦରକାର ।", "ଛବିଟି ସୁନ୍ଦର ଦିଶୁଛି ।"] },
  "ଜ": { wordString: "ଜଳ, ଜାମୁକୋଳି", sentences: ["ଜଳ ବିନା ଜୀବନ ଅସମ୍ଭବ ।", " ଜାମୁକୋଳି ଖାଇବାକୁ ସ୍ୱାଦିଷ୍ଟ ।"] },
  "ଝ": { wordString: "ଝରକା, ଝରଣା", sentences: ["ଝରକା ଦେଇ ପବନ ଆସେ ।", "ଝରଣା ପାଣି ଥଣ୍ଡା ।"] },
  "ଞ": { wordString: "ଞ", sentences: ["ଞ ବର୍ଣ୍ଣର ସ୍ୱତନ୍ତ୍ର ବ୍ୟବହାର ନାହିଁ । ।"] },
  "ଟ": { wordString: "ଟଗର, ଟଙ୍କା", sentences: ["ଟଗର ଫୁଲର ରଙ୍ଗ ଧଳା ।", "ଟଙ୍କା ସଞ୍ଚୟ କର ।"] },
  "ଠ": { wordString: "ଠକ, ଠାକୁର", sentences: ["ଠକ ଲୋକଙ୍କୁ ବିଶ୍ୱାସ କରନି ।", "ଆମେ ଠାକୁରଙ୍କୁ ପୂଜା କରୁ ।"] },
  "ଡ": { wordString: "ଡର, ଡାକ୍ତର", sentences: ["ମିଛ କହିବାକୁ ଡର ଲାଗେ ।", "ଅସୁସ୍ଥ ହେଲେ ଡାକ୍ତରଙ୍କ ପାଖକୁ ଯାଅ ।"] },
  "ଢ": { wordString: "ଢଗ, ଢାଙ୍କୁଣୀ", sentences: ["ଢଗ ଢମାଳି ମଜାଦାର ଅଟେ ।", "ଖାଦ୍ୟକୁ ଢାଙ୍କୁଣୀ ଦେଇ ରଖ ।"] },
  "ଣ": { wordString: "ହରିଣ, କ'ଣ", sentences: ["ହରିଣ ଜଙ୍ଗଲରେ ରହେ ।", "ତୁମେ କ'ଣ କରୁଛ?"] },
  "ତ": { wordString: "ତରାଜୁ, ତାରା", sentences: ["ଦୋକାନୀ ତରାଜୁରେ ଓଜନ କରେ ।", "ରାତି ଆକାଶରେ ତାରା ଝଲକେ ।"] },
  "ଥ": { wordString: "ଥଣ୍ଡା, ଥାଳି", sentences: ["ଆଜି ବହୁତ ଥଣ୍ଡା ଲାଗୁଛି ।", "ଆମେ ଥାଳିରେ ଖାଉ ।"] },
  "ଦ": { wordString: "ଦହି, ଦରଜା", sentences: ["ଦହି ଖାଇବା ସ୍ୱାସ୍ଥ୍ୟ ପାଇଁ ଭଲ ।", "ଘରର ଦରଜା ବନ୍ଦ କର ।"] },
  "ଧ": { wordString: "ଧାନ, ଧନ", sentences: ["ଧାନରୁ ଚାଉଳ ହୁଏ ।", "ବିଦ୍ୟା ଅଟଇ ମହା ଧନ ।"] },
  "ନ": { wordString: "ନଦୀ, ନଖ", sentences: ["ମହାନଦୀ ଓଡ଼ିଶାର ବଡ ନଦୀ ।", "ନଖକୁ ସଫା ରଖ ।"] },
  "ପ": { wordString: "ପତ୍ର, ପାଣି", sentences: ["ଗଛରେ ପତ୍ର ଥାଏ ।", "ପାଣି ଆମ ଜୀବନ ଅଟେ ।"] },
  "ଫ": { wordString: "ଫଳ, ଫୁଲ", sentences: ["ଫଳ ଖାଇବା ଶରୀର ପାଇଁ ଲାଭଦାୟକ ।", "ବଗିଚାରେ ଫୁଲ ଫୁଟିଛି ।"] },
  "ବ": { wordString: "ବହି, ବଗ", sentences: ["ମୁଁ ବହି ପଢୁଛି ।", "ବଗ ପୋଖରୀରେ ମାଛ ଧରେ ।"] },
  "ଭ": { wordString: "ଭଉଣୀ, ଭାତ", sentences: ["ମୋର ସାନ ଭଉଣୀ ଅଛି ।", "ଆମେ ପ୍ରତିଦିନ ଭାତ ଖାଉ ।"] },
  "ମ": { wordString: "ମନ୍ଦିର, ମା", sentences: ["ପୁରୀରେ ଶ୍ରୀ ଜଗନ୍ନାଥ ମନ୍ଦିର ଅଛି ।", "ମା'ଙ୍କୁ ସମ୍ମାନ କର ।"] },
  "ଯ": { wordString: "ଯଜ୍ଞ, ଯମ", sentences: ["ଋଷିମାନେ ଯଜ୍ଞ କରନ୍ତି ।", "ଯମ ଗୋଟିଏ ରାଜା ।"] },
  "ର": { wordString: "ରଥ, ରଜ", sentences: ["ରଥଯାତ୍ରା ଓଡ଼ିଶାର ଏକ ବଡ ପର୍ବ ।", "ରଜରେ ମୁଁ ଦୋଳି ଖେଳିଲି ।"] },
  "ଳ": { wordString: "ଫଳ, ଖେଳ", sentences: ["ଗଛରେ ଫଳ ଫଳେ ।", "ହକି ଆମର ଜାତୀୟ ଖେଳ ।"] },
  "ୱ": { wordString: "ୱିକେଟ", sentences: ["କ୍ରିକେଟ ଖେଳରେ ୱିକେଟ୍ ଏକ ଗୁରୁତ୍ୱପୂର୍ଣ୍ଣ ଅଂଶ ।"] },
  "ହ": { wordString: "ହଳ, ହାତ", sentences: ["ଚାଷୀ ବିଲରେ ହଳ କରେ ।", "ଖାଇବା ପୂର୍ବରୁ ହାତ ଧୋଅ ।"] },
  "କ୍ଷ": { wordString: "କ୍ଷମା, ଶିକ୍ଷକ", sentences: ["ଭୁଲ୍ ହେଲେ କ୍ଷମା ମାଗିବା ଉଚିତ୍ ।", "ଶିକ୍ଷକ ଆମକୁ ଭଲ ଜ୍ଞାନ ଦିଅନ୍ତି ।"] },
  "ୟ": { wordString: "ସମୟ, ବିଜୟ", sentences: ["ସମୟ ମୂଲ୍ୟବାନ ଅଟେ ।", "ପରିଶ୍ରମ କଲେ ନିଶ୍ଚୟ ବିଜୟ ମିଳେ ।"] },
  "ଲ": { wordString: "ଲତା, ଲୁଣ", sentences: ["ଲତା ଗଛରେ ଗୁଡେଇ ହୁଏ ।", "ଲୁଣ ବିନା ଖାଦ୍ୟ ଫିକା ଲାଗେ ।"] },

  // Extra Consonants
  "ଡ଼": { wordString: "ବଡ଼, ପାହାଡ଼", sentences: ["ହାତୀ ଏକ ବଡ଼ ଜୀବ ।", "ପାହାଡ଼ ଉପରେ ମନ୍ଦିର ଅଛି ।"] },
  "ଢ଼": { wordString: "ଗାଢ଼", sentences: ["ନୀଳ ରଙ୍ଗ ଗାଢ଼ ଦିଶୁଛି ।"] },
  "ଽ": { wordString: "ନାମ: ଅବଗ୍ରହ, ସୋଽହମ୍", sentences: ["ସୋଽହମ୍ ଏକ ଭଲ ପିଲା ।", "ଏହା ଅବଗ୍ରହ ଚିହ୍ନ ।"] },
  "ଵ": { wordString: "ପୁରାତନ ବର୍ଣ୍ଣ", sentences: ["ଏହା ଏକ ପୁରାତନ ବର୍ଣ୍ଣ ।"] },
  "୰": { wordString: "ନାମ: ସ୍ୱର୍ଗୀୟ", sentences: ["ମୃତ ବ୍ୟକ୍ତିଙ୍କ ପାଇଁ ବ୍ୟବହାର ହୁଏ ।"] },

  // === BARNA PARICHAYA (Descriptions) ===
  "ଶ": { wordString: "ନାମ: ତାଲବ୍ୟ ଶ, ଶିକ୍ଷକ", sentences: ["ଏହା ହେଉଛି : ତାଲବ୍ୟ ଶ", "ଆମ ଶିକ୍ଷକ ବହୁତ ଭଲ ପଢ଼ାନ୍ତି ।"] },
  "ଷ": { wordString: "ନାମ: ମୂର୍ଦ୍ଧନ୍ୟ ଷ, ଭାଷା", sentences: ["ଏହା ହେଉଛି : ମୂର୍ଦ୍ଧନ୍ୟ ଷ", "ଓଡ଼ିଆ ଆମର ମାତୃଭାଷା ।"] },
  "ସ": { wordString: "ନାମ: ଦନ୍ତ୍ୟ ସ, ସମୟ", sentences: ["ଏହା ହେଉଛି : ଦନ୍ତ୍ୟ ସ", "ସମୟ କାହାରିକୁ ଅପେକ୍ଷା କରେ ନାହିଁ ।"] },
  "ଂ": { wordString: "ନାମ: ଅନୁସ୍ୱାର, ସଂସାର, ସଂଖ୍ୟା", sentences: ["ବିଦ୍ୟାଳୟରେ ଛାତ୍ରଛାତ୍ରୀଙ୍କ ସଂଖ୍ୟା ବଢ଼ି ଯାଇଛି ।"] },
  "ଃ": { wordString: "ନାମ: ବିସର୍ଗ, ପୁନଃ, ଦୁଃଖ", sentences: ["ଜୀବନରେ ସୁଖ ଓ ଦୁଃଖ ଲାଗି ରହିଥାଏ ।"] },
  "ଁ": { wordString: "ନାମ: ଚନ୍ଦ୍ରବିନ୍ଦୁ, ଯାଁଆଳ", sentences: ["ତାଙ୍କର ଦୁଇଟି ଯାଁଆଳ ପୁଅ ଅଛନ୍ତି ।"] },
  "୍": { wordString: "ନାମ: ହଳନ୍ତ", sentences: ["ଦାମ୍ ଭଳି ଶବ୍ଦ, ପ+ତ୍+ର= ପତ୍ର", " ଯୁକ୍ତାକ୍ଷର ପାଇଁ ମଧ୍ୟ ବ୍ୟବହାର କରାଯାଏ ।"] },
  "<": { wordString: "ନାମ: ରେଫ (ର+୍)", sentences: ["ଧର୍ମ, କର୍ମ, ସୂର୍ଯ୍ୟ", "କର୍ମ ହିଁ ଭଗବାନ ।"] },
  ",": { wordString: "ନାମ: କମା", sentences: ["ଯେତେବେଳେ ଗୋଟିଏ ବାକ୍ୟରେ ଅନେକଗୁଡ଼ିଏ ଜିନିଷର ନାମ ଥାଏ ।", "ବଜାରରୁ ଆଳୁ, ବାଇଗଣ, ପୋଟଳ ଓ କଖାରୁ ନେଇ ଆସ ।"] },
  "?": { wordString: "ନାମ: ପ୍ରଶ୍ନବାଚକ ଚିହ୍ନ, ?", sentences: ["ତୁମର ନାମ କ’ଣ ?"] },
  "।": { wordString: "ନାମ: ପୂର୍ଣ୍ଣଚ୍ଛେଦ, ।", sentences: ["ଓଡ଼ିଆ ଲିଖନ ଶୈଳୀରେ ବାକ୍ୟ ଶେଷ ହେଲେ ପୂର୍ଣ୍ଣଚ୍ଛେଦ ବ୍ୟବହାର କରାଯାଏ ।"] },

  // === SANKHYA (Numbers) ===
  "୦": { wordString: "ଶୂନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧": { wordString: "ଏକ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨": { wordString: "ଦୁଇ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩": { wordString: "ତିନି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪": { wordString: "ଚାରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫": { wordString: "ପାଞ୍ଚ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬": { wordString: "ଛଅ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭": { wordString: "ସାତ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮": { wordString: "ଆଠ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯": { wordString: "ନଅ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୦": { wordString: "ଦଶ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୧": { wordString: "ଏଗାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୨": { wordString: "ବାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୩": { wordString: "ତେର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୪": { wordString: "ଚଉଦ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୫": { wordString: "ପନ୍ଦର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୬": { wordString: "ଷୋହଳ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୭": { wordString: "ସତର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୮": { wordString: "ଅଠର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୯": { wordString: "ଉଣେଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୦": { wordString: "କୋଡ଼ିଏ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୧": { wordString: "ଏକୋଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୨": { wordString: "ବାଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୩": { wordString: "ତେଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୪": { wordString: "ଚବିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୫": { wordString: "ପଚିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୬": { wordString: "ଛବିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୭": { wordString: "ସତେଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୮": { wordString: "ଅଠେଇଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୯": { wordString: "ଅଣତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୦": { wordString: "ତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୧": { wordString: "ଏକତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୨": { wordString: "ବତିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୩": { wordString: "ତେତିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୪": { wordString: "ଚଉତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୫": { wordString: "ପଞ୍ଚତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୬": { wordString: "ଛତିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୭": { wordString: "ସଇଁତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୮": { wordString: "ଅଠତିରିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୯": { wordString: "ଅଣଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୦": { wordString: "ଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୧": { wordString: "ଏକଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୨": { wordString: "ବୟାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୩": { wordString: "ତେୟାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୪": { wordString: "ଚଉରାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୫": { wordString: "ପଞ୍ଚଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୬": { wordString: "ଛୟାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୭": { wordString: "ସତଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୮": { wordString: "ଅଠଚାଳିଶି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୯": { wordString: "ଅଣଚାଶ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୦": { wordString: "ପଚାଶ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୧": { wordString: "ଏକାବନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୨": { wordString: "ବାଉନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୩": { wordString: "ତେପନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୪": { wordString: "ଚଉବନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୫": { wordString: "ପଞ୍ଚାବନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୬": { wordString: "ଛପନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୭": { wordString: "ସତାବନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୮": { wordString: "ଅଠାବନ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୯": { wordString: "ଅଣଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୦": { wordString: "ଷାଠିଏ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୧": { wordString: "ଏକଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୨": { wordString: "ବାଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୩": { wordString: "ତେଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୪": { wordString: "ଚଉଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୫": { wordString: "ପଞ୍ଚଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୬": { wordString: "ଛଅଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୭": { wordString: "ସତଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୮": { wordString: "ଅଠଷଠି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୬୯": { wordString: "ଅଣସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୦": { wordString: "ସତୁରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୧": { wordString: "ଏକସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୨": { wordString: "ବାସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୩": { wordString: "ତେସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୪": { wordString: "ଚଉସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୫": { wordString: "ପଞ୍ଚସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୬": { wordString: "ଛଅସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୭": { wordString: "ସତସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୮": { wordString: "ଅଠସ୍ତରି", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୭୯": { wordString: "ଅଣାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୦": { wordString: "ଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୧": { wordString: "ଏକାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୨": { wordString: "ବୟାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୩": { wordString: "ତେୟାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୪": { wordString: "ଚଉରାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୫": { wordString: "ପଞ୍ଚାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୬": { wordString: "ଛୟାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୭": { wordString: "ସତାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୮": { wordString: "ଅଠାଅଶୀ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୮୯": { wordString: "ଅଣାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୦": { wordString: "ନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୧": { wordString: "ଏକାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୨": { wordString: "ବୟାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୩": { wordString: "ତେୟାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୪": { wordString: "ଚଉରାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୫": { wordString: "ପଞ୍ଚାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୬": { wordString: "ଛୟାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୭": { wordString: "ସତାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୮": { wordString: "ଅଠାନବେ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୯୯": { wordString: "ଅନେଶୋତ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୦୦": { wordString: "ଶହେ, ଏକ ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୫୦": { wordString: "ଶହେ ପଚାଶ, ଦେଢ ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୦୦": { wordString: "ଦୁଇ ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୫୦": { wordString: "ଦୁଇ ଶହ ପଚାଶ, ଅଢେଇ ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୦୦": { wordString: "ତିନି ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୫୦": { wordString: "ତିନି ଶହ ପଚାଶ, ସାଢେ ତିନି ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୦୦": { wordString: "ଚାରି ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୫୦୦": { wordString: "ପାଞ୍ଚ ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୦୦୦": { wordString: "ଏକ ହଜାର, ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୧୦୦": { wordString: "ଏକ ହଜାର ଶହେ, ଏଗାର ଶହ", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୧୫୦୦": { wordString: "ଏକ ହଜାର ପାଞ୍ଚ ଶହ, ପନ୍ଦର ଶହ, ଦେଢ ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୦୦୦": { wordString: "ଦୁଇ ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୨୫୦୦": { wordString: "ଦୁଇ ହଜାର ପାଞ୍ଚ ଶହ, ପଚିଶ ଶହ, ଅଢେଇ ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୦୦୦": { wordString: "ତିନି ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୩୫୦୦": { wordString: "ତିନି ହଜାର ପାଞ୍ଚ ଶହ, ପଞ୍ଚତିରିଶି ଶହ, ସାଢେ ତିନି ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },
  "୪୦୦୦": { wordString: "ଚାରି ହଜାର", sentences: ["ଉଦାହରଣ ନାହିଁ"] },

  // === YUKTAKSHYARA (Conjuncts) - ALL with example sentences ===
  "ଙ୍କ": { wordString: "ଅଙ୍କ, ଡଙ୍କ", sentences: ["ଗଣିତରେ ଅଙ୍କ କଷାଯାଏ ।", "କଖାରୁ ଡଙ୍କରେ ତରକାରୀ କରାଯାଏ ।"] },
  "ଙ୍ଖ": { wordString: "ଶଙ୍ଖ", sentences: ["ମନ୍ଦିରରେ ପୂଜାରୀ ଶଙ୍ଖ ଫୁଙ୍କୁଛନ୍ତି ।"] },
  "ଙ୍ଗ": { wordString: "ରଙ୍ଗ, ଜଙ୍ଗଲ", sentences: ["ହୋଲି ଏକ ରଙ୍ଗର ପର୍ବ ଅଟେ ।", "ଜଙ୍ଗଲରେ ବାଘ ରହେ ।"] },
  "ଙ୍ଘ": { wordString: "ସଙ୍ଘର୍ଷ", sentences: ["ଜୀବନରେ ସଫଳତା ପାଇଁ ସଙ୍ଘର୍ଷ କରିବାକୁ ପଡେ ।"] },
  "ଞ୍ଚ": { wordString: "ପାଞ୍ଚ, ମଞ୍ଚ", sentences: ["ମୋ ପାଖରେ ପାଞ୍ଚଟି ଆମ୍ବ ଅଛି ।", "କଳାକାରମାନେ ମଞ୍ଚ ଉପରେ ଅଭିନୟ କରୁଛନ୍ତି ।"] },
  "ଞ୍ଛ": { wordString: "ଲାଞ୍ଛନା", sentences: ["କାହାକୁ ଲାଞ୍ଛନା ଦେବା ଏକ ଖରାପ ଅଭ୍ୟାସ ।"] },
  "ଞ୍ଜ": { wordString: "ପଞ୍ଜୁରୀ", sentences: ["ପଞ୍ଜୁରୀ ଭିତରେ ଶୁଆଟି ରାମ-ରାମ ଡାକୁଛି ।"] },
  "ଞ୍ଝ": { wordString: "ପଞ୍ଝା", sentences: ["ବାଘର ପଞ୍ଝା ବହୁତ ଶକ୍ତ ।"] },
  "ଣ୍ଟ": { wordString: "ଘଣ୍ଟି, କଣ୍ଟା", sentences: ["ବିଦ୍ୟାଳୟରେ ଛୁଟି ଘଣ୍ଟି ବାଜିଲା ।", "ମୋ ପାଦରେ କଣ୍ଟା ଟିଏ ଫୁଟି ଗଲା ।"] },
  "ଣ୍ଠ": { wordString: "କଣ୍ଠ, ଉତ୍କଣ୍ଠା", sentences: ["ମୋ ବନ୍ଧୁ ସୁନ୍ଦର କଣ୍ଠରେ ଗୀତ ଗାଇଲା ।", "ମୁଁ ଉତ୍କଣ୍ଠାର ସହ କାର୍ଯ୍ୟ କରେ ।"] },
  "ଣ୍ଡ": { wordString: "ଦଣ୍ଡ, ଅଣ୍ଡା", sentences: ["ବିନା ହେଲମେଟରେ ଗାଡି ଚଳାଇବା ଦଣ୍ଡନୀୟ ଅପରାଧ ।", "ମୋତେ ଅଣ୍ଡା ଖାଇବାକୁ ଭଲ ଲାଗେ ।"] },
  "ଣ୍ଢ": { wordString: "ଷଣ୍ଢ", sentences: ["ରାସ୍ତା ଉପରେ ଏକ ଷଣ୍ଢ ଠିଆ ହୋଇଛି ।"] },
  "ନ୍ତ": { wordString: "ଅନ୍ତ, ଶାନ୍ତ", sentences: ["ସମୟର ଅନ୍ତ ନାହିଁ ।", "ପିଲାଟି ବହୁତ ଶାନ୍ତ ସ୍ୱଭାବର ।"] },
  "ନ୍ଥ": { wordString: "ଗ୍ରନ୍ଥ, ପନ୍ଥା", sentences: ["ଭାଗବତ ଏକ ପବିତ୍ର ଗ୍ରନ୍ଥ ଅଟେ ।", "ମୋ ପାଖରେ ଅନ୍ୟ କିଛି ପନ୍ଥା ନାହିଁ ।"] },
  "ନ୍ଦ": { wordString: "ଚନ୍ଦନ, ଆନନ୍ଦ", sentences: ["ପୂଜାରେ ଚନ୍ଦନ କାଠ ବ୍ୟବହାର ହୁଏ ।", "ମୋ ସାଙ୍ଗମାନଙ୍କ ସହିତ ମୁଁ ଆନନ୍ଦରେ ଖେଳେ ।"] },
  "ନ୍ଧ": { wordString: "ଅନ୍ଧ, ବନ୍ଧ", sentences: ["ସାହାଯ୍ୟ ପାଇଁ ଅନ୍ଧ ଲୋକଟି ରାସ୍ତାରେ ଠିଆ ହୋଇଥିଲା ।"] },
  "ମ୍ପ": { wordString: "କମ୍ପ, ଚମ୍ପା", sentences: ["ଭୂମିକମ୍ପ ସମୟରେ ଘର କମ୍ପି ଉଠିଲା ।"] },
  "ମ୍ଫ": { wordString: "ବାମ୍ଫ, ଲମ୍ଫ", sentences: ["ଗରମ ପାଣିରୁ ବାମ୍ଫ ବାହାରୁଛି ।"] },
  "ମ୍ବ": { wordString: "ଆମ୍ବ, କମ୍ବଳ", sentences: ["ଆମ୍ବ ଏକ ସୁଆଦିଆ ଫଳ ।"] },
  "ମ୍ଭ": { wordString: "ଆରମ୍ଭ, ସ୍ତମ୍ଭ", sentences: ["କାମ ଆରମ୍ଭ କରିବା ପୂର୍ବରୁ ଭଗବାନଙ୍କୁ ସ୍ମରଣ କର ।"] },
  "କ୍କ": { wordString: "ଟକ୍କର", sentences: ["ଗାଡ଼ି ଦୁଇଟି ମଧ୍ୟରେ ଟକ୍କର ହେଲା ।"] },
  "କ୍ତ": { wordString: "ଭକ୍ତ, ରକ୍ତ", sentences: ["ଭକ୍ତମାନେ ମନ୍ଦିରରେ ଭଗବାନଙ୍କୁ ଦର୍ଶନ କରୁଛନ୍ତି ।"] },
  "ତ୍କ": { wordString: "ଉତ୍କଳ, ଚମତ୍କାର", sentences: ["ଉତ୍କଳ ଦିବସ ଏପ୍ରିଲ ୧ ତାରିଖ ଦିନ ପାଳନ କରାଯାଏ ।"] },
  "ଚ୍ଚ": { wordString: "ଉଚ୍ଚ, ସଚ୍ଚା", sentences: ["ହିମାଳୟ ପୃଥିବୀର ସବୁଠାରୁ ଉଚ୍ଚ ପର୍ବତମାଳା ।"] },
  "ଚ୍ଛ": { wordString: "ସ୍ୱଚ୍ଛ, ଇଚ୍ଛା", sentences: ["ଆମେ ଆମ ପରିବେଶକୁ ସ୍ୱଚ୍ଛ ରଖିବା ଉଚିତ୍ ।"] },
  "ଜ୍ଜ": { wordString: "ସଜ୍ଜା, ନିର୍ଲଜ୍ଜ", sentences: ["ବରଯାତ୍ରୀ ପାଇଁ ଗାଡ଼ିଟି ଫୁଲରେ ସଜ୍ଜା ହୋଇଛି ।"] },
  "ଜ୍ଞ": { wordString: "ବିଜ୍ଞ, ଅଜ୍ଞ", sentences: ["ବିଜ୍ଞ ଲୋକମାନେ ସବୁବେଳେ ଜ୍ଞାନ ଅର୍ଜନ କରନ୍ତି ।"] },
  "ଟ୍ଟ": { wordString: "ଭଟ୍ଟା", sentences: ["ସମୁଦ୍ରରେ ଭଟ୍ଟା ସୃଷ୍ଟି ହୁଏ ।"] },
  "ଡ୍ଗ": { wordString: "ଖଡ୍ଗଧାରୀ", sentences: ["ଖଡ୍ଗଧାରୀ ଯୋଦ୍ଧା ଯୁଦ୍ଧ ପାଇଁ ପ୍ରସ୍ତୁତ ହେଲେ ।"] },
  "ଡ୍ଡ": { wordString: "ଉଡ୍ଡୟନ", sentences: ["ଗଛରୁ ପକ୍ଷୀଗୁଡିକ ଦୂରକୁ ଉଡ୍ଡୟନ କଲେ ।"] },
  "ତ୍ତ": { wordString: "ଉତ୍ତର, ଚିତ୍ତ", sentences: ["ପ୍ରଶ୍ନର ଉତ୍ତର ସଠିକ୍ ଭାବେ ଦିଅ ।"] },
  "ତ୍ଥ": { wordString: "ଉତ୍ଥାନ", sentences: ["ସୂର୍ଯ୍ୟଙ୍କ ଉତ୍ଥାନ ସକାଳୁ ହୁଏ ।"] },
  "ଦ୍ୱ": { wordString: "ଦ୍ୱାର, ଦ୍ୱନ୍ଦ", sentences: ["ମନ୍ଦିରର ଦ୍ୱାର ସବୁବେଳେ ଖୋଲା ଥାଏ ।"] },
  "ଗ୍ଧ": { wordString: "ଦୁଗ୍ଧ, ସ୍ନିଗ୍ଧ", sentences: ["ଗାଈ ଦୁଗ୍ଧ ଶିଶୁମାନଙ୍କ ପାଇଁ ଉପକାରୀ ।"] },
  "ଦ୍ଘ": { wordString: "ଉଦ୍ଘାଟନ", sentences: ["ମନ୍ତ୍ରୀ ନୂଆ ପୋଲର ଉଦ୍ଘାଟନ କଲେ ।"] },
  "ଦ୍ଦ": { wordString: "ଉଦ୍ଦେଶ୍ୟ", sentences: ["ପାଠ ପଢିବାର ମୂଳ ଉଦ୍ଦେଶ୍ୟ ହେଉଛି ଜ୍ଞାନ ଆହରଣ ।"] },
  "ଦ୍ଧ": { wordString: "ଯୁଦ୍ଧ, ବୁଦ୍ଧ", sentences: ["ଯୁଦ୍ଧ କରିବା ଦ୍ୱାରା କେବଳ କ୍ଷତି ହୁଏ ।"] },
  "ବ୍ଦ": { wordString: "ଶବ୍ଦ, ଶତାବ୍ଦୀ", sentences: ["ଜୋରରେ ଶବ୍ଦ କରିବା ଅନୁଚିତ୍ ।"] },
  "ବ୍ବ": { wordString: "ମୁରବ୍ବା", sentences: ["ମୁରବ୍ବା ଖାଇବାକୁ ଭାରି ମିଠା ଲାଗେ ।"] },
  "ଦ୍ଭ": { wordString: "ଉଦ୍ଭବ, ଅଦ୍ଭୁତ", sentences: ["ଗଙ୍ଗା ନଦୀର ଉଦ୍ଭବ ହିମାଳୟରୁ ହୋଇଛି ।"] },
  "ପ୍ପ": { wordString: "ପିପ୍ପଳୀ", sentences: ["ପିପ୍ପଳୀ ଏକ ଔଷଧୀୟ ଗଛ ।"] },
  "ପ୍ତ": { wordString: "ସପ୍ତ, ଗୁପ୍ତ", sentences: ["ଇନ୍ଦ୍ରଧନୁରେ ସପ୍ତ ରଙ୍ଗ ଥାଏ ।"] },
  "ତ୍ମ": { wordString: "ଆତ୍ମନିର୍ଭର", sentences: ["ମୁଁ କାହା ଉପରେ ଆତ୍ମନିର୍ଭର କରେ ନାହିଁ ।"] },
  "ଳ୍କ": { wordString: "ଉଲ୍କା", sentences: ["ରାତିରେ ଆକାଶରୁ ଉଲ୍କା ପିଣ୍ଡ ଖସିବାର ଦେଖାଯାଏ ।"] },
  "ଳ୍ପ": { wordString: "ଗଳ୍ପ, ସ୍ୱଳ୍ପ", sentences: ["ଆଈମା ଛୋଟ ପିଲାଙ୍କୁ ଗଳ୍ପ ଶୁଣାନ୍ତି ।"] },
  "ଶ୍ଚ": { wordString: "ପଶ୍ଚିମ, ନିଶ୍ଚୟ", sentences: ["ସୂର୍ଯ୍ୟ ପଶ୍ଚିମ ଦିଗରେ ଅସ୍ତ ହୁଅନ୍ତି ।"] },
  "ଶ୍ନ": { wordString: "ପ୍ରଶ୍ନ", sentences: ["ଶିକ୍ଷକ ଛାତ୍ରଙ୍କୁ କଠିନ ପ୍ରଶ୍ନ ପଚାରିଲେ ।"] },
  "ଷ୍କ": { wordString: "ଶୁଷ୍କ, ପୁଷ୍କରିଣୀ", sentences: ["ଖରାଦିନେ ନଦୀ ପୋଖରୀ ଶୁଷ୍କ ହୋଇଯାଏ ।"] },
  "ଷ୍ଟ": { wordString: "କଷ୍ଟ, ନଷ୍ଟ", sentences: ["ପରିଶ୍ରମ କଲେ କଷ୍ଟର ଫଳ ମିଳେ ।"] },
  "ଷ୍ଠ": { wordString: "ଷଷ୍ଠ, ଓଷ୍ଠ", sentences: ["ସେ ଷଷ୍ଠ ଶ୍ରେଣୀରେ ପାଠ ପଢ଼େ ।"] },
  "ଷ୍ଣ": { wordString: "କୃଷ୍ଣ, ଉଷ୍ଣ", sentences: ["ଭଗବାନ ଶ୍ରୀକୃଷ୍ଣଙ୍କୁ ସମସ୍ତେ ପୂଜା କରନ୍ତି ।"] },
  "ଷ୍ମ": { wordString: "ଗ୍ରୀଷ୍ମ", sentences: ["ଗ୍ରୀଷ୍ମ ଋତୁରେ ପ୍ରବଳ ଗରମ ହୁଏ ।"] },
  "ସ୍କ": { wordString: "ପୁରସ୍କାର, ଭାସ୍କର", sentences: ["ଭଲ କାମ ପାଇଁ ତାଙ୍କୁ ପୁରସ୍କାର ମିଳିଲା ।"] },
  "ସ୍ତ": { wordString: "ଅସ୍ତ, ସମସ୍ତ", sentences: ["ସୂର୍ଯ୍ୟ ସନ୍ଧ୍ୟାରେ ଅସ୍ତ ଯାଆନ୍ତି ।"] },
  "ସ୍ଥ": { wordString: "ସ୍ଥାନ, ସୁସ୍ଥ", sentences: ["ଏହି ସ୍ଥାନ ବୁଲିବା ପାଇଁ ବହୁତ ସୁନ୍ଦର ।"] },
  "ସ୍ନ": { wordString: "ସ୍ନାନ, ସ୍ନେହ", sentences: ["ସକାଳୁ ଉଠି ସ୍ନାନ କରିବା ଉଚିତ୍ ।"] },
  "ସ୍ପ": { wordString: "ପରସ୍ପର, ସ୍ପର୍ଶ", sentences: ["ଆମେ ପରସ୍ପରକୁ ସାହାଯ୍ୟ କରିବା ଉଚିତ୍ ।"] },
  "ସ୍ଫ": { wordString: "ସ୍ଫଟିକ", sentences: ["ସ୍ଫଟିକ ପରି ମନକୁ ସ୍ୱଚ୍ଛ ରଖିବା ଉଚିତ୍ ।"] },
  "ସ୍ତ୍ର": { wordString: "ବସ୍ତ୍ର, ଅସ୍ତ୍ର", sentences: ["ଗରିବ ଲୋକଙ୍କୁ ବସ୍ତ୍ର ଦାନ କରିବା ଏକ ପୁଣ୍ୟ କାମ ।"] },
  "ତ୍ସ": { wordString: "ଉତ୍ସବ, ବତ୍ସ", sentences: ["ଦୀପାବଳି ଏକ ଆଲୋକର ଉତ୍ସବ ।"] },
  "କ୍ଷ୍ଣ": { wordString: "ତୀକ୍ଷ୍ଣ", sentences: ["ଛୁରୀଟି ବହୁତ ତୀକ୍ଷ୍ଣ ଅଟେ ।"] },
  "କ୍ଷ୍ମ": { wordString: "ସୂକ୍ଷ୍ମ, ଯକ୍ଷ୍ମା, ଲକ୍ଷ୍ମୀ", sentences: ["ଲକ୍ଷ୍ମୀ ପୂଜାରେ ଘରକୁ ସଜାଯାଏ ।"] },
  "କ୍ଷ୍ୟ": { wordString: "ଲକ୍ଷ୍ୟ, ସାକ୍ଷ୍ୟ", sentences: ["ଜୀବନରେ ଏକ ବଡ ଲକ୍ଷ୍ୟ ରଖିବା ଉଚିତ୍ ।"] },
  "ଙ୍\u200Cକ୍ତ": { wordString: "ପଙ୍\u200Cକ୍ତି", sentences: [""] },
  "ଙ୍କ୍ଷ": { wordString: "ଆକାଙ୍କ୍ଷା", sentences: ["ସମସ୍ତଙ୍କର ବଡ ହେବାର ଆକାଙ୍କ୍ଷା ଥାଏ ।"] },
  "ଜ୍ଜ୍ଵ": { wordString: "ଉଜ୍ଜ୍ୱଳ", sentences: ["ଚନ୍ଦ୍ର ରାତିରେ ଉଜ୍ଜ୍ୱଳ ଆଲୋକ ଦିଏ ।"] },
  "ତ୍ତ୍ବ": { wordString: "ମହତ୍ତ୍ୱ", sentences: ["ସମୟର ମହତ୍ତ୍ୱ ବୁଝିବା ଦରକାର ।"] },
  "ତ୍ତ୍ର": { wordString: "ଛତ୍ତ୍ର", sentences: ["ବର୍ଷା ଦିନେ ଛତୁଗୁଡିକ ରାଜାଙ୍କ ଛତ୍ତ୍ର ପରି ଦେଖାଯାଏ ।"] },
  "ତ୍ମ୍ୟ": { wordString: "ଦୌରାତ୍ମ୍ୟ", sentences: ["ଦୁଷ୍ଟ ଲୋକର ଦୌରାତ୍ମ୍ୟ ବଢିଗଲାଣି ।"] },
  "ଣ୍ଡ୍ର": { wordString: "ପୁଣ୍ଡ୍ର", sentences: ["ସାଧୁମାନେ କପାଳରେ ପୁଣ୍ଡ୍ର ଧାରଣ କରନ୍ତି ।"] },
  "ତ୍ସ୍ନ": { wordString: "ଜ୍ୟୋତ୍ସ୍ନା", sentences: ["ପୂର୍ଣ୍ଣମୀ ରାତିରେ ଜ୍ୟୋତ୍ସ୍ନା ପଡେ ।"] },
  "ତ୍ସ୍ୟ": { wordString: "ମତ୍ସ୍ୟ", sentences: ["ମତ୍ସ୍ୟ ଜଳରେ ବାସ କରେ ।"] },
  "ନ୍ତ୍ୟ": { wordString: "ଅନ୍ତ୍ୟ", sentences: ["ଅନ୍ତ୍ୟ ସମୟରେ ସତ୍ୟ କୁହ ।"] },
  "ନ୍ତ୍ର": { wordString: "ମନ୍ତ୍ର, ଯନ୍ତ୍ର", sentences: ["ପୂଜାରେ ବ୍ରାହ୍ମଣ ମନ୍ତ୍ର ପାଠ କରୁଛନ୍ତି ।"] },
  "ନ୍ତ୍ଵ": { wordString: "ସାନ୍ତ୍ଵନା", sentences: ["ଦୁଃଖୀ ଲୋକକୁ ସାନ୍ତ୍ଵନା ଦିଅ ।"] },
  "ନ୍ଦ୍ର": { wordString: "ଚନ୍ଦ୍ର, ଇନ୍ଦ୍ର", sentences: ["ରାତି ଆକାଶରେ ଚନ୍ଦ୍ର ଦେଖାଯାଏ ।"] },
  "ନ୍ଦ୍ବ": { wordString: "ଦ୍ୱନ୍ଦ୍ୱ", sentences: ["ମନରୁ ସବୁ ଦ୍ୱନ୍ଦ୍ୱ ଦୂର କର ।"] },
  "ନ୍ଧ୍ର": { wordString: "ରନ୍ଧ୍ର", sentences: ["ଆମ ଚର୍ମରେ ଛୋଟ ଛୋଟ ଲୋମ ରନ୍ଧ୍ର ଥାଏ ।"] },
  "ନ୍ଧ୍ୟ": { wordString: "ସନ୍ଧ୍ୟା", sentences: ["ସନ୍ଧ୍ୟା ସମୟରେ ତୁଳସୀ ଚଉରା ମୂଳେ ଦୀପ ଜଳାଯାଏ ।"] },
  "ନ୍ନ୍ୟ": { wordString: "ସନ୍ନ୍ୟାସୀ", sentences: ["ସନ୍ନ୍ୟାସୀ ହିମାଳୟରେ ତପସ୍ୟା କରନ୍ତି ।"] },
  "ମ୍ପ୍ର": { wordString: "ସମ୍ପ୍ରଦାୟ", sentences: ["ସବୁ ସମ୍ପ୍ରଦାୟର ଲୋକ ମିଳିମିଶି ରହିବା ଉଚିତ୍ ।"] },
  "ମ୍ଭ୍ର": { wordString: "ସମ୍ଭ୍ରାନ୍ତ", sentences: ["ସେ ଏକ ସମ୍ଭ୍ରାନ୍ତ ପରିବାରର ଲୋକ ।"] },
  "ର୍ଚ୍ଚ": { wordString: "ଚର୍ଚ୍ଚା, ଅର୍ଚ୍ଚନା", sentences: ["ଗାଁରେ ମେଳା ବିଷୟରେ ଚର୍ଚ୍ଚା ହେଉଛି ।"] },
  "ର୍ଚ୍ଛ": { wordString: "ମୂର୍ଚ୍ଛା", sentences: ["ଅତ୍ୟଧିକ ଗରମ ଯୋଗୁଁ ସେ ମୂର୍ଚ୍ଛା ହୋଇଗଲେ ।"] },
  "ର୍ଣ୍ଣ": { wordString: "ବର୍ଣ୍ଣ, ପୂର୍ଣ୍ଣ", sentences: ["ଓଡ଼ିଆ ଭାଷାରେ ଅନେକ ବର୍ଣ୍ଣ ଅଛି ।"] },
  "ର୍ତ୍ତ": { wordString: "ଗର୍ତ୍ତ, ନର୍ତ୍ତକୀ", sentences: ["ସେ ଜଣେ ଭଲ ନର୍ତ୍ତକୀ ।"] },
  "ର୍ଦ୍ଧ": { wordString: "ଅର୍ଦ୍ଧ", sentences: ["ମହାଦେବ ଅର୍ଦ୍ଧ ଚନ୍ଦ୍ରକୁ ନିଜ ଜଟାରେ ଧାରଣ କରଛନ୍ତି ।"] },
  "ର୍ଦ୍ଦ": { wordString: "ମାର୍ଦ୍ଦଳ", sentences: ["ଓଡ଼ିଶୀ ନୃତ୍ୟ ପରିବେଷଣ ସମୟରେ ମାର୍ଦ୍ଦଳର ତାଳ ଏକ ସ୍ୱତନ୍ତ୍ର ମାଧୁର୍ଯ୍ୟ ସୃଷ୍ଟି କରିଥାଏ ।"] },
  "ର୍ତ୍ସ": { wordString: "ଭର୍ତ୍ସନା", sentences: ["ଭୁଲ କାମ ପାଇଁ ଗୁରୁ ଭର୍ତ୍ସନା କଲେ ।"] },
  "ର୍ଦ୍ର": { wordString: "ଆର୍ଦ୍ର", sentences: ["ବର୍ଷା ଦିନେ ପାଗ ଆର୍ଦ୍ର ଥାଏ ।"] },
  "ର୍ଯ୍ୟ": { wordString: "ସୂର୍ଯ୍ୟ, କାର୍ଯ୍ୟ", sentences: ["ସୂର୍ଯ୍ୟ ପୂର୍ବ ଦିଗରେ ଉଦୟ ହୁଅନ୍ତି ।"] },
  "ର୍ଶ୍ଵ": { wordString: "ପାର୍ଶ୍ଵ", sentences: ["ରାସ୍ତାର ବାମ ପାର୍ଶ୍ଵରେ ଚାଲିବା ଉଚିତ୍ ।"] },
  "ଷ୍କ୍ର": { wordString: "ନିଷ୍କ୍ରିୟ", sentences: ["ଯନ୍ତ୍ରଟି ନିଷ୍କ୍ରିୟ ହୋଇ ପଡିଛି ।"] },
  "ଷ୍ଟ୍ର": { wordString: "ରାଷ୍ଟ୍ର", sentences: ["ଭାରତ ଆମର ରାଷ୍ଟ୍ର ଅଟେ ।"] },
  "ଷ୍ପ୍ର": { wordString: "ଦୁଷ୍ପ୍ରାପ୍ୟ", sentences: ["ଏହି ଔଷଧ ବହୁତ ଦୁଷ୍ପ୍ରାପ୍ୟ ଅଟେ ।"] },
  "ସ୍ଥ୍ୟ": { wordString: "ସ୍ୱାସ୍ଥ୍ୟ", sentences: ["ସ୍ୱାସ୍ଥ୍ୟ ହିଁ ସମ୍ପଦ ।"] },
  "ନ୍ତ୍ର୍ୟ": { wordString: "ସ୍ୱାତନ୍ତ୍ର୍ୟ", sentences: ["ଓଡ଼ିଶାର କଳାର ସ୍ୱାତନ୍ତ୍ର୍ୟ ରହିଛି ।"] },
  "ର୍ତ୍ତ୍ୟ": { wordString: "ମର୍ତ୍ତ୍ୟ", sentences: ["ମର୍ତ୍ତ୍ୟ ମଣ୍ଡଳେ ଦେହ ବହି, ଦେବତା ହେଲେ ମରଇ ।"] },
  "ର୍ତ୍ତ୍ର": { wordString: "କର୍ତ୍ତ୍ରୀ", sentences: ["ସେ ହେଉଛନ୍ତି ଘରର କର୍ତ୍ତ୍ରୀ ।"] },
  "ର୍ଦ୍ଧ୍ୱ": { wordString: "ଊର୍ଦ୍ଧ୍ୱ", sentences: ["ପକ୍ଷୀଟି ଆକାଶର ଊର୍ଦ୍ଧ୍ୱକୁ ଉଡିଗଲା ।"] },

  // === ଫଳା -- PHALA
  "୍ୟ": { wordString: "ସୂର୍ଯ୍ୟ, ସତ୍ୟ", sentences: ["ସୂର୍ଯ୍ୟ ପୂର୍ବ ଦିଗରେ ଉଦୟ ହୁଅନ୍ତି ।", "ସଦା ସର୍ବଦା ସତ୍ୟ କୁହ ।"] },
  "୍ର": { wordString: "ଗ୍ରାମ, ଚକ୍ର", sentences: ["ଆମ ଗ୍ରାମର ନାମ ମହୁଲି ।", "ଜାତୀୟ ପତାକାରେ ଅଶୋକ ଚକ୍ର ଅଛି ।"] },
  "୍ୱ": { wordString: "ଦ୍ୱାର, ସ୍ୱାଧୀନ", sentences: ["ମନ୍ଦିରର ଦ୍ୱାର ଖୋଲା ଅଛି ।", "ଭାରତ ଏକ ସ୍ୱାଧୀନ ଦେଶ ।"] },
  "୍ମ": { wordString: "ପଦ୍ମ, ଜନ୍ମ", sentences: ["ପଦ୍ମ ଆମର ଜାତୀୟ ଫୁଲ ।"] },
  "୍ନ": { wordString: "ରତ୍ନ, ଯତ୍ନ", sentences: ["ପରିଶ୍ରମ ହିଁ ପ୍ରକୃତ ରତ୍ନ ।", "ଶରୀରର ଯତ୍ନ ନେବା ଉଚିତ୍ ।"] },
  "୍ଳ": { wordString: "ଶୁକ୍ଳ, ବିପ୍ଳବ", sentences: ["ଅମାବାସ୍ୟା ରୁ ପୂର୍ଣ୍ଣମୀ ଯାଏ ଶୁକ୍ଳପକ୍ଷ ।"] },
  "୍ଲ": { wordString: "ପଲ୍ଲୀ", sentences: ["ଆମ ପଲ୍ଲୀ ବହୁତ ସୁନ୍ଦର ।"] }
};

export function getCharDetails(char: string): CharDetail {
  const data = odiaDictionary[char];
  if (!data) {
    return { words: ["ଉଦାହରଣ ନାହିଁ"], sentences: ["ଉଦାହରଣ ନାହିଁ"] };
  }
  const wordsList = data.wordString.split(",").map((w) => w.trim());
  return {
    words: wordsList,
    sentences: data.sentences
  };
}

// --- SCHOOL BOOKS DATA (Classes 1 to 10) ---
export interface SchoolBookData {
  id: string;
  title: string;
  url: string;
}

export const schoolClassesList: { title: string; screen: Screen; num: string }[] = [
  { title: "ପ୍ରଥମ ଶ୍ରେଣୀ", screen: Screen.CLASS_1, num: "୧" },
  { title: "ଦ୍ଵିତୀୟ ଶ୍ରେଣୀ", screen: Screen.CLASS_2, num: "୨" },
  { title: "ତୃତୀୟ ଶ୍ରେଣୀ", screen: Screen.CLASS_3, num: "୩" },
  { title: "ଚତୁର୍ଥ ଶ୍ରେଣୀ", screen: Screen.CLASS_4, num: "୪" },
  { title: "ପଞ୍ଚମ ଶ୍ରେଣୀ", screen: Screen.CLASS_5, num: "୫" },
  { title: "ଷଷ୍ଠ ଶ୍ରେଣୀ", screen: Screen.CLASS_6, num: "୬" },
  { title: "ସପ୍ତମ ଶ୍ରେଣୀ", screen: Screen.CLASS_7, num: "୭" },
  { title: "ଅଷ୍ଟମ ଶ୍ରେଣୀ", screen: Screen.CLASS_8, num: "୮" },
  { title: "ନବମ ଶ୍ରେଣୀ", screen: Screen.CLASS_9, num: "୯" },
  { title: "ଦଶମ ଶ୍ରେଣୀ", screen: Screen.CLASS_10, num: "୧୦" },
];

export const schoolBooksByClass: Record<string, SchoolBookData[]> = {
  [Screen.CLASS_1]: [
    { id: "class1_jhulana2", title: "ଝୁଲଣା - ୧ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Jhulana_Odia_Cl-I-P_1-184_Web_DRAFT_1_11zon.pdf" },
    { id: "class1_ganitakhela2", title: "ଗଣିତ ଖେଳ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Math_Class_1_Web(1)_11zon.pdf" },
    { id: "class1_jhulana1", title: "ଝୁଲଣା - ୧ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.I.-.Jhulana.pdf" },
    { id: "class1_ganitakhela", title: "ଗଣିତ ଖେଳ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.I.-.Math.pdf" },
    { id: "class1_hasakhela_1", title: "ହସଖେଳ (ପ୍ରଥମ ଭାଗ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Hasakhela-P-I-(Cl-I)-P-1-104.pdf" },
    { id: "class1_hasakhela_2", title: "ହସଖେଳ (ଦ୍ଵିତୀୟ ଭାଗ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Haso-Khelo-Part-II-112.pdf" },
    { id: "class1_sikhara_1", title: "ଶିଖର - ୧", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-I/Sikhara-1.pdf" }
  ],
  [Screen.CLASS_2]: [
    { id: "class2_jhulana3", title: "ଝୁଲଣା - ୨ (ସଂଶୋଧିତ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.II.-.Jhulana-2.pdf" },
    { id: "class2_majamajareganita3", title: "ମଜା ମଜାରେ ଗଣିତ (ସଂଶୋଧିତ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.II.-.Maja.Majare.Ganita.pdf" },
    { id: "class2_jhulana2", title: "ଝୁଲଣା - ୨ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.II.-.Jhulana-2.pdf" },
    { id: "class2_majamajareganita", title: "ମଜା ମଜାରେ ଗଣିତ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.II.-.Maja.Majare.Ganita.pdf" },
    { id: "class2_ganitakhela", title: "ଗଣିତ ଖେଳ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-II/Ganita-Khela-Cl-II-P-1-152.pdf" },
    { id: "class2_hasakhusi", title: "ହସଖୁସି", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-II/Hasakhusi-Cl-II-P-1-184.pdf" },
    { id: "class2_sikhara_2", title: "ଶିଖର - ୨", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-II/Sikhara-2.pdf" }
  ],
  [Screen.CLASS_3]: [
    { id: "class3_bhasha_mahaka_11", title: "ଭାଷା ମହକ ୧ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Class-3_Bhasa_Mahaka_11zon.pdf" },
    { id: "class3_bichitra_ama_pruthibi1", title: "ବିଚିତ୍ର ଆମ ପୃଥିବୀ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Bichitra_Pruthibi-III-P_1-176_Web_DRAFT_11zon.pdf" },
    { id: "class3_ganita_mela1", title: "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Ganita_Mela-3-Final-216%20-Class-III-Web_11zon.pdf" },
    { id: "class3_indradhanu1", title: "ଇନ୍ଦ୍ରଧନୁ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Class-3_Indradhanu(1)_11zon.pdf" },
    { id: "class3_khela_o_joga1", title: "ଖେଳ ଓ ଯୋଗ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Khel_Yogo-III_Inn-P_1-144_Web_DRAFT_11zon.pdf" },
    { id: "class3_pallavi1", title: "Pallavi (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi-III_Eng-Final_168_Web_DRAFT_11zon.pdf" },
    { id: "class3_bhasha_mahaka_1", title: "ଭାଷା ମହକ ୧ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Bhasa.MahakOdia.pdf" },
    { id: "class3_bichitra_ama_pruthibi", title: "ବିଚିତ୍ର ଆମ ପୃଥିବୀ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Bichitra.Ama.Pruthibi.pdf" },
    { id: "class3_ganita_mela", title: "ଗଣିତ ମେଳା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.GANITA.MELA-3.pdf" },
    { id: "class3_indradhanu", title: "ଇନ୍ଦ୍ରଧନୁ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.INDRADHANU-3.pdf" },
    { id: "class3_khela_o_joga", title: "ଖେଳ ଓ ଯୋଗ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.KHELA.O.YOGA.pdf" },
    { id: "class3_pallavi", title: "Pallavi (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%A7-%E0%AD%A9/Class.III.-.Pallavi.English.pdf" },
    { id: "class3_ganitamaja", title: "ଗଣିତ ମଜା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Ganita-Moja-III-Inner-P-100.pdf" },
    { id: "class3_amabhasa", title: "ଆମ ଭାଷା ଆମ ସାହିତ୍ୟ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Amo-Bhasa-Amo-Sahitya-III-112-Pagee.pdf" },
    { id: "class3_janaajana", title: "ଜଣା ଅଜଣା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/Jana-Ajana-III-Inner-P-1-146page.pdf" },
    { id: "class3_english", title: "MY ENGLISH BOOK", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-III/My-English-Book-III-Final.pdf" }
  ],
  [Screen.CLASS_4]: [
    { id: "class4_bhasha_mahaka_21", title: "ଭାଷା ମହକ ୨ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/BHASA-MAHAKA-CLASS-4-S-DRAFT_11zon.pdf" },
    { id: "class4_ama_bichitra_biswa1", title: "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Our_Wondrous-IV-P_1-176_Web_DRAFT_11zon.pdf" },
    { id: "class4_ganita_mela1", title: "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Mathe_Mela-Final-240-Class-4-Web_11zon.pdf" },
    { id: "class4_indradhanu1", title: "ଇନ୍ଦ୍ରଧନୁ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Indradhanu_Class-4_11zon.pdf" },
    { id: "class4_krida_o_joga1", title: "କ୍ରିଡ଼ା ଓ ଯୋଗ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Physcial_Class_4_Final_Web_2_11zon.pdf" },
    { id: "class4_pallavi1", title: "Pallavi (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi_English_Class-4_11zon.pdf" },
    { id: "class4_ama_bichitra_biswa", title: "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Ama.Bichitra.Biswa.pdf" },
    { id: "class4_bhasha_mahaka_2", title: "ଭାଷା ମହକ ୨ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Bhasa.Mahak.pdf" },
    { id: "class4_ganita_mela", title: "ଗଣିତ ମେଳା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.GANITA.MELAMath.pdf" },
    { id: "class4_indradhanu", title: "ଇନ୍ଦ୍ରଧନୁ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Indradhanu.pdf" },
    { id: "class4_krida_o_joga", title: "କ୍ରିଡ଼ା ଓ ଯୋଗ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Krida.o.Yoga.pdf" },
    { id: "class4_pallavi", title: "Pallavi (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.IV.-.Pallavi.English.pdf" },
    { id: "class4_ganitakuhuka", title: "ଗଣିତ କୁହୁକ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Ganita-Kuhuka-IV-P-1-208.pdf" },
    { id: "class4_sahityakalika", title: "ସାହିତ୍ୟ କଳିକା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Sahitya-Kalika-IV-P-1-112.pdf" },
    { id: "class4_ameoamaparibesa", title: "ଆମେ ଓ ଆମ ପରିବେଶ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/Ame-O-Ama-Paribesa-IV-P-1-208.pdf" },
    { id: "class4_english", title: "MY ENGLISH BOOK", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IV/English-IV-P-1-136.pdf" }
  ],
  [Screen.CLASS_5]: [
    { id: "class5_bhashamahaka_31", title: "ଭାଷାମହକ ୩ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/bhasa-mahak-v.pdf" },
    { id: "class5_ama_bichitra_biswa1", title: "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/our-earth-v.pdf" },
    { id: "class5_khela_o_joga1", title: "ଖେଳ ଓ ଯୋଗ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/play-v.pdf" },
    { id: "class5_ganita_mela1", title: "ଗଣିତ ମେଳା (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/math-v.pdf" },
    { id: "class5_nabarasa1", title: "ନବରସ (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/nabaras-v.pdf" },
    { id: "class5_pallavi1", title: "Pallavi (ସଂଶୋଧିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/revised_books/Pallavi_(Class-V).pdf" },
    { id: "class5_bhashamahaka_3", title: "ଭାଷାମହକ ୩ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.BHASHA.MAHAKOdia.pdf" },
    { id: "class5_ama_bichitra_biswa", title: "ଆମ ବିଚିତ୍ର ବିଶ୍ଵ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Ama.Bichitra.Biswa.pdf" },
    { id: "class5_khela_o_joga", title: "ଖେଳ ଓ ଯୋଗ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Khela.o.joga.pdf" },
    { id: "class5_ganita_mela", title: "ଗଣିତ ମେଳା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Ganita.MelaMath.pdf" },
    { id: "class5_nabarasa", title: "ନବରସ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.Nabarasa.pdf" },
    { id: "class5_pallavi", title: "Pallavi (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AA-%E0%AD%AB/Class.V.-.ENGLISH.PALLAVI.pdf" },
    { id: "class5_sahityakusum", title: "ସାହିତ୍ୟ କୁସୁମ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Sahitya-Kusum-Cl-V-(B)-P-1-144.pdf" },
    { id: "class5_ganitadhara", title: "ଗଣିତ ଧାରା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Ganita-Dhara-V-P-1-216.pdf" },
    { id: "class5_english", title: "MY ENGLISH BOOK", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/English-V-P-1-128.pdf" },
    { id: "class5_paribesabigyan", title: "ପରିବେଶ ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-V/Paribesa-Bigyan-V-P-1-208.pdf" }
  ],
  [Screen.CLASS_6]: [
    { id: "class6_sahitya_sudha", title: "ସାହିତ୍ୟସୁଧା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Sahitya.Sudha.pdf" },
    { id: "class6_ganita_prakasha", title: "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Math.pdf" },
    { id: "class6_samajika_bigyana", title: "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Social.Science.pdf" },
    { id: "class6_kaushala_bodha", title: "କୌଶଳ ବୋଧ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Koshalbodha.pdf" },
    { id: "class6_khela_shikshya", title: "ଖେଳ ଶିକ୍ଷା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Khela.Sahitya.pdf" },
    { id: "class6_kalakunja", title: "କଳାକୁଞ୍ଜ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Kalakunja.pdf" },
    { id: "class6_jignyasa", title: "ଜିଜ୍ଞାସା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Jingyasha.pdf" },
    { id: "class6_jasmine", title: "Jasmine (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.English.Jasmine.pdf" },
    { id: "class6_sanskrit_kalika", title: "संस्कृतकलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Sanaskruta.Kalika.1.pdf" },
    { id: "class6_hindi_kalika", title: "हिन्दी कलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AC/Class.VI.-.Hindi.Kalika.pdf" },
    { id: "class6_amasahitya", title: "ଆମ ସାହିତ୍ୟ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Ama-Sahitya-Page-(-1-120-)-pg.pdf" },
    { id: "class6_ganita", title: "ଗଣିତ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Math-Page-6-(1-240-).pdf" },
    { id: "class6_bigyana", title: "ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Science-(-1-104-)-pg.pdf" },
    { id: "class6_bhugola", title: "ଭୂଗୋଳ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Geography-Vi-104.pdf" },
    { id: "class6_itihasa", title: "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/History_Final_136.pdf" },
    { id: "class6_computer", title: "କମ୍ପ୍ୟୁଟର ଶିକ୍ଷା ଓ ପ୍ରୟୋଗ (ପ୍ରଥମ ଭାଗ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Computer-Sikshya-Part-1-(1-88page).pdf" },
    { id: "class6_english", title: "MY ENGLISH BOOK", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Eng-Vi-Page-1-152.pdf" },
    { id: "class6_bhasakalika", title: "ଭାଷା କଳିକା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VI/Bhasa-Kalika-VI-1-144-pg.pdf" }
  ],
  [Screen.CLASS_7]: [
    { id: "class7_sahityasumana", title: "ସାହିତ୍ୟସୁମନ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Odia.pdf" },
    { id: "class7_jignyasa", title: "ଜିଜ୍ଞାସା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Jingyasha.pdf" },
    { id: "class7_samajika_bigyana", title: "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Social.Science.pdf" },
    { id: "class7_khela_shikshya", title: "ଖେଳ ଶିକ୍ଷା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Khela.Shikhya.pdf" },
    { id: "class7_kaushala_bodha", title: "କୌଶଳ ବୋଧ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Koshalbodha.pdf" },
    { id: "class7_ganita_prakasha", title: "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Ganita.Prakash.pdf" },
    { id: "class7_kalakruti", title: "କଳାକୃତି (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Kalakruti.pdf" },
    { id: "class7_jasmine", title: "Jasmine (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII.-.JASMINE-ENGLISH.pdf" },
    { id: "class7_sanskrit_kalika", title: "संस्कृतकलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII-Sanskruta.Kalika.2.pdf" },
    { id: "class7_hindi_kalika", title: "हिन्दी कलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AD/Class.VII.-.Hindi.Kalika.2.pdf" },
    { id: "class7_sahityasaurabh", title: "ସାହିତ୍ୟ ସୌରଭ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Sahitya-Saurav-VII-P-1-160_compressed.pdf" },
    { id: "class7_ganita", title: "ଗଣିତ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Mathematics-VII-P-1-248_compressed.pdf" },
    { id: "class7_itihasa", title: "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Hist-&-Pol-VII-P-1-128_compressed.pdf" },
    { id: "class7_bhugola", title: "ଭୂଗୋଳ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Geography-VII-P-1-120_compressed.pdf" },
    { id: "class7_bigyana", title: "ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Science-VII-P-1-228_compressed.pdf" },
    { id: "class7_english", title: "ENGLISH READER", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/English-VII-P-1-144_compressed.pdf" },
    { id: "class7_bhasakalika2", title: "ଭାଷା କଳିକା ଭାଗ - ୨", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Bhasa-Kalika-VII-P-1-112_compressed.pdf" },
    { id: "class7_computer2", title: "କମ୍ପ୍ୟୁଟର ଶିକ୍ଷା ଓ ପ୍ରୟୋଗ (ଦ୍ଵିତୀୟ ଭାଗ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VII/Computer-Book-1-120page_compressed.pdf" }
  ],
  [Screen.CLASS_8]: [
    { id: "class8_sahitya_surabhi", title: "ସାହିତ୍ୟସୁରଭି (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Sahitya.Surabhi.pdf" },
    { id: "class8_ganita_prakasha", title: "ଗଣିତ ପ୍ରକାଶ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Math.pdf" },
    { id: "class8_jignyasa", title: "ଜିଜ୍ଞାସା (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Jingyasha.Science.pdf" },
    { id: "class8_samajika_bigyana", title: "ସାମାଜିକ ବିଜ୍ଞାନ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Samajika.Bingyana.pdf" },
    { id: "class8_kruti", title: "କୃତି (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.KRUTI.pdf" },
    { id: "class8_kaushala_bodha", title: "କୌଶଳ ବୋଧ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Koushalabodha.pdf" },
    { id: "class8_jasmine", title: "Jasmine (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Jasmine.English.pdf" },
    { id: "class8_sanskrit_kalika", title: "संस्कृतकलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.SANSKRUTA.KALIKA.pdf" },
    { id: "class8_hindi_kalika", title: "हिन्दी कलिका (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AE/Class.VIII.-.Hindi.Kalika.pdf" },
    { id: "class8_sahitika", title: "ସାହିତ୍ୟିକା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sahitika-8th.pdf" },
    { id: "class8_odiabyakarana", title: "ଆମ ଓଡ଼ିଆ ବ୍ୟାକରଣ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Ama-Odia-Byakarana-Final-104.pdf" },
    { id: "class8_saralasahitya", title: "ସରଳ ସାହିତ୍ୟ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Sahitya-VIII-P-1-104.pdf" },
    { id: "class8_bigyana", title: "ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Science-(-1-240-)-pg.pdf" },
    { id: "class8_bijaganita", title: "ସରଳ ଗଣିତ (ବୀଜ ଗଣିତ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Bija-Ganita-(-1-192-)-pg.pdf" },
    { id: "class8_geometry", title: "ସରଳ ଗଣିତ (ଜ୍ୟାମିତି)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Sarala-Geometry.pdf" },
    { id: "class8_bhugola", title: "ଭୂଗୋଳ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Geography.pdf" },
    { id: "class8_historypolitics", title: "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/History-&-Political-Science-8.pdf" },
    { id: "class8_newapproachenglish", title: "A New Approach to English", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/A-New-Approach-to-English-(-1-168-).pdf" },
    { id: "class8_storiespastpresent", title: "Stories Past & Present", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Stories-Past-and-Present.pdf" },
    { id: "class8_bhasasouravam", title: "ଭାଷା ସୌରଭମ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Bhasa-Souravam-8.pdf" },
    { id: "class8_hindipatha", title: "ହିନ୍ଦୀ ପାଠ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Hindi-Patha-VIII-P-1-120.pdf" },
    { id: "class8_saralahindibyakarana", title: "ସରଳ ହିନ୍ଦୀ ବ୍ୟାକରଣ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-VIII/Saral-Hindi-Byakaran-Class-8.pdf" }
  ],
  [Screen.CLASS_9]: [
    { id: "class9_sahityadhara", title: "ସାହିତ୍ୟ ଧାରା", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/77%20-%20OD_ODIA_MIL_CLASS9_TERM1.pdf" },
    { id: "class9_madhyamikabyakarana", title: "ମାଧ୍ୟମିକ ବ୍ୟାକରଣ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/78%20-%20OD_ODIA_ODIYAGRAMMAR_CLASS9_TERM1.pdf" },
    { id: "class9_bijaganita", title: "ମାଧ୍ୟମିକ ବୀଜଗଣିତ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/85%20-%20OD_ODIA_MATH_CLASS9_TERM1.pdf" },
    { id: "class9_geometry", title: "ମାଧ୍ୟମିକ ଜ୍ୟାମିତି", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/86%20-%20OD_ODIA_GEOMETRY_CLASS9_TERM1.pdf" },
    { id: "class9_physicalscience", title: "ଭୌତିକ ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/87%20-%20OD_ODIA_PHYSICALSCIENCE_CLASS9_TERM1.pdf" },
    { id: "class9_lifescience", title: "ଜୀବ ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/88%20-%20OD_ODIA_LIFESCIENCE_CLASS9_TERM1.pdf" },
    { id: "class9_historypolitics", title: "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/89%20-%20OD_ODIA_HISTORYCIVICS_CLASS9_TERM1.pdf" },
    { id: "class9_geoeco", title: "ଭୂଗୋଳ ଓ ଅର୍ଥନୀତି", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/90%20-%20OD_ODIA_GEOGRAPHYECONOMICS_CLASS9_TERM1.pdf" },
    { id: "class9_englishgrammar", title: "Learn & Practice Grammer", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/79%20-%20OD_ENGLISH_ENGLISHGRAMMAR_CLASS9_TERM1.pdf" },
    { id: "class9_communicativeenglish", title: "Skills of Communicative English", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/80%20-%20OD_ENGLISH_ENGLISH_CLASS9_TERM1.pdf" },
    { id: "class9_sanskritavallari", title: "संस्कृतवल्लरी", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/81%20-%20OD_SANSKRIT_SANSKRITLANGUAGE_CLASS9_TERM1.pdf" },
    { id: "class9_sanskritgrammar", title: "ସଂସ୍କୃତ ବ୍ୟାକରଣ ପ୍ରଦୀପଃ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/82%20-%20OD_ODIA_SANSKRITGRAMMAR_CLASS9_TERM1.pdf" },
    { id: "class9_hindimanjari", title: "हिन्दी मञ्जरी", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/83%20-%20OD_HINDI_HINDI_CLASS9_TERM1.pdf" },
    { id: "class9_hindigrammar", title: "हिन्दी व्याकरण और प्रयोग", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-IX/84%20-%20OD_HINDI_HINDIGRAMMAR_CLASS9_TERM1.pdf" },
    { id: "class9_paryatana_o_atithya_parichaya", title: "ପର୍ଯ୍ୟଟନ ଓ ଆତିଥ୍ୟ ପରିଚୟ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.IX.-.Vocational.Tourism.pdf" },
    { id: "class9_motor_jana_karigara", title: "ମୋଟରଯାନ କାରିଗର (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.IX.-.Vocational.Automotive.pdf" },
    { id: "class9_electronics_ebang_hardware", title: "ଇଲେଟ୍ରୋନିକ୍ସ ଏବଂ ହାର୍ଡୱୟାର (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.IX.-.Vocational.Electronics.pdf" },
    { id: "class9_shikhya_setu_ganita_ingraji", title: "ଶିକ୍ଷା ସେତୁ ଗଣିତ ଇଂରାଜୀ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.IX.-.Bridge.Course.Math.ENG.pdf" }
  ],
  [Screen.CLASS_10]: [
    { id: "class10_sahityasindhu", title: "ସାହିତ୍ୟ ସିନ୍ଧୁ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-01A-MIL_Odia_Class_X.pdf" },
    { id: "class10_madhyamikabyakarana", title: "ମାଧ୍ୟମିକ ବ୍ୟାକରଣ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/92%20-%20OD_ODIA_ODIAGRAMMAR_CLASS10_TERM1.pdf" },
    { id: "class10_communicativeenglish", title: "Skills of Communicative English", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-02A-English_Text_Class_X.pdf" },
    { id: "class10_englishgrammar", title: "Learn & Practice Grammer", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/94%20-%20OD_ENGLISH_ENGLISHGRAMMAR_CLASS10_TERM1.pdf" },
    { id: "class10_sanskritasahitya", title: "ସଂସ୍କୃତ ସାହିତ୍ୟ ମଞ୍ଜରୀ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/95%20-%20OD_SANSKRIT_SANSKRITLANGUAGE_CLASS10_TERM1.pdf" },
    { id: "class10_sanskritgrammar", title: "ସଂସ୍କୃତ ବ୍ୟାକରଣ ପ୍ରକାଶଃ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/96%20-%20OD_ODIA_SANSKRITGRAMMAR_CLASS10_TERM1.pdf" },
    { id: "class10_bijaganita", title: "ମାଧ୍ୟମିକ ବୀଜଗଣିତ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/99%20-%20OD_ODIA_MATH_CLASS10_TERM1.pdf" },
    { id: "class10_geometry", title: "ମାଧ୍ୟମିକ ଜ୍ୟାମିତି", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/100%20-%20OD_ODIA_GEOMETRY_CLASS10_TERM1.pdf" },
    { id: "class10_physicalscience", title: "ଭୌତିକ ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/101%20-%20OD_ODIA_PHYSICALSCIENCE_CLASS10_TERM1.pdf" },
    { id: "class10_lifescience", title: "ଜୀବ ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/102%20-%20OD_ODIA_LIFESCIENCE_CLASS10_TERM1.pdf" },
    { id: "class10_historypolitics", title: "ଇତିହାସ ଓ ରାଜନୀତି ବିଜ୍ଞାନ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/103%20-%20OD_ODIA_HISTORYCIVICS_CLASS10_TERM1.pdf" },
    { id: "class10_geoeco", title: "ଭୂଗୋଳ ଓ ଅର୍ଥନୀତି", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/104%20-%20OD_ODIA_GEOGRAPHYECONOMICS_CLASS10_TERM1.pdf" },
    { id: "class10_geoeco_addl", title: "ଭୂଗୋଳ ଓ ଅର୍ଥନୀତି (ଅତିରିକ୍ତ ପାଠ୍ୟକ୍ରମ)", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/10th-06B-Geography(addl)%20Class%20X.pdf" },
    { id: "class10_hindisourabh", title: "हिन्दी सौरभ", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/97%20-%20OD_HINDI_HINDI_CLASS10_TERM1.pdf" },
    { id: "class10_hindigrammar", title: "माध्यमिक हिन्दी व्याकरण", url: "https://osepa.odisha.gov.in/upload/ebooks/CLASS-X/98%20-%20OD_HINDI_HINDIGRAMMAR_CLASS10_TERM1.pdf" },
    { id: "class10_paryatana_o_atithyara_parichaya", title: "ପର୍ଯ୍ୟଟନ ଓ ଆତିଥ୍ୟର ପରିଚୟ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Tourism.pdf" },
    { id: "class10_krushi_bigyana", title: "କୃଷି ବିଜ୍ଞାନ (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Agriculture.pdf" },
    { id: "class10_automotive", title: "AUTOMOTIVE (ନୂତନ)", url: "https://github.com/odiaschoolbooks/odiabooks/releases/download/%E0%AD%AF-%E0%AD%A7%E0%AD%A6/Class.X.-.Vocational.Automotive.pdf" }
  ]
};

// --- ORDINALS DATA (Unchanged from OrdinalsScreen.kt) ---
export interface OrdinalEntry {
  number: string;
  word: string;
  symbol: string;
}

const ordinalNumbers = ["୧", "୨", "୩", "୪", "୫", "୬", "୭", "୮", "୯", "୧୦", "୧୧", "୧୨", "୧୩", "୧୪", "୧୫", "୧୬", "୧୭", "୧୮", "୧୯", "୨୦", "୨୧", "୨୨", "୨୩", "୨୪", "୨୫", "୨୬", "୨୭", "୨୮", "୨୯", "୩୦", "୩୧", "୩୨", "୩୩", "୩୪", "୩୫", "୩୬", "୩୭", "୩୮", "୩୯", "୪୦", "୪୧", "୪୨", "୪୩", "୪୪", "୪୫", "୪୬", "୪୭", "୪୮", "୪୯", "୫୦", "୫୧", "୫୨", "୫୩", "୫୪", "୫୫", "୫୬", "୫୭", "୫୮", "୫୯", "୬୦", "୬୧", "୬୨", "୬୩", "୬୪", "୬୫", "୬୬", "୬୭", "୬୮", "୬୯", "୭୦", "୭୧", "୭୨", "୭୩", "୭୪", "୭୫", "୭୬", "୭୭", "୭୮", "୭୯", "୮୦", "୮୧", "୮୨", "୮୩", "୮୪", "୮୫", "୮୬", "୮୭", "୮୮", "୮୯", "୯୦", "୯୧", "୯୨", "୯୩", "୯୪", "୯୫", "୯୬", "୯୭", "୯୮", "୯୯", "୧୦୦", "୨୦୦", "୩୦୦", "୪୦୦", "୫୦୦", "୬୦୦", "୭୦୦", "୮୦୦", "୯୦୦", "୧୦୦୦", "୧୦୦୦୦୦"];
const ordinalWords = ["ପ୍ରଥମ", "ଦ୍ୱିତୀୟ", "ତୃତୀୟ", "ଚତୁର୍ଥ", "ପଞ୍ଚମ", "ଷଷ୍ଠ", "ସପ୍ତମ", "ଅଷ୍ଟମ", "ନବମ", "ଦଶମ", "ଏକାଦଶ", "ଦ୍ୱାଦଶ", "ତ୍ରୟୋଦଶ", "ଚତୁର୍ଦ୍ଦଶ", "ପଞ୍ଚଦଶ", "ଷୋଡ଼ଶ", "ସପ୍ତଦଶ", "ଅଷ୍ଟାଦଶ", "ଊନବିଂଶ", "ବିଂଶ", "ଏକବିଂଶ", "ଦ୍ୱାବିଂଶ", "ତ୍ରୟୋବିଂଶ", "ଚତୁବିଂଶ", "ପଞ୍ଚବିଂଶ", "ଷଡବିଂଶ", "ସପ୍ତବିଂଶ", "ଅଷ୍ଟବିଂଶ", "ଊନତ୍ରିଂଶ", "ତ୍ରିଂଶ", "ଏକତ୍ରିଂଶ", "ଦ୍ୱାତ୍ରିଂଶ", "ତ୍ରୟତ୍ରିଂଶ", "ଚତୁତ୍ରିଂଶ", "ପଞ୍ଚତ୍ରିଂଶ", "ଷଟ୍ ତ୍ରିଂଶ", "ସପ୍ତତ୍ରିଂଶ", "ଅଷ୍ଟତ୍ରିଂଶ", "ଊନଚତ୍ୱାରିଂଶ", "ଚତ୍ୱାରିଂଶ", "ଏକଚତ୍ୱାରିଂଶ", "ଦ୍ୱିଚତ୍ୱାରିଂଶ", "ତ୍ରିଚତ୍ୱାରିଂଶ", "ଚତୁଃଚତ୍ୱାରିଂଶ", "ପଞ୍ଚଚତ୍ୱାରିଂଶ", "ଷଟ୍ ଚତ୍ୱାରିଂଶ", "ସପ୍ତଚତ୍ୱାରିଂଶ", "ଅଷ୍ଟଚତ୍ୱାରିଂଶ", "ଊନପଞ୍ଚାଶତ୍", "ପଞ୍ଚାଶତ୍", "ଏକପଞ୍ଚାଶତ୍", "ଦ୍ୱିପଞ୍ଚାଶତ୍", "ତ୍ରିପଞ୍ଚାଶତ୍", "ଚତୁପଞ୍ଚାଶତ୍", "ପଞ୍ଚପଞ୍ଚାଶତ୍", "ଷଡପଞ୍ଚାଶତ୍", "ସପ୍ତପଞ୍ଚାଶତ୍", "ଅଷ୍ଟପଞ୍ଚାଶତ୍", "ଊନଷଷ୍ଠୀତମ", "ଷଷ୍ଠୀତମ", "ଏକଷଷ୍ଠୀତମ", "ଦ୍ୱିଷଷ୍ଠୀତମ", "ତ୍ରିଷଷ୍ଠୀତମ", "ଚତୁଷଷ୍ଠୀତମ", "ପଞ୍ଚଷଷ୍ଠୀତମ", "ଷଟ୍ ଷଷ୍ଠୀତମ", "ସପ୍ତଷଷ୍ଠୀତମ", "ଅଷ୍ଟଷଷ୍ଠୀତମ", "ଊନସପ୍ତତିତମ", "ସପ୍ତତିତମ", "ଏକସପ୍ତତିତମ", "ଦ୍ୱିସପ୍ତତିତମ", "ତ୍ରିସପ୍ତତିତମ", "ଚତୁସପ୍ତତିତମ", "ପଞ୍ଚସପ୍ତତିତମ", "ଷଟ୍ ସପ୍ତତିତମ", "ସତସପ୍ତତିତମ", "ଅଷ୍ଟସପ୍ତତିତମ", "ଊନଶୀତିତମ", "ଅଶୀତିତମ", "ଏକାଶୀତିତମ", "ଦ୍ଵାଶୀତିତମ", "ତ୍ରିଶୀତିତମ", "ଚତୁଶୀତିତମ", "ପଞ୍ଚଶୀତିତମ", "ଷଟ୍ ଶୀତିତମ", "ସପ୍ତଶୀତିତମ", "ଅଷ୍ଟଶୀତିତମ", "ଊନନବତିତମ", "ନବତିତମ", "ଏକନବତିତମ", "ଦ୍ୱିନବତିତମ", "ତ୍ରିନବତିତମ", "ଚତୁନବତିତମ", "ପଞ୍ଚନବତିତମ", "ଷଟ୍ ନବତିତମ", "ସପ୍ତନବତିତମ", "ଅଷ୍ଟନବତିତମ", "ନବନବତିତମ", "ଶତତମ", "ଦ୍ୱିଶତତମ", "ତ୍ରିଶତତମ", "ଚତୁଶତତମ", "ପଞ୍ଚଶତତମ", "ଷଟ୍ ତତମ", "ସପ୍ତଶତତମ", "ଅଷ୍ଟଶତତମ", "ନବଶତତମ", "ସହସ୍ରତମ", "ଲକ୍ଷତମ"];
const ordinalSymbols = ["୧ମ", "୨ୟ", "୩ୟ", "୪ର୍ଥ", "୫ମ", "୬ଷ୍ଠ", "୭ମ", "୮ମ", "୯ମ", "୧୦ମ", "୧୧ଶ", "୧୨ଶ", "୧୩ଶ", "୧୪ଶ", "୧୫ଶ", "୧୬ଶ", "୧୭ଶ", "୧୮ଶ", "୧୯ଶ", "୨୦ଶ", "୨୧ଶ", "୨୨ଶ", "୨୩ଶ", "୨୪ଶ", "୨୫ଶ", "୨୬ଶ", "୨୭ଶ", "୨୮ଶ", "୨୯ଶ", "୩୦ଶ", "୩୧ଶ", "୩୨ଶ", "୩୩ଶ", "୩୪ଶ", "୩୫ଶ", "୩୬ଶ", "୩୭ଶ", "୩୮ଶ", "୩୯ଶ", "୪୦ଶ", "୪୧ଶ", "୪୨ଶ", "୪୩ଶ", "୪୪ଶ", "୪୫ଶ", "୪୬ଶ", "୪୭ଶ", "୪୮ଶ", "୪୯ଶ", "୫୦ମ", "୫୧ମ", "୫୨ମ", "୫୩ମ", "୫୪ମ", "୫୫ମ", "୫୬ମ", "୫୭ମ", "୫୮ମ", "୫୯ମ", "୬୦ମ", "୬୧ମ", "୬୨ମ", "୬୩ମ", "୬୪ମ", "୬୫ମ", "୬୬ମ", "୬୭ମ", "୬୮ମ", "୬୯ମ", "୭୦ମ", "୭୧ମ", "୭୨ମ", "୭୩ମ", "୭୪ମ", "୭୫ମ", "୭୬ମ", "୭୭ମ", "୭୮ମ", "୭୯ମ", "୮୦ମ", "୮୧ମ", "୮୨ମ", "୮୩ମ", "୮୪ମ", "୮୫ମ", "୮୬ମ", "୮୭ମ", "୮୮ମ", "୮୯ମ", "୯୦ମ", "୯୧ମ", "୯୨ମ", "୯୩ମ", "୯୪ମ", "୯୫ମ", "୯୬ମ", "୯୭ମ", "୯୮ମ", "୯୯ମ", "୧୦୦ମ", "୨୦୦ମ", "୩୦୦ମ", "୪୦୦ମ", "୫୦୦ମ", "୬୦୦ମ", "୭୦୦ମ", "୮୦୦ମ", "୯୦୦ମ", "୧୦୦୦ତମ", "୧୦୦୦୦୦ତମ"];

export const ordinalData: OrdinalEntry[] = ordinalNumbers.map((num, idx) => ({
  number: num,
  word: ordinalWords[idx],
  symbol: ordinalSymbols[idx]
}));

// --- COUNTING DATA (Unchanged from CountingScreen.kt) ---
export interface CountingEntry {
  number: string;
  name: string;
}

const countingNumbers = [
  "୧", "୧୦", "୧୦୦", "୧୦୦୦", "୧୦୦୦୦", "୧୦୦୦୦୦", "୧୦୦୦୦୦୦",
  "୧୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦",
  "୧୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦",
  "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦", "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦",
  "୧୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦୦"
];
const countingNames = [
  "ଏକ", "ଦଶ", "ଶହ", "ହଜାର", "ଅୟୁତ", "ଲକ୍ଷ", "ନିୟୁତ",
  "କୋଟି", "ଅର୍ବୁଦ", "ବୃନ୍ଦ", "ଖର୍ବ", "ନିଖର୍ବ", "ଶଙ୍ଖ",
  "ପଦ୍ମ", "ସାଗର", "ଅନ୍ତ୍ୟ", "ମଧ୍ୟ", "ପରାର୍ଦ୍ଧ"
];

export const gananaData: CountingEntry[] = countingNumbers.map((num, idx) => ({
  number: num,
  name: countingNames[idx]
}));

// --- LIPYANTARA DATA (Unchanged from Lipyantara.kt) ---
export interface LipyantaraGroup {
  title: string;
  entries: { odiaChar: string; transliteration: string; ipa: string }[];
}

export const lipyantaraGroups: LipyantaraGroup[] = [
  {
    title: "ସ୍ୱରବର୍ଣ୍ଣ (Vowels)",
    entries: [
      { odiaChar: "ଅ", transliteration: "a", ipa: "/ɔ/" },
      { odiaChar: "ଆ", transliteration: "aa", ipa: "/aː/" },
      { odiaChar: "ଇ", transliteration: "i", ipa: "/i/" },
      { odiaChar: "ଈ", transliteration: "ee", ipa: "/iː/" },
      { odiaChar: "ଉ", transliteration: "u", ipa: "/u/" },
      { odiaChar: "ଊ", transliteration: "oo", ipa: "/uː/" },
      { odiaChar: "ଋ", transliteration: "ru", ipa: "/ru/" },
      { odiaChar: "ଏ", transliteration: "e", ipa: "/e/" },
      { odiaChar: "ଐ", transliteration: "ai", ipa: "/ɔi̯/" },
      { odiaChar: "ଓ", transliteration: "o", ipa: "/o/" },
      { odiaChar: "ଔ", transliteration: "ou", ipa: "/ɔu̯/" },
    ]
  },
  {
    title: "ଯୋଗ ବାହକ (Combining Signs)",
    entries: [
      { odiaChar: "ଅଂ", transliteration: "an", ipa: "/ɔŋ/, /ɔn/" },
      { odiaChar: "ଅଃ", transliteration: "ah", ipa: "/ɔh/" },
      { odiaChar: "ଅଁ", transliteration: "an / am̐ (nasal)", ipa: "/ɔ̃/" },
    ]
  },
  {
    title: "ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ (Consonants)",
    entries: [
      { odiaChar: "କ", transliteration: "ka", ipa: "/kɔ/" },
      { odiaChar: "ଖ", transliteration: "kha", ipa: "/kʰɔ/" },
      { odiaChar: "ଗ", transliteration: "ga", ipa: "/ɡɔ/" },
      { odiaChar: "ଘ", transliteration: "gha", ipa: "/ɡʱɔ/" },
      { odiaChar: "ଙ", transliteration: "una / ṅa", ipa: "/ŋɔ/" },
      { odiaChar: "ଚ", transliteration: "cha", ipa: "/t͡ʃɔ/" },
      { odiaChar: "ଛ", transliteration: "chha", ipa: "/t͡ʃʰɔ/" },
      { odiaChar: "ଜ", transliteration: "ja", ipa: "/d͡ʒɔ/" },
      { odiaChar: "ଝ", transliteration: "jha", ipa: "/d͡ʒʱɔ/" },
      { odiaChar: "ଞ", transliteration: "nya / ña", ipa: "/ɲɔ/" },
      { odiaChar: "ଟ", transliteration: "Ta", ipa: "/ʈɔ/" },
      { odiaChar: "ଠ", transliteration: "Tha", ipa: "/ʈʰɔ/" },
      { odiaChar: "ଡ", transliteration: "Da", ipa: "/ɖɔ/" },
      { odiaChar: "ଢ", transliteration: "Dha", ipa: "/ɖʱɔ/" },
      { odiaChar: "ଣ", transliteration: "Na", ipa: "/ɳɔ/" },
      { odiaChar: "ତ", transliteration: "ta", ipa: "/t̪ɔ/" },
      { odiaChar: "ଥ", transliteration: "tha", ipa: "/t̪ʰɔ/" },
      { odiaChar: "ଦ", transliteration: "da", ipa: "/d̪ɔ/" },
      { odiaChar: "ଧ", transliteration: "dha", ipa: "/d̪ʱɔ/" },
      { odiaChar: "ନ", transliteration: "na", ipa: "/n̪ɔ/" },
      { odiaChar: "ପ", transliteration: "pa", ipa: "/pɔ/" },
      { odiaChar: "ଫ", transliteration: "pha / fa", ipa: "/pʰɔ/" },
      { odiaChar: "ବ", transliteration: "ba", ipa: "/bɔ/" },
      { odiaChar: "ଭ", transliteration: "bha / va", ipa: "/bʱɔ/" },
      { odiaChar: "ମ", transliteration: "ma", ipa: "/mɔ/" },
      { odiaChar: "ଯ", transliteration: "ja", ipa: "/d͡ʒɔ/" },
      { odiaChar: "ର", transliteration: "ra", ipa: "/rɔ/" },
      { odiaChar: "ଲ", transliteration: "la", ipa: "/lɔ/" },
      { odiaChar: "ଳ", transliteration: "La", ipa: "/ɭɔ/" },
      { odiaChar: "ୱ", transliteration: "wa", ipa: "/wɔ/, /vɔ/" },
      { odiaChar: "ଶ", transliteration: "sha", ipa: "/sɔ/, /ʃɔ/" },
      { odiaChar: "ଷ", transliteration: "sha", ipa: "/sɔ/" },
      { odiaChar: "ସ", transliteration: "sa", ipa: "/sɔ/" },
      { odiaChar: "ହ", transliteration: "ha", ipa: "/hɔ/" },
      { odiaChar: "କ୍ଷ", transliteration: "ksha / khya", ipa: "/kʰjɔ/" },
      { odiaChar: "ୟ", transliteration: "ya", ipa: "/jɔ/" },
      { odiaChar: "ଡ଼", transliteration: "Ra (flapped)", ipa: "/ɽɔ/" },
      { odiaChar: "ଢ଼", transliteration: "Rha (flapped)", ipa: "/ɽʱɔ/" },
    ]
  }
];

// --- DEEP LINKS CATEGORIES (Exact links requested by user) ---
export interface DeepLinkChip {
  label: string;
  screen: Screen;
  url: string;
}

export interface DeepLinkCategory {
  title: string;
  description?: string;
  chips: DeepLinkChip[];
}

export const deepLinkCategories: DeepLinkCategory[] = [
  {
    title: "ମୁଖ୍ୟ ବିଭାଗ",
    chips: [
      { label: "ମୁଖ୍ୟ ପୃଷ୍ଠା", screen: Screen.HOME, url: "https://odiabhasa.pages.dev/open?HOME" },
      { label: "ମେନୁ", screen: Screen.MENU, url: "https://odiabhasa.pages.dev/open?MENU" },
      { label: "ଆମ ବିଷୟରେ", screen: Screen.ABOUT, url: "https://odiabhasa.pages.dev/open?ABOUT" },
      { label: "ଓଡ଼ିଆ କ୍ୟାଲେଣ୍ଡର", screen: Screen.ODIA_CALENDAR, url: "https://odiabhasa.pages.dev/open?ODIA_CALENDAR" },
      { label: "ହାରମୋନିୟମ୍", screen: Screen.HARMONIUM, url: "https://odiabhasa.pages.dev/open?HARMONIUM" },
    ]
  },
  {
    title: "ଓଡ଼ିଆ ଶିକ୍ଷା (ବର୍ଣ୍ଣ ଓ ଶବ୍ଦ)",
    chips: [
      { label: "ସ୍ୱରବର୍ଣ୍ଣ", screen: Screen.SWARA, url: "https://odiabhasa.pages.dev/open?SWARA" },
      { label: "ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ", screen: Screen.BYANJANA, url: "https://odiabhasa.pages.dev/open?BYANJANA" },
      { label: "ଓଡ଼ିଆ ସଂଖ୍ୟା", screen: Screen.SANKHYA, url: "https://odiabhasa.pages.dev/open?SANKHYA" },
      { label: "ଯୁକ୍ତାକ୍ଷର", screen: Screen.YUKTA, url: "https://odiabhasa.pages.dev/open?YUKTA" },
      { label: "ମାତ୍ରା", screen: Screen.MATRA, url: "https://odiabhasa.pages.dev/open?MATRA" },
      { label: "ଫଳା", screen: Screen.PHALA, url: "https://odiabhasa.pages.dev/open?PHALA" },
      { label: "ବର୍ଣ୍ଣ ପରିଚୟ", screen: Screen.BARNA_PARICHAYA, url: "https://odiabhasa.pages.dev/open?BARNA_PARICHAYA" },
      { label: "ମଧୁ ବର୍ଣ୍ଣବୋଧ", screen: Screen.MADHU_BARNABODHA, url: "https://odiabhasa.pages.dev/open?MADHU_BARNABODHA" },
      { label: "ଲିଖନ ଅଭ୍ୟାସ", screen: Screen.WRITING, url: "https://odiabhasa.pages.dev/open?WRITING" },
    ]
  },
  {
    title: "ବିଦ୍ୟାଳୟ ବହି (ପ୍ରଥମ ରୁ ଦଶମ ଶ୍ରେଣୀ)",
    description: "ଆମ ଆପ୍ ରେ ପ୍ରଥମ ଶ୍ରେଣୀ ଠାରୁ ଆରମ୍ଭ କରି ଦଶମ ଶ୍ରେଣୀ ପର୍ଯ୍ୟନ୍ତ ସମସ୍ତ ସରକାରୀ ବିଦ୍ୟାଳୟ ବହି ମାଗଣାରେ ଉପଲବ୍ଧ ଅଛି। ତଳେ ଥିବା ଲିଙ୍କ୍ ଉପରେ କ୍ଲିକ୍ କରି ନିଜ ଶ୍ରେଣୀର ବହି ଖୋଲନ୍ତୁ:",
    chips: [
      { label: "ବିଦ୍ୟାଳୟ ପୁସ୍ତକ ମୁଖ୍ୟ ପୃଷ୍ଠା", screen: Screen.SCHOOL_BOOKS, url: "https://odiabhasa.pages.dev/open?SCHOOL_BOOKS" },
      { label: "ପ୍ରଥମ ଶ୍ରେଣୀ", screen: Screen.CLASS_1, url: "https://odiabhasa.pages.dev/open?CLASS_1" },
      { label: "ଦ୍ଵିତୀୟ ଶ୍ରେଣୀ", screen: Screen.CLASS_2, url: "https://odiabhasa.pages.dev/open?CLASS_2" },
      { label: "ତୃତୀୟ ଶ୍ରେଣୀ", screen: Screen.CLASS_3, url: "https://odiabhasa.pages.dev/open?CLASS_3" },
      { label: "ଚତୁର୍ଥ ଶ୍ରେଣୀ", screen: Screen.CLASS_4, url: "https://odiabhasa.pages.dev/open?CLASS_4" },
      { label: "ପଞ୍ଚମ ଶ୍ରେଣୀ", screen: Screen.CLASS_5, url: "https://odiabhasa.pages.dev/open?CLASS_5" },
      { label: "ଷଷ୍ଠ ଶ୍ରେଣୀ", screen: Screen.CLASS_6, url: "https://odiabhasa.pages.dev/open?CLASS_6" },
      { label: "ସପ୍ତମ ଶ୍ରେଣୀ", screen: Screen.CLASS_7, url: "https://odiabhasa.pages.dev/open?CLASS_7" },
      { label: "ଅଷ୍ଟମ ଶ୍ରେଣୀ", screen: Screen.CLASS_8, url: "https://odiabhasa.pages.dev/open?CLASS_8" },
      { label: "ନବମ ଶ୍ରେଣୀ", screen: Screen.CLASS_9, url: "https://odiabhasa.pages.dev/open?CLASS_9" },
      { label: "ଦଶମ ଶ୍ରେଣୀ", screen: Screen.CLASS_10, url: "https://odiabhasa.pages.dev/open?CLASS_10" },
    ]
  },
  {
    title: "ଗଣିତ ଏବଂ ଉପଯୋଗୀ ବିଭାଗ",
    chips: [
      { label: "ଗଣିତ ମୁଖ୍ୟ ପୃଷ୍ଠା", screen: Screen.GANITA, url: "https://odiabhasa.pages.dev/open?GANITA" },
      { label: "ଖେଳ ବିଭାଗ", screen: Screen.GAMES_CATEGORY, url: "https://odiabhasa.pages.dev/open?GAMES_CATEGORY" },
      { label: "ଶିକ୍ଷା ବିଭାଗ", screen: Screen.LEARNING_CATEGORY, url: "https://odiabhasa.pages.dev/open?LEARNING_CATEGORY" },
      { label: "ଉପଯୋଗୀ ବିଭାଗ", screen: Screen.UTILITY_CATEGORY, url: "https://odiabhasa.pages.dev/open?UTILITY_CATEGORY" },
      { label: "ପଣିକିଆ", screen: Screen.LEARNING_PANIKIA, url: "https://odiabhasa.pages.dev/open?LEARNING_PANIKIA" },
      { label: "କ୍ରମବାଚକ ସଂଖ୍ୟା", screen: Screen.LEARNING_ORDINALS, url: "https://odiabhasa.pages.dev/open?LEARNING_ORDINALS" },
      { label: "ଗଣତି ଶିକ୍ଷା", screen: Screen.LEARNING_COUNTING, url: "https://odiabhasa.pages.dev/open?LEARNING_COUNTING" },
      { label: "ଗଣିତ ପ୍ରଶ୍ନୋତ୍ତର", screen: Screen.GAME_MATH_QUIZ, url: "https://odiabhasa.pages.dev/open?GAME_MATH_QUIZ" },
      { label: "ତର୍କ ଖେଳ", screen: Screen.GAME_LOGIC, url: "https://odiabhasa.pages.dev/open?GAME_LOGIC" },
      { label: "ଶବ୍ଦ ସମସ୍ୟା", screen: Screen.GAME_WORD_PROBLEM, url: "https://odiabhasa.pages.dev/open?GAME_WORD_PROBLEM" },
      { label: "ସୁଡୋକୁ ଖେଳ", screen: Screen.GAME_SUDOKU, url: "https://odiabhasa.pages.dev/open?GAME_SUDOKU" },
      { label: "ନମ୍ବର ଖେଳ", screen: Screen.GAME_FTMN, url: "https://odiabhasa.pages.dev/open?GAME_FTMN" },
      { label: "ସ୍ଲାଇଡିଂ ପଜଲ୍", screen: Screen.GAME_SLIDING_PUZZLE, url: "https://odiabhasa.pages.dev/open?GAME_SLIDING_PUZZLE" },
      { label: "କାଲକୁଲେଟର", screen: Screen.UTILITY_CALCULATOR, url: "https://odiabhasa.pages.dev/open?UTILITY_CALCULATOR" },
      { label: "ଏକକ ରୂପାନ୍ତରଣ", screen: Screen.UTILITY_CONVERTER, url: "https://odiabhasa.pages.dev/open?UTILITY_CONVERTER" },
      { label: "ସଂଖ୍ୟାରୁ ଶବ୍ଦ", screen: Screen.UTILITY_NUM_TO_TEXT, url: "https://odiabhasa.pages.dev/open?UTILITY_NUM_TO_TEXT" },
      { label: "ଡିଜିଟାଲ୍ ବଜାର", screen: Screen.UTILITY_DIGITAL_BAJARA, url: "https://odiabhasa.pages.dev/open?UTILITY_DIGITAL_BAJARA" },
    ]
  },
  {
    title: "ସାହିତ୍ୟ, ଇତିହାସ ଓ ଅନ୍ୟାନ୍ୟ",
    chips: [
      { label: "ପାଠାଗାର (ବହି)", screen: Screen.BAHI, url: "https://odiabhasa.pages.dev/open?BAHI" },
      { label: "ପୁରୁଣା ବହି", screen: Screen.PURUNA_BAHI, url: "https://odiabhasa.pages.dev/open?PURUNA_BAHI" },
      { label: "ଆସ ଜାଣିବା", screen: Screen.ASA_JANIBA, url: "https://odiabhasa.pages.dev/open?ASA_JANIBA" },
      { label: "ଓଡ଼ିଶାର ଇତିହାସ", screen: Screen.HISTORY_ODISHA, url: "https://odiabhasa.pages.dev/open?HISTORY_ODISHA" },
      { label: "ଓଡ଼ିଆ ଭାଷାର ଇତିହାସ", screen: Screen.HISTORY_LANG, url: "https://odiabhasa.pages.dev/open?HISTORY_LANG" },
      { label: "ଲିପ୍ୟନ୍ତର", screen: Screen.LIPYANTARA, url: "https://odiabhasa.pages.dev/open?LIPYANTARA" },
    ]
  }
];
