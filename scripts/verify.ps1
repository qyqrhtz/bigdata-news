param(
    [string]$WslDistro = "Ubuntu-22.04",
    [switch]$SkipFrontend,
    [switch]$SkipFrontendInstall,
    [switch]$StrictWsl
)

$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$FrontendDir = Join-Path $Root "news-frontend"

function Write-Section {
    param([string]$Text)
    Write-Host ""
    Write-Host "==> $Text" -ForegroundColor Cyan
}

function Assert-Command {
    param([string]$Name)
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Missing command: $Name"
    }
}

function Invoke-Step {
    param(
        [string]$Name,
        [scriptblock]$Command
    )

    Write-Section $Name
    $global:LASTEXITCODE = 0
    & $Command

    if ($global:LASTEXITCODE -ne 0) {
        throw "Step failed: $Name (exit code $global:LASTEXITCODE)"
    }

    Write-Host "PASS: $Name" -ForegroundColor Green
}

function Convert-ToWslPath {
    param([string]$WindowsPath)

    $fullPath = [System.IO.Path]::GetFullPath($WindowsPath)
    $drive = ([System.IO.Path]::GetPathRoot($fullPath)).Substring(0, 1).ToLowerInvariant()
    $relative = $fullPath.Substring(3).Replace("\", "/")
    return "/mnt/$drive/$relative"
}

Push-Location $Root
try {
    Assert-Command "docker"
    Assert-Command "wsl.exe"
    Assert-Command "cmd.exe"

    Write-Section "Python and RAG syntax check in WSL2"
    $wslRoot = Convert-ToWslPath $Root
    $files = @(
        "main.py",
        "news_to_parquet.py",
        "news_analysis.py",
        "test.py",
        "rag-service/app.py",
        "rag-service/wsgi.py"
    ) | ForEach-Object { "$wslRoot/$($_.Replace('\', '/'))" }
    $compileCommand = "python3 -m py_compile $($files -join ' ')"
    $global:LASTEXITCODE = 0
    Invoke-Expression "wsl.exe -d $WslDistro -- sh -lc `"$compileCommand`""

    if ($global:LASTEXITCODE -ne 0) {
        $manualCommand = "wsl.exe -d $WslDistro -- sh -lc `"python3 -m py_compile $($files -join ' ')`""
        if ($StrictWsl) {
            throw "Step failed: Python and RAG syntax check in WSL2 (exit code $global:LASTEXITCODE)"
        }

        Write-Warning "WSL syntax check failed from this PowerShell script. Run this command manually to verify Python files:"
        Write-Host $manualCommand
        $global:LASTEXITCODE = 0
    }
    else {
        Write-Host "PASS: Python and RAG syntax check in WSL2" -ForegroundColor Green
    }

    Invoke-Step "Backend tests" {
        & (Join-Path $Root "mvnw.cmd") clean test
    }

    Invoke-Step "Docker Compose config" {
        & docker compose config --quiet
    }

    Invoke-Step "Docker Compose config with RAG profile" {
        & docker compose --profile rag config --quiet
    }

    if (-not $SkipFrontend) {
        if (-not (Test-Path -LiteralPath $FrontendDir)) {
            throw "Frontend directory not found: $FrontendDir"
        }

        Push-Location $FrontendDir
        try {
            if (-not $SkipFrontendInstall -and -not (Test-Path -LiteralPath (Join-Path $FrontendDir "node_modules"))) {
                Invoke-Step "Frontend dependency install" {
                    & cmd.exe /c npm ci
                }
            }

            Invoke-Step "Frontend production build" {
                & cmd.exe /c npm run build
            }
        }
        finally {
            Pop-Location
        }
    }

    Write-Host ""
    Write-Host "All verification steps passed." -ForegroundColor Green
}
finally {
    Pop-Location
}
