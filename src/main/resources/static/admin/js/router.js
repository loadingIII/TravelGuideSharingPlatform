const Router = {
  routes: {},
  current: null,
  register(path, handler) { this.routes[path] = handler; },
  start() {
    window.addEventListener('hashchange', () => this.resolve());
    this.resolve();
  },
  resolve() {
    const hash = window.location.hash.slice(1) || '/';
    for (const [pattern, handler] of Object.entries(this.routes)) {
      const match = this.matchRoute(pattern, hash);
      if (match) {
        this.current = { pattern, params: match };
        handler(match);
        return;
      }
    }
    window.location.hash = '#/';
  },
  matchRoute(pattern, hash) {
    const patternParts = pattern.split('/');
    const hashParts = hash.split('/');
    if (patternParts.length !== hashParts.length) return null;
    const params = {};
    for (let i = 0; i < patternParts.length; i++) {
      if (patternParts[i].startsWith(':')) {
        params[patternParts[i].slice(1)] = hashParts[i];
      } else if (patternParts[i] !== hashParts[i]) {
        return null;
      }
    }
    return params;
  },
  navigate(path) { window.location.hash = '#' + path; }
};
