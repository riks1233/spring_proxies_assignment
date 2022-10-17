# Helper script for local Windows development to set environment variables
# from .env located in root repository folder.
#
# BE CAREFUL! Docker documentation states "Values set in the shell
# environment override those set in the .env file." So if you would like
# to execute `docker compose` later with environment variables from .env file,
# make sure to do so from a fresh shell which does not have .env environment
# variables loaded yet.

$scriptDir = Split-Path $MyInvocation.MyCommand.Path
$envFile = "$scriptDir\..\.env"
if (Test-Path -Path $envFile -PathType Leaf) {
  foreach ($line in Get-Content "$scriptDir\..\.env") {
    if ($line -match '^\s*[^\s=#]+=[^\s]+$') {
      $keyVal = $line -split '=', 2
      $key = $keyVal[0].Trim()
      $val = $keyVal[1].Trim()
      [Environment]::SetEnvironmentVariable($key, $val)
    }
  }
} else {
  Write-Host "`.env` file has not been created. Create one from .env.dist template and populate it with relevant values."
}
