[CmdletBinding()]
param(
    [int]$Port = 8080,
    [int]$StartupTimeoutSeconds = 120
)

$ErrorActionPreference = 'Stop'

function Get-CloudflaredPath {
    $command = Get-Command cloudflared -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    $localPath = Join-Path $PSScriptRoot 'cloudflared.exe'
    if (Test-Path -LiteralPath $localPath) {
        return $localPath
    }

    throw "cloudflared nao foi encontrado. Instale-o e adicione-o ao PATH, ou copie cloudflared.exe para '$PSScriptRoot'."
}

function Import-EnvironmentFile {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        throw "Arquivo de configuracao nao encontrado: $Path"
    }

    $lineNumber = 0
    foreach ($line in Get-Content -LiteralPath $Path) {
        $lineNumber++
        $trimmedLine = $line.Trim()
        if (-not $trimmedLine -or $trimmedLine.StartsWith('#')) {
            continue
        }

        if ($trimmedLine -notmatch '^(?:export\s+)?(?<name>[A-Za-z_][A-Za-z0-9_]*)\s*=\s*(?<value>.*)$') {
            throw "Linha invalida no arquivo .env ($($Path):$lineNumber)."
        }

        $name = $matches.name
        $value = $matches.value.Trim()
        if ($value.Length -ge 2 -and (
                ($value.StartsWith('"') -and $value.EndsWith('"')) -or
                ($value.StartsWith("'") -and $value.EndsWith("'"))
            )) {
            $value = $value.Substring(1, $value.Length - 2)
        }

        # Valores exportados pelo terminal mantem precedencia sobre o arquivo local.
        if ($null -eq [Environment]::GetEnvironmentVariable($name, 'Process')) {
            [Environment]::SetEnvironmentVariable($name, $value, 'Process')
        }
    }
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$healthUrl = "http://localhost:$Port/api/actuator/health"
$environmentFile = Join-Path $projectRoot '.env'
Import-EnvironmentFile -Path $environmentFile
$cloudflaredPath = Get-CloudflaredPath
$mavenProcess = $null
$tunnelProcess = $null

try {
    Write-Host "Iniciando a API em http://localhost:$Port/api..." -ForegroundColor Cyan
    $mavenProcess = Start-Process -FilePath 'cmd.exe' `
        -ArgumentList '/c', '.\mvnw.cmd spring-boot:run' `
        -WorkingDirectory $projectRoot `
        -PassThru

    Write-Host "Aguardando a API responder em $healthUrl..." -ForegroundColor Cyan
    $deadline = (Get-Date).AddSeconds($StartupTimeoutSeconds)
    do {
        try {
            $response = Invoke-WebRequest -UseBasicParsing -Uri $healthUrl -TimeoutSec 3
            if ($response.StatusCode -eq 200) {
                break
            }
        } catch {
            Start-Sleep -Seconds 2
        }
    } while ((Get-Date) -lt $deadline)

    if (-not $response -or $response.StatusCode -ne 200) {
        throw "A API nao respondeu com HTTP 200 em $healthUrl dentro de $StartupTimeoutSeconds segundos."
    }

    $tunnelLog = Join-Path ([System.IO.Path]::GetTempPath()) "manutencao-api-cloudflared-$PID.log"
    Write-Host 'Criando tunel publico Cloudflare...' -ForegroundColor Cyan
    $tunnelProcess = Start-Process -FilePath $cloudflaredPath `
        -ArgumentList 'tunnel', '--url', "http://localhost:$Port", '--no-autoupdate', '--protocol', 'http2' `
        -RedirectStandardError $tunnelLog `
        -PassThru

    $deadline = (Get-Date).AddSeconds(30)
    $publicUrl = $null
    do {
        if (Test-Path -LiteralPath $tunnelLog) {
            $publicUrl = Select-String -LiteralPath $tunnelLog -Pattern 'https://[-a-z0-9]+\.trycloudflare\.com' |
                Select-Object -First 1 -ExpandProperty Matches |
                Select-Object -First 1 -ExpandProperty Value
        }
        if (-not $publicUrl) {
            Start-Sleep -Milliseconds 500
        }
    } while (-not $publicUrl -and (Get-Date) -lt $deadline -and -not $tunnelProcess.HasExited)

    if (-not $publicUrl) {
        throw "O Cloudflare nao gerou uma URL publica. Consulte o log: $tunnelLog"
    }

    Write-Host ''
    Write-Host 'LINK PUBLICO DA API:' -ForegroundColor Green
    Write-Host "$publicUrl/api" -ForegroundColor Green
    Write-Host "Swagger: $publicUrl/api/swagger-ui.html" -ForegroundColor Green
    Write-Host 'Pressione Ctrl+C para encerrar a API e o tunel.' -ForegroundColor Yellow

    Wait-Process -Id $tunnelProcess.Id
} finally {
    if ($tunnelProcess -and -not $tunnelProcess.HasExited) {
        Stop-Process -Id $tunnelProcess.Id -Force
    }
    if ($mavenProcess -and -not $mavenProcess.HasExited) {
        & taskkill.exe /PID $mavenProcess.Id /T /F | Out-Null
    }
}
