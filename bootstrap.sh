#!/bin/sh

# Create the pre-commit hook
HOOK_FILE=".git/hooks/pre-commit"

echo "Setting up git pre-commit hook..."

mkdir -p .git/hooks

cat << 'EOF' > "$HOOK_FILE"
#!/bin/sh

# Sync global coding guidelines
echo "Syncing global coding guidelines..."
cp ~/.config/agent-rules/global_agents.md AGENT_CODING_GUIDELINES.md

# Get a list of staged .kt and .kts files
STAGED_KOTLIN_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep -E '\.(kt|kts)$')

# Always run spotlessApply to format the copied guidelines
if [ -n "$STAGED_KOTLIN_FILES" ]; then
    echo "Running spotlessApply on staged kotlin files..."
    ./gradlew spotlessApply
    git add $STAGED_KOTLIN_FILES AGENT_CODING_GUIDELINES.md
else
    echo "Running spotlessApply on guidelines..."
    ./gradlew spotlessApply
    git add AGENT_CODING_GUIDELINES.md
fi

# Get a list of staged .kts files
STAGED_KTS_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep '\.kts$')

if [ -n "$STAGED_KTS_FILES" ]; then
    echo "Running sortDependencies on staged kts files..."
    ./gradlew sortDependencies
    git add $STAGED_KTS_FILES
fi

exit 0
EOF

chmod +x "$HOOK_FILE"

echo "Git pre-commit hook setup complete."
