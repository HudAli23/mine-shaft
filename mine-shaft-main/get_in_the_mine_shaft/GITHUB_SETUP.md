# GitHub Setup Guide

## Quick Setup Steps

### 1. Initialize Git Repository (if not already done)
```bash
git init
git add .
git commit -m "Initial commit"
```

### 2. Create Repository on GitHub
- Go to https://github.com/new
- Create a new repository (don't initialize with README)
- Copy the repository URL

### 3. Connect and Push
```bash
git remote add origin <your-repo-url>
git branch -M main
git push -u origin main
```

## Important Notes

### ✅ Files That WILL Be Committed (Small Size)
- Source code (`app/src/`)
- Gradle files (`build.gradle`, `settings.gradle`)
- Configuration files (`.gitignore`, `gradle.properties`)
- README.md
- **Total size: ~0.22 MB** (very small!)

### ❌ Files That WON'T Be Committed (Ignored)
- Build outputs (`app/build/`, `build/`)
- Gradle cache (`.gradle/`)
- IDE files (`.idea/`)
- APK/AAB files
- Log files
- Local properties

## Troubleshooting

### If files are "hidden" or missing:
1. Check `.gitignore` is working: `git status`
2. Verify files exist: `git ls-files`
3. Force add if needed: `git add -f <file>` (only for source files!)

### If repository is "too big":
1. Check for large files: `git ls-files | xargs ls -lh | sort -k5 -hr`
2. Remove build files from git history if accidentally committed:
   ```bash
   git rm -r --cached app/build
   git commit -m "Remove build files"
   ```

### If upload fails:
- GitHub has a 100MB file size limit per file
- Repository should be under 1GB total
- This project is only 0.22 MB, so size is not an issue!

## Verify Before Pushing
```bash
# Check what will be committed
git status

# Check repository size
git count-objects -vH

# List all tracked files
git ls-files
```
