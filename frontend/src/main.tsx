import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import "@radix-ui/themes/styles.css";

// import 'bootstrap/dist/css/bootstrap.css';
import App from './components/App.js'
import { Theme } from '@radix-ui/themes';

const rootEl = document.getElementById('root') as HTMLElement; // or: document.getElementById('root')!

createRoot(rootEl).render(
  <StrictMode>
    <Theme accentColor="teal" radius="full" scaling="95%">
      <App />
    </Theme>
  </StrictMode>,
)
