import test from 'node:test';
import assert from 'node:assert/strict';
import {
  toOdiaNum,
  fromOdiaNum,
  convertToOdiaWords,
  convertToEnglishWords,
  getPanikiaTableData,
  getCalculationIdea,
  getSmartIdea1,
  getSmartIdea2,
  shopUnits,
  generateWordProblem,
  parseDeepLinkFromSearch,
  getOdiaUrlPath,
  parseRouteFromLocation,
  shouldShowAppPopup,
  ONE_DAY_MS,
  ODIA_NUMBER_WORDS
} from './odiaUtils.ts';
import { Screen, getCharDetails, swarabarnaList } from '../data/odiaData.ts';
import {
  getAllHomeBarnabodhaApiData,
  getSwarabarnaApiData,
  getNumberToWordApiData,
  isOpenApiPath,
  resolveOpenApiV1Request
} from './openApiData.ts';

test('toOdiaNum and fromOdiaNum round-trip conversion', () => {
  assert.equal(toOdiaNum(0), '୦');
  assert.equal(toOdiaNum(1234567890), '୧୨୩୪୫୬୭୮୯୦');
  assert.equal(toOdiaNum('25.50'), '୨୫.୫୦');
  assert.equal(fromOdiaNum('୧୨୩୪୫୬୭୮୯୦'), '1234567890');
  assert.equal(fromOdiaNum(toOdiaNum(9876)), '9876');
});

test('convertToOdiaWords and convertToEnglishWords Indian numbering system', () => {
  assert.equal(convertToOdiaWords(0), 'ଶୂନ');
  assert.equal(convertToOdiaWords(21), 'ଏକୋଇଶ');
  assert.equal(convertToOdiaWords(25), 'ପଚିଶ');
  assert.equal(convertToOdiaWords(100), 'ଶହେ');
  assert.equal(convertToOdiaWords(125), 'ଶହେ ପଚିଶ');
  assert.equal(convertToOdiaWords(1500), 'ଏକ ହଜାର ପାଞ୍ଚ ଶହ');
  assert.equal(convertToEnglishWords(0), 'Zero');
  assert.equal(convertToEnglishWords(125), 'One Hundred Twenty Five');
  assert.equal(convertToEnglishWords(100000), 'One Lakh');
  for (const w of ODIA_NUMBER_WORDS) {
    assert.equal(/[\u0980-\u09FF]/.test(w), false, `Word ${w} must not contain Bengali chars`);
  }
});

test('getPanikiaTableData generates 10 rows with Odia script and numbers', () => {
  const table2 = getPanikiaTableData(2);
  assert.equal(table2.length, 10);
  assert.equal(table2[0].numericText, '୨ × ୧ = ୨');
  assert.equal(table2[0].scriptText, 'ଦୁଇ କେ ଦୁଇ');
  assert.equal(table2[4].product, 10);
  assert.equal(table2[4].scriptText, 'ଦୁଇ ପଞ୍ଚା ଦଶ');
});

test('Digital Bajara step-by-step explanation generators', () => {
  const kgUnit = shopUnits[0];
  const gmUnit = shopUnits[1];
  const ideaKg = getCalculationIdea('40', '2', kgUnit, 80);
  assert.ok(ideaKg.includes('₹୪୦'));
  assert.ok(ideaKg.includes('₹୮୦.୦୦'));

  const ideaGm = getCalculationIdea('100', '500', gmUnit, 50);
  assert.ok(ideaGm.includes('୧୦୦୦'));

  const smart1 = getSmartIdea1('1000', '100', '50', gmUnit, 500);
  assert.ok(smart1.includes('୫୦୦'));

  const smart2 = getSmartIdea2('1', '60', '3', kgUnit, 180);
  assert.ok(smart2.includes('୧୮୦.୦୦'));
});

test('generateWordProblem covers all 22 templates with valid answers and no Bengali chars', () => {
  for (let i = 1; i <= 22; i++) {
    const wp = generateWordProblem(i);
    assert.equal(wp.templateIndex, i);
    assert.ok(wp.question.length > 10);
    assert.ok(Number.isFinite(wp.answer) && wp.answer > 0);
    assert.ok(wp.solution.includes(toOdiaNum(wp.answer)));
    assert.equal(/[\u0980-\u09FF]/.test(wp.question), false);
    assert.equal(/[\u0980-\u09FF]/.test(wp.solution), false);
  }
});

