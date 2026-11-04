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
  const jsonText = JSON.stringify(data, null, 2);

  document.title = `${window.location.pathname}${window.location.search}`;
  document.body.className = '';
  document.body.style.margin = '0';
  document.body.style.backgroundColor = '#ffffff';
  document.body.style.color = '#000000';

  const pre = document.createElement('pre');
  pre.style.margin = '0';
  pre.style.padding = '12px';
  pre.style.fontFamily = 'monospace';
  pre.style.fontSize = '13px';
  pre.style.lineHeight = '1.45';
  pre.style.whiteSpace = 'pre-wrap';
  pre.style.wordWrap = 'break-word';
  pre.textContent = jsonText;

  document.body.innerHTML = '';
  document.body.appendChild(pre);
  return true;
}

if (!handleStaticOpenApiIfNeeded()) {
  createRoot(document.getElementById('root')!).render(<App />);
}



