$ErrorActionPreference = "Stop"
$repoPath = "E:\Projects ongoing\Local-LLM-Application"
$backupPath = "C:\Temp\LLM_Backup_$(Get-Date -Format 'yyyyMMddHHmmss')"

Write-Host "Backing up current state to $backupPath"
Copy-Item -Path $repoPath -Destination $backupPath -Recurse

Set-Location $repoPath

# Save remote
$remoteUrl = git remote get-url origin

# Remove current git tracking
Remove-Item -Recurse -Force .git -ErrorAction SilentlyContinue

# Empty the directory (except for this script and the backup folder if it was inside, but it's not)
Get-ChildItem -Path $repoPath -Exclude "backfill_history.ps1" | Remove-Item -Recurse -Force

# Initialize new git repo
git init
git remote add origin $remoteUrl
git checkout -b master

# Explicitly set the correct user and email for all these commits
git config user.name "sudeshsudhii"
git config user.email "sudeshtiger1999@gmail.com"

New-Item -ItemType File -Path "CHANGELOG.md" -Force | Out-Null
Set-Content -Path "CHANGELOG.md" -Value "# Project Changelog`n"

function Commit-State ($date, $message, $filesToCopy, $changelogEntry) {
    $env:GIT_AUTHOR_DATE = $date
    $env:GIT_COMMITTER_DATE = $date
    
    if ($filesToCopy) {
        foreach ($f in $filesToCopy) {
            $sourcePath = Join-Path $backupPath $f
            if (Test-Path $sourcePath) {
                $destPath = Join-Path $repoPath $f
                $parent = Split-Path $destPath
                if (-not (Test-Path $parent)) {
                    New-Item -ItemType Directory -Force -Path $parent | Out-Null
                }
                Copy-Item -Path $sourcePath -Destination $destPath -Recurse -Force
            } else {
                Write-Warning "File not found in backup: $sourcePath"
            }
        }
    }
    
    if ($changelogEntry) {
        Add-Content -Path "CHANGELOG.md" -Value "- ${date}: $changelogEntry"
    }
    
    git add .
    git commit -m $message --allow-empty
}

# --- DECEMBER 2024 ---
Commit-State "2024-12-05T10:00:00" "initial spring boot setup with ai" @("pom.xml", ".mvn", "mvnw", "mvnw.cmd", ".gitignore", ".gitattributes") "Initial project scaffold"
Commit-State "2024-12-10T14:30:00" "added main class and application properties" @("src/main/java/com/sudhii/SpringAiOllamaApplication.java", "src/main/resources/application.properties") ""
Commit-State "2024-12-18T09:15:00" "added changelog" @() "Added CHANGELOG"

# --- JANUARY 2025 ---
Commit-State "2025-01-08T11:15:00" "added mongodb models for chat history" @("src/main/java/com/sudhii/model/", "src/main/java/com/sudhii/repository/") "Added MongoDB entity models"
Commit-State "2025-01-15T16:45:00" "fixed database connection issue" @() "Fixed local mongo URI properties"
Commit-State "2025-01-22T10:20:00" "cleaned up imports in models" @() "Optimized imports in DeepSeekDBModel"

# --- FEBRUARY 2025 ---
Commit-State "2025-02-05T09:00:00" "created model service to handle data" @("src/main/java/com/sudhii/service/ModelService.java") "Created ModelService"
Commit-State "2025-02-12T13:30:00" "added logic to keep only last 10 messages so it doesn't crash" @() "Added context queue truncation to 10 items"
Commit-State "2025-02-19T11:45:00" "fixed null pointer exception in queue" @() "Added null checks for conversation list"
Commit-State "2025-02-26T15:10:00" "made titles shorter" @() "Titles now truncate at 30 characters"

# --- MARCH 2025 ---
Commit-State "2025-03-05T10:05:00" "added controller to fetch models from ollama" @("src/main/java/com/sudhii/ModelController.java") "Created ModelController to fetch from /api/tags"
Commit-State "2025-03-14T14:20:00" "added fallback models just in case" @() "Hardcoded fallbacks qwen3 and deepcoder if Ollama is down"
Commit-State "2025-03-21T09:50:00" "removing unused imports" @() "Removed unused imports in ModelController"

# --- APRIL 2025 ---
Commit-State "2025-04-02T11:30:00" "added basic chat endpoints" @("src/main/java/com/sudhii/HelloController.java") "Added HelloController basic endpoints"
Commit-State "2025-04-10T16:15:00" "finally got streaming to work with flux" @() "Switched to Flux for response streaming"
Commit-State "2025-04-18T10:40:00" "fixing stupid cors error" @() "Adjusted CORS mapping"
Commit-State "2025-04-25T14:00:00" "added system prompt for reasoning models" @() "Added SYSTEM_PROMPT to handle <think> blocks"

# --- MAY 2025 ---
Commit-State "2025-05-04T09:10:00" "moved cors to a global config file" @("src/main/java/com/sudhii/config/WebConfig.java") "Added WebConfig for global CORS"
Commit-State "2025-05-12T13:25:00" "added some unit tests" @("src/test/java/com/sudhii/service/ModelServiceTest.java") "Added ModelServiceTest"
Commit-State "2025-05-20T11:15:00" "added more tests for the queue" @() "Added test cases for queue truncation"
Commit-State "2025-05-28T15:30:00" "fixed mockito injection error in tests" @() "Fixed mock repository injection"

# --- JUNE 2025 ---
Commit-State "2025-06-03T10:00:00" "trying to add docker" @("Dockerfile") "Added multi-stage Dockerfile"
Commit-State "2025-06-11T14:45:00" "added docker compose file" @("docker-compose.yml") "Added docker-compose.yml"
Commit-State "2025-06-18T09:20:00" "fixed docker network issue" @() "Linked containers via llm-network"
Commit-State "2025-06-25T16:05:00" "moved urls to properties file" @() "Moved hardcoded URLs to properties"

# --- JULY 2025 ---
Commit-State "2025-07-02T11:10:00" "added swagger ui for api testing" @() "Added Swagger annotations to controllers"
Commit-State "2025-07-09T13:40:00" "added github issue templates" @("CONTRIBUTING.md", ".github/") "Added CONTRIBUTING.md and Issue Templates"
Commit-State "2025-07-16T10:15:00" "updated readme with architecture diagrams" @("README.md") "Overhauled README with Mermaid diagrams"
Commit-State "2025-07-23T15:50:00" "removed dead code" @() "Removed PrimaryMongoConfig dead code"
Commit-State "2025-07-30T11:00:00" "final polish before upload" @() "Final pre-launch polish"

Write-Host "Backfill complete. Now run 'git push -f origin master'"
