@echo off
setlocal enabledelayedexpansion

REM ====== CONFIGURATION ======
set COMMIT_MESSAGE=%1

if "%COMMIT_MESSAGE%"=="" (
    set /p COMMIT_MESSAGE=Enter commit message: 
)

echo ================================
echo 🚀 Auto Git Commit & Push Script
echo ================================

git add .
git commit -m "%COMMIT_MESSAGE%"
git push

echo.
echo ✅ Done! All changes (frontend + backend) pushed successfully.
pause
