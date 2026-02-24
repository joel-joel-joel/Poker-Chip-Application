# Frontend Troubleshooting Guide

## Blank White Screen Issue

If you see a blank white screen, follow these steps:

### 1. Check Browser Console

Open Developer Tools (F12 or Right-click → Inspect) and check the Console tab for errors.

### 2. Clear Cache and Restart

```bash
# Stop the dev server (Ctrl+C)
rm -rf node_modules/.vite
npm run dev
```

### 3. Hard Refresh Browser

- Chrome/Firefox: `Ctrl+Shift+R` (Windows/Linux) or `Cmd+Shift+R` (Mac)
- Or: Open DevTools → Right-click refresh button → "Empty Cache and Hard Reload"

### 4. Check for Common Errors

#### "global is not defined"
**Fixed in latest code** - Added polyfill in index.html and vite.config.js

#### "Failed to fetch"
- Backend not running → Start backend: `cd backend && ./mvnw spring-boot:run`
- Wrong API URL → Check `frontend/.env` has `VITE_API_URL=http://localhost:8080`

#### React Router errors
- Check browser URL
- Try navigating to `http://localhost:5173/register` directly

### 5. Enable Verbose Logging

Check browser console for these logs:
- ✅ "Rendering React app..."
- ✅ "React app rendered"
- ✅ "WebSocket connected successfully" (after 1 second)

### 6. Test Individual Pages

Try these URLs directly:

1. **Register Page**: http://localhost:5173/register
   - Should show registration form
   - No auth required

2. **Login Page**: http://localhost:5173/login
   - Should show login form (with warning)

3. **Lobby Page**: http://localhost:5173/lobby
   - Requires authentication
   - Will redirect to /login if not authenticated

### 7. Verify Backend Connection

Open browser DevTools → Network tab:
- Should see requests to `http://localhost:8080`
- Check for CORS errors (should not have any)
- Check response status codes

### 8. Check Package Versions

Ensure compatible versions:

```bash
npm list react react-dom react-router-dom
```

Expected:
- react: ^19.x
- react-dom: ^19.x
- react-router-dom: ^7.x

### 9. Clean Install

If all else fails:

```bash
rm -rf node_modules package-lock.json
npm install
npm run dev
```

### 10. Manual Test

Create a minimal test:

**Create**: `frontend/src/test.html`
```html
<!DOCTYPE html>
<html>
<head><title>Test</title></head>
<body>
  <h1>If you see this, Vite is working</h1>
  <div id="root">Loading...</div>
  <script type="module">
    console.log('Test page loaded');
    document.getElementById('root').innerText = 'React should render here';
  </script>
</body>
</html>
```

Visit: http://localhost:5173/src/test.html

---

## Common Issues & Solutions

### Issue: Nothing renders
**Solution**: Check that `<div id="root"></div>` exists in index.html

### Issue: "Cannot find module"
**Solution**:
```bash
npm install
```

### Issue: WebSocket errors
**Solution**: WebSocket is optional for now. The app uses polling as fallback.

### Issue: Authentication loop
**Solution**:
```bash
# Clear localStorage
# In browser console:
localStorage.clear()
# Then refresh
```

### Issue: Port already in use
**Solution**:
```bash
# Kill process on port 5173
lsof -ti:5173 | xargs kill -9
```

---

## Debug Checklist

- [ ] Backend running on http://localhost:8080
- [ ] Frontend running on http://localhost:5173
- [ ] No console errors (except WebSocket warnings are OK)
- [ ] index.html has `<div id="root"></div>`
- [ ] index.html has global polyfill script
- [ ] .env file exists with correct API_URL
- [ ] node_modules installed
- [ ] Vite cache cleared
- [ ] Browser cache cleared

---

## Still Having Issues?

1. **Check the logs**: Look at terminal output for both frontend and backend
2. **Try a different browser**: Sometimes browser extensions interfere
3. **Check firewall**: Ensure ports 5173 and 8080 are not blocked
4. **Restart everything**: Backend, frontend, and browser

---

## Success Indicators

When everything works, you should see:

### Browser Console:
```
Rendering React app...
React app rendered
WebSocket connected successfully
```

### Browser Screen:
- Either the Register page (if not authenticated)
- Or the Lobby page (if authenticated)

### Network Tab:
- Requests to localhost:8080 (green, status 200)
- No CORS errors
- No 404 errors

---

## Need More Help?

Check these files for detailed setup:
- `QUICK_START.md` - Basic setup guide
- `FRONTEND_SETUP.md` - Detailed frontend docs
- `README.md` - Complete project overview
