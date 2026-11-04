import fs from 'fs';
import path from 'path';
import { buildSync } from 'esbuild';
import {
  getAllHomeBarnabodhaApiData,
  getSwarabarnaApiData,
  getByanjanabarnaApiData,
  getBarnaParichayaApiData,
  getMatraApiData,
  getPhalaApiData,
  getYuktaksharaApiData,
  getSankhyaApiData
} from '../src/utils/openApiData';

const rootDir = process.cwd();
const publicApiDir = path.join(rootDir, 'public', 'api', 'v1');
fs.mkdirSync(publicApiDir, { recursive: true });

// 1. Write static JSON files to /public/api/v1/*.json
const staticPayloads: Record<string, unknown> = {
  'index.json': getAllHomeBarnabodhaApiData(''),
  'barnabodha.json': getAllHomeBarnabodhaApiData(''),
  'swarabarna.json': getSwarabarnaApiData(),
  'byanjanabarna.json': getByanjanabarnaApiData(),
  'barna-parichaya.json': getBarnaParichayaApiData(),
  'matra.json': getMatraApiData(),
  'phala.json': getPhalaApiData(),
  'yuktakshara.json': getYuktaksharaApiData(),
  'sankhya.json': getSankhyaApiData()
};

for (const [fileName, data] of Object.entries(staticPayloads)) {
  fs.writeFileSync(
    path.join(publicApiDir, fileName),
    JSON.stringify(data, null, 2),
    'utf-8'
  );
}

// 2. Bundle openApiData.ts into a standalone ESM string using esbuild
const bundleResult = buildSync({
  entryPoints: [path.join(rootDir, 'src', 'utils', 'openApiData.ts')],
  bundle: true,
  format: 'esm',
  platform: 'neutral',
  write: false,
  minify: false
});

const bundledOpenApiCode = bundleResult.outputFiles[0].text.replace(
  /export\s*\{[^}]*\};?\s*$/,
  ''
);

// 3. Create Netlify Edge Function (/netlify/edge-functions/api.js)
const netlifyEdgeDir = path.join(rootDir, 'netlify', 'edge-functions');
fs.mkdirSync(netlifyEdgeDir, { recursive: true });

const edgeFunctionCode = `// Auto-generated Real HTTP JSON API Edge Function for Netlify
${bundledOpenApiCode}

export default async function handler(request) {
  if (request.method === 'OPTIONS') {
    return new Response(null, {
      status: 204,
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
      }
    });
  }

  const url = new URL(request.url);
  const result = resolveOpenApiV1Request(url.pathname, url.search, url.origin);

  return new Response(JSON.stringify(result.data, null, 2), {
    status: result.status,
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Cache-Control': 'public, max-age=60'
    }
  });
}

export const config = {
  path: ['/api', '/api/*']
};
`;

fs.writeFileSync(path.join(netlifyEdgeDir, 'api.js'), edgeFunctionCode, 'utf-8');

// 4. Create Netlify Serverless Function (/netlify/functions/api.mjs)
const netlifyFuncDir = path.join(rootDir, 'netlify', 'functions');
fs.mkdirSync(netlifyFuncDir, { recursive: true });

const netlifyFunctionCode = `// Auto-generated Real HTTP JSON Serverless Function for Netlify
${bundledOpenApiCode}

export default async function handler(request) {
  if (request.method === 'OPTIONS') {
    return new Response(null, {
      status: 204,
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
      }
    });
  }

  const url = new URL(request.url);
  const rawPath = request.headers.get('x-nf-original-pathname') || url.pathname;
  const result = resolveOpenApiV1Request(rawPath, url.search, url.origin);

  return new Response(JSON.stringify(result.data, null, 2), {
    status: result.status,
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Cache-Control': 'public, max-age=60'
    }
  });
}

export const config = {
  path: ['/api', '/api/*']
};
`;

fs.writeFileSync(path.join(netlifyFuncDir, 'api.mjs'), netlifyFunctionCode, 'utf-8');

// 5. Create Cloudflare Pages Function (/functions/api/[[path]].js)
const cfPagesFuncDir = path.join(rootDir, 'functions', 'api');
fs.mkdirSync(cfPagesFuncDir, { recursive: true });

const cfPagesFunctionCode = `// Auto-generated Real HTTP JSON Function for Cloudflare Pages
${bundledOpenApiCode}

export async function onRequest(context) {
  const { request } = context;
  if (request.method === 'OPTIONS') {
    return new Response(null, {
      status: 204,
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type'
      }
    });
  }

  const url = new URL(request.url);
  const result = resolveOpenApiV1Request(url.pathname, url.search, url.origin);

  return new Response(JSON.stringify(result.data, null, 2), {
    status: result.status,
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
      'Cache-Control': 'public, max-age=60'
    }
  });
}
`;

fs.writeFileSync(path.join(cfPagesFuncDir, '[[path]].js'), cfPagesFunctionCode, 'utf-8');

console.log('Generated static JSON files and Edge/Serverless API functions successfully.');
