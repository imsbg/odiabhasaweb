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
  parseRouteFromLocation
} from './odiaUtils.ts';
import { Screen, getCharDetails, swarabarnaList } from '../data/odiaData.ts';
import {
  getAllHomeBarnabodhaApiData,
  getSwarabarnaApiData,
  getNumberToWordApiData
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
  assert.equal(convertToOdiaWords(25), 'ପଚିଶ');
  assert.equal(convertToOdiaWords(100), 'ଶହେ');
  assert.equal(convertToOdiaWords(125), 'ଶହେ ପଚିଶ');
  assert.equal(convertToOdiaWords(1500), 'ଏକ ହଜାର ପାଞ୍ଚ ଶହ');
  assert.equal(convertToEnglishWords(0), 'Zero');
  assert.equal(convertToEnglishWords(125), 'One Hundred Twenty Five');
  assert.equal(convertToEnglishWords(100000), 'One Lakh');
});

test('getPanikiaTableData generates 10 rows with Odia script and numbers', () => {
  const table2 = getPanikiaTableData(2);
  assert.equal(table2.length, 10);
  assert.equal(table2[0].numericText, '୨ × ୧ = ୨');
  assert.equal(table2[0].scriptText, 'ଦୁଇ କେ ଦୁଇ');
  assert.equal(table2[4].Product ?? table2[4].product, 10);
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

test('generateWordProblem covers all 22 templates with valid answers', () => {
  for (let i = 1; i <= 22; i++) {
    const wp = generateWordProblem(i);
    assert.equal(wp.templateIndex, i);
    assert.ok(wp.question.length > 10);
    assert.ok(Number.isFinite(wp.answer) && wp.answer > 0);
    assert.ok(wp.solution.includes(toOdiaNum(wp.answer)));
  }
});

test('parseDeepLinkFromSearch and Odia URL path routing', () => {
  assert.deepEqual(parseDeepLinkFromSearch('?HOME'), { screen: Screen.HOME });
  assert.deepEqual(parseDeepLinkFromSearch('?SWARA'), { screen: Screen.SWARA });
  assert.deepEqual(parseDeepLinkFromSearch('?CLASS_10'), { screen: Screen.CLASS_10 });
  assert.deepEqual(parseDeepLinkFromSearch('?GAME_SUDOKU'), { screen: Screen.GAME_SUDOKU });
  assert.deepEqual(parseDeepLinkFromSearch('?open=HARMONIUM'), { screen: Screen.HARMONIUM });
  assert.deepEqual(parseDeepLinkFromSearch('?Bahi=bahi_123'), { screen: Screen.BAHI, bahiId: 'bahi_123' });

  assert.equal(getOdiaUrlPath(Screen.ODIA_CALENDAR), '/ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/');
  assert.equal(getOdiaUrlPath(Screen.ASA_JANIBA), '/ଆସ-ଜାଣିବା/');
  assert.deepEqual(parseRouteFromLocation('/ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/', ''), { screen: Screen.ODIA_CALENDAR });
  assert.deepEqual(parseRouteFromLocation(encodeURI('/ଆସ-ଜାଣିବା/'), ''), { screen: Screen.ASA_JANIBA });
});

test('Open Web API v1 data generators for Home Barnabodha and Number to Word', () => {
  const allBarnabodha = getAllHomeBarnabodhaApiData();
  assert.equal(allBarnabodha.sections.length, 7);
  const swara = getSwarabarnaApiData();
  assert.equal(swara.titleOdia, 'ସ୍ୱରବର୍ଣ୍ଣ');
  assert.equal(swara.count, 14);
  const numWord = getNumberToWordApiData('୧୨୫');
  assert.equal(numWord.number, 125);
  assert.equal(numWord.odiaWord, 'ଶହେ ପଚିଶ');
  assert.equal(numWord.englishWord, 'One Hundred Twenty Five');
});

test('odiaData character lists and dictionary integrity', () => {
  assert.equal(swarabarnaList.length, 14);
  const detailsA = getCharDetails('ଅ');
  assert.ok(detailsA.words.includes('ଅରଟ'));
  assert.ok(detailsA.sentences.length > 0);
});

