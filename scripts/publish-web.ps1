# Compila el frontend Web (Kotlin/Wasm) y copia el bundle a:
#   - web-dist/                                  -> servicio "algebraquest-frontend" de Railway (Dockerfile + nginx)
#   - ..\backend\src\main\resources\web          -> el backend también lo sirve en su propia URL (opcional)
# Uso:  .\scripts\publish-web.ps1
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (-not $env:JAVA_HOME) { $env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr" }

# Gradle escribe avisos por stderr; se valida solo el código de salida.
.\gradlew.bat :shared:wasmJsBrowserDistribution --console=plain
if ($LASTEXITCODE -ne 0) { throw "Falló la compilación Wasm" }
$ErrorActionPreference = "Stop"

$dist = Join-Path $root "shared\build\dist\wasmJs\productionExecutable"
$exclude = @("*.map", "mime.types", ".htaccess", "_headers", "*.LICENSE.txt")

function Publish($target) {
    if (Test-Path $target) { Remove-Item $target -Recurse -Force }
    New-Item -ItemType Directory -Force $target | Out-Null
    Copy-Item "$dist\*" $target -Recurse -Force -Exclude $exclude
    Write-Host "Bundle copiado a $target"
}

Publish (Join-Path $root "web-dist")

$backendWeb = Join-Path $root "..\backend\src\main\resources\web"
if (Test-Path (Split-Path $backendWeb -Parent)) { Publish $backendWeb }

Write-Host "Listo. Haz commit y push para que Railway redespliegue."
