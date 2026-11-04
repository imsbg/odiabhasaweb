// Service Worker for SPA Navigation & Refresh Support on Odia URLs
self.addEventListener('install', () => {
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
  const req = event.request;
  if (req.method !== 'GET') return;

  const url = new URL(req.url);
  if (url.origin !== self.location.origin) return;

  // Intercept top-level page navigations (e.g., refreshing /ଓଡ଼ିଆ-କ୍ୟାଲେଣ୍ଡର/ or visiting a 404 route)
  // and serve the root SPA shell ('/') so reverse proxies never return 404 on Odia subpaths.
  if (req.mode === 'navigate' && !url.pathname.startsWith('/api')) {
    event.respondWith(
      fetch('/', { credentials: 'same-origin' }).catch(() => fetch(req))
    );
  }
});
