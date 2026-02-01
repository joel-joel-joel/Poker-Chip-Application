import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import "@radix-ui/themes/styles.css";

import App from './components/App'
import { Theme } from '@radix-ui/themes';
import { AuthProvider } from './context/AuthContext';

const rootEl = document.getElementById('root') as HTMLElement;

createRoot(rootEl).render(
  <StrictMode>
    <Theme accentColor="teal" radius="full" scaling="95%">
      <BrowserRouter>
        <AuthProvider>
          <App />
        </AuthProvider>
      </BrowserRouter>
    </Theme>
  </StrictMode>,
)
