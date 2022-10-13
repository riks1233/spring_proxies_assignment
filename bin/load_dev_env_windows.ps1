# Helper script for local Windows development to set environment variables
# from .dev_env located in root repository folder.
#
# BE CAREFUL! Documentation states "Values set in the shell environment override those set in the .env file."
# So if you would like to execute `docker compose` later with environment variables from .env file, make sure to
# do so from a fresh shell without relevant environment variables set.

$scriptDir = Split-Path $MyInvocation.MyCommand.Path
foreach($line in Get-Content "$scriptDir\..\.local_env") {
    if($line -match '^\s*[^\s=#]+=[^\s]+$'){
        $keyVal = $line -split '=', 2
        $key = $keyVal[0].Trim()
        $val = $keyVal[1].Trim()
        [Environment]::SetEnvironmentVariable($key, $val)
    }
}
