# Ścieżka do katalogu głównego projektu
$sourceDir = "./"
$zipPath = "../project-clean.zip"
$tempDir = "$env:TEMP\project-clean-zip"

# Wczytaj .gitignore jako tablicę reguł
$gitignoreLines = @"
.gradle
build/
.idea
.git
*.iws
*.iml
*.ipr
out/
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache
bin/
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
.vscode/
ui/frontend/node_modules/
ui/frontend/dist/
ui/frontend/.vite/
ui/frontend/.eslintcache
*.log
.DS_Store
Thumbs.db
**/.dockerignore
**/Dockerfile
docker-volume-data/
.env
/agent-gateway/build/
/agent-gateway/out/
/agent-gateway/.idea/
/agent-gateway/.classpath
/agent-gateway/.project
*.zip
agent-gateway/src/main/resources/application-local.properties
agent-gateway/src/testIntegration/resources/application-test.properties
"@ -split "`n"

# Czyści katalog tymczasowy
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Path $tempDir | Out-Null

# Funkcja ignorująca na podstawie gitignore
function IsIgnored {
    param (
        [string]$relativePath
    )

    $relative = $relativePath.Replace("/", "\")
    $ignored = $false

    foreach ($lineRaw in $gitignoreLines) {
        $line = $lineRaw.Trim()
        if ($line -eq "" -or $line.StartsWith("#")) { continue }

        $isNegation = $line.StartsWith("!")
        $pattern = if ($isNegation) { $line.Substring(1) } else { $line }
        $pattern = $pattern.Replace("/", "\").Replace("**", "*")

        # Dopasowanie jak wildcard
        if ($relative -like $pattern) {
            $ignored = -not $isNegation
        }
    }

    return $ignored
}

# Kopiowanie tylko nieignorowanych plików
Get-ChildItem -Path $sourceDir -Recurse -File | ForEach-Object {
    $rel = $_.FullName.Substring($sourceDir.Length).TrimStart("\")
    if (-not (IsIgnored $rel)) {
        $target = Join-Path $tempDir $rel
        $targetDir = Split-Path $target
        if (-not (Test-Path $targetDir)) {
            New-Item -ItemType Directory -Path $targetDir -Force | Out-Null
        }
        Copy-Item $_.FullName -Destination $target -Force
    }
}

# Tworzenie ZIP-a
if (Test-Path $zipPath) {
    Remove-Item $zipPath -Force
}
Compress-Archive -Path "$tempDir\*" -DestinationPath $zipPath

$zipItem = Get-Item $zipPath
$len = $zipItem.Length / 1MB

Write-Host ("ZIP stworzony: {0}." -f $zipPath)
Write-Host ("Rozmiar: MB" -f $len)