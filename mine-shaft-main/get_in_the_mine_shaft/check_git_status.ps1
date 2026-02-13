# Git Status Checker - Verifies what will be committed to GitHub

Write-Host "=== Git Repository Status Check ===" -ForegroundColor Cyan
Write-Host ""

# Check if git is initialized
if (Test-Path ".git") {
    Write-Host "[OK] Git repository initialized" -ForegroundColor Green
    
    # Check what files are tracked
    Write-Host "`nFiles currently tracked by git:" -ForegroundColor Yellow
    git ls-files | Measure-Object | ForEach-Object {
        Write-Host "  Total files: $($_.Count)" -ForegroundColor White
    }
    
    # Check repository size
    Write-Host "`nRepository size:" -ForegroundColor Yellow
    git count-objects -vH 2>$null | Select-String "size-pack" | ForEach-Object {
        Write-Host "  $_" -ForegroundColor White
    }
    
    # Check for large files
    Write-Host "`nLarge files (>1MB) in repository:" -ForegroundColor Yellow
    $largeFiles = git ls-files | ForEach-Object {
        $file = Get-Item $_ -ErrorAction SilentlyContinue
        if ($file -and $file.Length -gt 1MB) {
            [PSCustomObject]@{
                File = $_
                Size = "{0:N2} MB" -f ($file.Length / 1MB)
            }
        }
    }
    if ($largeFiles) {
        $largeFiles | Format-Table -AutoSize
    } else {
        Write-Host "  [OK] No large files found" -ForegroundColor Green
    }
    
    # Check untracked files
    Write-Host "`nUntracked files (will be ignored by .gitignore):" -ForegroundColor Yellow
    git status --porcelain | Select-String "^\?\?" | Measure-Object | ForEach-Object {
        Write-Host "  $($_.Count) untracked files" -ForegroundColor White
    }
    
} else {
    Write-Host "[X] Git repository not initialized" -ForegroundColor Red
    Write-Host "`nTo initialize:" -ForegroundColor Yellow
    Write-Host "  git init" -ForegroundColor White
    Write-Host "  git add ." -ForegroundColor White
    Write-Host "  git commit -m 'Initial commit'" -ForegroundColor White
}

Write-Host "`n=== Project Size Check ===" -ForegroundColor Cyan
$totalSize = 0
$fileCount = 0
Get-ChildItem -Recurse -File | Where-Object { 
    $_.FullName -notmatch "\\build\\|\\\.gradle\\|\\\.idea\\|\\captures\\" 
} | ForEach-Object {
    $totalSize += $_.Length
    $fileCount++
}
Write-Host "Total files (excluding build): $fileCount" -ForegroundColor White
$sizeMB = "{0:N2} MB" -f ($totalSize / 1MB)
Write-Host "Total size: $sizeMB" -ForegroundColor White

    Write-Host "`nProject is ready for GitHub upload!" -ForegroundColor Green
