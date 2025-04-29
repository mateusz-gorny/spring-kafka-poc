[CmdletBinding()]
param (
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$IncludeDirs
)

$zipFile = "project-backup.zip"
$projectRoot = Get-Location
$excludedDirs = @('node_modules', 'build', 'dist', '.git', '.gradle', '.idea', 'target', 'out')
$excludedExtensions = @('.iml', '.zip')

Add-Type -AssemblyName 'System.IO.Compression.FileSystem'

if (Test-Path $zipFile) {
    Remove-Item $zipFile
}

$zip = [System.IO.Compression.ZipFile]::Open($zipFile, 'Create')

$included = 0
$excluded = 0
$files = @()

function Resolve-Folder($name) {
    $cleanName = $name.TrimEnd([char]92, [char]47).Replace('./', '').Replace('.\\', '')
    $tryPaths = @(
        (Join-Path $projectRoot $cleanName),
        (Join-Path (Join-Path $projectRoot "modules") $cleanName),
        (Join-Path (Join-Path $projectRoot "agents") $cleanName)
    )
    foreach ($path in $tryPaths) {
        if ($path -and (Test-Path $path) -and (Get-Item $path).PSIsContainer) {
            Write-Host "Dodano folder: $path"
            return $path
        }
    }
    Write-Host "Folder nie znaleziony: $name"
    return $null
}

if ($IncludeDirs.Count -eq 0) {
    $files = Get-ChildItem -Recurse -File
} else {
    foreach ($arg in $IncludeDirs) {
        $resolved = Resolve-Folder $arg
        if ($resolved) {
            $files += Get-ChildItem -Recurse -File -Path $resolved
        }
    }
}

foreach ($file in $files) {
    $relativePath = $file.FullName.Substring($projectRoot.Path.Length + 1)
    $normalizedPath = $relativePath -replace '\\', '/'

    $parts = $relativePath -split '[\\/]'
    $dirMatch = $parts | Where-Object { $excludedDirs -contains $_ }
    $extMatch = $excludedExtensions | Where-Object { $relativePath.EndsWith($_) }

    if ($dirMatch) {
        Write-Host "Pominięto (folder): $relativePath"
        $excluded++
        continue
    }

    if ($extMatch) {
        Write-Host "Pominięto (rozszerzenie): $relativePath"
        $excluded++
        continue
    }

    [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($zip, $file.FullName, $normalizedPath)
    Write-Host "Dodano: $relativePath"
    $included++
}

$zip.Dispose()

Write-Host ""
Write-Host "Gotowe!"
Write-Host ("Spakowano {0} plików. Pominięto {1}." -f $included, $excluded)
