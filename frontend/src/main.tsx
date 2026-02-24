import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import "@radix-ui/themes/styles.css";

import App from './components/App'
import { Theme } from '@radix-ui/themes';
import { AuthProvider } from './context/AuthContext';
import { AuthErrorHandler } from './components/AuthErrorHandler';

const rootEl = document.getElementById('root');

if (!rootEl) {
  throw new Error('Root element not found');
}

console.log('Rendering React app...');

createRoot(rootEl).render(
  <StrictMode>
    <Theme accentColor="teal" radius="full" scaling="95%">
      <BrowserRouter>
        <AuthProvider>
          <AuthErrorHandler />
          <App />
        </AuthProvider>
      </BrowserRouter>
    </Theme>
  </StrictMode>,
)

console.log('React app rendered');
