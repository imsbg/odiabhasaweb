import express from 'express';
import path from 'path';
import { createServer as createViteServer } from 'vite';
import {
  getAllHomeBarnabodhaApiData,
  getSwarabarnaApiData,
  getByanjanabarnaApiData,
  getBarnaParichayaApiData,
  getMatraApiData,
  getPhalaApiData,
  getYuktaksharaApiData,
  getSankhyaApiData,
  getNumberToWordApiData,
  buildCharacterItem
} from './src/utils/openApiData';

async function startServer() {
  const app = express();
  const PORT = 3000;

  // Enable CORS & JSON formatting for Open API v1 (no API key required)
  app.use('/api', (_req, res, next) => {
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');
    next();
  });

  // Main /api/v1 and /api/v1/ endpoint
  app.get(['/api/v1', '/api/v1/'], (req, res) => {
    const numQuery = req.query.num ?? req.query.number ?? req.query.n;
    if (numQuery !== undefined) {
      return res.json(getNumberToWordApiData(String(numQuery)));
    }

    const typeQuery = String(req.query.type ?? req.query.section ?? '').toLowerCase().trim();
    if (typeQuery) {
      if (typeQuery === 'swara' || typeQuery === 'swarabarna' || typeQuery === 'ସ୍ୱରବର୍ଣ୍ଣ') {
        return res.json(getSwarabarnaApiData());
      }
      if (typeQuery === 'byanjana' || typeQuery === 'byanjanabarna' || typeQuery === 'ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ' || typeQuery === 'କ') {
        return res.json(getByanjanabarnaApiData());
      }
      if (typeQuery === 'barna_parichaya' || typeQuery === 'barna-parichaya' || typeQuery === 'ବର୍ଣ୍ଣ ପରିଚୟ' || typeQuery === 'ବର୍ଣ୍ଣ') {
        return res.json(getBarnaParichayaApiData());
      }
      if (typeQuery === 'matra' || typeQuery === 'ମାତ୍ରା' || typeQuery === 'ା') {
        return res.json(getMatraApiData());
      }
      if (typeQuery === 'phala' || typeQuery === 'ଫଳା' || typeQuery === '୍ୟ') {
        return res.json(getPhalaApiData());
      }
      if (typeQuery === 'yukta' || typeQuery === 'yuktakshara' || typeQuery === 'ଯୁକ୍ତାକ୍ଷର' || typeQuery === 'ଙ୍କ') {
        return res.json(getYuktaksharaApiData());
      }
      if (typeQuery === 'sankhya' || typeQuery === 'ସଂଖ୍ୟା' || typeQuery === '୦') {
        return res.json(getSankhyaApiData());
      }
    }

    const baseUrl = `${req.protocol}://${req.get('host')}`;
    return res.json(getAllHomeBarnabodhaApiData(baseUrl));
  });

  // /api/v1/barnabodha -> All 7 Home Barnabodha sections
  app.get('/api/v1/barnabodha', (req, res) => {
    const baseUrl = `${req.protocol}://${req.get('host')}`;
    res.json(getAllHomeBarnabodhaApiData(baseUrl));
  });

  // 1. ସ୍ୱରବର୍ଣ୍ଣ
  app.get(['/api/v1/swarabarna', '/api/v1/swara', `/api/v1/${encodeURIComponent('ସ୍ୱରବର୍ଣ୍ଣ')}`], (_req, res) => {
    res.json(getSwarabarnaApiData());
  });

  // 2. ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ (କ)
  app.get(['/api/v1/byanjanabarna', '/api/v1/byanjana', `/api/v1/${encodeURIComponent('ବ୍ୟଞ୍ଜନବର୍ଣ୍ଣ')}`], (_req, res) => {
    res.json(getByanjanabarnaApiData());
  });

  // 3. ବର୍ଣ୍ଣ ପରିଚୟ (ବର୍ଣ୍ଣ)
  app.get(['/api/v1/barna-parichaya', '/api/v1/barna_parichaya', `/api/v1/${encodeURIComponent('ବର୍ଣ୍ଣ-ପରିଚୟ')}`], (_req, res) => {
    res.json(getBarnaParichayaApiData());
  });

  // 4. ମାତ୍ରା (ା)
  app.get(['/api/v1/matra', `/api/v1/${encodeURIComponent('ମାତ୍ରା')}`], (_req, res) => {
    res.json(getMatraApiData());
  });

  // 5. ଫଳା (୍ୟ)
  app.get(['/api/v1/phala', `/api/v1/${encodeURIComponent('ଫଳା')}`], (_req, res) => {
    res.json(getPhalaApiData());
  });

  // 6. ଯୁକ୍ତାକ୍ଷର (ଙ୍କ)
  app.get(['/api/v1/yuktakshara', '/api/v1/yukta', `/api/v1/${encodeURIComponent('ଯୁକ୍ତାକ୍ଷର')}`], (_req, res) => {
    res.json(getYuktaksharaApiData());
  });

  // 7. ସଂଖ୍ୟା (୦)
  app.get(['/api/v1/sankhya', `/api/v1/${encodeURIComponent('ସଂଖ୍ୟା')}`], (_req, res) => {
    res.json(getSankhyaApiData());
  });

  // Single character lookup: /api/v1/char/:symbol
  app.get('/api/v1/char/:symbol', (req, res) => {
    const sym = decodeURIComponent(req.params.symbol);
    res.json({
      status: 'success',
      ...buildCharacterItem(sym)
    });
  });

  // 8. Number to Word: /api/v1/number-to-word?num=125 or /api/v1/number-to-word/125
  app.get('/api/v1/number-to-word', (req, res) => {
    const num = req.query.num ?? req.query.number ?? req.query.n ?? '0';
    res.json(getNumberToWordApiData(String(num)));
  });

  app.get('/api/v1/number-to-word/:number', (req, res) => {
    res.json(getNumberToWordApiData(decodeURIComponent(req.params.number)));
  });

  if (process.env.NODE_ENV !== 'production') {
    const vite = await createViteServer({
      server: { middlewareMode: true },
      appType: 'spa'
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (_req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`Server running on http://localhost:${PORT}`);
  });
}

startServer();
