<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <p class="copyright">&copy; 2026 旅游攻略平台</p>
        <div class="social-links">
          <a href="https://www.xiaohongshu.com" target="_blank" rel="noopener noreferrer" class="social-link" title="小红书">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm4.64 6.8c-.15 1.58-.8 5.42-1.13 7.19-.14.75-.42 1-.68 1.03-.58.05-1.02-.38-1.58-.75-.88-.58-1.38-.94-2.23-1.5-.99-.65-.35-1.01.22-1.59.15-.15 2.71-2.48 2.76-2.69a.2.2 0 0 0-.05-.18c-.06-.05-.14-.03-.21-.02-.09.02-1.49.95-4.22 2.79-.4.27-.76.41-1.08.4-.36-.01-1.04-.2-1.55-.37-.63-.2-1.12-.31-1.08-.66.02-.18.27-.36.74-.55 2.92-1.27 4.86-2.11 5.83-2.51 2.78-1.16 3.35-1.36 3.73-1.36.08 0 .27.02.39.12.1.08.13.19.14.27-.01.06.01.24 0 .38z"/>
            </svg>
            <span>小红书</span>
          </a>
          <a href="https://www.douyin.com" target="_blank" rel="noopener noreferrer" class="social-link" title="抖音">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M16.6 5.82s.51.5 0 0A4.278 4.278 0 0 1 15.54 3h-3.09v12.4a2.592 2.592 0 0 1-2.59 2.5c-1.42 0-2.6-1.16-2.6-2.6 0-1.72 1.66-3.01 3.37-2.48V9.66c-3.45-.46-6.47 2.22-6.47 5.64 0 3.33 2.76 5.7 5.69 5.7 3.14 0 5.69-2.55 5.69-5.7V9.01a7.35 7.35 0 0 0 4.3 1.38V7.3s-1.88.09-3.24-1.48z"/>
            </svg>
            <span>抖音</span>
          </a>
          <a href="https://weibo.com" target="_blank" rel="noopener noreferrer" class="social-link" title="微博">
            <svg viewBox="0 0 24 24" fill="currentColor">
              <path d="M10.098 20.323c-3.977.391-7.414-1.406-7.672-4.02-.259-2.609 2.759-5.047 6.74-5.441 3.979-.394 7.413 1.404 7.671 4.018.259 2.6-2.759 5.049-6.739 5.443zM9.05 17.219c-.384.616-1.208.884-1.829.602-.612-.279-.793-.991-.406-1.593.379-.595 1.176-.861 1.793-.601.622.263.82.972.442 1.592zm1.27-1.627c-.141.237-.449.353-.689.253-.236-.09-.313-.361-.177-.586.138-.227.436-.346.672-.24.239.09.315.36.194.573zm.176-2.719c-1.893-.493-4.033.45-4.857 2.118-.836 1.704-.026 3.591 1.886 4.21 1.983.64 4.318-.341 5.132-2.179.8-1.793-.201-3.642-2.161-4.149zm7.563-1.224c-.346-.105-.579-.18-.401-.649.386-1.02.425-1.899.003-2.525-.789-1.168-2.947-1.108-5.388-.034 0 0-.772.339-.575-.274.381-1.215.324-2.234-.27-2.822-1.349-1.33-4.937.045-8.013 3.073C1.353 10.476 0 12.555 0 14.359c0 3.457 4.438 5.559 8.783 5.559 5.693 0 9.481-3.309 9.481-5.939 0-1.587-1.339-2.486-2.205-2.73zm2.124-3.092c-.871-.975-2.156-1.349-3.358-1.098-.399.083-.654.473-.571.872.083.4.473.655.872.572.729-.151 1.493.074 2.01.651.517.577.693 1.359.472 2.077-.121.389.101.801.49.923.39.121.801-.101.923-.49.356-1.139.089-2.413-.838-3.507zm1.907-2.087C20.384 4.573 18.178 3.5 15.73 3.5c-.414 0-.75.336-.75.75s.336.75.75.75c1.936 0 3.713.824 4.938 2.197 1.225 1.374 1.767 3.182 1.527 5.092-.051.401.232.766.633.817.4.051.766-.232.817-.633.293-2.358-.383-4.636-1.82-6.441z"/>
            </svg>
            <span>微博</span>
          </a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: #333;
  color: white;
  padding: 16px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.copyright {
  margin: 0;
  font-size: 14px;
  opacity: 0.7;
}

.social-links {
  display: flex;
  gap: 20px;
  align-items: center;
}

.social-link {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  text-decoration: none;
  opacity: 0.7;
  transition: opacity 0.3s ease;
  font-size: 14px;
}

.social-link:hover {
  opacity: 1;
}

.social-link svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }

  .social-links {
    justify-content: center;
  }
}
</style>
