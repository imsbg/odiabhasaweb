import {createRoot} from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import {isOpenApiPath, resolveOpenApiV1Request} from './utils/openApiData';

if (typeof window !== 'undefined' && 'serviceWorker' in navigator) {
  navigator.serviceWorker
    .getRegistrations()
    .then((registrations) => {
      for (const reg of registrations) {
        if (reg.active?.scriptURL.endsWith('/sw.js') || reg.installing?.scriptURL.endsWith('/sw.js')) {
          reg.unregister().catch(() => {});
        }
      }
    })
    .catch(() => {});
}

function handleStaticOpenApiIfNeeded(): boolean {
  if (typeof window === 'undefined') return false;

  try {
    const redirectedPath = sessionStorage.getItem('odia_spa_redirect_path');
    if (redirectedPath) {
      const urlObj = new URL(redirectedPath, window.location.origin);
      if (isOpenApiPath(urlObj.pathname)) {
        sessionStorage.removeItem('odia_spa_redirect_path');
        window.history.replaceState(null, '', urlObj.pathname + urlObj.search + urlObj.hash);
      }
    }
  } catch {
    // Ignore storage errors
  }

  if (!isOpenApiPath(window.location.pathname)) {
    return false;
  }

  const {data} = resolveOpenApiV1Request(
    window.location.pathname,
    window.location.search,
    window.location.origin
  );
  const prettyJson = JSON.stringify(data, null, 2);
  const rawJson = JSON.stringify(data);

  document.title = '';
  // Remove HTML app favicons and meta tags so tab looks like a raw API endpoint
  document.querySelectorAll('link[rel*="icon"], style, link[rel="stylesheet"]').forEach((el) => el.remove());

  document.documentElement.style.colorScheme = 'light dark';
  document.body.className = '';
  document.body.style.margin = '0';

  const bar = document.createElement('div');
  bar.className = 'json-formatter-container';
  bar.style.cssText =
    'padding: 6px 10px; border-bottom: 1px solid rgba(128,128,128,0.3); font-family: system-ui, sans-serif; font-size: 12px; display: flex; align-items: center; gap: 6px; user-select: none;';

  const label = document.createElement('label');
  label.style.cssText = 'display: inline-flex; align-items: center; gap: 5px; cursor: pointer;';

  const checkbox = document.createElement('input');
  checkbox.type = 'checkbox';
  checkbox.checked = true;

  const span = document.createElement('span');
  span.textContent = 'Pretty-print';

  const pre = document.createElement('pre');
  pre.style.cssText =
    'margin: 0; padding: 10px; font-family: monospace; font-size: 13px; line-height: 1.4; white-space: pre-wrap; word-wrap: break-word;';
  pre.textContent = prettyJson;

  checkbox.addEventListener('change', () => {
    pre.textContent = checkbox.checked ? prettyJson : rawJson;
  });

  label.appendChild(checkbox);
  label.appendChild(span);
  bar.appendChild(label);

  document.body.innerHTML = '';
  document.body.appendChild(bar);
  document.body.appendChild(pre);
  return true;
}

if (!handleStaticOpenApiIfNeeded()) {
  createRoot(document.getElementById('root')!).render(<App />);
}