test('parseDeepLinkFromSearch and Odia URL path routing with 404 detection', () => {
  assert.deepEqual(parseDeepLinkFromSearch('?HOME'), { screen: Screen.HOME, isValidSearch: true });
  assert.deepEqual(parseDeepLinkFromSearch('?SWARA'), { screen: Screen.SWARA, isValidSearch: true });
  assert.deepEqual(parseDeepLinkFromSearch('?CLASS_10'), { screen: Screen.CLASS_10, isValidSearch: true });
  assert.deepEqual(parseDeepLinkFromSearch('?GAME_SUDOKU'), { screen: Screen.GAME_SUDOKU, isValidSearch: true });
  assert.deepEqual(parseDeepLinkFromSearch('?open=HARMONIUM'), { screen: Screen.HARMONIUM, isValidSearch: true });
  assert.deepEqual(parseDeepLinkFromSearch('?Bahi=bahi_123'), { screen: Screen.BAHI, bahiId: 'bahi_123', isValidSearch: true });

  assert.equal(getOdiaUrlPath(Screen.ODIA_CALENDAR), '/ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/');
  assert.equal(getOdiaUrlPath(Screen.ASA_JANIBA), '/ଆସ-ଜାଣିବା/');
  assert.deepEqual(parseRouteFromLocation('/ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/', ''), { screen: Screen.ODIA_CALENDAR, isNotFound: false });
  assert.deepEqual(parseRouteFromLocation(encodeURI('/ଆସ-ଜାଣିବା/'), ''), { screen: Screen.ASA_JANIBA, isNotFound: false });
  assert.deepEqual(parseRouteFromLocation('/unknown-404-page', ''), { screen: Screen.HOME, isNotFound: true });
  assert.deepEqual(parseRouteFromLocation('/କିଛି-ନାହିଁ/', ''), { screen: Screen.HOME, isNotFound: true });
  // Ensure /api/v1/* is never marked as isNotFound
  assert.deepEqual(parseRouteFromLocation('/api/v1/number-to-word', '?num=125'), { screen: Screen.HOME, isNotFound: false });
});

test('Open Web API v1 data generators and resolveOpenApiV1Request for static & server hosts', () => {
  const allBarnabodha = getAllHomeBarnabodhaApiData();
  assert.equal(allBarnabodha.sections.length, 7);
  const swara = getSwarabarnaApiData();
  assert.equal(swara.titleOdia, 'ସ୍ୱରବର୍ଣ୍ଣ');
  assert.equal(swara.count, 14);
  const numWord = getNumberToWordApiData('୧୨୫');
  assert.equal(numWord.number, 125);
  assert.equal(numWord.odiaWord, 'ଶହେ ପଚିଶ');
  assert.equal(numWord.englishWord, 'One Hundred Twenty Five');

  assert.equal(isOpenApiPath('/api/v1/number-to-word'), true);
  assert.equal(isOpenApiPath('/api/v1/'), true);
  assert.equal(isOpenApiPath('/ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/'), false);

  const resNum = resolveOpenApiV1Request('/api/v1/number-to-word', '?num=125', 'https://odiabhasa.netlify.app');
  assert.equal(resNum.status, 200);
  assert.equal((resNum.data as { odiaWord: string }).odiaWord, 'ଶହେ ପଚିଶ');

  const resNumPath = resolveOpenApiV1Request('/api/v1/number-to-word/125', '', 'https://odiabhasa.netlify.app');
  assert.equal((resNumPath.data as { odiaWord: string }).odiaWord, 'ଶହେ ପଚିଶ');

  const resSwara = resolveOpenApiV1Request('/api/v1/swarabarna', '', 'https://odiabhasa.netlify.app');
  assert.equal((resSwara.data as { key: string }).key, 'swarabarna');

  const resByanjana = resolveOpenApiV1Request('/api/v1/byanjanabarna', '', 'https://odiabhasa.netlify.app');
  assert.equal((resByanjana.data as { key: string }).key, 'byanjanabarna');

  const resBarna = resolveOpenApiV1Request('/api/v1/barna-parichaya', '', 'https://odiabhasa.netlify.app');
  assert.equal((resBarna.data as { key: string }).key, 'barna_parichaya');

  const resMatra = resolveOpenApiV1Request('/api/v1/matra', '', 'https://odiabhasa.netlify.app');
  assert.equal((resMatra.data as { key: string }).key, 'matra');

  const resPhala = resolveOpenApiV1Request('/api/v1/phala', '', 'https://odiabhasa.netlify.app');
  assert.equal((resPhala.data as { key: string }).key, 'phala');

  const resYukta = resolveOpenApiV1Request('/api/v1/yuktakshara', '', 'https://odiabhasa.netlify.app');
  assert.equal((resYukta.data as { key: string }).key, 'yuktakshara');

  const resSankhya = resolveOpenApiV1Request('/api/v1/sankhya', '', 'https://odiabhasa.netlify.app');
  assert.equal((resSankhya.data as { key: string }).key, 'sankhya');
});

test('odiaData character lists and dictionary integrity', () => {
  assert.equal(swarabarnaList.length, 14);
  const detailsA = getCharDetails('ଅ');
  assert.ok(detailsA.words.includes('ଅରଟ'));
  assert.ok(detailsA.sentences.length > 0);
});

test('shouldShowAppPopup enforces 1-day (24h) dismissal window', () => {
  const now = 1_700_000_000_000;
  assert.equal(shouldShowAppPopup(null, now), true);
  assert.equal(shouldShowAppPopup('', now), true);
  assert.equal(shouldShowAppPopup(String(now - 1000), now), false);
  assert.equal(shouldShowAppPopup(String(now - ONE_DAY_MS + 1000), now), false);
  assert.equal(shouldShowAppPopup(String(now - ONE_DAY_MS), now), true);
  assert.equal(shouldShowAppPopup(String(now - ONE_DAY_MS - 5000), now), true);
});


